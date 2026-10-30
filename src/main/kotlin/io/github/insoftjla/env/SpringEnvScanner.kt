package io.github.insoftjla.env

object SpringEnvScanner {

    private val pattern = Regex("""\$\{([A-Za-z_][A-Za-z0-9_]*)(:[^}]*)?}""")

    fun scan(content: String): Set<String> =
        pattern.findAll(content)
            .filter { it.groupValues[2].isEmpty() }
            .map { it.groupValues[1] }
            .toSet()
}