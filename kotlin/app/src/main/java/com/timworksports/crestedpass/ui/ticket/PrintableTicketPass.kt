package com.timworksports.crestedpass.ui.ticket

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.timworksports.crestedpass.data.model.Ticket
import com.timworksports.crestedpass.ui.components.QrBitmap
import com.timworksports.crestedpass.ui.theme.Border
import com.timworksports.crestedpass.ui.theme.Cream
import com.timworksports.crestedpass.ui.theme.Gold
import com.timworksports.crestedpass.ui.theme.Muted
import com.timworksports.crestedpass.ui.theme.Serif
import com.timworksports.crestedpass.ui.theme.SurfaceWhite

@Composable
fun PrintableTicketPass(
    ticket: Ticket,
    holderName: String,
    modifier: Modifier = Modifier
) {
    val qr = remember(ticket.qrToken) { QrBitmap.painter(ticket.qrToken) }
    val boxes = listOf(
        TicketPassPainter.pair(ticket.gate),
        TicketPassPainter.pair(ticket.block),
        TicketPassPainter.pair(ticket.seat)
    )
    Column(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, Border, RoundedCornerShape(14.dp))
            .background(Cream)
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .background(Color.Black)
                .padding(horizontal = 20.dp, vertical = 18.dp)
        ) {
            Text(
                "CRESTED PASS",
                color = Gold,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.4.sp,
                fontFamily = FontFamily.SansSerif
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "AFCON 2027 MATCH TICKET",
                color = SurfaceWhite,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = Serif
            )
        }
        Column(Modifier.padding(20.dp)) {
            Text(
                "MATCH",
                color = Gold,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.4.sp
            )
            Spacer(Modifier.height(6.dp))
            Text(
                ticket.matchLabel,
                color = Color.Black,
                fontSize = 26.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = Serif
            )
            Spacer(Modifier.height(4.dp))
            Text(ticket.venue, color = Gold, fontSize = 13.sp)
            Spacer(Modifier.height(16.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                boxes.forEach { (label, value) ->
                    Column(
                        Modifier
                            .weight(1f)
                            .border(1.dp, Border, RoundedCornerShape(6.dp))
                            .background(SurfaceWhite, RoundedCornerShape(6.dp))
                            .padding(horizontal = 10.dp, vertical = 10.dp)
                    ) {
                        Text(
                            label,
                            color = Gold,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.2.sp
                        )
                        Text(
                            value,
                            color = Color.Black,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = Serif
                        )
                    }
                }
            }
            Spacer(Modifier.height(20.dp))
            Image(
                painter = qr,
                contentDescription = "Ticket QR code",
                modifier = Modifier
                    .size(168.dp)
                    .align(Alignment.CenterHorizontally)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                ticket.qrToken,
                color = Muted,
                fontSize = 10.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
            Text("Issued to $holderName", color = Color.Black, fontSize = 13.sp)
            Spacer(Modifier.height(2.dp))
            Text(ticket.kickoffNote, color = Muted, fontSize = 12.sp)
            Spacer(Modifier.height(14.dp))
            HorizontalDivider(color = Border, thickness = 1.dp)
            Spacer(Modifier.height(10.dp))
            Text("Licensed to Uganda Tourism Board", color = Muted, fontSize = 10.sp)
            Spacer(Modifier.height(2.dp))
            Text("Timwork Sports  ·  Crested Pass", color = Muted, fontSize = 10.sp)
        }
    }
}
