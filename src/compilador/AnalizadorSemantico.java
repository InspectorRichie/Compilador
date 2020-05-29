package compilador;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Stack;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import estructuraDeDatos.ArbolBinario;
import estructuraDeDatos.NodoArbolBinario;
import misc.Statics;

public class AnalizadorSemantico {
	static Stack<Token> pilaDeParentesis;
	static String [] cabecera;
	static JTable tabla;
	static HashMap<String, Identificador> identificadoresReales;
	static ArrayList<Identificador> listaDeIdentificadoresCompleta;
	static ArrayList<String> listaDeImpresiones, listaDeCuadruplos;
	static ArrayList<Token> tokens;
	static Stack<Integer> alcanceLocal; // para guardar el alcance de las variables
	static int contadorDeIdentificadores, alcance, contadorDeContextos, tipo;
	static boolean hayIgual = false, hayVariable = false, analisisCorrecto;
	static String valor = "", nombre = "";
	
	@SuppressWarnings("unchecked")
	public static boolean analizarTokens(ArrayList<String> listaDeImpresiones, ArrayList<String> listaDeCuadruplos, ArrayList<Token> tokens, HashMap<String, Identificador> identificadoresReales, JTable tabla, String [] cabecera) {
		AnalizadorSemantico.cabecera = cabecera;
		AnalizadorSemantico.tabla = tabla;
		AnalizadorSemantico.identificadoresReales = identificadoresReales;
		AnalizadorSemantico.listaDeImpresiones = listaDeImpresiones;
		AnalizadorSemantico.listaDeCuadruplos = listaDeCuadruplos;
		AnalizadorSemantico.tokens = tokens;
		listaDeIdentificadoresCompleta = new ArrayList<Identificador>();
		alcanceLocal = new Stack<Integer>();
		tipo = -1;
		alcance = 0;
		valor = nombre = "";
		contadorDeIdentificadores = contadorDeContextos = 1; // inicia en 2 porque este se asigna al alcance, y alcance 0 es global
		boolean errorEnLinea = false;
		analisisCorrecto = true;
		hayIgual = hayVariable = false;
		int ultimaLineaDeError = -1;
		pilaDeParentesis = new Stack<Token>();
		for(int i=0; i<tokens.size(); i++) {
			Token token = tokens.get(i);
			if(token == null)
				continue;
			if(errorEnLinea)
				if(ultimaLineaDeError < token.getLinea()) { // si se ha pasado a otra linea y había error, se restaura todo, el error queda en la linea anterior
					errorEnLinea = false;
					tipo = -1;
					alcance = 0;
					valor = nombre = "";
				}
			if(tokens.get(i).getValor().equals(";")) {
				if(!(tipo < 0 && valor.length() == 0 && nombre.length() == 0)) {
					if(!creaNuevoSimbolo(i, errorEnLinea))
						analisisCorrecto = false;
				}
				tipo = -1;
				alcance = 0;
				valor = nombre = "";
				errorEnLinea = hayIgual = hayVariable = false;
				continue;
			}
			if(token.getTipo() == Statics.tipoDeDatoInt) {
				if(tipo < 0 && nombre.length() == 0 && valor.length() == 0)
					tipo = Statics.deArrayEstaticaADinamica(Statics.tipoDeDato).indexOf(token.getValor());
				else {
					errorEnLinea = true;
					analisisCorrecto = false;
					ultimaLineaDeError = token.getLinea();
					mensajeSeEsperaba(i);
				}
				continue;
			}
			if(token.getTipo() == Statics.identificadorInt) {
				if(/*tipo >= 0 && */nombre.length() == 0 && valor.length() == 0) {
					nombre = token.getValor();
					hayVariable = true;
					continue;
				}
				String mensajeError = "";
				if(nombre.length() > 0 && valor.length() == 0) {
					if(i > 0)
						if(tokens.get(i-1).getValor().equals("="))
							if(identificadoresReales.containsKey(token.getValor())) {
								int tipoDelPrimerIdentificador = tipo;
								if(tipoDelPrimerIdentificador < 0)
									if(identificadoresReales.containsKey(tokens.get(i-2).getValor()))
										tipoDelPrimerIdentificador = identificadoresReales.get(tokens.get(i-2).getValor()).getTipo();
									else {
										mensajeError = "<p>Error en la linea "+token.getLinea()+": error al asignar la variable <strong>" + token.getValor() + "</strong> a la variable " + tokens.get(i-2).getValor()
										+"<br />&nbsp;&nbsp;&nbsp;&nbsp;La variable " + tokens.get(i-2).getValor() + " no ha sido declarada anteriormente.";
										listaDeImpresiones.add(Statics.getHTML(mensajeError, Statics.consolaCss));
										analisisCorrecto = false;
									}
								if(identificadoresReales.get(token.getValor()).getTipo() == tipoDelPrimerIdentificador) {
									if(hayIgual)
										valor = identificadoresReales.get(token.getValor()).getValor();
									else {
										mensajeError = "<p>Error en la linea "+token.getLinea()+": error al asignar la variable <strong>" + token.getValor() + "</strong> a la variable <strong>" + nombre + "</strong>"
										+"<br />&nbsp;&nbsp;&nbsp;&nbsp;Hace falta un <b>=</b> entre ellos.";
										listaDeImpresiones.add(Statics.getHTML(mensajeError, Statics.consolaCss));
									}
									continue;
								}
								else {
									if(tipoDelPrimerIdentificador > 0) {
										mensajeError = "<p>Error en la linea "+token.getLinea()+": error al asignar la variable <strong>" + token.getValor() + "</strong> a la variable <strong>" + nombre + "</strong>"
										+"<br />&nbsp;&nbsp;&nbsp;&nbsp;La variable "+token.getValor()+" no es de un tipo compatible con el de la variable " + nombre + ", ("+Statics.tipoDeDato[identificadoresReales.get(token.getValor()).getTipo()]+" y " + Statics.tipoDeDato[tipoDelPrimerIdentificador] + ")";
										listaDeImpresiones.add(Statics.getHTML(mensajeError, Statics.consolaCss));
									}
									analisisCorrecto = false;
								}
							}
							else {
								mensajeError = "<p>Error en la linea "+token.getLinea() + ": error al asignar la variable <strong>" + token.getValor() + "</strong> a la variable <strong>" + nombre + "</strong>"
								+"<br />&nbsp;&nbsp;&nbsp;&nbsp;La variable "+token.getValor()+" no ha sido declarada antes de su uso.";
								listaDeImpresiones.add(Statics.getHTML(mensajeError, Statics.consolaCss));
								analisisCorrecto = false;
							}
				}
				errorEnLinea = true;
				analisisCorrecto = false;
				ultimaLineaDeError = token.getLinea();
				if(mensajeError.length() == 0) {
					mensajeSeEsperaba(i);
				}
				continue;
			}
			if(token.getValor().equals("=")) {
				if(/*tipo >= 0 && */nombre.length() > 0 && valor.length() == 0) {
					hayIgual = true;
					continue;
				}
				else {
					errorEnLinea = true;
					analisisCorrecto = false;
					ultimaLineaDeError = token.getLinea();
					mensajeSeEsperaba(i);
				}
				continue;
			}
			if(token.getTipo() == Statics.expresionAlgebraicaInt || token.getTipo() == Statics.dobleInt
					|| token.getTipo() == Statics.booleanoInt || token.getTipo() == Statics.enteroInt
					|| token.getTipo() == Statics.cadenaInt) {
				if(/*tipo >= 0 && */nombre.length() > 0 && valor.length() == 0 && hayIgual) {
					if(token.getTipo() == Statics.expresionAlgebraicaInt) {
						Token tokenEsteMamalonMamalaskiMamalawer = resuelveExpresionAlgebraica((ArbolBinario<Token>)token);
						if(tokenEsteMamalonMamalaskiMamalawer != null)
							valor = tokenEsteMamalonMamalaskiMamalawer.getValor();
					}
					else
						valor = token.getValor();
				}
				else {
					errorEnLinea = true;
					analisisCorrecto = false;
					ultimaLineaDeError = token.getLinea();
					if(!hayIgual) {
						String mensajeError =  "<p>Error en la linea "+token.getLinea()+": ";
						if(hayVariable) {
							mensajeError += "error al asignar la constante <strong>" + token.getValor() + "</strong> a la variable <strong>" + nombre + "</strong>"
							+"<br />&nbsp;&nbsp;&nbsp;&nbsp;Hace falta un <b>=</b> entre ellos.";
						}
						else {
							String tipoDeVariable;
							if(token.getTipo() == Statics.expresionAlgebraicaInt)
								tipoDeVariable = "la expresión algebraica";
							else
								tipoDeVariable = "la constante de tipo " + Statics.tipoDeToken[token.getTipo()];
							mensajeError += "no está haciendo nada con " + tipoDeVariable + " cuyo valor es igual a " + token.getValor() + ".";
						}
						listaDeImpresiones.add(Statics.getHTML(mensajeError, Statics.consolaCss));
					}
					else
						mensajeSeEsperaba(i);
				}
				continue;
			}
			if(token.getTipo() == Statics.parentesisInt || token.getTipo() == Statics.llaveInt) {
				if(token.getValor().equals("(") || token.getValor().equals("{")) {
					pilaDeParentesis.push(token);
					if(token.getValor().equals("{")) {
						if(tipo == Statics.getTipoDeConstante("class"))
							if(nombre.length() == 0) {
								String mensajeError = "<p>Error en la linea "+token.getLinea()+": no asignó un <strong>nombre</strong> para la clase que quiere asignar.";
								listaDeImpresiones.add(Statics.getHTML(mensajeError, Statics.consolaCss));
								errorEnLinea = true;
								analisisCorrecto = false;
							}
							else {
								int alcanceDeClase = (alcanceLocal.isEmpty() ? 0 : alcanceLocal.peek());
								identificadoresReales.put(nombre, new Identificador(token.getLinea(), alcanceDeClase, Statics.getTipoDeConstante("class"), nombre, "{}", contadorDeIdentificadores++) );
								errorEnLinea = false;
							}
						hayIgual = hayVariable = false;
						tipo = -1;
						alcance = 0;
						valor = nombre = "";
						alcanceLocal.push(contadorDeContextos++);
					}
				}
				else {
					if(pilaDeParentesis.peek().getValor().equals("(") && token.getValor().equals(")")
					|| pilaDeParentesis.peek().getValor().equals("{") && token.getValor().equals("}")) {
						pilaDeParentesis.pop();
						if(token.getValor().equals("{") || token.getValor().equals("}")) {
							System.out.println("-"+ nombre.length());
							if(tipo == Statics.getTipoDeConstante("class")) {
								if(token.getValor().equals("}")) {
									String mensajeError = "<p>Error en la linea "+token.getLinea()+": no está declarando correctamente la clase <strong>" + nombre + "</strong>"
									+"<br />&nbsp;&nbsp;&nbsp;&nbsp;Haga un correcto uso de las llaves <b>{}</b> luego del nombre de la clase.";
									listaDeImpresiones.add(Statics.getHTML(mensajeError, Statics.consolaCss));
									errorEnLinea = true;
									analisisCorrecto = false;
								}
							}
							else
								errorEnLinea = false;
							tipo = -1;
							alcance = 0;
							valor = nombre = "";
							hayIgual = hayVariable = false;
						}
					}
					if(token.getValor().equals("}")) {
						ArrayList<String> listaDeIdentificadoresABorrar = new ArrayList<String>();
						for(Identificador id: identificadoresReales.values())
							if(id.getAlcance() == alcanceLocal.peek()) {
								listaDeIdentificadoresCompleta.add(id);
								listaDeIdentificadoresABorrar.add(id.getNombre());
							}
						for(String identificador: listaDeIdentificadoresABorrar)
							identificadoresReales.remove(identificador);
						alcanceLocal.pop();
					}
				}
				continue;
			}
			if(token.getTipo() == Statics.claseInt) {
				if(tipo < 0) {
					tipo = Statics.getTipoDeConstante("class"); // no es una constante, sino declaración de clase, pero me es más cómodo usar esto así
				}
				else {
					errorEnLinea = true;
					analisisCorrecto = false;
					ultimaLineaDeError = token.getLinea();
					mensajeSeEsperaba(i);
				}
				continue;
			}
		}
		
		if(tipo < 4 && !(tipo < 0 && valor.length() == 0 && nombre.length() == 0)) { // si hay una última declaración a la cual no se le puso ; pero de hecho estaba correcta, se hace
			Token ultimoToken = tokens.get(tokens.size()-1);
			tokens.add(new Token(ultimoToken.getLinea(), Statics.signoInt, tokens.size(), ";"));
			if(creaNuevoSimbolo(tokens.size()-1, errorEnLinea))
				contadorDeIdentificadores++;
			}

		if(tipo >= 4) {
			String mensajeError = "<p>Error en la linea "+tokens.get(tokens.size()-1).getLinea()+": Clase mal definida.";
			listaDeImpresiones.add(Statics.getHTML(mensajeError, Statics.consolaCss));
			analisisCorrecto = false;
		}
		
		for(Identificador identificador: identificadoresReales.values()) {
			if(!listaDeIdentificadoresCompleta.contains(identificador)) {
				listaDeIdentificadoresCompleta.add(identificador);
			}
		}
		actualizaTabla();
		if(analisisCorrecto)
			listaDeImpresiones.add(Statics.getHTML("<var>Código sin errores semánticos.", Statics.consolaCss));
		return analisisCorrecto;
	}
	
