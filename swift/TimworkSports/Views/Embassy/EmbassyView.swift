import SwiftUI

struct EmbassyView: View {
    @StateObject private var vm: EmbassyViewModel

    init(repository: CrestedPassRepository) {
        _vm = StateObject(wrappedValue: EmbassyViewModel(repository: repository))
    }

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 16) {
                ScreenHeader(title: "Fan Embassy", subtitle: "Desks for visiting supporters")
                ForEach(vm.snapshot.embassies) { place in
                    BrandCard {
                        GoldLabel(text: "Location")
                        Text(place.name)
                            .font(.brandSans(17))
                            .foregroundStyle(Color.brandInk)
                            .padding(.top, 4)
                        CaptionText(text: "\(place.area) · \(place.detail)")
                    }
                }
                GoldLabel(text: "Squad")
                    .padding(.top, 4)
                CaptionText(text: "Linked friends for this match. Statuses are mocked — no live location.")
                ForEach(vm.snapshot.squad) { member in
                    BrandCard {
                        HStack {
                            VStack(alignment: .leading, spacing: 4) {
                                Text(member.name)
                                    .font(.brandSans(16))
                                    .foregroundStyle(Color.brandInk)
                                HStack(spacing: 6) {
                                    Circle()
                                        .fill(statusColor(member.status))
                                        .frame(width: 8, height: 8)
                                    CaptionText(text: member.status.label)
                                }
                                if let amount = member.pendingAmount {
                                    CaptionText(text: "Owes \(amount.ugx)")
                                }
                            }
                            Spacer()
                            if member.status == .paymentPending {
                                PillButton(title: "Settle", gold: true) {
                                    vm.settle(memberId: member.id)
                                }
                            }
                        }
                    }
                }
            }
            .padding(.horizontal, 20)
            .padding(.vertical, 16)
        }
        .background(Color.brandCream.ignoresSafeArea())
        .navigationTitle("Embassy / Squad")
        .navigationBarTitleDisplayMode(.inline)
    }

    private func statusColor(_ status: SquadStatus) -> Color {
        switch status {
        case .atVenue: return .stampGreen
        case .enRoute: return .brandGold
        case .paymentPending: return .lockedGray
        }
    }
}
