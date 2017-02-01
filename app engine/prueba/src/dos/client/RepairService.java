package dos.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import dos.shared.Cliente;
import dos.shared.Reparacion;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("repair")
public interface RepairService extends RemoteService {
	String greetServer(String name) throws IllegalArgumentException;
	
	String addClientToServer(String nombre, String apellidos, String dni, String email, String telefono, String direccion, String poblacion, String codpost) throws IllegalArgumentException;
	
	String[] getReparaciones() throws IllegalArgumentException;
	
	String[] getParaReparar() throws IllegalArgumentException;
	
	String[] getParaRecoger() throws IllegalArgumentException;
	
	Cliente mostrarCliente(String dni) throws IllegalArgumentException;

	String addProductToServer(String f, String t, String p, String mar, String mod, String nSerie, String so, String pSO, String des, String obs, String g, String pre, String rep, String recog, String empleado)throws IllegalArgumentException;

	Reparacion mostrarReparacion(String numSerie)throws IllegalArgumentException;
	
	String repararProducto(String numSerie)throws IllegalArgumentException;
	
	String recogerProducto(String numSerie)throws IllegalArgumentException;
	
	String eliminarCliente (String dni)throws IllegalArgumentException;

	String eliminarReparacion (String numSerie)throws IllegalArgumentException;

}
