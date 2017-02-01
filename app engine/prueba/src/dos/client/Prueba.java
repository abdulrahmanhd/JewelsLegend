package dos.client;

import dos.shared.FieldVerifier;

import java.util.ArrayList;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import dos.shared.*;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Prueba implements EntryPoint {
	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	private static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network " + "connection and try again.";

	/**
	 * Create a remote service proxy to talk to the server-side Greeting service.
	 */
	private final GreetingServiceAsync greetingService = GWT.create(GreetingService.class);
	private final RepairServiceAsync repairService = GWT.create(RepairService.class);
	
	
	public ArrayList<Cliente> clientes = new ArrayList<Cliente>();
	public ArrayList<Reparacion> reparaciones = new ArrayList<Reparacion>();

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		//logo.setUrl("images/appiriologo.png");
		LoginServiceAsync loginService = GWT.create(LoginService.class);
		loginService.login(GWT.getHostPageBaseURL(), new
				AsyncCallback<LoginInfo>() {
			public void onFailure(Throwable error) {
			}
			public void onSuccess(LoginInfo loginInfo) {
				//loginInfo = result;
				if(loginInfo.isLoggedIn()) {
					
					System.out.println("USUARIO VALIDADO");
					loadMainUI();
				} else {
					System.out.println("USUARIO NO VALIDADO CARGANDO LOGIN");
					loadLoginUI(loginInfo);
				}
			}
		});
		}
		private void loadLoginUI(LoginInfo loginInfo) {
			VerticalPanel loginPanel = new VerticalPanel();
			Anchor loginLink = new Anchor("Sign In");
			loginLink.setHref(loginInfo.getLoginUrl());
			//loginPanel.add(logo);
			loginPanel.add(new Label("Please sign-in with your Google Account to access the application."));
			loginPanel.add(loginLink);
			RootPanel.get("LoginPanel").add(loginPanel);
		}

	
	
	
	/**
	 * @wbp.parser.entryPoint
	 */
	public void loadMainUI() {
		final Button sendButton = new Button("Send");
		
		final Label errorLabel = new Label();

		// We can add style names to widgets
		sendButton.addStyleName("sendButton");

		// Add the nameField and sendButton to the RootPanel
		// Use RootPanel.get() to get the entire body element
		RootPanel rootPanel = RootPanel.get("nameFieldContainer");
		
		Label lblNewLabel = new Label("Identificaci\u00F3n (DNI)");
		rootPanel.add(lblNewLabel, 10, 10);
		
		final TextBox jtfdni = new TextBox();
		rootPanel.add(jtfdni, 222, 60);
		jtfdni.setSize("170px", "16px");
		
		Label lblDniobligatorio = new Label("DNI (Obligatorio)");
		rootPanel.add(lblDniobligatorio, 222, 38);
		
		Label lblNombreobligatorio = new Label("Nombre (Obligatorio)");
		rootPanel.add(lblNombreobligatorio, 433, 38);
		
		final TextBox jtfnombre = new TextBox();
		rootPanel.add(jtfnombre, 433, 60);
		jtfnombre.setSize("170px", "16px");
		
		Label lblApellidosobligatorio = new Label("Apellidos (Obligatorio)");
		rootPanel.add(lblApellidosobligatorio, 649, 38);
		lblApellidosobligatorio.setSize("162px", "16px");
		
		Label lblTelefonoobligatorio = new Label("Telefono (Obligatorio)");
		rootPanel.add(lblTelefonoobligatorio, 649, 103);
		
		Label lblEmailopcional = new Label("E-mail (Opcional)");
		rootPanel.add(lblEmailopcional, 222, 175);
		
		final TextBox jtfemail = new TextBox();
		rootPanel.add(jtfemail, 222, 197);
		jtfemail.setSize("302px", "16px");
		
		Label lblDireccionopcional = new Label("Direccion (Opcional)");
		rootPanel.add(lblDireccionopcional, 546, 175);
		
		final TextBox jtfdireccion = new TextBox();
		rootPanel.add(jtfdireccion, 546, 197);
		jtfdireccion.setSize("375px", "16px");
		
		Label lblPoblacionopcional = new Label("Poblacion (Opcional)");
		rootPanel.add(lblPoblacionopcional, 222, 103);
		
		final TextBox jtfpoblacion = new TextBox();
		rootPanel.add(jtfpoblacion, 222, 125);
		jtfpoblacion.setSize("170px", "16px");
		
		final TextBox jtftelefono = new TextBox();
		rootPanel.add(jtftelefono, 649, 125);
		jtftelefono.setSize("272px", "16px");
		
		final TextBox jtfcodigopostal = new TextBox();
		rootPanel.add(jtfcodigopostal, 434, 125);
		jtfcodigopostal.setSize("165px", "16px");
		
		final TextBox jtfapellidos = new TextBox();
		rootPanel.add(jtfapellidos, 649, 60);
		jtfapellidos.setSize("268px", "16px");
		
		Button btnAdd = new Button("Add");
		rootPanel.add(btnAdd, 222, 253);
	 
		Button btnDelete = new Button("Delete");
		btnDelete.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				jtfnombre.setText("");
				jtfapellidos.setText("");
				jtfdni.setText("");
				jtfemail.setText("");
				jtftelefono.setText("");
				jtfpoblacion.setText("");
				jtfdireccion.setText("");
				jtfcodigopostal.setText("");
			}
		});
		rootPanel.add(btnDelete, 273, 253);
		
		Label lblAtendidoPor = new Label("Atendido por");
		rootPanel.add(lblAtendidoPor, 848, 329);
		
		final ListBox jcbatendido = new ListBox();
		rootPanel.add(jcbatendido, 936, 325);
		jcbatendido.setSize("105px", "20px");
		
		Label lblNewLabel_1 = new Label("ID Servicios pendientes");
		rootPanel.add(lblNewLabel_1, 856, 392);
		
		Label lblArticulos = new Label("Articulos");
		rootPanel.add(lblArticulos, 8, 291);
		
		Label lblSn = new Label("S/N");
		rootPanel.add(lblSn, 378, 366);
		
		final TextBox jtfnunmeroSerie = new TextBox();
		rootPanel.add(jtfnunmeroSerie, 427, 358);
		jtfnunmeroSerie.setSize("97px", "16px");
		
		final ListBox jcbgarantia = new ListBox();
		rootPanel.add(jcbgarantia, 65, 404);
		jcbgarantia.setSize("93px", "20px");
		
		Label lblGarantia = new Label("Garantia");
		rootPanel.add(lblGarantia, 10, 408);
		
		Label lblDatosDelCliente = new Label("Datos del cliente");
		rootPanel.add(lblDatosDelCliente, 222, 10);
		
		Label lblMarca = new Label("Marca");
		rootPanel.add(lblMarca, 10, 366);
		
		Label lblModelo = new Label("Modelo");
		rootPanel.add(lblModelo, 176, 366);
		
		Label lblFunciona = new Label("Funciona");
		rootPanel.add(lblFunciona, 364, 408);
		
		Label lblPPrevisto = new Label("P Previsto");
		rootPanel.add(lblPPrevisto, 176, 408);
		
		final TextBox jtfmodelo = new TextBox();
		rootPanel.add(jtfmodelo, 229, 358);
		jtfmodelo.setSize("105px", "16px");
		
		final TextBox jtfmarca = new TextBox();
		rootPanel.add(jtfmarca, 53, 358);
		jtfmarca.setSize("97px", "16px");
		
		final ListBox jcbprecio = new ListBox();
		rootPanel.add(jcbprecio, 246, 404);
		jcbprecio.setSize("96px", "20px");
		
		Label lblDescripcinDeAvera = new Label("Descripci\u00F3n de aver\u00EDa");
		rootPanel.add(lblDescripcinDeAvera, 10, 481);
		
		final TextArea jtadescripcion = new TextArea();
		rootPanel.add(jtadescripcion, 14, 503);
		jtadescripcion.setSize("508px", "74px");
		
		Label lblNota = new Label("Nota");
		rootPanel.add(lblNota, 10, 599);
		
		final TextArea jtanota = new TextArea();
		rootPanel.add(jtanota, 14, 621);
		jtanota.setSize("508px", "74px");
		
		Label lblCodigoPostal = new Label("Codigo Postal");
		rootPanel.add(lblCodigoPostal, 433, 103);
		
		Label lblFecha = new Label("Fecha");
		rootPanel.add(lblFecha, 10, 325);
		
		final TextBox jtffecha = new TextBox();
		rootPanel.add(jtffecha, 53, 317);
		jtffecha.setSize("97px", "16px");
		
		Label lblTipo = new Label("Tipo");
		rootPanel.add(lblTipo, 194, 325);
		
		final TextBox jtftipo = new TextBox();
		rootPanel.add(jtftipo, 229, 317);
		jtftipo.setSize("105px", "16px");
		
		Label lblProducto = new Label("Producto");
		rootPanel.add(lblProducto, 365, 325);
		
		final TextBox jtfproducto = new TextBox();
		rootPanel.add(jtfproducto, 427, 317);
		jtfproducto.setSize("97px", "16px");
		
		Label lblSo_1 = new Label("S.O");
		rootPanel.add(lblSo_1, 565, 325);
		
		Label lblPassSo_1 = new Label("Pass S.O");
		rootPanel.add(lblPassSo_1, 546, 366);
		
		final TextBox jtfpassSO = new TextBox();
		rootPanel.add(jtfpassSO, 612, 358);
		jtfpassSO.setSize("105px", "16px");
		
		final TextBox jtfso = new TextBox();
		rootPanel.add(jtfso, 613, 317);
		jtfso.setSize("104px", "16px");
		
		final ListBox jcbfunciona = new ListBox();
		rootPanel.add(jcbfunciona, 427, 404);
		jcbfunciona.setSize("105px", "20px");
		
		Label lblRecogido = new Label("Recogido");
		rootPanel.add(lblRecogido, 546, 408);
		
		final ListBox jcbrecogido = new ListBox();
		rootPanel.add(jcbrecogido, 612, 404);
		jcbrecogido.setSize("113px", "20px");
		
		final TextBox jtfmostrar = new TextBox();
		rootPanel.add(jtfmostrar, 856, 665);
		jtfmostrar.setSize("175px", "16px");
		
		final TextBox mostrargarantia = new TextBox();
		rootPanel.add(mostrargarantia, 65, 431);
		mostrargarantia.setSize("85px", "16px");
		
		final TextBox mostrarprecio = new TextBox();
		rootPanel.add(mostrarprecio, 246, 431);
		mostrarprecio.setSize("88px", "16px");
		
		final TextBox mostrarfunciona = new TextBox();
		rootPanel.add(mostrarfunciona, 427, 430);
		mostrarfunciona.setSize("97px", "16px");
		
		final TextBox mostrarrecogido = new TextBox();
		rootPanel.add(mostrarrecogido, 612, 429);
		mostrarrecogido.setSize("105px", "16px");
		
		final TextBox mostraratendidopor = new TextBox();
		rootPanel.add(mostraratendidopor, 936, 351);
		mostraratendidopor.setSize("97px", "16px");
		
		Button btnMostrar = new Button("Cargar Servicio (por ID)");
		btnMostrar.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				for(int i=0;i<reparaciones.size();i++){
					if(reparaciones.get(i).getNumeroSerie().equals(jtfmostrar.getText())){
						jtffecha.setText(reparaciones.get(i).getFecha());
						jtftipo.setText(reparaciones.get(i).getTipo());
						jtfproducto.setText(reparaciones.get(i).getProducto());
						jtfmarca.setText(reparaciones.get(i).getMarca());
						jtfmodelo.setText(reparaciones.get(i).getModelo());
						jtfnunmeroSerie.setText(reparaciones.get(i).getNumeroSerie());
						jtfso.setText(reparaciones.get(i).getSistemOperativo());
						jtfpassSO.setText(reparaciones.get(i).getPassSO());
						jtadescripcion.setText(reparaciones.get(i).getDescripcion());
						jtanota.setText(reparaciones.get(i).getObservaciones());
						mostrargarantia.setText(reparaciones.get(i).getGarantia());
						mostrarprecio.setText(reparaciones.get(i).getPresupuesto());
						mostrarrecogido.setText(reparaciones.get(i).getRecoger());
						mostrarfunciona.setText(reparaciones.get(i).getReparar());
						mostraratendidopor.setText(reparaciones.get(i).getEmpleado());
					}
				}
			}
		});
		rootPanel.add(btnMostrar, 676, 665);
		btnMostrar.setSize("161px", "28px");
		
		Button btnGuardar = new Button("Guardar");
		btnGuardar.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				
				String fecha=jtffecha.getText();
				String tipo=jtftipo.getText();
				String producto=jtfproducto.getText();
				String marca=jtfmarca.getText();
				String modelo=jtfmodelo.getText();
				String numSerie=jtfnunmeroSerie.getText();
				String sistOp=jtfso.getText();
				String passSO=jtfpassSO.getText();
				String descripcion=jtadescripcion.getText();
				String observacion=jtanota.getText();
				
				int opcionGarantia=jcbgarantia.getSelectedIndex();
				String garantia="";
				switch (opcionGarantia) {
					case 1: garantia="1";
					break;
					case 2: garantia="2";
					break;
					case 3: garantia="3";
					break;
					case 4: garantia="4";
			        default:garantia="";
			        break;
				}
				
				int opcionPrecio=jcbprecio.getSelectedIndex();
				String precio="";
				switch (opcionPrecio) {
					case 1: precio="50";
					break;
					case 2: precio="100";
					break;
					case 3: precio="150";
					break;
					case 4: precio="200";
			        default:precio="";
			        break;
				}
				
				int opcionFunciona=jcbfunciona.getSelectedIndex();
				String funciona="";
				switch (opcionFunciona) {
					case 1: funciona="si";
					break;
					case 2: funciona="no";
					break;
			        default:funciona="";
			        break;
				}
				
				int opcionRecogido=jcbrecogido.getSelectedIndex();
				String recogido="";
				switch (opcionRecogido) {
					case 1: recogido="si";
					break;
					case 2: recogido="no";
					break;
			        default:recogido="";
			        break;
				}
				
				int opcionatendido=jcbatendido.getSelectedIndex();
				String atendido="";
				switch (opcionatendido) {
					case 1: atendido="Pablo J.";
					break;
					case 2: atendido="Jesus L.";
					break;
					case 3: atendido="Roberto O.";
					break;
					case 4: atendido="Pablo O.";
			        default:atendido="";
			        break;
				}
				Reparacion reparacion=new Reparacion(fecha,tipo,producto,marca,modelo,numSerie,sistOp,passSO,
						descripcion,observacion,garantia,precio,funciona,recogido,atendido);
				reparaciones.add(reparacion);
			}
		});
	
		rootPanel.add(btnGuardar, 14, 726);
		btnGuardar.setSize("69px", "28px");
		
		jcbrecogido.addItem("");
		jcbrecogido.addItem("SI");
		jcbrecogido.addItem("NO");
		
		jcbfunciona.addItem("");
		jcbfunciona.addItem("SI");
		jcbfunciona.addItem("NO");
		
		jcbprecio.addItem("");
		jcbprecio.addItem("50");
		jcbprecio.addItem("100");
		jcbprecio.addItem("150");
		jcbprecio.addItem("200");
		
		jcbgarantia.addItem("");
		jcbgarantia.addItem("1");
		jcbgarantia.addItem("2");
		jcbgarantia.addItem("3");
		jcbgarantia.addItem("4");
		
		jcbatendido.addItem("");
		jcbatendido.addItem("Pablo J.");
		jcbatendido.addItem("Jesus L.");
		jcbatendido.addItem("Roberto O.");
		jcbatendido.addItem("Pablo O.");
		
		Button btnBorrar = new Button("Borrar");
		btnBorrar.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				jtffecha.setText("");
				jtftipo.setText("");
				jtfproducto.setText("");
				jtfmarca.setText("");
				jtfmodelo.setText("");
				jtfnunmeroSerie.setText("");
				jtfso.setText("");
				jtfpassSO.setText("");
				jtadescripcion.setText("");
				jtanota.setText("");
				mostrargarantia.setText("");
				mostrarprecio.setText("");
				mostrarrecogido.setText("");
				mostrarfunciona.setText("");
				mostraratendidopor.setText("");
			}
		});
		rootPanel.add(btnBorrar, 110, 726);
		btnBorrar.setSize("61px", "28px");
		
		Button btnVerServiciosPendientes = new Button("Ver Servicios Pendientes");
		rootPanel.add(btnVerServiciosPendientes, 676, 530);
		
		Button btnMostrarCliente = new Button("Mostrar Cliente");
		rootPanel.add(btnMostrarCliente, 10, 103);
		
		final TextBox jtfmostrarCliente = new TextBox();
		rootPanel.add(jtfmostrarCliente, 10, 60);
		jtfmostrarCliente.setSize("170px", "16px");
		
		final ListBox listBox = new ListBox();
		rootPanel.add(listBox, 856, 431);
		listBox.setSize("185px", "217px");
		listBox.setVisibleItemCount(5);
		
		Button btnReparar = new Button("Reparar");
		rootPanel.add(btnReparar, 194, 726);
		
		Button btnRecoger = new Button("Recoger");
		rootPanel.add(btnRecoger, 273, 726);
		
		Button btnVerPReparar = new Button("Ver P. Para Reparar");
		rootPanel.add(btnVerPReparar, 676, 575);
		btnVerPReparar.setSize("161px", "28px");
		
		Button btnVerPRecoger = new Button("Ver P. Para Recoger");
		rootPanel.add(btnVerPRecoger, 676, 621);
		btnVerPRecoger.setSize("161px", "28px");

		// Create the popup dialog box
		final DialogBox dialogBox = new DialogBox();
		dialogBox.setText("Remote Procedure Call");
		dialogBox.setAnimationEnabled(true);
		final Button closeButton = new Button("Close");
		// We can set the id of a widget by accessing its Element
		closeButton.getElement().setId("closeButton");
		final Label textToServerLabel = new Label();
		final HTML serverResponseLabel = new HTML();
		VerticalPanel dialogVPanel = new VerticalPanel();
		dialogVPanel.addStyleName("dialogVPanel");
		dialogVPanel.add(new HTML("<b>Sending name to the server:</b>"));
		dialogVPanel.add(textToServerLabel);
		dialogVPanel.add(new HTML("<br><b>Server replies:</b>"));
		dialogVPanel.add(serverResponseLabel);
		dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
		dialogVPanel.add(closeButton);
		dialogBox.setWidget(dialogVPanel);

		// Add a handler to close the DialogBox
		closeButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				dialogBox.hide();
				sendButton.setEnabled(true);
				sendButton.setFocus(true);
			}
		});

		// Create a handler for the sendButton and nameField
		class MyHandler implements ClickHandler, KeyUpHandler {
			/**
			 * Fired when the user clicks on the sendButton.
			 */
			public void onClick(ClickEvent event) {
				sendNameToServer();
			}

			/**
			 * Fired when the user types in the nameField.
			 */
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					sendNameToServer();
				}
			}

			/**
			 * Send the name from the nameField to the server and wait for a response.
			 */
			private void sendNameToServer() {
				// First, we validate the input.
				errorLabel.setText("");
				String textToServer = "hola";
				if (!FieldVerifier.isValidName(textToServer)) {
					errorLabel.setText("Please enter at least four characters");
					return;
				}

				// Then, we send the input to the server.
				sendButton.setEnabled(false);
				textToServerLabel.setText(textToServer);
				serverResponseLabel.setText("");
				greetingService.greetServer(textToServer, new AsyncCallback<String>() {
					public void onFailure(Throwable caught) {
						// Show the RPC error message to the user
						dialogBox.setText("Remote Procedure Call - Failure");
						serverResponseLabel.addStyleName("serverResponseLabelError");
						serverResponseLabel.setHTML(SERVER_ERROR);
						dialogBox.center();
						closeButton.setFocus(true);
					}

					public void onSuccess(String result) {
						dialogBox.setText("Remote Procedure Call");
						serverResponseLabel.removeStyleName("serverResponseLabelError");
						serverResponseLabel.setHTML(result);
						dialogBox.center();
						closeButton.setFocus(true);
					}
				});
			}
		}
		
		
		class HandlerMeterCliente implements ClickHandler {
			

			public void onClick(ClickEvent event) {
					clientToServer();
				}
			
			private void clientToServer(){
				
				String nombre = jtfnombre.getText();
				String apellidos = jtfapellidos.getText();
				String dni = jtfdni.getText();
				String email = jtfemail.getText();
				String telefono = jtftelefono.getText();
				String direccion = jtfdireccion.getText();
				String poblacion = jtfpoblacion.getText();
				String codpost = jtfcodigopostal.getText();
				
				errorLabel.setText("");
				serverResponseLabel.setText("");
				
				repairService.addClientToServer(nombre, apellidos, dni, email, telefono, direccion, poblacion, codpost,	
						new AsyncCallback<String>() {
							public void onFailure(Throwable caught) {
								// Show the RPC error message to the user
								dialogBox
										.setText("Remote Procedure Call - Failure");
								serverResponseLabel
										.addStyleName("serverResponseLabelError");
								serverResponseLabel.setHTML(SERVER_ERROR);
								dialogBox.center();
								closeButton.setFocus(true);
							}

							public void onSuccess(String result) {
								dialogBox.setText("Cliente introducido. Pulse aceptar.");
								serverResponseLabel
										.removeStyleName("serverResponseLabelError");
								serverResponseLabel.setHTML(result);
								dialogBox.center();
								closeButton.setFocus(true);
								
								
							}
						});
			}
	}
		
		
