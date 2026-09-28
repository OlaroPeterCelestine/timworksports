import Combine
import Foundation

final class TicketViewModel: ObservableObject {
    @Published var snapshot: AppSnapshot
    private let repository: CrestedPassRepository

    init(repository: CrestedPassRepository) {
        self.repository = repository
        snapshot = repository.current
        repository.publisher
            .receive(on: DispatchQueue.main)
            .assign(to: &$snapshot)
    }

    func dismissAlert() {
        repository.dismissSafetyAlert()
    }

    func buy(_ eventId: String) {
        repository.buyTicket(eventId: eventId)
    }

    func topUp(_ amount: Int) {
        repository.topUp(amount: amount)
    }
}
