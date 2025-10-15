//print readable parenthesized representation of AST
//this prints the AST in the required parenthesized format

// AstPrinter.kt
object AstPrinter {
    fun print(expr: Expr): String {
        return when (expr) {
            is Expr.Literal -> literalToString(expr.value)
            is Expr.Grouping -> "(group ${print(expr.expression)})"
            is Expr.Unary -> "(${expr.operator.lexeme} ${print(expr.right)})"
            is Expr.Binary -> "(${expr.operator.lexeme} ${print(expr.left)} ${print(expr.right)})"
            is Expr.Block -> {
                val statements = expr.statements.joinToString(" ") { print(it) }
                "(block $statements)"
            }
            //else -> "<?>"
        }
    }

    private fun literalToString(value: Any?): String {
        return when (value) {
            null -> "nil"
            is Double -> value.toString()
            else -> value.toString()
        }
    }
}
