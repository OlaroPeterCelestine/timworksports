import SwiftUI

struct ARLensView: View {
    var body: some View {
        ZStack {
            LinearGradient(
                colors: [Color.brandNavy.opacity(0.92), Color.brandNavy],
                startPoint: .top,
                endPoint: .bottom
            )
            .ignoresSafeArea()
            VStack(spacing: 12) {
                GoldLabel(text: "AR lens")
                SerifTitle(text: "Coming soon", size: 32, color: .white)
                CaptionText(
                    text: "Camera preview is a placeholder in v1. No AR session is started.",
                    color: .brandGold
                )
                .multilineTextAlignment(.center)
                Text("Scan · Crested Pass")
                    .font(.brandSans(13))
                    .foregroundStyle(.white.opacity(0.7))
                    .padding(.top, 12)
            }
            .padding(32)
        }
        .navigationTitle("AR lens")
        .navigationBarTitleDisplayMode(.inline)
    }
}
