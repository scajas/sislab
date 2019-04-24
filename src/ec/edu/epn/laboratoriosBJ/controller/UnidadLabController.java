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


import ec.edu.epn.laboratorioBJ.beans.UnidadDAO;
import ec.edu.epn.laboratorioBJ.entities.UnidadLabo;
import ec.edu.epn.seguridad.VO.SesionUsuario;

@ManagedBean(name = "unidadLaboController")
@SessionScoped

public class UnidadLabController implements Serializable {

	/** VARIABLES DE SESION ***/
	private static final long serialVersionUID = 6771930005130933302L;
	FacesContext fc = FacesContext.getCurrentInstance();
	HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
	HttpSession session = request.getSession();
	SesionUsuario su = (SesionUsuario) session.getAttribute("sesionUsuario");

	/******************************************************************/

	/** SERIVICIOS **/
	@EJB(lookup = "java:global/ServiciosSeguridadEPN/UnidadDAOImplement!ec.edu.epn.laboratorioBJ.beans.UnidadDAO")
	private UnidadDAO unidadI;// I (interface)
	
	
	
	private UnidadLabo unidad;
	private List<UnidadLabo> listaUnidadLabo = new ArrayList<UnidadLabo>();
	private UnidadLabo nuevoUnidad;
	private String nombreTP;
	private List<UnidadLabo> filtroUnidad;
	
	@PostConstruct
	public void init() {
		try {
			
			setListaUnidadLab(unidadI.getAll(UnidadLabo.class));
			setNuevoUnidad(new UnidadLabo());
			unidad = new UnidadLabo();
			nuevoUnidad = new UnidadLabo();
		    
		    


		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//****** Modificar Unidad********//
		public void modificarUnidad(ActionEvent event) {
			
				try {
					if (unidad.getNombreU().equals(getNombreTP())) {
						unidadI.update(unidad);
						listaUnidadLabo = unidadI.getAll(UnidadLabo.class);

						RequestContext context = RequestContext.getCurrentInstance();
						context.execute("PF('modificarUnidad')");

						FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
								new FacesMessage(FacesMessage.SEVERITY_INFO, "", "Unidad actualizada exitosamente"));

					} else if (buscarUnidad(unidad.getNombreU()) == false) {
						unidadI.update(unidad);
						listaUnidadLabo = unidadI.getAll(UnidadLabo.class);

						RequestContext context = RequestContext.getCurrentInstance();
						context.execute("PF('modificarUnidad')");

						FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
								new FacesMessage(FacesMessage.SEVERITY_INFO, "", "Unidad actualizada exitosamente"));
					} else {
						
						listaUnidadLabo = unidadI.getAll(UnidadLabo.class);
						FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
								new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ha ocurrido un error", "La Unidad ya existe."));
					}

				} catch (Exception e) {
					FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
							new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Ha ocurrido un error"));
				}
			}
		
		
		//************Agregar Unidad************//
		
		public void agregarUnidad(ActionEvent event) {

			try {
				if (buscarUnidad(nuevoUnidad.getNombreU()) == true) {

					FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
							new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR!", "Esta Unidad ya existe."));
					
					nuevoUnidad = new UnidadLabo();

				} else {
					unidadI.save(nuevoUnidad);
					listaUnidadLabo = unidadI.getAll(UnidadLabo.class);

					FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
							new FacesMessage(FacesMessage.SEVERITY_INFO, "",
									"La Unidad se ha almacenado exitosamente"));

					nuevoUnidad = new UnidadLabo();
				}

			} catch (Exception e) {
				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR!", ""));
			}
			
		}
		
		//****** Eliminar Unidad ****//*

				public void eliminarUnidad(ActionEvent event) {

					try {

						unidadI.delete(unidad);
						listaUnidadLabo = unidadI.getAll(UnidadLabo.class);

						FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
								new FacesMessage(FacesMessage.SEVERITY_INFO, "",
										"La Unidad se ha eliminado correctamente"));

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
				
				//****** Busqueda de Unidad ****//*

				private boolean buscarUnidad(String valor) {
					try {
						listaUnidadLabo = unidadI.getAll(UnidadLabo.class);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					boolean resultado = false;
					for (UnidadLabo uni : listaUnidadLabo) {
						if (uni.getNombreU().trim().equals(valor.trim())) {
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
		
		

	public List<UnidadLabo> getListaUnidadLab() {
		return listaUnidadLabo;
	}

	public void setListaUnidadLab(List<UnidadLabo> listaUnidadLabo) {
		this.listaUnidadLabo = listaUnidadLabo;
	}

	public UnidadLabo getNuevoUnidad() {
		return nuevoUnidad;
	}

	public void setNuevoUnidad(UnidadLabo nuevoUnidad) {
		this.nuevoUnidad = nuevoUnidad;
	}

	public UnidadLabo getUnidad() {
		return unidad;
	}

	public void setUnidad(UnidadLabo unidad) {
		this.unidad = unidad;
	}

	public String getNombreTP() {
		return nombreTP;
	}

	public void setNombreTP(String nombreTP) {
		this.nombreTP = nombreTP;
	}

	public List<UnidadLabo> getFiltroUnidad() {
		return filtroUnidad;
	}

	public void setFiltroUnidad(List<UnidadLabo> filtroUnidad) {
		this.filtroUnidad = filtroUnidad;
	}

	
	
}
