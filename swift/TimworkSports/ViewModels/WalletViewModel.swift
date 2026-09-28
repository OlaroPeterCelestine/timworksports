import Combine
import Foundation

final class WalletViewModel: ObservableObject {
    @Published var snapshot: AppSnapshot
    @Published var showTopUp = false
    @Published var confirmedAmount: Int?
    private let repository: CrestedPassRepository

    init(repository: CrestedPassRepository) {
        self.repository = repository
        snapshot = repository.current
        repository.publisher
            .receive(on: DispatchQueue.main)
            .assign(to: &$snapshot)
    }

    func openTopUp() {
        confirmedAmount = nil
        showTopUp = true
    }

    func topUp(_ amount: Int) {
        repository.topUp(amount: amount)
        confirmedAmount = amount
        showTopUp = false
    }
}
