import Combine
import Foundation

protocol CrestedPassRepository: AnyObject {
    var current: AppSnapshot { get }
    var publisher: AnyPublisher<AppSnapshot, Never> { get }
    func topUp(amount: Int)
    func submitPrediction(homeScore: Int, awayScore: Int)
    func settle(memberId: String)
    func dismissSafetyAlert()
    func collectNextStamp()
    func toggleLanguage()
    func toggleReelLike(_ id: String)
    func toggleShotLike(_ id: String)
    func addDestinationToTrail(_ id: String)
    func freezeBand()
    func reissueBand()
    func simulateBandTap()
    func buyTicket(eventId: String)
}
