package fr.wared2003.fulcrumkiosk.ui.screens.kiosk

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.print.PrintAttributes
import android.print.PrintManager
import android.util.Log
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import fr.wared2003.fulcrumkiosk.MainActivity
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

class WebAppInterface(private val context: Context, private val webView: WebView) {
    @JavascriptInterface
    fun print() {
        (context as? Activity)?.runOnUiThread {
            val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
            val jobName = "Fulcrum Kiosk Document"
            val printAdapter = webView.createPrintDocumentAdapter(jobName)
            printManager.print(jobName, printAdapter, PrintAttributes.Builder().build())
        }
    }
}

fun Context.findWindow(): Window? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context.window
        context = context.baseContext
    }
    return null
}

@SuppressLint("ClickableViewAccessibility", "SetJavaScriptEnabled")
@Composable
fun KioskScreen(
    viewModel: KioskViewModel = koinViewModel() // Injection Koin automatique
) {
    var webViewRef: WebView? by remember { mutableStateOf(null) }
    val context = LocalContext.current
    val activity = context as? MainActivity

    val state by viewModel.state.collectAsState()

    var lastInteractionTime by remember { mutableStateOf(System.currentTimeMillis()) }
    var isScreenDimmed by remember { mutableStateOf(false) }

    BackHandler(enabled = true) {
        if (webViewRef?.canGoBack() == true) {
            webViewRef?.goBack()
        }
    }

    if (state.url.isBlank()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val window = context.findWindow()
    LaunchedEffect(state.brightness, state.isAutoBrightness, isScreenDimmed) {
        if (window != null) {
            val attributes = window.attributes
            attributes.screenBrightness = when {
                isScreenDimmed -> state.powerSavingDimValue
                state.isAutoBrightness -> WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
                else -> state.brightness
            }
            window.attributes = attributes
        }
    }

    LaunchedEffect(state.isFullScreen) {
        Log.d("KioskDebug", "État FullScreen reçu : ${state.isFullScreen}")
        val window = (context as? Activity)?.window ?: return@LaunchedEffect
        val controller = WindowCompat.getInsetsController(window, window.decorView)

        if (state.isFullScreen) {
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            controller.hide(WindowInsetsCompat.Type.systemBars())
            activity?.setKioskMode(state.isLockOn)
        } else {
            controller.show(WindowInsetsCompat.Type.systemBars())
            activity?.setKioskMode(false)
        }
    }

    LaunchedEffect(lastInteractionTime, state.powerSavingDelayMinutes) {
        val delayMillis = state.powerSavingDelayMinutes * 60 * 1000L
        delay(delayMillis)
        val elapsedTime = System.currentTimeMillis() - lastInteractionTime
        if (elapsedTime >= delayMillis) {
            isScreenDimmed = true
            viewModel.onEvent(KioskEvent.OnInactive)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                WebView(context).apply {
                    webViewRef = this

                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )

                    settings.apply {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        displayZoomControls = false
                        builtInZoomControls = false
                        useWideViewPort = true
                        loadWithOverviewMode = true
                        val defaultUserAgent = settings.userAgentString
                        val customUserAgent = defaultUserAgent.replace("; wv", "").replace("Version/\\d+\\.\\d+\\s".toRegex(), "")
                        userAgentString = customUserAgent
                    }

                    webChromeClient = android.webkit.WebChromeClient()
                    webViewClient = object : WebViewClient() {
                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            viewModel.onEvent(KioskEvent.OnPageFinished)
                        }
                    }
                    addJavascriptInterface(WebAppInterface(context, this), "Android")

                    loadUrl(state.url)

                    setOnTouchListener { _, event ->
                        if (event.action == android.view.MotionEvent.ACTION_DOWN) {
                            lastInteractionTime = System.currentTimeMillis()
                            if(isScreenDimmed) {
                                isScreenDimmed = false
                            }
                            viewModel.onEvent(KioskEvent.OnSecretBtnClicked)
                        }
                        false
                    }
                }

            },

            update = { webView ->
                if (webView.url != state.url) {
                    webView.loadUrl(state.url)
                }
            }
        )

        if (state.powerSavingAction == "off" && isScreenDimmed) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                lastInteractionTime = System.currentTimeMillis()
                                isScreenDimmed = false
                            }
                        )
                    }

            )
        }

        if (state.isLoading && state.url.isNotBlank()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}