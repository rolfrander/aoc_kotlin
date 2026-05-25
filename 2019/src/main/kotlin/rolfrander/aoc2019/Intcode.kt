package rolfrander.aoc2019


import kotlin.collections.List
import kotlin.collections.ArrayList
import kotlinx.coroutines.*

import org.apache.commons.logging.LogFactory

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
    DUMMY(1)
}

class Intcode(memoryIn: List<Int>, val id:Int=-1) {

    val log = LogFactory.getLog(this.javaClass)
    val logInstr = LogFactory.getLog("rolfrander.aoc2019.Intcode.INSTR")

    var ip: Int    = 0
    var cnt: Int   = 0
    var inpos: Int = 0
    val outputs: ArrayList<Int> = ArrayList<Int>()
    var inputs: Sequence<Int> = emptySequence()
    val memory: IntArray = memoryIn.toIntArray()


    fun input(): Int {
        val v = inputs.first()
        inputs = inputs.drop(1)
        return v
    }

    fun output(i: Int):Unit {
        outputs.add(i)
    }

    fun setMem(relpos: Int, v: Int) {
        memory[memory[ip+relpos]] = v
    }

    suspend fun tick(inputFn : suspend ()    -> Int  = ::input , 
                     outputFn: suspend (Int) -> Unit = ::output) {
        if(ip >= memory.size) {
            throw RuntimeException("segfault ip, cnt=${cnt}, ip=${ip}")
        }
        val opcode = OpCode.entries[memory[ip] % 100]
        val reg = IntArray(4)
        var modes = memory[ip] / 100
        // reg0 is the opcode, ignored
        // we also read the destination address in to a register, which isn't really needed when using setMem()
        for(i in 1.rangeTo(opcode.params)) {
            if(modes % 10 == 1) {
                reg[i] = memory[ip+i]
            } else {
                reg[i] = memory[memory[ip+i]]
            }
            modes = modes/10
        }

        if(logInstr.isDebugEnabled()) {
            modes = memory[ip] / 100
            var param = ""
            if(id >= 0) {
                param += "(%02d) ".format(id)
            }
            param += "%05d: %3.3s".format(memory[ip], opcode.toString())
            for(i in 1.rangeTo(opcode.params)) {
                if(modes % 10 == 1) {
                    param += " %d".format(reg[i])
                } else {
                    param += " [%d]%d".format(memory[ip+i], reg[i])
                }
                modes = modes/10
            }
            logInstr.debug(param)
        }

        var newip = -1
        when(opcode) {
            OpCode.ADD -> { setMem(3, reg[1]+reg[2])                         }
            OpCode.MUL -> { setMem(3, reg[1]*reg[2])                         }
            OpCode.IN  -> { setMem(1, inputFn())                             }
            OpCode.OUT -> { outputFn(reg[1])                                 }
            OpCode.JNZ -> { if(reg[1] != 0) { newip = reg[2] }               }
            OpCode.JZ  -> { if(reg[1] == 0) { newip = reg[2] }               }
            OpCode.LT  -> { setMem(3, if(reg[1]  < reg[2]) { 1 } else { 0 }) }
            OpCode.EQ  -> { setMem(3, if(reg[1] == reg[2]) { 1 } else { 0 }) }
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

    suspend fun run(inputFn : suspend ()    -> Int  = ::input , 
                    outputFn: suspend (Int) -> Unit = ::output) {
        try {
            while(memory[ip] != 99) {
                tick(inputFn, outputFn)
            }
        } catch(e: Exception) {
          throw RuntimeException("error ${e.message} at ip=${ip}, cnt=${cnt}", e)
        }
    }

    fun runFromMemory(vararg input: Int): Int {
        return runBlocking {
            for(i in 0.rangeUntil(input.size)) {
                memory[i+1] = input[i]
            }
            run()
            memory[0]
        }
    }
}
