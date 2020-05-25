package misc;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Statics {
	///////////////////////////////////////////////////////////////////////////////////
	///////////////////////////   CONSTANTES DEL PROGRAMA
	///////////////////////////////////////////////////////////////////////////////////
	public final static int
		palabraReservadaInt = 0,
		tipoDeDatoInt = 1,
		signoInt = 2,
		operadorLogicoInt = 3,
		operadorAritmeticoInt = 4,
		booleanoInt = 5,
		claseInt = 6,
		identificadorInt = 7,
		cadenaInt = 8,
		enteroInt = 9,
		parentesisInt = 10,
		llaveInt = 11,
		dobleInt = 12;
	public final static String [] tipoDeToken = {
		"Palabra resevada", // 0
		"Tipo de dato", // 1
		"Signo", // 2
		"Operador logico", // 3
		"Operador aritmetico", // 4
		"Booleano", // 5
		"Clase", // 6
		"Identificador", // 7
		"Cadena", // 8
		"Entero", // 9
		"Parentesis", // 10, ()
		"Llave", // 11, {}
		"Doble", // 12
	};
	public final static String[]
		alcance = {"global", "local"},
		palabraReservada = {"if","while"},
		tipoDeDato = {"int", "string", "boolean", "double", "class"}, // de dato y de otra cosas, por comodidad mía xd
		signo = {"=",";"},
		operadorLogico = {"<","<=",">",">=","==","!=","&&","||"},
		operadorAritmetico = {"+","-","*","/"},
		booleano = {"true","false"},
		parentesis = {"(", ")"},
		llave = {"{", "}"};
	public final static String consolaCss =
			"strong {" // para destacar el token, en el error
			+ "	font-style: italic;"
			+ "}"
			+ "p {" // para el error
			+ "	color: #DD0000"
			+ "}"
			+ "em {" // para el warning
			+ "	color: #888800"
			+ "}"
			+ "var {" // para lo verde bonito acá bien
			+ "	color: #008800"
			+ "}";
	
	///////////////////////////////////////////////////////////////////////////////////
	//////////////////////////   FUNCIONES DE ANALIZADORES
	///////////////////////////////////////////////////////////////////////////////////
	public static ArrayList<String> deArrayEstaticaADinamica(String [] estatica) {
		ArrayList<String> dinamica = new ArrayList<String>();
		for(String s: estatica)
			dinamica.add(s);
		return dinamica;
	}

	public static String [] deArrayDinamicaAEstatica(ArrayList<String> dinamica) {
		String [] estatica = new String[dinamica.size()];
		for(int i=0; i<dinamica.size(); i++)
			estatica[i] = dinamica.get(i);
		return estatica;
	}
	
	public static boolean esEntero(String token) {
		String caracter;
		for(int i=0; i<token.length(); i++) {
			caracter = token.charAt(i) + "";
			// si un caracter del String no es un valor del 0 al 9 retorna false
			if(caracter.hashCode()<48 || caracter.hashCode()>57)
				return false;
		}
		return true;
	}
	public static boolean esDoble(String token) {
		try {
			Double.parseDouble(token);
		} catch(Exception e) {
			return false;
		}
		return true;
	}
	
	public static boolean esConstante(String token) {
		if(Statics.esEntero(token) || token.endsWith("\"") && token.startsWith("\"")
		|| "true".equals(token) || "false".equals(token) || Statics.esDoble(token))
			return true; // entero
		return false;
	}
	
	public static int getTipoDeConstante(String str) {
		if(Statics.esEntero(str))
			return 0; // entero
		if(str.endsWith("\"") && str.startsWith("\""))
			return 1; // string
		if("true".equals(str) || "false".equals(str))
			return 2; // booleano
		if(Statics.esDoble(str))
			return 3; // double
		if(str.equals("class"))
			return 4; // clase
		if(str.equals("function"))
			return 5; // funcion
		return -1;
	}
	
	///////////////////////////////////////////////////////////////////////////////////
	////////////////////////////   FUNCIONES DE INTERFAZ   
	///////////////////////////////////////////////////////////////////////////////////
	public static void guardarArchivo(String ruta, String texto) {
		try {
			FileWriter archivo = new FileWriter(ruta);
			PrintWriter escritor = new PrintWriter(archivo);
			escritor.println(texto);
			archivo.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static String getHTML(String body, String css) {
		String output =	
				"<html>"
					+"<head>"
						+"<style>"
							+ css
						+"</style>"
					+"</head>"
					+"<body>"
						+ body
					+"</body>"
				+"</html>";
		return output;
	}
}