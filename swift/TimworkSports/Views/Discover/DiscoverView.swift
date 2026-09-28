import SwiftUI

enum DiscoverPane: String, CaseIterable, Identifiable {
    case events = "Events"
    case coaches = "Coaches"
    case rankings = "Rankings"
    case ads = "Ads"
    case sports = "Sports"
    case highlights = "Highlights"
    case reels = "Reels"
    case destinations = "Destinations"
    var id: String { rawValue }
}

struct DiscoverView: View {
    @StateObject private var vm: DiscoverViewModel
    @State private var pane: DiscoverPane = .events
    @State private var city = "All"
    @State private var playerSport = "All"
    @State private var scrollY: CGFloat = 0
    @State private var pagingOffset: CGFloat = 0
    @State private var titleHeight: CGFloat = 56
    @State private var chromeHeight: CGFloat = 108
    @State private var overlayHeight: CGFloat = 64
    private let repository: CrestedPassRepository

    init(repository: CrestedPassRepository) {
        self.repository = repository
        _vm = StateObject(wrappedValue: DiscoverViewModel(repository: repository))
    }

    private var cities: [String] {
        ["All"] + Array(Set(vm.snapshot.events.map(\.city))).sorted()
    }

    private var playerSports: [String] {
        ["All"] + vm.snapshot.sports.map(\.name)
    }

    private var chromeCollapse: CGFloat {
        if pane == .reels {
            return min(max(pagingOffset * 0.18, 0), 52)
        }
        return min(max(scrollY, 0), max(titleHeight, 0))
    }

    var body: some View {
        ZStack(alignment: .top) {
            Group {
                switch pane {
                case .sports:
                    SportsListView(
                        sports: vm.snapshot.sports,
                        coaches: vm.snapshot.coaches,
                        players: vm.snapshot.players,
                        events: vm.snapshot.events,
                        highlights: vm.snapshot.highlights,
                        repository: repository,
                        topInset: chromeHeight,
                        onScroll: { scrollY = $0 }
                    )
                case .coaches:
                    CoachesListView(coaches: vm.snapshot.coaches, ad: vm.snapshot.ads.dropFirst().first, topInset: chromeHeight, onScroll: { scrollY = $0 })
                case .rankings:
                    RankingsListView(
                        players: vm.snapshot.players,
                        sports: vm.snapshot.sports.map(\.name),
                        sport: playerSport,
                        ad: vm.snapshot.ads.dropFirst(2).first,
                        topInset: chromeHeight,
                        onScroll: { scrollY = $0 }
                    )
                case .ads:
                    AdsListView(ads: vm.snapshot.ads, topInset: chromeHeight, onScroll: { scrollY = $0 })
                case .reels:
                    ReelsFeedView(
                        reels: vm.snapshot.reels,
                        destinations: vm.snapshot.destinations,
                        onLike: vm.toggleLike,
                        addToTrail: { destId in
                            vm.addToTrail(destId)
                            pane = .destinations
                        },
                        onPagingOffset: { pagingOffset = $0 },
                        topGutter: overlayHeight > 0 ? overlayHeight + 10 : 56
                    )
                case .highlights:
                    HighlightsGridView(
                        clips: vm.snapshot.highlights,
                        topInset: chromeHeight,
                        onScroll: { scrollY = $0 }
                    )
                case .destinations:
                    DestinationsGridView(
                        destinations: vm.snapshot.destinations,
                        onAdd: vm.addToTrail,
                        topInset: chromeHeight,
                        onScroll: { scrollY = $0 }
                    )
                case .events:
                    EventsListView(
                        events: vm.snapshot.events.filter { city == "All" || $0.city == city },
                        tickets: vm.snapshot.tickets,
                        ad: vm.snapshot.ads.first,
                        repository: repository,
                        topInset: chromeHeight,
                        onScroll: { scrollY = $0 }
                    )
                }
            }

            DiscoverChrome(
                pane: $pane,
                city: $city,
                cities: cities,
                playerSport: $playerSport,
                playerSports: playerSports,
                overReels: pane == .reels,
                collapse: chromeCollapse
            )
            .zIndex(1)
            .onPreferenceChange(DiscoverTitleHeightKey.self) { titleHeight = $0 }
            .onPreferenceChange(DiscoverChromeSizeKey.self) { size in
                guard size.height > 0 else { return }
                overlayHeight = size.height
                if pane != .reels {
                    chromeHeight = size.height
                }
            }
        }
        .background(pane == .reels ? Color.black.ignoresSafeArea() : Color.brandCanvas.ignoresSafeArea())
        .toolbar(.hidden, for: .navigationBar)
        .onChange(of: pane, perform: { _ in
            scrollY = 0
            pagingOffset = 0
        })
    }
}

