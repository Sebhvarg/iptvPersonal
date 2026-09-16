package com.example.iptvprueba.data.datasource

interface PlaylistDataSource {
    suspend fun getPlaylistRaw(): String
}

class DefaultPlaylistDataSource : PlaylistDataSource {
    override suspend fun getPlaylistRaw(): String {
        return """
#EXTINF:-1 tvg-id="EcuadorTV.ec@SD" tvg-logo="https://i.imgur.com/hj6EYwe.png" group-title="General",Ecuador TV (1080p)
http://45.224.97.181:9999/EcuadorTV/index.m3u8
#EXTINF:-1 tvg-id="EcuaMundoRadioTV.ec@SD" tvg-logo="https://i.imgur.com/EMe5oWn.jpg" group-title="Music",EcuaMundo Radio TV (720p) [Not 24/7]
https://pacific.direcnode.com:3353/live/ecuamundotvlive.m3u8
#EXTINF:-1 tvg-id="Ecuavisa.ec@Quito" tvg-logo="https://i.imgur.com/Hl5wowk.png" group-title="General",Ecuavisa (1080p)
http://45.171.108.253:8888/ECUAVISA/index.m3u8
#EXTINF:-1 tvg-id="Ecuavisa.ec@Guayaquil" tvg-logo="https://i.imgur.com/Hl5wowk.png" group-title="General",Ecuavisa Guayaquil (1080p) [Not 24/7]
https://dai.google.com/linear/hls/event/GyPkTVDZSXGhpOvxPK7m2g/master.m3u8
        """.trimIndent()
    }
}
