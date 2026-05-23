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
@RequestMapping("/day03/")
class Day03 @Autowired constructor(config: AocData): AocBase(config, 3, "R75,D30,R83,U83,L12,D49,R71,U7,L72\n"+
"U62,R66,U55,R34,D71,R55,D58,R83\n") {

    val re = Regex("([RLUD])([0-9]+)")
    
    enum class Direction(val dx: Int, val dy: Int) {
        U(0, -1), D(0, 1), L(-1, 0), R(1, 0)
    }

    data class Path(val dir: Direction, val dist: Int)

    data class Coord(val x: Int, val y: Int) {
        fun move(d: Direction) = Coord(x+d.dx, y+d.dy)
    }
    

    data class Wire(val path: Sequence<Path>) {
        fun positions(): Set<Coord> = path.flatMap { generateSequence { it.dir } .take(it.dist)}
                                          .scan(Coord(0,0)) { c,d -> c.move(d)}
                                          .drop(1)
                                          .toSet()
        fun posWithTiming(): Map<Coord,Int> {
            val retmap = HashMap<Coord,Int>()
            path.flatMap { generateSequence { it.dir } .take(it.dist)}
                .scan(Coord(0,0)) { c,d -> c.move(d)}
                .drop(1) // drop 1 here to avoid the simple solution of intersection at (0,0), need to compensate on the index on next line
                .forEachIndexed { idx,coord -> retmap.put(coord,idx+1) }
            return retmap
        }
    }

    fun parse(data: String): Sequence<Wire> =
        data.splitLines()
            .map {
                Wire(re.findAll(it).map { Path(Direction.valueOf(it.groupValues.get(1)),
                                                   it.groupValues.get(2).toInt()) }
                    )
            }

    override fun part1(data: String): Any {
        return parse(data)
               .map { it.positions() }
               .reduce { a,b -> a.intersect(b) }
               .map { abs(it.x)+abs(it.y) }
               .min()
               //.toList()
    }

    override fun part2(data: String): Any {
        val positionsWithTiming = parse(data)
                                   .map { it.posWithTiming() }
        return positionsWithTiming.map { it.keys } 
               .reduce { a,b -> a.intersect(b) }
               .map { intersection -> positionsWithTiming.map { it.get(intersection) ?: 0 }.sum() }
               .min()
    }

}