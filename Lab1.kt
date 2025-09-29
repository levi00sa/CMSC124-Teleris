enum class TokenType {
    IDENTIFIER, STRING, NUMBER,  // literals, keywords, operators, symbols, special markers:
    VAR, FUN, VAL, CLASS, IF, ELSE, FOR, WHILE, RETURN, PRIVATE, PUBLIC, AND, OR, NOT, TRUE, FALSE,
    EQUAL_EQUAL, LESS, GREATER, MINUS_MINUS, PLUS_PLUS, LESS_EQUAL, GREATER_EQUAL, BANG_EQUAL, BANG, EQUAL,

    // Symbols
    LEFT_PAREN, RIGHT_PAREN, PLUS, MINUS, LEFT_BRACE, RIGHT_BRACE, COMMA, SEMICOLON, DOT, SLASH, STAR, DOUBLE_QUOTE,

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
    private var start = 0                 // index where the current token begins
    private var current = 0               // index of the character being scanned
    private var line = 1                  // line counter (for error reporting)
    private val tokens = mutableListOf<Token>()  // dynamic list of tokens found

    // keyword table (if word matches here, it's a keyword; otherwise identifier)
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
    )

    // scan the entire source into tokens
    fun scanTokens(): List<Token> {
        while (!isAtEnd()) {
            start = current    // mark beginning of next token
            scanToken()        // recognize next token
        }
        tokens.add(Token(TokenType.EOF, "", null, line)) // add EOF marker
        return tokens
    }

    // check if done reading
    private fun isAtEnd(): Boolean = current >= source.length

    // consume next character
    private fun advance(): Char = source[current++]

    // add token w/o literal
    private fun addToken(type: TokenType) = addToken(type, null)

    // add token w/ optional literal
    private fun addToken(type: TokenType, literal: Any?) {
        val text = source.substring(start, current) // extract substring for lexeme
        tokens.add(Token(type, text, literal, line))
    }

    // recognize token for current character
    private fun scanToken() {
        val c = advance()

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

            // multi-character operators
            '=' -> {
                if (match('=')) addToken(TokenType.EQUAL_EQUAL)
                else addToken(TokenType.EQUAL)
            }
            '!' -> {
                if (match('=')) addToken(TokenType.BANG_EQUAL)
                else addToken(TokenType.BANG)
            }
            '<' -> {
                if (match('=')) addToken(TokenType.LESS_EQUAL)
                else addToken(TokenType.LESS)
            }
            '>' -> {
                if (match('=')) addToken(TokenType.GREATER_EQUAL)
                else addToken(TokenType.GREATER)
            }

            '"' -> string()

            '/' -> {
                when {
                    match('/') -> {
                        // single-line comment, skip until newline
                        while (peek() != '\n' && !isAtEnd()) advance()
                    }
                    match('*') -> {
                        // block comment: skip until */
                        while (!(peek() == '*' && peekNext() == '/') && !isAtEnd()) {
                            if (peek() == '\n') line++
                            advance()
                        }
                        if (!isAtEnd()) {
                            advance() // consume *
                            advance() // consume /
                        } else {
                            println("Unterminated block comment at $line")
                        }
                    }
                    else -> addToken(TokenType.SLASH)
                }
            }

            // whitespace ignored (except newline increments line count)
            ' ', '\r', '\t' -> {}
            '\n' -> line++

            else -> {
                if (c.isDigit()) {
                    number()
                } else if (c.isLetter() || c == '_') {
                    identifier()
                } else {
                    println("Unexpected character: $c on line $line")
                }
            }
        }
    }

    private fun identifier() {
        while (peek().isLetterOrDigit() || peek() == '_') advance()
        val text = source.substring(start, current)
        addToken(keywords[text] ?: TokenType.IDENTIFIER)
    }

    private fun number() {
        while (peek().isDigit()) advance()
        if (peek() == '.' && peekNext().isDigit()) {
            advance() // consume '.'
            while (peek().isDigit()) advance()
        }
        addToken(TokenType.NUMBER, source.substring(start, current).toDouble())
    }

    private fun peek(): Char {
        if (isAtEnd()) return '\u0000'
        return source[current]
    }

    private fun string() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') line++
            advance()
        }
        if (isAtEnd()) {
            println("Unterminated string at $line")
            return
        }
        advance() // closing "
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

    private fun peekNext(): Char {
        if (current + 1 >= source.length) return '\u0000'
        return source[current + 1]
    }
}

// REPL: lets the user interactively type code & see the tokens generated
fun main() {
    println("Type in your prompt below and see the tokens generated.")

    while (true) {
        print("> ")
        val line = readLine() ?: break
        val scanner = Scanner(line)
        val tokens = scanner.scanTokens()

        for (token in tokens) {
            println(token)
        }
    }
}
