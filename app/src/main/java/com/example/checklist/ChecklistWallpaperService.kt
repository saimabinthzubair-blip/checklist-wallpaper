package com.example.checklist

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.service.wallpaper.WallpaperService
import android.view.MotionEvent
import android.view.SurfaceHolder

class ChecklistWallpaperService : WallpaperService() {

    override fun onCreateEngine(): Engine = ChecklistEngine()

    inner class ChecklistEngine : Engine() {

        private val prefs by lazy { applicationContext.getSharedPreferences("checklist", Context.MODE_PRIVATE) }

        // Edit your tasks here
        private val items = listOf("Water plants", "Reply to emails", "Gym", "Read 20 min")
        private val checked = BooleanArray(items.size)

        private val titlePaint = Paint().apply { color = Color.WHITE; textSize = 90f; isAntiAlias = true }
        private val textPaint = Paint().apply { color = Color.WHITE; textSize = 60f; isAntiAlias = true }
        private val boxPaint = Paint().apply { color = Color.WHITE; style = Paint.Style.STROKE; strokeWidth = 6f; isAntiAlias = true }
        private val tickPaint = Paint().apply { color = Color.GREEN; style = Paint.Style.STROKE; strokeWidth = 10f; isAntiAlias = true }

        private val startY = 400f
        private val rowHeight = 140f
        private val boxLeft = 120f
        private val boxSize = 70f

        override fun onCreate(surfaceHolder: SurfaceHolder) {
            super.onCreate(surfaceHolder)
            setTouchEventsEnabled(true)
            // Load saved ticks safely
            try {
                items.indices.forEach { i -> checked[i] = prefs.getBoolean("item$i", false) }
            } catch (e: Exception) {
                // If loading fails, start with everything unchecked
            }
        }

        override fun onSurfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            super.onSurfaceChanged(holder, format, width, height)
            draw()
        }

        override fun onVisibilityChanged(visible: Boolean) {
            if (visible) draw()
        }

        override fun onTouchEvent(event: MotionEvent) {
            super.onTouchEvent(event)
            if (event.action == MotionEvent.ACTION_UP) {
                items.indices.forEach { i ->
                    val top = startY + i * rowHeight
                    if (event.y in top..(top + rowHeight)) {
                        checked[i] = !checked[i]
                        try {
                            prefs.edit().putBoolean("item$i", checked[i]).apply()
                        } catch (e: Exception) {
                            // Ignore save errors so the wallpaper keeps working
                        }
                        draw()
                    }
                }
            }
        }

        private fun draw() {
            if (!isPreview.not() && !surfaceHolder.surface.isValid) return
            val canvas = try {
                surfaceHolder.lockCanvas()
            } catch (e: Exception) {
                null
            } ?: return

            try {
                canvas.drawColor(Color.parseColor("#1E1E2E"))
                canvas.drawText("To Do", boxLeft, 250f, titlePaint)

                items.forEachIndexed { i, label ->
                    val top = startY + i * rowHeight
                    val boxTop = top + (rowHeight - boxSize) / 2

                    canvas.drawRect(boxLeft, boxTop, boxLeft + boxSize, boxTop + boxSize, boxPaint)

                    if (checked[i]) {
                        canvas.drawLine(boxLeft + 12, boxTop + 35, boxLeft + 30, boxTop + 58, tickPaint)
                        canvas.drawLine(boxLeft + 30, boxTop + 58, boxLeft + 60, boxTop + 12, tickPaint)
                    }

                    canvas.drawText(label, boxLeft + boxSize + 40, boxTop + 52, textPaint)
                }
            } finally {
                surfaceHolder.unlockCanvasAndPost(canvas)
            }
        }
    }
}