class HandlerMostrarCliente implements ClickHandler{
			
			public void onClick(ClickEvent event){
				mostrarCliente(event);
			}
			
			public void mostrarCliente(ClickEvent event){
				errorLabel.setText("");
				String dni= jtfmostrarCliente.getText();
				serverResponseLabel.setText("");
				
				repairService.mostrarCliente(dni,
						new AsyncCallback<Cliente>() {
							public void onFailure(Throwable caught) {
								// Show the RPC error message to the user
								dialogBox
										.setText("Ver Cliente - DNI No existe");
								serverResponseLabel
										.addStyleName("serverResponseLabelError");
								serverResponseLabel.setHTML("No se encuentra ningún cliente con el DNI proporcionado: ");
								dialogBox.center();
								closeButton.setFocus(true);
							}

							public void onSuccess(Cliente result) {
								dialogBox.setText("Ver Cliente");
								serverResponseLabel
										.removeStyleName("serverResponseLabelError");
								serverResponseLabel.setHTML(result.getNombre());
								dialogBox.center();
								closeButton.setFocus(true);
								
								
								jtfnombre.setText(result.getNombre());
								jtfapellidos.setText(result.getApellidos());
								jtfdni.setText(result.getNIF());
								jtfemail.setText(result.getEmail());
								jtftelefono.setText(result.getTelefono());
								jtfpoblacion.setText(result.getLocalidad());
								jtfdireccion.setText(result.getDireccion());
								jtfcodigopostal.setText(result.getCodigoPostal());
								
								
							}
						});
			}
			
		}
		

