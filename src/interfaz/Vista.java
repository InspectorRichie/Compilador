package interfaz;

import java.io.Serializable;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.tools.ToolProvider;

import estructuraDeDatos.ListaDoble;
import misc.Statics;

@SuppressWarnings("serial")
public class Vista extends JFrame implements Serializable {
	public JMenuBar barraDelMenu;
	public JMenu menuArchivo,
		menuCompilar;
	public JMenuItem nuevoArchivo,
		cargarArchivo,
		guardarArchivo,
		guardarComo,
		salir,
		compilarCodigo;
	public JSplitPane dividor;
	public JTabbedPane codigoTabs,
		bottomTabs;
	public ListaDoble<JTextPane> txtCodigo;
	public File archivoTemporal;
	public ListaDoble<String> rutaDeArchivoActual,
		nombreDelArchivo,
		tituloVentana;
	public ListaDoble<Integer> tama�oTextoDelEditor;
	public ListaDoble<Boolean> cambiosGuardados;
	public ListaDoble<PanelPesta�a> titulo;
	public JList<String> consola, cuadruplos;
	public Escuchadores escuchadores;
	public int tabulaciones = 0,
		posicionRelativaDelDividorDeLosTabbedPane = 0;
	public boolean hayError = false; // hacer algo con esto despu�s, no me gusta
	public final String [] tituloTabla = new String[]{"Posici�n", "Alcance", "Tipo", "Simbolo", "Valor"};
	public DefaultTableModel modelo;
	public JTable tablaDatos;
	private final String carpetaDeAppData = System.getenv("APPDATA") + "/Cracks Code/",
		archivoUltimaSesion = carpetaDeAppData+"ultimaSesion.txt";
	public ImageIcon iconoNuevoTab,
		icoCode,
		icoBug,
		icoError,
		icoDone,
		icoTable,
		icoCerrarPesta�a;
	private int contadorDePesta�as = 0;
	private Object[] sesion;
	public Font fontHeader = new Font("Arial", Font.BOLD, 17),
			fuenteConsola = new Font("Consolas", Font.PLAIN, 16);

	public Vista() {
		super(" Compilador ");
		constructor();
	}
	
	public void constructor() {
		hazInterfaz();
		agregaEscuchadores();
	}
	
	private void hazInterfaz() {
		setSize(1200, 950);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setMinimumSize(new Dimension(700, 600));

		barraDelMenu = new JMenuBar();
		setJMenuBar(barraDelMenu);
		creaMenu();
		crearIconos();
		codigoTabs = new JTabbedPane();
		codigoTabs.insertTab("", iconoNuevoTab, null, "Nueva pesta�a", codigoTabs.getTabCount());
		codigoTabs.setSelectedIndex(codigoTabs.getTabCount()-1);
		creaFicheroTemporal();
			titulo = new ListaDoble<PanelPesta�a>();
			rutaDeArchivoActual = new ListaDoble<String>();
			nombreDelArchivo = new ListaDoble<String>();
			tituloVentana = new ListaDoble<String>();
			tama�oTextoDelEditor = new ListaDoble<Integer>();
			cambiosGuardados = new ListaDoble<Boolean>();
			txtCodigo = new ListaDoble<JTextPane>();
			nuevaPesta�a();
			
		crearPesta�asDeAbajo();
		
		setVisible(true);

		setTitle(tituloVentana.getByIndex(codigoTabs.getSelectedIndex()).dato);

	}
	
	private void crearIconos() {
		iconoNuevoTab = new ImageIcon("src/images/pesta�aNueva.png");
		icoCode = new ImageIcon();
		icoBug = new ImageIcon();
		icoError = new ImageIcon();
		icoDone = new ImageIcon();
		icoTable = new ImageIcon();
		icoCerrarPesta�a = new ImageIcon("src/images/cerrarPesta�a.png");
	}

