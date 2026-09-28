import SwiftUI

@main
struct TimworkSportsApp: App {
    @StateObject private var repository = MockRepository()
    @StateObject private var appearance = AppearanceStore()

    var body: some Scene {
        WindowGroup {
            ContentView()
                .environmentObject(repository)
                .environmentObject(appearance)
                .preferredColorScheme(appearance.preferred)
        }
    }
}
