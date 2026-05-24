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

import rolfrander.aoc.*

// https://en.wikipedia.org/wiki/Heap%27s_algorithm
// procedure permutations(n : integer, A : array of any):
//     // c is an encoding of the stack state.
//     // c[k] encodes the for-loop counter for when permutations(k + 1, A) is called
//     c : array of int
// 
//     for i := 0; i < n; i += 1 do
//         c[i] := 0
//     end for
// 
//     output(A)
//     
//     // i acts similarly to a stack pointer
//     i := 1;
//     while i < n do
//         if  c[i] < i then
//             if i is even then
//                 swap(A[0], A[i])
//             else
//                 swap(A[c[i]], A[i])
//             end if
//             output(A)
//             // Swap has occurred ending the while-loop. Simulate the increment of the while-loop counter
//             c[i] += 1
//             // Simulate recursive call reaching the base case by bringing the pointer to the base case analog in the array
//             i := 1
//         else
//             // Calling permutations(i+1, A) has ended as the while-loop terminated. Reset the state and simulate popping the stack by incrementing the pointer.
//             c[i] := 0
//             i += 1
//         end if
//     end while
fun <O> permutations(a: Array<O>): Sequence<Array<O>> {
    val n = a.size
    return sequence {
        val c = IntArray(n)

        yield(a)

        var i = 1
        while(i<n) {
            if(c[i] < i) {
                if(i%2 == 0) {
                    a[0] = a[i].also { a[i] = a[0] }
                } else {
                    a[c[i]] = a[i].also { a[i] = a[c[i]] }
                }
                yield(a)
                c[i]++
                i=1
            } else {
                c[i] = 0
                i++
            }
        }
    }
}

fun testDay07(arg: Array<String>) {
    permutations(arg).forEach { println(it.contentToString()) }
}

@RestController
@RequestMapping("/day07/")
class Day07 @Autowired constructor(config: AocData): AocBase(config, 7, """3,15,3,16,1002,16,10,16,1,16,15,15,4,15,99,0,0""") {
   
    fun run(program: List<Int>, input: Array<Int>): Int {
        val vm = { Intcode(program) }
        return sequenceOf(0).let { vm().runIo(sequenceOf(input[0])+it) }
                            .let { vm().runIo(sequenceOf(input[1])+it) }
                            .let { vm().runIo(sequenceOf(input[2])+it) }
                            .let { vm().runIo(sequenceOf(input[3])+it) }
                            .let { vm().runIo(sequenceOf(input[4])+it) }
                            .first()
    }

    override fun part1(data: String): Any {
        val program = data.parseInts().toList()
        return permutations(arrayOf(0,1,2,3,4))
               .map { run(program, it) }
               .max()
    }

    override fun part2(data: String): Any {
        if(isTesting) {
            val program = "3,26,1001,26,-4,26,3,27,1002,27,2,27,1,27,26,27,4,27,1001,28,-1,28,1005,28,6,99,0,0,5"
                          .parseInts().toList()
            return run(program, arrayOf(9,8,7,6,5))
        } else {
            return -1
        }
    }
}
