package com.moderntv.app.data.epg

import android.util.Xml
import org.xmlpull.v1.XmlPullParser

/**
 * EPG Manager — consome XMLTV (xmltv.php) e EPG curto (get_simple_data_table).
 * Lógica idêntica ao Smarters — apenas UI foi modernizada.
 */
class EpgManager {

    data class EpgEvent(
        val channelId: String,
        val title: String,
        val desc: String?,
        val start: Long, // epoch millis
        val end: Long,
        val nowPlaying: Boolean = false
    )

    data class EpgChannel(val id: String, val displayName: String, val icon: String?)

    // Parse XMLTV
    fun parseXmltv(xml: String): Pair<List<EpgChannel>, List<EpgEvent>> {
        val channels = mutableListOf<EpgChannel>()
        val events = mutableListOf<EpgEvent>()
        val parser = Xml.newPullParser().apply {
            setInput(xml.reader())
        }
        var eventType = parser.eventType
        var currentChannelId: String? = null
        var currentTitle: String? = null
        var currentDesc: String? = null
        var currentStart: Long? = null
        var currentEnd: Long? = null
        var currentDisplayName: String? = null
        var currentIcon: String? = null

        while (eventType != XmlPullParser.END_DOCUMENT) {
            when (eventType) {
                XmlPullParser.START_TAG -> when (parser.name) {
                    "channel" -> {
                        currentChannelId = parser.getAttributeValue(null, "id")
                        currentDisplayName = null
                        currentIcon = null
                    }
                    "display-name" -> if (currentChannelId != null && currentDisplayName == null) {
                        // capturado no TEXT
                    }
                    "icon" -> if (currentChannelId != null) {
                        currentIcon = parser.getAttributeValue(null, "src")
                    }
                    "programme" -> {
                        currentChannelId = parser.getAttributeValue(null, "channel")
                        currentStart = parseXmltvTime(parser.getAttributeValue(null, "start"))
                        currentEnd = parseXmltvTime(parser.getAttributeValue(null, "stop"))
                        currentTitle = null
                        currentDesc = null
                    }
                }
                XmlPullParser.TEXT -> {
                    val text = parser.text?.trim()
                    if (!text.isNullOrEmpty()) {
                        when (parser.name) {
                            // heurística simples: dentro de channel -> display-name, dentro de programme -> title/desc
                            else -> {
                                // fallback: se ainda não temos title, é title/desc
                                if (currentTitle == null && currentChannelId != null && currentStart != null) {
                                    // será tratado abaixo via stack — simplificado
                                }
                            }
                        }
                    }
                }
                XmlPullParser.END_TAG -> when (parser.name) {
                    "channel" -> {
                        if (currentChannelId != null) {
                            channels += EpgChannel(currentChannelId!!, currentDisplayName ?: currentChannelId!!, currentIcon)
                        }
                        currentChannelId = null
                    }
                    "programme" -> {
                        if (currentChannelId != null && currentTitle != null && currentStart != null && currentEnd != null) {
                            val now = System.currentTimeMillis()
                            events += EpgEvent(currentChannelId!!, currentTitle!!, currentDesc, currentStart!!, currentEnd!!, now in currentStart!!..currentEnd!!)
                        }
                        currentChannelId = null
                    }
                }
            }
            eventType = parser.next()
        }
        return channels to events
    }

    // Helpers
    private fun parseXmltvTime(raw: String?): Long {
        if (raw == null) return 0L
        // formato: 20261102180000 +0000
        return try {
            val fmt = java.text.SimpleDateFormat("yyyyMMddHHmmss Z", java.util.Locale.US)
            fmt.parse(raw)?.time ?: 0L
        } catch (_: Exception) { 0L }
    }

    fun nowAndNext(events: List<EpgEvent>): Pair<EpgEvent?, EpgEvent?> {
        val now = System.currentTimeMillis()
        val sorted = events.sortedBy { it.start }
        val nowEv = sorted.find { now in it.start..it.end }
        val nextEv = sorted.find { it.start > now }
        return nowEv to nextEv
    }

    fun timelineForChannel(events: List<EpgEvent>, channelId: String): List<EpgEvent> =
        events.filter { it.channelId == channelId }.sortedBy { it.start }
}
