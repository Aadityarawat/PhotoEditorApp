package com.example.photoeditor.others

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.OpenableColumns
import android.text.TextUtils
import android.webkit.MimeTypeMap
import java.io.File
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date


object FileHelper {

    @SuppressLint("Range")
    fun getFileName(activity: Activity?, uri: Uri) : String {
        val cursor = activity?.contentResolver?.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                return it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
            }
        }
        return ""
    }

    fun getFileFromUri(activity: Activity?, uri: Uri): File? {
        val inputStream = activity?.contentResolver?.openInputStream(uri)
        inputStream?.let {
            val extension = getExtensionFromUri(activity, uri)
            val file = createTempFileFromStream(activity, it, extension)
            it.close()
            return file
        }
        return null
    }

    private fun createTempFileFromStream(activity: Activity?, inputStream: InputStream, extension: String): File {
        val cacheDir = activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val tempFile = File.createTempFile("file", ".$extension", cacheDir)
        tempFile.deleteOnExit()  // Delete the temporary file on app exit
        tempFile.outputStream().use { output ->
            inputStream.copyTo(output)
        }
        return tempFile
    }

    fun createImageFile(context: Context): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(imageFileName, ".jpg", storageDir)
    }

    private fun getExtensionFromUri(activity: Activity?, uri: Uri): String {
        val mimeType = activity?.contentResolver?.getType(uri)
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType) ?: "unknown"
    }

    fun getExtensionFromUrl(url: String): String {
        val extension = MimeTypeMap.getFileExtensionFromUrl(url)
        return if (TextUtils.isEmpty(extension)) "" else ".$extension"
    }
}