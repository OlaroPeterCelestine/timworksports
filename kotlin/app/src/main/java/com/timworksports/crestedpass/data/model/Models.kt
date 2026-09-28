package com.timworksports.crestedpass.data.model

import java.text.NumberFormat
import java.util.Locale

data class User(
    val name: String,
    val walletBalance: Int
)

val User.initials: String
    get() = name.split(" ").mapNotNull { it.firstOrNull()?.uppercaseChar()?.toString() }.take(2).joinToString("")

val User.handle: String
    get() {
        val parts = name.replace(".", "").split(" ").filter { it.isNotBlank() }.map { it.lowercase() }
        return "@" + parts.joinToString(".")
    }

data class NextMatch(
    val home: String,
    val away: String,
    val venue: String,
    val kickoffInHours: Int
)

data class TrailProgress(
    val collected: Int,
    val total: Int
)

data class Transaction(
    val vendor: String,
    val amount: Int
)

data class LeaderboardEntry(
    val name: String,
    val points: Int
)

enum class StampState { COMPLETED, NEXT_AVAILABLE, LOCKED }

data class TrailStamp(
    val id: String,
    val name: String,
    val location: String,
    val state: StampState
)

enum class PlaceKind { EMBASSY, PARTNER }

data class Place(
    val id: String,
    val name: String,
    val kind: PlaceKind,
    val area: String,
    val detail: String
)

enum class SquadStatus { AT_VENUE, EN_ROUTE, PAYMENT_PENDING }

data class SquadMember(
    val id: String,
    val name: String,
    val status: SquadStatus,
    val pendingAmount: Int? = null
)

data class Ticket(
    val id: String,
    val matchLabel: String,
    val venue: String,
    val gate: String,
    val block: String,
    val seat: String,
    val kickoffNote: String,
    val qrToken: String,
    val eventId: String = ""
)

data class Prediction(
    val homeScore: Int? = null,
    val awayScore: Int? = null
)

data class ReelClip(
    val id: String,
    val type: String,
    val title: String,
    val videoUrl: String,
    val imageName: String,
    val destinationId: String? = null,
    val liked: Boolean = false,
    val authorName: String? = null
) {

    val author: String
        get() = authorName ?: when (type) {
            "travel" -> "UTB Trails"
            "fan" -> "Fan Village"
            else -> "Cranes TV"
        }

    val handle: String
        get() = "@" + author.lowercase().replace(" ", "").replace(".", "")

    val initials: String
        get() = author.split(" ").mapNotNull { it.firstOrNull()?.uppercaseChar()?.toString() }.take(2).joinToString("")
}

data class Shot(
    val id: String,
    val author: String,
    val handle: String,
    val caption: String,
    val imageName: String,
    val likes: Int,
    val liked: Boolean = false
) {
    val initials: String
        get() = author.split(" ").mapNotNull { it.firstOrNull()?.uppercaseChar()?.toString() }.take(2).joinToString("")
}

data class HighlightClip(
    val id: String,
    val match: String,
    val title: String,
    val videoUrl: String,
    val sport: String = "Football"
) {
    val imageName: String
        get() = when (sport.lowercase()) {
            "basketball" -> "event_basketball"
            "tennis" -> "event_tennis"
            "boxing" -> "event_boxing"
            "swimming" -> "event_swim"
            "athletics", "track" -> "event_track"
            else -> if (id == "h2") "highlight_ceremony" else "highlight_winner"
        }
}

data class SportProfile(
    val id: String,
    val name: String,
    val federation: String,
    val city: String,
    val blurb: String,
    val athletes: Int
) {
    val imageName: String
        get() = when (name.lowercase()) {
            "basketball" -> "event_basketball"
            "tennis" -> "event_tennis"
            "boxing" -> "event_boxing"
            "swimming" -> "event_swim"
            "athletics" -> "event_track"
            else -> "event_soccer"
        }
}

data class Coach(
    val id: String,
    val name: String,
    val sport: String,
    val role: String,
    val bio: String,
    val years: Int,
    val imageName: String,
    val rating: Double,
    val wins: Int,
    val draws: Int,
    val losses: Int,
    val licence: String,
    val clubs: String
) {
    val initials: String
        get() = name.split(" ").mapNotNull { it.firstOrNull()?.uppercaseChar()?.toString() }.take(2).joinToString("")

    val record: String
        get() = if (draws == 0) "$wins–$losses" else "$wins–$draws–$losses"

    val winRate: Int
        get() {
            val played = wins + draws + losses
            if (played == 0) return 0
            return wins * 100 / played
        }
}

data class MerchItem(
    val id: String,
    val name: String,
    val category: String,
    val price: Int,
    val detail: String,
    val imageName: String,
    val sizes: String
)

data class PromoAd(
    val id: String,
    val sponsor: String,
    val headline: String,
    val detail: String,
    val imageName: String
)

