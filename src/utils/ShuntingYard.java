package utils;

import java.util.Stack;

public class ShuntingYard {

    public static String infixToPostfix(String infix) {
        final String ops = "-+/*^";
        final String signs = "-+/*^)( ";
        StringBuilder sb = new StringBuilder();
        Stack<Integer> s = new Stack<>();

        /*for (String token : infix.split("\\s")) {
            if (token.isEmpty())
                continue;
            char c = token.charAt(0);*/
        char prev = 'a';
        for (int i = 0; i < infix.length(); i++){
            char c = infix.charAt(i);
            int idx = ops.indexOf(c);

            // check for operator
            if (idx != -1) {
                if (s.isEmpty())
                    s.push(idx);

                else {
                    while (!s.isEmpty()) {
                        int prec2 = s.peek() / 2;
                        int prec1 = idx / 2;
                        if (prec2 > prec1 || (prec2 == prec1 && c != '^'))
                            sb.append(' ').append(ops.charAt(s.pop()));
                        else break;
                    }
                    s.push(idx);
                }
            }
            else if (c == '(') {
                s.push(-2); // -2 stands for '('
            }
            else if (c == ')') {
                // until '(' on stack, pop operators.
                while (s.peek() != -2)
                    sb.append(ops.charAt(s.pop())).append(' ');
                s.pop();
            }
            else {
                if (c != ' ') {
//                sb.append(token).append(' ');
                    if (signs.indexOf(prev) != -1 && i > 0)
                        sb.append(" ");
                    sb.append(c);
                }
            }
            prev = c;
        }
        while (!s.isEmpty())
            sb.append(' ').append(ops.charAt(s.pop()));
        return sb.toString();
    }
}