import java.io.File;
import java.io.IOException;
import java.util.Vector;

public class TheMain {
    public static void main(String[] args) throws IOException {
        File file = new File("src/main/resources/input.txt");
        Lexer lexer = new Lexer(file); // Asegúrate de que el nombre de la clase sea correcto
        lexer.run();
        lexer.printTokens();
        
        Vector<TheToken> tokens = lexer.getTokens();
        TheParser parser = new TheParser(tokens);
        parser.run();
    }
}
