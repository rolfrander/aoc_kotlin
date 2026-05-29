package rolfrander.aoc2019

import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.beans.factory.annotation.Autowired

import kotlin.text.lineSequence
import kotlin.collections.dropLastWhile
import kotlin.sequences.sequenceOf
import kotlin.math.max

import kotlinx.html.*

import rolfrander.aoc.*

class Layer(val width: Int, val height: Int, d: List<Int>) {
    val pixel = d.toTypedArray()

    fun countDigits(d: Int) = pixel.filter { it == d }.count()

    fun stack(o: Layer): Layer {
        return pixel.zip(o.pixel)
                    .map { if(it.first == 2) { it.second } else { it.first } }
                    .toList()
                    .let { Layer(width, height, it) }
    }

    val DISP: String = ".X-"

    fun pre(): String {
        return pixel.map { DISP[it] }
                    .joinToString("")
                    .chunked(width)
                    .joinToString("\n")
    }

}

@RestController
@RequestMapping("/day08/")
class Day08 @Autowired constructor(config: AocData): AocBase(config, 8, """011222222100""") {

    fun fuel(weight: Int) = max(weight / 3 - 2, 0)

    fun parse(data: String, width: Int, height: Int): List<Layer> {
        return data.map { it.digitToIntOrNull() }
                   .filterNotNull()
                   .chunked(width*height)
                   .map { Layer(width, height, it) }
                   .toList()
    }

    fun computePart1(data: String, width: Int, height: Int): Int {
        return parse(data, width, height)
               .map { Pair(it,it.countDigits(0)) }
               .minWith(compareBy { it.second })
               .let { it.first }
               .let { it.countDigits(1)*it.countDigits(2) }
    }

    fun computePart2(data: String, width: Int, height: Int): Layer {
        return parse(data, width, height)
               .reduce { a,b -> a.stack(b) }
    }

    override fun formatResult(result: Any): BODY.() -> Unit {
        log.debug("format result of %s:%s".format(result, result::class))
        if(result is Layer) {
            // part 2
            val r = result.pre()
            log.debug("format part 2: %s".format(r))
            return { pre { +r } }
        } else {
            // part 1
            log.debug("format part 1")
            return super.formatResult(result)
        }
    }

    override fun part1(data: String): Any {
        if(isTesting) {
            return computePart1(data, 3, 2)
        } else {
            return computePart1(data, 25, 6)
        }
    }

    override fun part2(data: String): Any {
        if(isTesting) {
            return computePart2(data, 3, 2)
        } else {
            return computePart2(data, 25, 6)
        }
    }
}
