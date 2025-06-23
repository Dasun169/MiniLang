package compiler;

import java.io.*;
import java.util.*;
import java.util.regex.*;

public class SimpleLexer {

    // 1. Token Types
    public enum LexType {
        KEYWORD,        // Reserved words: int, if, else, while, print
        IDENTIFIER,     // Variable names
        NUMBER,         // Integer literals
        ASSIGN_OP,      // =
        SEMICOLON,      // ;
        OPERATOR,       // +, -, *, /
        COMPARATOR,     // <, >
        LEFT_PAREN,     // (
        RIGHT_PAREN,    // )
        LEFT_BRACE,     // {
        RIGHT_BRACE     // }
    }

    // 2. Token Representation
    public static class LexToken {
        LexType lexType;
        String lexValue;

        public LexToken(LexType lexType, String lexValue) {
            this.lexType = lexType;
            this.lexValue = lexValue;
        }

        @Override
        public String toString() {
            return "(" + lexType + ", " + lexValue + ")";
        }
    }

    // 3. Regular Expression Patterns
    private static final Map<LexType, String> PATTERNS = new LinkedHashMap<>();
    private static final Set<String> RESERVED_WORDS = Set.of("int", "if", "else", "while", "print");

    static {
        PATTERNS.put(LexType.KEYWORD,     "\\b(int|if|else|while|print)\\b");
        PATTERNS.put(LexType.IDENTIFIER,  "\\b[a-zA-Z_][a-zA-Z0-9_]*\\b");
        PATTERNS.put(LexType.NUMBER,      "\\b\\d+\\b");
        PATTERNS.put(LexType.ASSIGN_OP,   "=");
        PATTERNS.put(LexType.SEMICOLON,   ";");
        PATTERNS.put(LexType.OPERATOR,    "[+\\-*/]");
        PATTERNS.put(LexType.COMPARATOR,  "[<>]");
        PATTERNS.put(LexType.LEFT_PAREN,  "\\(");
        PATTERNS.put(LexType.RIGHT_PAREN, "\\)");
        PATTERNS.put(LexType.LEFT_BRACE,  "\\{");
        PATTERNS.put(LexType.RIGHT_BRACE, "\\}");
    }

    // 4. Lexical Analyzer Function
    public static List<LexToken> lexAnalyze(String sourceCode) {
        List<LexToken> lexTokens = new ArrayList<>();
        String combinedPattern = PATTERNS.values().stream()
                .reduce((p1, p2) -> p1 + "|" + p2)
                .orElseThrow(() -> new RuntimeException("No patterns defined"));

        Pattern pattern = Pattern.compile(combinedPattern);
        Matcher matcher = pattern.matcher(sourceCode);

        while (matcher.find()) {
            String matchedText = matcher.group();
            LexType matchedType = null;

            for (Map.Entry<LexType, String> entry : PATTERNS.entrySet()) {
                if (matchedText.matches(entry.getValue())) {
                    matchedType = entry.getKey();
                    break;
                }
            }

            // Convert identifiers to keywords if matched
            if (matchedType == LexType.IDENTIFIER && RESERVED_WORDS.contains(matchedText)) {
                matchedType = LexType.KEYWORD;
            }

            if (matchedType != null) {
                lexTokens.add(new LexToken(matchedType, matchedText));
            }
        }

        return lexTokens;
    }

    // 5. Main Method (Reads File and Prints Tokens)
    public static void main(String[] args) {
        String fileName = "src/compiler/input.minilang";
        StringBuilder sourceBuilder = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                sourceBuilder.append(line).append("\n");
            }

            // Lexical Analysis
            List<LexToken> tokens = lexAnalyze(sourceBuilder.toString());
            System.out.println("✅ Lexical Tokens:");
            for (LexToken token : tokens) {
                System.out.println(token);
            }

            // Syntax Analysis
            SimpleParser parser = new SimpleParser(tokens);
            parser.parse();

        } catch (FileNotFoundException e) {
            System.err.println("❌ Error: File not found - " + fileName);
        } catch (IOException e) {
            System.err.println("❌ Error reading file: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("❌ Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
