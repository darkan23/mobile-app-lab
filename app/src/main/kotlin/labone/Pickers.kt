package labone

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.labone.R
import com.example.labone.databinding.DatePickerDialogBinding
import splitties.alertdialog.appcompat.okButton
import splitties.resources.dimen
import java.time.Instant
import java.time.LocalDate

fun Fragment.showDatePicker(
    startDate: LocalDate,
    maxDate: Instant? = null,
    onPickDate: (date: LocalDate) -> Unit,
) {
    val view = DatePickerDialogBinding.inflate(LayoutInflater.from(context), view as? ViewGroup, false)
        .apply {
            datePicker.init(startDate.year, startDate.monthValue - 1, startDate.dayOfMonth) { _, _, _, _ ->
            }
            maxDate?.run { datePicker.maxDate = toEpochMilli() }
        }
    showAlert {
        setCancelable(true)
        setView(view.root)
        okButton {
            val date = view.datePicker.let { LocalDate.of(it.year, it.month + 1, it.dayOfMonth) }
            onPickDate(date)
        }
        cancelButton()
    }?.applyDateTimePickerSize(view.root, R.dimen.date_time_picker_width)
}

private fun AlertDialog.applyDateTimePickerSize(view: View, widthDimenId: Int) {
    window?.apply {
        view.measure(0, 0)
        val context = view.context
        setLayout(
            context.dimen(widthDimenId).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}