data class Player(
    val id: String,
    val name: String,
    val sport: String,
    val position: String,
    val club: String,
    val city: String,
    val nationality: String,
    val age: Int,
    val number: Int,
    val heightCm: Int,
    val appearances: Int,
    val scoring: Int,
    val scoringLabel: String,
    val bio: String
) {
    val initials: String
        get() = name.split(" ").mapNotNull { it.firstOrNull()?.uppercaseChar()?.toString() }.take(2).joinToString("")

    val imageName: String
        get() = when (sport.lowercase()) {
            "basketball" -> "event_basketball"
            "tennis" -> "event_tennis"
            "boxing" -> "event_boxing"
            "swimming" -> "event_swim"
            "athletics" -> "event_track"
            else -> "event_soccer"
        }
}

data class Destination(
    val id: String,
    val name: String,
    val category: String,
    val trailEligible: Boolean,
    val blurb: String
) {
    val imageName: String
        get() = when (id) {
            "d1" -> "dest_bwindi"
            "d2" -> "dest_nile"
            "d3" -> "dest_nakasero"
            else -> "dest_victoria"
        }
}

data class FestivalEvent(
    val id: String,
    val title: String,
    val sport: String,
    val date: String,
    val time: String,
    val city: String,
    val venue: String,
    val price: Int,
    val kickoffInHours: Int
) {
    val type: String get() = sport

    val imageName: String
        get() = when {
            id == "ev-foot2" -> "event_kenya_match"
            sport.lowercase() == "basketball" -> "event_basketball"
            sport.lowercase() == "tennis" -> "event_tennis"
            sport.lowercase() == "boxing" -> "event_boxing"
            sport.lowercase() == "swimming" -> "event_swim"
            sport.lowercase() in setOf("athletics", "track") -> "event_track"
            sport.lowercase() in setOf("football", "soccer") -> "event_soccer"
            else -> "hero_venue"
        }

    val whenLabel: String get() = "$date · $time"

    fun makeTicket(): Ticket {
        val seed = kotlin.math.abs(id.hashCode())
        val gates = listOf("Gate 2", "Gate 3", "Gate 4", "Gate 5")
        val blocks = listOf("Block A", "Block B", "Block C", "Block D")
        return Ticket(
            id = "t-$id",
            matchLabel = title,
            venue = venue,
            gate = gates[seed % gates.size],
            block = blocks[(seed / 3) % blocks.size],
            seat = "Seat ${1 + seed % 28}",
            kickoffNote = "Starts in ${kickoffInHours.asCountdown()}",
            qrToken = "CP-TICKET-${id.uppercase()}",
            eventId = id
        )
    }
}

data class CrestedBand(
    val paired: Boolean,
    val bandId: String,
    val status: String
)

data class Loyalty(
    val tier: String,
    val stampsToNextTier: Int
)

data class AppSnapshot(
    val user: User,
    val nextMatch: NextMatch,
    val trail: TrailProgress,
    val transactions: List<Transaction>,
    val leaderboard: List<LeaderboardEntry>,
    val stamps: List<TrailStamp>,
    val nearby: List<Place>,
    val embassies: List<Place>,
    val partners: List<Place>,
    val squad: List<SquadMember>,
    val ticket: Ticket,
    val tickets: List<Ticket> = emptyList(),
    val prediction: Prediction,
    val payToken: String,
    val language: String,
    val safetyAlert: String?,
    val safetyAlertDismissed: Boolean,
    val reels: List<ReelClip>,
    val shots: List<Shot> = emptyList(),
    val highlights: List<HighlightClip>,
    val destinations: List<Destination>,
    val events: List<FestivalEvent>,
    val sports: List<SportProfile> = emptyList(),
    val coaches: List<Coach> = emptyList(),
    val players: List<Player> = emptyList(),
    val ads: List<PromoAd> = emptyList(),
    val merch: List<MerchItem> = emptyList(),
    val crestedBand: CrestedBand,
    val loyalty: Loyalty
)

fun Int.asUgx(): String {
    val formatted = NumberFormat.getIntegerInstance(Locale.US).format(kotlin.math.abs(this))
    val sign = if (this < 0) "−" else ""
    return "${sign}UGX $formatted"
}

fun Int.asCountdown(): String {
    val days = this / 24
    val hours = this % 24
    return "${days}d ${hours}h"
}

fun SquadStatus.label(): String = when (this) {
    SquadStatus.AT_VENUE -> "At venue"
    SquadStatus.EN_ROUTE -> "En route"
    SquadStatus.PAYMENT_PENDING -> "Payment pending"
}

fun PlaceKind.label(): String = when (this) {
    PlaceKind.EMBASSY -> "Fan embassy"
    PlaceKind.PARTNER -> "Trail partner"
}