private enum DiscoverScrollOffsetKey: PreferenceKey {
    static var defaultValue: CGFloat = 0
    static func reduce(value: inout CGFloat, nextValue: () -> CGFloat) {
        value = nextValue()
    }
}

private enum DiscoverPagingOffsetKey: PreferenceKey {
    static var defaultValue: CGFloat = .infinity
    static func reduce(value: inout CGFloat, nextValue: () -> CGFloat) {
        value = min(value, nextValue())
    }
}

private enum DiscoverTitleHeightKey: PreferenceKey {
    static var defaultValue: CGFloat = 56
    static func reduce(value: inout CGFloat, nextValue: () -> CGFloat) {
        value = nextValue()
    }
}

private enum DiscoverChromeSizeKey: PreferenceKey {
    static var defaultValue: CGSize = CGSize(width: 0, height: 108)
    static func reduce(value: inout CGSize, nextValue: () -> CGSize) {
        value = nextValue()
    }
}

private struct DiscoverChrome: View {
    @Binding var pane: DiscoverPane
    @Binding var city: String
    var cities: [String]
    @Binding var playerSport: String
    var playerSports: [String]
    var overReels: Bool
    var collapse: CGFloat

    var body: some View {
        VStack(alignment: .leading, spacing: 10) {
            if !overReels {
                ScreenHeader(title: "Discover", subtitle: "Events, coaches, rankings, ads")
                    .background(
                        GeometryReader { geo in
                            Color.clear.preference(key: DiscoverTitleHeightKey.self, value: geo.size.height + 12)
                        }
                    )
            }
            chipRow(titles: DiscoverPane.allCases.map(\.rawValue), selected: pane.rawValue, onDark: overReels) { title in
                if let next = DiscoverPane(rawValue: title) { pane = next }
            }
            if pane == .rankings {
                chipRow(titles: playerSports, selected: playerSport, onDark: false) { playerSport = $0 }
            }
            if pane == .events {
                chipRow(titles: cities, selected: city, onDark: false) { city = $0 }
            }
        }
        .padding(.horizontal, 16)
        .padding(.top, 10)
        .padding(.bottom, 10)
        .frame(maxWidth: .infinity, alignment: .leading)
        .background { chromeBackground }
        .background(
            GeometryReader { geo in
                Color.clear.preference(key: DiscoverChromeSizeKey.self, value: geo.size)
            }
        )
        .offset(y: -collapse)
        .animation(.easeInOut(duration: 0.2), value: pane)
    }

    private func chipRow(titles: [String], selected: String, onDark: Bool, onSelect: @escaping (String) -> Void) -> some View {
        ScrollView(.horizontal, showsIndicators: false) {
            HStack(spacing: 8) {
                ForEach(titles, id: \.self) { title in
                    FilterChip(title: title, selected: selected == title, onDark: onDark) {
                        onSelect(title)
                    }
                }
            }
            .padding(.vertical, 2)
        }
    }

    @ViewBuilder
    private var chromeBackground: some View {
        if overReels {
            LinearGradient(
                colors: [.black.opacity(0.55), .black.opacity(0.18), .clear],
                startPoint: .top,
                endPoint: .bottom
            )
        } else {
            Color.brandCanvas
        }
    }
}

private struct DiscoverScroll<Content: View>: View {
    var topInset: CGFloat
    var onScroll: (CGFloat) -> Void
    @ViewBuilder var content: Content

    var body: some View {
        ScrollView {
            VStack(spacing: 0) {
                GeometryReader { geo in
                    Color.clear.preference(
                        key: DiscoverScrollOffsetKey.self,
                        value: -geo.frame(in: .named("discoverScroll")).minY
                    )
                }
                .frame(height: 0)
                Color.clear.frame(height: topInset)
                content
            }
        }
        .coordinateSpace(name: "discoverScroll")
        .scrollIndicators(.hidden)
        .onPreferenceChange(DiscoverScrollOffsetKey.self, perform: onScroll)
    }
}

