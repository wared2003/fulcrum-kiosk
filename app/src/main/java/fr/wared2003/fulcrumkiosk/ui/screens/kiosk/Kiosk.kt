package fr.wared2003.fulcrumkiosk.ui.screens.kiosk

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.print.PrintAttributes
import android.print.PrintManager
import android.util.Log
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import org.koin.androidx.compose.koinViewModel
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.changedToDown
import androidx.compose.ui.input.pointer.pointerInput
import fr.wared2003.fulcrumkiosk.MainActivity

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

@SuppressLint("ClickableViewAccessibility", "SetJavaScriptEnabled")
@Composable
fun KioskScreen(
    viewModel: KioskViewModel = koinViewModel() // Injection Koin automatique
) {
    var webViewRef: WebView? by remember { mutableStateOf(null) }
    val context = LocalContext.current
    val activity = context as? MainActivity

    val state by viewModel.state.collectAsState()

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
                               viewModel.onEvent( KioskEvent.OnSecretBtnClicked)
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

        if (state.isLoading && state.url.isNotBlank()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}