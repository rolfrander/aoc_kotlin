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
@RequestMapping("/day05/")
class Day05 @Autowired constructor(config: AocData): AocBase(config, 5, "1002,5,3,0,99,33") {


    override fun part1(data: String): Any {
        val vm = Intcode(data.parseInts().toList())
	vm.inputs = sequenceOf(1)
        vm.run()
	return vm.outputs
    }

    override fun part2(data: String): Any {

        return -1
    }

}
