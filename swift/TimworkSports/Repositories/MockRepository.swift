import Combine
import Foundation

final class MockRepository: ObservableObject, CrestedPassRepository {
    @Published private(set) var snapshot: AppSnapshot

    var current: AppSnapshot { snapshot }
    var publisher: AnyPublisher<AppSnapshot, Never> { $snapshot.eraseToAnyPublisher() }

    init(snapshot: AppSnapshot = MockRepository.initialSnapshot()) {
        self.snapshot = snapshot
    }

    func topUp(amount: Int) {
        snapshot.user.walletBalance += amount
        snapshot.transactions.insert(Transaction(id: UUID().uuidString, vendor: "Wallet top-up", amount: amount), at: 0)
    }

    func submitPrediction(homeScore: Int, awayScore: Int) {
        snapshot.prediction = Prediction(homeScore: homeScore, awayScore: awayScore)
    }

    func settle(memberId: String) {
        guard let index = snapshot.squad.firstIndex(where: { $0.id == memberId }),
              let amount = snapshot.squad[index].pendingAmount else { return }
        snapshot.squad[index].status = .atVenue
        snapshot.squad[index].pendingAmount = nil
        snapshot.user.walletBalance -= amount
        snapshot.transactions.insert(
            Transaction(id: UUID().uuidString, vendor: "Squad settle · \(snapshot.squad[index].name)", amount: -amount),
            at: 0
        )
    }

    func dismissSafetyAlert() {
        snapshot.safetyAlertDismissed = true
    }

    func collectNextStamp() {
        guard let nextIndex = snapshot.stamps.firstIndex(where: { $0.state == .nextAvailable }) else { return }
        snapshot.stamps[nextIndex].state = .completed
        let following = nextIndex + 1
        if following < snapshot.stamps.count {
            snapshot.stamps[following].state = .nextAvailable
        }
        snapshot.trail.collected = snapshot.stamps.filter { $0.state == .completed }.count
    }

    func toggleLanguage() {
        snapshot.language = snapshot.language == "EN" ? "LG" : "EN"
    }

    func toggleReelLike(_ id: String) {
        guard let index = snapshot.reels.firstIndex(where: { $0.id == id }) else { return }
        snapshot.reels[index].liked.toggle()
    }

    func toggleShotLike(_ id: String) {
        guard let index = snapshot.shots.firstIndex(where: { $0.id == id }) else { return }
        snapshot.shots[index].liked.toggle()
        snapshot.shots[index].likes += snapshot.shots[index].liked ? 1 : -1
    }

    func addDestinationToTrail(_ id: String) {
        guard snapshot.destinations.contains(where: { $0.id == id && $0.trailEligible }) else { return }
        collectNextStamp()
        let remaining = max(0, snapshot.loyalty.stampsToNextTier - 1)
        snapshot.loyalty.stampsToNextTier = remaining
        if snapshot.trail.collected >= 6 {
            snapshot.loyalty.tier = "Gold"
            snapshot.loyalty.stampsToNextTier = 0
        }
    }

    func freezeBand() {
        snapshot.crestedBand.status = snapshot.crestedBand.status == "frozen" ? "active" : "frozen"
    }

    func reissueBand() {
        snapshot.crestedBand.bandId = "CB-\(Int.random(in: 80000...89999))"
        snapshot.crestedBand.status = "active"
        snapshot.crestedBand.paired = true
    }

    func simulateBandTap() {
        if snapshot.crestedBand.status == "active" {
            collectNextStamp()
        }
    }

    func buyTicket(eventId: String) {
        guard let event = snapshot.events.first(where: { $0.id == eventId }) else { return }
        guard !snapshot.tickets.contains(where: { $0.eventId == eventId }) else { return }
        guard snapshot.user.walletBalance >= event.price else { return }
        let ticket = event.makeTicket()
        snapshot.user.walletBalance -= event.price
        snapshot.tickets.insert(ticket, at: 0)
        snapshot.ticket = ticket
        snapshot.transactions.insert(
            Transaction(id: UUID().uuidString, vendor: "Ticket · \(event.title)", amount: -event.price),
            at: 0
        )
    }

