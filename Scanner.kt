class Scanner(private val source: String) {                                                      // scanner takes raw source code & breaks it into tokens
    private var start = 0                                                                           // index where the current token begins
    private var current = 0                                                                         // index of the character we are currently scanning
    private var line = 1                                                                            // line counter (helps w error reporting)
    private val tokens = mutableListOf<Token>()                                                     // dynamic list of tokens found

    private val keywords = mapOf(
        "set" to TokenType.SET,
        "fn" to TokenType.FN,
        "if" to TokenType.IF,
        "else" to TokenType.ELSE,
        "while" to TokenType.WHILE,
        "for" to TokenType.FOR,
        "return" to TokenType.RETURN,
        "null" to TokenType.NULL,
    )

    fun scanTokens(): List<Token> {                                                                 // scan the entire source into tokens
        while (!isAtEnd()) {                                                                       // loop until all characters are consumed
            start = current                                                                        // mark beginning of the next token
            scanToken()                                                                            // recognize the next token
        }
        tokens.add(Token(TokenType.EOF, "", null, line))                                           // add special EOF token at the end
        return tokens
    }

    private fun isAtEnd(): Boolean {                                                                 // to check if we've read all characters
        return current >= source.length
    }

    private fun advance(): Char {                                                                     // to consume one character and move forward
        return source[current++]
    }

    private fun addToken(type: TokenType) {                                                          // add token w/o literal value
        addToken(type, null)
    }

    private fun addToken(type: TokenType, literal: Any?) {                                          // add token w optional literal value
        val text = source.substring(start, current)                                                 // extract substring for the lexeme
        tokens.add(Token(type, text, literal, line))                                               // push into token list
    }
    
    private fun scanToken() {                                                                       // determine what token the curr chara belongs to
        val c = advance()                                                                           // to get the next character using advance

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
            '=' -> addToken(TokenType.EQUAL)
            ':' -> addToken(TokenType.COLON)
            '"' -> string()
            '/' -> {
                if (match ('/')) { 
                    while (peek() != '\n' && !isAtEnd()) {
                        advance()
                    }                                          
                } else if (match('-')) {
                    while (true) {
                        if (isAtEnd()){
                            println("Error with $c at line $line")
                            return
                        }
                        if (peek() == '-' && peekNext() == '/') {
                            advance() //consume '-'
                            advance() //consume '/'
                            break     //skip comment
                        }
                        if (peek() == '\n') line++
                        advance()
                    }
                } else {
                    addToken(TokenType.SLASH)
                } 
            }
            
            ' ', '\r', '\t' -> {}                                                                 // Whitespace ignored but newlines increase line count
            '\n' -> line++

            else -> {                                                                               // if unrecognized character, we report it
                if (c.isDigit()) {
                    number()
                } else if (c.isLetter() || c == '_' || c == '$') {
                    identifier()
                } else {
                    println("Error with $c at line $line")
                }
            }
        }
    }
    
    private fun identifier() {
        while (peek().isLetter() || peek() == '_' || peek() == '$') 
            advance()
        val text = source.substring(start, current)
        when (text) {
            "true" -> addToken(TokenType.TRUE, true)
            "false" -> addToken(TokenType.FALSE, false)
            else -> addToken(keywords[text] ?: TokenType.IDENTIFIER)
        }
    }

    private fun number() {
        while (peek().isDigit()) advance()
        if (peek() == '.' && peekNext().isDigit()) {                                               // Handling float
            advance()                                                                               // Consume the '.'
            while (peek().isDigit()) advance()
        }

        addToken(TokenType.NUMBER, source.substring(start, current).toDouble())
    }

    private fun peek(): Char {
        if (isAtEnd()) return '\u0000'
        return source[current]
    }

    private fun string() {
        while (peek() != '"' && !isAtEnd()){
            if (peek() == '\n') line++
            advance()
        }
        if (isAtEnd()) {
            println("Error string at line $line")
            return
        }
        advance()

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
