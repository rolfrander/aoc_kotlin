package rolfrander.aoc2019

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping

@RestController
@RequestMapping("/day01")
class Day01 {
    @GetMapping("/hello", "/hello/")
    fun hello(@RequestParam("name") name: String) = "Hello, $name!"

    @GetMapping("", "/")
    fun index() = "Day 2019/1"


}