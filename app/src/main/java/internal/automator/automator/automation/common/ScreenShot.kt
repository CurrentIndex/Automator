package internal.automator.automator.automation.common

import android.graphics.Bitmap
import android.media.Image
import android.media.ImageReader
import com.blankj.utilcode.util.ImageUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.Buffer

object ScreenShot {
    suspend fun createBitmap(imageReader: ImageReader): Bitmap {
        val image = imageReader.acquireLatestImage()

        val planes: Array<Image.Plane> = image.planes
        val imageBuffer: Buffer = planes[0].buffer.rewind()

        val pixelStride = planes[0].pixelStride
        val rowStride = planes[0].rowStride
        val rowPadding = rowStride - pixelStride * image.width

        val bitmap = Bitmap.createBitmap(image.width + rowPadding / pixelStride, image.height, Bitmap.Config.ARGB_8888)
        bitmap.copyPixelsFromBuffer(imageBuffer)

        image.close()

//        val savePath = "/storage/emulated/0/DCIM/Automator/${System.currentTimeMillis()}.png"
//        withContext(Dispatchers.IO) {
//            ImageUtils.save(bitmap, savePath, Bitmap.CompressFormat.JPEG)
//            bitmap.recycle()
//        }

        return bitmap
    }
}