package planning.parser;


/**
 * Created by emre on 18/05/15.
 */
public class Atom implements LispParser.LispExpr {

    String name;

    public String toString() {
        return name;
    }

    public Atom(String text) {
        name = text;
    }

}