	private static Token resuelveExpresionAlgebraica(ArbolBinario<Token> arbol) {
		Object [] obj = new Object[6]; // 0: valor, 1: tipo, 2: ultimo operador, 3: tabla, 4: tabla en HTML, 5: indice
		System.out.println("\n\n\n");
		String expresion = retornaExpresionAritmetica(arbol.getRaiz(), "");
		obj = resuelveExpresionAlgebraica(arbol.getRaiz(), obj);
		if(obj != null) {
			String siguienteLinea = "";
			for (int i = 0; i < 65; i++) {
				if(i == 0)
					siguienteLinea+= "";
				else if(i == 64)
					siguienteLinea+= "";
				else if(i % 16 == 0)
					siguienteLinea+= "";
				else
					siguienteLinea+= "";
			}
			obj[3] += siguienteLinea + "\n";
			obj[4] += siguienteLinea;
			String expresionEnHTML = expresion + "</strong>" + " de la <strong>linea " + arbol.getLinea() + "</strong>";
			expresionEnHTML = Statics.centrarString("Expresión: <strong>" + nombre + " = " + expresionEnHTML, 65 + 34, "&nbsp;") + "<br />";
			expresion = nombre + " = " + expresion + " de la linea " + arbol.getLinea();
			expresion = Statics.centrarString(expresion, 65);
			String resultadoHTML = "<br />" + nombre + " := temporal" + obj[5] + " := " + obj[0];
			String resultado = "\n" + nombre + " := temporal" + obj[5] + " := " + obj[0];
			listaDeCuadruplos.add(Statics.getHTML(expresionEnHTML + obj[4] + resultadoHTML + "</p>", Statics.cuadruplosCss));
			System.out.println(expresion);
			System.out.println(obj[3] + resultado);
		}
		System.out.println("\n\n\n");
		if(obj == null)
			return null;
		else {
			arbol.setValor(obj[0] + "");
			arbol.setTipo((int)obj[1]);
		}
		Token newToken = new Token(arbol.getLinea(), arbol.getTipo(), arbol.getId(), arbol.getValor());
		tokens.set(arbol.getId(), newToken);
		if(newToken.getTipo() == Statics.enteroInt)
			newToken.setValor((int)Double.parseDouble(newToken.getValor()) + "");
		return newToken;
	}
	private static Object [] resuelveExpresionAlgebraica(NodoArbolBinario<Token> nodo, Object [] array) {
		if(nodo != null) {
			Object [] auxiliar = resuelveExpresionAlgebraica(nodo.getIzquierda(), array);
			if(auxiliar != null)
				array = auxiliar;
			if(array[0] == null) {
				if(nodo.getContenido().getTipo() == Statics.operadorAritmeticoInt)
					return null;
				else {
					int tipoDeResultado;
					double valor;
					if(nodo.getContenido().getTipo() == Statics.identificadorInt) {
						if(identificadoresReales.containsKey(nodo.getContenido().getValor())) {
							tipoDeResultado = identificadoresReales.get(nodo.getContenido().getValor()).getTipo();
							tipoDeResultado = Statics.deTipoDeDatoATipoDeToken(tipoDeResultado);
						}
						else {
							String texto = "<p>Error en la linea "+nodo.getContenido().getLinea()+": la variable <strong>"+nodo.getContenido().getValor()+"</strong> no ha sido definida anteriormente";
							listaDeImpresiones.add(Statics.getHTML(texto, Statics.consolaCss));
							analisisCorrecto = false;
							return null;
						}
						valor = Double.parseDouble(identificadoresReales.get(nodo.getContenido().getValor()).getValor());
					}
					else {
						valor = Double.parseDouble(nodo.getContenido().getValor());
						tipoDeResultado = nodo.getContenido().getTipo();
					}
					array[0] = valor;
					array[1] = tipoDeResultado;
					array[3] = "";
					array[5] = 0;
					for (int i = 0; i < 65; i++) {
						if(i == 0)
							array[3]+= "";
						else if(i == 64)
							array[3]+= "";
						else if(i % 16 == 0)
							array[3]+= "";
						else
							array[3]+= "";
					}
					array[4] = "<p>" + array[3];
					array[3]+= "\n|" + Statics.centrarString("OPERANDO 1", 15) + "" +
						Statics.centrarString("OPERANDO 2", 15) + "" +
						Statics.centrarString("OPERADOR", 15) + "" +
						Statics.centrarString("RESULTADO", 15) + "\n";
					array[4]+= "<br /><strong>" + Statics.centrarString("OPERANDO 1", 15, "&nbsp;") + "</strong>" +
							"<strong>" + Statics.centrarString("OPERANDO 2", 15, "&nbsp;") + "</strong>" +
							"<strong>" + Statics.centrarString("OPERADOR", 15, "&nbsp;") + "</strong>" +
							"<strong>" + Statics.centrarString("RESULTADO", 15, "&nbsp;") + "</strong><br />";
					String siguienteLinea = "";
					for (int i = 0; i < 65; i++) {
						if(i == 0)
							siguienteLinea+= "";
						else if(i == 64)
							siguienteLinea+= "";
						else if(i % 16 == 0)
							siguienteLinea+= "";
						else
							siguienteLinea+= "";
					}
					array[3]+= siguienteLinea + "\n";
					array[4]+= siguienteLinea + "<br />";
				}
			}
			else {
				if(nodo.getLlave() % 2 == 0) { // par, operando
					Object operando1 = array[0],
							operando2;
					if(nodo.getContenido().getTipo() == Statics.identificadorInt) {
						if(!identificadoresReales.containsKey(nodo.getContenido().getValor())) {
							String texto = "<p>Error en la linea "+nodo.getContenido().getLinea()+": la variable <strong>"+nodo.getContenido().getValor()+"</strong> no ha sido definida anteriormente";
							listaDeImpresiones.add(Statics.getHTML(texto, Statics.consolaCss));
							analisisCorrecto = false;
							return null;
						}
						operando2 = Double.parseDouble(identificadoresReales.get(nodo.getContenido().getValor()).getValor());
					}
					else {
						operando2 = Double.parseDouble(nodo.getContenido().getValor());
					}
					if(nodo.getContenido().getTipo() == Statics.dobleInt)
						array[1] = Statics.dobleInt;
					switch((String)array[2]) {
					case "+":
						array[0] = (double)array[0] +  (double)operando2;
						break;
					case "-":
						array[0] = (double)array[0] -  (double)operando2;
						break;
					case "/":
						array[0] = (double)array[0] /  (double)operando2;
						break;
					case "*":
						array[0] = (double)array[0] *  (double)operando2;
						break;
					default:
						System.out.println("operador inválido, sólo se admiten +, -, / y *");
					}
					operando1 = ((int)array[5] == 0) ? operando1 : "temporal" + ((int)array[5]);
					array[5] = (int)array[5] + 1;
					array[3]+= "" + Statics.centrarString(operando1 + "", 15) + "" +
						Statics.centrarString(operando2 + "", 15) + "" +
						Statics.centrarString(array[2] + "", 15) + "" +
						Statics.centrarString("temporal" + array[5], 15) + "\n";
					array[4]+= "<em>" + Statics.centrarString(operando1 + "", 15, "&nbsp;") + "</em>" +
						"<em>" + Statics.centrarString(operando2 + "", 15, "&nbsp;") + "</em>" +
						"<em>" + Statics.centrarString(array[2] + "", 15, "&nbsp;") + "</em>" +
						"<em>" + Statics.centrarString("temporal" + array[5] + "", 15, "&nbsp;") + "</em><br />";
				}
				else { // inpar, operador
					array[2] = nodo.getContenido().getValor();
				}
			}
			auxiliar = resuelveExpresionAlgebraica(nodo.getDerecha(), array);
			if(auxiliar != null)
				array = auxiliar;
		}
		return array;
	}

