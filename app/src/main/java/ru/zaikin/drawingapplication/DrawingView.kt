package ru.zaikin.drawingapplication

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View

class DrawingView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var drawPath: FingerPath = FingerPath(Color.BLACK, 20f)
    private var drawPaint: Paint = Paint()
    private var canvasPaint: Paint = Paint(Paint.DITHER_FLAG)
    private var color = Color.BLACK
    private lateinit var drawCanvas: Canvas
    private lateinit var canvasBitmap: Bitmap
    private var brushSize: Float = 20f
    private val paths = mutableListOf<FingerPath>()

    init {
        setUpDrawing()
    }

    private fun setUpDrawing() {
        drawPaint.color = color
        drawPaint.isAntiAlias = true
        drawPaint.strokeWidth = brushSize
        drawPaint.style = Paint.Style.STROKE
        drawPaint.strokeJoin = Paint.Join.ROUND
        drawPaint.strokeCap = Paint.Cap.ROUND
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        drawCanvas = Canvas(canvasBitmap)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(canvasBitmap, 0f, 0f, canvasPaint)


        for (path in paths) {
            drawPaint.color = drawPath.color
            drawPaint.strokeWidth = path.brushThickness
            canvas.drawPath(path, drawPaint)
        }

        /*if (!drawPath.isEmpty) {
            drawPaint.color = drawPath.color
            drawPaint.strokeWidth = path.brushThickness
            canvas.drawPath(drawPath, drawPaint)
        }*/
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val touchX = event?.x
        val touchY = event?.y

        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                drawPath.color = color
                drawPath.brushThickness = brushSize
                drawPath.reset()
                drawPath.moveTo(touchX!!, touchY!!)
            }

            MotionEvent.ACTION_MOVE -> {
                drawPath.lineTo(touchX!!, touchY!!)
            }

            MotionEvent.ACTION_UP -> {
                drawCanvas.drawPath(drawPath, drawPaint)
                drawPath = FingerPath(color, brushSize)
                paths.add(drawPath)
            }

            else -> return false
        }

        invalidate()
        return true
    }

    fun changeBrushSize(newSize: Float) {
        brushSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            newSize, resources.displayMetrics
        )

        drawPaint.strokeWidth = brushSize
    }

    internal inner class FingerPath(var color: Int, var brushThickness: Float) : Path()
}
