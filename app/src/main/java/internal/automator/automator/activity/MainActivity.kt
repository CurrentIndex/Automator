package internal.automator.automator.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import cn.vove7.andro_accessibility_api.api.requireBaseAccessibility
import cn.vove7.andro_accessibility_api.api.requireGestureAccessibility
import com.blankj.utilcode.util.PermissionUtils
import com.blankj.utilcode.util.ScreenUtils
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import internal.automator.automator.R
import internal.automator.automator.automation.runner.CollectRunner
import internal.automator.automator.automation.runner.CollectStoreRunner
import internal.automator.automator.service.ScreenCaptureService
import internal.automator.automator.view.Console
import internal.automator.common.ConsoleWindow
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


class MainActivity : AppCompatActivity() {
    companion object {
        const val VIRTUAL_DISPLAY_NAME = "screen_shot"
        val consoleWindow = ConsoleWindow()
    }

    private lateinit var console: Console
    private lateinit var shareNameText: TextInputEditText
    private lateinit var chatsNameText: TextInputEditText

    private lateinit var imageReader: ImageReader
    private lateinit var mediaProjectionManager: MediaProjectionManager
    private lateinit var mediaProjection: MediaProjection

    @SuppressLint("WrongConstant")
    private val screenCaptureResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
            mediaProjection = mediaProjectionManager.getMediaProjection(it.resultCode, it.data!!)
            imageReader = ImageReader.newInstance(
                ScreenUtils.getScreenWidth(),
                ScreenUtils.getScreenHeight(),
                PixelFormat.RGBA_8888, 3
            )

            mediaProjection.createVirtualDisplay(
                VIRTUAL_DISPLAY_NAME,
                ScreenUtils.getScreenWidth(),
                ScreenUtils.getScreenHeight(),
                ScreenUtils.getScreenDensityDpi(),
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                imageReader.surface, null, null
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        shareNameText = findViewById(R.id.shareName)
        chatsNameText = findViewById(R.id.chatsName)

        startForegroundService(Intent(applicationContext, ScreenCaptureService::class.java))
        mediaProjectionManager = getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        screenCaptureResult.launch(mediaProjectionManager.createScreenCaptureIntent())
        findViewById<MaterialButton>(R.id.runCollect).setOnClickListener { runRunner() }
        findViewById<MaterialButton>(R.id.runCollectStore).setOnClickListener { runStoreRunner() }
        findViewById<MaterialButton>(R.id.viewData).setOnClickListener {
//            readied {
//                console = consoleWindow.create(this)
//                console.setOnCommandListener {
//                    lifecycleScope.launch {
//                        val last = SF.containsText("已售").find().filter {
//                            !it.text!!.contains(" ") && !it.text!!.contains(":")
//                        }.toList().last().text
//                        println(last)
//                    }
//                }
//            }
            val intent = Intent(this, InformationActivity::class.java)
            startActivity(intent)
        }
    }


    private suspend fun recognizeBitmap(bitmap: Bitmap): Text {
        return suspendCoroutine { continuation ->
            val image = InputImage.fromBitmap(bitmap, 0)
            CollectStoreRunner.recognizer
                .process(image)
                .addOnSuccessListener { text -> continuation.resume(text) }
                .addOnFailureListener { e -> continuation.resumeWithException(e) }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        if (consoleWindow.isCreated) consoleWindow.remove(applicationContext, console)
    }


    private fun runRunner() {
        readied {
            console = consoleWindow.create(this)
            val runner = CollectRunner(applicationContext) { console.log(it) }
            val nameStr = shareNameText.text?.toString()
            runner.name = if (nameStr.isNullOrBlank()) "文件传输助手" else nameStr
            runner.duration = 1000
            runner.runAutomation()
        }
    }

    private fun runStoreRunner() {
        readied {
            console = consoleWindow.create(this)
            val nameStr = chatsNameText.text?.toString()
            val runner = CollectStoreRunner(applicationContext) { console.log(it) }
            runner.name = if (nameStr.isNullOrBlank()) "文件传输助手" else nameStr
            runner.imageReader = imageReader
            runner.runAutomation()
        }
    }

    private fun readied(readied: suspend () -> Unit) {
        if (!PermissionUtils.isGrantedDrawOverlays()) {
            PermissionUtils.requestDrawOverlays(null)
            return
        }
        try {
            requireBaseAccessibility(true)
            requireGestureAccessibility(true)
            lifecycleScope.launch { readied() }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}