import SwiftUI

struct TicketDetailView: View {
    @StateObject private var vm: TicketViewModel
    var eventId: String? = nil

    init(repository: CrestedPassRepository, eventId: String? = nil) {
        self.eventId = eventId
        _vm = StateObject(wrappedValue: TicketViewModel(repository: repository))
    }

    private var event: FestivalEvent? {
        guard let eventId else { return nil }
        return vm.snapshot.events.first { $0.id == eventId }
    }

    private var ownedTicket: Ticket? {
        if let eventId {
            return vm.snapshot.tickets.first { $0.eventId == eventId }
        }
        return vm.snapshot.tickets.first ?? vm.snapshot.ticket
    }

    private var showAlert: Bool {
        ownedTicket != nil && vm.snapshot.safetyAlert != nil && !vm.snapshot.safetyAlertDismissed
    }

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 16) {
                if let ticket = ownedTicket {
                    ownedPass(ticket)
                } else if let event {
                    checkout(event)
                } else {
                    ScreenHeader(title: "Tickets", subtitle: "Buy an upcoming event from Home")
                }
            }
            .padding(.horizontal, 20)
            .padding(.vertical, 16)
        }
        .background(Color.brandCream.ignoresSafeArea())
        .navigationTitle(ownedTicket == nil && event != nil ? "Buy ticket" : "Match ticket")
        .navigationBarTitleDisplayMode(.inline)
        .toolbar {
            if let ticket = ownedTicket {
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button("Print") {
                        TicketPassLayout.presentPrinter(
                            ticket: ticket,
                            holderName: vm.snapshot.user.name
                        )
                    }
                }
            }
        }
    }

    @ViewBuilder
    private func ownedPass(_ ticket: Ticket) -> some View {
        ScreenHeader(
            title: ticket.matchLabel,
            subtitle: "Printable Crested Pass · \(ticket.venue)"
        )
        if showAlert, let alert = vm.snapshot.safetyAlert {
            BrandCard(background: .brandRed) {
                HStack(alignment: .top) {
                    VStack(alignment: .leading, spacing: 6) {
                        Text("SAFETY ALERT")
                            .font(.brandSans(11, weight: .semibold))
                            .tracking(1.2)
                            .foregroundStyle(.white)
                        Text(alert)
                            .font(.brandSans(14))
                            .foregroundStyle(.white)
                    }
                    Spacer()
                    Button("Dismiss", action: vm.dismissAlert)
                        .font(.brandSans(13, weight: .semibold))
                        .foregroundStyle(.white)
                }
            }
        }
        PrintableTicketPass(ticket: ticket, holderName: vm.snapshot.user.name)
        PrimaryButton(title: "Print ticket", gold: true) {
            TicketPassLayout.presentPrinter(
                ticket: ticket,
                holderName: vm.snapshot.user.name
            )
        }
    }

    @ViewBuilder
    private func checkout(_ event: FestivalEvent) -> some View {
        SportCover(event: event)
            .frame(height: 180)
            .clipShape(RoundedRectangle(cornerRadius: 14, style: .continuous))
        ScreenHeader(title: event.title, subtitle: "\(event.sport) · \(event.whenLabel)")
        CaptionText(text: "\(event.venue) · \(event.city)")
        BrandCard {
            HStack {
                VStack(alignment: .leading, spacing: 4) {
                    GoldLabel(text: "Wallet")
                    Text(vm.snapshot.user.walletBalance.ugx)
                        .font(.brandSans(18, weight: .semibold))
                        .foregroundStyle(Color.brandInk)
                }
                Spacer()
                VStack(alignment: .trailing, spacing: 4) {
                    GoldLabel(text: "Ticket")
                    Text(event.price.ugx)
                        .font(.brandSans(18, weight: .semibold))
                        .foregroundStyle(Color.brandInk)
                }
            }
        }
        if vm.snapshot.user.walletBalance >= event.price {
            PrimaryButton(title: "Buy ticket", gold: true) {
                vm.buy(event.id)
            }
            CaptionText(text: "Pays from your Crested Pass wallet. Mock checkout only.")
        } else {
            CaptionText(text: "Need \((event.price - vm.snapshot.user.walletBalance).ugx) more. Top up to buy.")
            ForEach([10_000, 20_000, 50_000, 100_000], id: \.self) { amount in
                PrimaryButton(title: "Top up \(amount.ugx)") {
                    vm.topUp(amount)
                }
            }
        }
    }
}
