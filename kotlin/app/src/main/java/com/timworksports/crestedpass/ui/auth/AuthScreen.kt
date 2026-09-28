package com.timworksports.crestedpass.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.Image
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.ui.res.painterResource
import com.timworksports.crestedpass.R
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.timworksports.crestedpass.ui.components.Caption
import com.timworksports.crestedpass.ui.components.PrimaryButton
import com.timworksports.crestedpass.ui.components.SerifTitle
import com.timworksports.crestedpass.ui.theme.Gold

private enum class AuthPage { SignIn, SignUp }

@Composable
fun AuthScreen(onSignedIn: () -> Unit) {
    var page by rememberSaveable { mutableStateOf(AuthPage.SignIn) }
    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var error by rememberSaveable { mutableStateOf("") }
    val colors = MaterialTheme.colorScheme

    Column(
        Modifier
            .fillMaxSize()
            .background(colors.background)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
            .padding(top = 20.dp, bottom = 28.dp)
    ) {
        Text("Timwork Sports", color = Gold, fontSize = 28.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(6.dp))
        Caption(if (page == AuthPage.SignIn) "Coaching, events, athletes, and the shop" else "Create your Timwork Sports account")
        Spacer(Modifier.height(28.dp))
        SerifTitle(if (page == AuthPage.SignIn) "Sign in" else "Sign up", size = 26)
        Spacer(Modifier.height(16.dp))
        if (page == AuthPage.SignUp) {
            AuthField(name, { name = it }, "Full name")
            Spacer(Modifier.height(12.dp))
        }
        AuthField(email, { email = it }, "Email", KeyboardType.Email)
        Spacer(Modifier.height(12.dp))
        AuthField(password, { password = it }, "Password", KeyboardType.Password, password = true)
        if (error.isNotBlank()) {
            Spacer(Modifier.height(10.dp))
            Text(error, color = colors.error, fontSize = 13.sp)
        }
        Spacer(Modifier.height(18.dp))
        PrimaryButton(
            text = if (page == AuthPage.SignIn) "Sign in" else "Create account",
            onClick = {
                error = when {
                    page == AuthPage.SignUp && name.isBlank() -> "Enter your name."
                    !email.contains("@") -> "Enter a valid email."
                    password.length < 4 -> "Password needs at least 4 characters."
                    else -> ""
                }
                if (error.isBlank()) onSignedIn()
            }
        )
        Spacer(Modifier.height(22.dp))
        Text(
            "or continue with",
            color = colors.onSurfaceVariant,
            fontSize = 13.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(Modifier.height(14.dp))
        SocialSignIn(label = "Continue with Google", logo = R.drawable.ic_logo_google) {
            onSignedIn()
        }
        Spacer(Modifier.height(10.dp))
        SocialSignIn(label = "Continue with Apple", logo = R.drawable.ic_logo_apple) {
            onSignedIn()
        }
        Spacer(Modifier.height(10.dp))
        SocialSignIn(label = "Continue with Facebook", logo = R.drawable.ic_logo_facebook) {
            onSignedIn()
        }
        Spacer(Modifier.height(22.dp))
        Text(
            if (page == AuthPage.SignIn) "New here? Create an account" else "Already have an account? Sign in",
            color = Gold,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .clickable {
                    error = ""
                    page = if (page == AuthPage.SignIn) AuthPage.SignUp else AuthPage.SignIn
                }
        )
    }
}

@Composable
private fun AuthField(
    value: String,
    onValue: (String) -> Unit,
    label: String,
    keyboard: KeyboardType = KeyboardType.Text,
    password: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValue,
        label = { Text(label) },
        singleLine = true,
        visualTransformation = if (password) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = keyboard),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp)
    )
}

@Composable
private fun SocialSignIn(
    label: String,
    logo: Int,
    onClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .border(1.dp, colors.outline, RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(logo),
            contentDescription = null,
            modifier = Modifier.size(22.dp)
        )
        Spacer(Modifier.size(10.dp))
        Text(label, color = colors.onBackground, fontWeight = FontWeight.SemiBold)
    }
}
