package estructuraDeDatos;

import compilador.Token;

public class ArbolBinario <T> extends Token {
	private NodoArbolBinario<T> raiz;

	public ArbolBinario(int linea, int tipo, int id) {
		super(linea, tipo, id, "Por asignar");
		raiz = null;
	}

	public void insertar(int llave, T contenido) {
		NodoArbolBinario<T> nuevoNodo = new NodoArbolBinario<T>(llave, contenido);

		if(raiz == null)
			raiz = nuevoNodo;
		else {
			NodoArbolBinario<T> auxiliar = raiz;
			while(auxiliar!=null) {
				nuevoNodo.setPadre(auxiliar);
				if(nuevoNodo.getLlave() >= auxiliar.getLlave()) {
					auxiliar = auxiliar.getDerecha();
				}
				else {
					auxiliar = auxiliar.getIzquierda();
				}
			}

			if(nuevoNodo.getLlave() < nuevoNodo.getPadre().getLlave()) {
				nuevoNodo.getPadre().setIzquierda(nuevoNodo);
			}
			else {
				nuevoNodo.getPadre().setDerecha(nuevoNodo);
			}
		}
	}

	public boolean borrar(int llave) {
		if(llave == raiz.getLlave()) {
			raiz = null;
			return true;
		}
		NodoArbolBinario<T> nodo = raiz;
		boolean derecha = false;
		while(llave != nodo.getLlave() && nodo != null) {
			if(llave < nodo.getLlave()) {
				nodo = nodo.getIzquierda();
				derecha = false;
			}
			else if(llave > nodo.getLlave()) {
				nodo = nodo.getDerecha();
				derecha = true;
			}
		}
		if(llave == nodo.getLlave()) {
			if(derecha)
				nodo.getPadre().setDerecha(null);
			else
				nodo.getPadre().setIzquierda(null);
			return true;
		}
		return false;
	}

	
	public NodoArbolBinario<T> getByIndex(int index) {
		return getByIndex(raiz, index);
	}
	public NodoArbolBinario<T> getByIndex(NodoArbolBinario<T> raiz, int index) {
		if(raiz == null)
			return null;
		NodoArbolBinario<T> auxiliar = raiz;
		while(index != auxiliar.getLlave()) {
			if(index > auxiliar.getLlave())
				auxiliar = auxiliar.getDerecha();
			else
				auxiliar = auxiliar.getIzquierda();
			if(auxiliar == null)
				return null;
		}
		return auxiliar;
	}

	public NodoArbolBinario<T> getByContent(T contenido) {
		return getByContent(raiz, raiz, contenido);
	}
	public NodoArbolBinario<T> getByContent(NodoArbolBinario<T> raiz, T contenido) {
		return getByContent(raiz, raiz, contenido);
	}
	private NodoArbolBinario<T> getByContent(NodoArbolBinario<T> nodo, NodoArbolBinario<T> nodoCorrecto, T contenido) {
		if(nodo != null) {
			if(nodo.getContenido().equals(contenido))
				return nodo;
			nodoCorrecto = getByContent(nodo.getIzquierda(), contenido);
			if(nodoCorrecto != null)
				if(nodoCorrecto.getContenido().equals(contenido))
					return nodoCorrecto;
			nodoCorrecto = getByContent(nodo.getDerecha(), contenido);
		}
		return nodoCorrecto;
	}

	public boolean isEmpty() {
		return raiz == null;
	}

	public NodoArbolBinario<T> getRaiz() {
		return raiz;
	}

	public void setRaiz(NodoArbolBinario<T> raiz) {
		this.raiz = raiz;
	}

	// infijo = inorden
	public void recorridoInfijo() {
		int llaveDelUltimoNodo = obtenerLlaveDelUltimoNodoDeUnSubarbol(-1, raiz),
				profundidadDeLaRaiz = profundidadDeUnNodo(raiz);
		recorridoInfijo(raiz, llaveDelUltimoNodo, profundidadDeLaRaiz);
	}
	public void recorridoInfijo(NodoArbolBinario<T> nodo) {
		int llaveDelUltimoNodo = obtenerLlaveDelUltimoNodoDeUnSubarbol(-1, nodo),
			profundidadDeLaRaiz = profundidadDeUnNodo(nodo);
		recorridoInfijo(nodo, llaveDelUltimoNodo, profundidadDeLaRaiz);
	}
	public void recorridoInfijo(NodoArbolBinario<T> nodo, int llaveDelUltimoNodo, int profundidadDeLaRaiz) {
		if(nodo != null) {
			recorridoInfijo(nodo.getIzquierda(), llaveDelUltimoNodo, profundidadDeLaRaiz);
			if(nodo.getLlave() == llaveDelUltimoNodo)
				System.out.print("└─");
			else
				System.out.print("├─");
			for(int i = 0 ; i < profundidadDeUnNodo(nodo) - profundidadDeLaRaiz; i++)
				System.out.print("────");
			System.out.println(" Llave: " + nodo.getLlave() + ", valor: " + nodo.getContenido());
			recorridoInfijo(nodo.getDerecha(), llaveDelUltimoNodo, profundidadDeLaRaiz);
		}
	}

	private int obtenerLlaveDelUltimoNodoDeUnSubarbol(int llave, NodoArbolBinario<T> nodo) {
		if(nodo != null) {
			llave = nodo.getLlave();
			llave = obtenerLlaveDelUltimoNodoDeUnSubarbol(llave, nodo.getDerecha());
		}
		return llave;
	}

	private int profundidadDeUnNodo(NodoArbolBinario<T> nodo) {
		int altura = 0;
		while(nodo!=null) {
			altura++;
			nodo = nodo.getPadre();
		}
		return altura;
	}
	public int cuentaNodos() {
		return cuentaNodos(raiz);
	}

	public int cuentaNodos(NodoArbolBinario<T> nodo) {
		return cuentaNodos(nodo, 0);
	}

	public int cuentaNodos(NodoArbolBinario<T> nodo, int contador) {
		if(nodo != null) {
			contador = cuentaNodos(nodo.getIzquierda(), contador);
			contador++;
			contador = cuentaNodos(nodo.getDerecha(), contador);
		}
		return contador;
	}
	
} 