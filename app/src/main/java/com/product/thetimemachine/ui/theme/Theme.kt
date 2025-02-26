package com.product.thetimemachine.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.backgroundDark
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.backgroundDarkHighContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.backgroundDarkMediumContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.backgroundLight
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.backgroundLightHighContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.backgroundLightMediumContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.errorContainerDark
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.errorContainerDarkHighContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.errorContainerDarkMediumContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.errorContainerLight
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.errorContainerLightHighContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.errorContainerLightMediumContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.errorDark
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.errorDarkHighContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.errorDarkMediumContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.errorLight
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.errorLightHighContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.errorLightMediumContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.inverseOnSurfaceDark
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.inverseOnSurfaceDarkHighContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.inverseOnSurfaceDarkMediumContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.inverseOnSurfaceLight
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.inverseOnSurfaceLightHighContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.inverseOnSurfaceLightMediumContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.inversePrimaryDark
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.inversePrimaryDarkHighContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.inversePrimaryDarkMediumContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.inversePrimaryLight
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.inversePrimaryLightHighContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.inversePrimaryLightMediumContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.inverseSurfaceDark
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.inverseSurfaceDarkHighContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.inverseSurfaceDarkMediumContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.inverseSurfaceLight
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.inverseSurfaceLightHighContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.inverseSurfaceLightMediumContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.onBackgroundDark
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.onBackgroundDarkHighContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.onBackgroundDarkMediumContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.onBackgroundLight
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.onBackgroundLightHighContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.onBackgroundLightMediumContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.onErrorContainerDark
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.onErrorContainerDarkHighContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.onErrorContainerDarkMediumContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.onErrorContainerLight
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.onErrorContainerLightHighContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.onErrorContainerLightMediumContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.onErrorDark
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.onErrorDarkHighContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.onErrorDarkMediumContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.onErrorLight
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.onErrorLightHighContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.onErrorLightMediumContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.onPrimaryContainerDark
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.onPrimaryContainerDarkHighContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.onPrimaryContainerDarkMediumContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.onPrimaryContainerLight
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.onPrimaryContainerLightHighContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.onPrimaryContainerLightMediumContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.onPrimaryDark
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.onPrimaryDarkHighContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.onPrimaryDarkMediumContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.onPrimaryLight
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.onPrimaryLightHighContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.onPrimaryLightMediumContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.onSecondaryContainerDark
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.onSecondaryContainerDarkHighContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.onSecondaryContainerDarkMediumContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.onSecondaryContainerLight
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.onSecondaryContainerLightHighContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.onSecondaryContainerLightMediumContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.onSecondaryDark
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.onSecondaryDarkHighContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.onSecondaryDarkMediumContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.onSecondaryLight
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.onSecondaryLightHighContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.onSecondaryLightMediumContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.onSurfaceDark
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.onSurfaceDarkHighContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.onSurfaceDarkMediumContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.onSurfaceLight
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.onSurfaceLightHighContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.onSurfaceLightMediumContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.onSurfaceVariantDark
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.onSurfaceVariantDarkHighContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.onSurfaceVariantDarkMediumContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.onSurfaceVariantLight
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.onSurfaceVariantLightHighContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.onSurfaceVariantLightMediumContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.onTertiaryContainerDark
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.onTertiaryContainerDarkHighContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.onTertiaryContainerDarkMediumContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.onTertiaryContainerLight
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.onTertiaryContainerLightHighContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.onTertiaryContainerLightMediumContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.onTertiaryDark
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.onTertiaryDarkHighContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.onTertiaryDarkMediumContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.onTertiaryLight
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.onTertiaryLightHighContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.onTertiaryLightMediumContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.outlineDark
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.outlineDarkHighContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.outlineDarkMediumContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.outlineLight
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.outlineLightHighContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.outlineLightMediumContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.outlineVariantDark
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.outlineVariantDarkHighContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.outlineVariantDarkMediumContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.outlineVariantLight
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.outlineVariantLightHighContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.outlineVariantLightMediumContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.primaryContainerDark
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.primaryContainerDarkHighContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.primaryContainerDarkMediumContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.primaryContainerLight
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.primaryContainerLightHighContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.primaryContainerLightMediumContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.primaryDark
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.primaryDarkHighContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.primaryDarkMediumContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.primaryLight
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.primaryLightHighContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.primaryLightMediumContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.scrimDark
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.scrimDarkHighContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.scrimDarkMediumContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.scrimLight
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.scrimLightHighContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.scrimLightMediumContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.secondaryContainerDark
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.secondaryContainerDarkHighContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.secondaryContainerDarkMediumContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.secondaryContainerLight
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.secondaryContainerLightHighContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.secondaryContainerLightMediumContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.secondaryDark
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.secondaryDarkHighContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.secondaryDarkMediumContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.secondaryLight
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.secondaryLightHighContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.secondaryLightMediumContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.surfaceBrightDark
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.surfaceBrightDarkHighContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.surfaceBrightDarkMediumContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.surfaceBrightLight
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.surfaceBrightLightHighContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.surfaceBrightLightMediumContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.surfaceContainerDark
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.surfaceContainerDarkHighContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.surfaceContainerDarkMediumContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.surfaceContainerHighDark
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.surfaceContainerHighDarkHighContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.surfaceContainerHighDarkMediumContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.surfaceContainerHighLight
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.surfaceContainerHighLightHighContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.surfaceContainerHighLightMediumContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.surfaceContainerHighestDark
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.surfaceContainerHighestDarkHighContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.surfaceContainerHighestDarkMediumContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.surfaceContainerHighestLight
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.surfaceContainerHighestLightHighContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.surfaceContainerHighestLightMediumContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.surfaceContainerLight
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.surfaceContainerLightHighContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.surfaceContainerLightMediumContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.surfaceContainerLowDark
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.surfaceContainerLowDarkHighContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.surfaceContainerLowDarkMediumContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.surfaceContainerLowLight
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.surfaceContainerLowLightHighContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.surfaceContainerLowLightMediumContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.surfaceContainerLowestDark
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.surfaceContainerLowestDarkHighContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.surfaceContainerLowestDarkMediumContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.surfaceContainerLowestLight
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.surfaceContainerLowestLightHighContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.surfaceContainerLowestLightMediumContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.surfaceDark
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.surfaceDarkHighContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.surfaceDarkMediumContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.surfaceDimDark
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.surfaceDimDarkHighContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.surfaceDimDarkMediumContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.surfaceDimLight
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.surfaceDimLightHighContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.surfaceDimLightMediumContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.surfaceLight
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.surfaceLightHighContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.surfaceLightMediumContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.surfaceVariantDark
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.surfaceVariantDarkHighContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.surfaceVariantDarkMediumContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.surfaceVariantLight
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.surfaceVariantLightHighContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.surfaceVariantLightMediumContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.tertiaryContainerDark
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.tertiaryContainerDarkHighContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.tertiaryContainerDarkMediumContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.tertiaryContainerLight
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.tertiaryContainerLightHighContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.tertiaryContainerLightMediumContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.tertiaryDark
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.tertiaryDarkHighContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.tertiaryDarkMediumContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.tertiaryLight
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.tertiaryLightHighContrast
import com.product.thetimemachine.ui.theme.OliveGreenColorBase.tertiaryLightMediumContrast

