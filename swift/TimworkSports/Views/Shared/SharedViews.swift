import SwiftUI

struct BrandCard<Content: View>: View {
    var background: Color = .brandCard
    var padding: CGFloat = 16
    @ViewBuilder var content: () -> Content

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            content()
        }
        .padding(padding)
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(background)
        .clipShape(RoundedRectangle(cornerRadius: 14, style: .continuous))
        .overlay(
            RoundedRectangle(cornerRadius: 14, style: .continuous)
                .stroke(Color.brandBorder, lineWidth: 1)
        )
    }
}

struct FilterChip: View {
    let title: String
    var selected: Bool
    var onDark: Bool = false
    var action: () -> Void

    var body: some View {
        Button(action: action) {
            Text(title)
                .font(.system(size: 13, weight: .semibold))
                .foregroundStyle(selected ? Color.brandNavy : (onDark ? Color.white.opacity(0.78) : Color.brandMuted))
                .padding(.horizontal, 14)
                .padding(.vertical, 8)
                .background(selected ? Color.brandGold : (onDark ? Color.white.opacity(0.12) : Color.brandCard))
                .clipShape(Capsule())
                .overlay(
                    Capsule().stroke(
                        selected ? Color.brandGold : (onDark ? Color.clear : Color.brandBorder),
                        lineWidth: 1
                    )
                )
        }
        .buttonStyle(.plain)
    }
}

struct GoldLabel: View {
    let text: String
    var body: some View {
        Text(text)
            .font(.system(size: 12, weight: .medium))
            .foregroundStyle(Color.brandGold)
    }
}

struct SerifTitle: View {
    let text: String
    var size: CGFloat = 22
    var color: Color = .brandInk

    var body: some View {
        Text(text)
            .font(.system(size: size, weight: .semibold))
            .foregroundStyle(color)
    }
}

struct CaptionText: View {
    let text: String
    var color: Color = .brandMuted

    var body: some View {
        Text(text)
            .font(.brandSans(13))
            .foregroundStyle(color)
    }
}

struct PrimaryButton: View {
    let title: String
    var gold = false
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            Text(title)
                .font(.brandSans(15, weight: .semibold))
                .foregroundStyle(gold ? Color.brandNavy : Color.brandCanvas)
                .frame(maxWidth: .infinity)
                .padding(.vertical, 14)
                .background(gold ? Color.brandGold : Color.brandInk)
                .clipShape(RoundedRectangle(cornerRadius: 8, style: .continuous))
        }
        .buttonStyle(.plain)
    }
}

struct PillButton: View {
    let title: String
    var gold = false
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            Text(title)
                .font(.brandSans(13, weight: .semibold))
                .foregroundStyle(gold ? Color.brandNavy : Color.brandCanvas)
                .padding(.horizontal, 16)
                .padding(.vertical, 10)
                .background(gold ? Color.brandGold : Color.brandInk)
                .clipShape(Capsule())
        }
        .buttonStyle(.plain)
    }
}

struct TrailBar: View {
    let collected: Int
    let total: Int

    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            HStack {
                Text("Trail progress")
                    .font(.brandSans(14, weight: .semibold))
                    .foregroundStyle(Color.brandInk)
                Spacer()
                Text("\(collected) / \(total) stamps")
                    .font(.brandSans(13))
                    .foregroundStyle(Color.brandMuted)
            }
            GeometryReader { geo in
                ZStack(alignment: .leading) {
                    Capsule().fill(Color.brandBorder)
                    Capsule()
                        .fill(Color.brandGold)
                        .frame(width: total == 0 ? 0 : geo.size.width * CGFloat(collected) / CGFloat(total))
                }
            }
            .frame(height: 8)
        }
    }
}

struct LanguageToggle: View {
    let language: String
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            HStack(spacing: 6) {
                Text(language)
                    .font(.brandSans(12, weight: .semibold))
                Text("▾")
                    .font(.brandSans(10))
                    .foregroundStyle(Color.brandMuted)
            }
            .foregroundStyle(Color.brandInk)
            .padding(.horizontal, 10)
            .padding(.vertical, 6)
            .overlay(Capsule().stroke(Color.brandBorder, lineWidth: 1))
        }
        .buttonStyle(.plain)
    }
}

struct QuickAction: View {
    let label: String
    let systemImage: String
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            QuickActionLabel(label: label, systemImage: systemImage)
        }
        .buttonStyle(.plain)
    }
}