struct ReelsFeedView: View {
    let reels: [ReelClip]
    let destinations: [Destination]
    var startIndex: Int = 0
    var onLike: (String) -> Void
    var addToTrail: (String) -> Void
    var onPagingOffset: (CGFloat) -> Void = { _ in }
    var topGutter: CGFloat = 56

    var body: some View {
        GeometryReader { geo in
            Group {
                if #available(iOS 17.0, *) {
                    ScrollViewReader { proxy in
                        ScrollView(.vertical) {
                            LazyVStack(spacing: 0) {
                                ForEach(reels) { reel in
                                    reelPage(reel)
                                        .frame(width: geo.size.width, height: geo.size.height)
                                        .background {
                                            GeometryReader { page in
                                                Color.clear.preference(
                                                    key: DiscoverPagingOffsetKey.self,
                                                    value: abs(page.frame(in: .named("discoverReels")).minY)
                                                )
                                            }
                                        }
                                        .id(reel.id)
                                }
                            }
                            .scrollTargetLayout()
                        }
                        .scrollTargetBehavior(.paging)
                        .scrollIndicators(.hidden)
                        .coordinateSpace(name: "discoverReels")
                        .onPreferenceChange(DiscoverPagingOffsetKey.self) { value in
                            onPagingOffset(value == .infinity ? 0 : value)
                        }
                        .onAppear {
                            guard startIndex > 0, startIndex < reels.count else { return }
                            proxy.scrollTo(reels[startIndex].id, anchor: .top)
                        }
                    }
                } else {
                    TabView {
                        ForEach(Array(reels.enumerated()), id: \.element.id) { index, reel in
                            reelPage(reel).tag(index)
                        }
                    }
                    .tabViewStyle(.page(indexDisplayMode: .never))
                }
            }
        }
    }

    private func reelPage(_ reel: ReelClip) -> some View {
        ReelPageView(
            reel: reel,
            destination: destinations.first { $0.id == reel.destinationId },
            onLike: { onLike(reel.id) },
            addToTrail: addToTrail,
            topGutter: topGutter
        )
    }
}

struct ReelPageView: View {
    let reel: ReelClip
    let destination: Destination?
    var onLike: () -> Void
    var addToTrail: (String) -> Void
    var topGutter: CGFloat = 56
    @State private var muted = true

    var body: some View {
        ZStack(alignment: .bottomLeading) {
            Image(reel.imageName)
                .resizable()
                .scaledToFill()
                .frame(maxWidth: .infinity, maxHeight: .infinity)
                .clipped()

            LinearGradient(
                colors: [.clear, .black.opacity(0.7)],
                startPoint: .center,
                endPoint: .bottom
            )

            HStack(spacing: 10) {
                Circle()
                    .fill(Color.brandGold)
                    .frame(width: 36, height: 36)
                    .overlay {
                        Text(reel.initials)
                            .font(.system(size: 12, weight: .semibold))
                            .foregroundStyle(.black)
                    }
                VStack(alignment: .leading, spacing: 1) {
                    Text(reel.author)
                        .font(.system(size: 14, weight: .semibold))
                        .foregroundStyle(.white)
                    Text(reel.handle)
                        .font(.system(size: 12))
                        .foregroundStyle(.white.opacity(0.75))
                }
                Spacer()
            }
            .padding(.horizontal, 16)
            .padding(.top, topGutter)
            .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .topLeading)

            HStack(alignment: .bottom, spacing: 16) {
                VStack(alignment: .leading, spacing: 8) {
                    Text(reel.type.capitalized)
                        .font(.system(size: 12, weight: .medium))
                        .foregroundStyle(.white.opacity(0.8))
                    Text(reel.title)
                        .font(.system(size: 18, weight: .semibold))
                        .foregroundStyle(.white)
                        .lineLimit(3)
                    if let destination {
                        Button("Add to trail") { addToTrail(destination.id) }
                            .font(.system(size: 14, weight: .semibold))
                            .foregroundStyle(.white)
                            .padding(.top, 4)
                    }
                }
                Spacer()
                VStack(spacing: 18) {
                    Button(action: onLike) {
                        Image(systemName: reel.liked ? "heart.fill" : "heart")
                            .font(.system(size: 22))
                            .foregroundStyle(reel.liked ? Color.brandRed : .white)
                    }
                    Image(systemName: "square.and.arrow.up")
                        .font(.system(size: 20))
                        .foregroundStyle(.white)
                    Image(systemName: muted ? "speaker.slash" : "speaker.wave.2")
                        .font(.system(size: 20))
                        .foregroundStyle(.white)
                }
            }
            .padding(20)
        }
        .contentShape(Rectangle())
        .onTapGesture { muted.toggle() }
        .clipped()
    }
}

