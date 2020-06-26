package labone.profile

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.FileProvider
import androidx.fragment.app.DialogFragment
import com.airbnb.mvrx.MavericksView
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.signature.ObjectKey
import com.example.labone.R
import kotlinx.android.synthetic.main.fragment_profile.*
import labone.GlideApp
import labone.centerDialog
import labone.doAfterTextChanged
import labone.formatDate
import labone.navigateBack
import labone.showAlert
import labone.showDatePicker
import labone.toURI
import labone.toUri
import mu.KotlinLogging.logger
import splitties.alertdialog.appcompat.messageResource
import splitties.alertdialog.appcompat.okButton
import splitties.alertdialog.appcompat.titleResource
import splitties.mainhandler.mainHandler
import splitties.resources.color
import splitties.resources.colorSL
import splitties.resources.dimenPxSize
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.time.Instant
import java.time.LocalDate
import java.time.temporal.ChronoUnit

private const val CAMERA_OUTPUT_URI_KEY = "CAMERA_OUTPUT_URI"
private const val GALLERY_PICK_IMAGE_REQUEST_CODE = 100
private const val CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 200
private const val YEARS = 1990
private const val JPEG_QUALITY = 70

enum class AvatarPickType {
    PHOTO, GALLERY
}

class ProfileFragment : DialogFragment(R.layout.fragment_profile), MavericksView {

    private val log = logger {}

    private var robotoLight: Typeface? = null
    private var robotoRegular: Typeface? = null
    private var cameraOutputUri: Uri? = null
    private var TextView.styledText: String?
        get() = text?.toString()
        set(value) {
            if (value.isNullOrBlank()) {
                text = null
                robotoLight?.let { typeface = it }
            } else {
                setTextKeepState(value)
                robotoRegular?.let { typeface = it }
            }
        }

    private val viewModel by fragmentViewModel(ProfileViewModel::class)

    override fun onStart() {
        dialog?.let { centerDialog(it) }
        super.onStart()
    }