	@SuppressWarnings({ "unchecked", "unused" }) // unused pero de momento xd
	private boolean reanudarSesionAnterior() {
		sesion = new Object[8];
		try {
	    	// verificar si la carpeta existe
        	if(!Files.exists(Paths.get(carpetaDeAppData))) {
        		Files.createDirectory(Paths.get(carpetaDeAppData)); // si no existe, se crea
        	}
        	// verificar si el archivo existe
        	File fichero = new File(archivoUltimaSesion);
	    	if(!fichero.exists()) {
	    		return false; // si no existe, VETE YA, SI NO ENCUENTRAS MOTIIIVOOOOS
	    	}
	    	FileInputStream fis = new FileInputStream(fichero.getAbsoluteFile());
	    	ObjectInputStream ois = new ObjectInputStream(fis);
			sesion = (Object[]) ois.readObject();
			ois.close();
			fis.close();
		} catch (ClassNotFoundException | IOException | NullPointerException e) {
			System.out.println("Hubo un problema al leer los datos de la �ltima sesi�n.");
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			System.out.println("Hubo un problema al leer los datos de la �ltima sesi�n."
					+ "\nNi idea de porque ocurri� este problema.");
			e.printStackTrace();
			return false;
		}
		
		titulo = (ListaDoble<PanelPesta�a>)sesion[0];
		rutaDeArchivoActual = (ListaDoble<String>)sesion[1];
		nombreDelArchivo = (ListaDoble<String>)sesion[2];
		tituloVentana = (ListaDoble<String>)sesion[3];
		tama�oTextoDelEditor = (ListaDoble<Integer>)sesion[4];
		cambiosGuardados = (ListaDoble<Boolean>)sesion[5];
		txtCodigo = (ListaDoble<JTextPane>)sesion[6];
		
		int finDelFor = titulo.length();
		for(int i=0; i<finDelFor; i++) {
			String texto = txtCodigo.getByIndex(i).dato.getText(),
				ruta = rutaDeArchivoActual.getByIndex(i).dato,
				nombre = nombreDelArchivo.getByIndex(i).dato;
			nuevaPesta�a(texto, ruta, nombre, -1);
			titulo.getByIndex(i).dato.cargaEscuchadores();
			codigoTabs.setTabComponentAt(Math.max(0, i), titulo.getByIndex(i).dato);
		}
		
		return true;
	}
	
	public void guardarSesion() { // unused pero de momento xd
		sesion = new Object[]{
				titulo, // 0
				rutaDeArchivoActual, // 1
				nombreDelArchivo, // 2
				tituloVentana, // 3
				tama�oTextoDelEditor, // 4
				cambiosGuardados, //5
				txtCodigo}; //6
		try {
			File archivoParaEliminar = new File(archivoUltimaSesion);
			archivoParaEliminar.delete();
			FileOutputStream fis = new FileOutputStream(archivoUltimaSesion);
			ObjectOutputStream oos = new ObjectOutputStream(fis);
			oos.close();
			oos.writeObject(sesion);
			fis.close();
		} catch (IOException e) {
			System.out.println("Hubo un problema al guardar los datos de la sesi�n actual.");
			e.printStackTrace();
		}
	}
	
	private void creaMenu() {
		// css para unir los iconos con los textos
		String css =
				"div {"
				+ "		text-align: center;"
				+ "}"
				+ ".lateral {"
				+ "		color: #000000;"
				+ "		font-size: 1.25em;"
				+ "		font-weight: bold;"
				+ "}"
				+ ".acortador {"
				+ "		color: #888888;"
				+ "		font-size: 1em;"
				+ "		font-weight: plain;"
				+ "}";
		
		// imagenes
		String d = "<div width=96>";
		
		menuArchivo = new JMenu(Statics.getHTML("<p>Archivo", css));
		menuCompilar = new JMenu(Statics.getHTML("<p>Compilar", css));
		
		barraDelMenu.add(menuArchivo);
		barraDelMenu.add(menuCompilar);
		
		String a = "<br /><span class=\"acortador\">",
				l = "<span class=\"lateral\">";
		
		nuevoArchivo = new JMenuItem(Statics.getHTML(l+"Nuevo archivo "+a+"(ctrl+N)", css));
		cargarArchivo = new JMenuItem(Statics.getHTML(l+"Cargar archivo "+a+"(ctrl+O)", css));
		guardarArchivo = new JMenuItem(Statics.getHTML(l+"Guardar archivo "+a+"(ctrl+S)", css));
		guardarComo = new JMenuItem(Statics.getHTML(l+"Guardar archivo como "+a+"(ctrl+shift+S)", css));
		salir = new JMenuItem(Statics.getHTML(l+"Salir "+a+"(alt+F4)", css));
		compilarCodigo = new JMenuItem(Statics.getHTML(l+"Compilar c�digo "+a+"(F5)", css));
		
		menuArchivo.add(nuevoArchivo);
		menuArchivo.add(cargarArchivo);
		menuArchivo.add(guardarArchivo);
		menuArchivo.add(guardarComo);
		
		menuArchivo.add(salir);
		menuCompilar.add(compilarCodigo);
	}
	
