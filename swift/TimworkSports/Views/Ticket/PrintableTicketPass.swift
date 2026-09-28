import SwiftUI
import UIKit

struct PrintableTicketPass: View {
    let ticket: Ticket
    let holderName: String

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            VStack(alignment: .leading, spacing: 6) {
                Text("CRESTED PASS")
                    .font(.brandSans(11, weight: .semibold))
                    .tracking(1.4)
                    .foregroundStyle(Color.brandGold)
                Text("AFCON 2027 MATCH TICKET")
                    .font(.brandSerif(18))
                    .foregroundStyle(.white)
            }
            .padding(.horizontal, 20)
            .padding(.vertical, 18)
            .frame(maxWidth: .infinity, alignment: .leading)
            .background(Color.brandNavy)

            VStack(alignment: .leading, spacing: 0) {
                Text("MATCH")
                    .font(.brandSans(11, weight: .semibold))
                    .tracking(1.4)
                    .foregroundStyle(Color.brandGold)
                Text(ticket.matchLabel)
                    .font(.brandSerif(26))
                    .foregroundStyle(Color.brandInk)
                    .padding(.top, 6)
                Text(ticket.venue)
                    .font(.brandSans(13))
                    .foregroundStyle(Color.brandGold)
                    .padding(.top, 4)

                HStack(spacing: 10) {
                    seatBox(TicketPassLayout.pair(ticket.gate))
                    seatBox(TicketPassLayout.pair(ticket.block))
                    seatBox(TicketPassLayout.pair(ticket.seat))
                }
                .padding(.top, 16)

                QRCodeView(payload: ticket.qrToken)
                    .frame(width: 168, height: 168)
                    .frame(maxWidth: .infinity)
                    .padding(.top, 20)
                Text(ticket.qrToken)
                    .font(.brandSans(10))
                    .foregroundStyle(Color.brandMuted)
                    .frame(maxWidth: .infinity)
                    .multilineTextAlignment(.center)
                    .padding(.top, 8)

                Text("Issued to \(holderName)")
                    .font(.brandSans(13))
                    .foregroundStyle(Color.brandInk)
                    .padding(.top, 16)
                Text(ticket.kickoffNote)
                    .font(.brandSans(12))
                    .foregroundStyle(Color.brandMuted)
                    .padding(.top, 2)

                Rectangle()
                    .fill(Color.brandBorder)
                    .frame(height: 1)
                    .padding(.top, 14)
                Text("Licensed to Uganda Tourism Board")
                    .font(.brandSans(10))
                    .foregroundStyle(Color.brandMuted)
                    .padding(.top, 10)
                Text("Timwork Sports  ·  Crested Pass")
                    .font(.brandSans(10))
                    .foregroundStyle(Color.brandMuted)
                    .padding(.top, 2)
            }
            .padding(20)
            .background(Color.brandCream)
        }
        .clipShape(RoundedRectangle(cornerRadius: 14, style: .continuous))
        .overlay(
            RoundedRectangle(cornerRadius: 14, style: .continuous)
                .stroke(Color.brandBorder, lineWidth: 1)
        )
    }

    private func seatBox(_ pair: (String, String)) -> some View {
        VStack(alignment: .leading, spacing: 4) {
            Text(pair.0)
                .font(.brandSans(10, weight: .semibold))
                .tracking(1.2)
                .foregroundStyle(Color.brandGold)
            Text(pair.1)
                .font(.brandSerif(20))
                .foregroundStyle(Color.brandInk)
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(.horizontal, 10)
        .padding(.vertical, 10)
        .background(Color.white)
        .clipShape(RoundedRectangle(cornerRadius: 6, style: .continuous))
        .overlay(
            RoundedRectangle(cornerRadius: 6, style: .continuous)
                .stroke(Color.brandBorder, lineWidth: 1)
        )
    }
}

enum TicketPassLayout {
    static let pageSize = CGSize(width: 420, height: 595)

    static func pair(_ raw: String) -> (String, String) {
        let parts = raw.split(separator: " ", maxSplits: 1).map(String.init)
        let label = (parts.first ?? raw).uppercased()
        let value = parts.count > 1 ? parts[1] : raw
        return (label, value)
    }

    static func pdfData(ticket: Ticket, holderName: String) -> Data {
        let bounds = CGRect(origin: .zero, size: pageSize)
        let renderer = UIGraphicsPDFRenderer(bounds: bounds)
        return renderer.pdfData { context in
            context.beginPage()
            drawPass(in: context.cgContext, ticket: ticket, holderName: holderName)
        }
    }

    static func presentPrinter(ticket: Ticket, holderName: String) {
        let data = pdfData(ticket: ticket, holderName: holderName)
        let info = UIPrintInfo.printInfo()
        info.jobName = "Crested Pass — \(ticket.matchLabel)"
        info.outputType = .general
        info.orientation = .portrait
        let controller = UIPrintInteractionController.shared
        controller.printInfo = info
        controller.printingItem = data
        controller.present(animated: true)
    }

