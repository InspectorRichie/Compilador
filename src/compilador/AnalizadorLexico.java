package compilador;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;
import java.util.StringTokenizer;

import javax.swing.JOptionPane;

import misc.Statics;

public class AnalizadorLexico {
	public static boolean analizaCodigoDesdeArchivo(ArrayList<String> listaDeImpresiones, ArrayList<Token> listaDeTokens, String ruta) {//Recibe la ruta del archivo
		String lineaDeTexto="", token="";
		int linea = 1, id = 1;
		StringTokenizer tokenizer;
		boolean analisisCorrecto = true;
		boolean vacio = true;
		try {
			FileReader file = new FileReader(ruta); // Abro el archivo
			BufferedReader archivoEntrada = new BufferedReader(file); // Abro el lector del archivo
			lineaDeTexto = archivoEntrada.readLine(); // Saco la linea
			while (lineaDeTexto != null){ // Recorro el archivo
				lineaDeTexto = separaDelimitadores(lineaDeTexto); // Checo si en la linea hay operadores o identificadores combinados y le agrego espacios
				tokenizer = new StringTokenizer(lineaDeTexto); // Separo la linea en sus distintos listaDeTokens
				while(tokenizer.hasMoreTokens()) {
					vacio = false;
					token = tokenizer.nextToken();
					boolean bandera = analizarToken(token, linea, id++, listaDeImpresiones, listaDeTokens);//Y lo mando a analizar
					if(analisisCorrecto)
						analisisCorrecto = bandera; // si analisisCorrecto ya salió false una sola vez, ya no puede cambiarse
					if(token.equals("//"))
						break;
				}
				lineaDeTexto=archivoEntrada.readLine();
				linea++;//Cuento el renglon
			}
			archivoEntrada.close();
			if(vacio) {
				String texto = Statics.getHTML("<var><b>Código vacío", Statics.consolaCss);
				listaDeImpresiones.add(texto);
				System.out.println("Codigo vacío.");
			}
		} catch (FileNotFoundException e) {
			String texto = Statics.getHTML("No se encontro el archivo favor de checar la ruta <u>"+ruta, "");
			JOptionPane.showMessageDialog(null,texto,"Error al leer el código",JOptionPane.ERROR_MESSAGE);
			System.out.println("Archivo de código no encontrado para su análisis léxico.");
			return false;
		}
		catch(IOException e) {
			System.out.println("Algo falló en el análisis léxico al leer el código.");
			return false;
		}
		if(analisisCorrecto && !vacio)
			listaDeImpresiones.add(Statics.getHTML("<var><b>Código sin errores léxicos.", Statics.consolaCss));
		return analisisCorrecto;
	}
	