    override fun invalidate() = withState(viewModel) { state ->
        userName.styledText = state.name
        userSurname.styledText = state.surname
        renderGender(state.gender)
        if(state.photoUri != null) {
            renderPhoto(state.photoUri.toUri())
        }
        profileBirthDate.text = state.birthDate?.let { formatDate(it) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        isCancelable = false

        userName.doAfterTextChanged {
            viewModel.changeName(if (it?.toString().isNullOrBlank()) null else it.toString())
        }

        userSurname.doAfterTextChanged {
            viewModel.changeSurname(if (it?.toString().isNullOrBlank()) null else it.toString())
        }

        genderWoman.setOnClickListener { viewModel.changeGender(Gender.FEMALE) }

        genderMan.setOnClickListener { viewModel.changeGender(Gender.MALE) }

        val dateClicker = View.OnClickListener {
            val startDate = withState(viewModel) {
                it.birthDate ?: LocalDate.of(YEARS, 1, 1)
            }
            val maxDate = Instant.now().minus(1, ChronoUnit.DAYS)
            showDatePicker(startDate, maxDate) { viewModel.changeBirthDate(it) }
        }
        clickerDate.setOnClickListener(dateClicker)

        if (savedInstanceState != null) {
            cameraOutputUri = savedInstanceState.getParcelable(CAMERA_OUTPUT_URI_KEY)
        }
        val pickPhoto = View.OnClickListener {
            showAlert {
                titleResource = R.string.select_avatar
                setItems(R.array.pick_ava_types) { dialog, which ->
                    when (AvatarPickType.values().getOrNull(which)) {
                        AvatarPickType.GALLERY -> openGalleryApp()
                        AvatarPickType.PHOTO -> tryToCaptureImage()
                        else -> dialog.cancel()
                    }
                }
            }
        }

        profileAvatar.setOnClickListener(pickPhoto)

        done.setOnClickListener {
            viewModel.onDone()
            navigateBack()
        }
    }


    private fun renderGender(gender: Gender?) {
        val activeColor = color(R.color.white)
        val inactiveColor = color(R.color.colorAccent)
        val activeBorder = colorSL(R.color.colorAccent)
        val inactiveBorder = null
        when (gender) {
            Gender.MALE -> renderGenderColor(activeColor, inactiveColor, activeBorder, inactiveBorder)
            Gender.FEMALE -> renderGenderColor(inactiveColor, activeColor, inactiveBorder, activeBorder)
            null -> renderGenderColor(inactiveColor, inactiveColor, inactiveBorder, inactiveBorder)
        }
    }

    private fun renderGenderColor(man: Int, woman: Int, manBorder: ColorStateList?, womanBorder: ColorStateList?) {
        manGenderIcon.setColorFilter(man, PorterDuff.Mode.SRC_IN)
        manGenderLabel.setTextColor(man)
        robotoLight?.let { manGenderLabel.typeface = it }
        womanGenderIcon.setColorFilter(woman, PorterDuff.Mode.SRC_IN)
        womanGenderLabel.setTextColor(woman)
        robotoLight?.let { womanGenderLabel.typeface = it }
        genderMan.backgroundTintList = manBorder
        genderWoman.backgroundTintList = womanBorder
    }

    private fun openGalleryApp() {
        log.info("Open gallery for get avatar")
        val intent = Intent().apply {
            type = "image/*"
            action = Intent.ACTION_GET_CONTENT
        }
        startActivityForResult(intent, GALLERY_PICK_IMAGE_REQUEST_CODE)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(CAMERA_OUTPUT_URI_KEY, cameraOutputUri)
        super.onSaveInstanceState(outState)
    }

    private fun tryToCaptureImage() {
        log.info("Open camera for get avatar")
        val context = requireContext()
        if (context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            val newAvaFile = newCameraOutputFile()
            if (newAvaFile != null) {
                cameraOutputUri = FileProvider.getUriForFile(
                    context,
                    context.packageName + ".fileprovider",
                    newAvaFile
                )
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                    putExtra(MediaStore.EXTRA_OUTPUT, cameraOutputUri)
                }
                startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE)
            }
        }
    }

    private fun newCameraOutputFile(): File? {
        return try {
            val filesDir = requireContext().cacheDir
            val photosDir = File(filesDir, "pictures")
            if (photosDir.exists() || photosDir.mkdirs()) {
                val file = File(photosDir, "IMG_" + System.currentTimeMillis() + ".jpg")
                if (file.createNewFile()) file else null
            } else {
                null
            }
        } catch (e: IOException) {
            log.warn(e) { "IO error when creating camera output" }
            null
        }
    }

    private fun renderPhoto(avaUri: Uri?) {
        val defaultDrawable = AppCompatResources.getDrawable(requireContext(), R.drawable.icon_avatar)
        if (avaUri == null) {
            profileAvatar.setImageDrawable(defaultDrawable)
        } else {
            GlideApp.with(requireActivity())
                .load(avaUri)
                .signature(ObjectKey(System.currentTimeMillis()))
                .circleCrop()
                .placeholder(defaultDrawable)
                .error(defaultDrawable)
                .into(profileAvatar)
        }
    }

    // camera
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            GALLERY_PICK_IMAGE_REQUEST_CODE -> if (resultCode == Activity.RESULT_OK) {
                val receiverUri = data?.data
                if (receiverUri == null) {
                    log.warn("Fail to pick photo from gallery: receive null uri")
                    showPhotoAlert()
                } else {
                    log.info { "Try to process and save photo from gallery: $receiverUri" }
                    saveCustomerAva(receiverUri)
                }
            }
            CAMERA_CAPTURE_IMAGE_REQUEST_CODE -> if (resultCode == Activity.RESULT_OK) {
                cameraOutputUri?.let { receiverUri ->
                    log.info { "Try to process and save photo from camera: $receiverUri" }
                    saveCustomerAva(receiverUri)
                } ?: run {
                    log.warn("Got result from camera with null URI")
                    showPhotoAlert()
                }
            }
        }
    }

    private fun showPhotoAlert() = showAlert {
        titleResource = R.string.something_wrong
        messageResource = R.string.error_added_photo
        okButton()
    }

    private fun saveCustomerAva(srcUri: Uri) {
        val context = requireContext()
        val size = context.dimenPxSize(R.dimen.ava_server_size)
        Glide.with(this)
            .asBitmap()
            .load(srcUri)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .override(size, size)
            .centerCrop()
            .addListener(object : RequestListener<Bitmap> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Bitmap>?,
                    isFirstResource: Boolean
                ): Boolean {
                    log.warn(e) { "Customer photo transformation failed fro src: $srcUri" }
                    return true
                }

                override fun onResourceReady(
                    resource: Bitmap,
                    model: Any?,
                    target: Target<Bitmap?>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    @Suppress("TooGenericExceptionCaught")
                    try {
                        val fileName = "customer_ava_" + System.currentTimeMillis() + ".jpg"
                        val newAvatarFile = File(context.filesDir, fileName)
                        FileOutputStream(newAvatarFile).use { fos ->
                            resource.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, fos)
                        }
                        log.info { "Customer avatar saved to $newAvatarFile" }
                        viewModel.onPickAvatar(Uri.fromFile(newAvatarFile).toURI())
                    } catch (ex: Exception) {
                        log.warn(ex) { "Can't save transformed customer avatar" }
                        mainHandler.post {
                            showPhotoAlert()
                        }
                    }
                    return true
                }
            }).submit()
    }
}
