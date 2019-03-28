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

import ec.edu.epn.laboratorioBJ.beans.HidratacionDAO;
import ec.edu.epn.laboratorioBJ.entities.Hidratacion;
import ec.edu.epn.seguridad.VO.SesionUsuario;

@ManagedBean(name = "hidratacionController")
@SessionScoped

public class HidratacionController implements Serializable {

	/** VARIABLES DE SESION ***/
	private static final long serialVersionUID = 6771930005130933302L;
	FacesContext fc = FacesContext.getCurrentInstance();
	HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
	HttpSession session = request.getSession();
	SesionUsuario su = (SesionUsuario) session.getAttribute("sesionUsuario");

	/******************************************************************/

	/** SERIVICIOS **/
	@EJB(lookup = "java:global/ServiciosSeguridadEPN/HidratacionDAOImplement!ec.edu.epn.laboratorioBJ.beans.HidratacionDAO")
	private HidratacionDAO hidratacionI;// I (interface)

	private Hidratacion hidratacion;
	private List<Hidratacion> listaHidratacion = new ArrayList<Hidratacion>();
	private Hidratacion nuevoHidratacion;
	private String nombreTP;

	@PostConstruct
	public void init() {
		try {

			setListaHidratacion(hidratacionI.getAll(Hidratacion.class));
			setNuevoHidratacion(new Hidratacion());
			hidratacion = new Hidratacion();
			nuevoHidratacion = new Hidratacion();


		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//****** Modificar hidratacion ****//*

	public void modificarHidratacion(ActionEvent event) {
		
			try {
				if (hidratacion.getNombreHi().equals(getNombreTP())) {
					hidratacionI.update(hidratacion);
					listaHidratacion = hidratacionI.getAll(Hidratacion.class);

					RequestContext context = RequestContext.getCurrentInstance();
					context.execute("PF('modificarHidratacion').hide();");

					FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
							new FacesMessage(FacesMessage.SEVERITY_INFO, "", "Hidratación actualizada exitosamente"));

				} else if (buscarHidratacion(hidratacion.getNombreHi()) == false) {
					hidratacionI.update(hidratacion);
					listaHidratacion = hidratacionI.getAll(Hidratacion.class);

					RequestContext context = RequestContext.getCurrentInstance();
					context.execute("PF('modificarHidratacion').hide();");

					FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
							new FacesMessage(FacesMessage.SEVERITY_INFO, "", "Hidratación actualizada exitosamente"));
				} else {
					
					listaHidratacion = hidratacionI.getAll(Hidratacion.class);
					FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
							new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ha ocurrido un error", "La Hidratación ya existe."));
				}

			} catch (Exception e) {
				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Ha ocurrido un error"));
			}
		}
	
	
	//************Agregar Hidratación************//
	
	public void agregarHidratacion(ActionEvent event) {

		try {
			if (buscarHidratacion(nuevoHidratacion.getNombreHi()) == true) {

				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR!", "Esta Hidratación ya existe."));
				
				

			} else {
				hidratacionI.save(nuevoHidratacion);
				listaHidratacion = hidratacionI.getAll(Hidratacion.class);

				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_INFO, "",
								"La Hidratación se ha almacenado exitosamente"));

				nuevoHidratacion = new Hidratacion();
			}

		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR!", ""));
		}
		

	}
	
	
	//****** Eliminar Hidratacion ****//*

		public void eliminarHidratacion(ActionEvent event) {

			try {

				hidratacionI.delete(hidratacion);
				listaHidratacion = hidratacionI.getAll(Hidratacion.class);

				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_INFO, "",
								"La Hidratación se ha eliminado correctamente"));

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
		
		//****** Busqueda de Hidratacion ****//*

		private boolean buscarHidratacion(String valor) {
			try {
				listaHidratacion = hidratacionI.getAll(Hidratacion.class);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			boolean resultado = false;
			for (Hidratacion tipo : listaHidratacion) {
				if (tipo.getNombreHi().trim().equals(valor.trim())) {
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
		
		

	public List<Hidratacion> getListaHidratacion() {
		return listaHidratacion;
	}

	public void setListaHidratacion(List<Hidratacion> listaHidratacion) {
		this.listaHidratacion = listaHidratacion;
	}

	public Hidratacion getHidratacion() {
		return hidratacion;
	}

	public void setHidratacion(Hidratacion hidratacion) {
		this.hidratacion = hidratacion;
	}

	public Hidratacion getNuevoHidratacion() {
		return nuevoHidratacion;
	}

	public void setNuevoHidratacion(Hidratacion nuevoHidratacion) {
		this.nuevoHidratacion = nuevoHidratacion;
	}
	
	

	public String getNombreTP() {
		return nombreTP;
	}

	public void setNombreTP(String nombreTP) {
		this.nombreTP = nombreTP;
	}

}