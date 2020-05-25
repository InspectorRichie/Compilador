package estructuraDeDatos;

public class Token 
{
	private int tipo;
	String definicion[] = {
			"Modificador", // 0
			"Palabra resevada", // 1
			"Tipo de dato", // 2
			"Simbolo", // 3
			"Operador logico", // 4
			"Operador aritmetico", // 5
			"Constante", // 6
			"Identificador", // 7
			"Declaracion de clase"}; // 8
	private String valor;
	private int linea, columna;
	public Token(String valor, int tipo, int linea, int columna) {
		this.tipo=tipo;
		this.valor=valor;
		this.linea=linea;
		this.columna=columna;
	}
	public Token(String valor, int tipo, int linea) {
		this.tipo=tipo;
		this.valor=valor;
		this.linea=linea;
		this.columna=-666;
	}
	public int getTipo() {
		return tipo;
	}
	public String getValor() {
		return valor;
	}
	public int getColumna() {
		return columna;
	}
	public int getLinea() {
		return linea;
	}
	public void setValor(String valor) {
		this.valor = valor;
	}
	public String toString() {
		return "Token encontrado: " + definicion[tipo] + ": " + valor;
	}
}