class HandlerVerRecoger implements ClickHandler {
	

	public void onClick(ClickEvent event) {
			verReparaciones();
		}
	
	private void verReparaciones(){
		
		
		errorLabel.setText("");
		serverResponseLabel.setText("");
		int l = listBox.getItemCount();
		
		for(int i = 0; i<l; i++){
			
			listBox.removeItem(i);
		}
		
	
		repairService.getParaRecoger(new AsyncCallback<String[]>() {
					public void onFailure(Throwable caught) {
						// Show the RPC error message to the user
						dialogBox
								.setText("Remote Procedure Call - Failure");
						serverResponseLabel
								.addStyleName("serverResponseLabelError");
						serverResponseLabel.setHTML(SERVER_ERROR);
						dialogBox.center();
						closeButton.setFocus(true);
					}

					public void onSuccess(String[] result) {
						dialogBox.setText("Mostrando reparaciones pendientes.. Pulse aceptar para continuar.");
						serverResponseLabel
								.removeStyleName("serverResponseLabelError");
						serverResponseLabel.setHTML(result[0]);
						dialogBox.center();
						closeButton.setFocus(true);
						
						
						for(int i = 0; i< result.length; i++){
						
						listBox.insertItem(result[i], i);
						
						}
					}
				});
	}
}

