// Lexical Scanner
// Converts Teleris source code into a stream of tokens.
// Handles keywords, identifiers, numbers, strings, operators, symbols, comments, and whitespace.

enum class TokenType {
    IDENTIFIER, STRING, NUMBER,  // literals, keywords, operators, symbols, special markers:

    // Keywords
    VAR, FUN, VAL, CLASS, IF, ELSE, FOR, WHILE, RETURN, PRIVATE, PUBLIC, AND, OR, NOT, TRUE, FALSE, NULL,

    // Operators
    EQUAL_EQUAL, LESS, GREATER, MINUS_MINUS, PLUS_PLUS,
    LESS_EQUAL, GREATER_EQUAL, BANG_EQUAL, BANG, EQUAL,

    // Symbols
    LEFT_PAREN, RIGHT_PAREN, LEFT_BRACE, RIGHT_BRACE,
    LEFT_BRACKET, RIGHT_BRACKET,
    COMMA, SEMICOLON, DOT, SLASH, STAR, DOUBLE_QUOTE, COLON,

    // Special markers
    EOF, ERROR
}

// token holds info about each recognized piece of source code
data class Token(
    val type: TokenType,       // what category it belongs to
    val lexeme: String,        // the exact text from source
    val literal: Any?,         // actual value (if any, e.g., number value)
    val line: Int              // line number in source (for error reporting)
)

// scanner takes raw source code & breaks it into tokens
class Scanner(private val source: String) {
    private var start = 0
    private var current = 0
    private var line = 1
    private val tokens = mutableListOf<Token>()

    // keyword table
    private val keywords = mapOf(
        "var" to TokenType.VAR,
        "fun" to TokenType.FUN,
        "if" to TokenType.IF,
        "else" to TokenType.ELSE,
        "while" to TokenType.WHILE,
        "for" to TokenType.FOR,
        "return" to TokenType.RETURN,
        "true" to TokenType.TRUE,
        "false" to TokenType.FALSE,
        "null" to TokenType.NULL,
    )

    // scan the entire source into tokens
    fun scanTokens(): List<Token> {
        while (!isAtEnd()) {
            start = current
            scanToken()
        }
        tokens.add(Token(TokenType.EOF, "", null, line))
        return tokens
    }

    private fun isAtEnd(): Boolean = current >= source.length
    private fun advance(): Char = source[current++]
    private fun addToken(type: TokenType) = addToken(type, null)
    private fun addToken(type: TokenType, literal: Any?) {
        val text = source.substring(start, current)
        tokens.add(Token(type, text, literal, line))
    }

    private fun scanToken() {
        val c = advance()
        when (c) {
            '(' -> addToken(TokenType.LEFT_PAREN)
            ')' -> addToken(TokenType.RIGHT_PAREN)
            '{' -> addToken(TokenType.LEFT_BRACE)
            '}' -> addToken(TokenType.RIGHT_BRACE)
            '[' -> addToken(TokenType.LEFT_BRACKET)
            ']' -> addToken(TokenType.RIGHT_BRACKET)
            ',' -> addToken(TokenType.COMMA)
            '.' -> addToken(TokenType.DOT)
            '-' -> addToken(TokenType.MINUS)
            '+' -> addToken(TokenType.PLUS)
            ';' -> addToken(TokenType.SEMICOLON)
            '*' -> addToken(TokenType.STAR)

            // multi-character operators
            '=' -> if (match('=')) addToken(TokenType.EQUAL_EQUAL) else addToken(TokenType.EQUAL)
            '!' -> if (match('=')) addToken(TokenType.BANG_EQUAL) else addToken(TokenType.BANG)
            '<' -> if (match('=')) addToken(TokenType.LESS_EQUAL) else addToken(TokenType.LESS)
            '>' -> if (match('=')) addToken(TokenType.GREATER_EQUAL) else addToken(TokenType.GREATER)

            ':' -> addToken(TokenType.COLON)
            '"' -> string()

            '/' -> {
                when {
                    match('/') -> {
                        while (peek() != '\n' && !isAtEnd()) advance()
                    }
                    match('*') -> {
                        while (!(peek() == '*' && peekNext() == '/') && !isAtEnd()) {
                            if (peek() == '\n') line++
                            advance()
                        }
                        if (!isAtEnd()) {
                            advance() // consume *
                            advance() // consume /
                        } else {
                            println("Unterminated block comment at line $line")
                        }
                    }
                    else -> addToken(TokenType.SLASH)
                }
            }

            ' ', '\r', '\t' -> {}
            '\n' -> line++

            else -> {
                if (c.isDigit()) {
                    number()
                } else if (c.isLetter() || c == '_' || c == '$') {
                    identifier()
                } else {
                    println("Unexpected character: $c on line $line")
                }
            }
        }
    }

    private fun identifier() {
        while (peek().isLetterOrDigit() || peek() == '_' || peek() == '$') advance()
        val text = source.substring(start, current)
        addToken(keywords[text] ?: TokenType.IDENTIFIER)
    }

    private fun number() {
        while (peek().isDigit()) advance()
        if (peek() == '.' && peekNext().isDigit()) {
            advance()
            while (peek().isDigit()) advance()
        }
        addToken(TokenType.NUMBER, source.substring(start, current).toDouble())
    }

    private fun string() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') line++
            advance()
        }
        if (isAtEnd()) {
            println("Unterminated string at line $line")
            return
        }
        advance() // closing quote
        val value = source.substring(start + 1, current - 1)
        val lexeme = source.substring(start, current)
        tokens.add(Token(TokenType.STRING, lexeme, value, line))
    }

    private fun match(expected: Char): Boolean {
        if (isAtEnd()) return false
        if (source[current] != expected) return false
        current++
        return true
    }

    private fun peek(): Char = if (isAtEnd()) '\u0000' else source[current]
    private fun peekNext(): Char = if (current + 1 >= source.length) '\u0000' else source[current + 1]
}

// REPL for interactive testing
fun main() {
    println("Type in your prompt below and see the tokens generated.")
    while (true) {
        print("> ")
        val line = readLine() ?: break
        val scanner = Scanner(line)
        val tokens = scanner.scanTokens()
        for (token in tokens) println(token)
    }
}
