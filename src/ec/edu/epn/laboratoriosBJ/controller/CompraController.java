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
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.primefaces.model.StreamedContent;

import ec.edu.epn.laboratorioBJ.beans.CompraDAO;
import ec.edu.epn.laboratorioBJ.beans.ProveedorLabDAO;

import ec.edu.epn.laboratorioBJ.entities.Compra;
import ec.edu.epn.laboratorioBJ.entities.ProveedorLab;
import ec.edu.epn.seguridad.VO.SesionUsuario;

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

	// seteo de fecha
	public String cambioFecha(Date fecha) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

		String fechaFinal = format.format(fecha);

		return fechaFinal;
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
