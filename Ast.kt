//AST node types for the parser to build an AST since Kotlin does not support sealed interfaces
//AST is actually an abstract class so that we can have a common supertype for all nodes
//what happens here is that we have a sealed class Expression (Expr) with all the nodes the parser needs
// Ast.kt
sealed class Expr {
    data class Literal(val value: Any?) : Expr()
    data class Variable(val name: Token) : Expr()
    data class Grouping(val expression: Expr) : Expr()
    data class Unary(val operator: Token, val right: Expr) : Expr()
    data class Binary(val left: Expr, val operator: Token, val right: Expr) : Expr()
    data class Block(val statements: List<Expr>) : Expr()
}
