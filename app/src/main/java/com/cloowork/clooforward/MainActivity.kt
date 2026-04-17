package com.cloowork.clooforward

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat

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
            MaterialTheme {
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ClooForward Dashboard") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = senderIds,
                onValueChange = { senderIds = it },
                label = { Text("Bank Sender IDs / Keywords") },
                placeholder = { Text("e.g. CANARA, SBI, Credited") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = botToken,
                onValueChange = { botToken = it },
                label = { Text("Telegram Bot Token") },
                placeholder = { Text("123456789:ABCdefGHIjklMNOpqrSTUvwxYZ") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = chatId,
                onValueChange = { chatId = it },
                label = { Text("Telegram Chat ID") },
                placeholder = { Text("-1001234567890") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { isServiceActive = !isServiceActive },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isServiceActive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = if (isServiceActive) "Stop Service" else "Start Service",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            if (isServiceActive) {
                Text(
                    text = "Service is active and listening for SMS...",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
