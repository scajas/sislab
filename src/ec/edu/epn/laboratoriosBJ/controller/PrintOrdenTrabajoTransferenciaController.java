package ec.edu.epn.laboratoriosBJ.controller;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.RemoveException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.primefaces.context.RequestContext;

import ec.edu.epn.facturacion.entities.Factura;
import ec.edu.epn.facturacion.entities.TransferenciaInterna;
import ec.edu.epn.laboratorioBJ.beans.ClienteDAO;
import ec.edu.epn.laboratorioBJ.beans.DetalleOrdenDAO;
import ec.edu.epn.laboratorioBJ.beans.DetalleProDAO;
import ec.edu.epn.laboratorioBJ.beans.ExistenciasDAO;
import ec.edu.epn.laboratorioBJ.beans.LaboratorioDAO;
import ec.edu.epn.laboratorioBJ.beans.OrdenTrabajoDAO;
import ec.edu.epn.laboratorioBJ.beans.PersonalLabDAO;
import ec.edu.epn.laboratorioBJ.beans.ProformaDAO;
import ec.edu.epn.laboratorioBJ.beans.ServicioDAO;
import ec.edu.epn.laboratorioBJ.beans.TipoClienteDAO;
import ec.edu.epn.laboratorioBJ.beans.UnidadDAO;
import ec.edu.epn.laboratorioBJ.beans.UnidadMedidaDAO;
import ec.edu.epn.laboratorioBJ.entities.Cliente;
import ec.edu.epn.laboratorioBJ.entities.DetalleProforma;
import ec.edu.epn.laboratorioBJ.entities.Detalleorden;
import ec.edu.epn.laboratorioBJ.entities.Existencia;
import ec.edu.epn.laboratorioBJ.entities.LaboratorioLab;
import ec.edu.epn.laboratorioBJ.entities.Metodo;
import ec.edu.epn.laboratorioBJ.entities.Movimientosinventario;
import ec.edu.epn.laboratorioBJ.entities.OrdenTrabajo;
import ec.edu.epn.laboratorioBJ.entities.PersonalLab;
import ec.edu.epn.laboratorioBJ.entities.Proforma;
import ec.edu.epn.laboratorioBJ.entities.Servicio;
import ec.edu.epn.laboratorioBJ.entities.UnidadLabo;
import ec.edu.epn.seguridad.VO.SesionUsuario;
import javax.faces.application.FacesMessage;

@ManagedBean(name = "printOrdenTransferenciaController")
@SessionScoped

public class PrintOrdenTrabajoTransferenciaController implements Serializable {

	/** VARIABLES DE SESION ***/
	private static final long serialVersionUID = 1L;
	FacesContext fc = FacesContext.getCurrentInstance();
	HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
	HttpSession session = request.getSession();
	SesionUsuario su = (SesionUsuario) session.getAttribute("sesionUsuario");

	/****************************************************************************/
	/** SERVICIOS **/
	@EJB(lookup = "java:global/ServiciosSeguridadEPN/OrdenTrabajoDAOImplement!ec.edu.epn.laboratorioBJ.beans.OrdenTrabajoDAO")
	private OrdenTrabajoDAO ordenTrabajoI;// I (interface)

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/DetalleOrdenDAOImplement!ec.edu.epn.laboratorioBJ.beans.DetalleOrdenDAO")
	private DetalleOrdenDAO detalleOrdenI;

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/PersonalLabDAOImplement!ec.edu.epn.laboratorioBJ.beans.PersonalLabDAO")
	private PersonalLabDAO personalLabI;

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/LaboratorioDAOImplement!ec.edu.epn.laboratorioBJ.beans.LaboratorioDAO")
	private LaboratorioDAO laboratorioI;

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/ExistenciasDAOImplement!ec.edu.epn.laboratorioBJ.beans.ExistenciasDAO")
	private ExistenciasDAO existenciasI;

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/UnidadMedidaDAOImplement!ec.edu.epn.laboratorioBJ.beans.UnidadMedidaDAO")
	private UnidadMedidaDAO unidadMedidaI;

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/UnidadDAOImplement!ec.edu.epn.laboratorioBJ.beans.UnidadDAO")
	private UnidadDAO unidadI;

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/ProformaDAOImplement!ec.edu.epn.laboratorioBJ.beans.ProformaDAO")
	private ProformaDAO proformaI;

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/DetalleProDAOImplement!ec.edu.epn.laboratorioBJ.beans.DetalleProDAO")
	private DetalleProDAO detalleProI;

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/ClienteDAOImplement!ec.edu.epn.laboratorioBJ.beans.ClienteDAO")
	private ClienteDAO clienteI;

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/TipoClienteDAOImplement!ec.edu.epn.laboratorioBJ.beans.TipoClienteDAO")
	private TipoClienteDAO tipoClienteI;

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/ServicioDAOImplement!ec.edu.epn.laboratorioBJ.beans.ServicioDAO")
	private ServicioDAO servicioI;

