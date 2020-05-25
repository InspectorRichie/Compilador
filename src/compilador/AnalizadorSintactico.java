package compilador;

import java.util.ArrayList;
import java.util.Stack;

import misc.Statics;

public class AnalizadorSintactico {
	static Stack<Token> pilaDeParentesis;
	public static boolean parentesisCorrectos(ArrayList<Token> tokens, ArrayList<String> listaDeImpresiones) {
		pilaDeParentesis = new Stack<Token>();
		boolean analisisCorrecto = true;
		for(int i=0; i<tokens.size(); i++) {
			Token token = tokens.get(i);
			if(token.getTipo() != Statics.llaveInt && token.getTipo() != Statics.parentesisInt)
				continue;
			if(token.getValor().equals("(") || token.getValor().equals("{")) {
				pilaDeParentesis.push(token);
			}
			else {
				if(!pilaDeParentesis.isEmpty()) {
					if(pilaDeParentesis.peek().getValor().equals("(") && token.getValor().equals(")")
					|| pilaDeParentesis.peek().getValor().equals("{") && token.getValor().equals("}")) {
						pilaDeParentesis.pop();
					}
					else {
						String tokenEsperado;
						if(pilaDeParentesis.peek().getValor().equals("("))
							tokenEsperado = ")";
						else
							tokenEsperado = "}";
						String texto = Statics.getHTML("<p>Error la linea "+token.getLinea()+": Se esperaba un <strong>" + tokenEsperado + "</strong> para cerrar el "
							+ "<strong>" + pilaDeParentesis.peek().getValor() + "</strong> añadido en la linea " + pilaDeParentesis.peek().getLinea() + ".",  Statics.consolaCss);
						listaDeImpresiones.add(texto); //Es un error y guardo el donde se produjo el error
						System.out.println("Error sintáctico: se esperaba un " + tokenEsperado + " para cerrar el último " + token.getValor() + ".");
						analisisCorrecto = false;
					}
				}
				else {
					String tokenFaltante;
					if(token.getValor().equals(")"))
						tokenFaltante = "(";
					else
						tokenFaltante = "{";
					String texto = Statics.getHTML("<p>Error la linea "+token.getLinea()+": No hay un <strong>" + tokenFaltante + "</strong> para cerrar con el "
						+ "<strong>" + token.getValor() + "</strong> que está agregando.",  Statics.consolaCss);
					listaDeImpresiones.add(texto); //Es un error y guardo el donde se produjo el error
					System.out.println("Error sintáctico: no se esperaba un " + token.getValor() + " porque no hay un " + tokenFaltante + " anteriormente.");
					analisisCorrecto = false;
				}
			}
		}
		
		if(!pilaDeParentesis.isEmpty()) { // muestrá la cola de parentesis por cerrar y que no fueron cerrados
			Stack<Token> cola = new Stack<Token>();
			while(!pilaDeParentesis.isEmpty()) {
				cola.push(pilaDeParentesis.pop());
			}
			while(!cola.isEmpty()) {
				Token tokenDePila = cola.pop();
				String texto = Statics.getHTML("<p>Error la linea "+tokenDePila.getLinea()+": No se cerró correctamente el <strong>" + tokenDePila.getValor() + "</strong>.",  Statics.consolaCss);
				listaDeImpresiones.add(texto); //Es un error y guardo el donde se produjo el error
				System.out.println("Error en analisis léxico: No se cerró correctamente el " + tokenDePila.getValor() + " de la linea " + tokenDePila.getLinea() + ".");
				analisisCorrecto = false;
			}
		}
		
		if(analisisCorrecto)
			listaDeImpresiones.add(Statics.getHTML("<var>Código sin errores sintácticos", Statics.consolaCss));
		return analisisCorrecto;
	}
}