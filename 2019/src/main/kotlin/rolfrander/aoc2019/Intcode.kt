package rolfrander.aoc2019


import kotlin.collections.List
import kotlin.collections.ArrayList
import kotlinx.coroutines.*

import org.apache.commons.logging.LogFactory

import rolfrander.aoc.*

enum class OpCode(val params: Int) {
    NOP(0),
    ADD(3),
    MUL(3),
    IN(1),
    OUT(1),
    JNZ(2),
    JZ(2),
    LT(3),
    EQ(3),
    BAS(1),
    DUMMY(1)
}

/**
 * Abstraction of memory. For day 9 we need the program to be able to 
 * access memory outside of the initially loaded program. In practice, 
 * just extending the array with 0 is good enough, but for a more 
 * interresting problem, this class implements paged memory accessed by 
 * a hash-table, a sort of translation-lookaside-buffer.
 */
class Memory(memoryIn: List<Long>) {
    val pageSz:Int = 1024
    val memory:MutableMap<Long,LongArray> = HashMap<Long,LongArray>()

    constructor(s: String): this(s.parseLongs().toList())

    init {
        memoryIn.chunked(pageSz)
                .map { LongArray(pageSz) { i -> it.getOrNull(i)?.toLong() ?: 0L } }
                .mapIndexed { i,v -> Pair(i.toLong(),v) }
                .let { memory.putAll(it.asIterable()) }
    }

    operator fun get(pos: Long): Long {
        val page = pos / pageSz
        val off  = (pos % pageSz).toInt()
        if(page in memory) {
            return memory[page]!!.get(off)
        } else {
            return 0L
        }
    }

    operator fun set(pos: Long, v: Long) {
        val page = pos / pageSz
        val off  = (pos % pageSz).toInt()
        if(page !in memory) {
            memory[page] = LongArray(pageSz)
        }
        memory[page]?.set(off, v)
    }

}

open class Intcode(val memory: Memory, val id: Int = -1) {

    val log = LogFactory.getLog(this.javaClass)
    val logInstr = LogFactory.getLog("rolfrander.aoc2019.Intcode.INSTR")

    var ip: Long    = 0
    var base: Long  = 0
    var cnt: Long   = 0
    var inpos: Int = 0
    val outputs: ArrayList<Long> = ArrayList<Long>()
    var inputs: Sequence<Long> = emptySequence()


    suspend fun input(): Long {
        val v = inputs.first()
        inputs = inputs.drop(1)
        return v
    }

    suspend fun output(i: Long): Unit { outputs.add(i) }

    suspend fun tick(inputFn : suspend ()     -> Long = { input() } , 
                     outputFn: suspend (Long) -> Unit = { output(it) }) {
        val opcode = OpCode.entries[(memory[ip] % 100).toInt()]
        var modes = (memory[ip] / 100).toInt()
        val paramref = LongArray(4)
        // reg0 is the opcode, ignored
        // we also read the destination address in to a register, which 
        // isn't really needed when using setMem().
        for(i in 1.rangeTo(opcode.params)) {
            val r = ip+i
            paramref[i] = when(modes % 10) {
                0 -> memory[r]
                1 -> r
                2 -> memory[r]+base
                else -> throw RuntimeException("unknown addressing mode for opcode ${memory[ip]}")
            }

            modes = modes/10
        }

        val setmem:(Int,Long) -> Unit = { regno,v -> memory[paramref[regno]] = v }
        val reg:(Int) -> Long = { regno -> memory[paramref[regno]] }

        if(log.isDebugEnabled()) {
            modes = (memory[ip] / 100).toInt()
            var param = ""
            if(id >= 0) {
                param += "(%02d) ".format(id)
            }
            param = "%05d: %3.3s".format(memory[ip], opcode.toString())
            for(i in 1.rangeTo(opcode.params)) {
                val r = memory[ip+i]
                when(modes % 10) {
                    0 -> param += " [%d]%d".format(memory[ip+i], reg(i))
                    1 -> param += " %d".format(reg(i))
                    2 -> param += " [%d+%d]%d".format(base, memory[ip+i], reg(i))
                    else -> throw RuntimeException("unknown addressing mode for opcode ${memory[ip]}")
                }
                modes = modes/10
            }
            logInstr.debug(param)
        }

        var newip = -1L
        when(opcode) {
            OpCode.ADD -> { setmem(3, reg(1)+reg(2) )                        }
            OpCode.MUL -> { setmem(3, reg(1)*reg(2) )                        }
            OpCode.IN  -> { setmem(1, input())                            }
            OpCode.OUT -> { output(reg(1))                                   }
            OpCode.JNZ -> { if(reg(1) != 0L) { newip = reg(2) }              }
            OpCode.JZ  -> { if(reg(1) == 0L) { newip = reg(2) }              }
            OpCode.LT  -> { setmem(3, if(reg(1)  < reg(2)) { 1 } else { 0 }) }
            OpCode.EQ  -> { setmem(3, if(reg(1) == reg(2)) { 1 } else { 0 }) }
            OpCode.BAS -> { base += reg(1)                                   }
            else-> {
                throw RuntimeException("invalid opcode: ${memory[ip]}, ip=${ip}, cnt=${cnt}")
            }
        }
        if(newip >= 0) {
            ip = newip
        } else {
            ip += 1+opcode.params
        }
        cnt++
    }

    suspend fun run(inputFn : suspend ()     -> Long = ::input , 
                    outputFn: suspend (Long) -> Unit = ::output) {
        try {
            while(memory[ip] != 99L) {
                tick()
            }
        } catch(e: Exception) {
          throw RuntimeException("error ${e.message} at ip=${ip}, cnt=${cnt}", e)
        }
    }

    fun runFromMemory(vararg input: Long): Long {
        return runBlocking {
            for(i in 0.rangeUntil(input.size)) {
                memory[i+1L] = input[i]
            }
            run()
            memory[0]
        }
    }
}
