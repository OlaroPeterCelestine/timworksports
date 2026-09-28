import SwiftUI

struct SettingsView: View {
    @StateObject private var vm: ProfileViewModel
    @EnvironmentObject private var appearance: AppearanceStore
    private let repository: CrestedPassRepository
    @AppStorage("crested_notify_match") private var notifyMatch = true
    @AppStorage("crested_notify_trail") private var notifyTrail = true
    @AppStorage("crested_notify_wallet") private var notifyWallet = true
    @Environment(\.signOut) private var signOut

    init(repository: CrestedPassRepository) {
        self.repository = repository
        _vm = StateObject(wrappedValue: ProfileViewModel(repository: repository))
    }

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 18) {
                account
                payments
                appearanceSection
                notifications
                safety
                about
                signOut
            }
            .padding(.horizontal, 20)
            .padding(.vertical, 16)
        }
        .background(Color.brandCanvas.ignoresSafeArea())
        .navigationTitle("Settings")
        .navigationBarTitleDisplayMode(.inline)
    }

    private var account: some View {
        group("Account") {
            SettingsRow(
                systemImage: "person",
                title: vm.snapshot.user.name,
                value: vm.snapshot.user.handle,
                showChevron: false
            )
            Divider().overlay(Color.brandBorder)
            Button(action: vm.toggleLanguage) {
                SettingsRow(
                    systemImage: "globe",
                    title: "Language",
                    value: vm.snapshot.language == "EN" ? "English" : "Luganda",
                    showChevron: false
                )
            }
            .buttonStyle(.plain)
        }
    }

    private var payments: some View {
        group("Payments & security") {
            NavigationLink {
                WalletView(repository: repository, hidesNavigationBar: false)
            } label: {
                SettingsRow(
                    systemImage: "creditcard",
                    title: "Wallet",
                    value: vm.snapshot.user.walletBalance.ugx
                )
            }
            .buttonStyle(.plain)
            Divider().overlay(Color.brandBorder)
            NavigationLink {
                BandView(repository: repository)
            } label: {
                SettingsRow(
                    systemImage: "wave.3.right",
                    title: "Crested Band",
                    value: vm.snapshot.crestedBand.status.capitalized
                )
            }
            .buttonStyle(.plain)
            Divider().overlay(Color.brandBorder)
            Button(action: vm.freezeBand) {
                SettingsRow(
                    systemImage: "snowflake",
                    title: vm.snapshot.crestedBand.status == "frozen" ? "Unfreeze band" : "Freeze lost band",
                    showChevron: false
                )
            }
            .buttonStyle(.plain)
        }
    }

    private var appearanceSection: some View {
        VStack(alignment: .leading, spacing: 10) {
            GoldLabel(text: "Appearance")
            BrandCard {
                CaptionText(text: "Follows system unless you lock a look.")
                HStack(spacing: 8) {
                    ForEach(["system", "light", "dark"], id: \.self) { mode in
                        let selected = appearance.scheme == mode
                        Button {
                            appearance.scheme = mode
                        } label: {
                            Text(mode.capitalized)
                                .font(.brandSans(13, weight: .semibold))
                                .foregroundStyle(selected ? Color.black : Color.brandInk)
                                .frame(maxWidth: .infinity)
                                .padding(.vertical, 10)
                                .background(selected ? Color.brandGold : Color.brandCanvas)
                                .clipShape(RoundedRectangle(cornerRadius: 8, style: .continuous))
                                .overlay(
                                    RoundedRectangle(cornerRadius: 8, style: .continuous)
                                        .stroke(selected ? Color.clear : Color.brandBorder, lineWidth: 1)
                                )
                        }
                        .buttonStyle(.plain)
                    }
                }
                .padding(.top, 12)
            }
        }
    }

    private var notifications: some View {
        group("Notifications") {
            SettingsToggleRow(systemImage: "sportscourt", title: "Match alerts", isOn: $notifyMatch)
            Divider().overlay(Color.brandBorder)
            SettingsToggleRow(systemImage: "map", title: "Trail stamps", isOn: $notifyTrail)
            Divider().overlay(Color.brandBorder)
            SettingsToggleRow(systemImage: "creditcard", title: "Wallet activity", isOn: $notifyWallet)
        }
    }

    private var safety: some View {
        group("Safety & support") {
            if let alert = vm.snapshot.safetyAlert, !vm.snapshot.safetyAlertDismissed {
                CaptionText(text: alert)
                    .padding(.bottom, 6)
            }
            NavigationLink {
                EmbassyView(repository: repository)
            } label: {
                SettingsRow(systemImage: "building.2", title: "Fan Embassy")
            }
            .buttonStyle(.plain)
        }
    }

    private var about: some View {
        group("About") {
            SettingsRow(
                systemImage: "info.circle",
                title: "Timwork Sports",
                value: "AFCON 2027",
                showChevron: false
            )
            CaptionText(text: "Licensed to Uganda Tourism Board. Ticketing, wallet, trail, and predictor are mocked in this pitch build.")
                .padding(.top, 8)
        }
    }

    private var signOut: some View {
        VStack(alignment: .leading, spacing: 8) {
            Button(action: signOut) {
                BrandCard {
                    SettingsRow(
                        systemImage: "rectangle.portrait.and.arrow.right",
                        title: "Sign out",
                        destructive: true,
                        showChevron: false
                    )
                }
            }
            .buttonStyle(.plain)
        }
    }

    private func group<Content: View>(_ title: String, @ViewBuilder content: @escaping () -> Content) -> some View {
        VStack(alignment: .leading, spacing: 10) {
            GoldLabel(text: title)
            BrandCard {
                content()
            }
        }
    }
}
