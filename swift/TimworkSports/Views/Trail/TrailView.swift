import SwiftUI

struct TrailView: View {
    @StateObject private var vm: TrailViewModel

    init(repository: CrestedPassRepository) {
        _vm = StateObject(wrappedValue: TrailViewModel(repository: repository))
    }

    private var rewardCopy: String {
        let collected = vm.snapshot.trail.collected
        if collected >= 6 { return "Trail complete · signed Cranes scarf unlocked" }
        if collected >= 5 { return "Hospitality voucher unlocked · scarf at 6 stamps" }
        return "Next reward at 5 stamps · match-day hospitality voucher"
    }

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 16) {
                ScreenHeader(title: "Trail", subtitle: "Collect stamps at UTB partners")
                BrandCard {
                    TrailBar(collected: vm.snapshot.trail.collected, total: vm.snapshot.trail.total)
                }
                BrandCard(background: .brandNavy) {
                    GoldLabel(text: "Reward unlock")
                    Spacer().frame(height: 8)
                    SerifTitle(text: rewardCopy, size: 20, color: .white)
                    CaptionText(text: "Licensed with Uganda Tourism Board", color: .brandGold)
                }
                GoldLabel(text: "Stamp path")
                ForEach(Array(vm.snapshot.stamps.enumerated()), id: \.element.id) { index, stamp in
                    HStack(alignment: .top, spacing: 12) {
                        VStack(spacing: 0) {
                            StampNode(state: stamp.state)
                            if index != vm.snapshot.stamps.count - 1 {
                                Rectangle()
                                    .fill(lineColor(stamp.state))
                                    .frame(width: 2, height: 28)
                            }
                        }
                        BrandCard {
                            GoldLabel(text: stampLabel(stamp.state))
                            Text(stamp.name)
                                .font(.brandSans(16))
                                .foregroundStyle(Color.brandInk)
                                .padding(.top, 4)
                            CaptionText(text: stamp.location)
                            if stamp.state == .nextAvailable {
                                PrimaryButton(title: "Collect stamp (demo)", gold: true, action: vm.collectNext)
                                    .padding(.top, 10)
                            }
                        }
                    }
                }
                GoldLabel(text: "Partner directory")
                ForEach(vm.snapshot.partners) { place in
                    BrandCard {
                        Text(place.name)
                            .font(.brandSans(16))
                            .foregroundStyle(Color.brandInk)
                        CaptionText(text: "\(place.area) · \(place.detail)")
                    }
                }
            }
            .padding(.horizontal, 20)
            .padding(.vertical, 16)
        }
        .background(Color.brandCream.ignoresSafeArea())
        .toolbar(.hidden, for: .navigationBar)
        .navigationBarTitleDisplayMode(.inline)
    }

    private func stampLabel(_ state: StampState) -> String {
        switch state {
        case .completed: return "Collected"
        case .nextAvailable: return "Next available"
        case .locked: return "Locked"
        }
    }

    private func lineColor(_ state: StampState) -> Color {
        switch state {
        case .completed: return .stampGreen
        case .nextAvailable: return .brandGold
        case .locked: return .lockedGray
        }
    }
}

private struct StampNode: View {
    let state: StampState

    var body: some View {
        ZStack {
            Circle()
                .fill(fill)
                .frame(width: 28, height: 28)
            Circle()
                .stroke(border, lineWidth: 2)
                .frame(width: 28, height: 28)
            switch state {
            case .completed:
                Image(systemName: "checkmark")
                    .font(.system(size: 11, weight: .bold))
                    .foregroundStyle(.white)
            case .nextAvailable:
                Circle().fill(Color.brandGold).frame(width: 8, height: 8)
            case .locked:
                Image(systemName: "lock.fill")
                    .font(.system(size: 10))
                    .foregroundStyle(Color.brandMuted)
            }
        }
    }

    private var border: Color {
        switch state {
        case .completed: return .stampGreen
        case .nextAvailable: return .brandGold
        case .locked: return .lockedGray
        }
    }

    private var fill: Color {
        switch state {
        case .completed: return .stampGreen
        case .nextAvailable: return .white
        case .locked: return .brandCream
        }
    }
}
