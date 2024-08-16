package com.alura.concord.network

import android.accounts.NetworkErrorException
import android.content.Context
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.FileNotFoundException
import java.io.InputStream
import java.net.UnknownHostException

object DowloadService {

    suspend fun makeDowloadByUrl(
        url: String,
        onFinisheDowload: (InputStream) -> Unit,
        onFailureDowload: () -> Unit,
    ) {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .build()

        try {
            delay(2000)
            withContext(IO) {
                client.newCall(request).execute().let { response ->
                    response.body?.byteStream()?.let { fileData: InputStream ->
                        withContext(Main) {
                            onFinisheDowload(fileData)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            when (e) {
                is NetworkErrorException,
                is UnknownHostException,
                is FileNotFoundException -> {
                    onFailureDowload()
                }

                else -> throw e
            }
        }
    }
}