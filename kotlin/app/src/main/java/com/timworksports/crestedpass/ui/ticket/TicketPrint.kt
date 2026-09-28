package com.timworksports.crestedpass.ui.ticket

import android.content.Context
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.print.PrintManager
import android.print.pdf.PrintedPdfDocument
import com.timworksports.crestedpass.data.model.Ticket
import java.io.FileOutputStream

fun printMatchTicket(context: Context, ticket: Ticket, holderName: String) {
    val manager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
    val job = "Crested Pass — ${ticket.matchLabel}"
    val attrs = PrintAttributes.Builder()
        .setMediaSize(PrintAttributes.MediaSize.ISO_A5)
        .setColorMode(PrintAttributes.COLOR_MODE_COLOR)
        .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
        .build()
    manager.print(job, TicketPrintAdapter(context, ticket, holderName), attrs)
}

private class TicketPrintAdapter(
    private val context: Context,
    private val ticket: Ticket,
    private val holderName: String
) : PrintDocumentAdapter() {
    private var document: PrintedPdfDocument? = null

    override fun onLayout(
        oldAttributes: PrintAttributes?,
        newAttributes: PrintAttributes,
        cancellationSignal: CancellationSignal?,
        callback: LayoutResultCallback,
        extras: Bundle?
    ) {
        document = PrintedPdfDocument(context, newAttributes)
        if (cancellationSignal?.isCanceled == true) {
            callback.onLayoutCancelled()
            return
        }
        val info = PrintDocumentInfo.Builder("crested-pass-ticket.pdf")
            .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
            .setPageCount(1)
            .build()
        callback.onLayoutFinished(info, true)
    }

    override fun onWrite(
        pages: Array<out PageRange>,
        destination: ParcelFileDescriptor,
        cancellationSignal: CancellationSignal?,
        callback: WriteResultCallback
    ) {
        val pdf = document
        if (pdf == null) {
            callback.onWriteFailed("Missing print document")
            return
        }
        if (cancellationSignal?.isCanceled == true) {
            callback.onWriteCancelled()
            return
        }
        val page = pdf.startPage(0)
        TicketPassPainter.draw(
            page.canvas,
            page.info.pageWidth.toFloat(),
            page.info.pageHeight.toFloat(),
            ticket,
            holderName
        )
        pdf.finishPage(page)
        try {
            FileOutputStream(destination.fileDescriptor).use { pdf.writeTo(it) }
            callback.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
        } catch (error: Exception) {
            callback.onWriteFailed(error.message)
        } finally {
            pdf.close()
            document = null
        }
    }
}