    static func initialSnapshot() -> AppSnapshot {
        AppSnapshot(
            user: User(name: "Amara N.", walletBalance: 84_000),
            nextMatch: NextMatch(home: "Uganda", away: "Senegal", venue: "Nakivubo Stadium", kickoffInHours: 54),
            trail: TrailProgress(collected: 4, total: 6),
            transactions: [
                Transaction(id: "tx1", vendor: "Nile Cafe stall", amount: -4_500),
                Transaction(id: "tx2", vendor: "Nakasero craft market", amount: -12_000),
                Transaction(id: "tx3", vendor: "Wallet top-up", amount: 50_000)
            ],
            leaderboard: [
                LeaderboardEntry(name: "Denis K.", points: 420),
                LeaderboardEntry(name: "Amara N.", points: 390),
                LeaderboardEntry(name: "Joel M.", points: 355)
            ],
            stamps: [
                TrailStamp(id: "s1", name: "Nile Cafe stall", location: "Kampala", state: .completed),
                TrailStamp(id: "s2", name: "Nakasero craft market", location: "Kampala", state: .completed),
                TrailStamp(id: "s3", name: "Kasubi Tombs", location: "Kampala", state: .completed),
                TrailStamp(id: "s4", name: "Source of the Nile", location: "Jinja", state: .completed),
                TrailStamp(id: "s5", name: "Entebbe Botanical Gardens", location: "Entebbe", state: .nextAvailable),
                TrailStamp(id: "s6", name: "Bwindi Forest Gate", location: "Kanungu", state: .locked)
            ],
            nearby: [
                Place(id: "e1", name: "Fan Embassy Kampala", kind: .embassy, area: "Speke Road", detail: "Help desk, SIM cards, match info"),
                Place(id: "e2", name: "Fan Embassy Entebbe", kind: .embassy, area: "Airport Road", detail: "Arrivals desk for visiting fans"),
                Place(id: "p1", name: "Nile Cafe stall", kind: .partner, area: "Nakivubo", detail: "Match-day coffee and stamps"),
                Place(id: "p2", name: "Nakasero craft market", kind: .partner, area: "Nakasero", detail: "Trail partner · crafts and gifts")
            ],
            embassies: [
                Place(id: "e1", name: "Fan Embassy Kampala", kind: .embassy, area: "Speke Road", detail: "Open 08:00–22:00 · lost-and-found, ticketing help"),
                Place(id: "e2", name: "Fan Embassy Entebbe", kind: .embassy, area: "Airport Road", detail: "Open 06:00–midnight · arrivals & transfers"),
                Place(id: "e3", name: "Fan Embassy Jinja", kind: .embassy, area: "Main Street", detail: "Open 09:00–18:00 · trail desk")
            ],
            partners: [
                Place(id: "p1", name: "Nile Cafe stall", kind: .partner, area: "Nakivubo", detail: "Stamp + match-day menu"),
                Place(id: "p2", name: "Nakasero craft market", kind: .partner, area: "Nakasero", detail: "Stamp + UTB-certified crafts"),
                Place(id: "p3", name: "Entebbe Botanical Gardens", kind: .partner, area: "Entebbe", detail: "Next stamp on the trail"),
                Place(id: "p4", name: "Source of the Nile", kind: .partner, area: "Jinja", detail: "Trail partner · riverfront")
            ],
            squad: [
                SquadMember(id: "m1", name: "Denis K.", status: .atVenue),
                SquadMember(id: "m2", name: "Joel M.", status: .enRoute),
                SquadMember(id: "m3", name: "Sarah A.", status: .paymentPending, pendingAmount: 15_000),
                SquadMember(id: "m4", name: "Musa T.", status: .atVenue)
            ],
            ticket: Ticket(
                id: "t-uga-sen",
                matchLabel: "Uganda vs Senegal",
                venue: "Nakivubo Stadium",
                gate: "Gate 4",
                block: "Block C",
                seat: "Seat 18",
                kickoffNote: "Kickoff in 54 hours",
                qrToken: "CP-TICKET-AMARA-UGA-SEN",
                eventId: "ev-foot"
            ),
            tickets: [
                Ticket(
                    id: "t-uga-sen",
                    matchLabel: "Uganda vs Senegal",
                    venue: "Nakivubo Stadium",
                    gate: "Gate 4",
                    block: "Block C",
                    seat: "Seat 18",
                    kickoffNote: "Kickoff in 54 hours",
                    qrToken: "CP-TICKET-AMARA-UGA-SEN",
                    eventId: "ev-foot"
                )
            ],
            prediction: Prediction(),
            payToken: "CP-PAY-AMARA-N-WALLET",
            language: "EN",
            safetyAlert: "Stay hydrated. Follow stewards to Gate 4 if you need assistance.",
            safetyAlertDismissed: false,
            reels: [
                ReelClip(id: "r0", type: "fan", title: "Nakivubo from the stands", videoUrl: "mock://reel0.mp4", imageName: "reel_stands", authorName: "Amara N."),
                ReelClip(id: "r1", type: "highlight", title: "Uganda 2-1 Senegal — match winner", videoUrl: "mock://reel1.mp4", imageName: "reel_winner"),
                ReelClip(id: "r2", type: "travel", title: "Gorilla trekking, Bwindi Forest", videoUrl: "mock://reel2.mp4", imageName: "reel_bwindi", destinationId: "d1"),
                ReelClip(id: "r3", type: "fan", title: "Kampala Fan Village, matchday atmosphere", videoUrl: "mock://reel3.mp4", imageName: "reel_fanvillage"),
                ReelClip(id: "r4", type: "fan", title: "Outside the gates · who's coming?", videoUrl: "mock://reel4.mp4", imageName: "reel_gates", authorName: "Denis K."),
                ReelClip(id: "r5", type: "travel", title: "Match-day pours at Nile Cafe", videoUrl: "mock://reel5.mp4", imageName: "reel_cafe", authorName: "Nile Cafe")
            ],
            shots: [
                Shot(id: "s1", author: "Cranes TV", handle: "@cranestv", caption: "Late winner. The stadium went silent then exploded.", imageName: "shot_late_winner", likes: 12_840),
                Shot(id: "s2", author: "UTB Trails", handle: "@visituganda", caption: "Bwindi at first light. Stamp waiting at the gate.", imageName: "shot_bwindi", likes: 8_902),
                Shot(id: "s3", author: "Fan Village", handle: "@fanvillage", caption: "Kampala never sleeps on match week.", imageName: "shot_kampala_night", likes: 5_401),
                Shot(id: "s4", author: "Denis K.", handle: "@denisk", caption: "Outside Nakivubo. Who's coming?", imageName: "shot_nakivubo_gates", likes: 2_104),
                Shot(id: "s5", author: "Nile Cafe", handle: "@nilecafe", caption: "Match-day pours and a trail stamp.", imageName: "shot_nile_cafe", likes: 933)
            ],
            highlights: [
                HighlightClip(id: "h1", match: "Uganda vs Senegal", title: "Late winner, 89th minute", videoUrl: "mock://highlight1.mp4", sport: "Football"),
                HighlightClip(id: "h-bball", match: "Cranes vs Kenya", title: "Buzzer beater from the corner", videoUrl: "mock://highlight-bball.mp4", sport: "Basketball"),
                HighlightClip(id: "h-tennis", match: "Kampala Open", title: "Tie-break on centre court", videoUrl: "mock://highlight-tennis.mp4", sport: "Tennis"),
                HighlightClip(id: "h-box", match: "East Africa Fight Night", title: "Final round, split decision", videoUrl: "mock://highlight-box.mp4", sport: "Boxing"),
                HighlightClip(id: "h-swim", match: "Nile Swim Cup", title: "50m freestyle final", videoUrl: "mock://highlight-swim.mp4", sport: "Swimming"),
                HighlightClip(id: "h-track", match: "National Athletics Trials", title: "100m photo finish", videoUrl: "mock://highlight-track.mp4", sport: "Athletics"),
                HighlightClip(id: "h2", match: "Kenya vs Tanzania", title: "Opening ceremony recap", videoUrl: "mock://highlight2.mp4", sport: "Football")
            ],
            destinations: [
                Destination(id: "d1", name: "Bwindi Impenetrable Forest", category: "Gorilla trekking", trailEligible: true, blurb: "Mist-forest trails and mountain gorilla families. A Cranes' Trail stamp waits at the gate."),
                Destination(id: "d2", name: "Source of the Nile, Jinja", category: "Landmark", trailEligible: true, blurb: "Where the Nile begins. Partner check-in on the riverfront."),
                Destination(id: "d3", name: "Nakasero Market", category: "Culture", trailEligible: true, blurb: "Kampala's oldest market — crafts, coffee, and a trail partner stall."),
                Destination(id: "d4", name: "Lake Victoria Cruise", category: "Leisure", trailEligible: false, blurb: "Sunset on the lake. Book a boat, not a stamp.")
            ],
            events: [
                FestivalEvent(id: "ev-foot", title: "Uganda vs Senegal", sport: "Football", date: "14 Jun", time: "19:00", city: "Kampala", venue: "Nakivubo Stadium", price: 45_000, kickoffInHours: 54),
                FestivalEvent(id: "ev-bball", title: "Cranes vs Kenya", sport: "Basketball", date: "15 Jun", time: "18:00", city: "Kampala", venue: "Lugogo Arena", price: 25_000, kickoffInHours: 78),
                FestivalEvent(id: "ev-foot2", title: "Kenya vs Tanzania", sport: "Football", date: "16 Jun", time: "16:00", city: "Kampala", venue: "Mandela National Stadium", price: 35_000, kickoffInHours: 96),
                FestivalEvent(id: "ev-tennis", title: "Kampala Open final", sport: "Tennis", date: "16 Jun", time: "15:00", city: "Kampala", venue: "Lugogo Tennis Club", price: 18_000, kickoffInHours: 98),
                FestivalEvent(id: "ev-box", title: "East Africa Fight Night", sport: "Boxing", date: "17 Jun", time: "20:00", city: "Kampala", venue: "MTN Arena", price: 22_000, kickoffInHours: 126),
                FestivalEvent(id: "ev-swim", title: "Nile Swim Cup", sport: "Swimming", date: "18 Jun", time: "10:00", city: "Kampala", venue: "Kampala Aquatic Centre", price: 12_000, kickoffInHours: 140),
                FestivalEvent(id: "ev-track", title: "National Athletics Trials", sport: "Athletics", date: "19 Jun", time: "09:00", city: "Kampala", venue: "Mandela National Stadium", price: 8_000, kickoffInHours: 163),
                FestivalEvent(id: "ev-tennis2", title: "Lakeside invitational", sport: "Tennis", date: "20 Jun", time: "14:00", city: "Entebbe", venue: "Botanical Gardens courts", price: 10_000, kickoffInHours: 192)
            ],
            sports: [
                SportProfile(id: "sp-foot", name: "Football", federation: "Federation of Uganda Football Associations", city: "Kampala", blurb: "The Cranes' home programme: Nakivubo and Mandela, plus the fan village around every kickoff.", athletes: 23),
                SportProfile(id: "sp-bball", name: "Basketball", federation: "Federation of Uganda Basketball Associations", city: "Kampala", blurb: "Indoor nights at Lugogo. National team windows and club finals in the same week.", athletes: 15),
                SportProfile(id: "sp-tennis", name: "Tennis", federation: "Uganda Tennis Association", city: "Kampala", blurb: "Club finals and the lakeside invitational. Centre court sits inside Lugogo.", athletes: 12),
                SportProfile(id: "sp-box", name: "Boxing", federation: "Uganda Boxing Federation", city: "Kampala", blurb: "Fight night under the lights at MTN Arena. East Africa cards, local undercards.", athletes: 10),
                SportProfile(id: "sp-swim", name: "Swimming", federation: "Uganda Swimming Federation", city: "Kampala", blurb: "Morning finals at the aquatic centre. Sprint and distance on the same programme.", athletes: 18),
                SportProfile(id: "sp-track", name: "Athletics", federation: "Uganda Athletics Federation", city: "Kampala", blurb: "Trials on the Mandela track. Sprints, middle distance, and field events.", athletes: 28)
            ],
            coaches: [
                Coach(id: "c-foot", name: "Amina Okello", sport: "Football", role: "Head coach", bio: "Builds the Cranes' match-week plan and the set-piece sheet for Nakivubo.", years: 11, imageName: "coach_sofia", rating: 4.9, wins: 86, draws: 21, losses: 14, licence: "CAF A", clubs: "Vipers SC · Uganda Cranes"),
                Coach(id: "c-bball", name: "David Ssali", sport: "Basketball", role: "Head coach", bio: "Runs the Lugogo offence and the national-team camp before the Kenya window.", years: 9, imageName: "coach_marcus", rating: 4.7, wins: 112, draws: 0, losses: 38, licence: "FIBA Level 3", clubs: "City Oilers · Uganda"),
                Coach(id: "c-tennis", name: "Grace Namutebi", sport: "Tennis", role: "High-performance coach", bio: "Prepares the Kampala Open finalists and the lakeside invitational draw.", years: 8, imageName: "coach_maya", rating: 4.8, wins: 64, draws: 0, losses: 18, licence: "ITF coaching", clubs: "Lugogo Club · Kampala Open"),
                Coach(id: "c-box", name: "Joseph Kato", sport: "Boxing", role: "Head coach", bio: "Corners the East Africa Fight Night card and the amateur undercard.", years: 14, imageName: "coach_liam", rating: 4.6, wins: 41, draws: 0, losses: 9, licence: "National corners licence", clubs: "MTN Arena · Uganda Boxing"),
                Coach(id: "c-swim", name: "Joel Tumusiime", sport: "Swimming", role: "Head coach", bio: "Sets the Nile Swim Cup warm-up and the sprint final lineup.", years: 7, imageName: "coach_kenji", rating: 4.8, wins: 22, draws: 0, losses: 6, licence: "World Aquatics", clubs: "Nile Swim Club · Kampala Aquatic"),
                Coach(id: "c-track", name: "Mercy Adong", sport: "Athletics", role: "Sprints coach", bio: "Calls the 100m trials and the photo-finish relays at Mandela.", years: 12, imageName: "coach_aisha", rating: 4.9, wins: 18, draws: 0, losses: 4, licence: "World Athletics Level 2", clubs: "Mandela Track Club · Uganda")
            ],
            ads: [
                PromoAd(id: "ad1", sponsor: "Timwork Sports", headline: "Tickets open", detail: "Uganda vs Senegal and the rest of the Kampala card. Seats from the app.", imageName: "ad_tickets"),
                PromoAd(id: "ad2", sponsor: "Nile Cafe", headline: "Fan village coffee", detail: "Match-day stall at the gates. Stamp your trail when you order.", imageName: "ad_nile"),
                PromoAd(id: "ad3", sponsor: "Crested Band", headline: "Wear the pass", detail: "Tap in at the gate, the stall, and the fan village.", imageName: "ad_band")
            ],
            players: PlayerRoster.all(),
            crestedBand: CrestedBand(paired: true, bandId: "CB-88213", status: "active"),
            loyalty: Loyalty(tier: "Silver", stampsToNextTier: 2)
        )
    }
}

