package ec.edu.epn.laboratoriosBJ.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.primefaces.context.RequestContext;

import ec.edu.epn.facturacion.entities.EstadoFactura;
import ec.edu.epn.facturacion.entities.Factura;
import ec.edu.epn.laboratorioBJ.beans.MuestraDAO;
import ec.edu.epn.laboratorioBJ.beans.UnidadDAO;
import ec.edu.epn.laboratorioBJ.entities.Muestra;
import ec.edu.epn.laboratorioBJ.entities.UnidadLabo;
import ec.edu.epn.laboratorios.utilidades.Utilidades;
import ec.edu.epn.seguridad.VO.SesionUsuario;

@ManagedBean(name = "muestraController")
@SessionScoped

public class MuestraController implements Serializable {

	/** VARIABLES DE SESION ***/
	private static final long serialVersionUID = 6771930005130933302L;

	FacesContext fc = FacesContext.getCurrentInstance();
	HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
	HttpSession session = request.getSession();
	SesionUsuario su = (SesionUsuario) session.getAttribute("sesionUsuario");

	/****************************************************************************/

	/** SERVICIOS **/
	@EJB(lookup = "java:global/ServiciosSeguridadEPN/MuestraDAOImplement!ec.edu.epn.laboratorioBJ.beans.MuestraDAO")
	private MuestraDAO muestraI;

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/UnidadDAOImplement!ec.edu.epn.laboratorioBJ.beans.UnidadDAO")
	private UnidadDAO unidadI;
	/****************************************************************************/
	/** VARIABLES DE LA CLASE **/
	private List<Muestra> listaMuestra = new ArrayList<>();
	private Muestra nuevaMuestra;
	private Muestra muestra;
	private String nombreMuestra;
	private List<Factura> listaFactura = new ArrayList<>();
	private Factura factura;
	private Factura selectFactura;

	// ****** Filtros ****//*
	private List<Factura> filtrarFacturas;
	private List<Muestra> filtrarMuestras;
	private Utilidades utilidades;

	/** MÉTODO INIT **/
	@PostConstruct
	public void init() {
		try {

			UnidadLabo uni = new UnidadLabo();
			uni = (UnidadLabo) unidadI.getById(UnidadLabo.class, su.UNIDAD_USUARIO_LOGEADO);
			listaMuestra = muestraI.ListaMFById(uni.getCodigoU());

			setNuevaMuestra(new Muestra());
			muestra = new Muestra();
			utilidades = new Utilidades();

		} catch (Exception e) {

		}

	}

	/****** Agregar Nueva Muestra ****/

	public void agregarMuestra() {

		RequestContext context = RequestContext.getCurrentInstance();

		try {
			if (buscarMuesta(nuevaMuestra.getCodigoMCliente()) == true) {

				utilidades.mensajeError("La Muestra (" + nuevaMuestra.getCodigoMCliente() + " ) ya existe.");
			} else {

				Integer idMuestra = muestraI.generarId("Muestra", "auxMuestra");
				String codigoMuestra = String.valueOf(idMuestra);
				nuevaMuestra.setAuxMuestra(idMuestra);
				nuevaMuestra.setNumMuestraFact(muestraI.generarId("Muestra", "numMuestraFact"));
				nuevaMuestra.setIdUnidad(su.UNIDAD_USUARIO_LOGEADO);
				UnidadLabo uni = new UnidadLabo();

				uni = (UnidadLabo) unidadI.getById(UnidadLabo.class, su.UNIDAD_USUARIO_LOGEADO);

				switch (codigoMuestra.length()) {
				case 1:
					nuevaMuestra.setIdMuestra(uni.getCodigoU() + "-MU" + "0000" + codigoMuestra);
					break;
				case 2:
					nuevaMuestra.setIdMuestra(uni.getCodigoU() + "-MU" + "000" + codigoMuestra);
					break;
				case 3:
					nuevaMuestra.setIdMuestra(uni.getCodigoU() + "-MU" + "00" + codigoMuestra);
					break;
				case 4:
					nuevaMuestra.setIdMuestra(uni.getCodigoU() + "-MU" + codigoMuestra);
					break;

				default:
					break;
				}

				muestraI.save(nuevaMuestra);
				listaMuestra = muestraI.getAll(Muestra.class);

				utilidades.mensajeInfo("La muestra (" + nuevaMuestra.getCodigoMCliente() + ") se ha almacenado exitosamente");

				nuevaMuestra = new Muestra();
				context.execute("PF('nuevaMuestra').hide();");

			}

		} catch (Exception e) {

			utilidades.mensajeError("Ha ocurrido un problema");

		}

	}

	/****** Modificar Muestra ****/

