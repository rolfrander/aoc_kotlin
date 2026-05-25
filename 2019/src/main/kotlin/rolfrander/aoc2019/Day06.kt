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

@RestController
@RequestMapping("/day06/")
class Day06 @Autowired constructor(config: AocData): AocBase(config, 6, """COM)B
B)C
C)D
D)E
E)F
B)G
G)H
D)I
E)J
J)K
K)L
K)YOU
I)SAN""") {

    data class Orbit(val center: String, val sat: String) {
        override fun toString() = "${center})${sat}"
        companion object OrbitParser {
            fun create(line: String): Orbit {
                val (center,sat)=line.split(")")
                return Orbit(center,sat)
            }
        }
    }

    fun parse(data: String): Map<String,String> =
        data.splitLines()
            .mapNotNull { line -> line.split(")").let { (a,b) -> Pair(b,a)} }
            .toMap()

    fun countOrbits(o: Map<String,String>, start: String): Int {
        if(o.contains(start)) {
            return countOrbits(o, o.get(start) ?: "")+1
        } else {
            return 0
        }
    }

    fun countOrbitsToMap(o: Map<String,String>, start: String): Map<String,Int> {
        var e = o.get(start) ?: ""
        var distances: MutableMap<String,Int> = HashMap<String,Int>()
        var dist = 0
        while(o.contains(e)) {
           distances.put(e, dist++) 
           e = o.get(e)!!
        }
        return distances
    }

    override fun part1(data: String): Any {
        val orbitmap = parse(data)
        return orbitmap.keys
                       .map { countOrbits(orbitmap, it) }
                       .sum()
    }

    override fun part2(data: String): Any {
        val maximumDist = 295834 // the result from part1, the computed distance will never be larger than this
        val orbitmap = parse(data)
        val youdist = countOrbitsToMap(orbitmap, "YOU")
        var san = orbitmap.get("SAN")
        var dist = 0
        while(!youdist.contains(san)) {
            dist++
            if(dist > maximumDist) {
                throw RuntimeException("we got lost!")
            }
            san = orbitmap.get(san)
            if(san == null) {
                throw RuntimeException("found no common nodes?")
            }
        }
        return dist+youdist.get(san)!!
    }
}

