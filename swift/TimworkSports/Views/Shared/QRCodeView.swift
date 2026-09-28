import CoreImage.CIFilterBuiltins
import SwiftUI
import UIKit

struct QRCodeView: View {
    let payload: String

    var body: some View {
        Group {
            if let image = Self.makeImage(from: payload) {
                Image(uiImage: image)
                    .interpolation(.none)
                    .resizable()
                    .scaledToFit()
                    .accessibilityLabel("QR code")
            } else {
                CaptionText(text: "Could not generate code")
            }
        }
    }

    static func makeImage(from string: String) -> UIImage? {
        let filter = CIFilter.qrCodeGenerator()
        filter.message = Data(string.utf8)
        filter.correctionLevel = "M"
        guard let output = filter.outputImage else { return nil }
        let scaled = output.transformed(by: CGAffineTransform(scaleX: 12, y: 12))
        let color = CIFilter.falseColor()
        color.inputImage = scaled
        color.color0 = CIColor(red: 19 / 255, green: 41 / 255, blue: 61 / 255)
        color.color1 = CIColor(red: 1, green: 1, blue: 1)
        let context = CIContext()
        guard let ciImage = color.outputImage,
              let cgImage = context.createCGImage(ciImage, from: ciImage.extent) else { return nil }
        return UIImage(cgImage: cgImage)
    }
}