	public void modificarMuestra() {

		RequestContext context = RequestContext.getCurrentInstance();

		try {
			if (muestra.getCodigoMCliente().equals(getNombreMuestra())) {
				muestraI.update(muestra);
				listaMuestra = muestraI.getAll(Muestra.class);

				utilidades.mensajeInfo("La Muestra (" + muestra.getCodigoMCliente() + ") se ha actualizado exitosamente");

				context.execute("PF('modificarMuestra').hide();");

			} else if (buscarMuesta(muestra.getCodigoMCliente()) == false) {
				muestraI.update(muestra);
				listaMuestra = muestraI.getAll(Muestra.class);

				utilidades.mensajeInfo("La Muestra (" + muestra.getCodigoMCliente() + ") se ha actualizado exitosamente");

				context.execute("PF('modificarMuestra').hide();");

			} else {
				utilidades.mensajeError("La Muestra (" + muestra.getCodigoMCliente() + ") ya existe");

			}

		} catch (Exception e) {

			utilidades.mensajeError("Ha ocurrido un problema");
		}
	}

	/****** Eliminar Muestra ****/

	public void eliminarMuestra() {

		try {

			muestraI.delete(muestra);
			listaMuestra = muestraI.getAll(Muestra.class);

			utilidades.mensajeInfo("La Muestra (" + muestra.getCodigoMCliente() + ") se ha eliminado correctamente");

		} catch (Exception e) {

			if (e.getMessage() == "Transaction rolled back") {

				utilidades.mensajeError("La tabla Muestra (" + muestra.getCodigoMCliente() + ") tiene relación con otra tabla");

			} else {

				utilidades.mensajeError("Ha ocurrido un problema");

			}

		}

	}

	/****** Metodo de Busqueda de Facturas ****/
	public List<Factura> buscarFacturas() { //
		try {

			Long idUsuario = su.id_usuario_log;
			listaFactura = muestraI.getListaFacturas(idUsuario.intValue(), su.UNIDAD_USUARIO_LOGEADO);

		} catch (Exception e) {
	
		}
		return listaFactura;

	}

	/****** Busqueda de Muestra ****/

	private boolean buscarMuesta(String valor) {

		try {
			listaMuestra = muestraI.getAll(Muestra.class);
		} catch (Exception e) {

		}

		boolean resultado = false;
		for (Muestra tipo : listaMuestra) {
			if (tipo.getCodigoMCliente().equals(valor)) {
				resultado = true;
				break;
			} else {
				resultado = false;
			}
		}

		return resultado;
	}

	/****** Validación del Nombre ****/
	public void pasarNombre(String nombre) {
		setNombreMuestra(nombre);
	}

	/****** Selección de Facturas ****/

	public void seleccionarFactura() {

		try {
			getNuevaMuestra().setIdFactura(selectFactura.getIdFactura());
			utilidades.mensajeInfo("Se seleccionó la Factura (" + selectFactura.getIdFactura());
			selectFactura = new Factura();

		} catch (Exception e) {
			utilidades.mensajeError("No se ha seleccionado ninguna factura");
		}

	}

	/****** Metodo de Obtener Facturas ****/

	public String metodo(String idEstadoFactura) {
		String nombre = "";

		EstadoFactura estadoFactura = new EstadoFactura();

		estadoFactura = muestraI.findMetodoById(idEstadoFactura);

		if (estadoFactura == null) {
			nombre = "N/A";
		} else {
			nombre = estadoFactura.getNombreEf();
		}

		return nombre;
	}

	/****** Getter y Setter ****/

	public List<Muestra> getListaMuestra() {
		return listaMuestra;
	}

	public void setListaMuestra(List<Muestra> listaMuestra) {
		this.listaMuestra = listaMuestra;
	}

	public Muestra getMuestra() {
		return muestra;
	}

	public void setMuestra(Muestra muestra) {
		this.muestra = muestra;
	}

	public String getNombreMuestra() {
		return nombreMuestra;
	}

	public void setNombreMuestra(String nombreMuestra) {
		this.nombreMuestra = nombreMuestra;
	}

	public Muestra getNuevaMuestra() {
		return nuevaMuestra;
	}

	public void setNuevaMuestra(Muestra nuevaMuestra) {
		this.nuevaMuestra = nuevaMuestra;
	}

	public List<Factura> getListaFactura() {
		return listaFactura;
	}

	public void setListaFactura(List<Factura> listaFactura) {
		this.listaFactura = listaFactura;
	}

	public Factura getFactura() {
		return factura;
	}

	public void setFactura(Factura factura) {
		this.factura = factura;
	}

	public Factura getSelectFactura() {
		return selectFactura;
	}

	public void setSelectFactura(Factura selectFactura) {
		this.selectFactura = selectFactura;
	}

	public List<Factura> getFiltrarFacturas() {
		return filtrarFacturas;
	}

	public void setFiltrarFacturas(List<Factura> filtrarFacturas) {
		this.filtrarFacturas = filtrarFacturas;
	}

	public List<Muestra> getFiltrarMuestras() {
		return filtrarMuestras;
	}

	public void setFiltrarMuestras(List<Muestra> filtrarMuestras) {
		this.filtrarMuestras = filtrarMuestras;
	}

}