import Combine
import Foundation

final class BandViewModel: ObservableObject {
    @Published var snapshot: AppSnapshot
    @Published var lastTap: String?
    private let repository: CrestedPassRepository

    init(repository: CrestedPassRepository) {
        self.repository = repository
        snapshot = repository.current
        repository.publisher
            .receive(on: DispatchQueue.main)
            .assign(to: &$snapshot)
    }

    func simulateTap() {
        repository.simulateBandTap()
        let snap = repository.current
        lastTap = snap.crestedBand.status == "active"
            ? "Read \(snap.crestedBand.bandId) · wallet \(snap.user.walletBalance.ugx)"
            : "Band \(snap.crestedBand.bandId) is frozen"
    }

    func freeze() {
        repository.freezeBand()
    }

    func reissue() {
        repository.reissueBand()
        lastTap = "Reissued \(repository.current.crestedBand.bandId)"
    }
}
