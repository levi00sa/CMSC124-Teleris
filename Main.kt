fun main() {                                                                              // REPL: lets the user interactively type code & see the tokens generated
    println("Type in your prompt below and see the tokens generated.")    

    while (true) {
        print("> ")                                                                                  // as indicator to insert prompt after this symbol
        val line = readLine() ?: break                                                               // to read input (exit if null / Ctrl+D)
        val scanner = Scanner(line)                                                                  // to create scanner for that line
        val tokens = scanner.scanTokens()                                                            // to scan tokens

        for (token in tokens) {
            println(token)                                                                           // to print tokens for debugging
        }
    }
}
