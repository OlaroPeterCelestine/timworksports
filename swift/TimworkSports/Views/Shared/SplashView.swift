import SwiftUI

struct SplashView: View {
    var onFinished: () -> Void
    @State private var visible = false

    var body: some View {
        VStack(spacing: 12) {
            Text("Timwork Sports")
                .font(.system(size: 32, weight: .semibold))
                .foregroundStyle(Color.brandGold)
            Text("Coaching, events, athletes")
                .font(.system(size: 16))
                .foregroundStyle(Color.brandMuted)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .background(Color.brandCanvas.ignoresSafeArea())
        .opacity(visible ? 1 : 0)
        .onAppear {
            withAnimation(.easeOut(duration: 0.35)) { visible = true }
            DispatchQueue.main.asyncAfter(deadline: .now() + 1.4) {
                onFinished()
            }
        }
    }
}
