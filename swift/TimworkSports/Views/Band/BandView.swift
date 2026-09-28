import SwiftUI

struct BandView: View {
    @StateObject private var vm: BandViewModel

    init(repository: CrestedPassRepository) {
        _vm = StateObject(wrappedValue: BandViewModel(repository: repository))
    }

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 16) {
                ScreenHeader(title: "Crested Band", subtitle: "Tap to enter, pay, and stamp")

                BrandCard(background: .brandNavy, padding: 20) {
                    GoldLabel(text: vm.snapshot.crestedBand.paired ? "Paired" : "Unpaired")
                    Spacer().frame(height: 10)
                    SerifTitle(text: vm.snapshot.crestedBand.bandId, size: 28, color: .white)
                    CaptionText(text: "Status · \(vm.snapshot.crestedBand.status.capitalized)", color: .brandGold)
                        .padding(.top, 8)
                    CaptionText(
                        text: "The band holds an ID, not cash. Lost bands are frozen and reissued against the same wallet.",
                        color: .white.opacity(0.75)
                    )
                    .padding(.top, 10)
                }

                BrandCard {
                    GoldLabel(text: "Loyalty")
                    Spacer().frame(height: 8)
                    SerifTitle(text: vm.snapshot.loyalty.tier, size: 26)
                    CaptionText(text: vm.snapshot.loyalty.stampsToNextTier == 0
                        ? "Top tier unlocked"
                        : "\(vm.snapshot.loyalty.stampsToNextTier) stamps to the next tier")
                    CaptionText(text: "Priority lane · embassy fast-track · bonus predictor points")
                        .padding(.top, 6)
                }

                if let lastTap = vm.lastTap {
                    BrandCard {
                        GoldLabel(text: "Last tap")
                        Spacer().frame(height: 6)
                        Text(lastTap)
                            .font(.brandSans(15))
                            .foregroundStyle(Color.brandInk)
                    }
                }

                PrimaryButton(title: "Simulate tap", gold: true, action: vm.simulateTap)
                CaptionText(text: "Emulator-safe. On a device this same path reads the NFC UID.")
                PrimaryButton(
                    title: vm.snapshot.crestedBand.status == "frozen" ? "Unfreeze band" : "Freeze lost band",
                    action: vm.freeze
                )
                PrimaryButton(title: "Reissue band", action: vm.reissue)
            }
            .padding(20)
        }
        .background(Color.brandCream.ignoresSafeArea())
        .navigationTitle("Crested Band")
        .navigationBarTitleDisplayMode(.inline)
    }
}
