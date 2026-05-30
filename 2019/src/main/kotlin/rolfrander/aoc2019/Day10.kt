package rolfrander.aoc2019

import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2

import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.beans.factory.annotation.Autowired

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import rolfrander.aoc.*

fun sq(x: Int): Int = x*x

fun gcd(a: Int, b: Int): Int {
    var _a = abs(a)
    var _b = abs(b)
    while(_b != 0) {
        val t = _b
        _b = _a % _b
        _a = t
    }
    return _a
}

val PI_2 = PI/2

data class Vector(val x: Int, val y: Int) {
    /**
     * Not really normalization (that would be the unit-vector), but 
     * atleast reducing to smallest vector with integer values
     */
    fun normalize(): Vector {
        val f = gcd(x,y)
        if(f == 1) {
            return this
        }
        return Vector(x/f, y/f)
    }

    /**
     * approximate rotation angle in radians
     */
    fun rot(): Double {
        val res = atan2(y.toDouble(), x.toDouble())+PI_2
        if(res >= 0.0) {
            return res
        } else {
            return res + 2*PI
        }
    }
}

class AsteroidField(mapimage: String) {

    val log = LogFactory.getLog(this.javaClass)


    inner class Asteroid(val x: Int, val y: Int) {
        operator fun minus(o: Asteroid) = Vector(x-o.x, y-o.y)
        operator fun plus(v: Vector) = Asteroid(x+v.x,y+v.y)
        override fun equals(other: Any?) = this === other || ((other is Asteroid) && (x==other.x) && (y==other.y))
        override fun hashCode() = x+y*65536
        override fun toString() = "[%d,%d]".format(x,y)
        fun inField(): Boolean {
            return (x>=0) && (x<=width) && (y>=0) && (y<=height)
        }
    }


    val asteroids = mapimage.splitLines()
                .foldIndexed(mutableSetOf<Asteroid>()) { 
                    y, results, line -> line.foldIndexed(results) {
                        x, res2, c -> 
                            if(c == '#') {
                                res2.add(Asteroid(x,y)) 
                            }
                            res2
                    }
                }

    val width = asteroids.maxOf { it.x }
    val height= asteroids.maxOf { it.y }
    
    /**
     * for all asteroids, remove all other asteroids it blocks from the 
     * point x,y
     */
    fun sieve(origo: Asteroid, startset: Set<Asteroid>): Set<Asteroid> {
        val visible = startset.toMutableSet()
        visible.remove(origo)
        for(a in startset) {
            if(a == origo) {
                continue
            }
            // find all asteroids in the shadow of a, and remove them 
            // from the visible set
            val Δ = (a-origo).normalize()
            var cur = a + Δ
            while(cur.inField()) {
                if(visible.contains(cur)) {
                    visible.remove(cur)
                }
                cur = cur + Δ
            }
        }
        log.debug("visible from %s: %s".format(origo, visible))
        return visible
    }

    fun countVisible(origo: Asteroid): Int {
        return sieve(origo, asteroids).count()
    }

    fun shoot(origo: Asteroid, nth: Int): Asteroid {
        // assume nth counts from 1, subtract 1 to compensate
        var n = nth-1
        var allAsteroids = asteroids.toMutableSet()
        var visibleSet = sieve(origo, asteroids)
        while(n - visibleSet.count() >= 0) {
            if(visibleSet.count() == 0) {
                throw RuntimeException("no more elements to shoot")
            }
            n -= visibleSet.count()
            allAsteroids.removeAll(visibleSet)
            log.debug("left in allAsteroids, n=%d: %s".format(n, allAsteroids))
            visibleSet = sieve(origo, allAsteroids)
        }
        return visibleSet.sortedBy { a -> (a - origo).rot() }
                         .get(n)
    }

}

@RestController
@RequestMapping("/day10/")
class Day10 @Autowired constructor(config: AocData): AocBase(config, 10, """......#.#.
#..#.#....
..#######.
.#.#.###..
.#..#.....
..#....#.#
#..#....#.
.##.#..###
##...#..#.
.#....####""") {
   
    override fun part1(data: String): Any {
        val field = AsteroidField(data)
        return field.asteroids.maxOf { a -> field.countVisible(a) }
    }

    override fun part2(data: String): Any {
        val n = if(isTesting) { 34 } else { 200 }
        val field = AsteroidField(data)
        // I think this counts visible asteroids twice as many times as 
        // neccessary, should cache the result of countvisible 
        // somewhere...
        val posComparator = compareBy<AsteroidField.Asteroid>{ a -> field.countVisible(a) }
        val origo = field.asteroids.maxWith(posComparator)
        log.debug("origo = %s".format(origo))
        log.debug("visible count = %d".format(field.countVisible(origo)))
        log.debug("find element no %d".format(n))
        return field.shoot(origo, n).let { it.x*100+it.y }
    }

}
