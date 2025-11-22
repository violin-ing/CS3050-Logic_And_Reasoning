import java.util.List;
import java.util.ArrayList;
import java.util.regex.*;


class Lexer {
    private List<Token> tokens = new ArrayList<>();
    private int current = 0;

    public Lexer(String input) {
        // 1. (\|-) -> Matches turnstile |- exactly
        // 2. ([a-zA-Z][a-zA-Z0-9']*) -> Matches identifiers (inc z0, x', forall)
        // 3. (-[a-zA-Z][a-zA-Z0-9']*) -> Matches rule names starting with dash like -premiss
        // 4. (&-i|&-e[12]|v-i[12]|v-e|v-mtp|>-i|>-e|>-mt|~-i|~-e|0-e|A-i|A-e|E-i|E-e|id-i|id-e) -> ND Rules [cite: 86, 89]
        // 5. (\d+) -> Numbers
        // 6. (0) -> Falsity
        // 7. (.) -> Single char symbols
        
        String regex = "(\\|-)|(0)|(&-[ie][12]?|v-[ie][12]?|v-mtp|>-([ie]|mt)|~-[ie]|0-e|[AE]-[ie]|id-[ie])|(-[a-zA-Z]+)|([a-zA-Z][a-zA-Z0-9']*)|(\\d+)|(\\S)";
        
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        while (matcher.find()) {
            if (matcher.group(1) != null) tokens.add(new Token(Token.Type.TURNSTILE, matcher.group(1)));
            else if (matcher.group(2) != null) tokens.add(new Token(Token.Type.FALSITY, matcher.group(2)));
            else if (matcher.group(3) != null) tokens.add(new Token(Token.Type.IDENTIFIER, matcher.group(3))); // ND Rules
            else if (matcher.group(4) != null) tokens.add(new Token(Token.Type.IDENTIFIER, matcher.group(4))); // -premiss
            else if (matcher.group(5) != null) tokens.add(new Token(Token.Type.IDENTIFIER, matcher.group(5))); // Vars/Preds
            else if (matcher.group(6) != null) tokens.add(new Token(Token.Type.NUMBER, matcher.group(6)));
            else if (matcher.group(7) != null) {
                String s = matcher.group(7);
                switch (s) {
                    case "(" -> tokens.add(new Token(Token.Type.LPAREN, s));
                    case ")" -> tokens.add(new Token(Token.Type.RPAREN, s));
                    case "," -> tokens.add(new Token(Token.Type.COMMA, s));
                    case "." -> tokens.add(new Token(Token.Type.DOT, s));
                    case ":" -> tokens.add(new Token(Token.Type.COLON, s));
                    case "|" -> tokens.add(new Token(Token.Type.PIPE, s));
                    case "[" -> tokens.add(new Token(Token.Type.LBRACKET, s));
                    case "]" -> tokens.add(new Token(Token.Type.RBRACKET, s));
                    case "/" -> tokens.add(new Token(Token.Type.SLASH, s));
                    case "-" -> tokens.add(new Token(Token.Type.DASH, s));
                    // Standalone dash
                    case "&", "v", ">", "~" -> tokens.add(new Token(Token.Type.OPERATOR, s));
                    default -> {
                    }
                }
                // Ignore whitespace/unknowns
                            }
        }
    }

    public Token peek() { return current < tokens.size() ? tokens.get(current) : null; }
    public Token next() { return current < tokens.size() ? tokens.get(current++) : null; }
    public boolean match(Token.Type type) {
        if (peek() != null && peek().type == type) {
            next(); return true;
        }
        return false;
    }
    public void consume(Token.Type type) {
        if (!match(type)) throw new RuntimeException("Expected " + type + " but found " + peek());
    }
}