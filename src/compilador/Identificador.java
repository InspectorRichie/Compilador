package compilador;

import misc.Statics;

public class Identificador {
	private String nombre,
		valor;
	private boolean alcanceTerminado;
	private int posicion, // linea del código
		alcance,
		tipo,
		id; // es usada para ordenar los identificadores antes de mostrarlos en la tabla
	
	public Identificador(int posicion, int alcance, int tipo, String nombre, String valor, int id) {
		this.posicion = posicion;
		this.alcance = alcance; // 0: global, otro: local (el valor que sea será a quien pertenece)
		this.tipo = tipo;
		this.nombre = nombre;
		this.valor = valor;
		this.id = id;
		this.alcanceTerminado = false;
	}

	public int getPosicion() {
		return posicion;
	}
	public void setPosicion(int posicion) {
		this.posicion = posicion;
	}
	public int getAlcance() {
		return alcance;
	}
	public void setAlcance(int alcance) {
		this.alcance = alcance;
	}
	public int getTipo() {
		return tipo;
	}
	public void setTipo(int tipo) {
		this.tipo = tipo;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getValor() {
		return valor;
	}
	public void setValor(String valor) {
		this.valor = valor;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public boolean esDeAlcanceTerminado() {
		return alcanceTerminado;
	}
	public void alternarVidaDeAlcance() {
		this.alcanceTerminado = !this.alcanceTerminado;
	}
	public String toString() {
		return "Identificador "+nombre+": {"
			+ "\n\tid: " + id + ","
			+ "\n\tvalor: " + valor + ","
			+ "\n\ttipo: " + Statics.tipoDeDato[tipo]
			+ "\n\talcance: " + (alcance==0?"global":"local") + ", idAlcance: " + alcance
			+ "\n\tlinea: " + posicion
			+ "\n}";
	}
	public String toHTML() {
		return "Identificador "+nombre+": {<br />"
			+ "&nbsp;&nbsp;&nbsp;&nbsp;valor: " + valor + ",<br />"
			+ "&nbsp;&nbsp;&nbsp;&nbsp;tipo: " + Statics.tipoDeDato[tipo] + "<br />"
			+ "&nbsp;&nbsp;&nbsp;&nbsp;alcance: " + (alcance==0?"global":"local") + ", idAlcance: " + alcance + "<br />"
			+ "&nbsp;&nbsp;&nbsp;&nbsp;linea: " + posicion + "<br />"
			+ "}";
	}
}