struct QuickActionLabel: View {
    let label: String
    var systemImage: String = "circle"

    var body: some View {
        VStack(spacing: 8) {
            Image(systemName: systemImage)
                .font(.system(size: 15, weight: .semibold))
                .foregroundStyle(Color.brandInk)
                .frame(width: 36, height: 36)
                .background(Color.brandCanvas)
                .clipShape(Circle())
                .overlay(Circle().stroke(Color.brandBorder, lineWidth: 1))
            Text(label)
                .font(.brandSans(12, weight: .medium))
                .foregroundStyle(Color.brandInk)
        }
        .frame(maxWidth: .infinity)
        .padding(.vertical, 14)
        .background(Color.brandCard)
        .clipShape(RoundedRectangle(cornerRadius: 12, style: .continuous))
        .overlay(
            RoundedRectangle(cornerRadius: 12, style: .continuous)
                .stroke(Color.brandBorder, lineWidth: 1)
        )
    }
}

struct ScreenHeader: View {
    let title: String
    var subtitle: String? = nil
    var trailing: AnyView? = nil

    var body: some View {
        HStack(alignment: .top) {
            VStack(alignment: .leading, spacing: 4) {
                SerifTitle(text: title, size: 24)
                if let subtitle {
                    CaptionText(text: subtitle)
                }
            }
            Spacer()
            if let trailing {
                trailing
            }
        }
    }
}

struct SportCover: View {
    let event: FestivalEvent

    var body: some View {
        ZStack {
            LinearGradient(colors: event.coverColors, startPoint: .topLeading, endPoint: .bottomTrailing)
            Image(systemName: event.symbol)
                .font(.system(size: 42, weight: .semibold))
                .foregroundStyle(.white.opacity(0.22))
            Image(event.imageName)
                .resizable()
                .scaledToFill()
        }
        .clipped()
    }
}

struct AvatarView: View {
    let key: String
    let initials: String
    var size: CGFloat = 40
    var ring: Bool = false

    var body: some View {
        ZStack {
            if ring {
                Circle()
                    .stroke(Color.brandGold, lineWidth: 2)
                    .frame(width: size + 8, height: size + 8)
            }
            Circle()
                .fill(Color.avatar(for: key))
                .frame(width: size, height: size)
                .overlay {
                    Text(initials)
                        .font(.system(size: max(11, size * 0.32), weight: .semibold))
                        .foregroundStyle(.white)
                }
        }
        .frame(width: ring ? size + 8 : size, height: ring ? size + 8 : size)
        .accessibilityLabel(initials)
    }
}

struct SettingsRow: View {
    let systemImage: String
    let title: String
    var value: String? = nil
    var destructive: Bool = false
    var showChevron: Bool = true

    var body: some View {
        HStack(spacing: 12) {
            Image(systemName: systemImage)
                .font(.system(size: 14, weight: .semibold))
                .foregroundStyle(destructive ? Color.brandRed : Color.brandInk)
                .frame(width: 36, height: 36)
                .background(Color.brandCanvas)
                .clipShape(Circle())
                .overlay(Circle().stroke(Color.brandBorder, lineWidth: 1))
            Text(title)
                .font(.brandSans(15, weight: .medium))
                .foregroundStyle(destructive ? Color.brandRed : Color.brandInk)
            Spacer()
            if let value {
                Text(value)
                    .font(.brandSans(13))
                    .foregroundStyle(Color.brandMuted)
                    .lineLimit(1)
            }
            if showChevron {
                Image(systemName: "chevron.right")
                    .font(.system(size: 12, weight: .semibold))
                    .foregroundStyle(Color.brandMuted)
            }
        }
        .padding(.vertical, 2)
        .contentShape(Rectangle())
    }
}

struct SettingsToggleRow: View {
    let systemImage: String
    let title: String
    @Binding var isOn: Bool

    var body: some View {
        HStack(spacing: 12) {
            Image(systemName: systemImage)
                .font(.system(size: 14, weight: .semibold))
                .foregroundStyle(Color.brandInk)
                .frame(width: 36, height: 36)
                .background(Color.brandCanvas)
                .clipShape(Circle())
                .overlay(Circle().stroke(Color.brandBorder, lineWidth: 1))
            Text(title)
                .font(.brandSans(15, weight: .medium))
                .foregroundStyle(Color.brandInk)
            Spacer()
            Toggle("", isOn: $isOn)
                .labelsHidden()
                .tint(Color.brandGold)
        }
        .padding(.vertical, 2)
    }
}