struct HighlightsGridView: View {
    let clips: [HighlightClip]
    var topInset: CGFloat = 0
    var onScroll: (CGFloat) -> Void = { _ in }
    @State private var playing: HighlightClip?

    var body: some View {
        DiscoverScroll(topInset: topInset, onScroll: onScroll) {
            LazyVGrid(columns: [GridItem(.flexible(), spacing: 12), GridItem(.flexible(), spacing: 12)], spacing: 12) {
                ForEach(clips) { clip in
                    Button { playing = clip } label: {
                        VStack(alignment: .leading, spacing: 8) {
                            ZStack {
                                Image(clip.imageName)
                                    .resizable()
                                    .scaledToFill()
                                    .frame(height: 110)
                                    .clipped()
                                Circle()
                                    .fill(Color.brandNavy.opacity(0.55))
                                    .frame(width: 36, height: 36)
                                Image(systemName: "play.fill")
                                    .foregroundStyle(Color.brandGold)
                                    .font(.system(size: 13))
                            }
                            .clipShape(RoundedRectangle(cornerRadius: 12, style: .continuous))
                            GoldLabel(text: clip.sport)
                            Text(clip.title)
                                .font(.brandSans(13, weight: .medium))
                                .foregroundStyle(Color.brandInk)
                                .multilineTextAlignment(.leading)
                        }
                    }
                    .buttonStyle(.plain)
                }
            }
            .padding(.horizontal, 20)
            .padding(.bottom, 24)
        }
        .fullScreenCover(item: $playing) { clip in
            HighlightPlayerView(clip: clip) { playing = nil }
        }
    }
}

struct HighlightPlayerView: View {
    let clip: HighlightClip
    var onClose: () -> Void
    @State private var muted = true

    var body: some View {
        ZStack(alignment: .topLeading) {
            ReelPageView(
                reel: ReelClip(id: clip.id, type: "highlight", title: clip.title, videoUrl: clip.videoUrl, imageName: clip.imageName),
                destination: nil,
                onLike: {},
                addToTrail: { _ in }
            )
            Button(action: onClose) {
                Image(systemName: "xmark")
                    .foregroundStyle(.white)
                    .padding(12)
                    .background(Color.brandNavy.opacity(0.55))
                    .clipShape(Circle())
            }
            .padding(16)
        }
        .ignoresSafeArea()
        .onTapGesture { muted.toggle() }
    }
}

struct DestinationsGridView: View {
    let destinations: [Destination]
    var onAdd: (String) -> Void
    var topInset: CGFloat = 0
    var onScroll: (CGFloat) -> Void = { _ in }

    var body: some View {
        DiscoverScroll(topInset: topInset, onScroll: onScroll) {
            LazyVGrid(columns: [GridItem(.flexible(), spacing: 12), GridItem(.flexible(), spacing: 12)], spacing: 12) {
                ForEach(destinations) { item in
                    NavigationLink {
                        DestinationDetailView(destination: item, onAdd: { onAdd(item.id) })
                    } label: {
                        VStack(alignment: .leading, spacing: 8) {
                            Image(item.imageName)
                                .resizable()
                                .scaledToFill()
                                .frame(height: 118)
                                .clipped()
                                .clipShape(RoundedRectangle(cornerRadius: 12, style: .continuous))
                            GoldLabel(text: item.category)
                            Text(item.name)
                                .font(.brandSans(14, weight: .semibold))
                                .foregroundStyle(Color.brandInk)
                                .multilineTextAlignment(.leading)
                        }
                    }
                    .buttonStyle(.plain)
                }
            }
            .padding(.horizontal, 20)
            .padding(.bottom, 24)
        }
    }
}

