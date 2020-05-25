package interfaz;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

import compilador.AnalizadorLexico;
import compilador.AnalizadorSemantico;
import compilador.AnalizadorSintactico;
import compilador.Identificador;
import compilador.Token;
import misc.Statics;

@SuppressWarnings("serial")
public class Escuchadores implements Serializable, ActionListener, KeyListener, WindowListener, AncestorListener, MouseListener {
	private Vista vista;
	public boolean teclaControl = false,
		teclaShift = false,
		teclaAlt = false;
	public Escuchadores(Vista vista) {
		this.vista = vista;
	}
	private void modificaTitulos() {
		String consola = "Consola", datos = "Datos";
		ImageIcon icono = vista.icoDone;
		if(vista.bottomTabs.getSelectedIndex()==1)
			consola+= "*";
		else
			datos+= "*";
		if(vista.hayError)
			icono = vista.icoError;
		vista.bottomTabs.setIconAt(0, icono);
		vista.bottomTabs.setTitleAt(0, consola);
		vista.bottomTabs.setIconAt(1, icono);
		vista.bottomTabs.setTitleAt(1, datos);
		
		int tab = vista.getSelectedTab();
		if(vista.hayError)
			vista.titulo.getByIndex(tab).dato.setIcon(icono);
		else
			vista.titulo.getByIndex(tab).dato.setIcon(vista.icoCode);
	}
	private void cerrarPestaña() {
		if(teclaControl) {
			vista.cerrarPestaña();
		}
	}
	@Override
	public void keyPressed(KeyEvent key) {
		if(key.getKeyCode() == KeyEvent.VK_ALT) {
			key.consume();
			teclaAlt = true;
			return;
		}
		teclaAlt = false;
		switch(key.getKeyCode()) {
			case KeyEvent.VK_S:
				if(teclaControl) {
					if(teclaShift)
						vista.guardarComo.doClick();
					else
						vista.guardarArchivo.doClick();
					teclaControl = false;
					teclaShift = false;
				}
				break;
			case KeyEvent.VK_O:
				if(teclaControl) {
					vista.cargarArchivo.doClick();
					teclaControl = false;
					teclaShift = false;
				}
				break;
			case KeyEvent.VK_F2:
				vista.bottomTabs.setSelectedIndex(0);
				break;
			case KeyEvent.VK_F3:
				vista.bottomTabs.setSelectedIndex(1);
				break;
			case KeyEvent.VK_F5:
				vista.compilarCodigo.doClick();
				teclaControl = false;
				teclaShift = false;
				break;
			case KeyEvent.VK_ESCAPE:
				if(vista.getExtendedState() == Frame.MAXIMIZED_BOTH) {
					vista.setExtendedState(Frame.NORMAL);
		        }
				else {
		            vista.setExtendedState(Frame.MAXIMIZED_BOTH);
		        }
				break;
			case KeyEvent.VK_N:
				if(teclaControl) {
					vista.nuevoArchivo.doClick();
				}
				break;
			case KeyEvent.VK_W:
				if(teclaControl) {
					cerrarPestaña();
				}
				break;
			case KeyEvent.VK_PERIOD:
				if(teclaControl)
					if(vista.codigoTabs.getTabCount()>2)
						if(vista.getSelectedTab()==vista.codigoTabs.getTabCount()-2)
							vista.codigoTabs.setSelectedIndex(0);
						else
							vista.codigoTabs.setSelectedIndex(vista.getSelectedTab()+1);
				break;
			case KeyEvent.VK_COMMA:
				if(teclaControl)
					if(vista.codigoTabs.getTabCount()>2) {
						if(vista.getSelectedTab()==0)
							vista.codigoTabs.setSelectedIndex(vista.codigoTabs.getTabCount()-2);
						else
							vista.codigoTabs.setSelectedIndex(vista.getSelectedTab()-1);
					}
				break;
			case KeyEvent.VK_ENTER:
				if(teclaControl && teclaShift)
					if(vista.bottomTabs.getSelectedIndex() < vista.bottomTabs.getTabCount()-1)
						vista.bottomTabs.setSelectedIndex(vista.bottomTabs.getSelectedIndex()+1);
					else
						vista.bottomTabs.setSelectedIndex(0);
				else
					for(int i=0; i<vista.tabulaciones; i++) {
						final JTextPane aux = vista.txtCodigo.getByIndex(vista.getSelectedTab()-1).dato;
						aux.setText(aux.getText()+"\t");
					}
				break;
			case KeyEvent.VK_CONTROL:
				teclaControl = true;
				break;
			case KeyEvent.VK_SHIFT:
				teclaShift = true;
				break;
			/*case KeyEvent.VK_ADD:
				System.out.println("Datos: \n"
				+ "[\n"
				+ "\ttitulos: " + vista.titulo.length() + "\n"
				+ "\trutas de archivo actual: " + vista.rutaDeArchivoActual.length() + "\n"
				+ "\tnombres de archivos: " + vista.nombreDelArchivo.length() + "\n"
				+ "\ttitulos de ventana: " + vista.tituloVentana.length() + "\n"
				+ "\ttamaños de textos del editor: " + vista.tamañoTextoDelEditor.length() + "\n"
				+ "\tcambios guardados: " + vista.cambiosGuardados.length() + "\n"
				+ "]");
				break;
				/**/
		}
	}
	@Override
	public void keyReleased(KeyEvent key) {
		final int tabs = vista.getSelectedTab();
		if(key.getSource() == vista.txtCodigo.getByIndex(tabs).dato) { // Si estás en el editor de código
			if(vista.txtCodigo.getByIndex(tabs).dato.getText().length() != vista.tamañoTextoDelEditor.getByIndex(tabs).dato) { // entra sólo si escribiste sobre txtCodigo
				if(vista.rutaDeArchivoActual.getByIndex(tabs).dato.length() == 0)
					vista.setTitle("*archivo no guardado - Compilador");
				else
					vista.setTitle("*" + vista.nombreDelArchivo.getByIndex(tabs).dato + " - " + vista.rutaDeArchivoActual.getByIndex(tabs).dato + " - Compilador");
				vista.tituloVentana.getByIndex(tabs).dato = vista.getTitle();
				vista.codigoTabs.setTitleAt(vista.getSelectedTab(), "*" + vista.nombreDelArchivo.getByIndex(tabs).dato);
				vista.tamañoTextoDelEditor.getByIndex(tabs).dato = vista.txtCodigo.getByIndex(tabs).dato.getText().length();
				vista.cambiosGuardados.getByIndex(tabs).dato = false;
			}
		}
		switch(key.getKeyCode()) {
			case KeyEvent.VK_ALT:
				key.consume();
				if(!teclaAlt)
					return;
				if(vista.barraDelMenu.isFocusOwner())
					vista.txtCodigo.getByIndex(vista.getSelectedTab()).dato.requestFocus();
				else
					vista.menuArchivo.doClick();
				break;
			case KeyEvent.VK_CONTROL:
				teclaControl = false;
				break;
			case KeyEvent.VK_SHIFT:
				teclaShift = false;
				break;	
		}
	}
	@Override
	public void keyTyped(KeyEvent key) { // sólo para teclas de escritura; como letras, números, símbolos, etc. No jala con teclas como control, alt, fin, etc.
		int tabs = vista.codigoTabs.getSelectedIndex();
		String archivoNombre = vista.nombreDelArchivo.getByIndex(tabs).dato;
		if(teclaControl)
			return;
		vista.titulo.getByIndex(tabs).dato.setTitle("*" + archivoNombre);
	}
	@Override
	public void actionPerformed(ActionEvent evt) {
		if(evt.getSource() == vista.nuevoArchivo) {
			vista.nuevaPestaña();
			return;
		}
		if(evt.getSource() == vista.cargarArchivo) {
			vista.cargarArchivo();
			return;
		}
		if(evt.getSource() == vista.guardarArchivo) {
			if(vista.rutaDeArchivoActual.getByIndex(vista.getSelectedTab()).dato.length() == 0)
				vista.guardarArchivoComo();
			else {
				vista.guardarArchivo(vista.rutaDeArchivoActual.getByIndex(vista.getSelectedTab()).dato);
			}
			return;
		}
		if(evt.getSource() == vista.guardarComo) {
			vista.guardarArchivoComo();
			return;
		}
		if(evt.getSource() == vista.salir) {
			System.exit(0);
		}
		if(evt.getSource() == vista.compilarCodigo) {
			for(int i=0; i<50; i++)
				System.out.print("\n");
			int tabs = vista.getSelectedTab();
			String codigoAGuardar = vista.txtCodigo.getByIndex(tabs).dato.getText();
			Statics.guardarArchivo(vista.archivoTemporal.getAbsolutePath(), codigoAGuardar);
			ArrayList<Token> tokens = new ArrayList<Token>();
			ArrayList<String> listaDeImpresiones = new ArrayList<String>();
			boolean analisisCorrecto = AnalizadorLexico.analizaCodigoDesdeArchivo(listaDeImpresiones, tokens, vista.archivoTemporal.getAbsolutePath());
			if(analisisCorrecto && codigoAGuardar.length() > 0) {
				analisisCorrecto = AnalizadorSintactico.parentesisCorrectos(tokens, listaDeImpresiones);
			}
			if(analisisCorrecto && codigoAGuardar.length() > 0) {
				analisisCorrecto = AnalizadorSemantico.analizarTokens(listaDeImpresiones, tokens, new HashMap<String, Identificador>(), vista.tablaDatos, vista.tituloTabla);
			}
			vista.consola.setListData(Statics.deArrayDinamicaAEstatica(listaDeImpresiones));
			
			vista.hayError = !analisisCorrecto;
			modificaTitulos();
			return;
		}
	}
	public void windowActivated(WindowEvent e) {
	}
	@Override
	public void windowClosed(WindowEvent e) {
	}
	@Override
	public void windowClosing(WindowEvent e) {
		int tabs = 0, archivosSinGuardar=0;
		boolean hayArchivosSinGuardar = false;
		if(vista.codigoTabs.getTabCount() == 2 && !vista.cambiosGuardados.getByIndex(0).dato) {
			hayArchivosSinGuardar = true;
			archivosSinGuardar++;
		}
		else
			while(vista.codigoTabs.getTabCount() > tabs+1) {
				if(!vista.cambiosGuardados.getByIndex(tabs).dato) {
					hayArchivosSinGuardar = true;
					archivosSinGuardar++;
				}
				tabs++;
			}
		if(hayArchivosSinGuardar) {
			String texto = "<div>";
			if(archivosSinGuardar == 1)
				texto+= "No ha guardado los cambios realizados en <b>un archivo</b>";
			else
				texto+= "No ha guardado los cambios realizados en <b>" + archivosSinGuardar + " archivos</b>";
			texto+= "<br />¿Desea guardar los cambios antes de salir?";
			
			teclaShift = teclaAlt = teclaControl = false;
			String css = 
					"div {"
					+ "	text-align: center;"
					+ "	font-weight: plain;"
					+ "}";
			texto = Statics.getHTML(texto, css);
			int opcion = JOptionPane.showConfirmDialog(null, new JLabel(texto), "Cambios no guardados", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
			if(opcion == JOptionPane.YES_OPTION)
				vista.guardarArchivo.doClick();
			else
				if(opcion == JOptionPane.NO_OPTION) {
					// vista.guardarSesion();
					System.exit(0);
				}
		}
		else {
			// vista.guardarSesion();
			System.exit(0);
		}
	}
	@Override
	public void windowDeactivated(WindowEvent e) {
	}
	@Override
	public void windowDeiconified(WindowEvent e) {
	}
	@Override
	public void windowIconified(WindowEvent e) {
	}
	@Override
	public void windowOpened(WindowEvent e) {
	}

	@Override
	public void ancestorAdded(AncestorEvent a) {
		Object consola = vista.consola,
			datos = vista.tablaDatos;
		if(a.getSource().equals(consola) || a.getSource().equals(datos))
			if(vista.hayError) {
				vista.bottomTabs.setIconAt(0, vista.icoError);
				vista.bottomTabs.setTitleAt(0, "Consola");
				vista.bottomTabs.setIconAt(1, vista.icoError);
				vista.bottomTabs.setTitleAt(1, "Datos");
			}
			else {
				vista.bottomTabs.setIconAt(0, vista.icoBug);
				vista.bottomTabs.setTitleAt(0, "Consola");
				vista.bottomTabs.setIconAt(1, vista.icoTable);
				vista.bottomTabs.setTitleAt(1, "Datos");
			}

		int tabs = vista.getSelectedTab();
		vista.setTitle(vista.tituloVentana.getByIndex(tabs).dato);
		if(!a.getSource().equals(consola) && !a.getSource().equals(datos))
			vista.actualizarBotonesDePestañas();
	}
	@Override
	public void ancestorMoved(AncestorEvent a) {
	}
	@Override
	public void ancestorRemoved(AncestorEvent a) {
	}
	
	
	@Override
	public void mouseClicked(MouseEvent e) {
	}
	@Override
	public void mouseEntered(MouseEvent e) {
	}
	@Override
	public void mouseExited(MouseEvent e) {
	}
	@Override
	public void mousePressed(MouseEvent e) {
		if(vista.getSelectedTab()==vista.codigoTabs.getTabCount()-1)
			vista.nuevoArchivo.doClick();
	}
	@Override
	public void mouseReleased(MouseEvent e) {
	}
}