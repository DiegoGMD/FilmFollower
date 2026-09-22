package com.diegogmd.filmfollower.ui.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.diegogmd.filmfollower.R
import com.diegogmd.filmfollower.SecureStorage
import com.diegogmd.filmfollower.ui.theme.DarkCoffee
import com.diegogmd.filmfollower.ui.theme.FilmTypography
import com.diegogmd.filmfollower.ui.theme.LightCaramel
import com.diegogmd.filmfollower.ui.theme.OliveWood

@Composable
fun StartScreen(modifier: Modifier, navController: NavHostController) {
    val context = LocalContext.current
    var username by remember { mutableStateOf("") }
    val (usernameError, setUsernameError) = remember { mutableStateOf(false) }
    var apiKey by remember { mutableStateOf("") }
    val (apiKeyError, setApiKeyError) = remember { mutableStateOf(false) }

    val CourierPrimeFont = FontFamily(
        Font(R.font.courierprime_bold)
    )

    wallpaper()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkCoffee.copy(alpha = 0.3f))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(25.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "FilmFollower",
            color = DarkCoffee,
            fontSize = 45.sp,
            textAlign = TextAlign.Center,
            letterSpacing = 1.sp,
            fontFamily = CourierPrimeFont
        )
        Spacer(modifier = Modifier.size(20.dp))
        Text(
            text = stringResource(id = R.string.welcome_text),
            color = DarkCoffee,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 20.dp),
            lineHeight = 20.sp
        )
        Spacer(modifier = Modifier.size(20.dp))
        OutlinedTextField(
            value = username,
            onValueChange = {
                username = it
                setUsernameError(it.isEmpty())
            },
            isError = usernameError,
            label = { Text("Username") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = DarkCoffee,
                unfocusedBorderColor = OliveWood,
                errorBorderColor = Color.Red,
                focusedLabelColor = DarkCoffee,
                unfocusedLabelColor = OliveWood,
                cursorColor = DarkCoffee,
                errorTextColor = Color.Red
            ),
            textStyle = TextStyle(color = DarkCoffee)
        )
        Spacer(modifier = Modifier.size(20.dp))
        OutlinedTextField(
            value = apiKey,
            onValueChange = {
                apiKey = it
                setApiKeyError(it.isEmpty())
            },
            isError = apiKeyError,
            label = { Text("API Key") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = DarkCoffee,
                unfocusedBorderColor = OliveWood,
                errorBorderColor = Color.Red,
                focusedLabelColor = DarkCoffee,
                unfocusedLabelColor = OliveWood,
                cursorColor = DarkCoffee,
                errorTextColor = Color.Red
            ),
            textStyle = TextStyle(color = DarkCoffee)
        )
        Spacer(modifier = Modifier.size(20.dp))
        Button(
            onClick = {
                val validUser = username.isNotBlank()
                val validKey = apiKey.isNotBlank()
                setUsernameError(!validUser)
                setApiKeyError(!validKey)

                if (validUser && validKey) {
                    SecureStorage.saveCredentials(context, username.trim(), apiKey.trim())
                    navController.navigate("Main")
                }
            },
            modifier = Modifier.width(150.dp).height(60.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = DarkCoffee,
                contentColor = LightCaramel
            ),
            shape = RoundedCornerShape(24.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
        ) {
            Text(
                text = stringResource(id = R.string.save),
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                fontFamily = CourierPrimeFont
            )
        }
    }
}

@Composable
fun wallpaper() {
    Image(
        painter = painterResource(id = R.drawable.filmfollower_wallpaper),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize()
    )
}