class HandlerVerReparados implements ClickHandler {
	

	public void onClick(ClickEvent event) {
			verReparaciones();
		}
	
	private void verReparaciones(){
		
		
		errorLabel.setText("");
		serverResponseLabel.setText("");
		int l = listBox.getItemCount();
		
		for(int i = 0; i<l; i++){
			
			listBox.removeItem(i);
		}
		
		
		repairService.getParaReparar(new AsyncCallback<String[]>() {
					public void onFailure(Throwable caught) {
						// Show the RPC error message to the user
						dialogBox
								.setText("Remote Procedure Call - Failure");
						serverResponseLabel
								.addStyleName("serverResponseLabelError");
						serverResponseLabel.setHTML(SERVER_ERROR);
						dialogBox.center();
						closeButton.setFocus(true);
					}

					public void onSuccess(String[] result) {
						dialogBox.setText("Mostrando reparaciones pendientes.. Pulse aceptar para continuar.");
						serverResponseLabel
								.removeStyleName("serverResponseLabelError");
						serverResponseLabel.setHTML(result[0]);
						dialogBox.center();
						closeButton.setFocus(true);
						
						
						for(int i = 0; i< result.length; i++){
						
						listBox.insertItem(result[i], i);
						
						}
					}
				});
	}
}

		
class HandlerGetReparaciones implements ClickHandler {
			

