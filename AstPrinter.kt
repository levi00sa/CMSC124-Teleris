// AstPrinter.kt
object AstPrinter {
    fun print(expr: Expr): String {
        return when (expr) {
            is Expr.Literal -> literalToString(expr.value)
            is Expr.Grouping -> "(group ${print(expr.expression)})"
            is Expr.Unary -> "(${expr.operator.lexeme} ${print(expr.right)})"
            is Expr.Binary -> "(${expr.operator.lexeme} ${print(expr.left)} ${print(expr.right)})"
            is Expr.Block -> {
                // Use AstPrinter.print inside lambda so we return strings (not Unit).
                val statements = expr.statements.joinToString(" ") { AstPrinter.print(it) }
                "(block $statements)"
            }
        }
    }

    private fun literalToString(value: Any?): String {
        return when (value) {
            null -> "null"
            is Double -> value.toString()
            else -> value.toString()
        }
    }
}
