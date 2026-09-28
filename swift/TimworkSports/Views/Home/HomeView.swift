import SwiftUI

struct HomeView: View {
    @StateObject private var vm: HomeViewModel
    @State private var storyStart: Int?
    @State private var seen: Set<String> = []
    private let repository: CrestedPassRepository
    var onSelectTab: (AppTab) -> Void

    init(repository: CrestedPassRepository, onSelectTab: @escaping (AppTab) -> Void) {
        self.repository = repository
        _vm = StateObject(wrappedValue: HomeViewModel(repository: repository))
        self.onSelectTab = onSelectTab
    }

    var body: some View {
        VStack(spacing: 0) {
            HStack(alignment: .center, spacing: 12) {
                VStack(alignment: .leading, spacing: 2) {
                    Text("Timwork Sports")
                        .font(.system(size: 22, weight: .semibold))
                        .foregroundStyle(Color.brandInk)
                    Text("Upcoming sports · buy tickets")
                        .font(.system(size: 14))
                        .foregroundStyle(Color.brandMuted)
                }
                Spacer()
                NavigationLink {
                    TicketDetailView(repository: repository)
                } label: {
                    Text("Ticket")
                        .font(.system(size: 14, weight: .semibold))
                        .foregroundStyle(Color.brandInk)
                }
                Button { onSelectTab(.you) } label: {
                    AvatarView(
                        key: vm.snapshot.user.name,
                        initials: vm.snapshot.user.initials,
                        size: 32
                    )
                }
                .accessibilityLabel("Profile")
            }
            .padding(.horizontal, 20)
            .padding(.top, 8)
            .padding(.bottom, 10)
            .background(Color.brandCanvas)

            ScrollView {
                VStack(spacing: 0) {
                    StoryCirclesRow(
                        userName: vm.snapshot.user.name,
                        reels: vm.snapshot.reels,
                        seen: seen
                    ) { index, id in
                        seen.insert(id)
                        storyStart = index
                    }

                    NavigationLink {
                        PredictView(repository: repository)
                    } label: {
                        HStack(spacing: 10) {
                            Image(systemName: "sportscourt")
                                .font(.system(size: 14, weight: .semibold))
                                .foregroundStyle(Color.brandGold)
                            Text("\(vm.snapshot.nextMatch.home) vs \(vm.snapshot.nextMatch.away)")
                                .font(.system(size: 14, weight: .semibold))
                                .foregroundStyle(Color.brandInk)
                            Spacer()
                            Text(vm.snapshot.nextMatch.kickoffInHours.countdown)
                                .font(.brandSans(12, weight: .medium))
                                .foregroundStyle(Color.brandMuted)
                            Text("Predict")
                                .font(.brandSans(12, weight: .semibold))
                                .foregroundStyle(Color.brandGold)
                        }
                        .padding(.horizontal, 14)
                        .padding(.vertical, 12)
                        .background(Color.brandCard)
                        .clipShape(RoundedRectangle(cornerRadius: 12, style: .continuous))
                        .overlay(
                            RoundedRectangle(cornerRadius: 12, style: .continuous)
                                .stroke(Color.brandBorder, lineWidth: 1)
                        )
                    }
                    .buttonStyle(.plain)
                    .padding(.horizontal, 14)
                    .padding(.bottom, 6)

                    ForEach(vm.snapshot.shots) { shot in
                        ShotPost(shot: shot) {
                            vm.toggleShotLike(shot.id)
                        }
                    }
                }
            }
        }
        .background(Color.brandCanvas.ignoresSafeArea())
        .toolbar(.hidden, for: .navigationBar)
        .navigationBarTitleDisplayMode(.inline)
        .fullScreenCover(isPresented: Binding(
            get: { storyStart != nil },
            set: { if !$0 { storyStart = nil } }
        )) {
            ZStack(alignment: .topTrailing) {
                ReelsFeedView(
                    reels: vm.snapshot.reels,
                    destinations: vm.snapshot.destinations,
                    startIndex: storyStart ?? 0,
                    onLike: vm.toggleLike,
                    addToTrail: vm.addToTrail
                )
                .ignoresSafeArea()
                Button {
                    storyStart = nil
                } label: {
                    Image(systemName: "xmark")
                        .font(.system(size: 16, weight: .semibold))
                        .foregroundStyle(.white)
                        .padding(18)
                }
                .accessibilityLabel("Close")
            }
            .background(Color.black.ignoresSafeArea())
        }
    }
}

