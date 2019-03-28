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


import ec.edu.epn.laboratorioBJ.beans.LaboratorioDAO;
import ec.edu.epn.laboratorioBJ.beans.ServicioDAO;
import ec.edu.epn.laboratorioBJ.beans.TipoServicioDAO;
import ec.edu.epn.laboratorioBJ.beans.UnidadDAO;
import ec.edu.epn.laboratorioBJ.entities.LaboratorioLab;
import ec.edu.epn.laboratorioBJ.entities.Servicio;
import ec.edu.epn.laboratorioBJ.entities.Tiposervicio;
import ec.edu.epn.laboratorioBJ.entities.UnidadLabo;
import ec.edu.epn.seguridad.VO.SesionUsuario;


@ManagedBean(name = "servicioController")
@SessionScoped
public class ServicioController implements Serializable {

	/** VARIABLES DE SESION ***/
	private static final long serialVersionUID = 6771930005130933302L;
	FacesContext fc = FacesContext.getCurrentInstance();
	HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
	HttpSession session = request.getSession();
	SesionUsuario su = (SesionUsuario) session.getAttribute("sesionUsuario");

	/****************************************************************************/

	/** SERIVICIOS **/

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/ServicioDAOImplement!ec.edu.epn.laboratorioBJ.beans.ServicioDAO")
	private ServicioDAO servicioI;
	
	@EJB(lookup = "java:global/ServiciosSeguridadEPN/LaboratorioDAOImplement!ec.edu.epn.laboratorioBJ.beans.LaboratorioDAO")
	private LaboratorioDAO laboratorioI;
	
	
	@EJB(lookup = "java:global/ServiciosSeguridadEPN/TipoServicioDAOImplement!ec.edu.epn.laboratorioBJ.beans.TipoServicioDAO")	
	private TipoServicioDAO tipoServicioI;
	
	
	@EJB(lookup = "java:global/ServiciosSeguridadEPN/UnidadDAOImplement!ec.edu.epn.laboratorioBJ.beans.UnidadDAO")	
	private UnidadDAO unidadI;
	

	
	private Servicio servicio;
	private List<Servicio> listServicio = new ArrayList<Servicio>();
	
		
		
		//Select UnidadLAb
	private UnidadLabo tipoUnidadSelect;
	private List<UnidadLabo> listUnidadLabo = new ArrayList<UnidadLabo>();
	private UnidadLabo tipoUnidad;
	
	
	private List<Servicio> filtroServicio = new ArrayList<>();
	private Servicio nuevoServicio;
	private String nombreS;
	
	private LaboratorioLab laboratorioSelect;
	private List<LaboratorioLab> laboratorios = new ArrayList<LaboratorioLab>();
	
	private Tiposervicio tipoServicioSelect;
	private List<Tiposervicio> tipoServicios = new ArrayList<Tiposervicio>();

