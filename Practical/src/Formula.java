import java.util.List;


public abstract class Formula {
    public abstract String to_string();
}

class FalseFormula extends Formula {
    @Override
    public String to_string() {
        return "0";
    }
}

class BinaryFormula extends Formula { // (A & B), (A v B), (A > B)
    Formula left;
    Formula right;
    String operator;

    // Constructor
    public BinaryFormula(Formula left, String operator, Formula right) {
        this.left = left;
        this.right = right;
        this.operator = operator;
    }

    @Override
    public String to_string() {
        return "(" + left + " " + operator + " " + right + ")";
    }
}

class NegationFormula extends Formula { // ~A
    Formula formula;

    // Constructor
    public NegationFormula(Formula f) {
        this.formula = f;
    }

    @Override
    public String to_string() {
        return "~" + formula;
    }
}

class QuantifierFormula extends Formula { // forall(A), exists(A)
    String type; // "forall" or "exists"
    String variable;
    Formula formula;

    // Constructor
    public QuantifierFormula(String type, String var, Formula f) {
        this.type = type; this.variable = var; this.formula = f;
    }

    public String to_string() { 
        return type + "(" + variable + ") " + formula; 
    }
}

class PredicateFormula extends Formula { // P(x, y)
    String name;
    List<String> terms;
    
    // Constructor
    public PredicateFormula(String name, List<String> terms) {
        this.name = name;
        this.terms = terms;
    }

    public String to_string() {
        if (terms.isEmpty()) {
            return name;
        } else {
            return name + "(" + String.join(", ", terms) + ")";
        }
    }
}