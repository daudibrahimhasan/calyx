package com.calyx.app.ui.screens.name

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.calyx.app.ui.theme.LufgaFontFamily
import com.calyx.app.ui.theme.VibrantGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NameInputScreen(
    onNameSubmitted: () -> Unit
) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("calyz_prefs", android.content.Context.MODE_PRIVATE) }
    
    var name by remember { mutableStateOf("") }
    
    // Background Gradient (Dark Theme Style)
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF0A1612), Color(0xFF000000))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Aesthetic Icon or Logo placeholder
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(VibrantGreen.copy(alpha = 0.1f), RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "👋",
                fontSize = 40.sp
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "What should we call you?",
            fontFamily = LufgaFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Your name stays on this device only.",
            fontSize = 14.sp,
            color = Color.White.copy(alpha = 0.5f)
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Name Input Field
        OutlinedTextField(
            value = name,
            onValueChange = { if (it.length <= 15) name = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Your Name (Optional)", color = Color.White.copy(alpha = 0.3f)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = VibrantGreen,
                unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = VibrantGreen,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Done
            )
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Continue Button
        Button(
            onClick = {
                // Save name and setup UUID
                prefs.edit().apply {
                    putString("user_name", name.trim())
                    // Ensure UUID exists
                    if (prefs.getString("user_id", null) == null) {
                        putString("user_id", java.util.UUID.randomUUID().toString())
                    }
                    putBoolean("onboarding_complete", true)
                }.apply()
                onNameSubmitted()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = VibrantGreen)
        ) {
            Text(
                text = if (name.isBlank()) "Skip" else "Continue",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.Black
            )
        }
    }
}
