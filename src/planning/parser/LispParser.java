package planning.parser;

/**
 * Created by emre on 17/05/15.
 */
public class LispParser
{
    LispTokenizer tokenizer;

    public LispParser(LispTokenizer input)
    {
        tokenizer=input;
    }
    
    @SuppressWarnings("serial")
    public class ParseException extends Exception
    {

    }

    public interface LispExpr {
        // abstract parent for Atom and LispExprList
    }

    public LispExpr parseExpr() throws ParseException
    {
        Token token = tokenizer.next();
        switch(token.type)
        {
            case '(': return parseExprList(token);
            case '"': return new StringAtom(token.text);
            default: return new Atom(token.text);
        }
    }


    protected LispExprList parseExprList(Token openParen) throws ParseException
    {
        LispExprList acc = new LispExprList();
        while(tokenizer.peekToken().type != ')')
        {
            LispExpr element = parseExpr();
            acc.add(element);
        }
        tokenizer.next();
        return acc;
    }

}