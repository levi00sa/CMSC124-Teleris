// Lexical Scanner 
/** Implement a complete lexical scanner that can take source code written in a simple programming language and convert it into a stream of tokens. Your scanner will need to handle:

Single-character tokens (parentheses, operators, punctuation)
Multi-character operators (like == and <=)
String literals enclosed in quotes
Numeric literals (both integers and decimals)
Identifiers and keywords
Comments (which should be ignored)
Whitespace handling and error reporting

The expected, testable output for this laboratory activity is a Read-eval-print loop (REPL) in the commandline. 
This has your scanner implementation at its heart. 
The scanner must also provide some form of error handling for invalid or malformed lexemes. 

you're not allowed to use regular expressions in your implementation.

-> There are different token types we need to categorize (using enum to define token type contants)
-> Create a token class
-> Create the scanner
-> Then the REPL 
**/

enum class TokenType {
    //Literals
    IDENTIFIER, STRING, NUMBER,

    //Keywords
    VAR, FUN, VAL, CLASS, IF, ELSE, FOR, WHILE, RETURN, PRIVATE, PUBLIC, AND, OR, NOT, TRUE, FALSE,

    //Operators
    EQUAL_EQUAL, LESS, GREATER, MINUS_MINUS, PLUS_PLUS, LESS_EQUAL, GREATER_EQUAL, BANG_EQUAL, BANG, EQUAL,

    LEFT_PAREN, RIGHT_PAREN, PLUS, MINUS, LEFT_BRACE, RIGHT_BRACE, COMMA, SEMICOLON, DOT, SLASH, STAR,

    EOF, ERROR
}

data class Token(
    val type: TokenType,
    val lexeme: String,
    val literal: Any?,
    val line: Int,
)

class Scanner(private val source: String){
//freak idk
}

fun main(){
    println("This is the ktlox programming language")
    //MORE CODE
}
