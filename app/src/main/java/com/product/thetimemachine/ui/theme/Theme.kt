package com.product.thetimemachine.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.product.thetimemachine.Application.TheTimeMachineApp.appContext
import com.product.thetimemachine.ui.getPrefTheme


/*
    Based on:
    Multiple themes/colors in Kotlin Multiplatform Project (Android, iOS, Desktop applications)
    https://medium.com/@shivathapaa/multiple-themes-colors-in-kotlin-multiplatform-project-android-ios-desktop-applications-c10e05b54ab5
*/

typealias lightScheme= ColorScheme
typealias darkScheme= ColorScheme

/*
enum class MyThemeColor (color : String) {
    REDCYAN("REDCYAN"), OLIVEGREEN("OLIVEGREEN"),
}
*/

fun createColorSchemes(palette: MaterialThemeColorsPalette): Pair<lightScheme, darkScheme> {
    
    val lightScheme= lightColorScheme(
        primary= palette.primaryLight,
        onPrimary= palette.onPrimaryLight,
        primaryContainer= palette.primaryContainerLight,
        onPrimaryContainer= palette.onPrimaryContainerLight,
        secondary= palette.secondaryLight,
        onSecondary= palette.onSecondaryLight,
        secondaryContainer= palette.secondaryContainerLight,
        onSecondaryContainer= palette.onSecondaryContainerLight,
        tertiary= palette.tertiaryLight,
        onTertiary= palette.onTertiaryLight,
        tertiaryContainer= palette.tertiaryContainerLight,
        onTertiaryContainer= palette.onTertiaryContainerLight,
        error= palette.errorLight,
        onError= palette.onErrorLight,
        errorContainer= palette.errorContainerLight,
        onErrorContainer= palette.onErrorContainerLight,
        background= palette.backgroundLight,
        onBackground= palette.onBackgroundLight,
        surface= palette.surfaceLight,
        onSurface= palette.onSurfaceLight,
        surfaceVariant= palette.surfaceVariantLight,
        onSurfaceVariant= palette.onSurfaceVariantLight,
        outline= palette.outlineLight,
        outlineVariant= palette.outlineVariantLight,
        scrim= palette.scrimLight,
        inverseSurface= palette.inverseSurfaceLight,
        inverseOnSurface= palette.inverseOnSurfaceLight,
        inversePrimary= palette.inversePrimaryLight,
        surfaceDim= palette.surfaceDimLight,
        surfaceBright= palette.surfaceBrightLight,
        surfaceContainerLowest= palette.surfaceContainerLowestLight,
        surfaceContainerLow= palette.surfaceContainerLowLight,
        surfaceContainer= palette.surfaceContainerLight,
        surfaceContainerHigh= palette.surfaceContainerHighLight,
        surfaceContainerHighest= palette.surfaceContainerHighestLight,
    )
        

    val darkScheme =  darkColorScheme(
        primary= palette.primaryDark,
        onPrimary= palette.onPrimaryDark,
        primaryContainer= palette.primaryContainerDark,
        onPrimaryContainer= palette.onPrimaryContainerDark,
        secondary= palette.secondaryDark,
        onSecondary= palette.onSecondaryDark,
        secondaryContainer= palette.secondaryContainerDark,
        onSecondaryContainer= palette.onSecondaryContainerDark,
        tertiary= palette.tertiaryDark,
        onTertiary= palette.onTertiaryDark,
        tertiaryContainer= palette.tertiaryContainerDark,
        onTertiaryContainer= palette.onTertiaryContainerDark,
        error= palette.errorDark,
        onError= palette.onErrorDark,
        errorContainer= palette.errorContainerDark,
        onErrorContainer= palette.onErrorContainerDark,
        background= palette.backgroundDark,
        onBackground= palette.onBackgroundDark,
        surface= palette.surfaceDark,
        onSurface= palette.onSurfaceDark,
        surfaceVariant= palette.surfaceVariantDark,
        onSurfaceVariant= palette.onSurfaceVariantDark,
        outline= palette.outlineDark,
        outlineVariant= palette.outlineVariantDark,
        scrim= palette.scrimDark,
        inverseSurface= palette.inverseSurfaceDark,
        inverseOnSurface= palette.inverseOnSurfaceDark,
        inversePrimary= palette.inversePrimaryDark,
        surfaceDim= palette.surfaceDimDark,
        surfaceBright= palette.surfaceBrightDark,
        surfaceContainerLowest= palette.surfaceContainerLowestDark,
        surfaceContainerLow= palette.surfaceContainerLowDark,
        surfaceContainer= palette.surfaceContainerDark,
        surfaceContainerHigh= palette.surfaceContainerHighDark,
        surfaceContainerHighest= palette.surfaceContainerHighestDark,
    )

    val mediumContrastLightColorScheme = lightColorScheme(
        primary= palette.primaryLightMediumContrast,
        onPrimary= palette.onPrimaryLightMediumContrast,
        primaryContainer= palette.primaryContainerLightMediumContrast,
        onPrimaryContainer= palette.onPrimaryContainerLightMediumContrast,
        secondary= palette.secondaryLightMediumContrast,
        onSecondary= palette.onSecondaryLightMediumContrast,
        secondaryContainer= palette.secondaryContainerLightMediumContrast,
        onSecondaryContainer= palette.onSecondaryContainerLightMediumContrast,
        tertiary= palette.tertiaryLightMediumContrast,
        onTertiary= palette.onTertiaryLightMediumContrast,
        tertiaryContainer= palette.tertiaryContainerLightMediumContrast,
        onTertiaryContainer= palette.onTertiaryContainerLightMediumContrast,
        error= palette.errorLightMediumContrast,
        onError= palette.onErrorLightMediumContrast,
        errorContainer= palette.errorContainerLightMediumContrast,
        onErrorContainer= palette.onErrorContainerLightMediumContrast,
        background= palette.backgroundLightMediumContrast,
        onBackground= palette.onBackgroundLightMediumContrast,
        surface= palette.surfaceLightMediumContrast,
        onSurface= palette.onSurfaceLightMediumContrast,
        surfaceVariant= palette.surfaceVariantLightMediumContrast,
        onSurfaceVariant= palette.onSurfaceVariantLightMediumContrast,
        outline= palette.outlineLightMediumContrast,
        outlineVariant= palette.outlineVariantLightMediumContrast,
        scrim= palette.scrimLightMediumContrast,
        inverseSurface= palette.inverseSurfaceLightMediumContrast,
        inverseOnSurface= palette.inverseOnSurfaceLightMediumContrast,
        inversePrimary= palette.inversePrimaryLightMediumContrast,
        surfaceDim= palette.surfaceDimLightMediumContrast,
        surfaceBright= palette.surfaceBrightLightMediumContrast,
        surfaceContainerLowest= palette.surfaceContainerLowestLightMediumContrast,
        surfaceContainerLow= palette.surfaceContainerLowLightMediumContrast,
        surfaceContainer= palette.surfaceContainerLightMediumContrast,
        surfaceContainerHigh= palette.surfaceContainerHighLightMediumContrast,
        surfaceContainerHighest= palette.surfaceContainerHighestLightMediumContrast,
    )

    val highContrastLightColorScheme = lightColorScheme(
        primary= palette.primaryLightHighContrast,
        onPrimary= palette.onPrimaryLightHighContrast,
        primaryContainer= palette.primaryContainerLightHighContrast,
        onPrimaryContainer= palette.onPrimaryContainerLightHighContrast,
        secondary= palette.secondaryLightHighContrast,
        onSecondary= palette.onSecondaryLightHighContrast,
        secondaryContainer= palette.secondaryContainerLightHighContrast,
        onSecondaryContainer= palette.onSecondaryContainerLightHighContrast,
        tertiary= palette.tertiaryLightHighContrast,
        onTertiary= palette.onTertiaryLightHighContrast,
        tertiaryContainer= palette.tertiaryContainerLightHighContrast,
        onTertiaryContainer= palette.onTertiaryContainerLightHighContrast,
        error= palette.errorLightHighContrast,
        onError= palette.onErrorLightHighContrast,
        errorContainer= palette.errorContainerLightHighContrast,
        onErrorContainer= palette.onErrorContainerLightHighContrast,
        background= palette.backgroundLightHighContrast,
        onBackground= palette.onBackgroundLightHighContrast,
        surface= palette.surfaceLightHighContrast,
        onSurface= palette.onSurfaceLightHighContrast,
        surfaceVariant= palette.surfaceVariantLightHighContrast,
        onSurfaceVariant= palette.onSurfaceVariantLightHighContrast,
        outline= palette.outlineLightHighContrast,
        outlineVariant= palette.outlineVariantLightHighContrast,
        scrim= palette.scrimLightHighContrast,
        inverseSurface= palette.inverseSurfaceLightHighContrast,
        inverseOnSurface= palette.inverseOnSurfaceLightHighContrast,
        inversePrimary= palette.inversePrimaryLightHighContrast,
        surfaceDim= palette.surfaceDimLightHighContrast,
        surfaceBright= palette.surfaceBrightLightHighContrast,
        surfaceContainerLowest= palette.surfaceContainerLowestLightHighContrast,
        surfaceContainerLow= palette.surfaceContainerLowLightHighContrast,
        surfaceContainer= palette.surfaceContainerLightHighContrast,
        surfaceContainerHigh= palette.surfaceContainerHighLightHighContrast,
        surfaceContainerHighest= palette.surfaceContainerHighestLightHighContrast,
    )

    val mediumContrastDarkColorScheme = darkColorScheme(
        primary= palette.primaryDarkMediumContrast,
        onPrimary= palette.onPrimaryDarkMediumContrast,
        primaryContainer= palette.primaryContainerDarkMediumContrast,
        onPrimaryContainer= palette.onPrimaryContainerDarkMediumContrast,
        secondary= palette.secondaryDarkMediumContrast,
        onSecondary= palette.onSecondaryDarkMediumContrast,
        secondaryContainer= palette.secondaryContainerDarkMediumContrast,
        onSecondaryContainer= palette.onSecondaryContainerDarkMediumContrast,
        tertiary= palette.tertiaryDarkMediumContrast,
        onTertiary= palette.onTertiaryDarkMediumContrast,
        tertiaryContainer= palette.tertiaryContainerDarkMediumContrast,
        onTertiaryContainer= palette.onTertiaryContainerDarkMediumContrast,
        error= palette.errorDarkMediumContrast,
        onError= palette.onErrorDarkMediumContrast,
        errorContainer= palette.errorContainerDarkMediumContrast,
        onErrorContainer= palette.onErrorContainerDarkMediumContrast,
        background= palette.backgroundDarkMediumContrast,
        onBackground= palette.onBackgroundDarkMediumContrast,
        surface= palette.surfaceDarkMediumContrast,
        onSurface= palette.onSurfaceDarkMediumContrast,
        surfaceVariant= palette.surfaceVariantDarkMediumContrast,
        onSurfaceVariant= palette.onSurfaceVariantDarkMediumContrast,
        outline= palette.outlineDarkMediumContrast,
        outlineVariant= palette.outlineVariantDarkMediumContrast,
        scrim= palette.scrimDarkMediumContrast,
        inverseSurface= palette.inverseSurfaceDarkMediumContrast,
        inverseOnSurface= palette.inverseOnSurfaceDarkMediumContrast,
        inversePrimary= palette.inversePrimaryDarkMediumContrast,
        surfaceDim= palette.surfaceDimDarkMediumContrast,
        surfaceBright= palette.surfaceBrightDarkMediumContrast,
        surfaceContainerLowest= palette.surfaceContainerLowestDarkMediumContrast,
        surfaceContainerLow= palette.surfaceContainerLowDarkMediumContrast,
        surfaceContainer= palette.surfaceContainerDarkMediumContrast,
        surfaceContainerHigh= palette.surfaceContainerHighDarkMediumContrast,
        surfaceContainerHighest= palette.surfaceContainerHighestDarkMediumContrast,
    )

    val highContrastDarkColorScheme = darkColorScheme(
        primary= palette.primaryDarkHighContrast,
        onPrimary= palette.onPrimaryDarkHighContrast,
        primaryContainer= palette.primaryContainerDarkHighContrast,
        onPrimaryContainer= palette.onPrimaryContainerDarkHighContrast,
        secondary= palette.secondaryDarkHighContrast,
        onSecondary= palette.onSecondaryDarkHighContrast,
        secondaryContainer= palette.secondaryContainerDarkHighContrast,
        onSecondaryContainer= palette.onSecondaryContainerDarkHighContrast,
        tertiary= palette.tertiaryDarkHighContrast,
        onTertiary= palette.onTertiaryDarkHighContrast,
        tertiaryContainer= palette.tertiaryContainerDarkHighContrast,
        onTertiaryContainer= palette.onTertiaryContainerDarkHighContrast,
        error= palette.errorDarkHighContrast,
        onError= palette.onErrorDarkHighContrast,
        errorContainer= palette.errorContainerDarkHighContrast,
        onErrorContainer= palette.onErrorContainerDarkHighContrast,
        background= palette.backgroundDarkHighContrast,
        onBackground= palette.onBackgroundDarkHighContrast,
        surface= palette.surfaceDarkHighContrast,
        onSurface= palette.onSurfaceDarkHighContrast,
        surfaceVariant= palette.surfaceVariantDarkHighContrast,
        onSurfaceVariant= palette.onSurfaceVariantDarkHighContrast,
        outline= palette.outlineDarkHighContrast,
        outlineVariant= palette.outlineVariantDarkHighContrast,
        scrim= palette.scrimDarkHighContrast,
        inverseSurface= palette.inverseSurfaceDarkHighContrast,
        inverseOnSurface= palette.inverseOnSurfaceDarkHighContrast,
        inversePrimary= palette.inversePrimaryDarkHighContrast,
        surfaceDim= palette.surfaceDimDarkHighContrast,
        surfaceBright= palette.surfaceBrightDarkHighContrast,
        surfaceContainerLowest= palette.surfaceContainerLowestDarkHighContrast,
        surfaceContainerLow= palette.surfaceContainerLowDarkHighContrast,
        surfaceContainer= palette.surfaceContainerDarkHighContrast,
        surfaceContainerHigh= palette.surfaceContainerHighDarkHighContrast,
        surfaceContainerHighest= palette.surfaceContainerHighestDarkHighContrast,
    )

    return Pair(lightScheme, darkScheme)
}
fun getSelectedThemeColors(myThemeSelected: String) : Pair<lightScheme, darkScheme> {
    return createColorSchemes(getPalette(myThemeSelected)!!)
}

@Composable
fun getCurrentColorScheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    color: String = "",
    ) : ColorScheme {

    val (lightScheme, darkScheme) = when (color){
        "" -> getSelectedThemeColors(getPrefTheme(appContext))
        else -> getSelectedThemeColors(color)
}

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> darkScheme
        else -> lightScheme
    }
    return colorScheme
}

@Immutable
data class ColorFamily(
    val color: Color,
    val onColor: Color,
    val colorContainer: Color,
    val onColorContainer: Color
)

val unspecified_scheme = ColorFamily(
    Color.Unspecified, Color.Unspecified, Color.Unspecified, Color.Unspecified
)



@Composable
fun AppTheme(
    theme: String = "",
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable() () -> Unit
) {

  var colorScheme = getCurrentColorScheme(darkTheme = darkTheme, dynamicColor = dynamicColor, color = theme)

  MaterialTheme(
    colorScheme = colorScheme,
    typography = AppTypography,
    content = content
  )
}

