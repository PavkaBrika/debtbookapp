package com.breckneck.debtbook.finance.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.breckneck.debtbook.R
import com.breckneck.deptbook.domain.model.FinanceCategoryWithFinances

class FinanceProgressBar(context: Context, attrs: AttributeSet): View(context, attrs) {

    private val DEFAULT_WIDTH = 300
    private val DEFAULT_HEIGHT = 50
    private val DEFAULT_BACKGROUND_COLOR = Color.parseColor("#D9D9D9")

    private var backgroundColor = Color.GRAY

    private var categoryList: List<FinanceCategoryWithFinances> = listOf(
//        FinanceCategoryWithFinances(
//            financeCategory = FinanceCategory(name = "", color = "#4A148C", image = 1),
//            categoryPercentage = 30,
//            financeList = mutableListOf()
//        ),
//        FinanceCategoryWithFinances(
//            financeCategory = FinanceCategory(name = "", color = "#000000", image = 1),
//            categoryPercentage = 30,
//            financeList = mutableListOf()
//        ),
//        FinanceCategoryWithFinances(
//            financeCategory = FinanceCategory(name = "", color = "#311B92", image = 1),
//            categoryPercentage = 20,
//            financeList = mutableListOf()
//        ),
//        FinanceCategoryWithFinances(
//            financeCategory = FinanceCategory(name = "", color = "#1A237E", image = 1),
//            categoryPercentage = 10,
//            financeList = mutableListOf()
//        ),
//        FinanceCategoryWithFinances(
//            financeCategory = FinanceCategory(name = "", color = "#0D47A1", image = 1),
//            categoryPercentage = 5,
//            financeList = mutableListOf()
//        )
    )

    private val bodyRect = Rect()

    private val bodyRectPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val financeProgressPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        if (attrs != null) {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.FinanceProgressBar)
            backgroundColor = typedArray.getColor(R.styleable.FinanceProgressBar_financeBackgroundColor, DEFAULT_BACKGROUND_COLOR)
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
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawBodyRect(canvas = canvas)
        drawFinanceProgressRect(canvas = canvas)
    }

    private fun drawBodyRect(canvas: Canvas) {
        bodyRectPaint.color = backgroundColor
        canvas.drawRect(RectF(bodyRect), bodyRectPaint)
        invalidate()
        requestLayout()
    }

    private fun drawFinanceProgressRect(canvas: Canvas) {
        var marginLeft = 0
        var marginRight = 0
        for (category in categoryList) {
            val categoryRect = Rect()
            marginRight = marginLeft + (bodyRect.right * category.categoryPercentage) / 100
            categoryRect.left = marginLeft
            categoryRect.top = bodyRect.top
            categoryRect.right = marginRight
            categoryRect.bottom = 0
            marginLeft = marginRight
            financeProgressPaint.color = Color.parseColor(category.financeCategory.color)
            canvas.drawRect(categoryRect, financeProgressPaint)
            invalidate()
            requestLayout()
        }
    }

    fun setCategoryList(categoryList: List<FinanceCategoryWithFinances>) {
        this.categoryList = categoryList
        invalidate()
        requestLayout()
    }
}











