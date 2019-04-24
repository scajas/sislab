package ec.edu.epn.laboratoriosBJ.controller;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import org.primefaces.model.StreamedContent;

import ec.edu.epn.laboratorioBJ.beans.DetalleOrdenDAO;
import ec.edu.epn.laboratorioBJ.beans.LaboratorioDAO;
import ec.edu.epn.laboratorioBJ.beans.OrdenTrabajoDAO;
import ec.edu.epn.laboratorioBJ.beans.PersonalLabDAO;
import ec.edu.epn.laboratorioBJ.beans.ServicioDAO;
import ec.edu.epn.laboratorioBJ.entities.Detalleorden;
import ec.edu.epn.laboratorioBJ.entities.LaboratorioLab;
import ec.edu.epn.laboratorioBJ.entities.Metodo;
import ec.edu.epn.laboratorioBJ.entities.OrdenTrabajo;
import ec.edu.epn.laboratorioBJ.entities.PersonalLab;
import ec.edu.epn.laboratorioBJ.entities.Servicio;
import ec.edu.epn.seguridad.VO.SesionUsuario;

@ManagedBean(name = "ordenTrabajoController")
@SessionScoped

public class OrdenTrabajoController implements Serializable {

	/** VARIABLES DE SESION ***/
	private static final long serialVersionUID = 6771930005130933302L;
	FacesContext fc = FacesContext.getCurrentInstance();
	HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
	HttpSession session = request.getSession();
	SesionUsuario su = (SesionUsuario) session.getAttribute("sesionUsuario");

	/******************************************************************/

	/** SERIVICIOS **/
	@EJB(lookup = "java:global/ServiciosSeguridadEPN/OrdenTrabajoDAOImplement!ec.edu.epn.laboratorioBJ.beans.OrdenTrabajoDAO")
	private OrdenTrabajoDAO ordenTrabajoI;// I (interface)

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/ServicioDAOImplement!ec.edu.epn.laboratorioBJ.beans.ServicioDAO")
	private ServicioDAO servicioI;// I (interface)

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/DetalleOrdenDAOImplement!ec.edu.epn.laboratorioBJ.beans.DetalleOrdenDAO")
	private DetalleOrdenDAO detalleOrdenI;

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/PersonalLabDAOImplement!ec.edu.epn.laboratorioBJ.beans.PersonalLabDAO")
	private PersonalLabDAO personalLabI;

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/LaboratorioDAOImplement!ec.edu.epn.laboratorioBJ.beans.LaboratorioDAO")
	private LaboratorioDAO laboratorioI;

	private OrdenTrabajo ordenTrabajo;
	private List<OrdenTrabajo> listaOrdenTrabajo = new ArrayList<OrdenTrabajo>();
	private OrdenTrabajo nuevoOrdenTrabajo;
	private String nombreTP;
	private StreamedContent streamFile = null;

	private Detalleorden detalleOrden;
	private List<Detalleorden> listaDetalleOrden = new ArrayList<>();

	private Servicio servicio;
	private List<Servicio> serviciosOrden = new ArrayList<Servicio>();

	private PersonalLab personalLab;
	private List<PersonalLab> listaPersonalLab = new ArrayList<PersonalLab>();

	private LaboratorioLab laboratorio;
	private List<LaboratorioLab> listaLaboratorio = new ArrayList<LaboratorioLab>();

	private String tipoO;
	private Date fechaInicio;
	private Date fechaFin;

