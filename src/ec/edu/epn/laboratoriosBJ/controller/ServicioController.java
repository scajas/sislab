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

	private Servicio servicio;
	private List<Servicio> listServicio = new ArrayList<Servicio>();

	// Select UnidadLAb
	private UnidadLabo tipoUnidadSelect;
	private List<UnidadLabo> listUnidadLabo = new ArrayList<UnidadLabo>();
	private UnidadLabo tipoUnidad;

	// select Tipo servicio

	private Tiposervicio tipoServicioSelect;
	private List<Tiposervicio> listarTipoServicio = new ArrayList<Tiposervicio>();
	private Tiposervicio tiposerv;

	// select Laboratorio
	private LaboratorioLab laboratorioLabSelect;
	private List<LaboratorioLab> listarLaboratoriosLab = new ArrayList<LaboratorioLab>();
	private LaboratorioLab laboratorioLab;

	private List<Servicio> filtroServicio = new ArrayList<>();
	private Servicio nuevoServicio;
	private String nombreS;

	private StreamedContent streamFile = null;

	// Metodo Init
	@PostConstruct
	public void init() {
		try {

			listServicio = servicioI.listaServicioUnidad(su.UNIDAD_USUARIO_LOGEADO);
			setNuevoServicio(new Servicio());
			servicio = new Servicio();
			nuevoServicio = new Servicio();

			setListarLaboratoriosLab(laboratorioI.getAll(LaboratorioLab.class));
			setListarTipoServicio(tipoServicioI.getAll(Tiposervicio.class));

			// tipo servicio
			listarTipoServicio = tipoServicioI.getAll(Tiposervicio.class);
			tiposerv = new Tiposervicio();

			// Laboratorio
			listarLaboratoriosLab = laboratorioI.getAll(LaboratorioLab.class);
			laboratorioLab = new LaboratorioLab();

			listUnidadLabo = unidadI.getAll(UnidadLabo.class);
			tipoUnidad = new UnidadLabo();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void agregarServicio(ActionEvent event) {

		try {

			String codigoAux = servicioI.maxIdServ(su.UNIDAD_USUARIO_LOGEADO);
			String codigoCortado = codigoAux.substring(4, 8);
			Integer codigo = Integer.parseInt(codigoCortado);
			codigo = codigo + 1;
			String codigoFinal = codigo.toString();

			UnidadLabo uni = (UnidadLabo) unidadI.getById(UnidadLabo.class, su.UNIDAD_USUARIO_LOGEADO);

			switch (codigoFinal.length()) {
			case 1:

				nuevoServicio.setIdServicio(uni.getCodigoU() + "-S" + "0000" + codigoFinal);
				break;
			case 2:
				nuevoServicio.setIdServicio(uni.getCodigoU() + "-S" + "000" + codigoFinal);
				break;
			case 3:
				nuevoServicio.setIdServicio(uni.getCodigoU() + "-S" + "00" + codigoFinal);
				break;
			case 4:
				nuevoServicio.setIdServicio(uni.getCodigoU() + "-S" + "0" + codigoFinal);
				break;
			case 5:
				nuevoServicio.setIdServicio(uni.getCodigoU() + "-S" + codigoFinal);
				break;

			default:
				break;
			}

			nuevoServicio.setTiposervicio(tipoServicioSelect);
			nuevoServicio.setLaboratorio(laboratorioLabSelect);

			servicioI.save(nuevoServicio);
			listServicio = servicioI.listaServicioUnidad(su.UNIDAD_USUARIO_LOGEADO);

			FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
					new FacesMessage(FacesMessage.SEVERITY_INFO, "", "El Servicio se ha almacenado exitosamente"));

			nuevoServicio = new Servicio();
			tipoServicioSelect = new Tiposervicio();
			laboratorioLabSelect = new LaboratorioLab();

		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR!", "Consulte por ayuda "));
		}

	}

	public void modificarServivio(ActionEvent event) {
		try {

			if (servicio.getNombreS().equals(getNombreS())) {
				servicioI.update(servicio);
				listServicio = servicioI.listaServicioUnidad(su.UNIDAD_USUARIO_LOGEADO);

				RequestContext context = RequestContext.getCurrentInstance();
				context.execute("PF('modificarS').hide();");

				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_INFO, "", "Servicio actualizado exitosamente"));

			} else if (buscarServicio(servicio.getNombreS()) == false) {
				servicioI.update(servicio);
				listServicio = servicioI.listaServicioUnidad(su.UNIDAD_USUARIO_LOGEADO);

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
			listServicio = servicioI.listaServicioUnidad(su.UNIDAD_USUARIO_LOGEADO);

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

	public void buscarServicioCombo() {

		nuevoServicio.setTiposervicio(tipoServicioSelect);

		listServicio = servicioI.getparametrosTipoServicio(nuevoServicio.getTiposervicio().getNombreTs());

		System.out.print("Numero de servicios obtenidos: " + listServicio.size());

		tiposerv = new Tiposervicio();
	}

	public void buscarTipoServicio() {

		try {

			listarTipoServicio = tipoServicioI.getAll(Tiposervicio.class);
			// System.out.println("misdatos:"+aplicacion.getId());

			System.out.println("Tipo Servicio Seleccionado" + tiposerv.getNombreTs());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean buscarServicio(String valor) {
		try {
			listServicio = servicioI.getAll(Servicio.class);
		} catch (Exception e) {
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

	public void buscarServicioReporte() {
		try {
			listServicio = servicioI.listaServicioXTipo(tipoServicioSelect.getIdTiposerv());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void pasarNombre(Tiposervicio tipoServicio) {
		this.tipoServicioSelect = tipoServicio;
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

			/** DIRECCION IMAGENES */
			/*
			 * String direccionImagenes=
			 * FacesContext.getCurrentInstance().getExternalContext().
			 * getRealPath("images/logoSII/");
			 * 
			 * 
			 * if(direccionImagenes.toUpperCase().contains("C:") ||
			 * direccionImagenes.toUpperCase().contains("D:") ||
			 * direccionImagenes.toUpperCase().contains("E:") ||
			 * direccionImagenes.toUpperCase().contains("F:")){
			 * direccionImagenes= direccionImagenes + "\\"; }else{
			 * direccionImagenes= direccionImagenes + "/"; }
			 * 
			 * parametros.put("imagesDir", direccionImagenes);
			 */

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

			/** DIRECCION IMAGENES */
			/*
			 * String direccionImagenes=
			 * FacesContext.getCurrentInstance().getExternalContext().
			 * getRealPath("images/logoSII/");
			 * 
			 * 
			 * if(direccionImagenes.toUpperCase().contains("C:") ||
			 * direccionImagenes.toUpperCase().contains("D:") ||
			 * direccionImagenes.toUpperCase().contains("E:") ||
			 * direccionImagenes.toUpperCase().contains("F:")){
			 * direccionImagenes= direccionImagenes + "\\"; }else{
			 * direccionImagenes= direccionImagenes + "/"; }
			 * 
			 * parametros.put("imagesDir", direccionImagenes);
			 */

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

	public Tiposervicio getTipoServicioSelect() {
		return tipoServicioSelect;
	}

	public void setTipoServicioSelect(Tiposervicio tipoServicioSelect) {
		this.tipoServicioSelect = tipoServicioSelect;
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

	public List<Tiposervicio> getListarTipoServicio() {
		return listarTipoServicio;
	}

	public void setListarTipoServicio(List<Tiposervicio> listarTipoServicio) {
		this.listarTipoServicio = listarTipoServicio;
	}

	public Tiposervicio getTiposerv() {
		return tiposerv;
	}

	public void setTiposerv(Tiposervicio tiposerv) {
		this.tiposerv = tiposerv;
	}

	public LaboratorioLab getLaboratorioLabSelect() {
		return laboratorioLabSelect;
	}

	public void setLaboratorioLabSelect(LaboratorioLab laboratorioLabSelect) {
		this.laboratorioLabSelect = laboratorioLabSelect;
	}

	public List<LaboratorioLab> getListarLaboratoriosLab() {
		return listarLaboratoriosLab;
	}

	public void setListarLaboratoriosLab(List<LaboratorioLab> listarLaboratoriosLab) {
		this.listarLaboratoriosLab = listarLaboratoriosLab;
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

}
