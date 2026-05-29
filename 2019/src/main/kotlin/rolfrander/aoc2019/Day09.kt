package rolfrander.aoc2019

import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.beans.factory.annotation.Autowired

import kotlin.collections.dropLastWhile
import kotlin.sequences.sequenceOf
import kotlin.sequences.generateSequence
import kotlin.math.max
import kotlin.math.abs
import kotlin.text.lineSequence
import kotlin.text.Regex

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*

import rolfrander.aoc.*

@RestController
@RequestMapping("/day09/")
class Day09 @Autowired constructor(config: AocData): AocBase(config, 9, """109,1,204,-1,1001,100,1,100,1008,100,16,101,1006,101,0,99""") {

    fun run(data: String, input: Long): Any {
        val vm = Intcode(Memory(data))
        vm.inputs = sequenceOf(input)
        vm.run()
        return vm.outputs
    }

    val test2 = "1102,34915192,34915192,7,4,7,99,0"
    val test3 = "104,1125899906842624,99"

    override fun part1(data: String): Any {
        if(isTesting) {
            log.info("test2: "+run(test2,0L))
            log.info("test3: "+run(test3,0L))
        }
        return run(data,1L)
    }

    override fun part2(data: String): Any {
        return run(data,2L)
    }

}
