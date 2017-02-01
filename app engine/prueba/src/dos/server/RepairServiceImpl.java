package dos.server;


import dos.client.RepairService;
import dos.shared.*;

import java.util.List;

import javax.jdo.*;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class RepairServiceImpl extends RemoteServiceServlet implements RepairService {



public String greetServer(String input) throws IllegalArgumentException {
  return "";
}

//Añade un cliente al servidor

public String addClientToServer(String nombre, String apellidos, String dni, String email, String telefono, String direccion, String poblacion, String codpost) throws IllegalArgumentException {

	
	String mid;
  final Cliente cliente= new Cliente (nombre, apellidos, dni, email, telefono, direccion, poblacion, codpost);

  PersistenceManager pm = Pmf.get().getPersistenceManager();

  try {
	 pm.makePersistent(cliente);
	 System.out.println(pm.getObjectId(cliente));
     mid = pm.getObjectId(cliente).toString();
  } finally {
  pm.close();
  }
  return mid;
}


public String[] getReparaciones() throws IllegalArgumentException {

	  String reparacionesA[];

	  PersistenceManager pm = Pmf.get().getPersistenceManager();
	  
	  Query query = pm.newQuery(Reparacion.class);
	  
	  @SuppressWarnings("unchecked")
	  List<Reparacion> reparaciones = (List<Reparacion>) query.execute();
	  
	  reparacionesA = new String[reparaciones.size()];
	  
	  for(int i = 0; i< reparaciones.size(); i++){
		  
		  
			  reparacionesA[i] = String.valueOf(reparaciones.get(i).getID());
			  System.out.println(reparacionesA[i]);
		  
	  }
	  
	  
	  return reparacionesA;
	
}

public String[] getParaReparar() throws IllegalArgumentException {
		
	  String reparacionesA[];
	  int j=0;
	  PersistenceManager pm = Pmf.get().getPersistenceManager();
	  
	  Query query = pm.newQuery(Reparacion.class);
	  
	  @SuppressWarnings("unchecked")
	  List<Reparacion> reparaciones = (List<Reparacion>) query.execute();
	  
	  reparacionesA = new String[reparaciones.size()];
	  
	  for(int i = 0; i< reparaciones.size(); i++){
		  
		  if (reparaciones.get(i).getReparar().equals("no")){
			  reparacionesA[j] = String.valueOf(reparaciones.get(i).getID());
			  System.out.println(reparacionesA[j]);
			  j++;
		  }
	  }
	  
	  
	  return reparacionesA;
	
}

public String[] getParaRecoger() throws IllegalArgumentException {

	  String reparacionesA[];
	  int j=0;
	  PersistenceManager pm = Pmf.get().getPersistenceManager();
	  
	  Query query = pm.newQuery(Reparacion.class);
	  
	  @SuppressWarnings("unchecked")
	  List<Reparacion> reparaciones = (List<Reparacion>) query.execute();
	  
	  reparacionesA = new String[reparaciones.size()];
	  
	  for(int i = 0; i< reparaciones.size(); i++){
		  
		  if (reparaciones.get(i).getRecoger().equals("si")){
			  reparacionesA[j] = String.valueOf(reparaciones.get(i).getID());
			  System.out.println(reparacionesA[j]);
			  j++;
		  }
	  }
	  
	  
	  return reparacionesA;
	
}

//Muestra un cliente en caso de que esté en la base de datos
public Cliente mostrarCliente(String dni)throws IllegalArgumentException{
	
	
	PersistenceManager pm = Pmf.get().getPersistenceManager(); 
	
	
	final Cliente cliente = pm.getObjectById(Cliente.class, dni);
	
	return cliente;

	
	
}

//añade un producto al servidor

public String addProductToServer(String f, String t, String p, String mar, String mod, String nSerie, String so,
		String pSO, String des, String obs, String g, String pre, String rep, String recog, String empleado)throws IllegalArgumentException{
	String mid;
	final Reparacion reparacion = new Reparacion (f,t,p,mar,mod,nSerie,so,pSO,des,obs,g,pre,rep,recog,empleado);
	
	PersistenceManager pm = Pmf.get().getPersistenceManager();

	  try {
		  	pm.makePersistent(reparacion);
			 System.out.println(pm.getObjectId(reparacion));
			 reparacion.setID(Long.parseLong((pm.getObjectId(reparacion)).toString()));
		     pm.makePersistent(reparacion);
		     mid = pm.getObjectId(reparacion).toString();
	  } finally {
	  pm.close();
	  }
	  
	  	  
	  return mid;
}

//Muestra un producto de la base de datos

public Reparacion mostrarReparacion(String id)throws IllegalArgumentException{
	PersistenceManager pm = Pmf.get().getPersistenceManager(); 
	long l = Long.parseLong(id);
	final Reparacion reparacion = pm.getObjectById(Reparacion.class, l);
	return reparacion;
}

public String repararProducto(String numSerie)throws IllegalArgumentException{
	PersistenceManager pm = Pmf.get().getPersistenceManager(); 
	long l = Long.parseLong(numSerie);
	final Reparacion reparacion = pm.getObjectById(Reparacion.class, l);
	reparacion.setReparar("si");
	return "Producto "+reparacion.getNumeroSerie()+" reparado";// reparacion.toString();
}

//Cambia un producto a estado recoger
public String recogerProducto(String numSerie)throws IllegalArgumentException{
	PersistenceManager pm = Pmf.get().getPersistenceManager();
	long l = Long.parseLong(numSerie);
	final Reparacion reparacion = pm.getObjectById(Reparacion.class, l);
	reparacion.setRecoger("si");
	return "Producto "+reparacion.getNumeroSerie()+" dispuesto para recogida";
}

//Elimina un cliente de la base de datos
public String eliminarCliente(String dni) {
	 
	PersistenceManager pm = Pmf.get().getPersistenceManager(); 
		Cliente cliente = pm.getObjectById(Cliente.class, dni);
		try {
			pm.deletePersistent(cliente);
			}
			finally {
				pm.close();
			}

	return "Cliente eliminado";
}

//Elimina una reparacion de la base de datos
public String eliminarReparacion(String numSerie) {
	 
	PersistenceManager pm = Pmf.get().getPersistenceManager();
	long l = Long.parseLong(numSerie);
		Reparacion reparacion = pm.getObjectById(Reparacion.class, l);
		try {
			pm.deletePersistent(reparacion);
			}
			finally {
				pm.close();
			}
	
	return "Producto eliminado de reparaciones";
}



}
