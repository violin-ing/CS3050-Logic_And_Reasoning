public class Token {
    // Splits a line into individual symbols.
    // E.g. (A v B) is separated into ['(', 'A', 'v', 'B', ')'].

    public enum Type {
        LPAREN, RPAREN, COMMA, DOT, COLON, 
        TURNSTILE, // |-
        PIPE,      // |
        IDENTIFIER, // Strings like P, z0, forall, -assumption
        OPERATOR,   // &, v, >, ~
        NUMBER,     // 1, 2, 3
        FALSITY,    // 0
        LBRACKET, RBRACKET, SLASH, DASH // For annotations [z0/x]
    }
    Type type;
    String text;

    // Constructor
    Token(Type type, String text) {
        this.type = type; this.text = text; 
    }

    public String to_string() {
        return text;
    }
}