    private static func drawPass(in ctx: CGContext, ticket: Ticket, holderName: String) {
        let navy = UIColor(red: 19 / 255, green: 41 / 255, blue: 61 / 255, alpha: 1)
        let gold = UIColor(red: 201 / 255, green: 162 / 255, blue: 39 / 255, alpha: 1)
        let cream = UIColor(red: 250 / 255, green: 247 / 255, blue: 242 / 255, alpha: 1)
        let border = UIColor(red: 231 / 255, green: 226 / 255, blue: 216 / 255, alpha: 1)
        let muted = UIColor(red: 138 / 255, green: 127 / 255, blue: 110 / 255, alpha: 1)

        cream.setFill()
        ctx.fill(CGRect(origin: .zero, size: pageSize))
        navy.setFill()
        ctx.fill(CGRect(x: 0, y: 0, width: pageSize.width, height: 100))

        drawText("CRESTED PASS", at: CGPoint(x: 28, y: 28), font: sans(11, .semibold), color: gold, tracking: 1.4)
        drawText("AFCON 2027 MATCH TICKET", at: CGPoint(x: 28, y: 52), font: serif(18, .semibold), color: .white)

        drawText("MATCH", at: CGPoint(x: 28, y: 122), font: sans(11, .semibold), color: gold, tracking: 1.4)
        drawText(ticket.matchLabel, at: CGPoint(x: 28, y: 142), font: serif(26, .semibold), color: navy)
        drawText(ticket.venue, at: CGPoint(x: 28, y: 176), font: sans(13, .regular), color: gold)

        let boxes = [pair(ticket.gate), pair(ticket.block), pair(ticket.seat)]
        let boxY: CGFloat = 206
        let boxW: CGFloat = 114
        let boxH: CGFloat = 62
        boxes.enumerated().forEach { index, item in
            let x: CGFloat = 28 + CGFloat(index) * (boxW + 10)
            let rect = CGRect(x: x, y: boxY, width: boxW, height: boxH)
            UIColor.white.setFill()
            let path = UIBezierPath(roundedRect: rect, cornerRadius: 6)
            path.fill()
            border.setStroke()
            path.lineWidth = 1
            path.stroke()
            drawText(item.0, at: CGPoint(x: x + 12, y: boxY + 12), font: sans(10, .semibold), color: gold, tracking: 1.2)
            drawText(item.1, at: CGPoint(x: x + 12, y: boxY + 30), font: serif(20, .semibold), color: navy)
        }

        if let qr = QRCodeView.makeImage(from: ticket.qrToken) {
            let qrSize: CGFloat = 168
            let qrX = (pageSize.width - qrSize) / 2
            qr.draw(in: CGRect(x: qrX, y: 288, width: qrSize, height: qrSize))
        }
        drawCentered(ticket.qrToken, y: 466, font: sans(10, .regular), color: muted)

        drawText("Issued to \(holderName)", at: CGPoint(x: 28, y: 500), font: sans(13, .regular), color: navy)
        drawText(ticket.kickoffNote, at: CGPoint(x: 28, y: 520), font: sans(12, .regular), color: muted)

        border.setStroke()
        ctx.setLineWidth(1)
        ctx.move(to: CGPoint(x: 28, y: 546))
        ctx.addLine(to: CGPoint(x: 392, y: 546))
        ctx.strokePath()
        drawText("Licensed to Uganda Tourism Board", at: CGPoint(x: 28, y: 556), font: sans(10, .regular), color: muted)
        drawText("Timwork Sports  ·  Crested Pass", at: CGPoint(x: 28, y: 572), font: sans(10, .regular), color: muted)
    }

    private static func sans(_ size: CGFloat, _ weight: UIFont.Weight) -> UIFont {
        .systemFont(ofSize: size, weight: weight)
    }

    private static func serif(_ size: CGFloat, _ weight: UIFont.Weight) -> UIFont {
        if let descriptor = UIFont.systemFont(ofSize: size, weight: weight)
            .fontDescriptor.withDesign(.serif) {
            return UIFont(descriptor: descriptor, size: size)
        }
        return .systemFont(ofSize: size, weight: weight)
    }

    private static func drawText(
        _ string: String,
        at point: CGPoint,
        font: UIFont,
        color: UIColor,
        tracking: CGFloat = 0
    ) {
        var attributes: [NSAttributedString.Key: Any] = [
            .font: font,
            .foregroundColor: color
        ]
        if tracking != 0 {
            attributes[.kern] = tracking
        }
        (string as NSString).draw(at: point, withAttributes: attributes)
    }

    private static func drawCentered(_ string: String, y: CGFloat, font: UIFont, color: UIColor) {
        let attributes: [NSAttributedString.Key: Any] = [
            .font: font,
            .foregroundColor: color
        ]
        let size = (string as NSString).size(withAttributes: attributes)
        let x = (pageSize.width - size.width) / 2
        (string as NSString).draw(at: CGPoint(x: x, y: y), withAttributes: attributes)
    }
}
