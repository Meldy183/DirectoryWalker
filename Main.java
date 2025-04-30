import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = Integer.parseInt(scanner.nextLine());
        Handler handler = new Handler();
        for (int i = 0; i < n; i++) {
            String s = scanner.nextLine();
            if (Validator.validate(s)) {
                handler.takeInput(s);
            }
        }
        handler.build();
    }
}


