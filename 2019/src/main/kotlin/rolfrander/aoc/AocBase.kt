package rolfrander.aoc

import kotlin.math.max
import kotlin.system.measureTimeMillis

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.GetMapping

import kotlinx.html.*
import kotlinx.html.dom.*
import kotlinx.html.dom.createHTMLDocument

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory


abstract class AocBase(private val config: AocData, private val day: Int, private val testdata: String) {

    val data = config.getData(day)

    var isTesting = false;

    val log = LogFactory.getLog(this.javaClass)

    open fun part1(data: String): Any = "(unknown)"

    open fun part2(data: String): Any = "(unknown)"

    @GetMapping("/prod/")
    fun prod(): String {
        return compute(data, "..", "go to test")
    }

    @GetMapping("/")
    fun test(): String {
        try {
            isTesting = true
            return compute(testdata, "prod/", "go to prod")
        } finally {
            isTesting = false
        }
    }

    fun compute(data: String, link: String, linkText: String): String {
        var res1: String = ""
        var res2: String = ""
        val time1 = measureTimeMillis {
            res1 = part1(data).toString()
        }
        var time2 = measureTimeMillis { 
            res2 = part2(data).toString()
        }
        val doc = createHTMLDocument().html {
            head { title { +"AOC 2019 Day ${day}" }}
            body {
                h1 { +"AOC 2019 Day ${day}" }
                h2 { +"Part 1" }
                p { + res1 }
                p { +"Time: ${time1} ms"}
                h2 { +"Part 2"}
                p { + res2 }
                p { +"Time: ${time2} ms"}
                p { a(link) { +linkText }}
            }
        }
        return doc.serialize(false)
    }
}