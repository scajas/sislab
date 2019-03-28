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

import ec.edu.epn.laboratorioBJ.beans.PresentacionDAO;
import ec.edu.epn.laboratorioBJ.entities.Estadoproducto;
import ec.edu.epn.laboratorioBJ.entities.Presentacion;
import ec.edu.epn.seguridad.VO.SesionUsuario;



@ManagedBean(name = "presentacionController")
@SessionScoped
public class PresentacionController implements Serializable {

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
		@EJB(lookup = "java:global/ServiciosSeguridadEPN/PresentacionDAOImplement!ec.edu.epn.laboratorioBJ.beans.PresentacionDAO")
		
		private PresentacionDAO presentacionI;
		 
	    // variables de la clase	    
	    private Presentacion presentacion;	    
	    private List<Presentacion> Presentaciones = new ArrayList<>();	    
	    private List<Presentacion> filtroPresentacion = new ArrayList<>();	    
	    private Presentacion nuevoPresentacion;
		private String nombreTP;
	 
	    // Mpetodo Init
	    @PostConstruct
	    public void init() {
	        try {
	 	            
	            Presentaciones=presentacionI.getAll(Presentacion.class);	            
	            presentacion=new Presentacion();	            
	            nuevoPresentacion =new Presentacion();
	          
	 
	 
	        } catch (Exception e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
	    }
	 
	    /******* Método Guardar *******/
	 
	    public void agregarPresentacion(ActionEvent event) {
	 
	        try {
	            if (buscarPresentacion(nuevoPresentacion.getNombrePrs()) == true) {
	                FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
	                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ha ocurrido un error", "La presentación ("
	                                + nuevoPresentacion.getNombrePrs() + ") ya existe."));	                
	                nuevoPresentacion = new Presentacion();
	 
	 
	            } else {	                
	                presentacionI.save(nuevoPresentacion);
	 	                
	                Presentaciones = presentacionI.getAll(Presentacion.class);
	 
	                FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
	                        new FacesMessage(FacesMessage.SEVERITY_INFO, "", "Presentación almacenado exitosamente"));
	 
	                nuevoPresentacion = new Presentacion();	                
	 
	            }
	 
	        } catch (Exception e) {
	            FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
	                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Ha ocurrido un error"));
	 
	 
	 
	        }
	 
	    }
	 
	 
	
	    /******* Método Modificar *******/
	    
		public void modificarPresentacion(ActionEvent event) {
			try {
				if (presentacion.getNombrePrs().equals(getNombreTP())) {
					presentacionI.update(presentacion);
					Presentaciones = presentacionI.getAll(Presentacion.class);

					RequestContext context = RequestContext.getCurrentInstance();
					context.execute("PF('modificarPresentacion').hide();");

					FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
							new FacesMessage(FacesMessage.SEVERITY_INFO, "", "Presentación actualizado exitosamente"));

				} else if (buscarPresentacion(presentacion.getNombrePrs()) == false) {
					
					presentacionI.update(presentacion);
					Presentaciones = presentacionI.getAll(Presentacion.class);
					RequestContext context = RequestContext.getCurrentInstance();
					context.execute("PF('modificarPresentacion').hide();");

					FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
							new FacesMessage(FacesMessage.SEVERITY_INFO, "", "Presentación actualizado exitosamente"));
				} else {
					Presentaciones = presentacionI.getAll(Presentacion.class);
					FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
							new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ha ocurrido un error", "La Presentación ya existe."));
				}

			} catch (Exception e) {
				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Ha ocurrido un error"));
			}
		}
	 
	    
		/******* Método Eliminar *******/
	 
	    public void eliminarPresentacion(ActionEvent event) {
	        try {
	 
	            presentacionI.delete(presentacion);	            
	            Presentaciones = presentacionI.getAll(Presentacion.class);
	 
	            FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
	                    new FacesMessage(FacesMessage.SEVERITY_INFO, "", "Presentación eliminada exitosamente"));
	 
	        } catch (Exception e) {
	 
	            if (e.getMessage() == "Transaction rolled back") {
	                FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
	                        new FacesMessage(FacesMessage.SEVERITY_FATAL, "",
	                                "Ha ocurrido un error interno, comuniquese con el personal DGIP"));
	            } else {
	 
	                FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
	                        new FacesMessage(FacesMessage.SEVERITY_FATAL, "", "Ha ocurrido un error"));
	            }
	 
	        }
	 
	 
	    }
	 
	 
		/******* Validación  *******/
	 

		private boolean buscarPresentacion(String valor) {
			
			try {
				Presentaciones = presentacionI.getAll(Presentacion.class);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			boolean resultado = false;
			for (Presentacion tipo : Presentaciones) {
				if (tipo.getNombrePrs().equals(valor)) {
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

		/******* GET and SET  *******/
	    

		public Presentacion getPresentacion() {
			return presentacion;
		}



		public void setPresentacion(Presentacion presentacion) {
			this.presentacion = presentacion;
		}



		public List<Presentacion> getPresentaciones() {
			return Presentaciones;
		}



		public void setPresentaciones(List<Presentacion> presentaciones) {
			Presentaciones = presentaciones;
		}



		public List<Presentacion> getFiltroPresentacion() {
			return filtroPresentacion;
		}



		public void setFiltroPresentacion(List<Presentacion> filtroPresentacion) {
			this.filtroPresentacion = filtroPresentacion;
		}



		public Presentacion getNuevoPresentacion() {
			return nuevoPresentacion;
		}



		public void setNuevoPresentacion(Presentacion nuevoPresentacion) {
			this.nuevoPresentacion = nuevoPresentacion;
		}

		public String getNombreTP() {
			return nombreTP;
		}

		public void setNombreTP(String nombreTP) {
			this.nombreTP = nombreTP;
		}
	 


		
	
	
		

}
