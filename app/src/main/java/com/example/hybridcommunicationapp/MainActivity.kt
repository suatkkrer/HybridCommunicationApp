package com.example.hybridcommunicationapp

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.android.FlutterActivityLaunchConfigs
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.FlutterEngineCache
import io.flutter.embedding.engine.dart.DartExecutor
import io.flutter.plugin.common.MethodChannel

class MainActivity : ComponentActivity() {
    private val CHANNEL = "com.example.hybridcommunicationapp/channel"
    private val ENGINE_ID = "flutter_engine_id"
    private val SHARED_PREFS_KEY = "flutter_data"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val initialData = sharedPreferences.getString(SHARED_PREFS_KEY, "") ?: ""

        val flutterEngine = FlutterEngine(this).apply {
            dartExecutor.executeDartEntrypoint(
                DartExecutor.DartEntrypoint.createDefault()
            )
        }
        FlutterEngineCache.getInstance().put(ENGINE_ID, flutterEngine)

        setContent {
            MethodChannel(
                flutterEngine.dartExecutor.binaryMessenger,
                CHANNEL
            ).setMethodCallHandler { call, result ->
                when (call.method) {
                    "getData" -> {
                        result.success("Hello from Android!")
                    }
                    "sendData" -> {
                        val data = call.argument<String>("data") ?: ""
                        println("Data received from Flutter: $data")
                        with(sharedPreferences.edit()) {
                            putString(SHARED_PREFS_KEY, data)
                            apply()
                        }
                        result.success(data)
                    }
                    "openApp" -> {
                        try {
                            val intent = Intent(
                                applicationContext.packageManager.getLaunchIntentForPackage(
                                    applicationContext.packageName
                                )
                            ).apply {
                                flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TOP
                            }
                            startActivity(intent)
                            result.success("App launched successfully")
                        } catch (e: Exception) {
                            result.error("LAUNCH_FAILED", "Failed to launch app: ${e.message}", null)
                        }
                    }

                    else -> {
                        result.notImplemented()
                    }
                }
            }
            AppContent(initialData)
        }
    }
}

@Composable
fun AppContent(dataFromFlutter: String) {
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = dataFromFlutter, fontSize = 30.sp)
        Button(
            onClick = {
                context.startActivity(
                    FlutterActivity.withCachedEngine("flutter_engine_id")
                        .backgroundMode(FlutterActivityLaunchConfigs.BackgroundMode.transparent)
                        .build(context)
                )
            }
        ) {
            Text("Launch Flutter!")
        }
    }
}