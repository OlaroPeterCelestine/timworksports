import Combine
import Foundation

final class EmbassyViewModel: ObservableObject {
    @Published var snapshot: AppSnapshot
    private let repository: CrestedPassRepository

    init(repository: CrestedPassRepository) {
        self.repository = repository
        snapshot = repository.current
        repository.publisher
            .receive(on: DispatchQueue.main)
            .assign(to: &$snapshot)
    }

    func settle(memberId: String) {
        repository.settle(memberId: memberId)
    }
}
