package ec.edu.epn.laboratoriosBJ.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.edu.epn.laboratorioBJ.beans.DetalleOrdenDAO;
import ec.edu.epn.laboratorioBJ.beans.LaboratorioDAO;
import ec.edu.epn.laboratorioBJ.beans.OrdenTrabajoDAO;
import ec.edu.epn.laboratorioBJ.beans.PersonalLabDAO;
import ec.edu.epn.laboratorioBJ.beans.ServicioDAO;
import ec.edu.epn.laboratorioBJ.entities.Detalleorden;
import ec.edu.epn.laboratorioBJ.entities.LaboratorioLab;
import ec.edu.epn.laboratorioBJ.entities.OrdenTrabajo;
import ec.edu.epn.laboratorioBJ.entities.PersonalLab;
import ec.edu.epn.laboratorioBJ.entities.Servicio;
import ec.edu.epn.seguridad.VO.SesionUsuario;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

@ManagedBean(name = "reporteOrdenTrabajoController")
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

	private List<OrdenTrabajo> ordenTrabajos = new ArrayList<>();

	private StreamedContent streamFile = null;

	private List<Detalleorden> detalleOrdenes = new ArrayList<Detalleorden>();

	private Servicio servicio;

	private List<PersonalLab> listaPersonalLab = new ArrayList<PersonalLab>();

	private List<LaboratorioLab> laboratorios = new ArrayList<LaboratorioLab>();

	private String tipoOrden;
	private Date fechaInicio;
	private Date fechaFin;
	private String analista;
	private String estado;
	private String nombreL;

	@PostConstruct
	public void init() {
		try {

			servicio = new Servicio();
			listaPersonalLab = ordenTrabajoI.listaPersonalAnalista();

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

			detalleOrdenes = ordenTrabajoI.filtrarLista(cambioFecha(getFechaInicio()), cambioFecha(getFechaFin()),
					tipoOrden, analista, estado);

			mensajeInfo("Resultados Obtenidos" + detalleOrdenes.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void generarPDF() throws Exception {
		try {

			if (streamFile != null)
				streamFile.getStream().close();

			ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext()
					.getContext();

			String direccion = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/reportes/");

			if (direccion.toUpperCase().contains("C:") || direccion.toUpperCase().contains("D:")
					|| direccion.toUpperCase().contains("E:") || direccion.toUpperCase().contains("F:")) {
				direccion = direccion + "\\";
			} else {
				direccion = direccion + "/";
			}

			Map<String, Object> parametros = new HashMap<String, Object>();

			parametros.put("CONTEXT", servletContext.getRealPath("/"));
			parametros.put("fechaInicio", getFechaInicio());
			parametros.put("fechaFin", getFechaFin());
			parametros.put("tipoOrdenTrabajo", tipoOrden);
			parametros.put("nombrePe", analista);
			parametros.put("estadoOrden", estado);

			String jrxmlFile = FacesContext.getCurrentInstance().getExternalContext()
					.getRealPath("/reportes/reporteOrdenTrabajo.jrxml");
			InputStream input = new FileInputStream(new File(jrxmlFile));
			JasperReport jasperReport = JasperCompileManager.compileReport(input);
			parametros.put(JRParameter.REPORT_CONNECTION, coneccionSQL());

			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros);

			File sourceFile = new File(jrxmlFile);
			File destFile = new File(sourceFile.getParent(), "reporteOrdenTrabajo.pdf");

			JasperExportManager.exportReportToPdfFile(jasperPrint, destFile.toString());
			InputStream stream = new FileInputStream(destFile);

			streamFile = new DefaultStreamedContent(stream, "application/pdf", "reporteOrdenTrabajo.pdf");

		} catch (Exception e) {
			e.printStackTrace();

		}

	}

	private Connection coneccionSQL() throws IOException {
		try {
			conexionPostrges conexionSQL = new conexionPostrges();
			Connection con = conexionSQL.Conexion();
			return con;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void cerrarArchivo() throws IOException {
		if (streamFile != null)
			streamFile.getStream().close();

		streamFile = null;
		System.gc();
	}

	public String cambioFecha(Date fecha) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

		String fechaFinal = format.format(fecha);

		return fechaFinal;
	}

	// ****** Getter y Setters ****//*
	public List<PersonalLab> getListaPersonalLab() {
		return listaPersonalLab;
	}

	public void setListaPersonalLab(List<PersonalLab> listaPersonalLab) {
		this.listaPersonalLab = listaPersonalLab;
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

	public List<Detalleorden> getDetalleOrdenes() {
		return detalleOrdenes;
	}

	public void setDetalleOrdenes(List<Detalleorden> detalleOrdenes) {
		this.detalleOrdenes = detalleOrdenes;
	}

	public List<LaboratorioLab> getLaboratorios() {
		return laboratorios;
	}

	public void setLaboratorios(List<LaboratorioLab> laboratorios) {
		this.laboratorios = laboratorios;
	}

	public String getTipoOrden() {
		return tipoOrden;
	}

	public void setTipoOrden(String tipoOrden) {
		this.tipoOrden = tipoOrden;
	}

	public String getAnalista() {
		return analista;
	}

	public void setAnalista(String analista) {
		this.analista = analista;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public String getNombreL() {
		return nombreL;
	}

	public void setNombreL(String nombreL) {
		this.nombreL = nombreL;
	}

	public List<OrdenTrabajo> getOrdenTrabajos() {
		return ordenTrabajos;
	}

	public void setOrdenTrabajos(List<OrdenTrabajo> ordenTrabajos) {
		this.ordenTrabajos = ordenTrabajos;
	}

	public Servicio getServicio() {
		return servicio;
	}

	public void setServicio(Servicio servicio) {
		this.servicio = servicio;
	}

}
