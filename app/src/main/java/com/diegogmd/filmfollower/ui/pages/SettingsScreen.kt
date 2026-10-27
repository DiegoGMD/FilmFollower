package com.diegogmd.filmfollower.ui.pages

import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.diegogmd.filmfollower.R
import com.diegogmd.filmfollower.SecureStorage
import com.diegogmd.filmfollower.seedFilms
import com.diegogmd.filmfollower.ui.theme.Black
import com.diegogmd.filmfollower.ui.theme.BrightGold
import com.diegogmd.filmfollower.ui.theme.CarbonBlack
import com.diegogmd.filmfollower.ui.theme.SlateGrey
import com.diegogmd.filmfollower.ui.theme.DarkCoffee
import com.diegogmd.filmfollower.ui.theme.FilmTypography
import com.diegogmd.filmfollower.ui.theme.Graphite
import com.diegogmd.filmfollower.ui.theme.Gunmetal
import com.diegogmd.filmfollower.ui.theme.JetBlack
import com.diegogmd.filmfollower.ui.theme.LightCaramel
import com.diegogmd.filmfollower.ui.theme.Silver
import com.diegogmd.filmfollower.ui.theme.ThemeManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * One entry of the theme carousel.
 *
 * [previewRes] is a screenshot of the app in that theme (drop the PNGs in res/drawable,
 * e.g. R.drawable.theme_coffee). While it is null, a small drawn placeholder is shown
 * using [placeholderBackground] / [placeholderAccent].
 */

data class ThemeOption(
    val id: Int,
    val name: String,
    @DrawableRes val previewRes: Int? = null,
    val placeholderBackground: Color = DarkCoffee,
    val placeholderAccent: Color = LightCaramel
)

// TODO: replace with your real screenshots.
// I don't have them yet and considering not doing it, looks good and shows an approximation
private val themeOptions = listOf(
    ThemeOption(0, "OldFilm", previewRes = null, DarkCoffee, LightCaramel),
    ThemeOption(1, "OldFilm B&W", previewRes = null,
        Graphite, Silver
    ),
    ThemeOption(2, "Cold Blue", previewRes = null, JetBlack, SlateGrey),
    ThemeOption(3, "Movie Time", previewRes = null, Black, BrightGold),
)

