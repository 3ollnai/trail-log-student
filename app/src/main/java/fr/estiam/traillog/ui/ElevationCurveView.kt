package fr.estiam.traillog.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import fr.estiam.traillog.R

/** M2 custom View: Canvas drawing, XML attrs, basic TalkBack description. */
class ElevationCurveView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE; strokeWidth = 3 * resources.displayMetrics.density }
    private var samples: List<Float> = emptyList()
    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.ElevationCurveView, defStyleAttr, 0)
        try {
            linePaint.color = a.getColor(R.styleable.ElevationCurveView_curveColor, 0xff246b50.toInt())
            linePaint.strokeWidth = a.getDimension(R.styleable.ElevationCurveView_curveStrokeWidth, 3 * resources.displayMetrics.density)
        } finally { a.recycle() }
        isFocusable = true
    }
    fun setSamples(values: List<Float>) { samples = values; invalidate() }
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (samples.size < 2) return
        val left = paddingLeft.toFloat(); val right = (width - paddingRight).toFloat()
        val top = paddingTop.toFloat() + 8; val bottom = (height - paddingBottom).toFloat() - 8
        val min = samples.minOrNull() ?: 0f; val max = samples.maxOrNull() ?: 1f
        val span = (max - min).coerceAtLeast(1f)
        val path = Path()
        samples.forEachIndexed { i, n ->
            val x = left + (right - left) * i / (samples.size - 1)
            val y = bottom - ((n - min) / span) * (bottom - top)
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        canvas.drawPath(path, linePaint)
    }
}
