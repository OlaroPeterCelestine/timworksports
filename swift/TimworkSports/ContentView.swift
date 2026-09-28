import SwiftUI
import UIKit

enum AppTab: Hashable {
    case home, discover, wallet, trail, you
}

struct ContentView: View {
    @EnvironmentObject private var repository: MockRepository
    @EnvironmentObject private var appearance: AppearanceStore
    @State private var tab: AppTab = .home
    @State private var showSplash = true
    @State private var signedIn = false
    @State private var authSession = 0

    var body: some View {
        ZStack {
            if signedIn {
            TabView(selection: $tab) {
                NavigationStack {
                    HomeView(repository: repository, onSelectTab: { tab = $0 })
                }
                .tabItem { Label("Home", systemImage: "house") }
                .tag(AppTab.home)

                NavigationStack {
                    DiscoverView(repository: repository)
                }
                .tabItem { Label("Discover", systemImage: "safari") }
                .tag(AppTab.discover)

                NavigationStack {
                    WalletView(repository: repository)
                }
                .tabItem { Label("Wallet", systemImage: "creditcard") }
                .tag(AppTab.wallet)

                NavigationStack {
                    TrailView(repository: repository)
                }
                .tabItem { Label("Trail", systemImage: "map") }
                .tag(AppTab.trail)

                NavigationStack {
                    ProfileView(repository: repository, onSelectTab: { tab = $0 })
                }
                .tabItem { Label("You", systemImage: "person.crop.circle") }
                .tag(AppTab.you)
            }
            .tint(Color.brandGold)
            .onAppear(perform: styleTabBar)
            .environment(\.signOut) {
                tab = .home
                signedIn = false
                authSession += 1
            }
            } else if !showSplash {
                AuthView { signedIn = true }
                    .id(authSession)
            }

            if showSplash {
                SplashView {
                    withAnimation(.easeOut(duration: 0.3)) {
                        showSplash = false
                    }
                }
                .transition(.opacity)
                .zIndex(1)
            }
        }
    }

    private func styleTabBar() {
        let appearance = UITabBarAppearance()
        appearance.configureWithOpaqueBackground()
        appearance.backgroundColor = UIColor(red: 5 / 255, green: 6 / 255, blue: 5 / 255, alpha: 1)
        UITabBar.appearance().standardAppearance = appearance
        UITabBar.appearance().scrollEdgeAppearance = appearance
    }
}

private struct SignOutActionKey: EnvironmentKey {
    static let defaultValue: () -> Void = {}
}

extension EnvironmentValues {
    var signOut: () -> Void {
        get { self[SignOutActionKey.self] }
        set { self[SignOutActionKey.self] = newValue }
    }
}

#Preview {
    ContentView()
        .environmentObject(MockRepository())
        .environmentObject(AppearanceStore())
}
