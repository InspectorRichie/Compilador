package estructuraDeDatos;

import java.io.Serializable;

public class NodoListaDoble<E> implements Serializable {
	public E dato = null;
	int index = -1;
	public NodoListaDoble<E> siguiente = null;
	public NodoListaDoble<E> anterior = null;
	public NodoListaDoble(E dato) {
		this.dato=dato;
	}	
}