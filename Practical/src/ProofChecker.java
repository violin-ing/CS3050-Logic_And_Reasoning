import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ProofChecker {
    // Map to look formulae up by line number
    private final Map<Integer, Formula> FORMULA_MAP = new HashMap<>();

    public void check(List<ParsedLine> proof) {
        for (ParsedLine line : proof) {
            Formula curr = FormulaParser.parse(line.formula_string);

            boolean is_valid = check_line_logic(line, curr);

            if (!is_valid) {
                System.out.println("Response: Failure");
                throw new RuntimeException("logic error at line " + line.line_number);
            }

            FORMULA_MAP.put(line.line_number, curr);
        }

        System.out.println("Response: Success");
        System.out.println("Reason: proof is valid");
    }

    private boolean check_line_logic(ParsedLine line, Formula f) {
        String rule = line.rule;

        switch (rule) {
            case "-premiss", "-assumption" -> { return true; }
            
            // AND rules
            case "&-i" -> { return check_and_intro(line, f); }
            case "&-e1" -> { return check_and_elim(line, f, true); } // Left
            case "&-e2" -> { return check_and_elim(line, f, false); } // Right
            
            // OR rules
            case "v-i1" -> { return check_or_intro(line, f, true); } // Left
            case "v-i2" -> { return check_or_intro(line, f, false); } // Right
            // case "v-e" -> {}

            // Implication rules
            case ">-e" -> { return check_imp_elim(line, f); }
            // case ">-i" -> {}
            // case ">-mt" -> {}
            // case ">-i" -> {}

            // Negations rules
            case "~~-i" -> { return check_double_neg_intro(line, f); }
            case "~~-e" -> { return check_double_neg_elim(line, f); }
            case "~-i" -> { return check_neg_intro(line, f); }
            case "~-e" -> { return check_neg_elim(line, f); }
            case "0-e" -> { return check_falsity_elim(line, f); }

            default -> {
                System.err.println("Error: unknown rule: " + rule);
                return false;
            }
        }
    }

    private Formula get_dependency_formula(ParsedLine line, int index) {
        if (line.dependencies == null || line.dependencies.isEmpty()) return null;
        
        String[] deps = line.dependencies.split(",");
        if (index >= deps.length) return null;
        
        try {
            int line_num = Integer.parseInt(deps[index].trim());
            return FORMULA_MAP.get(line_num);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    // Helper function: checks if f1 == ~f2 OR f2 == ~f1
    private boolean is_contradiction(Formula a, Formula b) {
        if (a.get_type() == Formula.Type.NOT) {
            if (((Not)a).formula.equals(b)) return true;
        }
        if (b.get_type() == Formula.Type.NOT) {
            if (((Not)b).formula.equals(a)) return true;
        }
        return false;
    }

    // === CHECKER FUNCTIONS ===

    private boolean check_and_intro(ParsedLine line, Formula res) {
        // Rule: From A and B, infer (A & B)
        
        Formula f1 = get_dependency_formula(line, 0);
        Formula f2 = get_dependency_formula(line, 1);

        if (f1 == null || f2 == null) return false;

        Formula expected = new And(f1, f2);
        return expected.equals(res);
    }

    private boolean check_and_elim(ParsedLine line, Formula res, boolean is_left) {
        // Rule: From (A & B), infer A and B

        Formula parent = get_dependency_formula(line, 0); 
        if (parent == null) return false;

        // Parent must be AND
        if (parent.get_type() != Formula.Type.AND) return false;
        And p = (And) parent;

        Formula expected = is_left ? p.left : p.right;
        return expected.equals(res);
    }

    private boolean check_or_intro(ParsedLine line, Formula res, boolean is_left) {
        // Rule: From A, infer (A v B); from B, infer (A v B)

        Formula parent = get_dependency_formula(line, 0);
        if (parent == null) return false;

        if (res.get_type() != Formula.Type.OR) return false;
        Or r = (Or) res;

        Formula side_to_check = is_left ? r.left : r.right;
        return parent.equals(side_to_check);
    }

    private boolean check_imp_elim(ParsedLine line, Formula result) {
        // Rule: From (A > B) and A, infer B
        
        Formula f1 = get_dependency_formula(line, 0);
        Formula f2 = get_dependency_formula(line, 1);
        if (f1 == null || f2 == null) return false;

        // Find out which one is the '>' statement
        Implies implies_stmt = null;
        Formula antecedent = null;

        if (f1.get_type() == Formula.Type.IMPLIES) {
            implies_stmt = (Implies) f1;
            antecedent = f2;
        } else if (f2.get_type() == Formula.Type.IMPLIES) {
            implies_stmt = (Implies) f2;
            antecedent = f1;
        } else {
            return false;
        }

        if (!implies_stmt.left.equals(antecedent)) return false;
        return implies_stmt.right.equals(result);
    }

    private boolean check_double_neg_intro(ParsedLine line, Formula res) {
        // Rule: From A infer ~~A
        Formula parent = get_dependency_formula(line, 0);
        if (parent == null) return false;

        Formula expected = new Not(new Not(parent));
        return expected.equals(res);
    }
    
    private boolean check_double_neg_elim(ParsedLine line, Formula res) {
        // Rule: From ~~A infer A
        Formula parent = get_dependency_formula(line, 0);
        if (parent == null) return false;

        if (parent.get_type() != Formula.Type.NOT) return false;
        Not n1 = (Not) parent;
        
        if (n1.formula.get_type() != Formula.Type.NOT) return false;
        Not n2 = (Not) n1.formula;

        // The inner formula (A) must match the result
        return n2.formula.equals(res);
    }

    private boolean check_neg_elim(ParsedLine line, Formula res) {
        // Rule: From A and ~A, infer 0
        if (res.get_type() != Formula.Type.FALSITY) return false;

        Formula f1 = get_dependency_formula(line, 0);
        Formula f2 = get_dependency_formula(line, 1);
        if (f1 == null || f2 == null) return false;

        return is_contradiction(f1, f2);
    }

    private boolean check_falsity_elim(ParsedLine line, Formula res) {
        // Rule: From 0 infer anything
        Formula parent = get_dependency_formula(line, 0);
        if (parent == null) return false;

        // Parent must be 0
        return parent.get_type() == Formula.Type.FALSITY;
    }
}
