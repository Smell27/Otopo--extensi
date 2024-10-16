package eu.kanade.tachiyomi.extension.id.otakupoi

import android.net.Uri
import eu.kanade.tachiyomi.source.HttpSource
import eu.kanade.tachiyomi.source.online.HttpUrlSource
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import okhttp3.Request
import eu.kanade.tachiyomi.source.online.HttpSource.Companion.URL_REGEX
import java.net.URL

class Otakupoi : HttpSource() {
    override val name = "Otakupoi"
    override val baseUrl = "https://otakupoi.org"
    override val lang = "id"
    override val supportsLatest = true

    // Fungsi untuk mendapatkan daftar manga populer
    override fun popularMangaRequest(page: Int): Request {
        return Request.Builder().url("$baseUrl/page/$page").build()
    }

    override fun popularMangaFromJson(json: String): List<Manga> {
        val document = Jsoup.parse(json)
        val mangas = mutableListOf<Manga>()

        // Parsing manga dari halaman
        val mangaElements = document.select(".entry-title a")
        for (element in mangaElements) {
            val title = element.text()
            val url = element.attr("href")
            val manga = Manga.create(title, url)
            mangas.add(manga)
        }

        return mangas
    }

    // Mengambil detail manga berdasarkan URL
    override fun mangaDetailsRequest(manga: Manga): Request {
        return Request.Builder().url(manga.url).build()
    }

    override fun mangaDetailsFromJson(json: String): Manga {
        val document = Jsoup.parse(json)
        val manga = Manga.create(title = document.select(".post-title").text(), url = document.baseUri())
        manga.description = document.select(".entry-content").text()

        return manga
    }

    // Mengambil daftar chapter manga
    override fun chapterListRequest(manga: Manga): Request {
        return Request.Builder().url(manga.url).build()
    }

    override fun chapterListFromJson(json: String): List<Chapter> {
        val document = Jsoup.parse(json)
        val chapters = mutableListOf<Chapter>()

        // Parsing chapter
        val chapterElements = document.select(".chapter-list a")
        for (element in chapterElements) {
            val title = element.text()
            val url = element.attr("href")
            val chapter = Chapter.create(title, url)
            chapters.add(chapter)
        }

        return chapters
    }

    // Fungsi untuk mengambil gambar manga chapter
    override fun pageListRequest(chapter: Chapter): Request {
        return Request.Builder().url(chapter.url).build()
    }

    override fun pageListFromJson(json: String): List<Page> {
        val document = Jsoup.parse(json)
        val pages = mutableListOf<Page>()

        // Mengambil gambar dari halaman chapter
        val imageElements = document.select(".entry-content img")
        for (element in imageElements) {
            val imgUrl = element.attr("src")
            val page = Page.create(imgUrl)
            pages.add(page)
        }

        return pages
    }
}
