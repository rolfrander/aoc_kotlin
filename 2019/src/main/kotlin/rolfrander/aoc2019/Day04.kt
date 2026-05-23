package rolfrander.aoc2019

import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.beans.factory.annotation.Autowired

import kotlin.text.Regex
import kotlin.text.toInt

import rolfrander.aoc.*

@RestController
@RequestMapping("/day04/")
class Day04 @Autowired constructor(config: AocData): AocBase(config, 4, "111100-111120") {

    val re = Regex("[0-9]+")

    fun parse(data: String): IntRange {
        val values = re.findAll(data).map { it.groupValues[0].toInt() }.take(2).toList()
        return values[0].rangeTo(values[1])
    }

    fun hasDouble(num: String): Boolean {
        for(i in 1.rangeUntil(num.length)) {
            if(num[i-1] == num[i]) {
                return true
            }
        }
        return false
    }

    fun hasDoubleNotTriple(num: String): Boolean {
        var runningCounter = 1
        for(i in 1.rangeUntil(num.length)) {
            if(num[i-1] == num[i]) {
                runningCounter++
            } else {
                if(runningCounter == 2) {
                    return true
                }
                runningCounter = 1
            }
        }
        return runningCounter == 2
    }

    fun isIncreasing(num: String): Boolean {
        for(i in 1.rangeUntil(num.length)) {
            if(num[i-1] > num[i]) {
                return false
            }
        }
        return true

    }

    override fun part1(data: String) = 
        parse(data).map { it.toString() }
                   .filter { hasDouble(it) }
                   .filter { isIncreasing(it) }
                   .count()

    override fun part2(data: String): Any = 
        parse(data).map { it.toString() }
                   .filter { isIncreasing(it) }
                   .filter { hasDoubleNotTriple(it) }
                   .count()

}

