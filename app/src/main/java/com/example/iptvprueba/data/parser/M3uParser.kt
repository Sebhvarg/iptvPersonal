package com.example.iptvprueba.data.parser

import com.example.iptvprueba.domain.model.Channel
import java.util.regex.Pattern

interface M3uParser {
    fun parse(content: String): List<Channel>
}

class DefaultM3uParser : M3uParser {

    private val idPattern = Pattern.compile("tvg-id=\"([^\"]*)\"")
    private val logoPattern = Pattern.compile("tvg-logo=\"([^\"]*)\"")
    private val groupPattern = Pattern.compile("group-title=\"([^\"]*)\"")
    private val resolutionPattern = Pattern.compile("\\b(\\d{3,4}p|4K|FHD|HD|SD)\\b", Pattern.CASE_INSENSITIVE)

    override fun parse(content: String): List<Channel> {
        val channels = mutableListOf<Channel>()
        val lines = content.lines()

        var currentTvgId: String? = null
        var currentLogoUrl: String? = null
        var currentGroupTitle: String = "General"
        var currentChannelName: String? = null

        var index = 0
        while (index < lines.size) {
            val line = lines[index].trim()

            if (line.startsWith("#EXTINF:", ignoreCase = true)) {
                currentTvgId = extractAttribute(idPattern, line)
                currentLogoUrl = extractAttribute(logoPattern, line)
                currentGroupTitle = extractAttribute(groupPattern, line) ?: "General"

                val commaIndex = line.lastIndexOf(',')
                currentChannelName = if (commaIndex != -1 && commaIndex < line.length - 1) {
                    line.substring(commaIndex + 1).trim()
                } else {
                    currentTvgId ?: "Canal ${channels.size + 1}"
                }
            } else if (line.isNotEmpty() && !line.startsWith("#")) {
                if (currentChannelName != null) {
                    val resolution = extractResolution(currentChannelName)
                    val channelId = currentTvgId ?: "channel_${channels.size + 1}"

                    channels.add(
                        Channel(
                            id = channelId,
                            name = currentChannelName,
                            logoUrl = currentLogoUrl,
                            groupTitle = currentGroupTitle,
                            streamUrl = line,
                            resolution = resolution
                        )
                    )

                    currentTvgId = null
                    currentLogoUrl = null
                    currentGroupTitle = "General"
                    currentChannelName = null
                }
            }
            index++
        }

        return channels
    }

    private fun extractAttribute(pattern: Pattern, line: String): String? {
        val matcher = pattern.matcher(line)
        return if (matcher.find()) matcher.group(1) else null
    }

    private fun extractResolution(title: String): String {
        val matcher = resolutionPattern.matcher(title)
        return if (matcher.find()) matcher.group(1)?.uppercase() ?: "" else ""
    }
}
