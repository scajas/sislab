package ec.edu.epn.laboratoriosBJ.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.primefaces.context.RequestContext;

import ec.edu.epn.laboratorioBJ.beans.ProveedorLabDAO;
import ec.edu.epn.laboratorioBJ.beans.TipoProveedorDAO;
import ec.edu.epn.laboratorioBJ.entities.ProveedorLab;
import ec.edu.epn.laboratorioBJ.entities.Tipoproveedor;
import ec.edu.epn.seguridad.VO.SesionUsuario;

@ManagedBean(name = "proveedorController")
@SessionScoped

public class ProveedorLabController implements Serializable {

	/** VARIABLES DE SESION ***/
	private static final long serialVersionUID = 6771930005130933302L;
	FacesContext fc = FacesContext.getCurrentInstance();
	HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
	HttpSession session = request.getSession();
	SesionUsuario su = (SesionUsuario) session.getAttribute("sesionUsuario");

	/******************************************************************/

	/** SERIVICIOS **/
	@EJB(lookup = "java:global/ServiciosSeguridadEPN/ProveedorLabDAOImplement!ec.edu.epn.laboratorioBJ.beans.ProveedorLabDAO")
	private ProveedorLabDAO proveedorI;// I (interface)
	
	@EJB(lookup = "java:global/ServiciosSeguridadEPN/TipoProveedorDAOImplement!ec.edu.epn.laboratorioBJ.beans.TipoProveedorDAO")
	private TipoProveedorDAO tipoProveedorI;// I (interface)

	private ProveedorLab proveedor;
	private List<ProveedorLab> listaProveedorLab = new ArrayList<ProveedorLab>();
	private ProveedorLab nuevoProveedor;
	private String nombreTP;
	private Tipoproveedor tipoProveedorSelect;
	private List<Tipoproveedor> tipo= new ArrayList<Tipoproveedor>();

	@PostConstruct
	public void init() {
		try {
			
			setListaProveedorLab(proveedorI.getAll(ProveedorLab.class));
			setNuevoProveedor(new ProveedorLab());
		    proveedor = new ProveedorLab();
		    nuevoProveedor = new ProveedorLab();
		    setTipo(tipoProveedorI.getAll(Tipoproveedor.class));
		    


		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//****** Modificar Proveedor ****//*

	public void modificarProveedor(ActionEvent event) {
		try {
			if (proveedor.getNombrePv().equals(getNombreTP())) {
				
				proveedor.setTipoproveedor(tipoProveedorSelect);
				proveedorI.update(proveedor);
				listaProveedorLab = proveedorI.getAll(ProveedorLab.class);

				RequestContext context = RequestContext.getCurrentInstance();
				context.execute("PF('modificarProveedor').hide();");

				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_INFO, "", "Proveedor actualizado exitosamente"));

			} else if (buscarProveedor(proveedor.getNombrePv()) == false) {
				
				proveedor.setTipoproveedor(tipoProveedorSelect);
				proveedorI.update(proveedor);
				listaProveedorLab = proveedorI.getAll(ProveedorLab.class);

				RequestContext context = RequestContext.getCurrentInstance();
				context.execute("PF('modificarProveedor').hide();");

				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_INFO, "", "Proveedor actualizado exitosamente"));
			} else {
				listaProveedorLab = proveedorI.getAll(ProveedorLab.class);
				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ha ocurrido un error", "El Proveedor ya existe."));
			}

		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Ha ocurrido un error"));
		}
	}

	//************Agregar Proveedor************//
	
	public void agregarProveedor(ActionEvent event) {
		
		try {
			if(buscarProveedor(nuevoProveedor.getNombrePv())==true){
				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
				new FacesMessage(FacesMessage.SEVERITY_ERROR,"ERROR!","Este Proveedor ya existe."));
				
				nuevoProveedor=new ProveedorLab();
				
				
			}else{
				
				nuevoProveedor.setTipoproveedor(tipoProveedorSelect);
				proveedorI.save(nuevoProveedor);
				listaProveedorLab=proveedorI.getAll(ProveedorLab.class);
				
				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_INFO,"", "El Proveedor se ha almacenado exitosamente"));
				
				nuevoProveedor = new ProveedorLab();
				tipoProveedorSelect = new Tipoproveedor();
			}
				
		} catch (Exception e) {
			System.out.println ("El error es: " + e.getMessage());
		    e.printStackTrace();
			// TODO: handle exception
			FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
				new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", ""));	
			e.printStackTrace(System.out);
			
		}
	}
	
	//****** Eliminar Proveedor ****//*

		public void eliminarProveedorLab(ActionEvent event) {

			try {

				proveedorI.delete(proveedor);
				listaProveedorLab = proveedorI.getAll(ProveedorLab.class);

				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_INFO, "",
								"El Proveedor se ha eliminado correctamente"));

			} catch (Exception e) {

				if (e.getMessage() == "Transaction rolled back") {
					FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
							new FacesMessage(FacesMessage.SEVERITY_FATAL, "NO SE PUEDE ELIMINAR EL REGISTRO!",
									"Ha ocurrido un error interno, comuniquese con el personal DGIP"));
				} else {
					FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
							new FacesMessage(FacesMessage.SEVERITY_FATAL, "ERROR!", ""));
				}

			}

		}

	
	//****** Busqueda Proveedor ****//*

		private boolean buscarProveedor(String valor) {
			
		
			try {
				listaProveedorLab = proveedorI.getAll(ProveedorLab.class);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println ("El error es: " + e.getMessage());
			    e.printStackTrace();
			}
			
			boolean resultado = false;
			for (ProveedorLab pv : listaProveedorLab) {
				if (pv.getNombrePv().trim().equals(valor.trim())) {
					resultado = true;
					break;
				} else {
					resultado = false;
				}
				
			}
			
			return resultado;
		}

		public void pasarNombre(String nombre) {
			setNombreTP(nombre);
		}
			
			
			
			
	//****** Getter y Setters ****//*

			

			public List<ProveedorLab> getListaProveedorLab() {
				return listaProveedorLab;
			}

			

			public ProveedorLab getProveedor() {
				return proveedor;
			}

			public void setProveedor(ProveedorLab proveedor) {
				this.proveedor = proveedor;
			}

			public void setListaProveedorLab(List<ProveedorLab> listaProveedorLab) {
				this.listaProveedorLab = listaProveedorLab;
			}

			public ProveedorLab getNuevoProveedor() {
				return nuevoProveedor;
			}

			public void setNuevoProveedor(ProveedorLab nuevoProveedor) {
				this.nuevoProveedor = nuevoProveedor;
			}

			
			public String getNombreTP() {
				return nombreTP;
			}

			public void setNombreTP(String nombreTP) {
				this.nombreTP = nombreTP;
			}


			public Tipoproveedor getTipoProveedorSelect() {
				return tipoProveedorSelect;
			}

			public void setTipoProveedorSelect(Tipoproveedor tipoProveedorSelect) {
				this.tipoProveedorSelect = tipoProveedorSelect;
			}

			public List<Tipoproveedor> getTipo() {
				return tipo;
			}

			public void setTipo(List<Tipoproveedor> tipo) {
				this.tipo = tipo;
			}

			

			

			
	
}
