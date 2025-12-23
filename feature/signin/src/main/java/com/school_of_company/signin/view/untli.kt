import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.database.Cursor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

internal fun getMultipartFile(context: Context, uri: Uri): MultipartBody.Part? {
    val jpegFile = uriToJpeg(context, uri) ?: return null
    return fileToMultipartFile(jpegFile)
}

private fun uriToJpeg(context: Context, uri: Uri): File? {
    val bytes = context.contentResolver.openInputStream(uri)
        ?.use { input: InputStream -> input.readBytes() }
        ?: return null

    var bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size) ?: return null

    val exifData = getExifData(context, uri)
    if (exifData != null) {
        val rotated = rotateImage(bitmap, exifData)
        if (rotated != bitmap) bitmap.recycle()
        bitmap = rotated
    }

    val outputFile = createTempJpegFile(context) ?: return null
    FileOutputStream(outputFile).use { out ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
    }
    bitmap.recycle()
    return outputFile
}

private fun fileToMultipartFile(file: File): MultipartBody.Part {
    val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
    return MultipartBody.Part.createFormData("image", file.name, requestFile)
}

@Throws(IOException::class)
private fun createTempJpegFile(context: Context): File? {
    val outputDirectory = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile("image_", ".jpg", outputDirectory)
}

internal fun rotateImage(bitmap: Bitmap, exif: String): Bitmap {
    val matrix = Matrix()
    val angle = when (exif) {
        ExifInterface.ORIENTATION_ROTATE_90.toString() -> 90f
        ExifInterface.ORIENTATION_ROTATE_180.toString() -> 180f
        ExifInterface.ORIENTATION_ROTATE_270.toString() -> 270f
        else -> 0f
    }
    matrix.postRotate(angle)
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}

internal fun getExifData(context: Context, uri: Uri): String? {
    var exifData: String? = null
    var cursor: Cursor? = null
    try {
        val projection = arrayOf(MediaStore.Images.ImageColumns.DATA)
        cursor = context.contentResolver.query(uri, projection, null, null, null)
        if (cursor != null && cursor.moveToFirst()) {
            val filePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATA))
            exifData = getExifFromFile(filePath)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        cursor?.close()
    }
    return exifData
}

private fun getExifFromFile(filePath: String): String {
    val exif = ExifInterface(filePath)
    val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
    val setOrientation = when (orientation) {
        ExifInterface.ORIENTATION_UNDEFINED -> ExifInterface.ORIENTATION_UNDEFINED
        ExifInterface.ORIENTATION_ROTATE_90 -> ExifInterface.ORIENTATION_ROTATE_90
        ExifInterface.ORIENTATION_ROTATE_180 -> ExifInterface.ORIENTATION_ROTATE_180
        ExifInterface.ORIENTATION_ROTATE_270 -> ExifInterface.ORIENTATION_ROTATE_270
        else -> ExifInterface.ORIENTATION_NORMAL
    }
    return setOrientation.toString()
}
