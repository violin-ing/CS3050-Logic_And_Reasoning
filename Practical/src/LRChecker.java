import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class LRChecker {
    private static String mode;

    private static void args_validation(String args[]) {
        // Parse arguments
        if (args.length != 2) {
            System.err.println("error: usage: ./lrchecker [--check | --test] [file.nd | file.sq]");
            System.exit(1);
        }

        switch (args[0]) {
            case "--check" -> mode = "check";
            case "--test" -> mode = "test";
            default -> {
                System.err.println("error: usage: ./lrchecker [--check | --test] [file.nd | file.sq]");
                System.exit(1);
            }
        }
    }

    private static void file_validation(String file_name) {
        // Check file existence
        File file_obj = new File(file_name);
        if (!file_obj.isFile()) {
            System.err.printf("error: cannot access '%s': No such file or directory\n", file_name);
            System.exit(1);
        }

        // Check file extension
        String[] file_name_parts = file_name.split("\\.");
        int len = file_name_parts.length;
        String file_ext = (len == 1) ? "" : file_name_parts[len - 1];
        if (!(file_ext.equals("nd") || file_ext.equals("sq"))) {
            System.err.printf("error: '%s': file extension not allowed\n", file_name);
            System.exit(1);
        }
    }

    private static String[] parse_file(String file_name) throws IOException{
        List<String> file_lines = new ArrayList<>();

        try (BufferedReader buf = new BufferedReader(new FileReader(file_name))) {
            String line;
            while ((line = buf.readLine()) != null) {
                file_lines.add(line);
            }
        }
        return file_lines.toArray(String[]::new);
    }

    public static void main(String[] args) {
        // Validate arguments
        args_validation(args);

        // Validate file
        String file_name = args[1].trim();
        file_validation(file_name);

        // Parse file contents -> obtain array of lines
        String[] file_lines = null;
        try {
            file_lines = parse_file(file_name);
        } catch(IOException e) {
            System.err.println();
            System.exit(1);
        }

        switch (mode) {
            case "check" -> {
                // Proof checker mode
            }
            case "test" -> {
                // Validity tester mode
            }
            default -> System.exit(0);
        }
    }
}
