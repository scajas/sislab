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
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.edu.epn.laboratorioBJ.beans.CompraDAO;
import ec.edu.epn.laboratorioBJ.beans.ProveedorLabDAO;

import ec.edu.epn.laboratorioBJ.entities.Compra;
import ec.edu.epn.laboratorioBJ.entities.ProveedorLab;
import ec.edu.epn.seguridad.VO.SesionUsuario;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

@ManagedBean(name = "compraController")
@SessionScoped
@FacesValidator("primeDateRangeValidator")


public class CompraController implements Serializable, Validator {

	/** VARIABLES DE SESION ***/
	private static final long serialVersionUID = 6771930005130933302L;
	FacesContext fc = FacesContext.getCurrentInstance();
	HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
	HttpSession session = request.getSession();
	SesionUsuario su = (SesionUsuario) session.getAttribute("sesionUsuario");

	/****************************************************************************/

	/** SERIVICIOS **/

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/CompraDAOImplement!ec.edu.epn.laboratorioBJ.beans.CompraDAO")
	private CompraDAO compraI;

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/ProveedorLabDAOImplement!ec.edu.epn.laboratorioBJ.beans.ProveedorLabDAO")
	private ProveedorLabDAO proveedorI;// I (interface)

	// Variables

	private Compra compra;
	private List<Compra> listCompra = new ArrayList<Compra>();
	private StreamedContent streamFile = null;

	// proveedorLab
	private ProveedorLab proveedor;
	private List<ProveedorLab> listaProveedorLab = new ArrayList<ProveedorLab>();

	private Date fechaInicio;
	private Date fechaFin;


	// Metodo Init
	@PostConstruct
	public void init() {
		try {

			// Compra

			listCompra = compraI.getAll(Compra.class);
			compra = new Compra();

		} catch (Exception e) {
			// TODO Auto-generated catch block
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

	public void consultarCompra() {
		try {

			listCompra = compraI.getParametrosCompra(cambioFecha(getFechaInicio()), cambioFecha(getFechaFin()));

			System.out.print("NUMERO DE COMPRAS OBTENIDOS >:V " + listCompra.size());

			mensajeInfo("Numero de coincidencias encontradas:" + listCompra.size());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	/****** Generacion de PDF ****/

	public void generarPDF(ActionEvent event) throws Exception {
		try {

			if (streamFile != null)
				streamFile.getStream().close();

			Map<String, Object> parametros = new HashMap<String, Object>();
			parametros.put("fechaInicio", cambioFecha(getFechaInicio()));
			parametros.put("fechaFin", cambioFecha(getFechaFin()));
			parametros.put("idUnidad", su.UNIDAD_USUARIO_LOGEADO);
				
			
			String direccion = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/reportes/");
			if (direccion.toUpperCase().contains("C:") || direccion.toUpperCase().contains("D:")
					|| direccion.toUpperCase().contains("E:") || direccion.toUpperCase().contains("F:")) {
				direccion = direccion + "\\";
			} else {
				direccion = direccion + "/";
			}

			String jrxmlFile = FacesContext.getCurrentInstance().getExternalContext()
					.getRealPath("/reportes/ReporteCompra.jrxml");
			InputStream input = new FileInputStream(new File(jrxmlFile));
			JasperReport jasperReport = JasperCompileManager.compileReport(input);
			parametros.put(JRParameter.REPORT_CONNECTION, coneccionSQL());

			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros);

			File sourceFile = new File(jrxmlFile);
			File destFile = new File(sourceFile.getParent(), "compra.pdf");

			JasperExportManager.exportReportToPdfFile(jasperPrint, destFile.toString());
			InputStream stream = new FileInputStream(destFile);

			//streamFile = new DefaultStreamedContent(pdfData(), "application/pdf", "document.pdf");
			streamFile = new DefaultStreamedContent(stream, "application/pdf", "compra.pdf");

		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
					new FacesMessage(FacesMessage.SEVERITY_FATAL, "", "ERROR"));

		}

	}
	





		// seteo de fecha
	public String cambioFecha(Date fecha) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

		String fechaFinal = format.format(fecha);

		return fechaFinal;
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
	
	String fileDirectory = "C:/directory";
	String filePath = "resources/pdf";
	String fileName = "testPdf.pdf";

	public String filePathComplete() {
	    String path = fileDirectory + File.separator + filePath
	            + File.separator + fileName;
	    File pdf = new File(path);
	    if (pdf.exists()) {
	        path = "/pdf//" + filePath + File.separator + fileName;
	    } else {
	        // Information message
	    }
	    return path;
	  }

	/*
	 * get and set
	 */

	public Compra getCompra() {
		return compra;
	}

	public void setCompra(Compra compra) {
		this.compra = compra;
	}

	public List<Compra> getListCompra() {
		return listCompra;
	}

	public void setListCompra(List<Compra> listCompra) {
		this.listCompra = listCompra;
	}

	public ProveedorLab getProveedor() {
		return proveedor;
	}

	public void setProveedor(ProveedorLab proveedor) {
		this.proveedor = proveedor;
	}

	public List<ProveedorLab> getListaProveedorLab() {
		return listaProveedorLab;
	}

	public void setListaProveedorLab(List<ProveedorLab> listaProveedorLab) {
		this.listaProveedorLab = listaProveedorLab;
	}

	public StreamedContent getStreamFile() {
		return streamFile;
	}

	public void setStreamFile(StreamedContent streamFile) {
		this.streamFile = streamFile;
		System.out.println("PASA POR AQUI "+ streamFile);
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

	@Override
	public void validate(FacesContext arg0, UIComponent arg1, Object arg2) throws ValidatorException {
		// TODO Auto-generated method stub
		
	}

}
