package labone

import android.content.Context
import android.content.DialogInterface
import android.widget.Toast
import androidx.annotation.StringRes
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

fun Fragment.showToast(text: String, duration: Int = Toast.LENGTH_SHORT) =
    Toast.makeText(context, text, duration).show()

fun Fragment.showToast(@StringRes resId: Int, duration: Int = Toast.LENGTH_SHORT) =
    Toast.makeText(context, resId, duration).show()

fun Context.showToast(text: String, duration: Int = Toast.LENGTH_LONG): Unit =
    Toast.makeText(this, text, duration).show()
