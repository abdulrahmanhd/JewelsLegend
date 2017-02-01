package dos.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

import dos.shared.Cliente;
import dos.shared.Reparacion;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface RepairServiceAsync {
	
	
	void greetServer(String input, AsyncCallback<String> callback) throws IllegalArgumentException;
	
	void addClientToServer(String nombre, String apellidos, String dni, String email, String telefono, String direccion, String poblacion, String codpost, AsyncCallback<String> callback);
	
	void getReparaciones(AsyncCallback<String[]> callback) throws IllegalArgumentException;
	
	void getParaReparar(AsyncCallback<String[]> callback) throws IllegalArgumentException;
	
	void getParaRecoger(AsyncCallback<String[]> callback) throws IllegalArgumentException;
	
	void mostrarCliente(String dni,AsyncCallback<Cliente> callback)throws IllegalArgumentException;
	
	void addProductToServer(String f, String t, String p, String mar, String mod, String nSerie,String so, 
			String pSO, String des, String obs, String g, String pre, String rep, String recog, 
			String empleado,AsyncCallback<String> callback);
	
	void mostrarReparacion(String numSerie,AsyncCallback<Reparacion> callback)throws IllegalArgumentException;
	
	void repararProducto(String numSerie,AsyncCallback<String> callback)throws IllegalArgumentException;
	
	void recogerProducto(String numSerie,AsyncCallback<String> callback)throws IllegalArgumentException;
	
	void eliminarCliente(String dni, AsyncCallback<String> callback)throws IllegalArgumentException;
	
	void eliminarReparacion(String numSerie, AsyncCallback<String> callback)throws IllegalArgumentException;


	
}