			public void onClick(ClickEvent event) {
					verReparaciones();
				}
			
			private void verReparaciones(){
				
				
				errorLabel.setText("");
				serverResponseLabel.setText("");
				int l = listBox.getItemCount();
				
				for(int i = 0; i<l; i++){
					
					listBox.removeItem(i);
				}
				
				
				repairService.getReparaciones(	
						new AsyncCallback<String[]>() {
							public void onFailure(Throwable caught) {
								// Show the RPC error message to the user
								dialogBox
										.setText("Remote Procedure Call - Failure");
								serverResponseLabel
										.addStyleName("serverResponseLabelError");
								serverResponseLabel.setHTML(SERVER_ERROR);
								dialogBox.center();
								closeButton.setFocus(true);
							}

							public void onSuccess(String[] result) {
								dialogBox.setText("Mostrando reparaciones pendientes.. Pulse aceptar para continuar.");
								serverResponseLabel
										.removeStyleName("serverResponseLabelError");
								serverResponseLabel.setHTML(result[0]);
								dialogBox.center();
								closeButton.setFocus(true);
								
								
								for(int i = 0; i< result.length; i++){
								
								listBox.insertItem(result[i], i);
								
								}
							}
						});
			}
	}


class HandlerMeterReparacion implements ClickHandler {
	

