package planning.parser;

/**
 * Created by emre on 17/05/15.
 */
import java.util.Arrays;
import java.util.Iterator;
import java.util.ArrayList;


@SuppressWarnings("serial")
public class LispExprList extends ArrayList<LispParser.LispExpr> implements LispParser.LispExpr
{
    LispExprList parent = null;
    int indent =1;

    public int getIndent()
    {
        if (parent != null)
        {
            return parent.getIndent()+indent;
        }
        else return 0;
    }

    public void setIndent(int indent)
    {
        this.indent = indent;
    }

    public void setParent(LispExprList parent)
    {
        this.parent = parent;
    }

    public String toString()
    {
        String indent = "";
        if (parent != null && parent.get(0) != this)
        {
            indent = "\n";
            char[] chars = new char[getIndent()];
            Arrays.fill(chars, ' ');
            indent += new String(chars);
        }

        String output = indent+"(";
        for(Iterator<LispParser.LispExpr> it=this.iterator(); it.hasNext(); )
        {
            LispParser.LispExpr lispExpr = it.next();
            output += lispExpr.toString();
            if (it.hasNext())
                output += " ";
        }
        output += ")";
        return output;
    }

    @Override
    public synchronized boolean add(LispParser.LispExpr e)
    {
        if (e instanceof LispExprList)
        {
            ((LispExprList) e).setParent(this);
            if (size() != 0 && get(0) instanceof Atom)
                ((LispExprList) e).setIndent(2);
        }
        return super.add(e);
    }

}