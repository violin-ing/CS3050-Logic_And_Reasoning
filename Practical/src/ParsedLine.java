public class ParsedLine {
    int line_number;
    int depth;
    String flag; // The "new" variable for subproofs
    String formula_string; // Pass this to your FormulaParser later
    String rule;
    String dependencies; // You'll need to split this by commas later

    public ParsedLine(int l, int d, String fl, String f, String r, String dep) {
        this.line_number = l; this.depth = d; this.flag = fl;
        this.formula_string = f; this.rule = r; this.dependencies = dep;
    }
}