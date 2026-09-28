package com.timworksports.crestedpass.data.repository

import com.timworksports.crestedpass.data.model.AppSnapshot
import com.timworksports.crestedpass.data.model.Coach
import com.timworksports.crestedpass.data.model.CrestedBand
import com.timworksports.crestedpass.data.model.Destination
import com.timworksports.crestedpass.data.model.FestivalEvent
import com.timworksports.crestedpass.data.model.HighlightClip
import com.timworksports.crestedpass.data.model.LeaderboardEntry
import com.timworksports.crestedpass.data.model.Loyalty
import com.timworksports.crestedpass.data.model.MerchItem
import com.timworksports.crestedpass.data.model.NextMatch
import com.timworksports.crestedpass.data.model.Place
import com.timworksports.crestedpass.data.model.PlaceKind
import com.timworksports.crestedpass.data.model.Player
import com.timworksports.crestedpass.data.model.Prediction
import com.timworksports.crestedpass.data.model.PromoAd
import com.timworksports.crestedpass.data.model.ReelClip
import com.timworksports.crestedpass.data.model.Shot
import com.timworksports.crestedpass.data.model.SportProfile
import com.timworksports.crestedpass.data.model.SquadMember
import com.timworksports.crestedpass.data.model.SquadStatus
import com.timworksports.crestedpass.data.model.StampState
import com.timworksports.crestedpass.data.model.Ticket
import com.timworksports.crestedpass.data.model.TrailProgress
import com.timworksports.crestedpass.data.model.TrailStamp
import com.timworksports.crestedpass.data.model.Transaction
import com.timworksports.crestedpass.data.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MockRepository : CrestedPassRepository {
    private val state = MutableStateFlow(initialSnapshot())

    override fun observeSnapshot(): Flow<AppSnapshot> = state.asStateFlow()

    override suspend fun topUp(amount: Int) {
        state.update { current ->
            current.copy(
                user = current.user.copy(walletBalance = current.user.walletBalance + amount),
                transactions = listOf(Transaction("Wallet top-up", amount)) + current.transactions
            )
        }
    }

    override suspend fun submitPrediction(homeScore: Int, awayScore: Int) {
        state.update { it.copy(prediction = Prediction(homeScore, awayScore)) }
    }

    override suspend fun settle(memberId: String) {
        state.update { current ->
            val member = current.squad.find { it.id == memberId } ?: return@update current
            val amount = member.pendingAmount ?: return@update current
            current.copy(
                squad = current.squad.map { item ->
                    if (item.id == memberId) {
                        item.copy(status = SquadStatus.AT_VENUE, pendingAmount = null)
                    } else {
                        item
                    }
                },
                user = current.user.copy(walletBalance = current.user.walletBalance - amount),
                transactions = listOf(
                    Transaction("Squad settle · ${member.name}", -amount)
                ) + current.transactions
            )
        }
    }

    override suspend fun dismissSafetyAlert() {
        state.update { it.copy(safetyAlertDismissed = true) }
    }

    override suspend fun collectNextStamp() {
        state.update { current ->
            val nextIndex = current.stamps.indexOfFirst { it.state == StampState.NEXT_AVAILABLE }
            if (nextIndex < 0) return@update current
            val updated = current.stamps.mapIndexed { index, stamp ->
                when {
                    index == nextIndex -> stamp.copy(state = StampState.COMPLETED)
                    index == nextIndex + 1 -> stamp.copy(state = StampState.NEXT_AVAILABLE)
                    else -> stamp
                }
            }
            val collected = updated.count { it.state == StampState.COMPLETED }
            current.copy(
                stamps = updated,
                trail = TrailProgress(collected = collected, total = updated.size)
            )
        }
    }

    override suspend fun toggleLanguage() {
        state.update {
            it.copy(language = if (it.language == "EN") "LG" else "EN")
        }
    }

    override suspend fun toggleReelLike(id: String) {
        state.update { current ->
            current.copy(reels = current.reels.map { if (it.id == id) it.copy(liked = !it.liked) else it })
        }
    }

    override suspend fun toggleShotLike(id: String) {
        state.update { current ->
            current.copy(
                shots = current.shots.map { shot ->
                    if (shot.id != id) shot
                    else shot.copy(
                        liked = !shot.liked,
                        likes = shot.likes + if (shot.liked) -1 else 1
                    )
                }
            )
        }
    }

    override suspend fun addDestinationToTrail(id: String) {
        val eligible = state.value.destinations.any { it.id == id && it.trailEligible }
        if (eligible) collectNextStamp()
        state.update { current ->
            val remaining = maxOf(0, current.loyalty.stampsToNextTier - 1)
            current.copy(
                loyalty = current.loyalty.copy(
                    stampsToNextTier = remaining,
                    tier = if (current.trail.collected >= 6) "Gold" else current.loyalty.tier
                )
            )
        }
    }

    override suspend fun freezeBand() {
        state.update {
            it.copy(
                crestedBand = it.crestedBand.copy(
                    status = if (it.crestedBand.status == "frozen") "active" else "frozen"
                )
            )
        }
    }

    override suspend fun reissueBand() {
        state.update {
            it.copy(
                crestedBand = CrestedBand(true, "CB-${(80000..89999).random()}", "active")
            )
        }
    }

    override suspend fun simulateBandTap() {
        if (state.value.crestedBand.status == "active") collectNextStamp()
    }

    override suspend fun createEvent(
        title: String,
        sport: String,
        city: String,
        venue: String,
        date: String,
        time: String
    ) {
        state.update { current ->
            val id = "ev-${current.events.size + 1}-${title.hashCode().toString().replace("-", "x")}"
            val created = FestivalEvent(id, title, sport, date, time, city, venue, 0, 12)
            current.copy(events = listOf(created) + current.events)
        }
    }

    override suspend fun buyTicket(eventId: String) {
        state.update { current ->
            val event = current.events.find { it.id == eventId } ?: return@update current
            if (current.tickets.any { it.eventId == eventId }) return@update current
            if (current.user.walletBalance < event.price) return@update current
            val ticket = event.makeTicket()
            current.copy(
                user = current.user.copy(walletBalance = current.user.walletBalance - event.price),
                ticket = ticket,
                tickets = listOf(ticket) + current.tickets,
                transactions = listOf(Transaction("Ticket · ${event.title}", -event.price)) + current.transactions
            )
        }
    }

    companion object {
        fun initialSnapshot(): AppSnapshot = AppSnapshot(
            user = User(name = "Amara N.", walletBalance = 84_000),
            nextMatch = NextMatch(
                home = "Uganda",
                away = "Senegal",
                venue = "Nakivubo Stadium",
                kickoffInHours = 54
            ),
            trail = TrailProgress(collected = 4, total = 6),
            transactions = listOf(
                Transaction("Nile Cafe stall", -4_500),
                Transaction("Nakasero craft market", -12_000),
                Transaction("Wallet top-up", 50_000)
            ),
            leaderboard = listOf(
                LeaderboardEntry("Denis K.", 420),
                LeaderboardEntry("Amara N.", 390),
                LeaderboardEntry("Joel M.", 355)
            ),
            stamps = listOf(
                TrailStamp("s1", "Nile Cafe stall", "Kampala", StampState.COMPLETED),
                TrailStamp("s2", "Nakasero craft market", "Kampala", StampState.COMPLETED),
                TrailStamp("s3", "Kasubi Tombs", "Kampala", StampState.COMPLETED),
                TrailStamp("s4", "Source of the Nile", "Jinja", StampState.COMPLETED),
                TrailStamp("s5", "Entebbe Botanical Gardens", "Entebbe", StampState.NEXT_AVAILABLE),
                TrailStamp("s6", "Bwindi Forest Gate", "Kanungu", StampState.LOCKED)
            ),
            nearby = listOf(
                Place("e1", "Fan Embassy Kampala", PlaceKind.EMBASSY, "Speke Road", "Help desk, SIM cards, match info"),
                Place("e2", "Fan Embassy Entebbe", PlaceKind.EMBASSY, "Airport Road", "Arrivals desk for visiting fans"),
                Place("p1", "Nile Cafe stall", PlaceKind.PARTNER, "Nakivubo", "Match-day coffee and stamps"),
                Place("p2", "Nakasero craft market", PlaceKind.PARTNER, "Nakasero", "Trail partner · crafts and gifts")
            ),
            embassies = listOf(
                Place("e1", "Fan Embassy Kampala", PlaceKind.EMBASSY, "Speke Road", "Open 08:00–22:00 · lost-and-found, ticketing help"),
                Place("e2", "Fan Embassy Entebbe", PlaceKind.EMBASSY, "Airport Road", "Open 06:00–midnight · arrivals & transfers"),
                Place("e3", "Fan Embassy Jinja", PlaceKind.EMBASSY, "Main Street", "Open 09:00–18:00 · trail desk")
            ),
            partners = listOf(
                Place("p1", "Nile Cafe stall", PlaceKind.PARTNER, "Nakivubo", "Stamp + match-day menu"),
                Place("p2", "Nakasero craft market", PlaceKind.PARTNER, "Nakasero", "Stamp + UTB-certified crafts"),
                Place("p3", "Entebbe Botanical Gardens", PlaceKind.PARTNER, "Entebbe", "Next stamp on the trail"),
                Place("p4", "Source of the Nile", PlaceKind.PARTNER, "Jinja", "Trail partner · riverfront")
            ),
            squad = listOf(
                SquadMember("m1", "Denis K.", SquadStatus.AT_VENUE),
                SquadMember("m2", "Joel M.", SquadStatus.EN_ROUTE),
                SquadMember("m3", "Sarah A.", SquadStatus.PAYMENT_PENDING, pendingAmount = 15_000),
                SquadMember("m4", "Musa T.", SquadStatus.AT_VENUE)
            ),
            ticket = Ticket(
                id = "t-uga-sen",
                matchLabel = "Uganda vs Senegal",
                venue = "Nakivubo Stadium",
                gate = "Gate 4",
                block = "Block C",
                seat = "Seat 18",
                kickoffNote = "Kickoff in 54 hours",
                qrToken = "CP-TICKET-AMARA-UGA-SEN",
                eventId = "ev-foot"
            ),
            tickets = listOf(
                Ticket(
                    id = "t-uga-sen",
                    matchLabel = "Uganda vs Senegal",
                    venue = "Nakivubo Stadium",
                    gate = "Gate 4",
                    block = "Block C",
                    seat = "Seat 18",
                    kickoffNote = "Kickoff in 54 hours",
                    qrToken = "CP-TICKET-AMARA-UGA-SEN",
                    eventId = "ev-foot"
                )
            ),
            prediction = Prediction(),
            payToken = "CP-PAY-AMARA-N-WALLET",
            language = "EN",
            safetyAlert = "Stay hydrated. Follow stewards to Gate 4 if you need assistance.",
            safetyAlertDismissed = false,
            reels = listOf(
                ReelClip("r0", "fan", "Nakivubo from the stands", "mock://reel0.mp4", "reel_stands", authorName = "Amara N."),
                ReelClip("r1", "highlight", "Uganda 2-1 Senegal — match winner", "mock://reel1.mp4", "reel_winner"),
                ReelClip("r2", "travel", "Gorilla trekking, Bwindi Forest", "mock://reel2.mp4", "reel_bwindi", "d1"),
                ReelClip("r3", "fan", "Kampala Fan Village, matchday atmosphere", "mock://reel3.mp4", "reel_fanvillage"),
                ReelClip("r4", "fan", "Outside the gates · who's coming?", "mock://reel4.mp4", "reel_gates", authorName = "Denis K."),
                ReelClip("r5", "travel", "Match-day pours at Nile Cafe", "mock://reel5.mp4", "reel_cafe", authorName = "Nile Cafe")
            ),
            shots = listOf(
                Shot("s1", "Cranes TV", "@cranestv", "Late winner. The stadium went silent then exploded.", "shot_late_winner", 12_840),
                Shot("s2", "UTB Trails", "@visituganda", "Bwindi at first light. Stamp waiting at the gate.", "shot_bwindi", 8_902),
                Shot("s3", "Fan Village", "@fanvillage", "Kampala never sleeps on match week.", "shot_kampala_night", 5_401),
                Shot("s4", "Denis K.", "@denisk", "Outside Nakivubo. Who's coming?", "shot_nakivubo_gates", 2_104),
                Shot("s5", "Nile Cafe", "@nilecafe", "Match-day pours and a trail stamp.", "shot_nile_cafe", 933)
            ),
            highlights = listOf(
                HighlightClip("h1", "Uganda vs Senegal", "Late winner, 89th minute", "mock://highlight1.mp4", "Football"),
                HighlightClip("h-bball", "Cranes vs Kenya", "Buzzer beater from the corner", "mock://highlight-bball.mp4", "Basketball"),
                HighlightClip("h-tennis", "Kampala Open", "Tie-break on centre court", "mock://highlight-tennis.mp4", "Tennis"),
                HighlightClip("h-box", "East Africa Fight Night", "Final round, split decision", "mock://highlight-box.mp4", "Boxing"),
                HighlightClip("h-swim", "Nile Swim Cup", "50m freestyle final", "mock://highlight-swim.mp4", "Swimming"),
                HighlightClip("h-track", "National Athletics Trials", "100m photo finish", "mock://highlight-track.mp4", "Athletics"),
                HighlightClip("h2", "Kenya vs Tanzania", "Opening ceremony recap", "mock://highlight2.mp4", "Football")
            ),
            destinations = listOf(
                Destination("d1", "Bwindi Impenetrable Forest", "Gorilla trekking", true, "Mist-forest trails and mountain gorilla families. A Cranes' Trail stamp waits at the gate."),
                Destination("d2", "Source of the Nile, Jinja", "Landmark", true, "Where the Nile begins. Partner check-in on the riverfront."),
                Destination("d3", "Nakasero Market", "Culture", true, "Kampala's oldest market — crafts, coffee, and a trail partner stall."),
                Destination("d4", "Lake Victoria Cruise", "Leisure", false, "Sunset on the lake. Book a boat, not a stamp.")
            ),
            events = listOf(
                FestivalEvent("ev-foot", "Uganda vs Senegal", "Football", "14 Jun", "19:00", "Kampala", "Nakivubo Stadium", 45_000, 54),
                FestivalEvent("ev-bball", "Cranes vs Kenya", "Basketball", "15 Jun", "18:00", "Kampala", "Lugogo Arena", 25_000, 78),
                FestivalEvent("ev-foot2", "Kenya vs Tanzania", "Football", "16 Jun", "16:00", "Kampala", "Mandela National Stadium", 35_000, 96),
                FestivalEvent("ev-tennis", "Kampala Open final", "Tennis", "16 Jun", "15:00", "Kampala", "Lugogo Tennis Club", 18_000, 98),
                FestivalEvent("ev-box", "East Africa Fight Night", "Boxing", "17 Jun", "20:00", "Kampala", "MTN Arena", 22_000, 126),
                FestivalEvent("ev-swim", "Nile Swim Cup", "Swimming", "18 Jun", "10:00", "Kampala", "Kampala Aquatic Centre", 12_000, 140),
                FestivalEvent("ev-track", "National Athletics Trials", "Athletics", "19 Jun", "09:00", "Kampala", "Mandela National Stadium", 8_000, 163),
                FestivalEvent("ev-tennis2", "Lakeside invitational", "Tennis", "20 Jun", "14:00", "Entebbe", "Botanical Gardens courts", 10_000, 192)
            ),
            sports = listOf(
                SportProfile("sp-foot", "Football", "Federation of Uganda Football Associations", "Kampala", "The Cranes' home programme: Nakivubo and Mandela, plus the fan village around every kickoff.", 23),
                SportProfile("sp-bball", "Basketball", "Federation of Uganda Basketball Associations", "Kampala", "Indoor nights at Lugogo. National team windows and club finals in the same week.", 15),
                SportProfile("sp-tennis", "Tennis", "Uganda Tennis Association", "Kampala", "Club finals and the lakeside invitational. Centre court sits inside Lugogo.", 12),
                SportProfile("sp-box", "Boxing", "Uganda Boxing Federation", "Kampala", "Fight night under the lights at MTN Arena. East Africa cards, local undercards.", 10),
                SportProfile("sp-swim", "Swimming", "Uganda Swimming Federation", "Kampala", "Morning finals at the aquatic centre. Sprint and distance on the same programme.", 18),
                SportProfile("sp-track", "Athletics", "Uganda Athletics Federation", "Kampala", "Trials on the Mandela track. Sprints, middle distance, and field events.", 28)
            ),
            coaches = listOf(
                Coach("c-foot", "Amina Okello", "Football", "Head coach", "Builds the Cranes' match-week plan and the set-piece sheet for Nakivubo.", 11, "coach_sofia", 4.9, 86, 21, 14, "CAF A", "Vipers SC · Uganda Cranes"),
                Coach("c-bball", "David Ssali", "Basketball", "Head coach", "Runs the Lugogo offence and the national-team camp before the Kenya window.", 9, "coach_marcus", 4.7, 112, 0, 38, "FIBA Level 3", "City Oilers · Uganda"),
                Coach("c-tennis", "Grace Namutebi", "Tennis", "High-performance coach", "Prepares the Kampala Open finalists and the lakeside invitational draw.", 8, "coach_maya", 4.8, 64, 0, 18, "ITF coaching", "Lugogo Club · Kampala Open"),
                Coach("c-box", "Joseph Kato", "Boxing", "Head coach", "Corners the East Africa Fight Night card and the amateur undercard.", 14, "coach_liam", 4.6, 41, 0, 9, "National corners licence", "MTN Arena · Uganda Boxing"),
                Coach("c-swim", "Joel Tumusiime", "Swimming", "Head coach", "Sets the Nile Swim Cup warm-up and the sprint final lineup.", 7, "coach_kenji", 4.8, 22, 0, 6, "World Aquatics", "Nile Swim Club · Kampala Aquatic"),
                Coach("c-track", "Mercy Adong", "Athletics", "Sprints coach", "Calls the 100m trials and the photo-finish relays at Mandela.", 12, "coach_aisha", 4.9, 18, 0, 4, "World Athletics Level 2", "Mandela Track Club · Uganda")
            ),
            ads = listOf(
                PromoAd("ad1", "Timwork Sports", "Tickets open", "Uganda vs Senegal and the rest of the Kampala card. Seats from the app.", "ad_tickets"),
                PromoAd("ad2", "Nile Cafe", "Fan village coffee", "Match-day stall at the gates. Stamp your trail when you order.", "ad_nile"),
                PromoAd("ad3", "Crested Band", "Wear the pass", "Tap in at the gate, the stall, and the fan village.", "ad_band")
            ),
            merch = listOf(
                MerchItem("m1", "Training jersey", "Kit", 85_000, "Black and deep green match jersey. Breathable, club crest on the chest.", "event_soccer", "S · M · L · XL"),
                MerchItem("m2", "Coach shell", "Coaching", 120_000, "Sideline jacket for match week. Lightweight, deep green trim.", "coach_sofia", "S · M · L · XL"),
                MerchItem("m3", "Court hoodie", "Kit", 95_000, "Warm-up hoodie for indoor sessions and travel days.", "coach_marcus", "S · M · L · XL"),
                MerchItem("m4", "Match ball", "Training", 70_000, "Match-weight ball for coaching sessions and club finals.", "event_basketball", "Size 7"),
                MerchItem("m5", "Track singlet", "Kit", 45_000, "Race singlet for trials and club meets.", "event_track", "XS · S · M · L"),
                MerchItem("m6", "Session cap", "Training", 25_000, "Sun cap for outdoor coaching blocks.", "event_tennis", "One size")
            ),
            players = PlayerRoster.all(),
            crestedBand = CrestedBand(true, "CB-88213", "active"),
            loyalty = Loyalty("Silver", 2)
        )
    }
}

