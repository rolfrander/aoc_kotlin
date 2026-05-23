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

    override fun part1(data: String): Int {
        val mem = data.parseInts().toList()
        if(isTesting) {
            return Intcode(mem).run()
        } else {
            return Intcode(mem).run(12,2)
        }
    }

    override fun part2(data: String): Int {
        val mem = data.parseInts().toList()
        val expectedResult = 19690720
        if(isTesting) {
            return -1
        } else {
            for(noun in 0..<100) {
                for(verb in 0..<100) {
                    try {
                        if(Intcode(mem).run(noun, verb) == expectedResult) {
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
