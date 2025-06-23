package compiler;

import java.util.List;
import static compiler.SimpleLexer.*;

public class SimpleParser {

    private final List<LexToken> tokens;
    private int currentIndex = 0;

    public SimpleParser(List<LexToken> tokens) {
        this.tokens = tokens;
    }

    // Entry Point
    public void parse() {
        while (!isAtEnd()) {
            parseStatement();
        }
        System.out.println("✅ Syntax Analysis: Passed.");
    }


    // ========== STATEMENTS ==========

    private void parseStatement() {
        if (match(LexType.KEYWORD, "int")) {
            parseDeclaration();
        } else if (check(LexType.IDENTIFIER)) {
            parseAssignment();
        } else if (match(LexType.KEYWORD, "if")) {
            parseIfStatement();
        } else if (match(LexType.KEYWORD, "while")) {
            parseWhileStatement();
        } else if (match(LexType.KEYWORD, "print")) {
            parsePrintStatement();
        } else {
            error("Expected a valid statement.");
        }
    }

    private void parseDeclaration() {
        consume(LexType.IDENTIFIER, "Expected variable name after 'int'.");
        consume(LexType.SEMICOLON, "Expected ';' after declaration.");
    }

    private void parseAssignment() {
        consume(LexType.IDENTIFIER, "Expected variable name.");
        consume(LexType.ASSIGN_OP, "Expected '=' in assignment.");
        parseExpression();
        consume(LexType.SEMICOLON, "Expected ';' after assignment.");
    }

    private void parseIfStatement() {
        consume(LexType.LEFT_PAREN, "Expected '(' after 'if'.");
        parseExpression();
        consume(LexType.RIGHT_PAREN, "Expected ')' after condition.");
        parseBlock();
        if (match(LexType.KEYWORD, "else")) {
            parseBlock();
        }
    }

    private void parseWhileStatement() {
        consume(LexType.LEFT_PAREN, "Expected '(' after 'while'.");
        parseExpression();
        consume(LexType.RIGHT_PAREN, "Expected ')' after condition.");
        parseBlock();
    }

    private void parsePrintStatement() {
        consume(LexType.LEFT_PAREN, "Expected '(' after 'print'.");
        parseExpression();
        consume(LexType.RIGHT_PAREN, "Expected ')' after expression.");
        consume(LexType.SEMICOLON, "Expected ';' after print statement.");
    }

    private void parseBlock() {
        consume(LexType.LEFT_BRACE, "Expected '{' to start block.");
        while (!check(LexType.RIGHT_BRACE) && !isAtEnd()) {
            parseStatement();
        }
        consume(LexType.RIGHT_BRACE, "Expected '}' to close block.");
    }

    // ========== EXPRESSIONS ==========

    private void parseExpression() {
        parseArithmetic();
        if (match(LexType.COMPARATOR)) {
            parseArithmetic();
        }
    }

    private void parseArithmetic() {
        parseTerm();
        while (match(LexType.OPERATOR, "+", "-")) {
            parseTerm();
        }
    }

    private void parseTerm() {
        parseFactor();
        while (match(LexType.OPERATOR, "*", "/")) {
            parseFactor();
        }
    }

    private void parseFactor() {
        if (match(LexType.IDENTIFIER) || match(LexType.NUMBER)) {
            return;
        } else if (match(LexType.LEFT_PAREN)) {
            parseExpression();
            consume(LexType.RIGHT_PAREN, "Expected ')' after expression.");
        } else {
            error("Expected number, variable, or expression.");
        }
    }

    // ========== UTILITY FUNCTIONS ==========

    private boolean match(LexType type, String... values) {
        if (check(type)) {
            String lexeme = peek().lexValue;
            for (String v : values) {
                if (lexeme.equals(v)) {
                    advance();
                    return true;
                }
            }
        }
        return false;
    }

    private boolean match(LexType type) {
        if (check(type)) {
            advance();
            return true;
        }
        return false;
    }

    private void consume(LexType type, String errorMessage) {
        if (check(type)) {
            advance();
        } else {
            error(errorMessage);
        }
    }

    private boolean check(LexType type) {
        return !isAtEnd() && peek().lexType == type;
    }

    private LexToken peek() {
        return tokens.get(currentIndex);
    }

    private LexToken advance() {
        if (!isAtEnd()) currentIndex++;
        return previous();
    }

    private LexToken previous() {
        return tokens.get(currentIndex - 1);
    }

    private boolean isAtEnd() {
        return currentIndex >= tokens.size();
    }

    private void error(String message) {
        System.err.println("❌ Syntax Error: " + message + " at token: " + (isAtEnd() ? "EOF" : peek()));
        System.exit(1);
    }
}