/*
    Based on:
    Multiple themes/colors in Kotlin Multiplatform Project (Android, iOS, Desktop applications)
    https://medium.com/@shivathapaa/multiple-themes-colors-in-kotlin-multiplatform-project-android-ios-desktop-applications-c10e05b54ab5
*/

typealias lightScheme = ColorScheme
typealias darkScheme = ColorScheme

enum class MyThemeColor {
    REDCYAN, OLIVEGREEN,
}
fun createColorSchemes(palette: MaterialThemeColorsPalette): Pair<lightScheme, darkScheme> {
    val lightScheme = lightColorScheme(
        primary = primaryLight,
        onPrimary = onPrimaryLight,
        primaryContainer = primaryContainerLight,
        onPrimaryContainer = onPrimaryContainerLight,
        secondary = secondaryLight,
        onSecondary = onSecondaryLight,
        secondaryContainer = secondaryContainerLight,
        onSecondaryContainer = onSecondaryContainerLight,
        tertiary = tertiaryLight,
        onTertiary = onTertiaryLight,
        tertiaryContainer = tertiaryContainerLight,
        onTertiaryContainer = onTertiaryContainerLight,
        error = errorLight,
        onError = onErrorLight,
        errorContainer = errorContainerLight,
        onErrorContainer = onErrorContainerLight,
        background = backgroundLight,
        onBackground = onBackgroundLight,
        surface = surfaceLight,
        onSurface = onSurfaceLight,
        surfaceVariant = surfaceVariantLight,
        onSurfaceVariant = onSurfaceVariantLight,
        outline = outlineLight,
        outlineVariant = outlineVariantLight,
        scrim = scrimLight,
        inverseSurface = inverseSurfaceLight,
        inverseOnSurface = inverseOnSurfaceLight,
        inversePrimary = inversePrimaryLight,
        surfaceDim = surfaceDimLight,
        surfaceBright = surfaceBrightLight,
        surfaceContainerLowest = surfaceContainerLowestLight,
        surfaceContainerLow = surfaceContainerLowLight,
        surfaceContainer = surfaceContainerLight,
        surfaceContainerHigh = surfaceContainerHighLight,
        surfaceContainerHighest = surfaceContainerHighestLight,
    )

    val darkScheme = darkColorScheme(
        primary = primaryDark,
        onPrimary = onPrimaryDark,
        primaryContainer = primaryContainerDark,
        onPrimaryContainer = onPrimaryContainerDark,
        secondary = secondaryDark,
        onSecondary = onSecondaryDark,
        secondaryContainer = secondaryContainerDark,
        onSecondaryContainer = onSecondaryContainerDark,
        tertiary = tertiaryDark,
        onTertiary = onTertiaryDark,
        tertiaryContainer = tertiaryContainerDark,
        onTertiaryContainer = onTertiaryContainerDark,
        error = errorDark,
        onError = onErrorDark,
        errorContainer = errorContainerDark,
        onErrorContainer = onErrorContainerDark,
        background = backgroundDark,
        onBackground = onBackgroundDark,
        surface = surfaceDark,
        onSurface = onSurfaceDark,
        surfaceVariant = surfaceVariantDark,
        onSurfaceVariant = onSurfaceVariantDark,
        outline = outlineDark,
        outlineVariant = outlineVariantDark,
        scrim = scrimDark,
        inverseSurface = inverseSurfaceDark,
        inverseOnSurface = inverseOnSurfaceDark,
        inversePrimary = inversePrimaryDark,
        surfaceDim = surfaceDimDark,
        surfaceBright = surfaceBrightDark,
        surfaceContainerLowest = surfaceContainerLowestDark,
        surfaceContainerLow = surfaceContainerLowDark,
        surfaceContainer = surfaceContainerDark,
        surfaceContainerHigh = surfaceContainerHighDark,
        surfaceContainerHighest = surfaceContainerHighestDark,
    )

    val mediumContrastLightColorScheme = lightColorScheme(
        primary = primaryLightMediumContrast,
        onPrimary = onPrimaryLightMediumContrast,
        primaryContainer = primaryContainerLightMediumContrast,
        onPrimaryContainer = onPrimaryContainerLightMediumContrast,
        secondary = secondaryLightMediumContrast,
        onSecondary = onSecondaryLightMediumContrast,
        secondaryContainer = secondaryContainerLightMediumContrast,
        onSecondaryContainer = onSecondaryContainerLightMediumContrast,
        tertiary = tertiaryLightMediumContrast,
        onTertiary = onTertiaryLightMediumContrast,
        tertiaryContainer = tertiaryContainerLightMediumContrast,
        onTertiaryContainer = onTertiaryContainerLightMediumContrast,
        error = errorLightMediumContrast,
        onError = onErrorLightMediumContrast,
        errorContainer = errorContainerLightMediumContrast,
        onErrorContainer = onErrorContainerLightMediumContrast,
        background = backgroundLightMediumContrast,
        onBackground = onBackgroundLightMediumContrast,
        surface = surfaceLightMediumContrast,
        onSurface = onSurfaceLightMediumContrast,
        surfaceVariant = surfaceVariantLightMediumContrast,
        onSurfaceVariant = onSurfaceVariantLightMediumContrast,
        outline = outlineLightMediumContrast,
        outlineVariant = outlineVariantLightMediumContrast,
        scrim = scrimLightMediumContrast,
        inverseSurface = inverseSurfaceLightMediumContrast,
        inverseOnSurface = inverseOnSurfaceLightMediumContrast,
        inversePrimary = inversePrimaryLightMediumContrast,
        surfaceDim = surfaceDimLightMediumContrast,
        surfaceBright = surfaceBrightLightMediumContrast,
        surfaceContainerLowest = surfaceContainerLowestLightMediumContrast,
        surfaceContainerLow = surfaceContainerLowLightMediumContrast,
        surfaceContainer = surfaceContainerLightMediumContrast,
        surfaceContainerHigh = surfaceContainerHighLightMediumContrast,
        surfaceContainerHighest = surfaceContainerHighestLightMediumContrast,
    )

    val highContrastLightColorScheme = lightColorScheme(
        primary = primaryLightHighContrast,
        onPrimary = onPrimaryLightHighContrast,
        primaryContainer = primaryContainerLightHighContrast,
        onPrimaryContainer = onPrimaryContainerLightHighContrast,
        secondary = secondaryLightHighContrast,
        onSecondary = onSecondaryLightHighContrast,
        secondaryContainer = secondaryContainerLightHighContrast,
        onSecondaryContainer = onSecondaryContainerLightHighContrast,
        tertiary = tertiaryLightHighContrast,
        onTertiary = onTertiaryLightHighContrast,
        tertiaryContainer = tertiaryContainerLightHighContrast,
        onTertiaryContainer = onTertiaryContainerLightHighContrast,
        error = errorLightHighContrast,
        onError = onErrorLightHighContrast,
        errorContainer = errorContainerLightHighContrast,
        onErrorContainer = onErrorContainerLightHighContrast,
        background = backgroundLightHighContrast,
        onBackground = onBackgroundLightHighContrast,
        surface = surfaceLightHighContrast,
        onSurface = onSurfaceLightHighContrast,
        surfaceVariant = surfaceVariantLightHighContrast,
        onSurfaceVariant = onSurfaceVariantLightHighContrast,
        outline = outlineLightHighContrast,
        outlineVariant = outlineVariantLightHighContrast,
        scrim = scrimLightHighContrast,
        inverseSurface = inverseSurfaceLightHighContrast,
        inverseOnSurface = inverseOnSurfaceLightHighContrast,
        inversePrimary = inversePrimaryLightHighContrast,
        surfaceDim = surfaceDimLightHighContrast,
        surfaceBright = surfaceBrightLightHighContrast,
        surfaceContainerLowest = surfaceContainerLowestLightHighContrast,
        surfaceContainerLow = surfaceContainerLowLightHighContrast,
        surfaceContainer = surfaceContainerLightHighContrast,
        surfaceContainerHigh = surfaceContainerHighLightHighContrast,
        surfaceContainerHighest = surfaceContainerHighestLightHighContrast,
    )

    val mediumContrastDarkColorScheme = darkColorScheme(
        primary = primaryDarkMediumContrast,
        onPrimary = onPrimaryDarkMediumContrast,
        primaryContainer = primaryContainerDarkMediumContrast,
        onPrimaryContainer = onPrimaryContainerDarkMediumContrast,
        secondary = secondaryDarkMediumContrast,
        onSecondary = onSecondaryDarkMediumContrast,
        secondaryContainer = secondaryContainerDarkMediumContrast,
        onSecondaryContainer = onSecondaryContainerDarkMediumContrast,
        tertiary = tertiaryDarkMediumContrast,
        onTertiary = onTertiaryDarkMediumContrast,
        tertiaryContainer = tertiaryContainerDarkMediumContrast,
        onTertiaryContainer = onTertiaryContainerDarkMediumContrast,
        error = errorDarkMediumContrast,
        onError = onErrorDarkMediumContrast,
        errorContainer = errorContainerDarkMediumContrast,
        onErrorContainer = onErrorContainerDarkMediumContrast,
        background = backgroundDarkMediumContrast,
        onBackground = onBackgroundDarkMediumContrast,
        surface = surfaceDarkMediumContrast,
        onSurface = onSurfaceDarkMediumContrast,
        surfaceVariant = surfaceVariantDarkMediumContrast,
        onSurfaceVariant = onSurfaceVariantDarkMediumContrast,
        outline = outlineDarkMediumContrast,
        outlineVariant = outlineVariantDarkMediumContrast,
        scrim = scrimDarkMediumContrast,
        inverseSurface = inverseSurfaceDarkMediumContrast,
        inverseOnSurface = inverseOnSurfaceDarkMediumContrast,
        inversePrimary = inversePrimaryDarkMediumContrast,
        surfaceDim = surfaceDimDarkMediumContrast,
        surfaceBright = surfaceBrightDarkMediumContrast,
        surfaceContainerLowest = surfaceContainerLowestDarkMediumContrast,
        surfaceContainerLow = surfaceContainerLowDarkMediumContrast,
        surfaceContainer = surfaceContainerDarkMediumContrast,
        surfaceContainerHigh = surfaceContainerHighDarkMediumContrast,
        surfaceContainerHighest = surfaceContainerHighestDarkMediumContrast,
    )

    val highContrastDarkColorScheme = darkColorScheme(
        primary = primaryDarkHighContrast,
        onPrimary = onPrimaryDarkHighContrast,
        primaryContainer = primaryContainerDarkHighContrast,
        onPrimaryContainer = onPrimaryContainerDarkHighContrast,
        secondary = secondaryDarkHighContrast,
        onSecondary = onSecondaryDarkHighContrast,
        secondaryContainer = secondaryContainerDarkHighContrast,
        onSecondaryContainer = onSecondaryContainerDarkHighContrast,
        tertiary = tertiaryDarkHighContrast,
        onTertiary = onTertiaryDarkHighContrast,
        tertiaryContainer = tertiaryContainerDarkHighContrast,
        onTertiaryContainer = onTertiaryContainerDarkHighContrast,
        error = errorDarkHighContrast,
        onError = onErrorDarkHighContrast,
        errorContainer = errorContainerDarkHighContrast,
        onErrorContainer = onErrorContainerDarkHighContrast,
        background = backgroundDarkHighContrast,
        onBackground = onBackgroundDarkHighContrast,
        surface = surfaceDarkHighContrast,
        onSurface = onSurfaceDarkHighContrast,
        surfaceVariant = surfaceVariantDarkHighContrast,
        onSurfaceVariant = onSurfaceVariantDarkHighContrast,
        outline = outlineDarkHighContrast,
        outlineVariant = outlineVariantDarkHighContrast,
        scrim = scrimDarkHighContrast,
        inverseSurface = inverseSurfaceDarkHighContrast,
        inverseOnSurface = inverseOnSurfaceDarkHighContrast,
        inversePrimary = inversePrimaryDarkHighContrast,
        surfaceDim = surfaceDimDarkHighContrast,
        surfaceBright = surfaceBrightDarkHighContrast,
        surfaceContainerLowest = surfaceContainerLowestDarkHighContrast,
        surfaceContainerLow = surfaceContainerLowDarkHighContrast,
        surfaceContainer = surfaceContainerDarkHighContrast,
        surfaceContainerHigh = surfaceContainerHighDarkHighContrast,
        surfaceContainerHighest = surfaceContainerHighestDarkHighContrast,
    )

    return Pair(lightScheme, darkScheme)
}
fun getSelectedThemeColors(myThemeSelected: MyThemeColor) : Pair<lightScheme, darkScheme> {
    return when (myThemeSelected) {
        MyThemeColor.REDCYAN -> createColorSchemes(RedCyanColorBase)
        MyThemeColor.OLIVEGREEN -> createColorSchemes(OliveGreenColorBase)
    }
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
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    selectedTheme: MyThemeColor = MyThemeColor.REDCYAN,
    content: @Composable() () -> Unit
) {
    val (lightScheme, darkScheme) = getSelectedThemeColors(selectedTheme)

  val colorScheme = when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
          val context = LocalContext.current
          if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }
      
      darkTheme -> darkScheme
      else -> lightScheme
  }

  MaterialTheme(
    colorScheme = lightScheme,//colorScheme,
    typography = AppTypography,
    content = content
  )
}

