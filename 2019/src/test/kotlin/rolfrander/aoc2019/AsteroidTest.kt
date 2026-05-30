package rolfrander.aoc2019

import kotlin.collections.ArrayList

import org.junit.jupiter.api.Assertions.assertEquals

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource


class AsteroidTest {

    @Test
    fun testGcd() {
        assertEquals(2, gcd(6,8))
        assertEquals(2, gcd(6,-8))
        assertEquals(2, gcd(-6,-8))
    }

    @Test
    fun testNormalize() {
        assertEquals(Vector(2,3), Vector(4,6).normalize())
        assertEquals(Vector(2,-3), Vector(4,-6).normalize())
        assertEquals(Vector(-2,3), Vector(-4,6).normalize())
        assertEquals(Vector(-2,-3), Vector(-4,-6).normalize())
        assertEquals(Vector(2,5), Vector(2,5).normalize())
    }

    fun rotAndPrint(v: Vector) {
        println("%s %f".format(v, v.rot()))
    }

    @Test
    fun testRot() {
        println("forventer stigende resultater her")
        rotAndPrint(Vector(0,-1))    
        rotAndPrint(Vector(1,-1000))    
        rotAndPrint(Vector(1,-1))    
        rotAndPrint(Vector(1000,-1))    
        rotAndPrint(Vector(1,0))    
        rotAndPrint(Vector(1000,1))    
        rotAndPrint(Vector(1,1))    
        rotAndPrint(Vector(-1,1000))    
        rotAndPrint(Vector(0,1))    
        rotAndPrint(Vector(1,1000))    
        rotAndPrint(Vector(-1,1))    
        rotAndPrint(Vector(-1000,1))    
        rotAndPrint(Vector(-1,0))    
        rotAndPrint(Vector(-1000,-1))    
        rotAndPrint(Vector(-1,-1))    
        rotAndPrint(Vector(-1,-1000))    
    }

    @Test
    fun testShoot() {
        val field = AsteroidField("""..........
..........
.....#....
..........
..........
..........
...#......
..........
#.........
..........""")
        val origo = field.Asteroid(5,8)
        println(field.asteroids)
        println(field.asteroids.map { a -> (a - origo).rot() }.toList())
        assertEquals(field.Asteroid(5,2), field.shoot(origo, 1))
        
    }

}
