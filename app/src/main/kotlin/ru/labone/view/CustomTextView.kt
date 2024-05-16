package ru.labone.view

import android.content.Context
import android.text.Layout
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import kotlin.math.ceil

// TODO for what is this for ?
class CustomTextView : AppCompatTextView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val layout = layout
        if (layout != null) {
            val width =
                ceil(getMaxLineWidth(layout).toDouble()).toInt() + compoundPaddingLeft + compoundPaddingRight
            val height = measuredHeight
            setMeasuredDimension(width, height)
        }
    }

    private fun getMaxLineWidth(layout: Layout): Float {
        val lines = layout.lineCount
        var maxWidth = 0.0f
        for (i in 0 until lines) {
            if (layout.getLineWidth(i) > maxWidth) {
                maxWidth = layout.getLineWidth(i)
            }
        }
        return maxWidth
    }
}
