package rolfrander.aoc2019

import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.beans.factory.annotation.Autowired

import kotlin.text.lineSequence
import kotlin.collections.dropLastWhile
import kotlin.sequences.sequenceOf
import kotlin.math.max

import rolfrander.aoc.*

@RestController
@RequestMapping("/day01/")
class Day01 @Autowired constructor(config: AocData): AocBase(config, 1, "100756") {

    fun fuel(weight: Int) = max(weight / 3 - 2, 0)

    override fun part1(data: String): Int {
        return data.splitLines()
                   .map { it.toInt() }
                   .map { it / 3 - 2}
                   .sum()
    }

    override fun part2(data: String): Int {
        val fuelWeight = part1(data)
        return generateSequence(fuelWeight) { fuel(it) }
               .takeWhile { it > 0 }
               .sum()
    }

}