	private static String retornaExpresionAritmetica(NodoArbolBinario<Token> nodo, String expresion) {
		if(nodo != null) {
			expresion = retornaExpresionAritmetica(nodo.getIzquierda(), expresion);
			expresion+= nodo.getContenido().getValor() + " ";
			expresion = retornaExpresionAritmetica(nodo.getDerecha(), expresion);
		}
		return expresion;
	}
	
	private static boolean creaNuevoSimbolo(int i, boolean errorEnLinea) {
		boolean esUnaDeclaracionVacia = false,
			esUnaAsignacionPeroNoUnaDeclaracion = false,
			todoCorrecto = true;
		if(alcance == 0) // si no se escribió el alcance
			alcance = (alcanceLocal.isEmpty() ? 0 : alcanceLocal.peek()); // si está afuera de todo, es automáticamente global, de lo contrario es automáticamente local
		if(tipo < 0) {
			if(i >= 3) {
				int tipoDelIdentificador = tipo,
					tipoDeLaAsignacion = Statics.getTipoDeConstante(tokens.get(i-1).getValor());
				if(identificadoresReales.containsKey(nombre))
					tipoDelIdentificador = identificadoresReales.get(nombre).getTipo();
				else
					todoCorrecto = false;
				if(tipoDeLaAsignacion < 0) {
					if(identificadoresReales.containsKey(tokens.get(i-1).getValor()))
						tipoDeLaAsignacion = identificadoresReales.get(tokens.get(i-1).getValor()).getTipo();
					else
						todoCorrecto = false;
				}
				boolean esUnaAsignacionCorrecta = tipoDelIdentificador == tipoDeLaAsignacion;
				if(esUnaAsignacionCorrecta && tokens.get(i-2).getValor().equals("=") && tokens.get(i-3).getTipo() == Statics.identificadorInt)
					esUnaAsignacionPeroNoUnaDeclaracion = true;
			}
			if(!esUnaAsignacionPeroNoUnaDeclaracion) {
				if(hayIgual)
					imprimirErrorDeDeclaracion(tokens.get(i).getLinea(), "tipo de dato", " "+nombre);
				todoCorrecto = false;
			}
		}
		if(nombre.length() == 0) {
			imprimirErrorDeDeclaracion(tokens.get(i).getLinea(), "nombre", "");
			todoCorrecto = false;
		}
		if(valor.length() == 0) {
			if(i > 1) {
				if(tipo >= 0 && nombre.length() > 0 && tokens.get(i-1).getTipo() == Statics.identificadorInt && tokens.get(i-2).getTipo() == Statics.tipoDeDatoInt)
					esUnaDeclaracionVacia = true;
			}
			if(!esUnaDeclaracionVacia) {
				boolean esNulo = false;
				if(i > 0)
					if(tokens.get(i-1).getTipo() == Statics.identificadorInt)
						if(identificadoresReales.containsKey(tokens.get(i-1).getValor()))
							if(identificadoresReales.get(tokens.get(i-1).getValor()).getValor().length() == 0)
								esNulo= true;
				if(esNulo) {
					String texto = "<p>Error en la linea "+tokens.get(i).getLinea()+": el variable <strong>"+tokens.get(i-1).getValor()+"</strong> no tiene asignado un valor aún, por lo"
					+ "<br />&nbsp;&nbsp;&nbsp;&nbsp;que no puede ser usado para asignarse a otra variable, como intentó con <strong>"+nombre;
					listaDeImpresiones.add(Statics.getHTML(texto, Statics.consolaCss));
					todoCorrecto = false;
				}
				else {
					if(hayIgual)
						imprimirErrorDeDeclaracion(tokens.get(i).getLinea(), "valor", " "+nombre);
					todoCorrecto = false;
				}
			}
		}
		if(todoCorrecto) {
			Identificador nuevoIdentificador;
			nuevoIdentificador = new Identificador(tokens.get(i).getLinea(), alcance, tipo, nombre, valor, contadorDeIdentificadores);
			if(!identificadoresReales.containsKey(nombre)) {
				if(!errorEnLinea) {
					if((esUnaAsignacionPeroNoUnaDeclaracion)
					|| esUnaDeclaracionVacia
					|| (tipo >= 0 && tipo <= 3 && tipo == Statics.getTipoDeConstante(valor))) {
						if(!esUnaAsignacionPeroNoUnaDeclaracion) {
							identificadoresReales.put(nombre, nuevoIdentificador);
							contadorDeIdentificadores++;
//							listaDeImpresiones.add(Statics.getHTML(nuevoIdentificador.toHTML(), Statics.consolaCss));
//							System.out.println(nuevoIdentificador);
						}
						else {
							identificadoresReales.get(nombre).setValor(valor);
						}
					}
					else {
						todoCorrecto = false;
						String texto = "<p>Error en la linea " + tokens.get(i).getLinea() + ": El identificador <strong>" + nombre + "</strong>, del tipo <i>" + Statics.tipoDeDato[tipo] + "</i> es incompatible"
							+ "<br />&nbsp;&nbsp;&nbsp;&nbsp;con el tipo de valor <i>" + Statics.tipoDeDato[Statics.getTipoDeConstante(valor)] + "</i> que se le está asignando";
						listaDeImpresiones.add(Statics.getHTML(texto, Statics.consolaCss));
					}
				}
			}
			else {
				if(esUnaAsignacionPeroNoUnaDeclaracion) {
					identificadoresReales.get(nombre).setValor(valor);
				}
				else {
					todoCorrecto = false;
					String texto = "<p>Error en la linea " + tokens.get(i).getLinea() + ": El identificador <strong>" + nombre + "</strong>, del tipo " + Statics.tipoDeDato[tipo] + ", ya existe"
						+ "<br />&nbsp;&nbsp;&nbsp;&nbsp;en la linea " + identificadoresReales.get(nombre).getPosicion() + " con el tipo de identificador <i>" + Statics.tipoDeDato[identificadoresReales.get(nombre).getTipo()];
					listaDeImpresiones.add(Statics.getHTML(texto, Statics.consolaCss));
				}
			}
		}
		return todoCorrecto;
	}
	
