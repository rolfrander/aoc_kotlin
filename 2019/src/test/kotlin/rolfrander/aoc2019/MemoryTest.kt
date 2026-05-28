package rolfrander.aoc2019

import kotlin.collections.ArrayList

import org.junit.jupiter.api.Assertions.assertEquals

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource


class MemoryTest {

    @Test
    fun initMemShort() {
        val data = listOf(0L, 1L, 2L, 3L)
        val mem = Memory(data)
        for(i in 0L..3L) {
            assertEquals(i, mem[i], "expected element ${i} to have value ${i}")
        }
    }

    @Test
    fun initMemLong() {
        val data = ArrayList<Long>()
        for(i in 0L..1026L) {
            data.add(i)
        }
        val mem = Memory(data)
        assertEquals(mem[0], 0L, "expected element 0 to have value 0")
        assertEquals(mem[1023], 1023L, "expected element 1023 to have value 1023")
        assertEquals(mem[1024], 1024L, "expected element 1024 to have value 1024")
        assertEquals(mem[1025], 1025L, "expected element 1025 to have value 1025")
        assertEquals(mem[1026], 1026L, "expected element 1026 to have value 1026")
    }

    @Test
    fun writeInsideBounds() {
        val data = listOf(0L, 1L, 2L, 3L)
        val mem = Memory(data)
        assertEquals(1L, mem[1L], "expected element 1 to be 1")
        mem[1L] = 42L
        assertEquals(42L, mem[1L], "expected element 1 to be 42")
    }
    
    @Test
    fun writeOutsideBounds() {
        val data = listOf(0L, 1L, 2L, 3L)
        val mem = Memory(data)
        mem[9L] = 42L
        assertEquals(42L, mem[9L], "expected element 1 to be 42")
    }

    @Test
    fun readOutsideBounds() {
        val data = listOf(0L, 1L, 2L, 3L)
        val mem = Memory(data)
        assertEquals(0L, mem[9L], "expected element 9 of non-initialized memory to be 0")
    }

    @Test
    fun writeOutsidePage() {
        val data = listOf(0L, 1L, 2L, 3L)
        val mem = Memory(data)
        mem[2000L] = 42L
        assertEquals(42L, mem[2000L], "expected element 2000 to be 42")
    }

    @Test
    fun readOutsidePage() {
        val data = listOf(0L, 1L, 2L, 3L)
        val mem = Memory(data)
        assertEquals(0L, mem[2000L], "expected element 2000 of non-initialized memory to be 0")
    }
}

