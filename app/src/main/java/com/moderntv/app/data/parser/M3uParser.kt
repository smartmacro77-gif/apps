package com.moderntv.app.data.parser

/**
 * M3U Parser — 100% preservado do Smarters original.
 * Suporta #EXTM3U, #EXTINF com tvg-name/tvg-logo/group-title, e URLs.
 * Nenhuma lógica alterada — apenas exposto para a nova UI consumir.
 */
object M3uParser {

    data class M3uChannel(
        val name: String,
        val url: String,
        val groupTitle: String?,
        val tvgId: String?,
        val tvgName: String?,
        val tvgLogo: String?,
        val tvgShift: String?,
        val duration: String // -1 por padrão
    )

    // Regex herdada do Smarters
    private val extInfRegex = Regex("""#EXTINF:(-?\d+)\s*(.*)""")
    private val attrRegex = Regex("""([a-zA-Z0-9\-_]+)=\"([^\"]*)\"""")

    fun parse(m3uContent: String): List<M3uChannel> {
        val lines = m3uContent.lines().map { it.trim() }.filter { it.isNotEmpty() }
        val result = mutableListOf<M3uChannel>()
        var pendingInfo: String? = null
        var pendingDuration: String = "-1"

        for (line in lines) {
            when {
                line.startsWith("#EXTM3U") -> continue
                line.startsWith("#EXTINF:") -> {
                    val m = extInfRegex.find(line)
                    if (m != null) {
                        pendingDuration = m.groupValues[1]
                        pendingInfo = m.groupValues[2] // contém atributos + título
                    }
                }
                line.startsWith("#") -> continue // comentários/extras
                else -> {
                    // linha de URL
                    if (pendingInfo != null) {
                        val attrs = parseAttrs(pendingInfo!!)
                        val displayName = extractDisplayName(pendingInfo!!)
                        result += M3uChannel(
                            name = displayName.ifBlank { attrs["tvg-name"] ?: "Canal" },
                            url = line,
                            groupTitle = attrs["group-title"],
                            tvgId = attrs["tvg-id"],
                            tvgName = attrs["tvg-name"],
                            tvgLogo = attrs["tvg-logo"],
                            tvgShift = attrs["tvg-shift"],
                            duration = pendingDuration
                        )
                        pendingInfo = null
                    } else {
                        // URL órfã — ainda adiciona
                        result += M3uChannel(line, line, null, null, null, null, null, "-1")
                    }
                }
            }
        }
        return result
    }

    private fun parseAttrs(info: String): Map<String, String> {
        return attrRegex.findAll(info).associate { it.groupValues[1] to it.groupValues[2] }
    }

    private fun extractDisplayName(info: String): String {
        // após a última vírgula vem o nome de exibição
        val idx = info.lastIndexOf(',')
        return if (idx != -1 && idx + 1 < info.length) info.substring(idx + 1).trim() else ""
    }

    fun groupByCategory(channels: List<M3uChannel>): Map<String, List<M3uChannel>> =
        channels.groupBy { it.groupTitle ?: "Sem Categoria" }
}
