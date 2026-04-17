package com.cloowork.clooforward

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.colorspace.ColorSpaces
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat

val CtaColor = Color(red = 0.859f, green = 0.216f, blue = 0.267f, colorSpace = ColorSpaces.DisplayP3)

@Composable
fun ClooForwardTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = CtaColor,
            background = Color(0xFF121212),
            surface = Color(0xFF1E1E1E),
            surfaceVariant = Color(0xFF2D2D2D),
            onBackground = Color.White,
            onSurface = Color.White,
            onSurfaceVariant = Color(0xFFA0A0A0)
        )
    } else {
        lightColorScheme(
            primary = CtaColor,
            background = Color(0xFFF8F9FA),
            surface = Color.White,
            surfaceVariant = Color(0xFFF0F0F0),
            onBackground = Color(0xFF121212),
            onSurface = Color(0xFF121212),
            onSurfaceVariant = Color(0xFF666666)
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.entries.all { it.value }
        if (granted) {
            Toast.makeText(this, "Permissions Granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "SMS Permissions are required for the app to function", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Request permissions on startup
        if (!hasRequiredPermissions()) {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.RECEIVE_SMS,
                    Manifest.permission.READ_SMS
                )
            )
        }

        setContent {
            ClooForwardTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ClooForwardDashboard()
                }
            }
        }
    }

    private fun hasRequiredPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.RECEIVE_SMS
        ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_SMS
                ) == PackageManager.PERMISSION_GRANTED
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClooForwardDashboard() {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("ClooForwardPrefs", Context.MODE_PRIVATE)

    // State variables initialized from SharedPreferences
    var senderIds by remember { mutableStateOf(prefs.getString("SENDER_IDS", "") ?: "") }
    var botToken by remember { mutableStateOf(prefs.getString("BOT_TOKEN", "") ?: "") }
    var chatId by remember { mutableStateOf(prefs.getString("CHAT_ID", "") ?: "") }
    var isServiceActive by remember { mutableStateOf(prefs.getBoolean("SERVICE_ACTIVE", false)) }

    // Save changes to SharedPreferences
    LaunchedEffect(senderIds, botToken, chatId, isServiceActive) {
        prefs.edit().apply {
            putString("SENDER_IDS", senderIds)
            putString("BOT_TOKEN", botToken)
            putString("CHAT_ID", chatId)
            putBoolean("SERVICE_ACTIVE", isServiceActive)
            apply()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        
        // Header Section
        Text(
            text = "ClooForward",
            fontSize = 34.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Automated SMS to Telegram forwarding",
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp, bottom = 40.dp)
        )

        // Input Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                OutlinedTextField(
                    value = senderIds,
                    onValueChange = { senderIds = it },
                    label = { Text("Bank Sender IDs / Keywords") },
                    placeholder = { Text("e.g. CANARA, SBI, Credited") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Sender IDs") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )

                OutlinedTextField(
                    value = botToken,
                    onValueChange = { botToken = it },
                    label = { Text("Telegram Bot Token") },
                    placeholder = { Text("123456789:ABCdef...") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Bot Token") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )

                OutlinedTextField(
                    value = chatId,
                    onValueChange = { chatId = it },
                    label = { Text("Telegram Chat ID") },
                    placeholder = { Text("-1001234567890") },
                    leadingIcon = { Icon(Icons.Default.Send, contentDescription = "Chat ID") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Status Indicator
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 20.dp)
        ) {
            val statusColor by animateColorAsState(
                targetValue = if (isServiceActive) Color(0xFF4CAF50) else Color(0xFF9E9E9E),
                label = "statusColor"
            )
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(statusColor)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = if (isServiceActive) "Service is Active & Listening" else "Service is Inactive",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp
            )
        }

        // CTA Button
        Button(
            onClick = { isServiceActive = !isServiceActive },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(bottom = 8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = CtaColor,
                contentColor = Color.White
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 4.dp,
                pressedElevation = 8.dp
            )
        ) {
            Text(
                text = if (isServiceActive) "Stop Forwarding" else "Start Forwarding",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
