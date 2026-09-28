import Foundation
import SwiftUI

struct User: Codable, Hashable {
    var name: String
    var walletBalance: Int

    var initials: String {
        name.split(separator: " ").compactMap { $0.first.map(String.init) }.prefix(2).joined()
    }

    var handle: String {
        let parts = name.replacingOccurrences(of: ".", with: "")
            .split(separator: " ")
            .map { $0.lowercased() }
        return "@" + parts.joined(separator: ".")
    }
}

struct NextMatch: Codable, Hashable {
    var home: String
    var away: String
    var venue: String
    var kickoffInHours: Int
}

struct TrailProgress: Codable, Hashable {
    var collected: Int
    var total: Int
}

struct Transaction: Codable, Hashable, Identifiable {
    var id: String
    var vendor: String
    var amount: Int
}

struct LeaderboardEntry: Codable, Hashable, Identifiable {
    var id: String { name }
    var name: String
    var points: Int
}

enum StampState: String, Codable {
    case completed
    case nextAvailable
    case locked
}

struct TrailStamp: Codable, Hashable, Identifiable {
    var id: String
    var name: String
    var location: String
    var state: StampState
}

enum PlaceKind: String, Codable {
    case embassy
    case partner

    var label: String {
        switch self {
        case .embassy: return "Fan embassy"
        case .partner: return "Trail partner"
        }
    }
}

struct Place: Codable, Hashable, Identifiable {
    var id: String
    var name: String
    var kind: PlaceKind
    var area: String
    var detail: String
}

enum SquadStatus: String, Codable {
    case atVenue
    case enRoute
    case paymentPending

    var label: String {
        switch self {
        case .atVenue: return "At venue"
        case .enRoute: return "En route"
        case .paymentPending: return "Payment pending"
        }
    }
}

struct SquadMember: Codable, Hashable, Identifiable {
    var id: String
    var name: String
    var status: SquadStatus
    var pendingAmount: Int?
}

struct Ticket: Codable, Hashable, Identifiable {
    var id: String
    var matchLabel: String
    var venue: String
    var gate: String
    var block: String
    var seat: String
    var kickoffNote: String
    var qrToken: String
    var eventId: String = ""
}

struct Prediction: Codable, Hashable {
    var homeScore: Int?
    var awayScore: Int?
}

struct ReelClip: Codable, Hashable, Identifiable {
    var id: String
    var type: String
    var title: String
    var videoUrl: String
    var imageName: String
    var destinationId: String? = nil
    var liked: Bool = false
    var authorName: String? = nil


    var author: String {
        if let authorName { return authorName }
        switch type {
        case "travel": return "UTB Trails"
        case "fan": return "Fan Village"
        default: return "Cranes TV"
        }
    }

    var handle: String {
        "@" + author.lowercased()
            .replacingOccurrences(of: " ", with: "")
            .replacingOccurrences(of: ".", with: "")
    }

    var initials: String {
        author.split(separator: " ").compactMap { $0.first.map(String.init) }.prefix(2).joined().uppercased()
    }
}

struct Shot: Codable, Hashable, Identifiable {
    var id: String
    var author: String
    var handle: String
    var caption: String
    var imageName: String
    var likes: Int
    var liked: Bool = false

    var initials: String {
        author.split(separator: " ").compactMap { $0.first.map(String.init) }.prefix(2).joined().uppercased()
    }
}

struct HighlightClip: Codable, Hashable, Identifiable {
    var id: String
    var match: String
    var title: String
    var videoUrl: String
    var sport: String = "Football"

    var imageName: String {
        switch sport.lowercased() {
        case "basketball": return "event_basketball"
        case "tennis": return "event_tennis"
        case "boxing": return "event_boxing"
        case "swimming": return "event_swim"
        case "athletics", "track": return "event_track"
        default: return id == "h2" ? "highlight_ceremony" : "highlight_winner"
        }
    }
}

struct SportProfile: Codable, Hashable, Identifiable {
    var id: String
    var name: String
    var federation: String
    var city: String
    var blurb: String
    var athletes: Int

    var imageName: String {
        switch name.lowercased() {
        case "basketball": return "event_basketball"
        case "tennis": return "event_tennis"
        case "boxing": return "event_boxing"
        case "swimming": return "event_swim"
        case "athletics": return "event_track"
        default: return "event_soccer"
        }
    }
}

struct Coach: Codable, Hashable, Identifiable {
    var id: String
    var name: String
    var sport: String
    var role: String
    var bio: String
    var years: Int
    var imageName: String
    var rating: Double
    var wins: Int
    var draws: Int
    var losses: Int
    var licence: String
    var clubs: String

    var record: String {
        draws == 0 ? "\(wins)–\(losses)" : "\(wins)–\(draws)–\(losses)"
    }

    var initials: String {
        name.split(separator: " ").compactMap { $0.first.map(String.init) }.prefix(2).joined().uppercased()
    }
}

struct PromoAd: Codable, Hashable, Identifiable {
    var id: String
    var sponsor: String
    var headline: String
    var detail: String
    var imageName: String
}

