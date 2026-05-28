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
@RequestMapping("/day02/")
class Day02 @Autowired constructor(config: AocData): AocBase(config, 2, "1,1,1,4,99,5,6,0,99") {

    fun fuel(weight: Int) = max(weight / 3 - 2, 0)

    override fun part1(data: String): Long {
        val mem = Memory(data)
        if(isTesting) {
            return Intcode(mem).runFromMemory()
        } else {
            return Intcode(mem).runFromMemory(12,2)
        }
    }

    override fun part2(data: String): Long {
        val mem = Memory(data)
        val expectedResult = 19690720L
        if(isTesting) {
            return -1
        } else {
            for(noun in 0L..<100L) {
                for(verb in 0L..<100L) {
                    try {
                        if(Intcode(mem).runFromMemory(noun, verb) == expectedResult) {
                            return noun*100+verb
                        }
                    } catch(e: RuntimeException) {
                        log.error("VM crash with parameters noun=${noun}, verb=${verb}, message: ${e.message}")
                    }
                }
            }
            return -1
        }
    }

}
