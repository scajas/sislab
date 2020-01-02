package ec.edu.epn.laboratoriosBJ.controller;

import java.io.Serializable;
import java.util.ArrayList;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
//import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
//import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

//import org.primefaces.context.RequestContext;

import ec.edu.epn.laboratorioBJ.beans.DetalleOrdenDAO;
import ec.edu.epn.laboratorioBJ.beans.UnidadDAO;
//import ec.edu.epn.laboratorioBJ.beans.NormaDAO;
import ec.edu.epn.laboratorioBJ.entities.Detalleorden;
import ec.edu.epn.laboratorioBJ.entities.Metodo;
//import ec.edu.epn.laboratorioBJ.entities.Norma;
import ec.edu.epn.laboratorioBJ.entities.UnidadLabo;
import ec.edu.epn.seguridad.VO.SesionUsuario;

@ManagedBean(name = "detalleOrdenController")
@SessionScoped
public class DetalleOrdenController implements Serializable {

	/** VARIABLES DE SESION ***/
	private static final long serialVersionUID = 6771930005130933302L;
	FacesContext fc = FacesContext.getCurrentInstance();
	HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
	HttpSession session = request.getSession();
	SesionUsuario su = (SesionUsuario) session.getAttribute("sesionUsuario");

	/*****************************************************************************/

	/** SERIVICIOS **/

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/DetalleOrdenDAOImplement!ec.edu.epn.laboratorioBJ.beans.DetalleOrdenDAO")

	private DetalleOrdenDAO detalleOrdenI;

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/UnidadDAOImplement!ec.edu.epn.laboratorioBJ.beans.UnidadDAO")
	private UnidadDAO unidadI;

	/****************************************************************************/
	/** VARIABLES DE LA CLASE **/
	private Detalleorden detalleOrden;
	private List<Detalleorden> detallesOrden = new ArrayList<Detalleorden>();

	// Metodo Init
	@PostConstruct
	public void init() {
		try {

			UnidadLabo uni = new UnidadLabo();
			uni = (UnidadLabo) unidadI.getById(UnidadLabo.class, su.UNIDAD_USUARIO_LOGEADO);
			detallesOrden = detalleOrdenI.listTAbyId(uni.getCodigoU());

			setDetalleOrden(new Detalleorden());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String metodo(String idMetodo) {
		String nombre = "";

		Metodo metodo = new Metodo();

		metodo = detalleOrdenI.findMetodoById(idMetodo);

		if (metodo == null) {
			nombre = "N/A";
		} else {
			nombre = metodo.getNombreMt();
		}

		return nombre;
	}

	public String servicio(String idServicio) {
		String nombre = "";

		Metodo metodo = new Metodo();

		metodo = detalleOrdenI.findMetodoById(idServicio);

		if (metodo == null) {
			nombre = "N/A";
		} else {
			nombre = metodo.getServicio().getNombreS();
		}

		return nombre;
	}

	/****** Getter y Setter ****/
	public Detalleorden getDetalleOrden() {
		return detalleOrden;
	}

	public void setDetalleOrden(Detalleorden detalleOrden) {
		this.detalleOrden = detalleOrden;
	}

	public List<Detalleorden> getDetallesOrden() {
		return detallesOrden;
	}

	public void setDetallesOrden(List<Detalleorden> detallesOrden) {
		this.detallesOrden = detallesOrden;
	}

}