	@SuppressWarnings("unchecked")
	
	
	private void crearPesta�asDeAbajo() {
		bottomTabs = new JTabbedPane();
		consola=new JList<String>();
		cuadruplos = new JList<String>();
		consola.setFont(fuenteConsola);
		cuadruplos.setFont(fuenteConsola);
		DefaultListCellRenderer renderer = (DefaultListCellRenderer)cuadruplos.getCellRenderer();
		renderer.setHorizontalAlignment(SwingConstants.CENTER);
		JScrollPane pesta�aConsola = new JScrollPane(consola);
		JScrollPane pesta�aCuadruplos = new JScrollPane(cuadruplos);
		modelo = new DefaultTableModel(new Object[0][0], tituloTabla);
		tablaDatos = new JTable(modelo);
		tablaDatos.getTableHeader().setFont(fontHeader);
		JScrollPane pesta�aDatos = new JScrollPane(tablaDatos);
		pesta�aConsola.setFocusable(false);
		pesta�aCuadruplos.setFocusable(false);
		pesta�aDatos.setFocusable(false);
		bottomTabs.insertTab("Consola", icoBug, pesta�aConsola, "Consola con informaci�n del �ltimo c�digo compilado", 0);
		bottomTabs.insertTab("Datos", icoTable, pesta�aDatos, "Tabla con datos guardados del �ltimo c�digo compilado", 1);
		bottomTabs.insertTab("Cuadruplos", icoTable, pesta�aCuadruplos, "Tablas de cuadruplos de expresiones encontradas en el �ltimo c�digo compilado", 2);
		
		dividor = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		posicionRelativaDelDividorDeLosTabbedPane = 300;
		dividor.setDividerLocation(getHeight()-posicionRelativaDelDividorDeLosTabbedPane);
        dividor.setTopComponent(codigoTabs);
		dividor.setBottomComponent(bottomTabs);
		add(dividor);
	}
	
