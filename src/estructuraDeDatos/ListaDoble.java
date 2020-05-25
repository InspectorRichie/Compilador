package estructuraDeDatos;

import java.io.Serializable;

public class ListaDoble<E> implements Serializable {
	private NodoDoble <E>Inicio;
	private NodoDoble <E>Fin;
	private int size=0;
	public boolean vacia() {
		return Inicio==null;
	}
	public int length(){
		return size;
	}
	public void insertar(E dato){
		NodoDoble<E>nuevo = new NodoDoble<E>(dato);
		if(vacia()) {
			Inicio=nuevo;
			Fin=nuevo;
		}
		else {
			Fin.siguiente=nuevo;
			nuevo.anterior=Fin;
		}
		Fin=nuevo;
		nuevo.index = size;
		size++;
	}
	public void mostrar() {
		NodoDoble <E>Aux=Inicio;
		while(Aux !=null){
			System.out.println(Aux);
			Aux=Aux.siguiente;
		}
	}
	public NodoDoble<E> buscar(E dato){
		NodoDoble<E> Aux=Inicio;
		while(Aux !=null){
			if(Aux.dato.equals(dato))
				return Aux;
			Aux=Aux.siguiente;
		}
		return null;
	}
	public NodoDoble<E> getFin(){
		return Fin;
	}
	public NodoDoble<E> getInicio(){
		return Inicio;
	}
	public NodoDoble<E> getByIndex(int n) {
		if(Inicio == null)
			return null;
		if(Inicio.siguiente == null)
			if(n == 0)
				return Inicio;
			else
				return null;
		if(n>size-1 || n<0)
			return null;

		NodoDoble<E> nodo = Inicio;
		for(int i=0; i<n; i++) {
			nodo = nodo.siguiente;
		}
		return nodo;
	}
	public void borrar(int n) {
		NodoDoble<E> Tra=Inicio, Ant=null;
		int cont=0, r=0; //cont indicará en que posición se encontró, r si se encontró o no el resultado
		while (Tra!=null) {
			if (cont == n) {
				r=1;
				break;
			}
			Ant=Tra;
			Tra=Tra.siguiente;
			cont++;
		}
		if (r==1) { // se encontró
			if (Inicio==Fin)
				Inicio=Fin=null;
			else
				if (Tra==Inicio)
					Inicio=Inicio.siguiente;
				else
					if (Tra==Fin) {
						Fin=Ant;
						Fin.siguiente = null;
					}
					else {
						Ant.siguiente = Tra.siguiente;
						Tra.siguiente = null;
					}
		}
		if (r==0)
			System.out.println("No se encontró.");
		size--;
	}
	public String toString() {
		String text = "\n[\n";
		NodoDoble<E> asd = Inicio;
		while(asd != null) {
			text+= "\t"+asd.dato;
			if(asd.siguiente != null)
				text+= ",\n";
			asd = asd.siguiente;
			if(asd == null)
				text+= "\n]";
		}
		return text;
	}
}