struct DestinationDetailView: View {
    let destination: Destination
    var onAdd: () -> Void

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 16) {
                Image(destination.imageName)
                    .resizable()
                    .scaledToFill()
                    .frame(height: 220)
                    .clipped()
                    .clipShape(RoundedRectangle(cornerRadius: 14, style: .continuous))
                GoldLabel(text: destination.category)
                SerifTitle(text: destination.name, size: 28)
                CaptionText(text: destination.blurb)
                if destination.trailEligible {
                    PrimaryButton(title: "Add to Trail", gold: true, action: onAdd)
                }
                PrimaryButton(title: "Book this", action: {})
            }
            .padding(20)
        }
        .background(Color.brandCream.ignoresSafeArea())
        .navigationBarTitleDisplayMode(.inline)
    }
}

struct EventsListView: View {
    let events: [FestivalEvent]
    var tickets: [Ticket] = []
    var ad: PromoAd? = nil
    var repository: CrestedPassRepository? = nil
    var topInset: CGFloat = 0
    var onScroll: (CGFloat) -> Void = { _ in }

    var body: some View {
        DiscoverScroll(topInset: topInset, onScroll: onScroll) {
            VStack(alignment: .leading, spacing: 12) {
                if let ad {
                    AdBanner(ad: ad)
                }
                ForEach(events.sorted { $0.kickoffInHours < $1.kickoffInHours }) { event in
                    let owned = tickets.contains { $0.eventId == event.id }
                    Group {
                        if let repository {
                            NavigationLink {
                                TicketDetailView(repository: repository, eventId: event.id)
                            } label: {
                                eventRow(event, owned: owned)
                            }
                            .buttonStyle(.plain)
                        } else {
                            eventRow(event, owned: owned)
                        }
                    }
                }
            }
            .padding(.horizontal, 20)
            .padding(.bottom, 24)
        }
    }

    private func eventRow(_ event: FestivalEvent, owned: Bool) -> some View {
        BrandCard {
            VStack(alignment: .leading, spacing: 10) {
                SportCover(event: event)
                    .frame(maxWidth: .infinity)
                    .frame(height: 148)
                    .clipShape(RoundedRectangle(cornerRadius: 10, style: .continuous))
                VStack(alignment: .leading, spacing: 4) {
                    GoldLabel(text: event.sport)
                    Text(event.title)
                        .font(.brandSans(16, weight: .semibold))
                        .foregroundStyle(Color.brandInk)
                    CaptionText(text: "\(event.whenLabel) · \(event.city)")
                    HStack {
                        Text(event.price.ugx)
                            .font(.brandSans(13, weight: .semibold))
                            .foregroundStyle(Color.brandInk)
                        Spacer()
                        Text(owned ? "View ticket" : "Buy ticket")
                            .font(.brandSans(12, weight: .semibold))
                            .foregroundStyle(owned ? Color.brandMuted : Color.brandNavy)
                    }
                }
            }
        }
    }
}

struct SportsListView: View {
    let sports: [SportProfile]
    var coaches: [Coach] = []
    var players: [Player] = []
    var events: [FestivalEvent] = []
    var highlights: [HighlightClip] = []
    var repository: CrestedPassRepository? = nil
    var topInset: CGFloat = 0
    var onScroll: (CGFloat) -> Void = { _ in }

    var body: some View {
        DiscoverScroll(topInset: topInset, onScroll: onScroll) {
            VStack(spacing: 12) {
                ForEach(sports) { sport in
                    NavigationLink {
                        SportDetailView(
                            sport: sport,
                            coaches: coaches.filter { $0.sport == sport.name },
                            players: players.filter { $0.sport == sport.name },
                            events: events.filter { $0.sport == sport.name },
                            highlights: highlights.filter { $0.sport == sport.name },
                            repository: repository
                        )
                    } label: {
                        BrandCard {
                            HStack(spacing: 12) {
                                Image(sport.imageName)
                                    .resizable()
                                    .scaledToFill()
                                    .frame(width: 72, height: 72)
                                    .clipped()
                                    .clipShape(RoundedRectangle(cornerRadius: 10, style: .continuous))
                                VStack(alignment: .leading, spacing: 4) {
                                    Text(sport.name)
                                        .font(.system(size: 18, weight: .semibold))
                                        .foregroundStyle(Color.brandInk)
                                    CaptionText(text: sport.federation)
                                    CaptionText(text: "\(sport.athletes) athletes · \(sport.city)")
                                }
                            }
                        }
                    }
                    .buttonStyle(.plain)
                }
            }
            .padding(.horizontal, 20)
            .padding(.bottom, 28)
        }
    }
}

