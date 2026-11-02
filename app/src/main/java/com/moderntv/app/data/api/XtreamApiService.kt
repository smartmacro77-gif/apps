package com.moderntv.app.data.api

import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

/**
 * Xtream Codes API — 100% PRESERVADO do Smarters original.
 * Nenhuma rota/parâmetro foi alterado. Apenas a UI chama estes métodos.
 * Mantém compatibilidade com: player_api.php + get.php (M3U) + xmltv.php (EPG)
 */
interface XtreamApiService {

    // Autenticação + info da conta
    @GET("player_api.php")
    suspend fun authenticate(
        @Query("username") username: String,
        @Query("password") password: String
    ): XtreamAuthResponse

    // Categorias
    @GET("player_api.php")
    suspend fun getLiveCategories(
        @Query("username") username: String,
        @Query("password") password: String,
        @Query("action") action: String = "get_live_categories"
    ): List<XtreamCategory>

    @GET("player_api.php")
    suspend fun getVodCategories(
        @Query("username") username: String,
        @Query("password") password: String,
        @Query("action") action: String = "get_vod_categories"
    ): List<XtreamCategory>

    @GET("player_api.php")
    suspend fun getSeriesCategories(
        @Query("username") username: String,
        @Query("password") password: String,
        @Query("action") action: String = "get_series_categories"
    ): List<XtreamCategory>

    // Streams
    @GET("player_api.php")
    suspend fun getLiveStreams(
        @Query("username") username: String,
        @Query("password") password: String,
        @Query("action") action: String = "get_live_streams",
        @Query("category_id") categoryId: String? = null
    ): List<XtreamLiveStream>

    @GET("player_api.php")
    suspend fun getVodStreams(
        @Query("username") username: String,
        @Query("password") password: String,
        @Query("action") action: String = "get_vod_streams",
        @Query("category_id") categoryId: String? = null
    ): List<XtreamVodStream>

    @GET("player_api.php")
    suspend fun getSeries(
        @Query("username") username: String,
        @Query("password") password: String,
        @Query("action") action: String = "get_series",
        @Query("category_id") categoryId: String? = null
    ): List<XtreamSeries>

    // Metadados detalhados
    @GET("player_api.php")
    suspend fun getVodInfo(
        @Query("username") username: String,
        @Query("password") password: String,
        @Query("action") action: String = "get_vod_info",
        @Query("vod_id") vodId: String
    ): XtreamVodInfo

    @GET("player_api.php")
    suspend fun getSeriesInfo(
        @Query("username") username: String,
        @Query("password") password: String,
        @Query("action") action: String = "get_series_info",
        @Query("series_id") seriesId: String
    ): XtreamSeriesInfo

    // EPG curto e longo
    @GET("player_api.php")
    suspend fun getShortEpg(
        @Query("username") username: String,
        @Query("password") password: String,
        @Query("action") action: String = "get_simple_data_table",
        @Query("stream_id") streamId: String
    ): EpgResponse

    @GET("xmltv.php")
    suspend fun getXmltv(
        @Query("username") username: String,
        @Query("password") password: String
    ): String // XML bruto — parseado por EpgManager

    // M3U fallback
    @GET
    suspend fun getM3u(@Url url: String): String
}

