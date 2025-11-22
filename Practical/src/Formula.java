import java.util.List;
import java.util.ArrayList;
import java.util.Objects;


public abstract class Formula {
    public enum Type {
        PREDICATE, AND, OR, IMPLIES, NOT, FORALL, EXISTS, FALSITY
    }
    public abstract Type get_type();

    public abstract String to_string();
}

/**
 * For 0 (i.e. not)
 */
class Falsity extends Formula {
    Formula formula;

    public Falsity(Formula f) {
        formula = f;
    }

    @Override
    public Type get_type() { return Type.FALSITY; }

    @Override
    public String to_string() {
        return "0" + formula;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Falsity;
    }
}

/**
 * For A(), R(x,y) etc.
 */
class Predicate extends Formula {
    String name;
    List<String> terms;

    public Predicate(String name, List<String> terms) {
        this.name = name;
        this.terms = terms;
    }

    @Override
    public Type get_type() { return Type.PREDICATE; }

    @Override
    public String to_string() {
        return terms.isEmpty() ? name : name + "(" + String.join(", ", terms) + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Predicate)) return false;
        Predicate p = (Predicate) o;
        return name.equals(p.name) && terms.equals(p.terms);
    }
}

/**
 * Binary operations: &, v, >
 */
abstract class BinaryFormula extends Formula {
    Formula left, right;

    public BinaryFormula(Formula l, Formula r) {
        left = l;
        right = r;
    }

    @Override 
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BinaryFormula bf = (BinaryFormula) o;
        return left.equals(bf.left) && right.equals(bf.right);
    }
}

class And extends BinaryFormula {
    public And(Formula l, Formula r) { super(l, r); }

    @Override
    public Type get_type() { return Type.AND; }

    @Override
    public String to_string() {
        return "(" + left + " & " + right + ")";
    }
}

class Or extends BinaryFormula {
    public Or(Formula l, Formula r) { super(l, r); }

    @Override
    public Type get_type() { return Type.OR; }

    @Override
    public String to_string() {
        return "(" + left + " v " + right + ")";
    }
}

class Implies extends BinaryFormula {
    public Implies(Formula l, Formula r) { super(l, r); }

    @Override
    public Type get_type() { return Type.IMPLIES; }

    @Override
    public String to_string() {
        return "(" + left + " > " + right + ")";
    }
}

/**
 * For quantifiers: forall, exists
 */
abstract class Quantifier extends Formula {
    String variable;
    Formula inner;

    public Quantifier(String v, Formula i) { this.variable = v; this.inner = i; }
    
    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        Quantifier q = (Quantifier) o;
        return variable.equals(q.variable) && inner.equals(q.inner);
    }
}

class ForAll extends Quantifier {
    public ForAll(String v, Formula i) { super(v, i); }

    @Override 
    public Type get_type() { return Type.FORALL; }

    @Override 
    public String to_string() { 
        return "forall(" + variable + ")" + inner; 
    }
}

class Exists extends Quantifier {
    public Exists(String v, Formula i) { super(v, i); }

    @Override 
    public Type get_type() { return Type.EXISTS; }

    @Override 
    public String to_string() { 
        return "exists(" + variable + ")" + inner; 
    }
}