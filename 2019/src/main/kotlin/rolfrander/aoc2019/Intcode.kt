package rolfrander.aoc2019

import kotlin.enums.enumEntries
import kotlin.collections.List
import kotlin.collections.ArrayList
import org.apache.commons.logging.LogFactory

enum class OpCode(val params: Int) {
    NOP(0),
    ADD(3),
    MUL(3),
    IN(1),
    OUT(1),
    DUMMY(1)
}

class Intcode(memoryIn: List<Int>) {

    val log = LogFactory.getLog(this.javaClass)

    var ip: Int    = 0
    var cnt: Int   = 0
    var inpos: Int = 0
    val outputs: ArrayList<Int> = ArrayList<Int>()
    var inputs: Sequence<Int> = emptySequence()
    val memory: IntArray = memoryIn.toIntArray()

    // fun setInputs(i: Sequence<Int>) {
    //     inputs = i
    // }

    open fun getInput(): Int {
	val v = inputs.first()
	inputs = inputs.drop(1)
	return v
    }

    open fun output(i: Int) = outputs.add(i)

    fun setMem(relpos: Int, v: Int) {
        memory[memory[ip+relpos]] = v
    }

    fun tick() {
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

        if(log.isDebugEnabled()) {
            modes = memory[ip] / 100
            val param = Array<String>(4) {""}
            for(i in 1.rangeTo(opcode.params)) {
                if(modes % 10 == 1) {
                    param[i] = "%d".format(reg[i])
                } else {
                    param[i] = "[%d]%d".format(memory[ip+i], reg[i])
                }
                modes = modes/10
            }
            log.debug("%3.3s %s %s %s".format(opcode.toString(), param[1], param[2], param[3]))
        }

        when(opcode) {
            OpCode.ADD -> { setMem(3, reg[1]+reg[2]) }
            OpCode.MUL -> { setMem(3, reg[1]*reg[2]) }
            OpCode.IN  -> { setMem(1, getInput())    }
            OpCode.OUT -> { output(reg[1])           }
            else-> {
                throw RuntimeException("invalid opcode: ${memory[ip]}, ip=${ip}, cnt=${cnt}")
            }
        }
        ip += 1+opcode.params
        cnt++
    }

    fun run(): Int {
        while(memory[ip] != 99) {
            tick()
        }
	return memory[0]
    }

    fun run(in1: Int, in2: Int): Int {
        memory[1] = in1
        memory[2] = in2
        return run()
    }

    fun getMem(position: Int) = memory[position]
}
