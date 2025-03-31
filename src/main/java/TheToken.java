/**
 * A Token is a pair of a value (string or word) and its type,
 * with additional line number information for better error reporting
 *
 * @author javiergs
 * @version 1.1
 */
public class TheToken {
	
	private String value;
	private String type;
	private int lineNumber;  // Nuevo campo para almacenar el número de línea
	
	public TheToken(String value, String type) {
		this.value = value;
		this.type = type;
		this.lineNumber = 0;  // Valor por defecto
	}
	
	// Constructor sobrecargado para incluir número de línea
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
	
	// Método nuevo para obtener el número de línea
	public int getLineNumber() {
		return lineNumber;
	}
	
	// Método para establecer el número de línea después de la creación del token
	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}
}
