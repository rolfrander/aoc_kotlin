package rolfrander.aoc

import org.springframework.beans.factory.annotation.Autowired
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.exists
import kotlin.io.path.readText
import kotlin.io.path.writeText
import java.net.http.HttpClient
import java.net.http.HttpClient.Redirect
import java.net.http.HttpRequest
import java.net.http.HttpResponse.BodyHandlers
import java.net.URI

class AocData(private val year: String, 
              private val session: String) {

    val cli = HttpClient.newBuilder().followRedirects(Redirect.NORMAL).build()

    fun getCachePath(): Path {
        return Path.of(".cache").createDirectories()
    }

    fun getData(day: Int): String {
        val path = getCachePath()
        val dataFile = path.resolve("day%02d.txt".format(day))
        if(dataFile.exists()) {
            return dataFile.readText()
        } else {
            val req = HttpRequest.newBuilder()
                        .uri(URI.create("https://adventofcode.com/%s/day/%d/input".format(year, day)))
                        .header("Cookie", "session=%s".format(session))
                        .GET()
                        .build()
            val response = cli.send(req, BodyHandlers.ofString())
            val data = response.body()
            dataFile.writeText(data)
            return data
        }
    }
}
