package compiler;

import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import static compiler.SimpleLexer.*;

public class SimpleParser {

    private final List<LexToken> tokens;
    private int currentIndex = 0;
    private final Set<String> declaredVariables = new HashSet<>();
    private int tempCounter = 0;
    private List<String> threeAddressCode = new ArrayList<>();

    private String newTemp() {
        return "t" + (tempCounter++);
    }

    public SimpleParser(List<LexToken> tokens) {
        this.tokens = tokens;
    }

    // Entry Point
    public void parse() {
        while (!isAtEnd()) {
            parseStatement();
        }

        System.out.println("✅ Syntax Analysis: Passed.");

        // Print generated 3-address code (if you're using code generation)
        if (!threeAddressCode.isEmpty()) {
            System.out.println("Generated 3-Address Code:");
            for (String line : threeAddressCode) {
                System.out.println(line);
            }
        }

        // ✅ Print the Symbol Table (Declared Variables)
        System.out.println("Declared Variables (Symbol Table):");
        for (String var : declaredVariables) {
            System.out.println("- " + var);
        }
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

    private String consumeIdentifier(String errorMessage) {
        if (check(LexType.IDENTIFIER)) {
            String name = peek().lexValue;
            advance();
            return name;
        } else {
            error(errorMessage);
            return null; // unreachable
        }
    }

    private String parseExpressionWithCode() {
        String left = parseTermWithCode();
        while (match(LexType.OPERATOR, "+", "-")) {
            String op = previous().lexValue;
            String right = parseTermWithCode();
            String temp = newTemp();
            threeAddressCode.add(temp + " = " + left + " " + op + " " + right);
            left = temp;
        }
        return left;
    }

    private String parseTermWithCode() {
        String left = parseFactorWithCode();
        while (match(LexType.OPERATOR, "*", "/")) {
            String op = previous().lexValue;
            String right = parseFactorWithCode();
            String temp = newTemp();
            threeAddressCode.add(temp + " = " + left + " " + op + " " + right);
            left = temp;
        }
        return left;
    }

    private String parseFactorWithCode() {
        if (match(LexType.NUMBER) || match(LexType.IDENTIFIER)) {
            return previous().lexValue;
        } else if (match(LexType.LEFT_PAREN)) {
            String expr = parseExpressionWithCode();
            consume(LexType.RIGHT_PAREN, "Expected ')' after expression.");
            return expr;
        } else {
            error("Expected number, variable, or expression.");
            return null;
        }
    }

    private void parseDeclaration() {
        String varName = consumeIdentifier("Expected variable name after 'int'.");
        if (declaredVariables.contains(varName)) {
            error("Variable '" + varName + "' already declared.");
        }
        declaredVariables.add(varName);
        consume(LexType.SEMICOLON, "Expected ';' after declaration.");
    }

    private void parseAssignment() {
        String varName = consumeIdentifier("Expected variable name.");
        if (!declaredVariables.contains(varName)) {
            error("Variable '" + varName + "' not declared.");
        }
        consume(LexType.ASSIGN_OP, "Expected '=' in assignment.");
        String result = parseExpressionWithCode();
        threeAddressCode.add(varName + " = " + result);
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
        if (check(LexType.IDENTIFIER)) {
            String varName = peek().lexValue;
            if (!declaredVariables.contains(varName)) {
                error("Semantic Error: Variable '" + varName + "' not declared.");
            }
        }
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
