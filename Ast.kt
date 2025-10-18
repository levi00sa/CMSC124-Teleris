// Ast.kt
// Defines the AST node types used by the parser and printer.

sealed class Expr {
    data class Literal(val value: Any?) : Expr()
    data class Variable(val name: Token) : Expr()
    data class Grouping(val expression: Expr) : Expr()
    data class Unary(val operator: Token, val right: Expr) : Expr()
    data class Binary(val left: Expr, val operator: Token, val right: Expr) : Expr()
    data class Block(val statements: List<Expr>) : Expr()
}

//sealed class Expr with data classes for the node kinds 
    //your Parser builds.
// Literal.value is Any? because scanner stores 
    //Double, String, Boolean or null.