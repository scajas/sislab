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

import ec.edu.epn.laboratorioBJ.beans.GradoDAO;
import ec.edu.epn.laboratorioBJ.entities.Grado;

import ec.edu.epn.seguridad.VO.SesionUsuario;

@ManagedBean(name = "gradoController")
@SessionScoped
public class GradoController implements Serializable {

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
		@EJB(lookup = "java:global/ServiciosSeguridadEPN/GradoDAOImplement!ec.edu.epn.laboratorioBJ.beans.GradoDAO")
		
		private GradoDAO gradoI;

		 
	    // variables de la clase	    
	    private Grado grado;
	    private List<Grado> Grados = new ArrayList<>();	    
	    private List<Grado> filtroGrado = new ArrayList<>();	    
	    private Grado nuevoGrado;
	    private String nombreTP;
	 
	    // Mpetodo Init
	    @PostConstruct
	    public void init() {
	        try {	 	            
	            	  
	            Grados = gradoI.getAll(Grado.class);	            	
	            grado = new Grado();	           
	            nuevoGrado = new Grado();
	          	 
	 
	        } catch (Exception e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
	    }
	 
	    /******* Método Guardar *******/
	 
	    public void agregarGrado(ActionEvent event) {
	 
	        try {
	            if (buscarGrado(nuevoGrado.getNombreGr()) == true) {
	                FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
	                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ha ocurrido un error", "El Grado ("
	                                + nuevoGrado.getNombreGr() + ") ya existe."));            
	                 nuevoGrado = new Grado();
	 
	 
	            } else {       	                
	                gradoI.save(nuevoGrado); 	              
	                
	                Grados = gradoI.getAll(Grado.class); 
	 
	                FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
	                        new FacesMessage(FacesMessage.SEVERITY_INFO, "", "Grado almacenado exitosamente"));
	 
	                nuevoGrado = new Grado();               
	 
	            }
	 
	        } catch (Exception e) {
	            FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
	                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Ha ocurrido un error"));
	 
	 
	 
	        }
	 
	    }
	 
	 
	 
	    /******* Método Modificar *******/
	    
	    public void modificarGrado(ActionEvent event) {
	    	try {
				if (grado.getNombreGr().equals(getNombreTP())) {
					gradoI.update(grado);
					Grados = gradoI.getAll(Grado.class);

					RequestContext context = RequestContext.getCurrentInstance();
					context.execute("PF('modificarGrado').hide();");

					FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
							new FacesMessage(FacesMessage.SEVERITY_INFO, "", "Grado actualizado exitosamente"));

				} else if (buscarGrado(grado.getNombreGr()) == false) {
					gradoI.update(grado);
					Grados = gradoI.getAll(Grado.class);

					RequestContext context = RequestContext.getCurrentInstance();
					context.execute("PF('modificarGrado').hide();");

					FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
							new FacesMessage(FacesMessage.SEVERITY_INFO, "", "Grado actualizado exitosamente"));
				} else {
					Grados = gradoI.getAll(Grado.class);
					FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
							new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ha ocurrido un error", "El Grado ya existe."));
				}

			} catch (Exception e) {
				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Ha ocurrido un error"));
			}
	    }
	 
	    
		/******* Método Eliminar *******/
	 
	    public void eliminarGrado(ActionEvent event) {
	        try {	 
	            	 
	            gradoI.delete(grado);	            
	            Grados = gradoI.getAll(Grado.class);
	 
	            FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
	                    new FacesMessage(FacesMessage.SEVERITY_INFO, "", "Grado eliminada exitosamente"));
	 
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
	 
	    private boolean buscarGrado(String valor) {
			try {
				Grados = gradoI.getAll(Grado.class);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			boolean resultado = false;
			for (Grado tipo : Grados) {
				if (tipo.getNombreGr().equals(valor)) {
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
	    
		public List<Grado> getFiltroGrado() {
			return filtroGrado;
		}

		public void setFiltroGrado(List<Grado> filtroGrado) {
			this.filtroGrado = filtroGrado;
		}

		public Grado getGrado() {
			return grado;
		}

		public void setGrado(Grado grado) {
			this.grado = grado;
		}

		public List<Grado> getGrados() {
			return Grados;
		}

		public void setGrados(List<Grado> grados) {
			Grados = grados;
		}

		public Grado getNuevoGrado() {
			return nuevoGrado;
		}

		public void setNuevoGrado(Grado nuevoGrado) {
			this.nuevoGrado = nuevoGrado;
		}

		public String getNombreTP() {
			return nombreTP;
		}

		public void setNombreTP(String nombreTP) {
			this.nombreTP = nombreTP;
		}

		

}
