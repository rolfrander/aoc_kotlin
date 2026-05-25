package rolfrander.aoc2019

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Bean
import rolfrander.aoc.AocData

@SpringBootApplication
@Configuration
@ConfigurationProperties("aoc")
class Aoc2019Application {

	var sessionCookie = ""
    var cacheDir = ".cache"

	@Bean fun aocData(): AocData {
		return AocData("2019", sessionCookie, cacheDir)
	}
}



fun main(args: Array<String>) {
    if(args.size > 0) {
        when(args[0]) {
            "-test07"  -> if(args.size > 2) {
                              rolfrander.aoc2019.testDay07(args.copyOfRange(1, args.size))
                          } else {
                              println("-test07 requires atleast two more arguments")
                          }
            "-test07b" -> rolfrander.aoc2019.testDay07b(args.copyOfRange(1, args.size))
            else       -> println("unknown test: ${args[0]}")
        }
    } else {
    	runApplication<Aoc2019Application>(*args)
    }
}
