package compilador;

import misc.Statics;

public class Token 
{
	private String valor;
	private int linea,
		id,
		tipo;
	public Token(int linea, int tipo, int id, String valor) {
		this.tipo=tipo;
		this.valor=valor;
		this.linea=linea;
		this.id=id;
	}
	public int getTipo() {
		return tipo;
	}
	public String getValor() {
		return valor;
	}
	public int getId() {
		return id;
	}
	public int getLinea() {
		return linea;
	}
	public String toString() {
		return "Token " + valor + ":"
			+ "\n{"
			+ "\n\tid: "+id+","
			+ "\n\tlinea: "+linea+","
			+ "\n\ttipo: " + Statics.tipoDeToken[tipo]
			+ "\n}";
	}
	public String toHTML() {
		return "Token <b>" + valor + "</b>: {<br />"
				+ "&nbsp;&nbsp;&nbsp;&nbsp;id: "+id+",<br />"
				+ "&nbsp;&nbsp;&nbsp;&nbsp;linea: "+linea+",<br />"
				+ "&nbsp;&nbsp;&nbsp;&nbsp;tipo: " + Statics.tipoDeToken[tipo]+"<br />"
				+ "}";
	}
}