	@PostConstruct
	public void init() {
		try {
			listaDetalleOrden = detalleOrdenI.getAll(Detalleorden.class);
			detalleOrden = new Detalleorden();

			ordenTrabajo = new OrdenTrabajo();
			listaOrdenTrabajo = ordenTrabajoI.getAll(OrdenTrabajo.class);

			listaLaboratorio = ordenTrabajoI.listaLaboratorioUnidad(su.UNIDAD_USUARIO_LOGEADO);
			laboratorio = new LaboratorioLab();

			listaPersonalLab = ordenTrabajoI.listaPersonalAnalista();
			personalLab = new PersonalLab();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String servicio(String idServicio) {
		String nombre = "";

		Servicio servicio = new Servicio();

		servicio = detalleOrdenI.findServicioById(idServicio);

		if (servicio == null) {
			nombre = "N/A";
		} else {
			nombre = servicio.getNombreS();
		}

		return nombre;
	}

	public void mensajeError(String mensaje) {

		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR!", mensaje));
	}

	public void mensajeInfo(String mensaje) {

		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", mensaje));

	}

	public void buscarOrden() {

		try {

			System.out.println("ESTOS SON LOS REGISTROS OBTENIDOS ;" +cambioFecha(getFechaFin()) + cambioFecha(getFechaInicio())
					+ ordenTrabajo.getTipoOt() + ordenTrabajo.getEstadoOt() + laboratorio.getNombreL() + personalLab.getNombresPe());

			listaDetalleOrden = ordenTrabajoI.filtrarLista(cambioFecha(getFechaInicio()), cambioFecha(getFechaFin()),
					ordenTrabajo.getTipoOt(), ordenTrabajo.getEstadoOt(), laboratorio.getNombreL(), personalLab.getNombresPe());

			System.out.println("Número de servicios Obtenidos: " + listaDetalleOrden.size());

			mensajeInfo("Coincidencias Encontradas");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String cambioFecha(Date fecha) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

		String fechaFinal = format.format(fecha);

		return fechaFinal;
	}

	// ****** Getter y Setters ****//*

	public String getNombreTP() {
		return nombreTP;
	}

	public void setNombreTP(String nombreTP) {
		this.nombreTP = nombreTP;
	}

	public OrdenTrabajo getOrdenTrabajo() {
		return ordenTrabajo;
	}

	public void setOrdenTrabajo(OrdenTrabajo ordenTrabajo) {
		this.ordenTrabajo = ordenTrabajo;
	}

	public List<OrdenTrabajo> getListaOrdenTrabajo() {
		return listaOrdenTrabajo;
	}

	public void setListaOrdenTrabajo(List<OrdenTrabajo> listaOrdenTrabajo) {
		this.listaOrdenTrabajo = listaOrdenTrabajo;
	}

	public OrdenTrabajo getNuevoOrdenTrabajo() {
		return nuevoOrdenTrabajo;
	}

	public void setNuevoOrdenTrabajo(OrdenTrabajo nuevoOrdenTrabajo) {
		this.nuevoOrdenTrabajo = nuevoOrdenTrabajo;
	}

	public Detalleorden getDetalleOrden() {
		return detalleOrden;
	}

	public void setDetalleOrden(Detalleorden detalleOrden) {
		this.detalleOrden = detalleOrden;
	}

	public Servicio getServicio() {
		return servicio;
	}

	public void setServicio(Servicio servicio) {
		this.servicio = servicio;
	}

	public List<Servicio> getServiciosOrden() {
		return serviciosOrden;
	}

	public void setServiciosOrden(List<Servicio> serviciosOrden) {
		this.serviciosOrden = serviciosOrden;
	}

	public String getTipoO() {
		return tipoO;
	}

	public void setTipoO(String tipoO) {
		this.tipoO = tipoO;
	}

	public PersonalLab getPersonalLab() {
		return personalLab;
	}

	public void setPersonalLab(PersonalLab personalLab) {
		this.personalLab = personalLab;
	}

	public List<PersonalLab> getListaPersonalLab() {
		return listaPersonalLab;
	}

	public void setListaPersonalLab(List<PersonalLab> listaPersonalLab) {
		this.listaPersonalLab = listaPersonalLab;
	}

	public LaboratorioLab getLaboratorio() {
		return laboratorio;
	}

	public void setLaboratorio(LaboratorioLab laboratorio) {
		this.laboratorio = laboratorio;
	}

	public List<LaboratorioLab> getListaLaboratorio() {
		return listaLaboratorio;
	}

	public void setListaLaboratorio(List<LaboratorioLab> listaLaboratorio) {
		this.listaLaboratorio = listaLaboratorio;
	}

	public List<Detalleorden> getListaDetalleOrden() {
		return listaDetalleOrden;
	}

	public void setListaDetalleOrden(List<Detalleorden> listaDetalleOrden) {
		this.listaDetalleOrden = listaDetalleOrden;
	}

	public Date getFechaInicio() {
		return fechaInicio;
	}

	public void setFechaInicio(Date fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	public Date getFechaFin() {
		return fechaFin;
	}

	public void setFechaFin(Date fechaFin) {
		this.fechaFin = fechaFin;
	}

	public StreamedContent getStreamFile() {
		return streamFile;
	}

	public void setStreamFile(StreamedContent streamFile) {
		this.streamFile = streamFile;
	}

}
