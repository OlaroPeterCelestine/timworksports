import SwiftUI
import UIKit

extension Color {
    static let brandNavy = Color.white
    static let brandGold = Color(red: 31 / 255, green: 138 / 255, blue: 76 / 255)
    static let brandRed = Color(red: 200 / 255, green: 16 / 255, blue: 46 / 255)
    static let stampGreen = Color(red: 31 / 255, green: 138 / 255, blue: 76 / 255)
    static let lockedGray = Color(white: 0.54)

    static let brandCanvas = Color(uiColor: UIColor { trait in
        trait.userInterfaceStyle == .dark ? .black : .white
    })
    static let brandCard = Color(uiColor: UIColor { trait in
        trait.userInterfaceStyle == .dark
            ? UIColor(white: 0.08, alpha: 1)
            : UIColor(white: 0.96, alpha: 1)
    })
    static let brandInk = Color(uiColor: UIColor { trait in
        trait.userInterfaceStyle == .dark ? .white : UIColor(white: 0.07, alpha: 1)
    })
    static let brandBorder = Color(uiColor: UIColor { trait in
        trait.userInterfaceStyle == .dark
            ? UIColor(white: 0.16, alpha: 1)
            : UIColor(white: 0.89, alpha: 1)
    })
    static let brandMuted = Color(uiColor: UIColor { trait in
        trait.userInterfaceStyle == .dark
            ? UIColor(white: 0.62, alpha: 1)
            : UIColor(white: 0.40, alpha: 1)
    })

    static let brandCream = brandCanvas

    static func avatar(_ key: String) -> Color {
        avatar(for: key)
    }

    static func avatar(for key: String) -> Color {
        let palette: [Color] = [
            .brandNavy,
            .brandGold,
            .brandRed,
            .stampGreen,
            Color(red: 61 / 255, green: 90 / 255, blue: 128 / 255),
            Color(red: 107 / 255, green: 63 / 255, blue: 160 / 255),
            Color(red: 139 / 255, green: 94 / 255, blue: 60 / 255)
        ]
        var hash = 0
        for scalar in key.unicodeScalars {
            hash = 31 &* hash &+ Int(scalar.value)
        }
        return palette[abs(hash) % palette.count]
    }
}

final class AppearanceStore: ObservableObject {
    @AppStorage("crested_pass_scheme") var scheme: String = "system" {
        willSet { objectWillChange.send() }
    }

    var preferred: ColorScheme? {
        switch scheme {
        case "dark": return .dark
        case "light": return .light
        default: return nil
        }
    }

    var icon: String {
        switch scheme {
        case "dark": return "moon.fill"
        case "light": return "sun.max.fill"
        default: return "circle.lefthalf.filled"
        }
    }

    var label: String {
        switch scheme {
        case "dark": return "Dark"
        case "light": return "Light"
        default: return "System"
        }
    }

    func cycle() {
        scheme = scheme == "system" ? "light" : scheme == "light" ? "dark" : "system"
    }
}