private object PlayerRoster {
    fun all(): List<Player> {
        val first = listOf(
            "Farouk", "Sarah", "Denis", "Joan", "Emmanuel", "Patricia",
            "Brian", "Lydia", "Mark", "Isaac", "Sharon", "Moses",
            "Claire", "Joel", "Anita", "Joshua", "Mercy", "Samuel",
            "Hannah", "Peter", "Amina", "Daniel", "Ruth", "Joseph",
            "Grace", "David", "Nora", "Simon", "Esther", "Paul"
        )
        val last = listOf(
            "Magumba", "Achieng", "Wasswa", "Nankya", "Otim", "Akello",
            "Ssemakula", "Nambi", "Byaruhanga", "Lubega", "Atim", "Waiswa",
            "Mbabazi", "Tumusiime", "Kiconco", "Adong", "Ocen", "Namara",
            "Kato", "Nakato", "Mugisha", "Okello", "Ssali", "Namutebi",
            "Babirye", "Ojok", "Auma", "Ssebugwawo", "Nabirye", "Opio"
        )
        data class Spec(
            val sport: String,
            val count: Int,
            val positions: List<String>,
            val clubs: List<String>,
            val score: String,
            val heightBase: Int
        )
        val specs = listOf(
            Spec("Football", 23, listOf("Striker", "Midfielder", "Defender", "Goalkeeper", "Winger"), listOf("Vipers SC", "KCCA FC", "SC Villa", "URA FC"), "goals", 174),
            Spec("Basketball", 15, listOf("Point guard", "Shooting guard", "Small forward", "Power forward", "Center"), listOf("City Oilers", "Namuwongo Blazers", "KCCA Leopards"), "points", 182),
            Spec("Tennis", 12, listOf("Singles", "Doubles"), listOf("Lugogo Club", "Kampala Club", "Entebbe Club"), "titles", 170),
            Spec("Boxing", 10, listOf("Welterweight", "Featherweight", "Light heavyweight", "Flyweight", "Middleweight"), listOf("MTN Arena", "Nakivubo gym", "Lugogo gym"), "wins", 168),
            Spec("Swimming", 18, listOf("Freestyle", "Butterfly", "Backstroke", "Breaststroke"), listOf("Kampala Aquatic Centre", "Nile Swim Club"), "medals", 172),
            Spec("Athletics", 28, listOf("100m", "1500m", "Long jump", "400m", "Marathon", "High jump"), listOf("Mandela Track Club", "Kampala Distance", "Jinja Athletics"), "medals", 168)
        )
        val cities = listOf("Kampala", "Entebbe", "Jinja", "Gulu")
        val used = mutableSetOf<String>()
        val out = mutableListOf<Player>()
        specs.forEachIndexed { sportIndex, spec ->
            repeat(spec.count) { i ->
                var shift = 0
                var name: String
                do {
                    val fi = (i * 3 + sportIndex * 5 + shift) % first.size
                    val li = (i * 2 + sportIndex * 7 + shift) % last.size
                    name = "${first[fi]} ${last[li]}"
                    shift++
                } while (name in used && shift < 40)
                used += name
                val position = spec.positions[i % spec.positions.size]
                val club = spec.clubs[i % spec.clubs.size]
                val age = 18 + (i * 3 + sportIndex) % 15
                val number = 1 + (i * 7 + sportIndex) % 99
                val height = spec.heightBase + (i * 2) % 18
                val apps = 6 + (i * 2) % 22
                val score = (i * 3 + sportIndex) % 18
                val city = cities[(i + sportIndex) % cities.size]
                out += Player(
                    id = "p-${spec.sport.lowercase().take(4)}-$i",
                    name = name,
                    sport = spec.sport,
                    position = position,
                    club = club,
                    city = city,
                    nationality = "Uganda",
                    age = age,
                    number = number,
                    heightCm = height,
                    appearances = apps,
                    scoring = score,
                    scoringLabel = spec.score,
                    bio = "$name is a $position for $club. $apps appearances this season, based in $city."
                )
            }
        }
        return out
    }
}
