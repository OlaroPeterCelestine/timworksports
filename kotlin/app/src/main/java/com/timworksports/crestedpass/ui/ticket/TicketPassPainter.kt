package com.timworksports.crestedpass.ui.ticket

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import com.timworksports.crestedpass.data.model.Ticket
import com.timworksports.crestedpass.ui.components.QrBitmap

/**
 * Shared A5 ticket artboard (420 × 595 pt). iOS draws the same coordinates
 * so printed pages match in demos.
 */
object TicketPassPainter {
    const val PAGE_WIDTH = 420f
    const val PAGE_HEIGHT = 595f

    private const val NAVY = 0xFF13293D.toInt()
    private const val GOLD = 0xFFC9A227.toInt()
    private const val CREAM = 0xFFFAF7F2.toInt()
    private const val BORDER = 0xFFE7E2D8.toInt()
    private const val MUTED = 0xFF8A7F6E.toInt()
    private const val WHITE = 0xFFFFFFFF.toInt()

    fun draw(canvas: Canvas, pageWidth: Float, pageHeight: Float, ticket: Ticket, holderName: String) {
        canvas.save()
        canvas.scale(pageWidth / PAGE_WIDTH, pageHeight / PAGE_HEIGHT)

        fill(canvas, 0f, 0f, PAGE_WIDTH, PAGE_HEIGHT, CREAM)
        fill(canvas, 0f, 0f, PAGE_WIDTH, 100f, NAVY)

        text(canvas, "CRESTED PASS", 28f, 28f, 11f, GOLD, sans = true, bold = true, tracking = 0.14f)
        text(canvas, "AFCON 2027 MATCH TICKET", 28f, 52f, 18f, WHITE, sans = false, bold = true)

        text(canvas, "MATCH", 28f, 122f, 11f, GOLD, sans = true, bold = true, tracking = 0.14f)
        text(canvas, ticket.matchLabel, 28f, 142f, 26f, NAVY, sans = false, bold = true)
        text(canvas, ticket.venue, 28f, 176f, 13f, GOLD, sans = true, bold = false)

        val boxes = listOf(
            pair(ticket.gate),
            pair(ticket.block),
            pair(ticket.seat)
        )
        val boxY = 206f
        val boxW = 114f
        val boxH = 62f
        boxes.forEachIndexed { index, (label, value) ->
            val x = 28f + index * (boxW + 10f)
            strokeBox(canvas, x, boxY, boxW, boxH)
            text(canvas, label, x + 12f, boxY + 12f, 10f, GOLD, sans = true, bold = true, tracking = 0.12f)
            text(canvas, value, x + 12f, boxY + 30f, 20f, NAVY, sans = false, bold = true)
        }

        val qr = QrBitmap.encode(ticket.qrToken, 336)
        val qrSize = 168f
        val qrX = (PAGE_WIDTH - qrSize) / 2f
        val qrY = 288f
        canvas.drawBitmap(qr, null, RectF(qrX, qrY, qrX + qrSize, qrY + qrSize), null)
        textCentered(canvas, ticket.qrToken, PAGE_WIDTH / 2f, 466f, 10f, MUTED)

        text(canvas, "Issued to $holderName", 28f, 500f, 13f, NAVY, sans = true, bold = false)
        text(canvas, ticket.kickoffNote, 28f, 520f, 12f, MUTED, sans = true, bold = false)

        val line = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = BORDER
            strokeWidth = 1f
        }
        canvas.drawLine(28f, 546f, 392f, 546f, line)
        text(canvas, "Licensed to Uganda Tourism Board", 28f, 556f, 10f, MUTED, sans = true, bold = false)
        text(canvas, "Timwork Sports  ·  Crested Pass", 28f, 572f, 10f, MUTED, sans = true, bold = false)

        canvas.restore()
    }

    fun pair(raw: String): Pair<String, String> {
        val parts = raw.trim().split(" ", limit = 2)
        val label = parts[0].uppercase()
        val value = parts.getOrNull(1) ?: raw
        return label to value
    }

    private fun fill(canvas: Canvas, x: Float, y: Float, w: Float, h: Float, color: Int) {
        canvas.drawRect(x, y, x + w, y + h, Paint(Paint.ANTI_ALIAS_FLAG).apply { this.color = color })
    }

    private fun strokeBox(canvas: Canvas, x: Float, y: Float, w: Float, h: Float) {
        val fill = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = WHITE }
        val stroke = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = BORDER
            style = Paint.Style.STROKE
            strokeWidth = 1f
        }
        val rect = RectF(x, y, x + w, y + h)
        canvas.drawRoundRect(rect, 6f, 6f, fill)
        canvas.drawRoundRect(rect, 6f, 6f, stroke)
    }

    private fun text(
        canvas: Canvas,
        value: String,
        x: Float,
        yTop: Float,
        size: Float,
        color: Int,
        sans: Boolean,
        bold: Boolean,
        tracking: Float = 0f
    ) {
        val paint = paint(size, color, sans, bold, tracking)
        canvas.drawText(value, x, yTop - paint.ascent(), paint)
    }

    private fun textCentered(
        canvas: Canvas,
        value: String,
        cx: Float,
        yTop: Float,
        size: Float,
        color: Int
    ) {
        val paint = paint(size, color, sans = true, bold = false).apply {
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText(value, cx, yTop - paint.ascent(), paint)
    }

    private fun paint(
        size: Float,
        color: Int,
        sans: Boolean,
        bold: Boolean,
        tracking: Float = 0f
    ): Paint {
        val family = if (sans) Typeface.SANS_SERIF else Typeface.SERIF
        val style = if (bold) Typeface.BOLD else Typeface.NORMAL
        return Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = color
            textSize = size
            typeface = Typeface.create(family, style)
            letterSpacing = tracking
        }
    }
}