struct SportDetailView: View {
    let sport: SportProfile
    let coaches: [Coach]
    var players: [Player] = []
    let events: [FestivalEvent]
    let highlights: [HighlightClip]
    var repository: CrestedPassRepository?

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 12) {
                Image(sport.imageName)
                    .resizable()
                    .scaledToFill()
                    .frame(height: 180)
                    .clipped()
                    .clipShape(RoundedRectangle(cornerRadius: 14, style: .continuous))
                SerifTitle(text: sport.name, size: 28)
                CaptionText(text: sport.federation)
                CaptionText(text: sport.blurb)
                GoldLabel(text: "Coaches")
                ForEach(coaches) { person in
                    Text("\(person.name) · \(person.role)")
                        .font(.brandSans(15, weight: .semibold))
                        .foregroundStyle(Color.brandInk)
                }
                GoldLabel(text: "Players")
                ForEach(players) { athlete in
                    NavigationLink {
                        PlayerDetailView(player: athlete)
                    } label: {
                        VStack(alignment: .leading, spacing: 2) {
                            Text("\(athlete.name) · \(athlete.position)")
                                .font(.brandSans(15, weight: .semibold))
                                .foregroundStyle(Color.brandInk)
                            CaptionText(text: "\(athlete.club) · #\(athlete.number)")
                        }
                    }
                    .buttonStyle(.plain)
                }
                GoldLabel(text: "Events")
                ForEach(events.sorted { $0.kickoffInHours < $1.kickoffInHours }) { event in
                    if let repository {
                        NavigationLink {
                            TicketDetailView(repository: repository, eventId: event.id)
                        } label: {
                            eventLine(event)
                        }
                        .buttonStyle(.plain)
                    } else {
                        eventLine(event)
                    }
                }
                GoldLabel(text: "Highlights")
                ForEach(highlights) { clip in
                    VStack(alignment: .leading, spacing: 2) {
                        Text(clip.title)
                            .font(.brandSans(15, weight: .semibold))
                            .foregroundStyle(Color.brandInk)
                        CaptionText(text: clip.match)
                    }
                }
            }
            .padding(20)
        }
        .background(Color.brandCanvas.ignoresSafeArea())
        .navigationTitle(sport.name)
        .navigationBarTitleDisplayMode(.inline)
    }

    private func eventLine(_ event: FestivalEvent) -> some View {
        VStack(alignment: .leading, spacing: 2) {
            Text(event.title)
                .font(.brandSans(15, weight: .semibold))
                .foregroundStyle(Color.brandInk)
            CaptionText(text: "\(event.whenLabel) · \(event.venue)")
        }
    }
}

struct RankingsListView: View {
    let players: [Player]
    var sports: [String] = []
    var sport: String = "All"
    var ad: PromoAd? = nil
    var topInset: CGFloat = 0
    var onScroll: (CGFloat) -> Void = { _ in }

    private var sections: [String] {
        sports.filter { sport == "All" || $0 == sport }
    }

    var body: some View {
        DiscoverScroll(topInset: topInset, onScroll: onScroll) {
            VStack(alignment: .leading, spacing: 12) {
                if let ad {
                    AdBanner(ad: ad)
                }
                ForEach(sections, id: \.self) { name in
                    let ranked = players
                        .filter { $0.sport == name }
                        .sorted { lhs, rhs in
                            if lhs.scoring == rhs.scoring { return lhs.name < rhs.name }
                            return lhs.scoring > rhs.scoring
                        }
                    if !ranked.isEmpty {
                        GoldLabel(text: name)
                        rankingRows(ranked)
                    }
                }
            }
            .padding(.horizontal, 20)
            .padding(.bottom, 28)
        }
    }

