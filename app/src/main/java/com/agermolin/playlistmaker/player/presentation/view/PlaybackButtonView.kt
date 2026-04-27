package com.agermolin.playlistmaker.player.presentation.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.agermolin.playlistmaker.R
import com.google.android.material.R as MaterialR
import kotlin.jvm.JvmOverloads
import kotlin.math.min

class PlaybackButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {

    private val playDrawable: Drawable
    private val pauseDrawable: Drawable
    private val backgroundPaint: Paint
    private val circleRectF = RectF()
    private val iconBounds = Rect()
    private var isPlaying: Boolean = false

    init {
        isClickable = true
        val a = context.obtainStyledAttributes(attrs, R.styleable.PlaybackButtonView, defStyleAttr, 0)
        val playId = a.getResourceId(
            R.styleable.PlaybackButtonView_playbackPlayIcon,
            R.drawable.play,
        )
        val pauseId = a.getResourceId(
            R.styleable.PlaybackButtonView_playbackPauseIcon,
            R.drawable.pause,
        )
        a.recycle()

        val iconColor = resolveThemeColor(MaterialR.attr.colorSecondary)
        val bgColor = resolveThemeColor(MaterialR.attr.colorOnSecondary)

        playDrawable = requireNotNull(ContextCompat.getDrawable(context, playId)).mutate()
        pauseDrawable = requireNotNull(ContextCompat.getDrawable(context, pauseId)).mutate()
        playDrawable.setTint(iconColor)
        pauseDrawable.setTint(iconColor)

        backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = bgColor
            style = Paint.Style.FILL
        }

        updateContentDescription()
    }

    private fun resolveThemeColor(attr: Int): Int {
        val typedValue = android.util.TypedValue()
        if (context.theme.resolveAttribute(attr, typedValue, true)) {
            if (typedValue.resourceId != 0) {
                return ContextCompat.getColor(context, typedValue.resourceId)
            }
            if (typedValue.type >= android.util.TypedValue.TYPE_FIRST_COLOR_INT &&
                typedValue.type <= android.util.TypedValue.TYPE_LAST_COLOR_INT
            ) {
                return typedValue.data
            }
        }
        return ContextCompat.getColor(context, R.color.white)
    }

    /**
     * Синхронизация с фактом воспроизведения: при [playing] == true показываем иконку «Пауза».
     */
    fun setPlaying(playing: Boolean) {
        if (isPlaying == playing) return
        isPlaying = playing
        updateContentDescription()
        invalidate()
    }

    private fun currentDrawable(): Drawable = if (isPlaying) pauseDrawable else playDrawable

    private fun updateContentDescription() {
        val res = if (isPlaying) R.string.pause else R.string.play
        contentDescription = context.getString(res)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isEnabled) {
            return false
        }
        return when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> true
            MotionEvent.ACTION_UP -> {
                performClick()
                true
            }
            else -> super.onTouchEvent(event)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        circleRectF.set(0f, 0f, w.toFloat(), h.toFloat())
        val maxIcon = resources.getDimensionPixelSize(R.dimen.player_playback_button_icon_size)
        val side = min(maxIcon, min(w, h))
        val left = (w - side) / 2
        val top = (h - side) / 2
        iconBounds.set(left, top, left + side, top + side)
    }

    override fun onDraw(canvas: Canvas) {
        val alpha = if (isEnabled) 255 else 97
        backgroundPaint.alpha = alpha
        val d = currentDrawable()
        d.alpha = alpha
        canvas.drawOval(circleRectF, backgroundPaint)
        d.setBounds(iconBounds)
        d.draw(canvas)
    }
}
