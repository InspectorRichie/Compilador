package compilador;

import java.util.ArrayList;
import java.util.Stack;

import estructuraDeDatos.ArbolBinario;
import estructuraDeDatos.NodoArbolBinario;
import misc.Statics;

public class AnalizadorSintactico {
	static Stack<Token> pilaDeParentesis;
	static ArrayList<Token> listaDeOperadores,listaDeOperandos;
	public static boolean analizaTokens(ArrayList<String> listaDeImpresiones, ArrayList<Token> tokens) {
		pilaDeParentesis = new Stack<Token>();
		listaDeOperadores = new ArrayList<Token>();
		listaDeOperandos = new ArrayList<Token>();
		boolean analisisCorrecto = true;
		ArrayList<ArbolBinario<Token>> arboles = new ArrayList<ArbolBinario<Token>>();
		for(int i=0; i<tokens.size(); i++) {
			System.out.println(tokens.get(i));
			Token token = tokens.get(i);
			if(token.getTipo() != Statics.llaveInt && token.getTipo() != Statics.parentesisInt
					&& token.getTipo() != Statics.enteroInt && token.getTipo() != Statics.operadorAritmeticoInt
					&& token.getTipo() != Statics.identificadorInt && token.getTipo() != Statics.dobleInt
					&& !token.getValor().equals(";")) {
				continue;
			}
			if(token.getValor().equals("(") || token.getValor().equals("{")) {
				pilaDeParentesis.push(token);
			}
			else if(token.getValor().equals(")") || token.getValor().equals("}")) {
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
			else if(token.getTipo() == Statics.enteroInt || token.getTipo() == Statics.identificadorInt || token.getTipo() == Statics.dobleInt) {
				if(listaDeOperadores.size() == 0) {
					if(tokens.get(i + 1).getTipo() != Statics.operadorAritmeticoInt)
						continue;
				}
				if(listaDeOperandos.size() > listaDeOperadores.size()) {
					String texto = Statics.getHTML("<p>Error la linea "+token.getLinea()+": Expresión algebraica erronea, se esperaba un <strong>operador</strong> luego del operando <strong>" + listaDeOperandos.get(listaDeOperandos.size()-1).getValor() + "</strong>."
						+ "<br />&nbsp;&nbsp;&nbsp;&nbsp;Se obtuvo <strong>" + token.getValor() + "<strong>",  Statics.consolaCss);
					listaDeImpresiones.add(texto); //Es un error y guardo el donde se produjo el error
					analisisCorrecto = false;
				}
				listaDeOperandos.add(token);
				tokens.set(i, null);
//				System.out.println("entero "+token.getValor()+" quitado");
			}
			else if(token.getTipo() == Statics.operadorAritmeticoInt) {
				if(listaDeOperandos.size() == listaDeOperadores.size()) {
					String error;
					if(listaDeOperadores.size() > 0)
						error = "se esperaba un <strong>operando</strong> luego del operador <strong>" + listaDeOperadores.get(listaDeOperadores.size()-1).getValor() + "</strong>."
							+ "<br />&nbsp;&nbsp;&nbsp;&nbsp;Se obtuvo <strong>" + token.getValor() + "<strong>";
					else
						error = "se esperaba un <strong>operando</strong> antes del primer operador <strong>" + token.getValor() + "</strong>";
					String texto = Statics.getHTML("<p>Error la linea "+token.getLinea()+": Expresión algebraica erronea, " + error,  Statics.consolaCss);
					listaDeImpresiones.add(texto); //Es un error y guardo el donde se produjo el error
					analisisCorrecto = false;
				}
				listaDeOperadores.add(token);
				tokens.set(i, null);
//				System.out.println("operador "+token.getValor()+" quitado");
			}
			else { // ;
//				System.out.println("token "+token.getValor()+" encontrado");
				if(listaDeOperadores.isEmpty() && listaDeOperandos.isEmpty())
					continue;
//				System.out.println(listaDeOperandos.size() + ", " + listaDeOperadores.size());
				if(listaDeOperandos.size() == listaDeOperadores.size() + 1) {
					if(listaDeOperadores.size() > 0) {
						ArbolBinario<Token> arbol = new ArbolBinario<>(token.getLinea(),
							Statics.expresionAlgebraicaInt, listaDeOperandos.get(0).getId());
						arboles.add(arbol);
						arbol.insertar(0, listaDeOperandos.get(0));
						for(int z = 0 ; z < listaDeOperadores.size() ; z++) {
							arbol.insertar(z*2 + 1, listaDeOperadores.get(z));
							arbol.insertar(z*2 + 2, listaDeOperandos.get(z + 1));
						}
						arbol.setValor("arbolonski");
						//ordenaArbolDeExpresionAlgebraica(arbol);
						tokens.set(listaDeOperandos.get(0).getId(), arbol);
//						System.out.println(arbol.getValor());
					}
					else {
						tokens.set(listaDeOperandos.get(0).getId(), listaDeOperandos.get(0));
//						System.out.println("operando "+listaDeOperandos.get(0).getValor()+" reestablecido");
					}
				}
				else {
					String texto = Statics.getHTML("<p>Error la linea "+token.getLinea()+": La cantidad de <strong>operadores</strong> debe ser igual a la cantidad de <strong>operandos</strong> menos uno."
							+ "<br />&nbsp;&nbsp;&nbsp;&nbsp;Hay " + listaDeOperandos.size() + " operandos y " + listaDeOperadores.size() + " operadores.",  Statics.consolaCss);
					
					listaDeImpresiones.add(texto); //Es un error y guardo el donde se produjo el error
					analisisCorrecto = false;
				}
				listaDeOperandos.clear();
				listaDeOperadores.clear();
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
				System.out.println("Error en analisis sintáctico: No se cerró correctamente el " + tokenDePila.getValor() + " de la linea " + tokenDePila.getLinea() + ".");
				analisisCorrecto = false;
			}
		}
		
		for(int i=0; i<tokens.size(); i++) {
			if(tokens.get(i) == null) {
				tokens.remove(i--);
			}
			else {
				tokens.get(i).setId(i);
			}
		}
		
		if(analisisCorrecto)
			listaDeImpresiones.add(Statics.getHTML("<var>Código sin errores sintácticos", Statics.consolaCss));
		return analisisCorrecto;
	}
	
	private static NodoArbolBinario<Token> creaSubArbol(ArbolBinario<Token> arbol, int inicio, int fin) {
		ArbolBinario<Token> auxiliar = new ArbolBinario<Token>(arbol.getLinea(), arbol.getTipo(), arbol.getId());
//		System.out.println("~");
//		arbol.recorridoInfijo();
//		System.out.println("~");
		int contador = 0;
		for(int i = inicio; i < arbol.cuentaNodos(); i++) {
			NodoArbolBinario<Token> nodo = arbol.getByIndex(i);
			auxiliar.insertar(contador++, nodo.getContenido());
//			System.out.println(contador + ", " + nodo);
		}
		NodoArbolBinario<Token>
				raiz = new NodoArbolBinario<Token>(0, auxiliar.getByIndex(1).getContenido()), // raiz
				primerOperador = new NodoArbolBinario<Token>(0, auxiliar.getByIndex(0).getContenido());
		raiz.setDerecha(null);
		raiz.setIzquierda(primerOperador);
		primerOperador.setPadre(raiz);
		if(auxiliar.cuentaNodos() > 3) {
			NodoArbolBinario<Token> restoDeLaExpresion = creaSubArbol(auxiliar, 2, auxiliar.cuentaNodos() - 1);
			raiz.setDerecha(restoDeLaExpresion);
		}
		else {
			NodoArbolBinario<Token> nodoActual = new NodoArbolBinario<Token>(0, auxiliar.getByIndex(2).getContenido());
			nodoActual.setPadre(raiz);
			raiz.setDerecha(nodoActual);
		}
		return raiz;
	}

	private static void ordenaArbolDeExpresionAlgebraica(ArbolBinario<Token> arbol) {
		ArbolBinario<Token> auxiliar = new ArbolBinario<Token>(arbol.getLinea(), arbol.getTipo(), arbol.getId());
		NodoArbolBinario<Token>
				raiz = new NodoArbolBinario<Token>(0, arbol.getByIndex(1).getContenido()), // raiz
				primerOperador = new NodoArbolBinario<Token>(0, arbol.getByIndex(0).getContenido());
		raiz.setIzquierda(primerOperador);
		primerOperador.setPadre(raiz);
		int pemdasAnterior = Statics.getPEMDAS(raiz.getContenido().getValor());
		if(arbol.cuentaNodos() > 3) {
			NodoArbolBinario<Token> restoDeLaExpresion = creaSubArbol(arbol, 2, arbol.cuentaNodos() - 1);
			raiz.setDerecha(restoDeLaExpresion);
		}
		else {
			NodoArbolBinario<Token> nodoActual = new NodoArbolBinario<Token>(0, arbol.getByIndex(2).getContenido());
			nodoActual.setPadre(raiz);
			raiz.setDerecha(nodoActual);
		}
		/*
		if(arbol.cuentaNodos() > 3)
			for(int i = 2; i < arbol.cuentaNodos(); i++) {
				if(i % 2 == 0) { // operando
					if(arbol.cuentaNodos() > i + 1) {
						NodoArbolBinario<Token> restoDeLaExpresion = creaSubArbol(arbol, i, arbol.cuentaNodos() - 1);
						ultimoOperador.setDerecha(restoDeLaExpresion);
						continue;
					}
					NodoArbolBinario<Token> nodoActual = arbol.getByIndex(i);
					nodoActual.setDerecha(null);
					nodoActual.setIzquierda(null);
					nodoActual.setPadre(ultimoOperador);
					ultimoOperador.setDerecha(nodoActual);
				}
				else { // operador
					int pemdasActual = Statics.getPEMDAS(arbol.getByIndex(i).getContenido().getValor());
					if(pemdasAnterior >= pemdasActual) {
						NodoArbolBinario<Token> nodoActual = arbol.getByIndex(i);
						ultimoOperador.setDerecha(nodoActual);
						nodoActual.setDerecha(null);
						nodoActual.setIzquierda(null);
						nodoActual.setPadre(ultimoOperador);
//						auxiliar.borrar(0);
//						auxiliar.insertar(0, arbol.getByIndex(1).getContenido());
					}
					else {
						ultimoOperador.setDerecha(arbol.getByIndex(i));
//						NodoArbolBinario<Token> operandoDerechoSustituido = auxiliar.getByIndex(auxiliar.cuentaNodos() - 1);
//						NodoArbolBinario<Token> operandoIzquierdoSustituido = auxiliar.getByIndex(auxiliar.cuentaNodos() - 3);
//						auxiliar.borrar(i - 3); // operando a la izquierda
//						auxiliar.insertar(i - 3, arbol.getByIndex(i).getContenido());
//						auxiliar.borrar(i - 1); // operando a la derecha
//						auxiliar.insertar(i - 4, operandoDerechoSustituido.getContenido());
//						auxiliar.insertar(i - 1, operandoIzquierdoSustituido.getContenido());
//						posicionDelOperadorAnterior = i - 3;
					}
					ultimoOperador = arbol.getByIndex(i);
					pemdasAnterior = pemdasActual;
				}
			}
			*/
		auxiliar.setRaiz(raiz);
		//auxiliar.recorridoInfijo();
		arbol = auxiliar;
	};
}