	public void onClick(ClickEvent event) {
			reparacionToServer();
		}
	
	private void reparacionToServer(){
		
		String fecha=jtffecha.getText();
		String tipo=jtftipo.getText();
		String producto=jtfproducto.getText();
		String marca=jtfmarca.getText();
		String modelo=jtfmodelo.getText();
		String numSerie=jtfnunmeroSerie.getText();
		String sistOp=jtfso.getText();
		String passSO=jtfpassSO.getText();
		String descripcion=jtadescripcion.getText();
		String observacion=jtanota.getText();
		
		int opcionGarantia=jcbgarantia.getSelectedIndex();
		String garantia="";
		switch (opcionGarantia) {
			case 1: garantia="1";
			break;
			case 2: garantia="2";
			break;
			case 3: garantia="3";
			break;
			case 4: garantia="4";
	        default:garantia="";
	        break;
		}
		
		int opcionPrecio=jcbprecio.getSelectedIndex();
		String precio="";
		switch (opcionPrecio) {
			case 1: precio="50";
			break;
			case 2: precio="100";
			break;
			case 3: precio="150";
			break;
			case 4: precio="200";
	        default:precio="";
	        break;
		}
		
		int opcionFunciona=jcbfunciona.getSelectedIndex();
		String funciona="";
		switch (opcionFunciona) {
			case 1: funciona="si";
			break;
			case 2: funciona="no";
			break;
	        default:funciona="";
	        break;
		}
		
		int opcionRecogido=jcbrecogido.getSelectedIndex();
		String recogido="";
		switch (opcionRecogido) {
			case 1: recogido="si";
			break;
			case 2: recogido="no";
			break;
	        default:recogido="";
	        break;
		}
		
		int opcionatendido=jcbatendido.getSelectedIndex();
		String atendido="";
		switch (opcionatendido) {
			case 1: atendido="Pablo J.";
			break;
			case 2: atendido="Jesus L.";
			break;
			case 3: atendido="Roberto O.";
			break;
			case 4: atendido="Pablo O.";
	        default:atendido="";
	        break;
		}
		
		
		errorLabel.setText("");
		serverResponseLabel.setText("");
		
		repairService.addProductToServer(fecha,tipo,producto,marca,modelo,numSerie,sistOp,passSO,
		descripcion,observacion,garantia,precio,funciona,recogido,atendido,	
				new AsyncCallback<String>() {
					public void onFailure(Throwable caught) {
						// Show the RPC error message to the user
						dialogBox
								.setText("Remote Procedure Call - Failure");
						serverResponseLabel
								.addStyleName("serverResponseLabelError");
						serverResponseLabel.setHTML(SERVER_ERROR);
						dialogBox.center();
						closeButton.setFocus(true);
					}

					public void onSuccess(String result) {
						dialogBox.setText("Añadir producto");
						serverResponseLabel
								.removeStyleName("serverResponseLabelError");
						serverResponseLabel.setHTML(result);
						dialogBox.center();
						closeButton.setFocus(true);
					}
				});
	}
}
				
				
			

