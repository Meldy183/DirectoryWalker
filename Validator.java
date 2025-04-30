// validates length
public class Validator {
    static Boolean validate(String input) {
        String[] parse = input.split(" ");
        if (parse[0].equals("DIR")) {
            return (parse.length == 3 || parse.length == 4);
        }
        if (parse[0].equals("FILE")) {
            return parse.length == 7;
        }
        return false;
    }
}
