import Combine
import Foundation

final class ProfileViewModel: ObservableObject {
    @Published var snapshot: AppSnapshot
    private let repository: CrestedPassRepository

    init(repository: CrestedPassRepository) {
        self.repository = repository
        snapshot = repository.current
        repository.publisher
            .receive(on: DispatchQueue.main)
            .assign(to: &$snapshot)
    }

    var points: Int {
        snapshot.leaderboard.first(where: { $0.name == snapshot.user.name })?.points ?? 0
    }

    var myShots: [Shot] {
        snapshot.shots.filter { $0.author == snapshot.user.name }
    }

    var myPostImages: [(id: String, imageName: String)] {
        let shots = myShots.map { (id: $0.id, imageName: $0.imageName) }
        let reels = snapshot.reels
            .filter { $0.author == snapshot.user.name }
            .map { (id: $0.id, imageName: $0.imageName) }
        return shots + reels
    }

    func toggleLanguage() {
        repository.toggleLanguage()
    }

    func freezeBand() {
        repository.freezeBand()
    }

    func toggleShotLike(_ id: String) {
        repository.toggleShotLike(id)
    }
}
