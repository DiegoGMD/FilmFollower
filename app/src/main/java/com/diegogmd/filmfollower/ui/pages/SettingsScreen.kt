package com.diegogmd.filmfollower.ui.pages

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
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
import com.diegogmd.filmfollower.R
import com.diegogmd.filmfollower.SecureStorage
import com.diegogmd.filmfollower.ui.theme.Black
import com.diegogmd.filmfollower.ui.theme.DarkCoffee
import com.diegogmd.filmfollower.ui.theme.FilmTypography
import com.diegogmd.filmfollower.ui.theme.LightCaramel
import com.diegogmd.filmfollower.ui.theme.OliveWood
import com.diegogmd.filmfollower.ui.theme.Silver

// Card colour = your background nudged slightly towards the caramel, like the
// slightly lighter brown cards in the reference.
private val SectionCardColor = lerp(DarkCoffee, LightCaramel, 0.08f)

/**
 * One entry of the theme carousel.
 *
 * [previewRes] is a screenshot of the app in that theme (drop the PNGs in res/drawable,
 * e.g. R.drawable.theme_coffee). While it is null, a small drawn placeholder is shown
 * using [placeholderBackground] / [placeholderAccent].
 */
data class ThemeOption(
    val id: String,
    val name: String,
    @DrawableRes val previewRes: Int? = null,
    val placeholderBackground: Color = DarkCoffee,
    val placeholderAccent: Color = LightCaramel
)

// TODO: replace with your real themes and screenshots.
private val themeOptions = listOf(
    ThemeOption("OldFilm", "OldFilm", previewRes = null, DarkCoffee, LightCaramel),
    ThemeOption("OldFilm W&B", "OldFilm W&B", previewRes = null, Black, Silver),
    ThemeOption("Darkness", "Darkness", previewRes = null, DarkCoffee, LightCaramel)
)

@Composable
fun SettingsScreen(
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val isPreview = LocalInspectionMode.current

    // SecureStorage can't run inside the Android Studio preview, so fall back to dummy values there.
    var username by remember {
        mutableStateOf(if (isPreview) "GMD" else SecureStorage.getUsername(context))
    }
    var usernameError by remember { mutableStateOf(false) }

    var apiToken by remember {
        mutableStateOf(if (isPreview) "apiReadAccessToken" else SecureStorage.getApiReadAccessToken(context))
    }
    var apiTokenError by remember { mutableStateOf(false) }
    var showToken by remember { mutableStateOf(false) }

    // TODO: initialise from your saved theme preference
    var selectedThemeId by remember { mutableStateOf(themeOptions.first().id) }
    var themeExpanded by remember { mutableStateOf(false) }
    var showPreviews by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkCoffee)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        // Back button: tonal circle, like the reference
        Box(
            modifier = Modifier
                .padding(20.dp)
                .clickable(onClick = onBackClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = LightCaramel,
                modifier = Modifier.size(28.dp)
            )
        }

        Text(
            text = "Settings",
            style = FilmTypography.titleMedium.copy(fontSize = 44.sp, lineHeight = 52.sp),
            color = LightCaramel,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // ---------- Account ----------
        SettingsSection(label = "Account") {
            username?.let {
                SettingsFieldRow(
                    title = "Username",
                    supporting = "Shown on your profile",
                    value = it,
                    onValueChange = {
                        username = it
                        usernameError = it.isBlank()
                    },
                    isError = usernameError,
                    errorText = "Username can't be empty",
                    imeAction = ImeAction.Next,
                    saveLabel = stringResource(id = R.string.save),
                    onSave = {
                        val valid = username!!.isNotBlank()
                        usernameError = !valid
                        if (valid) SecureStorage.changeUsername(context, username!!.trim())
                    }
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // ---------- Data source ----------
        SettingsSection(label = "Data source") {
            apiToken?.let {
                SettingsFieldRow(
                    title = "API read access token",
                    supporting = "Used to load film data",
                    value = it,
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
                            Text(if (showToken) "Hide" else "Show", color = LightCaramel)
                        }
                    },
                    saveLabel = stringResource(id = R.string.save),
                    onSave = {
                        val valid = apiToken!!.isNotBlank()
                        apiTokenError = !valid
                        if (valid) {
                            // TODO: use your real setter name here
                            SecureStorage.changeApiReadAccessToken(context, apiToken!!.trim())
                        }
                    }
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // ---------- Appearance ----------
        SettingsSection(label = "Appearance") {
            // TODO: Make ThemeSelector and the preview themes button on the same row
            ThemeSelector(
                options = themeOptions,
                selectedId = selectedThemeId,
                expanded = themeExpanded,
                onToggle = { themeExpanded = !themeExpanded },
                onSelect = {
                    selectedThemeId = it
                    // TODO: persist + apply the theme
                }
            )

            Spacer(Modifier.height(16.dp))

            OutlinedButton(
                onClick = { showPreviews = true },
                modifier = Modifier.align(Alignment.End).height(48.dp),
                border = BorderStroke(1.dp, LightCaramel),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text("Preview themes", fontSize = 16.sp, color = LightCaramel)
            }
        }

        if (showPreviews) {
            ThemePreviewSheet(
                options = themeOptions,
                selectedId = selectedThemeId,
                onSelect = {
                    selectedThemeId = it
                    // TODO: persist + apply the theme
                },
                onDismiss = { showPreviews = false }
            )
        }

        Spacer(Modifier.height(32.dp))
    }
}

/** Rounded tonal card with a small label on top, like "Storage" / "Account" / "Playback". */
@Composable
private fun SettingsSection(
    label: String,
    bleed: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SectionCardColor)
            .padding(vertical = 20.dp)
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = LightCaramel.copy(alpha = 0.7f),
            modifier = Modifier.padding(horizontal = 24.dp)
        )
        Spacer(Modifier.height(16.dp))
        Column(
            modifier = Modifier.padding(horizontal = if (bleed) 0.dp else 24.dp),
            content = content
        )
    }
}

