package dos.shared;


import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;


@SuppressWarnings("serial")
@PersistenceCapable
public class Reparacion implements java.io.Serializable  {
	
	//Atributos
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long id;
	@Persistent
	private String fecha;
	@Persistent
	private String tipo;
	@Persistent
	private String producto;
	@Persistent
	private String marca;
	@Persistent
	private String modelo;
	@Persistent
	private String numeroSerie;
	@Persistent
	private String sistemOperativo;
	@Persistent
	private String passSO;
	@Persistent
	private String descripcion;
	@Persistent
	private String observaciones;
	@Persistent
	private String garantia;
	@Persistent
	private String presupuesto;
	@Persistent
	private String reparar;
	@Persistent
	private String recoger;
	@Persistent
	private String empleado;
	
	//Constructor	
	
	
	public Reparacion(){
		
		
	}
	public Reparacion(String f, String t, String p, String mar, String mod, String nSerie,String so, 
			String pSO, String des, String obs, String g, String pre, String rep, String recog, String empleado) {
		this.fecha=f;
		this.tipo=t;
		this.producto=p;
		this.marca=mar;
		this.modelo=mod;
		this.numeroSerie=nSerie;
		this.sistemOperativo=so;
		this.passSO=pSO;
		this.descripcion=des;
		this.observaciones=obs;
		this.garantia=g;
		this.presupuesto=pre;
		this.reparar=rep;
		this.recoger=recog;
		this.empleado=empleado;
	}

	//Funciones
	public void setID(long n){
		this.id=n;
	}
	
	public long getID(){
		return this.id;
	}
	
	public void setFecha(String f){
		this.fecha=f;
	}
	
	public String getFecha(){
		return this.fecha;
	}
	
	public void setTipo(String t){
		this.tipo=t;
	}
	
	public String getTipo(){
		return this.tipo;
	}
	
	public void setProducto(String p){
		this.producto=p;
	}
	
	public String getProducto(){
		return this.producto;
	}
	
	public void setMarca(String m){
		this.marca=m;
	}
	
	public String getMarca(){
		return this.marca;
	}
	
	public void setModelo(String m){
		this.modelo=m;
	}
	
	public String getModelo(){
		return this.modelo;
	}
	
	public void setNumeroSerie(String ns){
		this.numeroSerie=ns;
	}
	
	public String getNumeroSerie(){
		return this.numeroSerie;
	}
	
	public void setSistemaOperativo(String so){
		this.sistemOperativo=so;
	}
	
	public String getSistemOperativo(){
		return this.sistemOperativo;
	}
	
	public void setPassSO(String passSO){
		this.passSO=passSO;
	}
	
	public String getPassSO(){
		return this.passSO;
	}
	
	public void setDescripcion(String d){
		this.descripcion=d;
	}
	
	public String getDescripcion(){
		return this.descripcion;
	}
	
	public void setObservaciones(String obs){
		this.observaciones=obs;
	}
	
	public String getObservaciones(){
		return this.observaciones;
	}
	
	public void setGarantia(String g){
		this.garantia=g;
	}
	
	public String getGarantia(){
		return this.garantia;
	}
	
	public void setPresupuesto(String p){
		this.presupuesto=p;
	}
	
	public String getPresupuesto(){
		return this.presupuesto;
	}
	
	public void setReparar(String r){
		this.reparar=r;
	}
	
	public String getReparar(){
		return this.reparar;
	}
	
	public void setRecoger(String r){
		this.recoger=r;
	}
	
	public String getRecoger(){
		return this.recoger;
	}
	
	public void setEmpleado(String e){
		this.empleado=e;
	}
	
	public String getEmpleado(){
		return this.empleado;
	}
}
