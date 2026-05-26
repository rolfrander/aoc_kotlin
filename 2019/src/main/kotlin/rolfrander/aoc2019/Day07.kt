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

/**
 * pipe-function moving data from a RecieveChannel to a SendChannel
 */
fun <T> CoroutineScope.pipe(
    input: ReceiveChannel<T>,
    output: SendChannel<T>
) = launch {
    for (x in input) {
        output.send(x)
    }
    output.close()
}


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

        yield(a.copyOf())

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

suspend fun testSender(chan: SendChannel<Int>): Unit {
    println("starting sender")
    for(i in 10 downTo 0) {
        Thread.sleep(1000) 
        chan.send(i)
    }
    chan.close() 
    println("stopping sender")
}

suspend fun testReceiver(chan: ReceiveChannel<Int>): Unit {
    println("starting receiver")
    while(true) {
        val result = chan.receiveCatching()
        when(val element = result.getOrNull()) {
            null -> { println("channel closed") ; break ; }
            else -> println(element)
        }
    }
    println("stopping receiver")
}

fun testDay07b(arg: Array<String>) {
    return runBlocking {
        val chan = Channel<Int>()
        async { testSender(chan) }
        async { testReceiver(chan) }
    }
}

suspend fun runWithInput(vm: Intcode, phaseSetting: Long, input: Long): Long {
    vm.inputs = sequenceOf(phaseSetting, input)
    vm.run()
    return vm.outputs.first()
}

suspend fun run(program: String, input: Array<Long>): Long {
    var out = 0L
    for(i in 0..4) {
        out = runWithInput(Intcode(Memory(program)), input[i], out) 
    }
    return out
}

suspend fun runWithChannels(program: String, 
                            i: Int,
                            inChan: ReceiveChannel<Long>,
                            outChan: SendChannel<Long>) {
    val vm = Intcode(Memory(program), i)
    vm.run(inChan::receive, outChan::send)
    outChan.close()
}


@RestController
@RequestMapping("/day07/")
class Day07 @Autowired constructor(config: AocData): AocBase(config, 7, """3,15,3,16,1002,16,10,16,1,16,15,15,4,15,99,0,0""") {

    override fun part1(data: String): Any {
        return runBlocking {
            var best = Long.MIN_VALUE

            for (perm in permutations(arrayOf(0L,1L,2L,3L,4L))) {
                best = maxOf(best, run(data, perm))
            }
            best
        }
    }

    val testvector: Array<String> =
        arrayOf("3,26,1001,26,-4,26,3,27,1002,27,2,27,1,27,26,27,4,27,1001,28,-1,28,1005,28,6,99,0,0,5",
                "3,52,1001,52,-5,52,3,53,1,52,56,54,1007,54,5,55,1005,55,26,1001,54,-5,54,1105,1,12,1,53,54,53,1008,54,0,55,1001,55,1,55,2,53,55,53,4,53,1001,56,-1,56,1005,56,6,99,0,0,0,0,10")

    val testparams = arrayOf(arrayOf(9L,8L,7L,6L,5L), arrayOf(9L,7L,8L,5L,6L))
    
    suspend fun configureAmps(program: String, params: Array<Long>): Long = coroutineScope {
        val channels = Array(5) { _ -> Channel<Long>() }
        log.debug("        launch each VM")
        val invocation = Array(5) {
            i -> async {
                    log.debug("        - launch ${i}")
                    runWithChannels(data, i, channels[i], channels[(i+1) % 5])
                    log.debug("        v done ${i}")
            }
        }

        // we insert values in the opposite direction to make sure that
        // the last VM starts first. This avoids the race condition
        // where a VM outputs a value before we send the input parameter.
        for((param,chan) in params.zip(channels).asReversed()) {
            log.debug("        -> send ${param}")
            chan.send(param)
            log.debug("        !  sent ${param}")
        }
        // channels[0] is the output of channel 4 and input of channel 0
        channels[0].send(0)
        // when VM-0 is done...
        invocation[0].await()
        // ... we wait for the last output from VM[4]
        channels[0].receive()
    }



    suspend fun configureAmpsForTestcase(testcase: Int): Long {
        val program = testvector[testcase]
        val params = testparams[testcase]
        return configureAmps(program, params)
    }
    
    override fun part2(data: String): Any {
        return runBlocking {
            if(isTesting) {
                log.debug("day 7 - testing")
                "res 0: %d, res 1: %d".format(configureAmpsForTestcase(0), configureAmpsForTestcase(1))
            } else {
                var best = Long.MIN_VALUE

                for (perm in permutations(arrayOf(5L,6L,7L,8L,9L))) {
                    log.debug("running with inputs %s".format(perm))
                    best = maxOf(best, configureAmps(data, perm))
                }
                best
            }
        }
    }
}
