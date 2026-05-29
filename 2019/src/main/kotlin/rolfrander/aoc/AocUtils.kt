package rolfrander.aoc

import kotlin.text.*

fun CharSequence.splitLines() = this.trimEnd().lineSequence()

fun CharSequence.parseInts(): Sequence<Int> {
    val re = Regex("-?[0-9]+")
    return re.findAll(this).map { it.value.toInt()}
}

fun CharSequence.parseLongs(): Sequence<Long> {
    val re = Regex("-?[0-9]+")
    return re.findAll(this).map { it.value.toLong()}
}
