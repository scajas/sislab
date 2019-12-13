package ec.edu.epn.laboratoriosBJ.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.edu.epn.laboratorioBJ.beans.LaboratorioDAO;
import ec.edu.epn.laboratorioBJ.beans.ServicioDAO;
import ec.edu.epn.laboratorioBJ.beans.TipoServicioDAO;
import ec.edu.epn.laboratorioBJ.beans.UnidadDAO;
import ec.edu.epn.laboratorioBJ.entities.LaboratorioLab;
import ec.edu.epn.laboratorioBJ.entities.Servicio;

import ec.edu.epn.laboratorioBJ.entities.Tiposervicio;
import ec.edu.epn.laboratorioBJ.entities.UnidadLabo;
import ec.edu.epn.seguridad.VO.SesionUsuario;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

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

	// Tipo servicio

	private Servicio servicio;
	private List<Servicio> servicios = new ArrayList<Servicio>();
	private Servicio nuevoServicio;
	private List<Servicio> filtroServicios;

	// Tipo servicio

	private Tiposervicio tipoServicioSelect;
	private List<Tiposervicio> tipoServicios = new ArrayList<Tiposervicio>();
	private Tiposervicio tiposervicio;

	// Laboratorio
	private LaboratorioLab laboratorioLabSelect;
	private List<LaboratorioLab> laboratorios = new ArrayList<LaboratorioLab>(); // unidad
	private LaboratorioLab laboratorioLab;

	private String nombreS;
	private String idTipoS;
	private StreamedContent streamFile = null;

	// Metodo Init
	@PostConstruct
	public void init() {
		try {

			// uni = (UnidadLabo) unidadI.getById(UnidadLabo.class,
			// su.UNIDAD_USUARIO_LOGEADO);
			servicios = servicioI.listaServicioUnidad(su.UNIDAD_USUARIO_LOGEADO);

			servicio = new Servicio();
			nuevoServicio = new Servicio();

			// tipo servicio
			setTipoServicios(tipoServicioI.getAll(Tiposervicio.class));
			setTiposervicio(new Tiposervicio());

			// Laboratorio
			laboratorios = servicioI.listaLaboratorioUnidad(su.UNIDAD_USUARIO_LOGEADO);
			laboratorioLab = new LaboratorioLab();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/****** Mensajes Personalizados ****/
	public void mensajeError(String mensaje) {

		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR!", mensaje));
	}

	public void mensajeInfo(String mensaje) {

		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", mensaje));

	}

	public void updateTable() {
		try {
			servicios = servicioI.listaServicioUnidad(su.UNIDAD_USUARIO_LOGEADO);
		} catch (Exception e) {

		}
	}

	public void createIdExistencia() {

		RequestContext context = RequestContext.getCurrentInstance();

		try {

			String codigoAux = servicioI.maxIdServ(su.UNIDAD_USUARIO_LOGEADO);
			String codigoCortado = codigoAux.substring(4, 8);
			Integer codigo = Integer.parseInt(codigoCortado);
			codigo = codigo + 1;

			String codigoFinal = codigo.toString();

			UnidadLabo uni = (UnidadLabo) unidadI.getById(UnidadLabo.class, su.UNIDAD_USUARIO_LOGEADO);

			switch (codigoFinal.length()) {
			case 1:

				nuevoServicio.setIdServicio(uni.getCodigoU() + "-S" + "000" + codigoFinal);
				break;
			case 2:
				nuevoServicio.setIdServicio(uni.getCodigoU() + "-S" + "00" + codigoFinal);
				break;
			case 3:
				nuevoServicio.setIdServicio(uni.getCodigoU() + "-S" + "0" + codigoFinal);
				break;

			case 4:
				nuevoServicio.setIdServicio(uni.getCodigoU() + "-S" + codigoFinal);
				break;

			default:
				break;
			}

			nuevoServicio.setLaboratorio(laboratorioLabSelect);
			nuevoServicio.setTiposervicio(tipoServicioSelect);

			servicioI.save(nuevoServicio);
			mensajeInfo("El Servicio (" + nuevoServicio.getNombreS() + ") se ha almacenado exitosamente");

			updateTable();
			nuevoServicio = new Servicio();
			tipoServicioSelect = new Tiposervicio();
			laboratorioLabSelect = new LaboratorioLab();

			context.execute("PF('nuevoS').hide();");

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void agregarServicio() {

		try {

			if (buscarServicio(nuevoServicio.getNombreS()) == true) {

				mensajeError("El Servicio (" + nuevoServicio.getNombreS() + ") ya existe.");

			} else if (tipoServicioSelect.getIdTiposerv() == 5) {

				System.out.println("Entro a la validación de Analisis Interno");
				createIdExistencia();

			} else if (nuevoServicio.getPrecioS() == 0) {

				System.out.println("Entro a la validación del Precio");

				mensajeError("El Precio tiene que ser mayor a cero");

			} else {
				createIdExistencia();
			}

		} catch (Exception e) {
			mensajeError("ha ocurrido un problema");
			e.printStackTrace();
		}

	}

	public void modificarServicio() {

		RequestContext context = RequestContext.getCurrentInstance();

		try {

			if (servicio.getNombreS().equals(getNombreS())) {

				servicioI.update(servicio);
				updateTable();

				mensajeInfo("El Servicio (" + servicio.getNombreS() + ") se ha actualizado exitosamente");

				context.execute("PF('modificarS').hide();");

			} else if (buscarServicio(servicio.getNombreS()) == false) {

				servicioI.update(servicio);
				updateTable();

				mensajeInfo("El Servicio (" + servicio.getNombreS() + ") se ha actualizado exitosamente");

				context.execute("PF('modificarS').hide();");
			}

			else {

				mensajeError("El Servicio (" + nuevoServicio.getNombreS() + ") ya existe.");
			}

		} catch (Exception e) {
			mensajeError("Ha ocurrido un problema");
		}
	}

	public void eliminarServicio() {
		try {

			servicioI.delete(servicio);
			updateTable();

			mensajeInfo("El Servicio (" + servicio.getNombreS() + ") se ha eliminado exitosamente");

		} catch (Exception e) {

			if (e.getMessage() == "Transaction rolled back") {
				mensajeError("La tabla Servicio (" + servicio.getNombreS() + ") tiene relación con otra tabla");
			} else {
				mensajeError("Ha ocurrido un problema");
			}

		}
	}

	public void buscarServicioCombo() {

		nuevoServicio.setTiposervicio(tipoServicioSelect);

		servicios = servicioI.getparametrosTipoServicio(nuevoServicio.getTiposervicio().getNombreTs());

		tiposervicio = new Tiposervicio();
	}

	public void buscarTipoServicio() {

		try {

			tipoServicios = tipoServicioI.getAll(Tiposervicio.class);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean buscarServicio(String valor) {
		try {
			servicios = servicioI.getAll(Servicio.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		boolean resultado = false;
		for (Servicio tipo : servicios) {
			if (tipo.getNombreS().equals(valor)) {
				resultado = true;
				break;
			} else {
				resultado = false;
			}
		}
		return resultado;
	}

	public void buscarServicioReporte() {
		try {
			servicios = servicioI.listaServicioXTipo(tipoServicioSelect.getIdTiposerv());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void pasarNombre(String nombre) {
		setNombreS(nombre);
	}

	public void generarPDF(ActionEvent event) throws Exception {
		try {

			if (streamFile != null)
				streamFile.getStream().close();

			Map<String, Object> parametros = new HashMap<String, Object>();
			parametros.put("idServicio", tipoServicioSelect.getIdTiposerv());

			String direccion = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/reportes/");
			if (direccion.toUpperCase().contains("C:") || direccion.toUpperCase().contains("D:")
					|| direccion.toUpperCase().contains("E:") || direccion.toUpperCase().contains("F:")) {
				direccion = direccion + "\\";
			} else {
				direccion = direccion + "/";
			}

			String jrxmlFile = FacesContext.getCurrentInstance().getExternalContext()
					.getRealPath("/reportes/ReporteServicio1.jrxml");
			InputStream input = new FileInputStream(new File(jrxmlFile));
			JasperReport jasperReport = JasperCompileManager.compileReport(input);
			parametros.put(JRParameter.REPORT_CONNECTION, coneccionSQL());

			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros);

			File sourceFile = new File(jrxmlFile);
			File destFile = new File(sourceFile.getParent(), "ReporteServicio1.pdf");

			JasperExportManager.exportReportToPdfFile(jasperPrint, destFile.toString());
			InputStream stream = new FileInputStream(destFile);

			streamFile = new DefaultStreamedContent(stream, "application/pdf", "ReporteServicio1.pdf");

		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
					new FacesMessage(FacesMessage.SEVERITY_FATAL, "", "ERROR"));

		}

	}

	public void generarEXCEL(ActionEvent event) throws Exception {
		try {

			if (streamFile != null)
				streamFile.getStream().close();

			Map<String, Object> parametros = new HashMap<String, Object>();
			parametros.put("idServicio", tipoServicioSelect.getIdTiposerv());

			String direccion = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/reportes/");
			if (direccion.toUpperCase().contains("C:") || direccion.toUpperCase().contains("D:")
					|| direccion.toUpperCase().contains("E:") || direccion.toUpperCase().contains("F:")) {
				direccion = direccion + "\\";
			} else {
				direccion = direccion + "/";
			}

			String jrxmlFile = FacesContext.getCurrentInstance().getExternalContext()
					.getRealPath("/reportes/ReporteServicio1.jrxml");
			InputStream input = new FileInputStream(new File(jrxmlFile));
			JasperReport jasperReport = JasperCompileManager.compileReport(input);
			parametros.put(JRParameter.REPORT_CONNECTION, coneccionSQL());

			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros);

			File sourceFile = new File(jrxmlFile);
			File destFile = new File(sourceFile.getParent(), "ReporteServicio1.xls");

			JasperExportManager.exportReportToPdfFile(jasperPrint, destFile.toString());
			InputStream stream = new FileInputStream(destFile);

			streamFile = new DefaultStreamedContent(stream, "application/xls", "ReporteServicio1.xls");

		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
					new FacesMessage(FacesMessage.SEVERITY_FATAL, "", "ERROR"));

		}

	}

	public void cerrarArchivo() throws IOException {
		if (streamFile != null)
			streamFile.getStream().close();

		streamFile = null;
		System.gc();
	}

	private Connection coneccionSQL() throws IOException {
		try {
			conexionPostrges conexionSQL = new conexionPostrges();
			Connection con = conexionSQL.getConnection();
			return con;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

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

	public Tiposervicio getTipoServicioSelect() {
		return tipoServicioSelect;
	}

	public void setTipoServicioSelect(Tiposervicio tipoServicioSelect) {
		this.tipoServicioSelect = tipoServicioSelect;
	}

	public LaboratorioLab getLaboratorioLabSelect() {
		return laboratorioLabSelect;
	}

	public void setLaboratorioLabSelect(LaboratorioLab laboratorioLabSelect) {
		this.laboratorioLabSelect = laboratorioLabSelect;
	}

	public LaboratorioLab getLaboratorioLab() {
		return laboratorioLab;
	}

	public void setLaboratorioLab(LaboratorioLab laboratorioLab) {
		this.laboratorioLab = laboratorioLab;
	}

	/**
	 * @param streamFile
	 *            the streamFile to set
	 */
	public void setStreamFile(StreamedContent streamFile) {
		this.streamFile = streamFile;
	}

	public StreamedContent getStreamFile() {
		return streamFile;
	}

	public List<LaboratorioLab> getLaboratorios() {
		return laboratorios;
	}

	public void setLaboratorios(List<LaboratorioLab> laboratorios) {
		this.laboratorios = laboratorios;
	}

	public Tiposervicio getTiposervicio() {
		return tiposervicio;
	}

	public void setTiposervicio(Tiposervicio tiposervicio) {
		this.tiposervicio = tiposervicio;
	}

	public List<Tiposervicio> getTipoServicios() {
		return tipoServicios;
	}

	public void setTipoServicios(List<Tiposervicio> tipoServicios) {
		this.tipoServicios = tipoServicios;
	}

	public List<Servicio> getServicios() {
		return servicios;
	}

	public void setServicios(List<Servicio> servicios) {
		this.servicios = servicios;
	}

	public List<Servicio> getFiltroServicios() {
		return filtroServicios;
	}

	public void setFiltroServicios(List<Servicio> filtroServicios) {
		this.filtroServicios = filtroServicios;
	}

	public String getIdTipoS() {
		return idTipoS;
	}

	public void setIdTipoS(String idTipoS) {
		this.idTipoS = idTipoS;
	}

}