/** Horizontally snapping carousel of app screenshots; tapping a card selects that theme. */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ThemeCarousel(
    options: List<ThemeOption>,
    selectedId: String,
    onSelect: (String) -> Unit
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
        targetValue = if (selected) LightCaramel else OliveWood.copy(alpha = 0.5f),
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
                        .background(LightCaramel),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Selected",
                        tint = DarkCoffee,
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
            color = if (selected) LightCaramel else LightCaramel.copy(alpha = 0.7f)
        )
    }
}

/** Tiny fake app screen, only used until you add real screenshots. */
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

/** Collapsible theme picker: header shows the current theme, tap to unfold the list. */
@Composable
private fun ThemeSelector(
    options: List<ThemeOption>,
    selectedId: String,
    expanded: Boolean,
    onToggle: () -> Unit,
    onSelect: (String) -> Unit
) {
    val selected = options.first { it.id == selectedId }
    val chevronRotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "themeChevron"
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .clickable(onClick = onToggle),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Theme", fontSize = 20.sp, color = LightCaramel)
                Text(selected.name, fontSize = 14.sp, color = LightCaramel.copy(alpha = 0.7f))
            }
            Icon(
                imageVector = Icons.Filled.KeyboardArrowDown,
                contentDescription = if (expanded) "Collapse themes" else "Expand themes",
                tint = LightCaramel,
                modifier = Modifier.size(28.dp).rotate(chevronRotation)
            )
        }

        AnimatedVisibility(visible = expanded) {
            Column(modifier = Modifier.padding(top = 8.dp)) {
                options.forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onSelect(option.id) }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = option.id == selectedId,
                            onClick = { onSelect(option.id) },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = LightCaramel,
                                unselectedColor = OliveWood
                            )
                        )
                        Text(option.name, fontSize = 16.sp, color = LightCaramel)
                    }
                }
            }
        }
    }
}

/** Bottom sheet with the screenshot carousel. Tapping a card selects that theme. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ThemePreviewSheet(
    options: List<ThemeOption>,
    selectedId: String,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = SectionCardColor,
        contentColor = LightCaramel
    ) {
        Column(modifier = Modifier.navigationBarsPadding().padding(bottom = 16.dp)) {
            Text(
                text = "Theme previews",
                fontSize = 24.sp,
                color = LightCaramel,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            Text(
                text = "Tap a preview to switch",
                fontSize = 14.sp,
                color = LightCaramel.copy(alpha = 0.7f),
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
                    containerColor = LightCaramel,
                    contentColor = DarkCoffee
                ),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text("Done", fontSize = 16.sp)
            }
        }
    }
}

/** Title + supporting line, then a text field and a Save button underneath. */
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
        Text(title, fontSize = 20.sp, color = LightCaramel)
        Text(supporting, fontSize = 14.sp, color = LightCaramel.copy(alpha = 0.7f))

        Spacer(Modifier.height(12.dp))

        // TODO: Make OutlinedTextField and the save button on the same row

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
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
                focusedTextColor = LightCaramel,
                unfocusedTextColor = LightCaramel,
                errorTextColor = LightCaramel,
                focusedBorderColor = LightCaramel,
                unfocusedBorderColor = OliveWood,
                errorBorderColor = Color(0xFFFFB4AB),
                cursorColor = LightCaramel,
                errorCursorColor = Color(0xFFFFB4AB),
                errorSupportingTextColor = Color(0xFFFFB4AB)
            )
        )

        Spacer(Modifier.height(8.dp))

        Button(
            onClick = onSave,
            modifier = Modifier
                .align(Alignment.End)
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = LightCaramel,
                contentColor = DarkCoffee
            ),
            shape = RoundedCornerShape(24.dp)
        ) {
            Text(text = saveLabel, fontSize = 16.sp)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    SettingsScreen()
}