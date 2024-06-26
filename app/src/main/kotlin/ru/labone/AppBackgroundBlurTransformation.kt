package ru.labone

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.Config.ARGB_8888
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PorterDuff.Mode
import android.graphics.PorterDuff.Mode.SRC_ATOP
import android.graphics.PorterDuffColorFilter
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import androidx.annotation.Px
import coil.size.Dimension
import coil.size.Size
import coil.size.isOriginal
import coil.size.pxOrElse
import coil.transform.Transformation
import kotlin.math.roundToInt

const val SCALE = 10

class AppBackgroundBlurTransformation(
    @Px private val radius: Float = 0f,
    val context: Context,
) : Transformation {

    override val cacheKey = "${javaClass.name}-$radius"

    override suspend fun transform(input: Bitmap, size: Size): Bitmap {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        val (outputWidth, outputHeight) = calculateOutputSize(input, size)
        val output = input.blurBitmap(outputWidth, outputHeight, paint)
        val multiplier = calculateScaleFactor(input.width, input.height, outputWidth, outputHeight)
        val dx = (outputWidth - multiplier * input.width) / 2
        val dy = (outputHeight - multiplier * input.height) / 2
        val scaleToFit = Matrix().apply {
            setTranslate(dx, dy)
            preScale(multiplier, multiplier)
        }
        val ret = Bitmap.createBitmap(outputWidth, outputHeight, ARGB_8888)
        val retCanvas = Canvas(ret)
        val rect = RectF(0f, 0f, output.width.toFloat(), output.height.toFloat())
        retCanvas.drawRoundRect(rect, radius, radius, paint)
        paint.setXfermode(PorterDuffXfermode(Mode.SRC_IN))
        retCanvas.drawBitmap(output, 0f, 0f, paint)
        retCanvas.drawBitmap(input, scaleToFit, null)
        output.recycle()

        return ret
    }

    private fun Bitmap.blurBitmap(outputWidth: Int, outputHeight: Int, paint: Paint): Bitmap {
        val width: Int = width / SCALE
        val height: Int = height / SCALE
        var output = Bitmap.createBitmap(width, height, ARGB_8888)
        val canvas = Canvas(output)
        canvas.scale(1 / SCALE.toFloat(), 1 / SCALE.toFloat())
        val filter = PorterDuffColorFilter(Color.TRANSPARENT, SRC_ATOP)
        paint.setColorFilter(filter)
        canvas.drawBitmap(this, 0f, 0f, paint)
        output = Blur.rs(context, output)

        val scaled = Bitmap.createScaledBitmap(output, outputWidth, outputHeight, true)
        output.recycle()

        return scaled
    }

    private fun calculateOutputSize(input: Bitmap, size: Size): Pair<Int, Int> =
        if (size.isOriginal) {
            input.width to input.height
        } else {
            val (dstWidth, dstHeight) = size
            if (dstWidth is Dimension.Pixels && dstHeight is Dimension.Pixels) {
                dstWidth.px to dstHeight.px
            } else {
                val multiplier = calculateScaleFactor(
                    srcWidth = input.width,
                    srcHeight = input.height,
                    dstWidth = dstWidth.pxOrElse { Int.MIN_VALUE },
                    dstHeight = dstHeight.pxOrElse { Int.MIN_VALUE }
                )
                val outputWidth = (multiplier * input.width).roundToInt()
                val outputHeight = (multiplier * input.height).roundToInt()
                outputWidth to outputHeight
            }

        }

    private fun calculateScaleFactor(
        srcWidth: Int,
        srcHeight: Int,
        dstWidth: Int,
        dstHeight: Int
    ): Float {
        val maxScale = maxOf(dstWidth.toFloat() / srcWidth, dstHeight.toFloat() / srcHeight)
        return minOf(maxScale, 1f)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return other is AppBackgroundBlurTransformation && radius == other.radius
    }

    override fun hashCode(): Int = radius.hashCode()
}
