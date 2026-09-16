package com.example.iptvprueba.data.datasource

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader

interface PlaylistDataSource {
    suspend fun getPlaylistRaw(): String
}

class AssetPlaylistDataSource(
    private val context: Context,
    private val assetFileName: String = "playlist.m3u"
) : PlaylistDataSource {

    private val tag = "AssetPlaylistDS"

    override suspend fun getPlaylistRaw(): String = withContext(Dispatchers.IO) {
        try {
            val inputStream = context.assets.open(assetFileName)
            val content = BufferedReader(InputStreamReader(inputStream)).use { it.readText() }
            content
        } catch (e: Exception) {
            Log.e(tag, "Error reading asset file $assetFileName", e)
            ""
        }
    }
}
