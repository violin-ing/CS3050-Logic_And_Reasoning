import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LineParser {
    /**
     * Group 1: Line Number
     * Group 2: Indentation (pipes and spaces)
     * Group 3: Flag variable (e.g., z0)
     * Group 4: Formula String
     * Group 5: Rule Name
     * Group 6: Dependencies/Annotations
     */
    private static final Pattern LINE_PATTERN = Pattern.compile(
        "^(\\d+)\\.\\s+([|\\s]*)(?:(\\w+)\\s+)?(.+?)\\s+([a-zA-Z0-9\\-&>~]+)\\s*:\\s*(.*)$"
    );
    
    // E.g. 3. | (z0,z0) A-e : 2[z0/y]
    // line_num = 3
    // formula_str = R(z0,z0)
    // rule = A-e
    // dependencies = 2[z0/y]

    public static ParsedLine parse_nd_line(String raw_line) {
        Matcher m = LINE_PATTERN.matcher(raw_line.trim());
        if (m.find()) {
            int line_num = Integer.parseInt(m.group(1));
            String indent_str = m.group(2);
            int depth = (int) indent_str.chars().filter(ch -> ch == '|').count();
            String flag = m.group(3); // might be null
            String formula_str = m.group(4).trim();
            String rule = m.group(5);
            String dependencies = m.group(6);

            return new ParsedLine(line_num, depth, flag, formula_str, rule, dependencies);
        }
        throw new IllegalArgumentException("Error : invalid line format: " + raw_line);
    }
}
