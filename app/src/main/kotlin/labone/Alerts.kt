package labone

import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.example.labone.R
import splitties.alertdialog.appcompat.alertDialog
import splitties.alertdialog.appcompat.negativeButton

inline fun Fragment.showAlert(dialogConfig: AlertDialog.Builder.() -> Unit): AlertDialog? {
    return if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
        context?.alertDialog(dialogConfig)?.also { it.show() }
    } else {
        null
    }
}

@Suppress("NOTHING_TO_INLINE")
inline fun AlertDialog.Builder.cancelButton(noinline onCancel: ((DialogInterface) -> Unit)? = null) {
    if (onCancel == null) {
        setNegativeButton(R.string.cancel, null)
    } else {
        negativeButton(R.string.cancel, onCancel)
        setOnCancelListener(onCancel)
    }
}