	private static void imprimirErrorDeDeclaracion(int linea, String error, String identificador) {
		String mensaje = "<p>Error en la linea <strong>"+linea+"</strong>"
			+ "<br />&nbsp;&nbsp;&nbsp;&nbsp;No se definió un <strong>"+error+"</strong> para el identificador" + identificador + ".";
		String htmlImpreso = Statics.getHTML(mensaje, Statics.consolaCss);
		listaDeImpresiones.add(htmlImpreso);
		analisisCorrecto = false;
	}
	
	private static void mensajeSeEsperaba(int i) {
		String seEsperaba = "Se esperaba ";
		if(i == 0)
			seEsperaba+= "una declaración de <b>clase</b> o <b>tipo de dato</b>.";
		else
			switch(tokens.get(i-1).getTipo()) {
			case Statics.tipoDeDatoInt:
				seEsperaba+= "un <b>identificador</b> luego del token <i>"+tokens.get(i-1).getValor();
				break;
			case Statics.identificadorInt:
				boolean esUnaClase = false;
					if(i > 1)
						if(tokens.get(i-2).getValor().equals("class"))
							esUnaClase = true;
				if(!esUnaClase)
					seEsperaba+= "un signo <b>=</b> luego del token <i>"+tokens.get(i-1).getValor();
				else
					seEsperaba+= "un signo <b>{</b> luego del token <i>"+tokens.get(i-1).getValor();
				break;
			case Statics.booleanoInt:
			case Statics.enteroInt:
			case Statics.dobleInt:
			case Statics.cadenaInt:
				seEsperaba+= "un signo <b>;</b> luego de la constante <i>"+tokens.get(i-1).getValor();
				break;
			case Statics.claseInt:
				seEsperaba+= "un <b>identificador</b> para nombrar la clase.";
				break;
			case Statics.llaveInt:
				seEsperaba+= "una declaración de <b>clase</b> o <b>tipo de dato</b>.";
				break;
			case Statics.parentesisInt:
				if(tokens.get(i-1).getValor().equals("("))
					seEsperaba+= "una <b>expresión lógica</b> o un <b>booleano</b> luego del <i>(";
				else
					seEsperaba+= "un <b>modificador</b>, <b>clase</b> o <b>tipo de dato</b>.";
				break;
			case Statics.operadorAritmeticoInt:
				seEsperaba+= "un <b>valor numérico</b> luego del operador <i>"+tokens.get(i-1).getValor();
				break;
			case Statics.operadorLogicoInt:
				break;
			case Statics.palabraReservadaInt: // if, while
				break;
			case Statics.signoInt: // =, ;
				break;
			default: // =, ;
				seEsperaba+= "algo luego del token <i>"+tokens.get(i-1).getValor()+", F como dicen los chavos.";
				break;
			}
		String mensajeError = "<p>Error en el token <strong>"+tokens.get(i).getValor()+"</strong> en la linea "+tokens.get(i).getLinea()
		+"<br />&nbsp;&nbsp;&nbsp;&nbsp;"+seEsperaba;
		listaDeImpresiones.add(Statics.getHTML(mensajeError, Statics.consolaCss));
	}
	