@Composable
fun SettingsScreen(
    modifier: Modifier,
    onBackClick: () -> Unit = {},
) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current

    var username by remember { mutableStateOf(SecureStorage.getUsername(context).orEmpty()) }
    var usernameError by remember { mutableStateOf(false) }

    var apiToken by remember { mutableStateOf(SecureStorage.getApiReadAccessToken(context).orEmpty()) }
    var apiTokenError by remember { mutableStateOf(false) }
    var showToken by remember { mutableStateOf(false) }

    val selectedThemeId = ThemeManager.themeId
    val selectedTheme = themeOptions.first { it.id == selectedThemeId }
    var showPreviews by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CarbonBlack)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        Box(
            modifier = Modifier
                .padding(bottom = 20.dp)
                .clickable(onClick = onBackClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Silver,
                modifier = Modifier.size(28.dp)
            )
        }

        Text(
            text = "Settings",
            style = FilmTypography.titleMedium.copy(fontSize = 44.sp, lineHeight = 52.sp),
            color = Silver,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // ---------- Account ----------
        SettingsSection(label = "Account") {
            SettingsFieldRow(
                title = "Username",
                supporting = "Shown on your profile",
                value = username,
                onValueChange = {
                    username = it
                    usernameError = it.isBlank()
                },
                isError = usernameError,
                errorText = "Username can't be empty",
                imeAction = ImeAction.Next,
                saveLabel = stringResource(id = R.string.save),
                onSave = {
                    val valid = username.isNotBlank()
                    usernameError = !valid
                    if (valid) SecureStorage.changeUsername(context, username.trim())
                }
            )
        }

        Spacer(Modifier.height(16.dp))

        // ---------- Data source ----------
        SettingsSection(label = "Data source") {
            SettingsFieldRow(
                title = "API read access token",
                supporting = "Used to load film data",
                value = apiToken,
                onValueChange = {
                    apiToken = it
                    apiTokenError = it.isBlank()
                },
                isError = apiTokenError,
                errorText = "Token can't be empty",
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Password,
                visualTransformation = if (showToken) VisualTransformation.None
                else PasswordVisualTransformation(),
                trailing = {
                    TextButton(onClick = { showToken = !showToken }) {
                        Text(if (showToken) "Hide" else "Show", color = Silver)
                    }
                },
                saveLabel = stringResource(id = R.string.save),
                onSave = {
                    val valid = apiToken.isNotBlank()
                    apiTokenError = !valid
                    if (valid) {
                        SecureStorage.changeApiReadAccessToken(context, apiToken.trim())
                    }
                }
            )
        }

        Spacer(Modifier.height(16.dp))

        // ---------- Appearance ----------
        SettingsSection(label = "Appearance") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Theme", fontSize = 20.sp, color = Silver)
                    Text(
                        text = selectedTheme.name,
                        fontSize = 14.sp,
                        color = Silver.copy(alpha = 0.7f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Button(
                    onClick = { showPreviews = true },
                    modifier = Modifier.height(48.dp),
                    shape = RoundedCornerShape(16.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Silver,
                        contentColor = Graphite
                    )
                ) {
                    Text(stringResource(id = R.string.choose), fontSize = 16.sp)
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // ---------- Credits ----------
        SettingsSection(label = "Credits") {
            // Developer
            Image(
                painter = painterResource(R.drawable.diegogmd_logo),
                contentDescription = "Developer logo",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .height(40.dp)
                    .clickable { uriHandler.openUri("https://github.com/DiegoGMD") }
            )
            Spacer(Modifier.height(8.dp))
            Text("DiegoGMD", fontSize = 20.sp, color = Silver)
            Text("Developer", fontSize = 14.sp, color = Silver.copy(alpha = 0.7f))

            Spacer(Modifier.height(16.dp))
            HorizontalDivider(color = Gunmetal, thickness = 1.dp)
            Spacer(Modifier.height(16.dp))

            // Data source
            Spacer(Modifier.height(8.dp))
            Image(
                painter = painterResource(R.drawable.tmdb_logo),
                contentDescription = "TMDB logo",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .height(32.dp)
                    .clickable { uriHandler.openUri("https://www.themoviedb.org") }
            )
            Spacer(Modifier.height(8.dp))
            Text("The Movie Database", fontSize = 20.sp, color = Silver)
            Text("Films and shows data", fontSize = 14.sp, color = Silver.copy(alpha = 0.7f))

            Spacer(Modifier.height(12.dp))

            Text(
                text = "This product uses the TMDB API but is not endorsed or certified by TMDB.",
                fontSize = 12.sp,
                lineHeight = 16.sp,
                color = Silver.copy(alpha = 0.7f)
            )
        }

        Spacer(Modifier.height(16.dp))

        val scope = rememberCoroutineScope()
        // ---------- Testing Data ----------
        SettingsSection(label = "Testing Data") {
            Button(
                onClick = {
                    scope.launch {
                        val result = withContext(Dispatchers.IO) {
                            runCatching { seedFilms(context.applicationContext) }
                        }
                        Toast.makeText(
                            context,
                            if (result.isSuccess) "Testing data loaded"
                            else "Failed: ${result.exceptionOrNull()?.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Silver,
                    contentColor = Graphite
                ),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text("Load Testing Data", fontSize = 16.sp)
            }
        }

        if (showPreviews) {
            ThemePreviewSheet(
                options = themeOptions,
                selectedId = selectedThemeId,
                onSelect = {
                    ThemeManager.setTheme(context, it)
                },
                onDismiss = { showPreviews = false }
            )
        }

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun SettingsSection(
    label: String,
    bleed: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Graphite),
        border = BorderStroke(1.dp, Gunmetal)
    ) {
        Column(modifier = Modifier.padding(vertical = 20.dp)) {
            Text(
                text = label,
                fontSize = 14.sp,
                color = Silver,
                style = FilmTypography.titleMedium,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            Spacer(Modifier.height(16.dp))
            Column(
                modifier = Modifier.padding(horizontal = if (bleed) 0.dp else 24.dp),
                content = content
            )
        }
    }
}

// Horizontally snapping carousel of app screenshots; tapping a card selects that theme.
@Composable
private fun ThemeCarousel(
    options: List<ThemeOption>,
    selectedId: Int,
    onSelect: (Int) -> Unit
) {
    val listState = rememberLazyListState()

    LazyRow(
        state = listState,
        flingBehavior = rememberSnapFlingBehavior(lazyListState = listState),
        contentPadding = PaddingValues(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(options, key = { it.id }) { option ->
            ThemeCard(
                option = option,
                selected = option.id == selectedId,
                onClick = { onSelect(option.id) }
            )
        }
    }
}

@Composable
private fun ThemeCard(
    option: ThemeOption,
    selected: Boolean,
    onClick: () -> Unit
) {
    val borderColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.primary else CarbonBlack.copy(alpha = 0.5f),
        label = "themeCardBorder"
    )
    val shape = RoundedCornerShape(20.dp)

    Column(
        modifier = Modifier.width(150.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(9f / 16f)
                .clip(shape)
                .border(width = if (selected) 3.dp else 1.dp, color = borderColor, shape = shape)
                .clickable(onClick = onClick)
        ) {
            if (option.previewRes != null) {
                Image(
                    painter = painterResource(option.previewRes),
                    contentDescription = "${option.name} theme preview",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                ThemePlaceholder(option)
            }

            if (selected) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Graphite),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Selected",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        Text(
            text = option.name,
            fontSize = 16.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            color = if (selected) Silver else Silver.copy(alpha = 0.7f)
        )
    }
}

// Tiny fake app screen, only used until you add real screenshots.
@Composable
private fun ThemePlaceholder(option: ThemeOption) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(option.placeholderBackground)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            Modifier
                .fillMaxWidth(0.5f)
                .height(10.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(option.placeholderAccent)
        )
        repeat(3) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(option.placeholderAccent.copy(alpha = 0.2f))
            )
        }
    }
}

// Bottom sheet with the screenshot carousel. Tapping a card selects that theme.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ThemePreviewSheet(
    options: List<ThemeOption>,
    selectedId: Int,
    onSelect: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = Graphite,
        contentColor = Silver
    ) {
        Column(modifier = Modifier
            .navigationBarsPadding()
            .padding(bottom = 16.dp)) {
            Text(
                text = "Theme previews",
                fontSize = 24.sp,
                color = Silver,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            Text(
                text = "Tap a preview to switch",
                fontSize = 14.sp,
                color = Silver.copy(alpha = 0.7f),
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            Spacer(Modifier.height(16.dp))

            ThemeCarousel(options = options, selectedId = selectedId, onSelect = onSelect)

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(horizontal = 24.dp)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Silver,
                    contentColor = Graphite
                ),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text("Done", fontSize = 16.sp)
            }
        }
    }
}