    @ViewBuilder
    private func rankingRows(_ ranked: [Player]) -> some View {
        ForEach(Array(ranked.enumerated()), id: \.element.id) { index, athlete in
                    NavigationLink {
                        PlayerDetailView(player: athlete)
                    } label: {
                        BrandCard {
                            HStack(spacing: 12) {
                                Text("\(index + 1)")
                                    .font(.brandSans(18, weight: .bold))
                                    .foregroundStyle(index < 3 ? Color.brandGold : Color.brandMuted)
                                    .frame(width: 28, alignment: .leading)
                                Image(athlete.imageName)
                                    .resizable()
                                    .scaledToFill()
                                    .frame(width: 56, height: 56)
                                    .clipped()
                                    .clipShape(RoundedRectangle(cornerRadius: 8, style: .continuous))
                                VStack(alignment: .leading, spacing: 4) {
                                    Text(athlete.name)
                                        .font(.brandSans(16, weight: .semibold))
                                        .foregroundStyle(Color.brandInk)
                                    CaptionText(text: "\(athlete.club) · \(athlete.position)")
                                }
                                Spacer()
                                VStack(alignment: .trailing, spacing: 2) {
                                    Text("\(athlete.scoring)")
                                        .font(.brandSans(16, weight: .bold))
                                        .foregroundStyle(Color.brandInk)
                                    CaptionText(text: athlete.scoringLabel)
                                }
                            }
                        }
                    }
                    .buttonStyle(.plain)
        }
    }
}

struct AdBanner: View {
    let ad: PromoAd

    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            Image(ad.imageName)
                .resizable()
                .scaledToFill()
                .frame(maxWidth: .infinity)
                .frame(height: 140)
                .clipped()
            VStack(alignment: .leading, spacing: 4) {
                GoldLabel(text: "Sponsored · \(ad.sponsor)")
                Text(ad.headline)
                    .font(.brandSans(16, weight: .semibold))
                    .foregroundStyle(Color.brandInk)
                CaptionText(text: ad.detail)
            }
            .padding(.horizontal, 12)
            .padding(.bottom, 12)
        }
        .background(Color.brandCard)
        .clipShape(RoundedRectangle(cornerRadius: 14, style: .continuous))
        .overlay(
            RoundedRectangle(cornerRadius: 14, style: .continuous)
                .stroke(Color.brandBorder, lineWidth: 1)
        )
    }
}

struct AdsListView: View {
    let ads: [PromoAd]
    var topInset: CGFloat = 0
    var onScroll: (CGFloat) -> Void = { _ in }

    var body: some View {
        DiscoverScroll(topInset: topInset, onScroll: onScroll) {
            VStack(spacing: 12) {
                ForEach(ads) { ad in
                    AdBanner(ad: ad)
                }
            }
            .padding(.horizontal, 20)
            .padding(.bottom, 28)
        }
    }
}

struct PlayerDetailView: View {
    let player: Player

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 12) {
                Image(player.imageName)
                    .resizable()
                    .scaledToFill()
                    .frame(maxWidth: .infinity)
                    .frame(height: 180)
                    .clipped()
                    .clipShape(RoundedRectangle(cornerRadius: 14, style: .continuous))
                SerifTitle(text: player.name, size: 28)
                GoldLabel(text: "\(player.position) · \(player.sport)")
                CaptionText(text: player.club)
                GoldLabel(text: "Season")
                detail("Appearances", "\(player.appearances)")
                detail(player.scoringLabel.capitalized, "\(player.scoring)")
                GoldLabel(text: "Profile")
                detail("Nationality", player.nationality)
                detail("City", player.city)
                detail("Age", "\(player.age)")
                detail("Number", "#\(player.number)")
                detail("Height", "\(player.heightCm) cm")
                detail("Appearances", "\(player.appearances)")
                detail(player.scoringLabel.capitalized, "\(player.scoring)")
                CaptionText(text: player.bio)
            }
            .padding(20)
        }
        .background(Color.brandCanvas.ignoresSafeArea())
        .navigationTitle(player.name)
        .navigationBarTitleDisplayMode(.inline)
    }

    private func detail(_ label: String, _ value: String) -> some View {
        HStack {
            CaptionText(text: label)
            Spacer()
            Text(value)
                .font(.brandSans(15, weight: .semibold))
                .foregroundStyle(Color.brandInk)
        }
    }
}

