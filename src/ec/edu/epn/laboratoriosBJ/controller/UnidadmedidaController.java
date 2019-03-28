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

import ec.edu.epn.laboratorioBJ.beans.UnidadMedidaDAO;
import ec.edu.epn.laboratorioBJ.entities.Estadoproducto;
import ec.edu.epn.laboratorioBJ.entities.Unidadmedida;
import ec.edu.epn.seguridad.VO.SesionUsuario;

@ManagedBean(name = "unidadMedidaController")
@SessionScoped

public class UnidadmedidaController implements Serializable {

	/** VARIABLES DE SESION ***/
	private static final long serialVersionUID = 6771930005130933302L;
	FacesContext fc = FacesContext.getCurrentInstance();
	HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
	HttpSession session = request.getSession();
	SesionUsuario su = (SesionUsuario) session.getAttribute("sesionUsuario");

	/****************************************************************************/

	/** SERIVICIOS **/
	@EJB(lookup = "java:global/ServiciosSeguridadEPN/UnidadMedidaDAOImplement!ec.edu.epn.laboratorioBJ.beans.UnidadMedidaDAO")
	private UnidadMedidaDAO unidadMedidaI;

	/** VARIABLES DE LA CLASE **/

	private List<Unidadmedida> listUnidadMedida = new ArrayList<Unidadmedida>();
	private Unidadmedida nuevoUnidadMedida;
	private Unidadmedida modificarUnidadMedida; // select
	private String nombreTP;
	

	/** METODO INIT **/
	@PostConstruct
	public void init() {

		try {

			listUnidadMedida = unidadMedidaI.getAll(Unidadmedida.class);
			nuevoUnidadMedida = new Unidadmedida();
			modificarUnidadMedida = new Unidadmedida();
			

		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}

	/****** Guardar ****/
	public void agregarUnidadMedida(ActionEvent event) {
		try {

			
			if (buscarUnidadMedida(nuevoUnidadMedida.getMedidaUm()) == true) {
				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR!", "La unidad de medida ya existe."));

				
			} else {
				
				unidadMedidaI.save(nuevoUnidadMedida);
				listUnidadMedida = unidadMedidaI.getAll(Unidadmedida.class);

				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_INFO, "", "La unidad de medida se almacenado correctamente"));

				nuevoUnidadMedida = new Unidadmedida();

			}

		} catch (Exception e) {
			
			FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Ha ocurrido un error"));

		}

	}

	
	
	public void modificarUnidadMedida(ActionEvent event) {
		try {
			if (modificarUnidadMedida.getMedidaUm().equals(getNombreTP())) {
				unidadMedidaI.update(modificarUnidadMedida);
				listUnidadMedida = unidadMedidaI.getAll(Unidadmedida.class);

				RequestContext context = RequestContext.getCurrentInstance();
				context.execute("PF('modificarUnidadMedida').hide();");

				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_INFO, "", "Unidad de Medida actualizado exitosamente"));

			} else if (buscarUnidadMedida(modificarUnidadMedida.getMedidaUm()) == false) {
				unidadMedidaI.update(modificarUnidadMedida);
				listUnidadMedida = unidadMedidaI.getAll(Unidadmedida.class);
				RequestContext context = RequestContext.getCurrentInstance();
				context.execute("PF('modificarUnidadMedida').hide();");

				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_INFO, "", "Unidad de Medida actualizado exitosamente"));
			} else {
				
				listUnidadMedida = unidadMedidaI.getAll(Unidadmedida.class);
				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ha ocurrido un error", "La Unidad de Medida ya existe."));
			}

		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Ha ocurrido un error"));
		}
	}

	public void eliminarUnidadMedida(ActionEvent event) {
		try {

			unidadMedidaI.delete(modificarUnidadMedida);
			listUnidadMedida = unidadMedidaI.getAll(Unidadmedida.class);

			FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
					new FacesMessage(FacesMessage.SEVERITY_INFO, "", "La unidad de medida se a eliminado correctamente"));

		} catch (Exception e) {
			
			if (e.getMessage() == "Transaction rolled back") {
				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_FATAL, "NO SE PUEDE ELIMINAR EL REGISTRO!",
								"La tabla Unidad de Medida tiene relación con otra tabla"));
				
			} else {
				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Ha ocurrido un error"));


			}
			
			
			
			
		}

	}
	
	/****** Busqueda de Estado Producto ****/

	private boolean buscarUnidadMedida(String valor) {
		try {
			listUnidadMedida = unidadMedidaI.getAll(Unidadmedida.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		boolean resultado = false;
		for (Unidadmedida tipo : listUnidadMedida) {
			if (tipo.getMedidaUm().equals(valor)) {
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

	public List<Unidadmedida> getListUnidadMedida() {
		return listUnidadMedida;
	}

	public void setListUnidadMedida(List<Unidadmedida> listUnidadMedida) {
		this.listUnidadMedida = listUnidadMedida;
	}

	public Unidadmedida getNuevoUnidadMedida() {
		return nuevoUnidadMedida;
	}

	public void setNuevoUnidadMedida(Unidadmedida nuevoUnidadMedida) {
		this.nuevoUnidadMedida = nuevoUnidadMedida;
	}

	public Unidadmedida getModificarUnidadMedida() {
		return modificarUnidadMedida;
	}

	public void setModificarUnidadMedida(Unidadmedida modificarUnidadMedida) {
		this.modificarUnidadMedida = modificarUnidadMedida;
	}

	public String getNombreTP() {
		return nombreTP;
	}

	public void setNombreTP(String nombreTP) {
		this.nombreTP = nombreTP;
	}





}
