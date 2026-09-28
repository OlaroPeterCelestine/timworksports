import Combine
import Foundation

final class TrailViewModel: ObservableObject {
    @Published var snapshot: AppSnapshot
    private let repository: CrestedPassRepository

    init(repository: CrestedPassRepository) {
        self.repository = repository
        snapshot = repository.current
        repository.publisher
            .receive(on: DispatchQueue.main)
            .assign(to: &$snapshot)
    }

    func collectNext() {
        repository.collectNextStamp()
    }
}
