package com.nll.clooforward

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class SmsReceiver : BroadcastReceiver() {

    private val client = OkHttpClient()
    private val scope = CoroutineScope(Dispatchers.IO)

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) return

        val prefs = context.getSharedPreferences("ClooForwardPrefs", Context.MODE_PRIVATE)
        val isServiceActive = prefs.getBoolean("SERVICE_ACTIVE", false)
        if (!isServiceActive) return

        val botToken = prefs.getString("BOT_TOKEN", "") ?: ""
        val chatId = prefs.getString("CHAT_ID", "") ?: ""
        val senderIdsRaw = prefs.getString("SENDER_IDS", "") ?: ""

        if (botToken.isBlank() || chatId.isBlank() || senderIdsRaw.isBlank()) return

        // Extract keywords/sender IDs
        val keywords = senderIdsRaw.split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
        for (sms in messages) {
            val sender = sms.displayOriginatingAddress ?: ""
            val body = sms.displayMessageBody ?: ""

            // Check if sender ID or body matches any of the configured keywords
            val matches = keywords.any { keyword ->
                sender.contains(keyword, ignoreCase = true) || body.contains(keyword, ignoreCase = true)
            }

            if (matches) {
                // Forward to Telegram
                forwardToTelegram(botToken, chatId, sender, body)
            }
        }
    }

    private fun forwardToTelegram(botToken: String, chatId: String, sender: String, body: String) {
        scope.launch {
            try {
                val url = "https://api.telegram.org/bot$botToken/sendMessage"
                
                val json = JSONObject().apply {
                    put("chat_id", chatId)
                    put("text", "Bank Alert from: $sender\n\n$body")
                }

                val requestBody = json.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
                
                val request = Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build()

                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        Log.e("SmsReceiver", "Failed to send to Telegram: ${response.code}")
                    } else {
                        Log.i("SmsReceiver", "Successfully forwarded SMS to Telegram.")
                    }
                }
            } catch (e: Exception) {
                Log.e("SmsReceiver", "Error forwarding SMS", e)
            }
        }
    }
}
