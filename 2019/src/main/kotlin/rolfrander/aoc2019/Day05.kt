package rolfrander.aoc2019

import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.beans.factory.annotation.Autowired

import kotlin.text.lineSequence
import kotlin.collections.dropLastWhile
import kotlin.sequences.sequenceOf
import kotlin.math.max
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*

import rolfrander.aoc.*

@RestController
@RequestMapping("/day05/")
class Day05 @Autowired constructor(config: AocData): AocBase(config, 5, "1002,5,3,0,99,33") {

    fun run(data: String, input: Long): Any {
        return runBlocking {
            val vm = Intcode(Memory(data))
            vm.inputs = sequenceOf(input)
            vm.run()
            vm.outputs
        }
    }

    /* 
     * For example, here are several programs that take one input, compare it to the value 8, and then produce one output:
     * 
     *     3,9,8,9,10,9,4,9,99,-1,8 - Using position mode, consider whether the input is equal to 8; output 1 (if it is) or 0 (if it is not).
     *     3,9,7,9,10,9,4,9,99,-1,8 - Using position mode, consider whether the input is less than 8; output 1 (if it is) or 0 (if it is not).
     *     3,3,1108,-1,8,3,4,3,99 - Using immediate mode, consider whether the input is equal to 8; output 1 (if it is) or 0 (if it is not).
     *     3,3,1107,-1,8,3,4,3,99 - Using immediate mode, consider whether the input is less than 8; output 1 (if it is) or 0 (if it is not).
     * 
     * Here are some jump tests that take an input, then output 0 if the input was zero or 1 if the input was non-zero:
     * 
     *     3,12,6,12,15,1,13,14,13,4,13,99,-1,0,1,9 (using position mode)
     *     3,3,1105,-1,9,1101,0,0,12,4,12,99,1 (using immediate mode)
     * 
     * Here's a larger example:
     * 
     * 3,21,1008,21,8,20,1005,20,22,107,8,21,20,1006,20,31,
     * 1106,0,36,98,0,0,1002,21,125,20,4,20,1105,1,46,104,
     * 999,1105,1,46,1101,1000,1,20,4,20,1105,1,46,98,99
     * 
     * The above example program uses an input instruction to ask for a single number.
     * The program will then output 999 if the input value is below 8, output 1000 if
     * the input value is equal to 8, or output 1001 if the input value is greater than 8.
     */
    val test1 = "3,9,8,9,10,9,4,9,99,-1,8"
    val test2 = "3,9,7,9,10,9,4,9,99,-1,8"
    val test3 = "3,3,1108,-1,8,3,4,3,99"
    val test4 = "3,3,1107,-1,8,3,4,3,99"
    val test5 = "3,12,6,12,15,1,13,14,13,4,13,99,-1,0,1,9"
    val test6 = "3,3,1105,-1,9,1101,0,0,12,4,12,99,1"
    val test7 = "3,21,1008,21,8,20,1005,20,22,107,8,21,20,1006,20,31,1106,0,36,98,0,0,1002,21,125,20,4,20,1105,1,46,104,999,1105,1,46,1101,1000,1,20,4,20,1105,1,46,98,99"

    override fun part1(data: String): Any {
        return run(data, 1L)
    }

    override fun part2(data: String): Any {
        if(isTesting) {
            log.info("test1(7): "+run(test1, 7))
            log.info("test1(8): "+run(test1, 8))
            log.info("test7(7): "+run(test7, 7))
            log.info("test7(8): "+run(test7, 8))
            log.info("test7(9): "+run(test7, 9))
        }
        return run(data, 5)
    }

}
