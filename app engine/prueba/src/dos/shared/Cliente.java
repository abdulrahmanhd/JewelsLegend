package dos.shared;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@SuppressWarnings("serial")
@PersistenceCapable

public class Cliente implements java.io.Serializable  {
	
	//Atributos
	@Persistent
	private String nombre;
	@Persistent
	private String apellidos;
	@PrimaryKey
	@Persistent
	private String nif;
	@Persistent
	private String telefono;
	@Persistent
	private String direccion;
	@Persistent
	private String localidad;
	@Persistent
	private String codigoPostal;
	@Persistent
	private String email;
	
	//Constructor
	
	
	public Cliente(){
		
		
	}
	
	public Cliente(String n, String a, String nif, String e, String t, String l, String dir,String cp) {
		this.nombre=n;
		this.apellidos=a;
		this.nif=nif;
		this.email=e;
		this.telefono=t;
		this.direccion=dir;
		this.localidad=l;
		this.codigoPostal=cp;
	}

	//Funciones
	public void setNombre(String n){
		this.nombre=n;
	}
	
	public String getNombre(){
		return this.nombre;
	}
	
	public void setApellidos(String a){
		this.apellidos=a;
	}
	
	public String getApellidos(){
		return this.apellidos;
	}
	
	public void setNIF(String nif){
		this.nif=nif;
	}
	
	public String getNIF(){
		return this.nif;
	}
	
	public void setDireccion(String d){
		this.direccion=d;
	}
	
	public String getDireccion(){
		return this.direccion;
	}
	
	public void setLocalidad(String l){
		this.localidad=l;
	}
	
	public String getLocalidad(){
		return this.localidad;
	}
	
	public void setCodigoPostal(String cp){
		this.codigoPostal=cp;
	}
	
	public String getCodigoPostal(){
		return this.codigoPostal;
	}
	
	public void setEmail(String e){
		this.email=e;
	}
	
	public String getEmail(){
		return this.email;
	}
	
	public void setTelefono(String t){
		this.telefono=t;
	}
	
	public String getTelefono(){
		return this.telefono;
	}
}
