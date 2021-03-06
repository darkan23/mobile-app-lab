package labone

import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.labone.R
import kotlinx.android.synthetic.main.date_picker_dialog.view.*
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import splitties.alertdialog.appcompat.okButton
import splitties.resources.color
import splitties.resources.dimen

fun Fragment.showDatePicker(
    startDate: LocalDate,
    maxDate: Instant? = null,
    onPickDate: (date: LocalDate) -> Unit
) {
    val view = layoutInflater.inflate(R.layout.date_picker_dialog, view as? ViewGroup, false)
        .apply {
            datePicker.init(startDate.year, startDate.monthValue - 1, startDate.dayOfMonth)
            datePicker.setSelectionDivider(ColorDrawable(color(R.color.colorAccent)))
            maxDate?.run { datePicker.setMaxDate(toEpochMilli()) }
        }
    showAlert {
        setCancelable(true)
        setView(view)
        okButton {
            val date = view.datePicker.let { LocalDate.of(it.year, it.month + 1, it.dayOfMonth) }
            onPickDate(date)
        }
        cancelButton()
    }?.applyDateTimePickerSize(view, R.dimen.date_time_picker_width)
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