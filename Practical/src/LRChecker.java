public class LRChecker {
    public static void main(String[] args) {
        bool test_mode, check_mode = false;

        // Parse arguments
        if (args.length != 3) {
            System.err.println("error: usage: lrchecker [--check | --test] file.[nd | sq]");
            exit();
        }

        if (args[1].equals("--check")) {
            check_mode = true;
        } else if (args[1].equals("--test")) {
            test_mode = true;
        } else {
            System.err.println("error: usage: lrchecker [--check | --test] file.[nd | sq]");
            exit();
        }

        String file_name = args[2];

        // Checking file extension
        String file_ext = 
    }
}

/**
 * === ASCII SYMBOLS ===
 * - 0 => falsity
 * - & => conjunction
 * - v => disjunction
 * - > => implication
 * - ~ => negation
 * - forall => universal quantification
 * - exists => existential quantification
 */