	/****************************************************************************/
	/** VARIABLES **/

	/* Orden Interna */
	private OrdenTrabajo ordenTrabajo; // Orden interno

	/* Orden Detalle OT */
	private List<Detalleorden> listaDetalleOrden = new ArrayList<>();
	private List<Detalleorden> listaDetalleOrdenMuestra = new ArrayList<>();

	/* Personal */
	private PersonalLab personalLab;
	private List<PersonalLab> tempPersonalLabs = new ArrayList<PersonalLab>();

	/* Cliente */
	private Cliente cliente;

	/* Servicio */

	/* Metodo */

	/* Unidad */
	private UnidadLabo unidad = new UnidadLabo();

	/* Laboratorio */
	private LaboratorioLab laboratorio = new LaboratorioLab();

	/** METODO Init **/
	@PostConstruct
	public void init() {
		try {
			String id = obtenerVariable();
			ordenTrabajo = ordenTrabajoI.buscarOTById(id);
			listaDetalleOrden = ordenTrabajoI.listarDetalleOrdenById(id);
			llenarPersonalTemp(listaDetalleOrden);
			System.out.println("Lista de detallePro: " + listaDetalleOrden.size());
			System.out.println("Lista de personal: " + tempPersonalLabs.size());

			Long iduser = su.id_usuario_log;

			setUnidad((UnidadLabo) unidadI.getById(UnidadLabo.class, su.UNIDAD_USUARIO_LOGEADO));

			laboratorio = proformaI.obtenerLaboratorioByUsr(iduser.intValue(), su.UNIDAD_USUARIO_LOGEADO);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/* Metodos de limpieza de formularios */

	public void limpiarOT() {
		try {
			String id = obtenerVariable();
			ordenTrabajo = ordenTrabajoI.buscarOTById(id);
			listaDetalleOrden = ordenTrabajoI.listarDetalleOrdenById(id);
			llenarPersonalTemp(listaDetalleOrden);
			llenarlistaTempM();
			System.out.println("Lista de detallePro: " + listaDetalleOrden.size());
			System.out.println("Lista de personal: " + tempPersonalLabs.size());

			Long iduser = su.id_usuario_log;
			// iduser.intValue());
			setUnidad((UnidadLabo) unidadI.getById(UnidadLabo.class, su.UNIDAD_USUARIO_LOGEADO));

			laboratorio = proformaI.obtenerLaboratorioByUsr(iduser.intValue(), su.UNIDAD_USUARIO_LOGEADO);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void llenarlistaTempM() {
		listaDetalleOrdenMuestra.clear();
		boolean prueba = true;

		for (Detalleorden detalleo : listaDetalleOrden) {

			if (listaDetalleOrdenMuestra.size() == 0) {
				listaDetalleOrdenMuestra.add(detalleo);
			} else {
				for (Detalleorden d : listaDetalleOrdenMuestra) {
					System.out.println("compara: " + detalleo.getMuestra().getIdMuestra() + " con"
							+ d.getMuestra().getIdMuestra());
					if (detalleo.getMuestra().getIdMuestra().equals(d.getMuestra().getIdMuestra())) {
						prueba = true;
						break;
					} else {
						prueba = false;
					}
				}

				if (prueba == false) {
					//System.out.println("Este es ela muestra que guarda: " + detalleo.getPersonal().getIdPersonal());
					listaDetalleOrdenMuestra.add(detalleo);
				} else {
					//System.out.println("No se guarda la muestra");
				}

			}

		}
	}

	public int listarNumeroM(String idMuestra) {
		int i = 1;

		System.out.println("Este es el tamaño de la lista: " + listaDetalleOrdenMuestra.size());

		for (Detalleorden detalleorden : listaDetalleOrdenMuestra) {
			if (detalleorden.getMuestra().getIdMuestra().equals(idMuestra)) {
				break;
			} else {
				i++;
			}

		}

		// System.out.println("Este es id: " + getTempIdTablaM());

		return i;
	}

	public void llenarPersonalTemp(List<Detalleorden> detalleordens) {
		tempPersonalLabs.clear();
		boolean prueba = true;

		for (Detalleorden detalleo : detalleordens) {
			System.out.println("Este es la lista actual: " + tempPersonalLabs.size());
			System.out.println("Este es el personal: " + detalleo.getPersonal().getIdPersonal());

			if (tempPersonalLabs.size() == 0) {
				tempPersonalLabs.add(detalleo.getPersonal());
				System.out.println("entra al if");
			} else {
				for (PersonalLab p : tempPersonalLabs) {
					System.out
							.println("compara: " + detalleo.getPersonal().getIdPersonal() + " con" + p.getIdPersonal());
					if (detalleo.getPersonal().getIdPersonal().equals(p.getIdPersonal())) {
						prueba = true;
						break;
					} else {
						prueba = false;
					}
				}

				if (prueba == false) {
					System.out.println("Este es el personal que guarda: " + detalleo.getPersonal().getIdPersonal());
					tempPersonalLabs.add(detalleo.getPersonal());
				} else {
					System.out.println("No se guarda el personal");
				}

			}

		}
	}

	public String cambiarFormatoDouble(double numero) {
		DecimalFormat formato = new DecimalFormat("#.00");
		return formato.format(numero);
	}

	public void holaMundo() {
		System.err.println("esta funcando");
	}

	public void regresarPanelOT() {

		try {
			FacesContext contex = FacesContext.getCurrentInstance();

			contex.getExternalContext().redirect("/SisLab/pages/ordenTrabajoDesdeTransf.jsf");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String obtenerVariable() {
		try {

			FacesContext contex = FacesContext.getCurrentInstance();
			String id = contex.getExternalContext().getSessionMap().get("idOT").toString();

			System.out.println("Esto es el id trae: " + id);
			return id;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Me voy al carajo, no funciona esta redireccion");
			return null;
		}
	}

	/******************************/
	public String metodo(String id) {
		String nombre = "";

		Metodo m = new Metodo();

		m = detalleOrdenI.findMetodoById(id);

		if (m == null) {
			nombre = "N/A";
		} else {
			nombre = m.getNombreMt();
		}

		return nombre;
	}

	public Servicio servicio(String idServicio) {

		Servicio s = new Servicio();

		s = detalleOrdenI.findServicioById(idServicio);

		if (s == null) {
			s.setNombreS("N/A");
		}

		return s;
	}

	public Factura obtenerFactura(String idFactura) {

		Factura f = new Factura();

		f = ordenTrabajoI.buscarFacturaById(idFactura);

		if (f == null) {
			f.setIdProforma("N/A");
		}

		return f;
	}

	public TransferenciaInterna obtenerTransferencia(String idTransferencia) {

		TransferenciaInterna t = new TransferenciaInterna();

		t = ordenTrabajoI.buscarTransferenciaById(idTransferencia);

		if (t == null) {
			t.setIdProforma("N/A");
		}

		return t;
	}

	public boolean disabledScroll() {
		System.out.println(listaDetalleOrden.size());
		if (listaDetalleOrden.size() > 15) {
			return false;
		} else {
			return true;
		}

	}

	/****** Manejo de fechas ****/
	public String cambioFecha(Date fecha) {
		if (fecha == null) {
			return "No registrada";
		} else {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

			String fechaFinal = format.format(fecha);

			return fechaFinal;
		}

	}

	/****** Mensajes Personalizados ****/
	public void mensajeError(String mensaje) {

		FacesContext contextM = FacesContext.getCurrentInstance();
		contextM.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR!", mensaje));
	}

	public void mensajeInfo(String mensaje) {
		FacesContext contextM = FacesContext.getCurrentInstance();

		contextM.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", mensaje));

	}

	/****** Getter y Setter de Estado Producto ****/

	public OrdenTrabajo getOrdenTrabajo() {
		return ordenTrabajo;
	}

	public void setOrdenTrabajo(OrdenTrabajo ordenTrabajo) {
		this.ordenTrabajo = ordenTrabajo;
	}

	public List<Detalleorden> getListaDetalleOrden() {
		return listaDetalleOrden;
	}

	public void setListaDetalleOrden(List<Detalleorden> listaDetalleOrden) {
		this.listaDetalleOrden = listaDetalleOrden;
	}

	public PersonalLab getPersonalLab() {
		return personalLab;
	}

	public void setPersonalLab(PersonalLab personalLab) {
		this.personalLab = personalLab;
	}

	public List<PersonalLab> getTempPersonalLabs() {
		return tempPersonalLabs;
	}

	public void setTempPersonalLabs(List<PersonalLab> tempPersonalLabs) {
		this.tempPersonalLabs = tempPersonalLabs;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public UnidadLabo getUnidad() {
		return unidad;
	}

	public void setUnidad(UnidadLabo unidad) {
		this.unidad = unidad;
	}

	public LaboratorioLab getLaboratorio() {
		return laboratorio;
	}

	public void setLaboratorio(LaboratorioLab laboratorio) {
		this.laboratorio = laboratorio;
	}

	public List<Detalleorden> getListaDetalleOrdenMuestra() {
		return listaDetalleOrdenMuestra;
	}

	public void setListaDetalleOrdenMuestra(List<Detalleorden> listaDetalleOrdenMuestra) {
		this.listaDetalleOrdenMuestra = listaDetalleOrdenMuestra;
	}

}