struct Player: Codable, Hashable, Identifiable {
    var id: String
    var name: String
    var sport: String
    var position: String
    var club: String
    var city: String
    var nationality: String
    var age: Int
    var number: Int
    var heightCm: Int
    var appearances: Int
    var scoring: Int
    var scoringLabel: String
    var bio: String

    var initials: String {
        name.split(separator: " ").compactMap { $0.first.map(String.init) }.prefix(2).joined().uppercased()
    }

    var imageName: String {
        switch sport.lowercased() {
        case "basketball": return "event_basketball"
        case "tennis": return "event_tennis"
        case "boxing": return "event_boxing"
        case "swimming": return "event_swim"
        case "athletics": return "event_track"
        default: return "event_soccer"
        }
    }
}

struct Destination: Codable, Hashable, Identifiable {
    var id: String
    var name: String
    var category: String
    var trailEligible: Bool
    var blurb: String
    var imageName: String {
        switch id {
        case "d1": return "dest_bwindi"
        case "d2": return "dest_nile"
        case "d3": return "dest_nakasero"
        default: return "dest_victoria"
        }
    }
}

struct FestivalEvent: Codable, Hashable, Identifiable {
    var id: String
    var title: String
    var sport: String
    var date: String
    var time: String
    var city: String
    var venue: String
    var price: Int
    var kickoffInHours: Int

    var type: String { sport }

    var imageName: String {
        if id == "ev-foot2" { return "event_kenya_match" }
        switch sport.lowercased() {
        case "basketball": return "event_basketball"
        case "tennis": return "event_tennis"
        case "boxing": return "event_boxing"
        case "swimming": return "event_swim"
        case "athletics", "track": return "event_track"
        case "football", "soccer": return "event_soccer"
        default: return "hero_venue"
        }
    }

    var symbol: String {
        switch sport.lowercased() {
        case "basketball": return "basketball.fill"
        case "tennis": return "sportscourt"
        case "boxing": return "flame.fill"
        case "swimming": return "drop.fill"
        case "athletics", "track": return "figure.run"
        case "football", "soccer": return "soccerball"
        default: return "sportscourt"
        }
    }

    var whenLabel: String { "\(date) · \(time)" }

    var coverColors: [Color] {
        switch sport.lowercased() {
        case "basketball": return [Color(red: 0.72, green: 0.32, blue: 0.08), Color(red: 0.12, green: 0.08, blue: 0.06)]
        case "tennis": return [Color(red: 0.18, green: 0.48, blue: 0.28), Color(red: 0.06, green: 0.16, blue: 0.12)]
        case "boxing": return [Color(red: 0.55, green: 0.08, blue: 0.12), Color(red: 0.10, green: 0.05, blue: 0.06)]
        case "swimming": return [Color(red: 0.08, green: 0.38, blue: 0.58), Color(red: 0.04, green: 0.12, blue: 0.22)]
        case "athletics", "track": return [Color(red: 0.72, green: 0.52, blue: 0.12), Color(red: 0.16, green: 0.10, blue: 0.04)]
        default: return [Color.brandNavy, Color.black]
        }
    }

    func makeTicket() -> Ticket {
        let seed = abs(id.hashValue)
        let gates = ["Gate 2", "Gate 3", "Gate 4", "Gate 5"]
        let blocks = ["Block A", "Block B", "Block C", "Block D"]
        return Ticket(
            id: "t-\(id)",
            matchLabel: title,
            venue: venue,
            gate: gates[seed % gates.count],
            block: blocks[(seed / 3) % blocks.count],
            seat: "Seat \(1 + seed % 28)",
            kickoffNote: "Starts in \(kickoffInHours.countdown)",
            qrToken: "CP-TICKET-\(id.uppercased())",
            eventId: id
        )
    }
}

struct CrestedBand: Codable, Hashable {
    var paired: Bool
    var bandId: String
    var status: String
}

struct Loyalty: Codable, Hashable {
    var tier: String
    var stampsToNextTier: Int
}

struct AppSnapshot: Codable, Hashable {
    var user: User
    var nextMatch: NextMatch
    var trail: TrailProgress
    var transactions: [Transaction]
    var leaderboard: [LeaderboardEntry]
    var stamps: [TrailStamp]
    var nearby: [Place]
    var embassies: [Place]
    var partners: [Place]
    var squad: [SquadMember]
    var ticket: Ticket
    var tickets: [Ticket]
    var prediction: Prediction
    var payToken: String
    var language: String
    var safetyAlert: String?
    var safetyAlertDismissed: Bool
    var reels: [ReelClip]
    var shots: [Shot]
    var highlights: [HighlightClip]
    var destinations: [Destination]
    var events: [FestivalEvent]
    var sports: [SportProfile]
    var coaches: [Coach]
    var players: [Player]
    var ads: [PromoAd]
    var crestedBand: CrestedBand
    var loyalty: Loyalty
}

extension Int {
    var ugx: String {
        let formatter = NumberFormatter()
        formatter.numberStyle = .decimal
        formatter.groupingSeparator = ","
        let digits = formatter.string(from: NSNumber(value: abs(self))) ?? "\(abs(self))"
        return self < 0 ? "−UGX \(digits)" : "UGX \(digits)"
    }

    var countdown: String {
        "\(self / 24)d \(self % 24)h"
    }
}