	// Metodo Init
	@PostConstruct
	public void init() {
		try {
			
			listServicio =  servicioI.listaServicioUnidad(su.UNIDAD_USUARIO_LOGEADO);
			listServicio  = servicioI.getAll(Servicio.class);
			servicio = new Servicio();
			nuevoServicio =  new Servicio();
			setLaboratorios(laboratorioI.getAll(LaboratorioLab.class));
			setTipoServicios(tipoServicioI.getAll(Tiposervicio.class));
			
			listUnidadLabo = unidadI.getAll(UnidadLabo.class);			
			tipoUnidad = new UnidadLabo();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public void agregarServicio(ActionEvent event) {
		
		try {
			if (buscarServicio(nuevoServicio.getNombreS()) == true) {
				FacesContext.getCurrentInstance()
						.addMessage(event.getComponent().getClientId(), new FacesMessage(FacesMessage.SEVERITY_ERROR,
								"ERROR!", "Ha ocurrido un error, El Servicio (" + nuevoServicio.getNombreS()
										+ ")ya existe."));
				nuevoServicio = new Servicio();

			} else {

				
				String codigoAux = servicioI.maxIdServ(su.UNIDAD_USUARIO_LOGEADO);
				String codigoCortado = codigoAux.substring(0, 3);
				Integer codigo = Integer.parseInt(codigoCortado);
				codigo = codigo + 1;
				String codigoFinal = codigo.toString();
				
				UnidadLabo uni = (UnidadLabo) unidadI.getAll(UnidadLabo.class);
			

				switch (codigoFinal.length()) {
				case 1:			
				
					nuevoServicio.setIdServicio( uni.getCodigoU() + "-S" + "0000" + codigoAux);
					break;
				case 2:
					nuevoServicio.setIdServicio(uni.getCodigoU() + "-S" + "000" + codigoAux);
					break;
				case 3:
					nuevoServicio.setIdServicio(uni.getCodigoU() + "-S" + "00" + codigoAux);
					break;
				case 4:
					nuevoServicio.setIdServicio(uni.getCodigoU() + "-S" + "0" + codigoAux);
					break;
				case 5:
					nuevoServicio.setIdServicio(uni.getCodigoU() + "-S" + codigoAux);
					break;

				default:
					break;
				}
				
				
			
				nuevoServicio.setTiposervicio(tipoServicioSelect);
				listUnidadLabo = unidadI.getAll(UnidadLabo.class);
			
				servicioI.save(nuevoServicio);				
				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(), new FacesMessage(
						FacesMessage.SEVERITY_INFO, "", "El Servicio se ha almacenado exitosamente"));

				nuevoServicio = new Servicio();
	

			}

		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR!", ""));
		}

	}
	
	
	
	public void agregarServicio1(ActionEvent event) {
		try {
			if (buscarServicio(nuevoServicio.getNombreS()) == true) {
				FacesContext.getCurrentInstance()
						.addMessage(event.getComponent().getClientId(), new FacesMessage(FacesMessage.SEVERITY_ERROR,
								"", "Ha ocurrido un error, este servicio (" + nuevoServicio.getNombreS() + " ya existe"));				
				nuevoServicio =  new Servicio();
			} else {
				// seteo de las campos
			     	   //nuevoBodega.setIdUnidad(su.UNIDAD_USUARIO_LOGEADO);
				       //   nuevoServicio.setIdUnidad(su.UNIDAD_USUARIO_LOGEADO);  
				//Long iduser = su.id_usuario_log;
				    //   nuevoBodega.setIdUsuario(iduser.intValue());
				  //     nuevoServicio.setIdServicio(iduser.intValue());  ///
 
				nuevoServicio =  new Servicio();
								
				listServicio = servicioI.listaServicioUnidad(su.UNIDAD_USUARIO_LOGEADO);

				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_INFO, "", "Servicio registrado exitosamente"));

				nuevoServicio =  new Servicio();
			}

		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Ha ocurrido un error"));
		}

	}

	public void modificarServivio(ActionEvent event) {
		try {

			if (servicio.getNombreS().equals(getNombreS())) {				
				servicioI.update(servicio);				
				listServicio =  servicioI.listaServicioUnidad(su.UNIDAD_USUARIO_LOGEADO);

				RequestContext context = RequestContext.getCurrentInstance();
				context.execute("PF('modificarS').hide();");

				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_INFO, "", "Servicio actualizado exitosamente"));

			} else if ( buscarServicio(servicio.getNombreS())  == false ) {				
				servicioI.update(servicio);				
				listServicio =  servicioI.listaServicioUnidad(su.UNIDAD_USUARIO_LOGEADO);

				RequestContext context = RequestContext.getCurrentInstance();
				context.execute("PF('modificarS').hide();");

				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_INFO, "", "Servicio actualizado exitosamente"));
			} else {				
				listServicio = servicioI.listaServicioUnidad(su.UNIDAD_USUARIO_LOGEADO);
				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ha ocurrido un error",
								"La Bodega (" + nuevoServicio.getNombreS() + ") ya existe."));
			}

		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Ha ocurrido un error"));
		}
	}

	public void eliminarServicio(ActionEvent event) {
		try {
			
			servicioI.delete(servicio);			
			listServicio =  servicioI.listaServicioUnidad(su.UNIDAD_USUARIO_LOGEADO);

			FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
					new FacesMessage(FacesMessage.SEVERITY_INFO, "", "Servicio eliminada exitosamente"));
			

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

	private boolean buscarServicio(String valor) {
		try {			
			listServicio =  servicioI.getAll(Servicio.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		boolean resultado = false;
		for (Servicio tipo : listServicio) {
			if (tipo.getNombreS().equals(valor)) {
				resultado = true;
				break;
			} else {
				resultado = false;
			}
		}
		return resultado;
	}

	public void pasarNombre(String nombre, UnidadLabo tipoUnidades) {
		setNombreS(nombre);
		tipoUnidad = tipoUnidades;
	}


	/*
	 * get and set
	 */

	public Servicio getServicio() {
		return servicio;
	}

	public void setServicio(Servicio servicio) {
		this.servicio = servicio;
	}

	public Servicio getNuevoServicio() {
		return nuevoServicio;
	}
	
	public void setNuevoServicio(Servicio nuevoServicio) {
		this.nuevoServicio = nuevoServicio;
	}

	public String getNombreS() {
		return nombreS;
	}

	public void setNombreS(String nombreS) {
		this.nombreS = nombreS;
	}

	public List<Servicio> getListServicio() {
		return listServicio;
	}

	public void setListServicio(List<Servicio> listServicio) {
		this.listServicio = listServicio;
	}


	public List<Servicio> getFiltroServicio() {
		return filtroServicio;
	}


	public void setFiltroServicio(List<Servicio> filtroServicio) {
		this.filtroServicio = filtroServicio;
	}


	public LaboratorioLab getLaboratorioSelect() {
		return laboratorioSelect;
	}


	public void setLaboratorioSelect(LaboratorioLab laboratorioSelect) {
		this.laboratorioSelect = laboratorioSelect;
	}


	public List<LaboratorioLab> getLaboratorios() {
		return laboratorios;
	}


	public void setLaboratorios(List<LaboratorioLab> laboratorios) {
		this.laboratorios = laboratorios;
	}


	public Tiposervicio getTipoServicioSelect() {
		return tipoServicioSelect;
	}


	public void setTipoServicioSelect(Tiposervicio tipoServicioSelect) {
		this.tipoServicioSelect = tipoServicioSelect;
	}


	public List<Tiposervicio> getTipoServicios() {
		return tipoServicios;
	}


	public void setTipoServicios(List<Tiposervicio> tipoServicios) {
		this.tipoServicios = tipoServicios;
	}


	public List<UnidadLabo> getListUnidadLabo() {
		return listUnidadLabo;
	}


	public void setListUnidadLabo(List<UnidadLabo> listUnidadLabo) {
		this.listUnidadLabo = listUnidadLabo;
	}


	public UnidadLabo getTipoUnidadSelect() {
		return tipoUnidadSelect;
	}


	public void setTipoUnidadSelect(UnidadLabo tipoUnidadSelect) {
		this.tipoUnidadSelect = tipoUnidadSelect;
	}


	public UnidadLabo getTipoUnidad() {
		return tipoUnidad;
	}


	public void setTipoUnidad(UnidadLabo tipoUnidad) {
		this.tipoUnidad = tipoUnidad;
	}



}
