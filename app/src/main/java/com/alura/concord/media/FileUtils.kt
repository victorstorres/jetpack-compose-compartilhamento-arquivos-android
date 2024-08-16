package com.alura.concord.media

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStream

fun Long.formatReadableFileSize(): String {
    val size = this
    val kilobyte = 1024
    val megaByte = kilobyte * 1024
    val gigaByte = megaByte * 1024

    return when {
        size < kilobyte -> "$size B"
        size < megaByte -> "${size / kilobyte} KB"
        size < gigaByte -> "${size / megaByte} MB"
        else -> "${size / gigaByte} GB"
    }
}

suspend fun Context.saveOnInternalStorage(
    inputStream: InputStream,
    fileName: String,
    onSucess: (String) -> Unit,
    onFailure: () -> Unit,
) {
    val path = getExternalFilesDir("temp")
    val newFile = File(path, fileName)
    withContext(IO) {
        newFile.outputStream().use { file ->
            inputStream.copyTo(file)
        }

        if (newFile.exists()) {
            onSucess(newFile.path)
        } else {
            onFailure()
        }
    }
}

fun Context.openWith(mediaLink: String) {

    val file = File(mediaLink)


    // Estamos passando o caminho do arquivo
    val fileExtension = MimeTypeMap.getFileExtensionFromUrl(Uri.encode(file.path))
    //Com isso ele indentifica o formado da foto que vai ser aberta: Foto, imagens e etc...
    //Capaz de indentificar dinamicamente
    val fileMimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension) ?: "*/*"

    val contentUri: Uri = FileProvider.getUriForFile(
        this,
        "com.alura.concord.fileprovider",
        file
    )
    val shareIntent = Intent().apply {
        action = Intent.ACTION_VIEW
        setDataAndType(contentUri, fileMimeType)
        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
    }
    startActivity(Intent.createChooser(shareIntent, "Abrir com"))
}