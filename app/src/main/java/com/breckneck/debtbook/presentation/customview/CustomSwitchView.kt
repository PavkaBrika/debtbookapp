package com.breckneck.debtbook.presentation.customview

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.breckneck.debtbook.R

class CustomSwitchView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val DEFAULT_WIDTH = 300
    private val DEFAULT_HEIGHT = 50
    private val DEFAULT_BACKGROUND_COLOR = Color.parseColor("#D9D9D9")
    private val DEFAULT_ENABLED_BACKGROUND_COLOR = Color.parseColor("#4dba34")
    private val DEFAULT_DISABLED_BACKGROUND_COLOR = Color.parseColor("#b04c4c")
    private val DEFAULT_TEXT_COLOR = Color.parseColor("#ffffff")
    private val DEFAULT_ENABLED_TEXT_COLOR = Color.parseColor("#ffffff")
    private val DEFAULT_DISABLED_TEXT_COLOR = Color.parseColor("#ffffff")
    private val DEFAULT_ENABLED_TEXT = "Enabled"
    private val DEFAULT_DISABLED_TEXT = "Disabled"
    private val DEFAULT_ENABLED = true

    private var backgroundColor = Color.GRAY
    private var enabledBackgroundColor = Color.GREEN
    private var disabledBackgroundColor = Color.RED
    private var textColor = Color.WHITE
    private var enabledTextColor = Color.RED
    private var disabledTextColor = Color.GREEN
    private var enabledText = "Enabled"
    private var disabledText = "Disabled"
    private var isEnabled = true

    //RECT
    private val bodyRect = Rect()
    private val stateRect = Rect()
    //RECT PAINT
    private val bodyRectPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val stateRectPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    //TEXT PAINT
    private val enabledTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val disabledTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)


    init {
        if (attrs != null) {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomSwitchView)
            backgroundColor = typedArray.getColor(R.styleable.CustomSwitchView_backgroundColor, DEFAULT_BACKGROUND_COLOR)
            enabledBackgroundColor = typedArray.getColor(R.styleable.CustomSwitchView_enabledBackgroundColor, DEFAULT_ENABLED_BACKGROUND_COLOR)
            disabledBackgroundColor = typedArray.getColor(R.styleable.CustomSwitchView_disabledBackgroundColor, DEFAULT_DISABLED_BACKGROUND_COLOR)
            textColor = typedArray.getColor(R.styleable.CustomSwitchView_textColor, DEFAULT_TEXT_COLOR)
            enabledTextColor = typedArray.getColor(R.styleable.CustomSwitchView_enabledTextColor, DEFAULT_ENABLED_TEXT_COLOR)
            disabledTextColor = typedArray.getColor(R.styleable.CustomSwitchView_disabledTextColor, DEFAULT_DISABLED_TEXT_COLOR)
            enabledText = typedArray.getString(R.styleable.CustomSwitchView_enabledText) ?: DEFAULT_ENABLED_TEXT
            disabledText = typedArray.getString(R.styleable.CustomSwitchView_disabledText) ?: DEFAULT_DISABLED_TEXT
            isEnabled = typedArray.getBoolean(R.styleable.CustomSwitchView_enabled, DEFAULT_ENABLED)
            typedArray.recycle()
        }
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val width: Int
        val height: Int

        if (widthMode == MeasureSpec.EXACTLY)
            width = widthSize
        else if (widthMode == MeasureSpec.AT_MOST)
            width = DEFAULT_WIDTH.coerceAtMost(widthSize)
        else
            width = DEFAULT_WIDTH

        if (heightMode == MeasureSpec.EXACTLY)
            height = heightSize
        else if (heightMode == MeasureSpec.AT_MOST)
            height = DEFAULT_HEIGHT.coerceAtMost(heightSize)
        else
            height = DEFAULT_HEIGHT

        setMeasuredDimension(width, height)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w == 0)
            return
        bodyRect.left = 0
        bodyRect.top = h
        bodyRect.right = w
        bodyRect.bottom = 0

        if (isEnabled) {
            stateRect.left = 0
            stateRect.top = 0
            stateRect.right = w / 2
            stateRect.bottom = h
        } else {
            stateRect.left = w / 2
            stateRect.top = 0
            stateRect.right = w
            stateRect.bottom = h
        }

    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        drawBodyRect(canvas = canvas!!)
        drawStateRect(canvas = canvas)
        drawText(canvas = canvas)
        invalidate()
    }

    private fun drawBodyRect(canvas: Canvas) {
        bodyRectPaint.color = backgroundColor
        canvas.drawRoundRect(RectF(bodyRect), 15F, 15F, bodyRectPaint)
        invalidate()
        requestLayout()
    }

    private fun drawStateRect(canvas: Canvas) {
        if (isEnabled)
            stateRectPaint.color = enabledBackgroundColor
        else
            stateRectPaint.color = disabledBackgroundColor
        canvas.drawRoundRect(RectF(stateRect), 15F, 15F, stateRectPaint)
        invalidate()
        requestLayout()
    }

    private fun drawText(canvas: Canvas) {
        enabledTextPaint.color = enabledTextColor
        enabledTextPaint.textAlign = Paint.Align.CENTER
        enabledTextPaint.textSize = height * 0.5F
        enabledTextPaint.typeface = Typeface.DEFAULT_BOLD
        val offsetY = (enabledTextPaint.descent() + enabledTextPaint.ascent())
        canvas.drawText(enabledText,
            (bodyRect.right / 4).toFloat(), stateRect.bottom + offsetY, enabledTextPaint)
        disabledTextPaint.color = disabledTextColor
        disabledTextPaint.textAlign = Paint.Align.CENTER
        disabledTextPaint.textSize = height * 0.5F
        disabledTextPaint.typeface = Typeface.DEFAULT_BOLD
        canvas.drawText(disabledText,
            (bodyRect.right - bodyRect.right / 4).toFloat(), stateRect.bottom + offsetY, disabledTextPaint)
    }

//    override fun onTouchEvent(event: MotionEvent?): Boolean {
//        super.onTouchEvent(event!!)
//        if (checkClickInStateRect(event.x, event.y)) {
//
//            return true
//        }
//        return false
//    }
//
//    private fun checkClickInStateRect(clickX: Float, clickY: Float): Boolean {
//        if (clickX > stateRect.centerX() - stateRect.)
//    }
}