class HandlerMostrarReparacion implements ClickHandler{
	public void onClick(ClickEvent event){
		mostrarCliente(event);
	}
	
	public void mostrarCliente(ClickEvent event){
		errorLabel.setText("");
		String id=jtfmostrar.getText();
		serverResponseLabel.setText("");
		
		repairService.mostrarReparacion(id,new AsyncCallback<Reparacion>() {
					public void onFailure(Throwable caught) {
						// Show the RPC error message to the user
						dialogBox
								.setText("Ver Reparacion - No existe");
						serverResponseLabel
								.addStyleName("serverResponseLabelError");
						serverResponseLabel.setHTML("No se dispone de ningun producto con nº Seire: "+jtfmostrar.getText());
						dialogBox.center();
						closeButton.setFocus(true);
					}

					public void onSuccess(Reparacion result) {
						dialogBox.setText("Ver Producto");
						serverResponseLabel
								.removeStyleName("serverResponseLabelError");
						serverResponseLabel.setHTML(result.getModelo());
						dialogBox.center();
						closeButton.setFocus(true);
						
						jtffecha.setText(result.getFecha());
						jtftipo.setText(result.getTipo());
						jtfproducto.setText(result.getProducto());
						jtfmarca.setText(result.getMarca());
						jtfmodelo.setText(result.getModelo());
						jtfnunmeroSerie.setText(result.getNumeroSerie());
						jtfso.setText(result.getSistemOperativo());
						jtfpassSO.setText(result.getPassSO());
						jtadescripcion.setText(result.getDescripcion());
						jtanota.setText(result.getObservaciones());
						mostrargarantia.setText(result.getGarantia());
						mostrarprecio.setText(result.getPresupuesto());
						mostrarrecogido.setText(result.getRecoger());
						mostrarfunciona.setText(result.getReparar());
						mostraratendidopor.setText(result.getEmpleado());
						
					}
				});
	}
}

class HandlerRecoger implements ClickHandler {
	

	public void onClick(ClickEvent event) {
			recoger(event);
		}
	
	private void recoger(ClickEvent event){
		
		String numSerie = jtfmostrar.getText();
		
		errorLabel.setText("");
		serverResponseLabel.setText("");
		repairService.recogerProducto(numSerie,new AsyncCallback<String>() {
					public void onFailure(Throwable caught) {
						// Show the RPC error message to the user
						dialogBox
								.setText("No existe");
						serverResponseLabel
								.addStyleName("ERROR:");
						serverResponseLabel.setHTML("El producto solicitado no existe");
						dialogBox.center();
						closeButton.setFocus(true);
					}

					public void onSuccess(String result) {
						dialogBox.setText("Producto dispuesto para ser recogido");
						serverResponseLabel
								.removeStyleName("serverResponseLabelError");
						serverResponseLabel.setHTML(result);
						dialogBox.center();
						closeButton.setFocus(true);
					}
				});
	}
	
	
}	

class HandlerReparar implements ClickHandler {
	

	public void onClick(ClickEvent event) {
			clientToServer(event);
		}
	
