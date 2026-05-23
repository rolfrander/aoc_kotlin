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

	@Bean fun aocData(): AocData {
		return AocData("2019", sessionCookie)
	}
}



fun main(args: Array<String>) {
	runApplication<Aoc2019Application>(*args)
}
