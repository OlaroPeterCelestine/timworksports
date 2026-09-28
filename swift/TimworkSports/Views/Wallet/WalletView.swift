import SwiftUI

struct WalletView: View {
    @StateObject private var vm: WalletViewModel
    private let amounts = [10_000, 20_000, 50_000, 100_000]
    var hidesNavigationBar: Bool = true

    init(repository: CrestedPassRepository, hidesNavigationBar: Bool = true) {
        self.hidesNavigationBar = hidesNavigationBar
        _vm = StateObject(wrappedValue: WalletViewModel(repository: repository))
    }

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 16) {
                ScreenHeader(title: "Wallet", subtitle: "Cashless fan spend · mock payments only")
                GoldLabel(text: "Balance")
                SerifTitle(text: vm.snapshot.user.walletBalance.ugx, size: 36)
                CaptionText(text: "Held for \(vm.snapshot.user.name)")
                PrimaryButton(title: "Top up", gold: true, action: vm.openTopUp)
                if let amount = vm.confirmedAmount {
                    BrandCard {
                        Text("Simulated top-up of \(amount.ugx) complete.")
                            .font(.brandSans(14))
                            .foregroundStyle(Color.stampGreen)
                    }
                }
                BrandCard(padding: 20) {
                    GoldLabel(text: "Tap to pay")
                    QRCodeView(payload: vm.snapshot.payToken)
                        .frame(width: 220, height: 220)
                        .frame(maxWidth: .infinity)
                        .padding(.vertical, 12)
                    CaptionText(text: "Show this code at a Crested Pass stall. Token \(vm.snapshot.payToken).")
                        .frame(maxWidth: .infinity)
                        .multilineTextAlignment(.center)
                }
                GoldLabel(text: "Activity")
                    .padding(.top, 8)
                ForEach(vm.snapshot.transactions) { txn in
                    BrandCard {
                        HStack {
                            VStack(alignment: .leading, spacing: 2) {
                                Text(txn.vendor)
                                    .font(.brandSans(15))
                                    .foregroundStyle(Color.brandInk)
                                CaptionText(text: txn.amount < 0 ? "Spend" : "Top-up")
                            }
                            Spacer()
                            Text(txn.amount.ugx)
                                .font(.brandSans(15))
                                .foregroundStyle(txn.amount < 0 ? Color.brandNavy : Color.stampGreen)
                        }
                    }
                }
            }
            .padding(.horizontal, 20)
            .padding(.vertical, 16)
        }
        .background(Color.brandCream.ignoresSafeArea())
        .toolbar(hidesNavigationBar ? .hidden : .automatic, for: .navigationBar)
        .navigationTitle("Wallet")
        .navigationBarTitleDisplayMode(.inline)
        .sheet(isPresented: $vm.showTopUp) {
            VStack(alignment: .leading, spacing: 12) {
                SerifTitle(text: "Top up wallet", size: 22)
                CaptionText(text: "No payment SDK in this prototype — amounts are simulated.")
                ForEach(amounts, id: \.self) { amount in
                    PrimaryButton(title: amount.ugx) {
                        vm.topUp(amount)
                    }
                }
                Spacer()
            }
            .padding(20)
            .presentationDetents([.medium])
        }
    }
}