	private void clientToServer(ClickEvent event){
		
		String numSerie = jtfmostrar.getText();
		
		errorLabel.setText("");
		serverResponseLabel.setText("");
		repairService.repararProducto(numSerie,new AsyncCallback<String>() {
					public void onFailure(Throwable caught) {
						// Show the RPC error message to the user
						dialogBox
								.setText("No existe");
						serverResponseLabel
								.addStyleName("ERROR:");
						serverResponseLabel.setHTML("El producto solicitado no existe");
						dialogBox.center();
						closeButton.setFocus(true);
					}

					public void onSuccess(String result) {
						dialogBox.setText("Producto reparado");
						serverResponseLabel
								.removeStyleName("serverResponseLabelError");
						serverResponseLabel.setHTML(result);
						dialogBox.center();
						closeButton.setFocus(true);
					}
				});
	}
}	

class HandlerEliminarCliente implements ClickHandler {
	

	public void onClick(ClickEvent event) {
			eliminarCliente(event);
		}
	
	private void eliminarCliente(ClickEvent event){
		
		String dni = jtfmostrarCliente.getText();
		
		errorLabel.setText("");
		serverResponseLabel.setText("");
		repairService.eliminarCliente(dni,new AsyncCallback<String>() {
					public void onFailure(Throwable caught) {
						// Show the RPC error message to the user
						dialogBox
								.setText("No existe");
						serverResponseLabel
								.addStyleName("serverResponseLabelError");
						serverResponseLabel.setHTML(SERVER_ERROR);
						dialogBox.center();
						closeButton.setFocus(true);
					}

					public void onSuccess(String result) {
						dialogBox.setText("Eliminacion realizada");
						serverResponseLabel
								.removeStyleName("serverResponseLabelError");
						serverResponseLabel.setHTML(result);
						dialogBox.center();
						closeButton.setFocus(true);
					}
				});
	}
}	

class HandlerEliminarReparacion implements ClickHandler {
	

	public void onClick(ClickEvent event) {
			eliminarReparacion(event);
		}
	
	private void eliminarReparacion(ClickEvent event){
		
		String numSerie = jtfmostrar.getText();
		
		errorLabel.setText("");
		serverResponseLabel.setText("");
		repairService.eliminarReparacion(numSerie,new AsyncCallback<String>() {
					public void onFailure(Throwable caught) {
						// Show the RPC error message to the user
						dialogBox
								.setText("No existe");
						serverResponseLabel
								.addStyleName("serverResponseLabelError");
						serverResponseLabel.setHTML(SERVER_ERROR);
						dialogBox.center();
						closeButton.setFocus(true);
					}

					public void onSuccess(String result) {
						dialogBox.setText("Eliminacion realizada");
						serverResponseLabel
								.removeStyleName("serverResponseLabelError");
						serverResponseLabel.setHTML(result);
						dialogBox.center();
						closeButton.setFocus(true);
					}
				});
	}
}	
			
			

		

		// Add a handler to send the name to the server
		MyHandler handler = new MyHandler();
		sendButton.addClickHandler(handler);
		
		
		HandlerMeterCliente handlerMeter = new HandlerMeterCliente();
		btnAdd.addClickHandler(handlerMeter);
	    HandlerGetReparaciones handlerRecuperar = new HandlerGetReparaciones();
	    btnVerServiciosPendientes.addClickHandler(handlerRecuperar);
	    HandlerMostrarCliente handlerMostrarCliente = new HandlerMostrarCliente();
		btnMostrarCliente.addClickHandler(handlerMostrarCliente);
	    
		HandlerMeterReparacion handlerMeterReparacion = new HandlerMeterReparacion();
		btnGuardar.addClickHandler(handlerMeterReparacion);
		
	    HandlerMostrarReparacion handlerVerReparacion = new HandlerMostrarReparacion();
	    btnMostrar.addClickHandler(handlerVerReparacion);
	    
	    HandlerReparar handlerRepar = new HandlerReparar();
	    btnReparar.addClickHandler(handlerRepar);
	    
	    HandlerRecoger handlerRecoger = new HandlerRecoger();
	    btnRecoger.addClickHandler(handlerRecoger);
	     
	    HandlerEliminarCliente handlerEliminarCliente = new HandlerEliminarCliente();
	    btnDelete.addClickHandler(handlerEliminarCliente);
	    
	    HandlerEliminarReparacion handlerEliminarReparacion = new HandlerEliminarReparacion();
	    btnBorrar.addClickHandler(handlerEliminarReparacion);
		
	    HandlerVerReparados handlerVerReparados = new HandlerVerReparados();
	    btnVerPReparar.addClickHandler(handlerVerReparados);
	    
	    HandlerVerRecoger handlerVerRecoger = new HandlerVerRecoger();
	    btnVerPRecoger.addClickHandler(handlerVerRecoger);
	    
	}
}
