package labone

import android.app.Dialog
import android.view.Gravity
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.example.labone.R
import splitties.resources.bool
import splitties.resources.dimenPxSize

// TODO #20979 Перенести анимацию показа dialogFragment в navGraph когда пофиксят
//  https://stackoverflow.com/questions/57462884/navigation-architecture-component-transition-animations-not-working-for-dialog
fun DialogFragment.centerDialog(dialog: Dialog) {
    dialog.window?.apply {
        attributes.windowAnimations = R.style.BottomUpAnimation
        setGravity(Gravity.CENTER)
        val mIsTablet = bool(R.bool.tablet_attribute)
        if (mIsTablet) {
            val widthPx = dimenPxSize(R.dimen.tablet_center_dialog_width)
            setLayout(widthPx, WindowManager.LayoutParams.WRAP_CONTENT)
        } else {
            setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )
        }
    }
}