	public static boolean analizarToken(String strToken, int linea, int id, ArrayList<String> listaDeImpresiones, ArrayList<Token> listaDeTokens) {
		int tipo=-1;
		if(Statics.deArrayEstaticaADinamica(Statics.palabraReservada).contains(strToken)) 
			tipo = Statics.palabraReservadaInt; // Es una palabra reservada
		else if(strToken.equals("class"))
			tipo = Statics.claseInt; // Es una clase
		else if(Statics.deArrayEstaticaADinamica(Statics.tipoDeDato).contains(strToken)) 
			tipo = Statics.tipoDeDatoInt; // Es un tipo de datos
		else if(Statics.deArrayEstaticaADinamica(Statics.signo).contains(strToken)) 
			tipo = Statics.signoInt; // Es un simbolo
		else if(Statics.deArrayEstaticaADinamica(Statics.operadorLogico).contains(strToken)) 
			tipo = Statics.operadorLogicoInt; // Es un operador logico
		else if(Statics.deArrayEstaticaADinamica(Statics.operadorAritmetico).contains(strToken))
			tipo = Statics.operadorAritmeticoInt; // Es un operador aritmetico
		else if(Statics.deArrayEstaticaADinamica(Statics.booleano).contains(strToken)) 
			tipo = Statics.booleanoInt; // Es una constante booleana
		else if(Statics.esEntero(strToken)) 
			tipo = Statics.enteroInt; // Es un número entero
		else if(Statics.esDoble(strToken)) 
			tipo = Statics.dobleInt; // Es un número doble
		else if(Statics.deArrayEstaticaADinamica(Statics.parentesis).contains(strToken))
			tipo = Statics.parentesisInt; // Es un parentesis
		else if(Statics.deArrayEstaticaADinamica(Statics.llave).contains(strToken))
			tipo = Statics.llaveInt; // Es una llave
		else if("//".equals(strToken))
			return true;
		
		if(tipo==-1) { // No es ninguna de arriba
			// Si entra aquí quiere decir que no es ninguna de las anteriores y paso analizarla letra por letra
			String caracter = "";
			boolean bandera = true;
			for(int i=0; i < strToken.length(); i++) {
				caracter = strToken.charAt(i) + "";
				//			 a				  -			z					,			A				-		   Z				 ,   á é í ó ú Á É Í Ó Ú ñ Ñ                        "
				if(!(caracter.hashCode() >= 97 && caracter.hashCode()<=122) && !(caracter.hashCode() >= 65 && caracter.hashCode()<=90) && !esCaracterDelEspañol(caracter) && caracter.hashCode()!=34 && !Statics.esEntero(caracter) && !caracter.equals("_") && !caracter.equals("-")) {
					bandera = false;
					break;
				}
			}
			if(bandera)
				if(!strToken.startsWith("\"") && !strToken.endsWith("\""))
					tipo = Statics.identificadorInt; // Es un identificador
				else if(strToken.startsWith("\"") && strToken.endsWith("\""))
					tipo = Statics.cadenaInt; // Es una cadena de caracteres
				else
					bandera = false;
			if(!bandera) {
				String tab = "&nbsp&nbsp&nbsp&nbsp ";
				String texto = Statics.getHTML("<p>Error léxico en el token <strong>"+strToken+"</strong> de la linea <b>"+linea+"</b>."
					+ "<br />"+tab+"Caracter <b>"+caracter+"</b> no válido en el compilador.",  Statics.consolaCss);
				listaDeImpresiones.add(texto); //Es un error y guardo el donde se produjo el error
				System.out.println("Error léxico: se encontró un caracter inválido para el compilador.");
				return false;
			}
		}
		
		// todo bien, todo nice, entonces creas un token y lo añades a la lista de listaDeTokens
		Token newToken = new Token(linea, tipo, id, strToken);
		listaDeTokens.add(newToken);
//		listaDeImpresiones.add(Statics.getHTML(newToken.toHTML(), Statics.consolaCss));
//		System.out.println(newToken);
		return true;
	}
	
	private static boolean esCaracterDelEspañol(String caracter) {
		//                           á    é    í    ó    ú    Á    É    Í    Ó    Ú    ñ    Ñ
		int [] caracteresValidos = {225, 233, 237, 243, 250, 193, 201, 205, 211, 218, 241, 209};
		for(int i=0; i<caracteresValidos.length; i++)
			if(caracter.hashCode() == caracteresValidos[i])
				return true;
		return false;
	}
	
	public static String separaDelimitadores(String linea){ // mete espacios entre los >, <, !=, <=, >=, ==, = y 
		for(int i=0; i<linea.length(); i++)
			if(linea.charAt(i) == '=') {
				// añade un espacio atrás, si lo que tienes atrás no es =, !, < o >
				if(i > 0)
					if(linea.charAt(i-1) != '=' && linea.charAt(i-1) != '!' && linea.charAt(i-1) != '<' && linea.charAt(i-1) != '>') {
						linea = linea.substring(0, i) + " " + linea.substring(i++, linea.length()); // incrementa en uno i, para evadir el espacio añadido
					}
				// añade un espacio al frente, si lo que tienes al frente no es otro =
				if(linea.charAt(i+1) != '=') {
					linea = linea.substring(0, i+1) + " " + linea.substring(1+i++, linea.length()); // incrementa en uno i, para evadir el espacio añadido
				}
			}
			else if(linea.charAt(i) == '!' || linea.charAt(i) == '<' || linea.charAt(i) == '>') {
				linea = linea.substring(0, i) + " " + linea.substring(i++, linea.length()); // incrementa en uno i, para evadir el espacio añadido
				if(linea.length() > i+1)
					if(linea.charAt(i+1) != '=') {
						linea = linea.substring(0, i+1) + " " + linea.substring(1+i++, linea.length()); // incrementa en uno i, para evadir el espacio añadido
					}
			}
			else if("(){}[];/".contains(linea.charAt(i)+"")) {
				if(i > 0)
					if("/".equals(linea.charAt(i-1)+"")) {
						linea = linea.substring(0, i+1) + " " + linea.substring(1+i++, linea.length());
						continue;
					}
				if(i < linea.length()-1)
					if("/".equals(linea.charAt(i+1)+"")) {
						linea = linea.substring(0, i) + " " + linea.substring(i++, linea.length());
						continue;
					}
				linea = linea.substring(0, i) + " " + linea.charAt(i) + " " + linea.substring(1+i, linea.length());
				i+=2; // incrementa en dos i, para evadir los dos espacios añadidos
			}
		return linea;
	}
}