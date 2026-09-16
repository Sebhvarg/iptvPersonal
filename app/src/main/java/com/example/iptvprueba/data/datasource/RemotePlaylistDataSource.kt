package com.example.iptvprueba.data.datasource

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class RemotePlaylistDataSource(
    private val playlistUrl: String = "https://raw.githubusercontent.com/Sebhvarg/iptvPersonal/main/playlist.m3u",
    private val fallbackDataSource: PlaylistDataSource = DefaultPlaylistDataSource()
) : PlaylistDataSource {

    private val tag = "RemotePlaylistDS"

    override suspend fun getPlaylistRaw(): String = withContext(Dispatchers.IO) {
        try {
            Log.d(tag, "Fetching playlist from: $playlistUrl")
            val url = URL(playlistUrl)
            val connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 8000
                readTimeout = 8000
                useCaches = false
                setRequestProperty("Cache-Control", "no-cache, no-store, must-revalidate")
                setRequestProperty("Pragma", "no-cache")
                setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android TV) IPTV/1.0")
            }

            val responseCode = connection.responseCode
            Log.d(tag, "HTTP response code: $responseCode")

            if (responseCode == HttpURLConnection.HTTP_OK) {
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val content = reader.use { it.readText() }
                connection.disconnect()
                if (content.isNotBlank() && content.contains("#EXTINF")) {
                    return@withContext content
                }
            }
            connection.disconnect()
            Log.w(tag, "Remote playlist unreachable or invalid, using fallback")
            fallbackDataSource.getPlaylistRaw()
        } catch (e: Exception) {
            Log.e(tag, "Error fetching remote playlist: ${e.message}, using fallback", e)
            fallbackDataSource.getPlaylistRaw()
        }
    }
}
