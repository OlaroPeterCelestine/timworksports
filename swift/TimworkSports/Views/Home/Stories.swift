import SwiftUI

struct StoryCircle: Identifiable, Hashable {
    let id: String
    let name: String
    let initials: String
    let imageName: String
    let reelIndex: Int
    var isYou: Bool = false
}

func storyCircles(userName: String, reels: [ReelClip]) -> [StoryCircle] {
    let yours = reels.firstIndex { $0.author == userName } ?? 0
    let youImage = reels.indices.contains(yours) ? reels[yours].imageName : "reel_stands"
    let you = StoryCircle(
        id: "you",
        name: "Your story",
        initials: userName.split(separator: " ").compactMap { $0.first.map(String.init) }.prefix(2).joined().uppercased(),
        imageName: youImage,
        reelIndex: yours,
        isYou: true
    )
    var others: [StoryCircle] = []
    var seenAuthors = Set<String>()
    for (index, reel) in reels.enumerated() where reel.author != userName {
        guard !seenAuthors.contains(reel.author) else { continue }
        seenAuthors.insert(reel.author)
        others.append(
            StoryCircle(
                id: reel.author,
                name: String(reel.author.split(separator: " ").first ?? Substring(reel.author)),
                initials: reel.initials,
                imageName: reel.imageName,
                reelIndex: index
            )
        )
    }
    return [you] + others
}

struct StoryCirclesRow: View {
    let userName: String
    let reels: [ReelClip]
    let seen: Set<String>
    var onOpen: (Int, String) -> Void

    var body: some View {
        let stories = storyCircles(userName: userName, reels: reels)
        ScrollView(.horizontal, showsIndicators: false) {
            HStack(spacing: 12) {
                ForEach(stories) { story in
                    StoryAvatar(story: story, seen: seen.contains(story.id)) {
                        onOpen(story.reelIndex, story.id)
                    }
                }
            }
            .padding(.horizontal, 14)
            .padding(.vertical, 10)
        }
    }
}

private struct StoryAvatar: View {
    let story: StoryCircle
    let seen: Bool
    var onClick: () -> Void

    var body: some View {
        Button(action: onClick) {
            VStack(spacing: 6) {
                ZStack {
                    Group {
                        if seen || story.isYou {
                            Circle()
                                .stroke(Color.brandBorder, lineWidth: 1.5)
                        } else {
                            Circle()
                                .stroke(
                                    LinearGradient(
                                        colors: [.brandGold, Color(red: 232 / 255, green: 197 / 255, blue: 71 / 255), .brandRed, .brandGold],
                                        startPoint: .topLeading,
                                        endPoint: .bottomTrailing
                                    ),
                                    lineWidth: 2.5
                                )
                        }
                    }
                    .frame(width: 78, height: 78)
                    Circle()
                        .fill(Color.brandCanvas)
                        .frame(width: 70, height: 70)
                    Image(story.imageName)
                        .resizable()
                        .scaledToFill()
                        .frame(width: 64, height: 64)
                        .clipShape(Circle())
                    if story.isYou {
                        Circle()
                            .fill(Color.brandGold)
                            .frame(width: 20, height: 20)
                            .overlay {
                                Image(systemName: "plus")
                                    .font(.system(size: 10, weight: .bold))
                                    .foregroundStyle(.black)
                            }
                            .overlay(Circle().stroke(Color.brandCanvas, lineWidth: 2))
                            .offset(x: 22, y: 22)
                    }
                }
                .frame(width: 78, height: 78)
                Text(story.name)
                    .font(.system(size: 12))
                    .foregroundStyle(Color.brandInk)
                    .lineLimit(1)
            }
            .frame(width: 76)
        }
        .buttonStyle(.plain)
    }
}

struct ShotPost: View {
    let shot: Shot
    var onLike: () -> Void

    private var likesLabel: String {
        let formatter = NumberFormatter()
        formatter.numberStyle = .decimal
        let digits = formatter.string(from: NSNumber(value: shot.likes)) ?? "\(shot.likes)"
        return "\(digits) likes"
    }

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            HStack(spacing: 10) {
                Image(shot.imageName)
                    .resizable()
                    .scaledToFill()
                    .frame(width: 36, height: 36)
                    .clipShape(Circle())
                VStack(alignment: .leading, spacing: 1) {
                    Text(shot.author)
                        .font(.system(size: 14, weight: .semibold))
                        .foregroundStyle(Color.brandInk)
                    Text(shot.handle)
                        .font(.system(size: 12))
                        .foregroundStyle(Color.brandMuted)
                }
                Spacer()
            }
            .padding(.horizontal, 14)
            .padding(.vertical, 10)

            Image(shot.imageName)
                .resizable()
                .scaledToFill()
                .frame(maxWidth: .infinity)
                .aspectRatio(4 / 5, contentMode: .fill)
                .clipped()

            HStack(spacing: 16) {
                Button(action: onLike) {
                    Image(systemName: shot.liked ? "heart.fill" : "heart")
                        .font(.system(size: 22))
                        .foregroundStyle(shot.liked ? Color.brandRed : Color.brandInk)
                }
                Image(systemName: "bubble.right")
                    .font(.system(size: 20))
                    .foregroundStyle(Color.brandInk)
                Image(systemName: "paperplane")
                    .font(.system(size: 20))
                    .foregroundStyle(Color.brandInk)
                Spacer()
                Image(systemName: "bookmark")
                    .font(.system(size: 20))
                    .foregroundStyle(Color.brandInk)
            }
            .padding(.horizontal, 12)
            .padding(.vertical, 10)

            Text(likesLabel)
                .font(.system(size: 14, weight: .semibold))
                .foregroundStyle(Color.brandInk)
                .padding(.horizontal, 14)

            HStack(alignment: .top, spacing: 6) {
                Text(shot.author)
                    .font(.system(size: 14, weight: .semibold))
                    .foregroundStyle(Color.brandInk)
                Text(shot.caption)
                    .font(.system(size: 14))
                    .foregroundStyle(Color.brandInk)
            }
            .padding(.horizontal, 14)
            .padding(.top, 4)
            .padding(.bottom, 12)

            Divider().overlay(Color.brandBorder)
        }
    }
}