@Composable
private fun SettingsFieldRow(
    title: String,
    supporting: String,
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean,
    errorText: String,
    saveLabel: String,
    onSave: () -> Unit,
    imeAction: ImeAction,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailing: (@Composable () -> Unit)? = null
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(title, fontSize = 20.sp, color = Silver)
        Text(supporting, fontSize = 14.sp, color = Silver.copy(alpha = 0.7f))

        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                singleLine = true,
                isError = isError,
                visualTransformation = visualTransformation,
                trailingIcon = trailing,
                supportingText = if (isError) {
                    { Text(errorText, maxLines = 1, overflow = TextOverflow.Ellipsis) }
                } else null,
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Silver,
                    unfocusedTextColor = Silver,
                    errorTextColor = MaterialTheme.colorScheme.error,
                    focusedBorderColor = Silver,
                    unfocusedBorderColor = Silver,
                    errorBorderColor = MaterialTheme.colorScheme.error,
                    cursorColor = Silver,
                    errorCursorColor = MaterialTheme.colorScheme.error,
                    errorSupportingTextColor = MaterialTheme.colorScheme.error
                )
            )

            Button(
                onClick = onSave,
                modifier = Modifier.height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Silver,
                    contentColor = Graphite
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(text = saveLabel, fontSize = 16.sp)
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun SettingsScreenPreview() {
    SettingsScreen(
        modifier = Modifier,
    )
}