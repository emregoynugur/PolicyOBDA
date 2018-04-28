package utils;



import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.jena.sparql.expr.E_Add;
import org.apache.jena.sparql.expr.E_Divide;
import org.apache.jena.sparql.expr.E_Equals;
import org.apache.jena.sparql.expr.E_GreaterThan;
import org.apache.jena.sparql.expr.E_GreaterThanOrEqual;
import org.apache.jena.sparql.expr.E_LessThan;
import org.apache.jena.sparql.expr.E_LessThanOrEqual;
import org.apache.jena.sparql.expr.E_Multiply;
import org.apache.jena.sparql.expr.E_NotEquals;
import org.apache.jena.sparql.expr.E_Subtract;
import org.apache.jena.sparql.expr.Expr;
import org.apache.jena.sparql.expr.ExprVar;
import org.apache.jena.sparql.expr.nodevalue.NodeValueBoolean;
import org.apache.jena.sparql.expr.nodevalue.NodeValueFloat;
import org.apache.jena.sparql.expr.nodevalue.NodeValueInteger;
import org.apache.jena.sparql.syntax.ElementFilter;

/**
 * Created by emre on 12/04/15.
 */
public class SystemFunctionParser {
    /* TODO add other expressions and implement mathematical expression evaluator */
    private final static String CURRENT_TIME = "current_time";

    private final static String EQUALS = "equals";
    private final static String NOT_EQUALS = "not_equals";

    private final static String GREATER_THAN = "greater_than";
    private final static String GREATER_THAN_OR_EQUAL = "greater_than_or_equal";

    private final static String LESS_THAN = "less_than";
    private final static String LESS_THAN_OR_EQUAL = "less_than_or_equal";

    //TODO: time
    //private String timeVar = "";
    //private String timeVal = SchemaDateFormat.getTimeStamp();

    public SystemFunctionParser() {

    }

    public List<ElementFilter> parseSystemFunctions(Set<String> functions) {
        List<ElementFilter> filters = new ArrayList<ElementFilter>();
        List<Expr> expressions = new ArrayList<Expr>();

        /*TODO Should do a regex check for parsing */
        /*for (String f : functions) {
            if (f.contains(CURRENT_TIME))
                timeVar = f.substring(f.indexOf("(") + 1, f.indexOf(")"));
        }*/

        for (String f : functions) {
            if (f.contains(CURRENT_TIME))
                continue;

            String function = f.substring(0, f.indexOf("("));
            String[] params = f.substring(f.indexOf("(") + 1, f.indexOf(")")).split(",");

            expressions.add(parseFunction(function, params));
        }

        for (Expr e : expressions) {
            filters.add(new ElementFilter(e));
        }

        return filters;
    }

    private Expr parseFunction(String function, String[] params) {
        switch (function) {
            case GREATER_THAN:
                return new E_GreaterThan(parseExpression(params[0]), parseExpression(params[1]));
            case GREATER_THAN_OR_EQUAL:
                return new E_GreaterThanOrEqual(parseExpression(params[0]), parseExpression(params[1]));
            case LESS_THAN:
                return new E_LessThan(parseExpression(params[0]), parseExpression(params[1]));
            case LESS_THAN_OR_EQUAL:
                return new E_LessThanOrEqual(parseExpression(params[0]), parseExpression(params[1]));
            case EQUALS:
                return new E_Equals(parseExpression(params[0]), parseExpression(params[1]));
            case NOT_EQUALS: 
            	return new E_NotEquals(parseExpression(params[0]), parseExpression(params[1]));
        }
        return null;
    }

    private Expr parseExpression(String param) {
        String op = "";

        /* Works only for expressions with two parameters and single operator. Currently operation precedence is not considered. */
        if (param.contains("-"))
            op = "-";
        else if (param.contains("+"))
            op = "+";
        else if (param.contains("*"))
            op = "*";
        else if (param.contains("/"))
            op = "/";

        if (op == null || op.length() < 1) {
            return parseSingleExpression(param);
        }

        System.out.printf("postfix: %s%n", ShuntingYard.infixToPostfix(param));
        System.out.printf("postfix: %s%n", ShuntingYard.infixToPostfix("3 - 5 + 8- 1+7 * 5"));

        return evalRPN(ShuntingYard.infixToPostfix(param));
    }

    private Expr parseSingleExpression(String param) {
        if (isInteger(param))
            return new NodeValueInteger(Integer.parseInt(param));
        else if (isFloat(param))
            return new NodeValueFloat(Float.parseFloat(param));
        else if(isBoolean(param))
        	return new NodeValueBoolean(Boolean.parseBoolean(param));
//            else if (SchemaDateFormat.isDate(op))
//                return new NodeValueDT("asd", null);
//            else if (param.contains(timeVar))
//                return new NodeValue.makeString(SchemaDateFormat.getTimeStamp());
        /*else if (param.equals(timeVar))
            return new NodeValueDT (timeVal);*/
        else if (param.contains("?"))
            return new ExprVar(param.replace("?", ""));
//                return new NodeValueString(op);
        else
            return null;
    }

    private Expr evalRPN(String expr) {
        String cleanExpr = expr;//cleanExpr(expr);
        LinkedList<Expr> stack = new LinkedList<Expr>();
        for (String token : cleanExpr.split("\\s")) {
            Expr tokenNum = parseSingleExpression(token);//Double.parseDouble(token);
            if (tokenNum != null) {
                stack.push(tokenNum);
            } else if (token.equals("*")) {
                Expr secondOperand = stack.pop();
                Expr firstOperand = stack.pop();
                stack.push(new E_Multiply(firstOperand, secondOperand));
            } else if (token.equals("/")) {
                Expr secondOperand = stack.pop();
                Expr firstOperand = stack.pop();
                stack.push(new E_Divide(firstOperand, secondOperand));
            } else if (token.equals("-")) {
                Expr secondOperand = stack.pop();
                Expr firstOperand = stack.pop();
                stack.push(new E_Subtract(firstOperand, secondOperand));
            } else if (token.equals("+")) {
                Expr secondOperand = stack.pop();
                Expr firstOperand = stack.pop();
                stack.push(new E_Add(firstOperand, secondOperand));
            } else if (token.equals("^")) {
                // TODO:
                /*Expr secondOperand = stack.pop();
                Expr firstOperand = stack.pop();
                stack.push(Math.pow(firstOperand, secondOperand));*/
            } else {//just in case
                System.err.println("Error EvalRPN");
                return null;
            }
            System.out.println(stack);
        }
        Expr ret = stack.pop();
        System.out.println("Final answer: " + ret);
        return ret;
    }

    /*private static String cleanExpr(String expr) {
        //remove all non-operators, non-whitespace, and non digit chars
        return expr.replaceAll("[^\\^\\*\\+\\-\\d/\\s]", "");
    }*/

    private static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return false;
        } catch (NullPointerException e) {
            return false;
        }
        // only got here if we didn't return false
        return true;
    }
    
    private static boolean isBoolean(String s) {
        try {
            Boolean.parseBoolean(s);
        } catch (NumberFormatException e) {
            return false;
        } catch (NullPointerException e) {
            return false;
        }
        // only got here if we didn't return false
        return true;
    }

    private static boolean isFloat(String s) {
        try {
            Float.parseFloat(s);
        } catch (NumberFormatException e) {
            return false;
        } catch (NullPointerException e) {
            return false;
        }
        // only got here if we didn't return false
        return true;
    }
}
