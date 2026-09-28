import Combine
import Foundation

final class PredictViewModel: ObservableObject {
    @Published var snapshot: AppSnapshot
    @Published var homeScore = 1
    @Published var awayScore = 1
    private let repository: CrestedPassRepository

    init(repository: CrestedPassRepository) {
        self.repository = repository
        snapshot = repository.current
        repository.publisher
            .receive(on: DispatchQueue.main)
            .assign(to: &$snapshot)
    }

    func bumpHome(_ delta: Int) {
        homeScore = min(9, max(0, homeScore + delta))
    }

    func bumpAway(_ delta: Int) {
        awayScore = min(9, max(0, awayScore + delta))
    }

    func submit() {
        repository.submitPrediction(homeScore: homeScore, awayScore: awayScore)
    }
}