enum PlayerRoster {
    static func all() -> [Player] {
        let first = [
            "Farouk", "Sarah", "Denis", "Joan", "Emmanuel", "Patricia",
            "Brian", "Lydia", "Mark", "Isaac", "Sharon", "Moses",
            "Claire", "Joel", "Anita", "Joshua", "Mercy", "Samuel",
            "Hannah", "Peter", "Amina", "Daniel", "Ruth", "Joseph",
            "Grace", "David", "Nora", "Simon", "Esther", "Paul"
        ]
        let last = [
            "Magumba", "Achieng", "Wasswa", "Nankya", "Otim", "Akello",
            "Ssemakula", "Nambi", "Byaruhanga", "Lubega", "Atim", "Waiswa",
            "Mbabazi", "Tumusiime", "Kiconco", "Adong", "Ocen", "Namara",
            "Kato", "Nakato", "Mugisha", "Okello", "Ssali", "Namutebi",
            "Babirye", "Ojok", "Auma", "Ssebugwawo", "Nabirye", "Opio"
        ]
        struct Spec {
            let sport: String
            let count: Int
            let positions: [String]
            let clubs: [String]
            let score: String
            let heightBase: Int
        }
        let specs = [
            Spec(sport: "Football", count: 23, positions: ["Striker", "Midfielder", "Defender", "Goalkeeper", "Winger"], clubs: ["Vipers SC", "KCCA FC", "SC Villa", "URA FC"], score: "goals", heightBase: 174),
            Spec(sport: "Basketball", count: 15, positions: ["Point guard", "Shooting guard", "Small forward", "Power forward", "Center"], clubs: ["City Oilers", "Namuwongo Blazers", "KCCA Leopards"], score: "points", heightBase: 182),
            Spec(sport: "Tennis", count: 12, positions: ["Singles", "Doubles"], clubs: ["Lugogo Club", "Kampala Club", "Entebbe Club"], score: "titles", heightBase: 170),
            Spec(sport: "Boxing", count: 10, positions: ["Welterweight", "Featherweight", "Light heavyweight", "Flyweight", "Middleweight"], clubs: ["MTN Arena", "Nakivubo gym", "Lugogo gym"], score: "wins", heightBase: 168),
            Spec(sport: "Swimming", count: 18, positions: ["Freestyle", "Butterfly", "Backstroke", "Breaststroke"], clubs: ["Kampala Aquatic Centre", "Nile Swim Club"], score: "medals", heightBase: 172),
            Spec(sport: "Athletics", count: 28, positions: ["100m", "1500m", "Long jump", "400m", "Marathon", "High jump"], clubs: ["Mandela Track Club", "Kampala Distance", "Jinja Athletics"], score: "medals", heightBase: 168)
        ]
        let cities = ["Kampala", "Entebbe", "Jinja", "Gulu"]
        var used = Set<String>()
        var out: [Player] = []
        for (sportIndex, spec) in specs.enumerated() {
            for i in 0..<spec.count {
                var shift = 0
                var name = ""
                repeat {
                    let fi = (i * 3 + sportIndex * 5 + shift) % first.count
                    let li = (i * 2 + sportIndex * 7 + shift) % last.count
                    name = "\(first[fi]) \(last[li])"
                    shift += 1
                } while used.contains(name) && shift < 40
                used.insert(name)
                let position = spec.positions[i % spec.positions.count]
                let club = spec.clubs[i % spec.clubs.count]
                let age = 18 + (i * 3 + sportIndex) % 15
                let number = 1 + (i * 7 + sportIndex) % 99
                let height = spec.heightBase + (i * 2) % 18
                let apps = 6 + (i * 2) % 22
                let score = (i * 3 + sportIndex) % 18
                let city = cities[(i + sportIndex) % cities.count]
                let slug = String(spec.sport.lowercased().prefix(4))
                out.append(Player(
                    id: "p-\(slug)-\(i)",
                    name: name,
                    sport: spec.sport,
                    position: position,
                    club: club,
                    city: city,
                    nationality: "Uganda",
                    age: age,
                    number: number,
                    heightCm: height,
                    appearances: apps,
                    scoring: score,
                    scoringLabel: spec.score,
                    bio: "\(name) is a \(position) for \(club). \(apps) appearances this season, based in \(city)."
                ))
            }
        }
        return out
    }
}
