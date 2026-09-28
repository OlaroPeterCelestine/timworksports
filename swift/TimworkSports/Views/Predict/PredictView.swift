import SwiftUI

struct PredictView: View {
    @StateObject private var vm: PredictViewModel

    init(repository: CrestedPassRepository) {
        _vm = StateObject(wrappedValue: PredictViewModel(repository: repository))
    }

    var body: some View {
        let match = vm.snapshot.nextMatch
        ScrollView {
            VStack(alignment: .leading, spacing: 16) {
                ScreenHeader(title: "Predict", subtitle: "Lock a score before kickoff")
                BrandCard(background: .brandNavy) {
                    GoldLabel(text: "Current fixture")
                    Spacer().frame(height: 8)
                    SerifTitle(text: "\(match.home) vs \(match.away)", size: 24, color: .white)
                    CaptionText(text: "\(match.venue) · \(match.kickoffInHours)h to kickoff", color: .brandGold)
                }
                BrandCard {
                    GoldLabel(text: "Your score")
                    HStack {
                        ScoreStepper(label: match.home, value: vm.homeScore, bump: vm.bumpHome)
                        Text("–")
                            .font(.brandSerif(28))
                            .foregroundStyle(Color.brandInk)
                        ScoreStepper(label: match.away, value: vm.awayScore, bump: vm.bumpAway)
                    }
                    .padding(.vertical, 12)
                    PrimaryButton(
                        title: vm.snapshot.prediction.homeScore == nil ? "Lock in prediction" : "Update prediction",
                        gold: true,
                        action: vm.submit
                    )
                    if let home = vm.snapshot.prediction.homeScore,
                       let away = vm.snapshot.prediction.awayScore {
                        CaptionText(text: "Locked in: \(match.home) \(home)–\(away) \(match.away)")
                            .padding(.top, 10)
                    }
                }
                BrandCard {
                    GoldLabel(text: "Points key")
                    CaptionText(text: "Exact score · 10 pts")
                    CaptionText(text: "Correct winner + goal difference · 5 pts")
                    CaptionText(text: "Correct winner · 3 pts")
                    CaptionText(text: "Any prediction submitted · 1 pt")
                }
                GoldLabel(text: "Leaderboard")
                ForEach(Array(vm.snapshot.leaderboard.enumerated()), id: \.element.id) { index, entry in
                    let isYou = entry.name == vm.snapshot.user.name
                    BrandCard(background: isYou ? Color.brandGold.opacity(0.18) : .brandCard) {
                        HStack {
                            Text("\(index + 1)")
                                .font(.brandSerif(20))
                                .foregroundStyle(isYou ? Color.brandInk : Color.brandGold)
                                .padding(.trailing, 8)
                            VStack(alignment: .leading, spacing: 2) {
                                Text(isYou ? "\(entry.name)  ·  you" : entry.name)
                                    .font(.brandSans(15, weight: isYou ? .semibold : .regular))
                                    .foregroundStyle(Color.brandInk)
                                if isYou { CaptionText(text: "Your row") }
                            }
                            Spacer()
                            SerifTitle(text: "\(entry.points)", size: 22)
                        }
                    }
                }
            }
            .padding(.horizontal, 20)
            .padding(.vertical, 16)
        }
        .background(Color.brandCream.ignoresSafeArea())
        .navigationTitle("Predict")
        .navigationBarTitleDisplayMode(.inline)
    }
}

private struct ScoreStepper: View {
    let label: String
    let value: Int
    let bump: (Int) -> Void

    var body: some View {
        VStack(spacing: 8) {
            CaptionText(text: label)
            stepperButton("+") { bump(1) }
            Text("\(value)")
                .font(.brandSerif(28))
                .foregroundStyle(Color.brandInk)
                .frame(width: 64, height: 64)
                .background(Color.white)
                .clipShape(RoundedRectangle(cornerRadius: 12, style: .continuous))
                .overlay(
                    RoundedRectangle(cornerRadius: 12, style: .continuous)
                        .stroke(Color.brandBorder, lineWidth: 1)
                )
            stepperButton("–") { bump(-1) }
        }
        .frame(maxWidth: .infinity)
    }

    private func stepperButton(_ title: String, action: @escaping () -> Void) -> some View {
        Button(action: action) {
            Text(title)
                .font(.brandSans(18, weight: .semibold))
                .foregroundStyle(Color.brandInk)
                .frame(width: 36, height: 36)
                .background(Color.white)
                .clipShape(Circle())
                .overlay(Circle().stroke(Color.brandBorder, lineWidth: 1))
        }
        .buttonStyle(.plain)
    }
}
