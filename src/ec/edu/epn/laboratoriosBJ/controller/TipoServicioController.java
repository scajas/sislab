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

import ec.edu.epn.laboratorioBJ.beans.TipoServicioDAO;
import ec.edu.epn.laboratorioBJ.entities.Tiposervicio;
import ec.edu.epn.seguridad.VO.SesionUsuario;

@ManagedBean(name = "tipoServicioController")
@SessionScoped
public class TipoServicioController implements Serializable {
	
	// ****************************************************************/
			// ********************* VARIABLES MANEJO SESION **********************/
			// ****************************************************************/

			/**
			* 
			*/
			private static final long serialVersionUID = 1L;
			FacesContext fc = FacesContext.getCurrentInstance();
			HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
			HttpSession session = request.getSession();
			SesionUsuario su = (SesionUsuario) session.getAttribute("sesionUsuario");

			// ****************************************************************/
			// ********************* DAOS **********************/
			// ****************************************************************/
			@EJB(lookup = "java:global/ServiciosSeguridadEPN/TipoServicioDAOImplement!ec.edu.epn.laboratorioBJ.beans.TipoServicioDAO")	
			private TipoServicioDAO tipoServicioI;
			
			// variable de la clase
			private Tiposervicio tiposervicio;
			private List<Tiposervicio> tiposservicio = new ArrayList<>();
			private List<Tiposervicio> filtroTipoServicio = new ArrayList<>();
			private Tiposervicio nuevoTipoServicio;
			private String nombreTS;
			
			// Mpetodo Init
		    @PostConstruct
		    public void init() {
		        try {	 	            
		            	
		            tiposservicio = tipoServicioI.getAll(Tiposervicio.class);		                        
		            tiposervicio =  new Tiposervicio();		            
		            nuevoTipoServicio =  new Tiposervicio();
		          
		 
		 
		        } catch (Exception e) {
		            // TODO Auto-generated catch block
		            e.printStackTrace();
		        }
		    }
		 
		    /******* Método Guardar *******/
		 
		    public void agregarTipoServicio(ActionEvent event) {
		 
		        try {
		            if (buscarTipoServicio(nuevoTipoServicio.getNombreTs()) == true) {
		                FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
		                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ha ocurrido un error", "El tipo de servicio ("
		                                + nuevoTipoServicio.getNombreTs() + ") ya existe."));     
		                nuevoTipoServicio =  new Tiposervicio();
		 
		 
		            } else {   
		                tipoServicioI.save(nuevoTipoServicio);	 	                
		                
		                tiposservicio = tipoServicioI.getAll(Tiposervicio.class);
		 
		                FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
		                        new FacesMessage(FacesMessage.SEVERITY_INFO, "", "Tipo Servicio almacenado exitosamente"));
		 
		                nuevoTipoServicio =  new Tiposervicio();                
		 
		            }
		 
		        } catch (Exception e) {
		            FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
		                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Ha ocurrido un error"));
		 
		 
		 
		        }
		 
		    }
		 
		 
		
		    /******* Método Modificar *******/
		    
			public void modificarTipoServicio(ActionEvent event) {
				try {
					if (tiposervicio.getNombreTs().equals(getNombreTS())) {						
						tipoServicioI.update(tiposervicio);						
						tiposservicio = tipoServicioI.getAll(Tiposervicio.class);

						RequestContext context = RequestContext.getCurrentInstance();
						context.execute("PF('modificarTipoServicio').hide();");

						FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
								new FacesMessage(FacesMessage.SEVERITY_INFO, "", "Tipo Servicio actualizado exitosamente"));

					} else if (buscarTipoServicio(tiposervicio.getNombreTs()) == false) {
												
						tipoServicioI.update(tiposervicio);						
						tiposservicio =  tipoServicioI.getAll(Tiposervicio.class);
						RequestContext context = RequestContext.getCurrentInstance();
						context.execute("PF('modificarTipoServicio').hide();");

						FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
								new FacesMessage(FacesMessage.SEVERITY_INFO, "", "Tipo Servicio actualizado exitosamente"));
					} else {						
						tiposservicio =  tipoServicioI.getAll(Tiposervicio.class);
						FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
								new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ha ocurrido un error", "El Tipo Servicio ("
		                                + nuevoTipoServicio.getNombreTs() + ") ya existe."));
					}

				} catch (Exception e) {
					FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
							new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Ha ocurrido un error"));
				}
			}
		 
		    
			/******* Método Eliminar *******/
		 
		    public void eliminarTipoServicio(ActionEvent event) {
		        try {
		 		            	 
		            tipoServicioI.delete(tiposervicio);		            
		            tiposservicio =  tipoServicioI.getAll(Tiposervicio.class);
		 
		            FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
		                    new FacesMessage(FacesMessage.SEVERITY_INFO, "", "Tipo Servicio eliminada exitosamente"));
		 
		        } catch (Exception e) {
		 
		            if (e.getMessage() == "Transaction rolled back") {
		                FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
		                		new FacesMessage(FacesMessage.SEVERITY_ERROR, "NO PUEDE ELIMINAR EL REGISTRO!",
										"La tabla Grado tiene relación con otra tabla"));
		            } else {
		 
		                FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
		                        new FacesMessage(FacesMessage.SEVERITY_FATAL, "", "Ha ocurrido un error"));
		            }
		 
		        }
		 
		 
		    }
		 
		 
			/******* Validación  *******/
		 

			private boolean buscarTipoServicio(String valor) {
				
				try {					
					tiposservicio =  tipoServicioI.getAll(Tiposervicio.class);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				boolean resultado = false;
				for (Tiposervicio tipo : tiposservicio) {
					if (tipo.getNombreTs().equals(valor)) {
						resultado = true;
						break;
					} else {
						resultado = false;
					}
				}
				
				return resultado;
			}

			public void pasarNombre(String nombre) {
				setNombreTS(nombre);
			}

			/******* GET and SET  *******/
			
			public Tiposervicio getTiposervicio() {
				return tiposervicio;
			}
			public void setTiposervicio(Tiposervicio tiposervicio) {
				this.tiposervicio = tiposervicio;
			}
			public List<Tiposervicio> getTiposservicio() {
				return tiposservicio;
			}
			public void setTiposservicio(List<Tiposervicio> tiposservicio) {
				this.tiposservicio = tiposservicio;
			}
			public List<Tiposervicio> getFiltroTipoServicio() {
				return filtroTipoServicio;
			}
			public void setFiltroTipoServicio(List<Tiposervicio> filtroTipoServicio) {
				this.filtroTipoServicio = filtroTipoServicio;
			}
			public Tiposervicio getNuevoTipoServicio() {
				return nuevoTipoServicio;
			}
			public void setNuevoTipoServicio(Tiposervicio nuevoTipoServicio) {
				this.nuevoTipoServicio = nuevoTipoServicio;
			}
			public String getNombreTS() {
				return nombreTS;
			}
			public void setNombreTS(String nombreTS) {
				this.nombreTS = nombreTS;
			}
			
			
			
}