struct CoachesListView: View {
    let coaches: [Coach]
    var ad: PromoAd? = nil
    var topInset: CGFloat = 0
    var onScroll: (CGFloat) -> Void = { _ in }

    var body: some View {
        DiscoverScroll(topInset: topInset, onScroll: onScroll) {
            VStack(spacing: 12) {
                if let ad {
                    AdBanner(ad: ad)
                }
                ForEach(coaches) { person in
                    NavigationLink {
                        CoachDetailView(coach: person)
                    } label: {
                        BrandCard {
                            HStack(spacing: 12) {
                                Image(person.imageName)
                                    .resizable()
                                    .scaledToFill()
                                    .frame(width: 72, height: 72)
                                    .clipped()
                                    .clipShape(RoundedRectangle(cornerRadius: 10, style: .continuous))
                                VStack(alignment: .leading, spacing: 4) {
                                    Text(person.name)
                                        .font(.brandSans(16, weight: .semibold))
                                        .foregroundStyle(Color.brandInk)
                                    GoldLabel(text: "\(person.role) · \(person.sport)")
                                    CaptionText(text: "\(person.years) years")
                                }
                            }
                        }
                    }
                    .buttonStyle(.plain)
                }
            }
            .padding(.horizontal, 20)
            .padding(.bottom, 28)
        }
    }
}

struct CoachDetailView: View {
    let coach: Coach
    @State private var panel = "Record"

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 12) {
                Image(coach.imageName)
                    .resizable()
                    .scaledToFill()
                    .frame(maxWidth: .infinity)
                    .frame(height: 220)
                    .clipped()
                    .clipShape(RoundedRectangle(cornerRadius: 14, style: .continuous))
                SerifTitle(text: coach.name, size: 26)
                HStack {
                    coachIcon("star", "Rating")
                    coachIcon("trophy", "Record")
                    coachIcon("rosette", "Experience")
                    coachIcon("calendar", "Book")
                }
                .frame(maxWidth: .infinity)
                if panel == "Rating" {
                    Text(String(format: "%.1f", coach.rating))
                        .font(.system(size: 40, weight: .semibold))
                        .foregroundStyle(Color.brandInk)
                    CaptionText(text: "out of 5 · \(coach.years * 12) session reviews")
                } else if panel == "Record" {
                    detailCoach("Record", coach.record)
                    detailCoach("Wins", "\(coach.wins)")
                    if coach.draws > 0 { detailCoach("Draws", "\(coach.draws)") }
                    detailCoach("Losses", "\(coach.losses)")
                    detailCoach("Match weeks", "\(coach.years * 28)")
                } else if panel == "Experience" {
                    detailCoach("Years", "\(coach.years)")
                    detailCoach("Licence", coach.licence)
                    detailCoach("Clubs", coach.clubs)
                    detailCoach("Role", coach.role)
                    CaptionText(text: coach.bio)
                } else {
                    CaptionText(text: "Sessions this week: Tue 29 · 09:00, Thu 1 · 16:00, Sat 3 · 06:30.")
                }
            }
            .frame(maxWidth: .infinity, alignment: .leading)
            .padding(20)
        }
        .background(Color.brandCanvas.ignoresSafeArea())
        .navigationTitle(coach.name)
        .navigationBarTitleDisplayMode(.inline)
    }

    private func coachIcon(_ symbol: String, _ label: String) -> some View {
        Button { panel = label } label: {
            VStack(spacing: 6) {
                Image(systemName: symbol)
                    .font(.system(size: 18, weight: .semibold))
                    .foregroundStyle(panel == label ? Color.black : Color.brandGold)
                    .frame(width: 48, height: 48)
                    .background(panel == label ? Color.brandGold : Color.brandCard, in: Circle())
                Text(label)
                    .font(.system(size: 11, weight: .medium))
                    .foregroundStyle(Color.brandInk)
            }
        }
        .buttonStyle(.plain)
        .frame(maxWidth: .infinity)
    }

    private func detailCoach(_ label: String, _ value: String) -> some View {
        HStack {
            CaptionText(text: label)
            Spacer()
            Text(value)
                .font(.brandSans(15, weight: .semibold))
                .foregroundStyle(Color.brandInk)
        }
    }
}
