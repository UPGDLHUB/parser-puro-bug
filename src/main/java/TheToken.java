/**
 * A Token is a pair of a value (string or word) and its type
 *
 * @author javiergs
 * @version 1.0
 */
public class TheToken {

    private String value;
    private String type;
    private int lineNumber;

    public TheToken(String value, String type, int lineNumber) {
        this.value = value;
        this.type = type;
        this.lineNumber = lineNumber;
    }

    public String getValue() {
        return value;
    }

    public String getType() {
        return type;
    }

    public int getLineNumber(){
        return lineNumber;
    }

}