	public static void actualizaTabla() {
		Identificador [] identificador = new Identificador[listaDeIdentificadoresCompleta.size()];
		int i = 0;
		for(Identificador identificadorDesdeLaHashMap: listaDeIdentificadoresCompleta) { // recolecta los datos de la lista de identificadores completa, la cual contiene también los identificadores muertos
			identificador[i] = identificadorDesdeLaHashMap;
			i++;
		}

		DefaultTableModel modelo = new DefaultTableModel(new Object[0][0], cabecera);
		tabla.setModel(modelo);
		// y mete los datos obtenidos, ya una vez ordenados ordena los datos obtenidos
		Identificador [] tablaOrdenada = new Identificador[identificador.length];
		for(i = 1; i <= identificador.length; i++) {
			for(int j = 0; j < identificador.length; j++)
				if(identificador[j].getId() == i) {
					Identificador id  = tablaOrdenada[i-1] = identificador[j];
					int alcanceIndex = (id.getAlcance() < 2 ? id.getAlcance() : 1);
					Object datosTabla[]= {id.getPosicion(), Statics.alcance[alcanceIndex], Statics.tipoDeDato[id.getTipo()], id.getNombre(), id.getValor()};
					modelo.addRow(datosTabla);
					break;
				}
		}
		
			Font fontHeader = new Font("Arial", Font.BOLD, 17);
			Font fontTable = new Font("Arial", Font.PLAIN, 17);
			tabla.getTableHeader().setFont(fontHeader);
			tabla.setFont(fontTable);
			
			// hacer que no se pueda enfocar, ni modificar por el usuario
			tabla.setEnabled(false);
			tabla.setFocusable(false);

			@SuppressWarnings("serial")
			DefaultTableCellRenderer celdasCentradas = new DefaultTableCellRenderer() {
				@Override
				public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
																boolean hasFocus, int row, int column) {
					Component celda = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
					return celda;
				}
			};
			celdasCentradas.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
			for(i = 0; i < tabla.getColumnModel().getColumnCount(); i++) {
				tabla.getColumnModel().getColumn(i).setCellRenderer(celdasCentradas);
			}
	}

}
