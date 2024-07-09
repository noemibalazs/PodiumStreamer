package theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontStyle
import podiumstreamer.composeapp.generated.resources.Res
import podiumstreamer.composeapp.generated.resources.philosopher_bold
import podiumstreamer.composeapp.generated.resources.philosopher_italic
import podiumstreamer.composeapp.generated.resources.philosopher_regular
import org.jetbrains.compose.resources.Font

@Composable
fun PhilosopherFontFamily() = FontFamily(
    Font(Res.font.philosopher_regular, weight = FontWeight.Normal),
    Font(Res.font.philosopher_italic, weight = FontWeight.Normal, FontStyle.Italic),
    Font(Res.font.philosopher_bold, weight = FontWeight.Bold)
)

@Composable
fun PhilosopherTypography() = Typography().run {

    val fontFamily = PhilosopherFontFamily()
    copy(
        displayLarge = displayLarge.copy(fontFamily = fontFamily),
        displayMedium = displayMedium.copy(fontFamily = fontFamily),
        displaySmall = displaySmall.copy(fontFamily = fontFamily),
        headlineLarge = headlineLarge.copy(fontFamily = fontFamily),
        headlineMedium = headlineMedium.copy(fontFamily = fontFamily),
        headlineSmall = headlineSmall.copy(fontFamily = fontFamily),
        titleLarge = titleLarge.copy(fontFamily = fontFamily),
        titleMedium = titleMedium.copy(fontFamily = fontFamily),
        titleSmall = titleSmall.copy(fontFamily = fontFamily),
        bodyLarge = bodyLarge.copy(fontFamily = fontFamily),
        bodyMedium = bodyMedium.copy(fontFamily = fontFamily),
        bodySmall = bodySmall.copy(fontFamily = fontFamily),
        labelLarge = labelLarge.copy(fontFamily = fontFamily),
        labelMedium = labelMedium.copy(fontFamily = fontFamily),
        labelSmall = labelSmall.copy(fontFamily = fontFamily)
    )
}
