package labone

import android.app.Dialog
import android.view.Gravity
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.example.labone.R

// TODO #20979 Перенести анимацию показа dialogFragment в navGraph когда пофиксят
//  https://stackoverflow.com/questions/57462884/navigation-architecture-component-transition-animations-not-working-for-dialog
fun DialogFragment.centerDialog(dialog: Dialog) {
    dialog.window?.apply {
        attributes.windowAnimations = R.style.BottomUpAnimation
        setGravity(Gravity.CENTER)
        setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
    }
}
