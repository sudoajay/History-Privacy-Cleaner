package com.sudoajay.historycachecleaner.helper

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.annotation.ColorInt
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.sudoajay.historyprivacycleaner.R
import kotlin.math.roundToInt

open class InsetDivider private constructor() : ItemDecoration() {
    private val mPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    // in pixel
    var dividerHeight = 0

    // left inset for vertical list, top inset for horizontal list
    var firstInset = 0

    // right inset for vertical list, bottom inset for horizontal list
    var secondInset = 0
    private var mColor = 0
    var orientation = 0

    // set it to true to draw divider on the tile, or false to draw beside the tile.
    // if you set it to false and have inset at the same time, you may see the background of
    // the parent of RecyclerView.
    var overlay = false

    var color: Int
        get() = mColor
        set(color) {
            mColor = color
            mPaint.color = color
        }

    override fun onDrawOver(
        c: Canvas,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        if (orientation == VERTICAL_LIST) {
            drawVertical(c, parent)
        } else {
            drawHorizontal(c, parent)
        }
    }

    private fun drawVertical(c: Canvas, parent: RecyclerView) {
        val left = parent.paddingLeft + firstInset
        val right = parent.width - parent.paddingRight - secondInset
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            if (parent.getChildAdapterPosition(child) == parent.adapter!!.itemCount - 1) {
                continue
            }
            val params =
                child.layoutParams as RecyclerView.LayoutParams
            val bottom: Int
            val top: Int
            if (overlay) {
                bottom = child.bottom + params.bottomMargin + ViewCompat.getTranslationY(child)
                    .roundToInt()
                top = bottom - dividerHeight
            } else {
                top = child.bottom + params.bottomMargin + ViewCompat.getTranslationY(child)
                    .roundToInt()
                bottom = top + dividerHeight
            }
            c.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), mPaint)
        }
    }

    private fun drawHorizontal(c: Canvas, parent: RecyclerView) {
        val top = parent.paddingTop + firstInset
        val bottom = parent.height - parent.paddingBottom - secondInset
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            if (parent.getChildAdapterPosition(child) == parent.adapter!!.itemCount - 1) {
                continue
            }
            val params = child
                .layoutParams as RecyclerView.LayoutParams
            val right: Int
            val left: Int
            if (overlay) {
                right = child.right + params.rightMargin + ViewCompat.getTranslationX(child)
                    .roundToInt()
                left = right - dividerHeight
            } else {
                left = child.right + params.rightMargin + ViewCompat.getTranslationX(child)
                    .roundToInt()
                right = left + dividerHeight
            }
            c.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), mPaint)
        }
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        if (overlay) {
            super.getItemOffsets(outRect, view, parent, state)
            return
        }
        if (orientation == VERTICAL_LIST) {
            outRect[0, 0, 0] = dividerHeight
        } else {
            outRect[0, 0, dividerHeight] = 0
        }
    }

    /**
     * Handy builder for creating [InsetDivider] instance.
     */
    class Builder(private val mContext: Context) {
        private var mDividerHeight = 0
        private var mFirstInset = 0
        private var mSecondInset = 0
        private var mColor = 0
        private var mOrientation = 0
        private var mOverlay =
            true // set default to true to follow Material Design Guidelines

        fun dividerHeight(dividerHeight: Int): Builder {
            mDividerHeight = dividerHeight
            return this
        }

        fun insets(
            firstInset: Int,
            secondInset: Int
        ): Builder {
            mFirstInset = firstInset
            mSecondInset = secondInset
            return this
        }

        fun color(@ColorInt color: Int): Builder {
            mColor = color
            return this
        }

        fun orientation(orientation: Int): Builder {
            mOrientation = orientation
            return this
        }


        fun build(): InsetDivider {
            val insetDivider = InsetDivider()
            when {
                mDividerHeight == 0 -> {
                    // Set default divider height to 1dp.
                    insetDivider.dividerHeight =
                        mContext.resources.getDimensionPixelSize(R.dimen.divider_height)
                }
                mDividerHeight > 0 -> {
                    insetDivider.dividerHeight = mDividerHeight
                }
                else -> {
                    throw IllegalArgumentException("Divider's height can't be negative.")
                }
            }
            insetDivider.firstInset = if (mFirstInset < 0) 0 else mFirstInset
            insetDivider.secondInset = if (mSecondInset < 0) 0 else mSecondInset
            require(mColor != 0) { "Don't forget to set color" }
            insetDivider.color = mColor
            require(!(mOrientation != HORIZONTAL_LIST && mOrientation != VERTICAL_LIST)) { "Invalid orientation" }
            insetDivider.orientation = mOrientation
            insetDivider.overlay = mOverlay
            return insetDivider
        }

    }

    companion object {
        const val HORIZONTAL_LIST = LinearLayoutManager.HORIZONTAL
        const val VERTICAL_LIST = LinearLayoutManager.VERTICAL
    }

    init {
        mPaint.style = Paint.Style.FILL
    }
}