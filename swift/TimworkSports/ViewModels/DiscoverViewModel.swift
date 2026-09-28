import Combine
import Foundation

final class DiscoverViewModel: ObservableObject {
    @Published var snapshot: AppSnapshot
    private let repository: CrestedPassRepository

    init(repository: CrestedPassRepository) {
        self.repository = repository
        snapshot = repository.current
        repository.publisher
            .receive(on: DispatchQueue.main)
            .assign(to: &$snapshot)
    }

    func toggleLike(_ id: String) {
        repository.toggleReelLike(id)
    }

    func addToTrail(_ destinationId: String) {
        repository.addDestinationToTrail(destinationId)
    }
}
