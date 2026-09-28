import SwiftUI
import UIKit

private enum AuthPage {
    case signIn, signUp
}

struct AuthView: View {
    var onSignedIn: () -> Void
    @State private var page: AuthPage = .signIn
    @State private var name = ""
    @State private var email = ""
    @State private var password = ""
    @State private var error = ""

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 0) {
                Text("Timwork Sports")
                    .font(.system(size: 28, weight: .semibold))
                    .foregroundStyle(Color.brandGold)
                CaptionText(text: page == .signIn ? "Coaching, events, athletes, and the shop" : "Create your Timwork Sports account")
                    .padding(.top, 6)
                SerifTitle(text: page == .signIn ? "Sign in" : "Sign up", size: 26)
                    .padding(.top, 28)
                if page == .signUp {
                    AuthField(title: "Full name", text: $name)
                        .padding(.top, 16)
                }
                AuthField(title: "Email", text: $email, keyboard: .emailAddress)
                    .padding(.top, 12)
                AuthField(title: "Password", text: $password, secure: true)
                    .padding(.top, 12)
                if !error.isEmpty {
                    Text(error)
                        .font(.brandSans(13))
                        .foregroundStyle(Color.brandRed)
                        .padding(.top, 10)
                }
                PrimaryButton(title: page == .signIn ? "Sign in" : "Create account") {
                    if page == .signUp && name.trimmingCharacters(in: .whitespaces).isEmpty {
                        error = "Enter your name."
                    } else if !email.contains("@") {
                        error = "Enter a valid email."
                    } else if password.count < 4 {
                        error = "Password needs at least 4 characters."
                    } else {
                        error = ""
                        onSignedIn()
                    }
                }
                .padding(.top, 18)
                Text("or continue with")
                    .font(.brandSans(13))
                    .foregroundStyle(Color.brandMuted)
                    .frame(maxWidth: .infinity)
                    .padding(.top, 22)
                SocialSignIn(label: "Continue with Google", systemImage: nil, mark: "G", markColor: Color(red: 66 / 255, green: 133 / 255, blue: 244 / 255), action: onSignedIn)
                    .padding(.top, 14)
                SocialSignIn(label: "Continue with Apple", systemImage: "apple.logo", mark: "", markColor: .clear, action: onSignedIn)
                    .padding(.top, 10)
                SocialSignIn(label: "Continue with Facebook", systemImage: nil, mark: "f", markColor: Color(red: 24 / 255, green: 119 / 255, blue: 242 / 255), action: onSignedIn)
                    .padding(.top, 10)
                Button {
                    error = ""
                    page = page == .signIn ? .signUp : .signIn
                } label: {
                    Text(page == .signIn ? "New here? Create an account" : "Already have an account? Sign in")
                        .font(.brandSans(15, weight: .semibold))
                        .foregroundStyle(Color.brandNavy)
                        .frame(maxWidth: .infinity)
                }
                .buttonStyle(.plain)
                .padding(.top, 22)
            }
            .padding(.horizontal, 24)
            .padding(.vertical, 36)
        }
        .background(Color.brandCanvas.ignoresSafeArea())
    }
}

private struct AuthField: View {
    let title: String
    @Binding var text: String
    var keyboard: UIKeyboardType = .default
    var secure = false

    var body: some View {
        Group {
            if secure {
                SecureField(title, text: $text)
            } else {
                TextField(title, text: $text)
                    .keyboardType(keyboard)
                    .textInputAutocapitalization(keyboard == .emailAddress ? .never : .words)
            }
        }
        .font(.brandSans(16))
        .padding(14)
        .background(Color.brandCard)
        .clipShape(RoundedRectangle(cornerRadius: 10, style: .continuous))
        .overlay(
            RoundedRectangle(cornerRadius: 10, style: .continuous)
                .stroke(Color.brandBorder, lineWidth: 1)
        )
    }
}

private struct SocialSignIn: View {
    let label: String
    var systemImage: String? = nil
    let mark: String
    let markColor: Color
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            HStack(spacing: 10) {
                if let systemImage {
                    Image(systemName: systemImage)
                        .font(.system(size: 20, weight: .regular))
                        .foregroundStyle(Color.brandInk)
                        .frame(width: 28, height: 28)
                } else {
                    Text(mark)
                        .font(.system(size: 15, weight: .bold))
                        .foregroundStyle(.white)
                        .frame(width: 28, height: 28)
                        .background(markColor)
                        .clipShape(Circle())
                }
                Text(label)
                    .font(.brandSans(15, weight: .semibold))
                    .foregroundStyle(Color.brandInk)
            }
            .frame(maxWidth: .infinity)
            .padding(.vertical, 12)
            .overlay(
                RoundedRectangle(cornerRadius: 10, style: .continuous)
                    .stroke(Color.brandBorder, lineWidth: 1)
            )
        }
        .buttonStyle(.plain)
    }
}