// --- Models (mesmos do Smarters, sem alteração) ---
data class XtreamAuthResponse(
    val user_info: UserInfo,
    val server_info: ServerInfo
)
data class UserInfo(
    val username: String,
    val password: String,
    val auth: Int, // 1 = ok
    val status: String, // Active
    val exp_date: String?,
    val is_trial: String,
    val active_cons: String,
    val created_at: String,
    val max_connections: String,
    val allowed_output_formats: List<String>
)
data class ServerInfo(
    val url: String,
    val port: String,
    val https_port: String,
    val server_protocol: String,
    val rtmp_port: String,
    val timezone: String,
    val timestamp_now: Long,
    val time_now: String
)
data class XtreamCategory(
    val category_id: String,
    val category_name: String,
    val parent_id: Int
)
data class XtreamLiveStream(
    val num: Int,
    val name: String,
    val stream_type: String,
    val stream_id: Int,
    val stream_icon: String,
    val epg_channel_id: String?,
    val added: String,
    val category_id: String,
    val custom_sid: String?,
    val tv_archive: Int,
    val direct_source: String
)
data class XtreamVodStream(
    val num: Int,
    val name: String,
    val stream_type: String,
    val stream_id: Int,
    val stream_icon: String,
    val rating: String?,
    val rating_5based: Double?,
    val added: String,
    val category_id: String,
    val container_extension: String
)
data class XtreamSeries(
    val num: Int,
    val name: String,
    val series_id: Int,
    val cover: String,
    val plot: String?,
    val cast: String?,
    val director: String?,
    val genre: String?,
    val releaseDate: String?,
    val last_modified: String,
    val rating: String?,
    val rating_5based: Double?,
    val backdrop_path: List<String>?,
    val youtube_trailer: String?,
    val episode_run_time: String?,
    val category_id: String
)
data class XtreamVodInfo(
    val info: VodInfoDetail,
    val movie_data: MovieData?
)
data class VodInfoDetail(
    val name: String,
    val o_name: String?,
    val cover_big: String?,
    val movie_image: String?,
    val youtube_trailer: String?,
    val genre: String?,
    val plot: String?,
    val cast: String?,
    val director: String?,
    val releasedate: String?,
    val duration: String?,
    val rating: String?,
    val backdrop_path: List<String>?
)
data class MovieData(val stream_id: Int, val name: String, val added: String, val category_id: String, val container_extension: String)
data class XtreamSeriesInfo(
    val seasons: List<Season>,
    val info: SeriesInfoDetail,
    val episodes: Map<String, List<Episode>>
)
data class Season(val air_date: String?, val episode_count: Int, val id: Int, val name: String, val overview: String?, val season_number: Int, val cover: String?, val cover_big: String?)
data class SeriesInfoDetail(
    val name: String,
    val cover: String?,
    val plot: String?,
    val cast: String?,
    val director: String?,
    val genre: String?,
    val releaseDate: String?,
    val last_modified: String,
    val rating: String?,
    val youtube_trailer: String?,
    val backdrop_path: List<String>?
)
data class Episode(
    val id: String,
    val episode_num: Int,
    val title: String,
    val container_extension: String,
    val info: EpisodeInfo?,
    val added: String,
    val season: Int,
    val stream_id: Int,
    val direct_source: String?
)
data class EpisodeInfo(val movie_image: String?, val plot: String?, val duration: String?, val releasedate: String?, val rating: String?)
data class EpgResponse(val epg_listings: List<EpgListing>)
data class EpgListing(
    val id: String,
    val epg_id: String,
    val title: String,
    val lang: String,
    val start: String, // "2026-11-02 18:00:00"
    val end: String,
    val description: String?,
    val now_playing: Int // 1 se ao vivo agora
)

/**
 * Factory que preserva baseUrl dinâmica (Host do login)
 */
object XtreamApiFactory {
    fun create(baseHost: String): XtreamApiService {
        val host = baseHost.trim().trimEnd('/')
        val normalized = if (host.startsWith("http")) host else "http://$host"
        return retrofit2.Retrofit.Builder()
            .baseUrl("$normalized/")
            .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
            .client(
                okhttp3.OkHttpClient.Builder()
                    .addInterceptor(okhttp3.logging.HttpLoggingInterceptor().apply { level = okhttp3.logging.HttpLoggingInterceptor.Level.BASIC })
                    .connectTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
                    .readTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
                    .build()
            )
            .build()
            .create(XtreamApiService::class.java)
    }

    fun buildStreamUrl(host: String, username: String, password: String, streamId: Int, extension: String = "m3u8"): String {
        val h = host.trim().trimEnd('/').removePrefix("http://").removePrefix("https://")
        val proto = if (host.startsWith("https")) "https" else "http"
        return "$proto://$h/live/$username/$password/$streamId.$extension"
    }
    fun buildVodUrl(host: String, username: String, password: String, streamId: Int, extension: String): String {
        val h = host.trim().trimEnd('/').removePrefix("http://").removePrefix("https://")
        val proto = if (host.startsWith("https")) "https" else "http"
        return "$proto://$h/movie/$username/$password/$streamId.$extension"
    }
    fun buildSeriesUrl(host: String, username: String, password: String, streamId: Int, extension: String): String {
        val h = host.trim().trimEnd('/').removePrefix("http://").removePrefix("https://")
        val proto = if (host.startsWith("https")) "https" else "http"
        return "$proto://$h/series/$username/$password/$streamId.$extension"
    }
}
