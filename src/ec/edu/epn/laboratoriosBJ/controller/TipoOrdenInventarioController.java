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

import ec.edu.epn.laboratorioBJ.beans.TipoOrdenInventarioDAO;
import ec.edu.epn.laboratorioBJ.entities.Estadoproducto;
import ec.edu.epn.laboratorioBJ.entities.Tipordeninv;
import ec.edu.epn.seguridad.VO.SesionUsuario;

@ManagedBean(name = "tipoOrdenInventarioController")
@SessionScoped

public class TipoOrdenInventarioController implements Serializable {

	/** VARIABLES DE SESION ***/
	private static final long serialVersionUID = 6771930005130933302L;
	FacesContext fc = FacesContext.getCurrentInstance();
	HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
	HttpSession session = request.getSession();
	SesionUsuario su = (SesionUsuario) session.getAttribute("sesionUsuario");

	/****************************************************************************/

	/** SERIVICIOS **/
	@EJB(lookup = "java:global/ServiciosSeguridadEPN/TipoOrdenInventarioDAOImplement!ec.edu.epn.laboratorioBJ.beans.TipoOrdenInventarioDAO")
	private TipoOrdenInventarioDAO tipoOrdenInventarioI;

	/** VARIABLES DE LA CLASE **/

	private List<Tipordeninv> listTipoInventario = new ArrayList<Tipordeninv>();
	private Tipordeninv nuevoTipoInventario;
	private Tipordeninv modificarTipoInventario; // select
	private String nombreTP;
	

	/** METODO INIT **/
	@PostConstruct
	public void init() {

		try {

			listTipoInventario = tipoOrdenInventarioI.getAll(Tipordeninv.class);
			nuevoTipoInventario = new Tipordeninv();
			modificarTipoInventario = new Tipordeninv();
			

		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}

	/****** Guardar ****/
	public void agregarTipoInventario(ActionEvent event) {
		try {

			
			if (buscarTipoOrdenInventario(nuevoTipoInventario.getNombreToi()) == true) {
				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR!", "El Tipo Orden de Inventario ya existe."));

				
			} else {
				
				tipoOrdenInventarioI.save(nuevoTipoInventario);
				listTipoInventario = tipoOrdenInventarioI.getAll(Tipordeninv.class);

				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_INFO, "", "El Tipo Orden de Inventario se almacenado correctamente"));

				nuevoTipoInventario = new Tipordeninv();

			}

		} catch (Exception e) {
			
			FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Ha ocurrido un error"));

		}

	}

	
	
	public void modificarTipoInventario(ActionEvent event) {
		try {
			if (modificarTipoInventario.getNombreToi().equals(getNombreTP())) {
				tipoOrdenInventarioI.update(modificarTipoInventario);
				listTipoInventario = tipoOrdenInventarioI.getAll(Tipordeninv.class);

				RequestContext context = RequestContext.getCurrentInstance();
				context.execute("PF('modificarTipoOrden').hide();");

				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_INFO, "", "Tipo Orden de Inventario actualizado exitosamente"));

			} else if (buscarTipoOrdenInventario(modificarTipoInventario.getNombreToi()) == false) {
				tipoOrdenInventarioI.update(modificarTipoInventario);
				listTipoInventario = tipoOrdenInventarioI.getAll(Tipordeninv.class);

				RequestContext context = RequestContext.getCurrentInstance();
				context.execute("PF('modificarTipoOrden').hide();");

				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_INFO, "", "Tipo Orden de Inventario actualizado exitosamente"));
			} else {
				listTipoInventario = tipoOrdenInventarioI.getAll(Tipordeninv.class);
				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ha ocurrido un error", "El Tipo Orden de Inventario ya existe."));
			}

		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Ha ocurrido un error"));
		}
	}

	public void eliminarTipoInventario(ActionEvent event) {
		try {

			tipoOrdenInventarioI.delete(modificarTipoInventario);
			listTipoInventario = tipoOrdenInventarioI.getAll(Tipordeninv.class);

			FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
					new FacesMessage(FacesMessage.SEVERITY_INFO, "", "Tipo Orden de Inventario eliminado correctamente"));

		} catch (Exception e) {
			
			if (e.getMessage() == "Transaction rolled back") {
				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_FATAL, "NO SE PUEDE ELIMINAR EL REGISTRO!",
								"La tabla Tipo Orden de Inventario tiene relación con otra tabla"));
				
			} else {
				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Ha ocurrido un error"));


			}
			
			
			
			
		}

	}
	
	/****** Busqueda de Estado Producto ****/

	private boolean buscarTipoOrdenInventario(String valor) {
		try {
			listTipoInventario = tipoOrdenInventarioI.getAll(Tipordeninv.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		boolean resultado = false;
		for (Tipordeninv tipo : listTipoInventario) {
			if (tipo.getNombreToi().equals(valor)) {
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



	/** GET AND SET **/

	public List<Tipordeninv> getListTipoInventario() {
		return listTipoInventario;
	}

	public void setListTipoInventario(List<Tipordeninv> listTipoInventario) {
		this.listTipoInventario = listTipoInventario;
	}

	public Tipordeninv getNuevoTipoInventario() {
		return nuevoTipoInventario;
	}

	public void setNuevoTipoInventario(Tipordeninv nuevoTipoInventario) {
		this.nuevoTipoInventario = nuevoTipoInventario;
	}

	public Tipordeninv getModificarTipoInventario() {
		return modificarTipoInventario;
	}

	public void setModificarTipoInventario(Tipordeninv modificarTipoInventario) {
		this.modificarTipoInventario = modificarTipoInventario;
	}

	public String getNombreTP() {
		return nombreTP;
	}

	public void setNombreTP(String nombreTP) {
		this.nombreTP = nombreTP;
	}
	
	

}
