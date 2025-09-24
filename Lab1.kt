// Lexical Scanner 
/** Implement a complete lexical scanner that can take source code written in a simple programming language and convert it into a stream of tokens. 
Your scanner will need to handle:

- Single-character tokens (parentheses, operators, punctuation)     --> Done
- Multi-character operators (like == and <=)
- String literals enclosed in quotes
- Numeric literals (both integers and decimals)
- Identifiers and keywords
- Comments (which should be ignored)
- Whitespace handling and error reporting                          --> Done

The expected, testable output for this laboratory activity is a Read-eval-print loop (REPL) in the command line. 
This has your scanner implementation at its heart. 
The scanner must also provide some form of error handling for invalid or malformed lexemes. 

you’re not allowed to use regular expressions in your implementation.

Steps:
1. Define token types using an enum
2. Create a token class
3. Create the scanner
4. Create the REPL for interactive testing
**/

// Define all possible token types our language understands
enum class TokenType {
    // Literals
    IDENTIFIER, STRING, NUMBER,

    // Keywords
    VAR, FUN, VAL, CLASS, IF, ELSE, FOR, WHILE, RETURN, PRIVATE, PUBLIC, AND, OR, NOT, TRUE, FALSE,

    // Operators (single and multi-character)
    EQUAL_EQUAL, LESS, GREATER, MINUS_MINUS, PLUS_PLUS, LESS_EQUAL, GREATER_EQUAL, BANG_EQUAL, BANG, EQUAL,

    // Symbols
    LEFT_PAREN, RIGHT_PAREN, PLUS, MINUS, LEFT_BRACE, RIGHT_BRACE, COMMA, SEMICOLON, DOT, SLASH, STAR,

    // Special markers
    EOF, ERROR
}

//token holds info about each recognized piece of source code
data class Token(
    val type: TokenType,       //category/type of token (from enum above)
    val lexeme: String,        //the actual text matched from the source
    val literal: Any?,         //literal value (e.g. number value, string contents), null if none
    val line: Int,             //line number in source (for error reporting)
)

//scanner takes raw source code & breaks it into tokens
class Scanner(private val source: String) {
    private var start = 0      //index where the current token begins
    private var current = 0    //index of the character we are currently scanning
    private var line = 1       //line counter (helps w error reporting)
    private val tokens = mutableListOf<Token>()  // dynamic list of tokens found

    //scan the entire source into tokens
    fun scanTokens(): List<Token> {
        while (!isAtEnd()) {       //loop until all characters are consumed
            start = current        //mark beginning of the next token
            scanToken()            //recognize the next token
        }
        tokens.add(Token(TokenType.EOF, "", null, line)) //add special EOF token at the end
        return tokens
    }

    //to check if we've read all characters
    private fun isAtEnd(): Boolean {
        return current >= source.length
    }

    //to consume one character and move forward
    private fun advance(): Char {
        return source[current++]
    }

    //add token w/o literal value
    private fun addToken(type: TokenType) {
        addToken(type, null)
    }

    //add token w optional literal value
    private fun addToken(type: TokenType, literal: Any?) {
        val text = source.substring(start, current) // extract substring for the lexeme
        tokens.add(Token(type, text, literal, line)) // push into token list
    }
    
    //determine what token the curr chara belongs to
    private fun scanToken() {
        val c = advance()       //to get the next character

        when (c) {
            '(' -> addToken(TokenType.LEFT_PAREN)
            ')' -> addToken(TokenType.RIGHT_PAREN)
            '{' -> addToken(TokenType.LEFT_BRACE)
            '}' -> addToken(TokenType.RIGHT_BRACE)
            ',' -> addToken(TokenType.COMMA)
            '.' -> addToken(TokenType.DOT)
            '-' -> addToken(TokenType.MINUS)
            '+' -> addToken(TokenType.PLUS)
            ';' -> addToken(TokenType.SEMICOLON)
            '*' -> addToken(TokenType.STAR)

            //Whitespace ignored but newlines increase line count
            ' ', '\r', '\t' -> {}
            '\n' -> line++

            else -> {
                //if unrecognized character, we report it
                println("Unexpected character: $c on line $line")
            }
        }
    }
}

//REPL: lets the user interactively type code & see the tokens generated
fun main() {
    println("This is the ktlox programming language. Type in your prompt below and see the tokens generated.")    

    while (true) {
        print("> ")                          //as indicator to insert prompt after this symbol
        val line = readLine() ?: break       //to read input (exit if null / Ctrl+D)
        val scanner = Scanner(line)          //to create scanner for that line
        val tokens = scanner.scanTokens()    //to scan tokens

        for (token in tokens) {
            println(token)                   //to print tokens for debugging
        }
    }
}