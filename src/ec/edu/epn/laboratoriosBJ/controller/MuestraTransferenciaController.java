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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import ec.edu.epn.facturacion.entities.TransferenciaInterna;
import ec.edu.epn.laboratorioBJ.beans.MuestraDAO;
import ec.edu.epn.laboratorioBJ.beans.UnidadDAO;
import ec.edu.epn.laboratorioBJ.entities.Muestra;
import ec.edu.epn.laboratorioBJ.entities.UnidadLabo;
import ec.edu.epn.seguridad.VO.SesionUsuario;

@ManagedBean(name = "muestraTransferenciaController")
@SessionScoped

public class MuestraTransferenciaController implements Serializable {

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
	private TransferenciaInterna transferenciaInterna;
	private TransferenciaInterna  selectTransferenciaInterna;

	// ****** Filtros ****//*
	private List<TransferenciaInterna> filtrarTransferenciaInterna;
	private List<Muestra> filtrarMuestras;

	/** MÉTODO INIT **/
	@PostConstruct
	public void init() {
		try {
			setListaMuestra(muestraI.getAll(Muestra.class));
			setNuevaMuestra(new Muestra());
			muestra = new Muestra();

		} catch (Exception e) {

		}

	}

	/****** Buscar Transferencias ****/
	public List<TransferenciaInterna> buscarTransferencias() { // 
		try {

			Long idUsuario = su.id_usuario_log;
			listaTransferencia = muestraI.getListaTransferencia(idUsuario.intValue(), su.UNIDAD_USUARIO_LOGEADO);
			System.out.println("Transferencias consultadas:" + listaTransferencia.size());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return listaTransferencia;

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

	/****** Agregar Nueva Muestra****/

	public void agregarMuestra() {

		try {
			if (buscarMuesta(nuevaMuestra.getCodigoMCliente()) == true) {

				mensajeError(
						"Ha ocurrido un error, La Muestra ( " + nuevaMuestra.getCodigoMCliente() + " ) ya existe.");

				nuevaMuestra = new Muestra();

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

				mensajeInfo("La muestra ( " + nuevaMuestra.getCodigoMCliente() + " ) se ha almacenado exitosamente");

				nuevaMuestra = new Muestra();

			}

		} catch (Exception e) {

			mensajeError("Ha ocurrido un error");

		}

	}

	// ****** Modificar Muestra ****//*

	public void modificarMuestra() {

		try {
			if (muestra.getCodigoMCliente().equals(getNombreMuestra())) {
				muestraI.update(muestra);
				listaMuestra = muestraI.getAll(Muestra.class);

				mensajeInfo("La Muestra ( " + muestra.getCodigoMCliente() + " ) se ha actualizado exitosamente");

			} else if (buscarMuesta(muestra.getCodigoMCliente()) == false) {
				muestraI.update(muestra);
				listaMuestra = muestraI.getAll(Muestra.class);

				mensajeInfo("La Muestra ( " + muestra.getCodigoMCliente() + " ) se ha actualizado exitosamente");

			} else {
				mensajeError("La Muestra ( " + muestra.getCodigoMCliente() + " ) ya existe");

			}

		} catch (Exception e) {

			mensajeError("Ha ocurrido un error");
		}
	}

	// ****** Eliminar Estado Producto ****//*

	public void eliminarMuestra() {

		try {

			muestraI.delete(muestra);
			listaMuestra = muestraI.getAll(Muestra.class);

			mensajeInfo("La Muestra ( " + muestra.getCodigoMCliente() + " ) se ha eliminado correctamente");

		} catch (Exception e) {

			if (e.getMessage() == "Transaction rolled back") {

				mensajeError("La tabla Muestra tiene relación con otra tabla");

			} else {

				mensajeError("Ha ocurrido un error");

			}

		}

	}

	// ****** Busqueda de Muestra ****//*

	private boolean buscarMuesta(String valor) {

		try {
			listaMuestra = muestraI.getAll(Muestra.class);
		} catch (Exception e) {

			e.printStackTrace();
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

	// ****** Validacion de Nombre ****//*
	public void pasarNombre(String nombre) {
		setNombreMuestra(nombre);
	}

	// ****** Seleccion de Transferencias ****//*
	public void seleccionarIdTransferencia() {

		try {
			getNuevaMuestra().setIdTi(selectTransferenciaInterna.getIdTi());

			mensajeInfo("Se seleccionó la Transferencia (" + selectTransferenciaInterna.getIdTi());

			System.out.println(
					"este es el id de Transferencia: " + selectTransferenciaInterna.getIdTi() + getNuevaMuestra().getIdTi());
		} catch (Exception e) {
			
			mensajeError("No se ha seleccionado ninguna Transferencia");
		}

	}


	/****** metodos Get y Set de Transferencia ****/

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

	public List<Muestra> getFiltrarMuestras() {
		return filtrarMuestras;
	}

	public void setFiltrarMuestras(List<Muestra> filtrarMuestras) {
		this.filtrarMuestras = filtrarMuestras;
	}

	public TransferenciaInterna getTransferenciaInterna() {
		return transferenciaInterna;
	}

	public void setTransferenciaInterna(TransferenciaInterna transferenciaInterna) {
		this.transferenciaInterna = transferenciaInterna;
	}

	public TransferenciaInterna getSelectTransferenciaInterna() {
		return selectTransferenciaInterna;
	}

	public void setSelectTransferenciaInterna(TransferenciaInterna selectTransferenciaInterna) {
		this.selectTransferenciaInterna = selectTransferenciaInterna;
	}

	public List<TransferenciaInterna> getFiltrarTransferenciaInterna() {
		return filtrarTransferenciaInterna;
	}

	public void setFiltrarTransferenciaInterna(List<TransferenciaInterna> filtrarTransferenciaInterna) {
		this.filtrarTransferenciaInterna = filtrarTransferenciaInterna;
	}

	private List<TransferenciaInterna> listaTransferencia = new ArrayList<>();
	
	public List<TransferenciaInterna> getListaTransferencia() {
		return listaTransferencia;
	}

	public void setListaTransferencia(List<TransferenciaInterna> listaTransferencia) {
		this.listaTransferencia = listaTransferencia;
	}

}