	private void creaFicheroTemporal() {
		try {
			archivoTemporal = File.createTempFile("archivoTemporal",null);
			archivoTemporal.deleteOnExit();
		}
		catch (Exception e) {
			System.out.println("Error al guardar el archivo temporal para compilar su c�digo.");
			e.printStackTrace();
			JOptionPane.showMessageDialog(null,"Error al guardar el archivo temporal para compilar su c�digo.","Alerta",JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private void agregaEscuchadores() {
		// Evitar que cualquier vaina pueda tener foco, s�lo el texto ocupa tener foco, creo yo
			// El mismo JFrame
				setFocusable(false);
			// Paneles de pesta�as
				dividor.setFocusable(false);
					codigoTabs.setFocusable(false);
					bottomTabs.setFocusable(false);

			// Paneles de las pesta�as
				tablaDatos.setFocusable(false);
				consola.setFocusable(false); // la wea de la pesta�a Consola
				cuadruplos.setFocusable(false); 
				
			// Los menus
				menuArchivo.setFocusable(false);
					nuevoArchivo.setFocusable(false);
					cargarArchivo.setFocusable(false);
					guardarArchivo.setFocusable(false);
					guardarComo.setFocusable(false);
						// los items de este menu son volatiles, se le pone el focusable(false) en el momento que se crean, en cargaArchivosRecientes()
					salir.setFocusable(false);
				menuCompilar.setFocusable(false);
					compilarCodigo.setFocusable(false);
					
		// escuchadores
			escuchadores = new Escuchadores(this);
			// Teclas
				for(int i=0; i<txtCodigo.length(); i++)
					txtCodigo.getByIndex(i).dato.addKeyListener(escuchadores);
				addKeyListener(escuchadores);
			
			// Items del menu
				// Archivo
					nuevoArchivo.addActionListener(escuchadores);
					cargarArchivo.addActionListener(escuchadores);
					guardarArchivo.addActionListener(escuchadores);
					guardarComo.addActionListener(escuchadores);
					salir.addActionListener(escuchadores);
				// Compilar
					compilarCodigo.addActionListener(escuchadores);
				
			// Cambio entre pesta�as
				consola.addAncestorListener(escuchadores);
				tablaDatos.addAncestorListener(escuchadores);
				codigoTabs.addMouseListener(escuchadores);
				for(int i=0; i<txtCodigo.length(); i++)
					txtCodigo.getByIndex(i).dato.addAncestorListener(escuchadores);
				
			// Ventana
				addWindowListener(escuchadores);
	}
	
	public void nuevaPesta�a(String texto, String ruta, String nombre, int tama�o) {
		int tabs = codigoTabs.getTabCount();
		if(tama�o >= 0) { // significa que es una adici�n normal
			if(texto.length() == 0) {
				texto = "Escriba su codigo aqui.";
				tama�o = texto.length();
			}
			JTextPane cajaDeTexto = new JTextPane() {
				public boolean getScrollableTracksViewportWidth()
			    {
			        return getUI().getPreferredSize(this).width 
			            <= getParent().getSize().width;
			    }
			};
			cajaDeTexto.setText(texto);
			txtCodigo.insertar(cajaDeTexto);
			cajaDeTexto.setFont(new Font("Consolas", Font.PLAIN, 16));
			cajaDeTexto.addKeyListener(escuchadores);
			cajaDeTexto.addAncestorListener(escuchadores);
			rutaDeArchivoActual.insertar(ruta);
			nombreDelArchivo.insertar(nombre);
			if(ruta.length() == 0)
				tituloVentana.insertar(" Compilador ");
			else
				tituloVentana.insertar(nombre + " - " + ruta + " - Compilador");
			tama�oTextoDelEditor.insertar(tama�o);
			cambiosGuardados.insertar(true);
		} // de ser tama�o un negativo es porque se quiere la pura pesta�a y los datos anteriores ya se tienen
		codigoTabs.insertTab("esto no deber�a salir nunca", null, new JScrollPane(txtCodigo.getByIndex(tabs-1).dato), "Archivo sin guardar", tabs);
		if(tama�o >=0) {
			String toolTipText = ruta;
			if(ruta.length() == 0)
				toolTipText = "Archivo sin guardar";
			PanelPesta�a panelPesta�a = new PanelPesta�a(nombre, toolTipText, this, contadorDePesta�as++);
			titulo.insertar(panelPesta�a);
			codigoTabs.setTabComponentAt(Math.max(0, tabs), panelPesta�a);
		}
		
		codigoTabs.removeTabAt(codigoTabs.getTabCount()-2); // elimina el +
		codigoTabs.insertTab("", iconoNuevoTab, null, "Nueva pesta�a", codigoTabs.getTabCount()); // a�ade el +
		codigoTabs.setSelectedIndex(codigoTabs.getTabCount()-2); // selecciona la �ltima pesta�a c�digo
		
		txtCodigo.getByIndex(tabs-1).dato.requestFocus();
//		System.out.println("tama�o del texto: "+tama�o);
	}
	
	public void actualizarBotonesDePesta�as() {
		for(int i=0; i<titulo.length(); i++)
			if(i != getSelectedTab())
				titulo.getByIndex(i).dato.boton.setIcon(null);
			else
				titulo.getByIndex(i).dato.boton.setIcon(icoCerrarPesta�a);
	}

	public void nuevaPesta�a() {
		nuevaPesta�a("", "", "Sin t�tulo", 0);
	}
	
	public void cerrarPesta�a(int tabs) {
		if(!cambiosGuardados.getByIndex(tabs).dato) {
			Escuchadores es = escuchadores;
			es.teclaShift = es.teclaAlt = es.teclaControl = false;
			String texto = "<div>No ha guardado los cambios hechos en <b><u>"+nombreDelArchivo.getByIndex(tabs).dato+"</u></b>."
					+ "<br />�Desea guardar los cambios antes de cerrar la pesta�a?";
			String css = 
					"div {"
					+ "	text-align: center;"
					+ "	font-weight: plain;"
					+ "}";
			texto = Statics.getHTML(texto, css);
			final int opcion = JOptionPane.showConfirmDialog(null, new JLabel(texto), "Cambios no guardados", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
			switch(opcion) {
			case JOptionPane.YES_OPTION:
				guardarArchivo.doClick();
				break;
			case JOptionPane.NO_OPTION:
				break;
			default:
				return;
			}
		}
		titulo.borrar(tabs);
		txtCodigo.borrar(tabs);
		rutaDeArchivoActual.borrar(tabs);
		nombreDelArchivo.borrar(tabs);
		tituloVentana.borrar(tabs);
		tama�oTextoDelEditor.borrar(tabs);
		cambiosGuardados.borrar(tabs);
		codigoTabs.remove(tabs);
		if(codigoTabs.getTabCount()==1) {
			nuevaPesta�a();
			txtCodigo.getByIndex(getSelectedTab()).dato.requestFocus();
		}
		int tabASeleccionar = getSelectedTab();
		if(getSelectedTab()==codigoTabs.getTabCount()-1)
			tabASeleccionar = codigoTabs.getTabCount()-2;
		codigoTabs.setSelectedIndex(tabASeleccionar);
		txtCodigo.getByIndex(tabASeleccionar).dato.requestFocus();
	}
	
	public void cerrarPesta�a() {
		cerrarPesta�a(getSelectedTab());
	}
	
	private void cargarArchivo(String ruta) {
		try {
			InputStreamReader inputStreamReader;
			File fichero = new File(ruta);
			FileInputStream ficheroInputStream = new FileInputStream(fichero);
			inputStreamReader =
				new InputStreamReader(ficheroInputStream, Charset.forName("UTF-8"));
			String texto = "";
			int dato = inputStreamReader.read();
			if(inputStreamReader.ready()) {
				while(dato != -1) {
					char caracterActual = (char) dato;
					dato = inputStreamReader.read();
					texto+= "" + caracterActual;
				}
				
				//txtCodigo.getByIndex(getSelectedTab()).dato.setText(str);
			}
			else
				System.out.print("El archivo no est� listo para su lectura.");
			inputStreamReader.close();
			final int tabs = getSelectedTab();
			nuevaPesta�a(texto, fichero.getPath(), fichero.getName(), texto.length());
			tituloVentana.getByIndex(tabs).dato = getTitle();
			
			codigoTabs.setToolTipTextAt(tabs+1, ruta);
		} catch(FileNotFoundException e) {
			String mensaje = "No se ha encontrado el archivo que se trat� de abrir.";
        	JOptionPane.showMessageDialog(null, mensaje, "Archivo no encontrado", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public void cargarArchivo() {
        FileNameExtensionFilter filtro = new FileNameExtensionFilter("Archivos de cracks (*.txt, *.java, *.crack)", "txt", "java", "crack");
        
        JFileChooser explorador = new JFileChooser();              //Muestra una ventana que permite navegar por los directorios
        explorador.setDialogTitle("Abrir");                        //Agrega t�tulo al cuadro de di�logo
        explorador.setFileFilter(filtro);                          //Se agrega el filtro de tipo de archivo al cuadro de di�logo
 
        if (explorador.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
        	cargarArchivo(explorador.getSelectedFile().getAbsolutePath());
		}
	}
	
	public void guardarArchivo(String ruta) {
        try {
			FileWriter archivo = new FileWriter(ruta);
			PrintWriter escritor = new PrintWriter(archivo);
			
			final int tabs = getSelectedTab();
			JTextPane codigoEnPesta�a = txtCodigo.getByIndex(tabs).dato;
			String str = codigoEnPesta�a.getText();
			escritor.print(str); 
			archivo.close();
			escritor.close();
			setTitle(nombreDelArchivo.getByIndex(tabs).dato + " - " + rutaDeArchivoActual.getByIndex(tabs).dato + " - CRACK'S Code");
			tituloVentana.getByIndex(tabs).dato = getTitle();
			String nombreDeArchivo = new File(ruta).getName(),
				toolTipText = ruta;
			titulo.getByIndex(tabs).dato.setTitle(nombreDeArchivo, toolTipText);
			
			codigoTabs.setToolTipTextAt(tabs, ruta);
        }catch (Exception e) {
        	e.printStackTrace();
	    }
	}
	
	public void guardarArchivoComo() {
        FileNameExtensionFilter filtro = new FileNameExtensionFilter("Archivos de cracks (*.txt, *.java, *.crack)", "txt", "java", "crack");
		JFileChooser explorador = new JFileChooser();              //Muestra una ventana que permite navegar por los directorios
        explorador.setApproveButtonText("Guardar");
		explorador.setDialogTitle("Guardar como");                 //Agrega t�tulo al cuadro de di�logo
        explorador.setFileFilter(filtro);                          //Se agrega el filtro de tipo de archivo al cuadro de di�logo
 
        try {
	        if (explorador.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				File fichero = explorador.getSelectedFile();      
				
				int tabs = getSelectedTab();
				rutaDeArchivoActual.getByIndex(tabs).dato = fichero.getPath();
				nombreDelArchivo.getByIndex(tabs).dato = fichero.getName();
				guardarArchivo(fichero.getPath());
				setTitle(nombreDelArchivo.getByIndex(tabs).dato + " - " + rutaDeArchivoActual.getByIndex(tabs).dato + " - CRACK'S Code");

				JOptionPane.showMessageDialog(null, "El archivo ha sido guardado con �xito.", "", JOptionPane.INFORMATION_MESSAGE);
			}
		}catch (Exception e) {
        	e.printStackTrace();
	    }
	}
	
	public int getSelectedTab() {
		return codigoTabs.getSelectedIndex();
	}
}
