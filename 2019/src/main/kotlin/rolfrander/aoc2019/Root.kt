package rolfrander.aoc2019

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.context.ApplicationContext
import org.springframework.beans.factory.annotation.Autowired
import kotlin.reflect.KAnnotatedElement

@RestController
@RequestMapping("/")
class Root {

    @Autowired
    lateinit var context: ApplicationContext

    fun link(url: String, name: String): String {
        return "<a href=\"$url\">$name</a>"
    }

    @GetMapping("/")
    fun index(): String {
        val ret = StringBuilder()
        ret.append("<html><head><title>AOC 2019</title></head><body>\n")
        ret.append("<ul>\n")
        for((name,ctrl) in context.getBeansWithAnnotation(RestController::class.java)) {
            
            ret.append("<li>")
            val controllerClass = ctrl::class
            val requestMapping = (controllerClass.annotations.filter { it is RequestMapping }).firstOrNull()
            if(requestMapping != null && requestMapping is RequestMapping) {
                ret.append(link(requestMapping.value.firstOrNull() ?: "", name))
            } else {
                ret.append(name)
            }
            ret.append("</li>\n")
        }
        ret.append("</ul>\n")
        ret.append("</body></html>")
        return ret.toString()
    }
}