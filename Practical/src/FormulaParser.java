import java.util.ArrayList;
import java.util.List;


public class FormulaParser {
    private String input;
    private int pos;

    public FormulaParser(String input) {
        this.input = input;
        this.pos = 0;
    }
    
    public static Formula parse(String text) {
        FormulaParser parser = new FormulaParser(text.trim());
        return parser.parse_implication();
    }

    
    /**
     * Helper functions for String processing...
     */
    private String parse_identifier() {
        skip_whitespace();
        int start = pos;

        while (pos < input.length() && Character.isLetterOrDigit(input.charAt(pos))) {
            pos++;
        }

        return input.substring(start, pos);
    }

    private String parse_term() {
        return parse_identifier();
    }

    private void skip_whitespace(){
        while (pos < input.length() && Character.isWhitespace(input.charAt(pos))) {
            pos++;
        }
    }

    private boolean match(char c) {
        skip_whitespace();
        if (pos < input.length() && input.charAt(pos) == c) {
            pos++;
            return true;
        }
        return false;
    }

    private boolean peek(char c) {
        skip_whitespace();
        return pos < input.length() && input.charAt(pos) == c;
    }

    private boolean peek_string(String s) {
        skip_whitespace();
        return input.startsWith(s, pos);
    }
    
    private void consume(String s) {
        if (peek_string(s)) pos += s.length();
    }

    private void expect(char c) {
        if (!match(c)) throw new RuntimeException("Error: expected '" + c + "' at index " + pos);
    }


    /**
     * Recursive parsing levels from lowest precedence to highest:
     * >, v, &
     * ~, forall, exists
     */

    // IMPLICATION (A > B)
    private Formula parse_implication() {
        Formula left = parse_disjunction();
        skip_whitespace();

        if (match('>')) {
            Formula right = parse_implication();
            return new Implies(left, right);
        }

        return left;
    }

    // DISJUNCTION (A v B)
    private Formula parse_disjunction() {
        Formula left = parse_conjunction();
        skip_whitespace();

        while (match('v')) {
            Formula right = parse_conjunction();
            left = new Or(left, right);
        }

        return left;
    }

    // CONJUNCTION (A & B)
    private Formula parse_conjunction() {
        Formula left = parse_unary();
        skip_whitespace();

        while (match('&')) {
            Formula right = parse_unary();
            left = new And(left, right);
        }

        return left;
    }

    // UNARY (~A, forall(x), exists(x))
    private Formula parse_unary() {
        skip_whitespace();

        if (match('~')) {
            return new Not(parse_unary());
        } 
        
        else if (peek_string("forall")) {
            consume("forall");
            return parse_quantifier(true);
        }

        else if (peek_string("exists")) {
            consume("exists");
            return parse_quantifier(false);
        }

        return parse_primary();
    }

    private Formula parse_quantifier(boolean is_universal) { // T = universal, F = existential
        skip_whitespace();
        expect('(');
        String var = parse_term();
        expect(')');
        Formula f = parse_unary();
        return is_universal ? new ForAll(var, f) : new Exists(var, f);
    }

    private Formula parse_primary() {
        skip_whitespace();

        if (match('(')) {
            Formula f = parse_implication();
            expect(')');
            return f;
        }

        if (match('0')) {
            return new Falsity();
        }

        return parse_predicate();
    }

    private Formula parse_predicate() {
        String name = parse_identifier();
        List<String> args = new ArrayList<>();

        skip_whitespace();

        if (match('(')) {
            if (!peek(')')) {
                do { 
                    args.add(parse_term());
                    skip_whitespace();
                } while (match(','));
            }
            expect(')');
        }
        return new Predicate(name, args);
    }
}