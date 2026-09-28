import Combine
import Foundation

final class HomeViewModel: ObservableObject {
    @Published var snapshot: AppSnapshot
    private let repository: CrestedPassRepository

    init(repository: CrestedPassRepository) {
        self.repository = repository
        snapshot = repository.current
        repository.publisher
            .receive(on: DispatchQueue.main)
            .assign(to: &$snapshot)
    }

    func toggleLanguage() {
        repository.toggleLanguage()
    }

    func toggleLike(_ id: String) {
        repository.toggleReelLike(id)
    }

    func toggleShotLike(_ id: String) {
        repository.toggleShotLike(id)
    }

    func addToTrail(_ id: String) {
        repository.addDestinationToTrail(id)
    }

    func owns(_ eventId: String) -> Bool {
        snapshot.tickets.contains { $0.eventId == eventId }
    }
}
