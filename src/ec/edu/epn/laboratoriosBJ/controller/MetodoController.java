package ec.edu.epn.laboratoriosBJ.controller;

import java.io.Serializable;
import java.math.BigDecimal;
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

import org.primefaces.context.RequestContext;

import ec.edu.epn.laboratorioBJ.beans.DetalleMetodoDAO;
import ec.edu.epn.laboratorioBJ.beans.ExistenciasDAO;
import ec.edu.epn.laboratorioBJ.beans.MetodoDAO;
import ec.edu.epn.laboratorioBJ.beans.ServicioDAO;
import ec.edu.epn.laboratorioBJ.beans.UnidadDAO;
import ec.edu.epn.laboratorioBJ.entities.Detallemetodo;
import ec.edu.epn.laboratorioBJ.entities.Existencia;
import ec.edu.epn.laboratorioBJ.entities.LaboratorioLab;
import ec.edu.epn.laboratorioBJ.entities.Metodo;
import ec.edu.epn.laboratorioBJ.entities.Servicio;
import ec.edu.epn.laboratorioBJ.entities.UnidadLabo;
import ec.edu.epn.seguridad.VO.SesionUsuario;

@ManagedBean(name = "metodoController")
@SessionScoped

public class MetodoController implements Serializable {

	/** VARIABLES DE SESION ***/
	private static final long serialVersionUID = 6771930005130933302L;
	FacesContext fc = FacesContext.getCurrentInstance();
	HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
	HttpSession session = request.getSession();
	SesionUsuario su = (SesionUsuario) session.getAttribute("sesionUsuario");

	/****************************************************************************/

	/** SERIVICIOS **/

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/MetodoDAOImplement!ec.edu.epn.laboratorioBJ.beans.MetodoDAO")
	private MetodoDAO metodoI;

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/UnidadDAOImplement!ec.edu.epn.laboratorioBJ.beans.UnidadDAO")
	private UnidadDAO unidadI;

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/ServicioDAOImplement!ec.edu.epn.laboratorioBJ.beans.ServicioDAO")
	private ServicioDAO servicioI;

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/DetalleMetodoDAOImplement!ec.edu.epn.laboratorioBJ.beans.DetalleMetodoDAO")
	private DetalleMetodoDAO detalleMetodoI;

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/ExistenciasDAOImplement!ec.edu.epn.laboratorioBJ.beans.ExistenciasDAO")
	private ExistenciasDAO existenciasI;

	// Variables

	private Metodo metodo;
	private List<Metodo> metodos = new ArrayList<Metodo>();
	private Metodo nuevoMetodo;
	private List<Metodo> filterMetodos = new ArrayList<>();
	private List<Metodo> nuevoMetodos = new ArrayList<>();
	private List<Detallemetodo> tempDetalleMetodo = new ArrayList<>();

	private Servicio servicioSelect;
	private List<Servicio> servicios;

	private Existencia existencia;
	private List<Existencia> existencias = new ArrayList<>();
	private List<Existencia> filterExistencia;
	private Existencia selectExistencia;
	private List<Existencia> tempExistencias = new ArrayList<>();

	private List<Detallemetodo> detalleMetodos = new ArrayList<>();
	private List<Detallemetodo> tempDetalleMetodos = new ArrayList<>();
	private List<Detallemetodo> tempDetalleMetodosEdit = new ArrayList<>();
	private List<Detallemetodo> nuevoDetalleMetodos = new ArrayList<>();
	private Detallemetodo nuevoDetalleMetodo;
	private Detallemetodo nuevoDetalleMetodoAux;
	private Detallemetodo detalleMetodo;

	private LaboratorioLab laboratorio;

	UnidadLabo unidadLabo;
	private String nombreM;

	// Metodo Init
	@PostConstruct
	public void init() {
		try {

			// Metodos

			metodo = new Metodo();
			nuevoMetodo = new Metodo();
			tblMetodos();

			// Servicios
			UnidadLabo uni = new UnidadLabo();
			uni = (UnidadLabo) unidadI.getById(UnidadLabo.class, su.UNIDAD_USUARIO_LOGEADO);
			servicios = servicioI.listaServicioUnidad(uni.getIdUnidad());

			// Existencias
			existencias = detalleMetodoI.listaExistencias(su.UNIDAD_USUARIO_LOGEADO);
			selectExistencia = new Existencia();

			// DetalleMetodo
			nuevoDetalleMetodos.clear();
			tempDetalleMetodos.clear();
			tempDetalleMetodos = new ArrayList<>();

			tempDetalleMetodosEdit.clear();
			tempDetalleMetodosEdit = new ArrayList<>();

			nuevoDetalleMetodo = new Detallemetodo();
			detalleMetodo = new Detallemetodo();

			tempExistencias = new ArrayList<Existencia>();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void tblMetodos() {
		try {
			UnidadLabo uni = new UnidadLabo();
			uni = (UnidadLabo) unidadI.getById(UnidadLabo.class, su.UNIDAD_USUARIO_LOGEADO);
			metodos = metodoI.getListMetodos(uni.getCodigoU());
		} catch (Exception e) {
			// TODO: handle exception
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

	public void seleccionarExistencia() {

		try {

			if (buscarExistencia(selectExistencia.getIdExistencia()) == false) {

				RequestContext context = RequestContext.getCurrentInstance();
				getNuevoDetalleMetodo().setIdExistencia(getSelectExistencia().getIdExistencia());

				mensajeInfo("Se seleccionó la Existencia (" + selectExistencia.getIdExistencia());
				existencia = selectExistencia;
				selectExistencia = new Existencia();
				filterExistencia = new ArrayList<Existencia>();
				context.execute("PF('listadoEx').hide();");

			} else {
				mensajeError("La existencia (" + selectExistencia.getIdExistencia() + ") ya ha sido seleccionada");
				selectExistencia = new Existencia();
			}

		} catch (Exception e) {
			mensajeError("No se ha seleccionado ninguna Existencia");
			e.printStackTrace();
		}

	}

	/****** Lista de Existencias ****/

	public boolean buscarExistencia(String id) {

		boolean resultado = false;

		for (Detallemetodo detalleMetodos : nuevoDetalleMetodos) {

			if (detalleMetodos.getIdExistencia().equals(id)) {
				resultado = true;
				break;
			} else {
				resultado = false;
			}

		}

		return resultado;
	}

	public void agregarDetalleTemp() {

		RequestContext context = RequestContext.getCurrentInstance();

		try {

			if (nuevoDetalleMetodo.getIdExistencia() == null) {
				mensajeError("No ha seleccionado Ninguna Existencia");
			} else {
				double resultado;
				double cantidadMo = nuevoDetalleMetodo.getCantidadDmt();
				double cantidadE = existencia.getCantidadE().doubleValue();

				resultado = cantidadE + cantidadMo;

				existencia.setCantidadE(BigDecimal.valueOf(resultado));
				nuevoDetalleMetodo.setCantidadDmt(getExistencia().getCantidadE().floatValue());

				tempExistencias.add(getExistencia());
				nuevoDetalleMetodos.add(nuevoDetalleMetodo);
				setDetalleMetodos(nuevoDetalleMetodos);

				mensajeInfo("Se ha almacenado (" + nuevoDetalleMetodo.getIdExistencia() + ") correctamente.");

				nuevoDetalleMetodo = new Detallemetodo();
				existencia = new Existencia();
				existencias = detalleMetodoI.listaExistencias(su.UNIDAD_USUARIO_LOGEADO);
				tempExistencias = new ArrayList<Existencia>();
				context.execute("PF('nuevaExistencia').hide();");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/****** Editar Temporal ****/

	public void editarTemp() {

		RequestContext context = RequestContext.getCurrentInstance();
		for (Detallemetodo dm : nuevoDetalleMetodos) {
			if (dm.getIdExistencia().equals(detalleMetodo.getIdExistencia())) {

				mensajeInfo("Se ha editado el Mov. de Inventario (" + detalleMetodo.getIdExistencia() + ")");
				context.execute("PF('editarMI').hide();");

				break;
			} else {

			}

		}

	}

	/****** Eliminar Temporal ****/

	public void eliminarTemp() {

		try {
			nuevoDetalleMetodos.remove(detalleMetodo);
			setDetalleMetodos(nuevoDetalleMetodos);
			mensajeInfo("Se ha eliminado el Detalle Método (" + detalleMetodo.getIdExistencia() + ")");
		} catch (Exception e) {
			e.printStackTrace();
			mensajeError("Ha ocurrido un error interno.");
		}

	}

	public void limpiarCampos() {

		try {

			// Metodo
			metodo = new Metodo();
			nuevoMetodo = new Metodo();
			servicioSelect = new Servicio();

			// Detalle Metodo
			nuevoDetalleMetodos.clear();
			tempDetalleMetodos.clear();
			nuevoDetalleMetodo = new Detallemetodo();
			detalleMetodo = new Detallemetodo();
			nuevoDetalleMetodoAux = new Detallemetodo();
			nuevoDetalleMetodo.setCantidadDmt(0);
			nuevoDetalleMetodoAux.setCantidadDmt(0);

			actualizarExistencias();
			mensajeInfo("Se ha limpiado todo el formulario");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/****** Cargar Detalle Metodo ****/

	public void cargarDetalleMet(String id) {

		tempDetalleMetodos = detalleMetodoI.listaDetallesById(id);

	}

	/****** Cargar Detalle Metodo - Edit ****/

	public void cargarDetalleMetEdit(String id) {

		tempDetalleMetodosEdit = detalleMetodoI.listaDetallesById(id);

	}

	/****** Busqueda de Existencias ****/
	public Existencia cambiarDatosExistencia(String id) {
		Existencia existenciatemp = detalleMetodoI.buscarExistencias(id);
		return existenciatemp;
	}

	public void guardarMetodo() {

		RequestContext context = RequestContext.getCurrentInstance();

		try {

			if (nuevoMetodo.getNombreMt() == "") {

				mensajeError("Ingrese el Método");

			} else if (servicioSelect == null) {

				mensajeError("Seleccione el Servicio");

			} else if (nuevoDetalleMetodos.size() == 0) {

				mensajeError("Debe ingresar Detalle Metodo");

			} else {

				/** ID Metodo **/
				getIdMetodo();
				nuevoMetodo.setServicio(servicioSelect);
				metodoI.save(nuevoMetodo);

				/** ID Detalle Metodo **/
				obtenerIdDetalle(nuevoMetodo);
				guardarDetalleM();

				/** ID ORD INV - MOV INV **/

				mensajeInfo("El Método (" + nuevoMetodo.getIdMetodo() + ") se ha almacenado exitosamente");
				context.execute("PF('nuevoMetodo').hide();");

				// Limpiar Campos y actualizar
				actualizarExistencias();
				tblMetodos();
				servicioSelect = new Servicio();
				nuevoMetodo = new Metodo();
				nuevoDetalleMetodos.clear();
				tempDetalleMetodos.clear();
				nuevoDetalleMetodo = new Detallemetodo();
				detalleMetodo = new Detallemetodo();
				nuevoDetalleMetodoAux = new Detallemetodo();
				nuevoDetalleMetodo.setCantidadDmt(0);
				nuevoDetalleMetodoAux.setCantidadDmt(0);

				existencias = detalleMetodoI.listaExistencias(su.UNIDAD_USUARIO_LOGEADO);
				tempExistencias = new ArrayList<Existencia>();
				selectExistencia = new Existencia();
			}

		} catch (Exception e) {

			mensajeError("Ha ocurrido un error");
			e.printStackTrace();
		}
	}

	public void getIdMetodo() {
		try {

			UnidadLabo uni = new UnidadLabo();
			uni = (UnidadLabo) unidadI.getById(UnidadLabo.class, su.UNIDAD_USUARIO_LOGEADO);

			/** Creacion del ID **/
			String codigoAux = metodoI.maxIdMetodo(su.UNIDAD_USUARIO_LOGEADO);

			if (codigoAux == null) {
				codigoAux = (uni.getCodigoU() + "-M0000");
			}

			String[] partsId = codigoAux.split("-M", 2);

			partsId = partsId[1].split("-");

			String codigoCortado = partsId[0];

			Integer codigo = Integer.parseInt(codigoCortado);
			codigo = codigo + 1;

			Long id = su.id_usuario_log;

			String codigoMetodo = codigo.toString();
			nuevoMetodo.setAuxIdmetodo(id.intValue());

			switch (codigoMetodo.length()) {

			case 1:
				nuevoMetodo.setIdMetodo(uni.getCodigoU() + "-M" + "000" + codigoMetodo);

				break;
			case 2:
				nuevoMetodo.setIdMetodo(uni.getCodigoU() + "-M" + "00" + codigoMetodo);
				break;
			case 3:
				nuevoMetodo.setIdMetodo(uni.getCodigoU() + "-M" + "0" + codigoMetodo);
				break;
			case 4:
				nuevoMetodo.setIdMetodo(uni.getCodigoU() + "-M" + codigoMetodo);

				break;

			default:
				break;
			}

		} catch (Exception e) {
			mensajeError("Ha ocurrido un error");
			e.printStackTrace();
		}
	}

	/** Actualizar Existencias en la base de datos **/
	public void actualizarExistencias() {

		for (Existencia existencia : tempExistencias) {
			try {
				existenciasI.update(existencia);
			} catch (Exception e) {

			}

		}
	}

	/****** Cambiar Id de Movimientos Inventarios ****/
	public void obtenerIdDetalle(Metodo metodo) {
		int i = 0;
		for (Detallemetodo dm : nuevoDetalleMetodos) {

			dm.setMetodo(metodo);

			dm.setCantidadDmt(0);

			nuevoDetalleMetodos.set(i, dm);

			i++;
		}
	}

	/** Guarda MI en la base de datos **/
	public void guardarDetalleM() {

		for (Detallemetodo dm : nuevoDetalleMetodos) {
			try {
				detalleMetodoI.save(dm);
			} catch (Exception e) {

			}

		}
	}

	/** Eliminar Método **/

	public void eliminarMetodo() {

		try {

			obtenerIdDetalleMetodo(metodo.getIdMetodo());

			eliminarDetalleMetodo(detalleMetodos);
			metodoI.delete(getMetodo());
			metodos.remove(metodo);
			mensajeInfo("El Método (" + metodo.getNombreMt() + ") se ha eliminado correctamente.");

			tblMetodos();

		} catch (Exception e) {

			if (e.getMessage() == "Transaction rolled back") {

				mensajeError("La tabla Método (" + metodo.getNombreMt() + ") tiene relación con otra tabla.");

			} else {

				mensajeError("Ha ocurrido un problema.");
			}

		}

	}

	public void obtenerIdDetalleMetodo(String id) {

		detalleMetodos.clear();
		detalleMetodos = detalleMetodoI.listaDetallesById(id);

	}

	public void eliminarDetalleMetodo(List<Detallemetodo> dm) {

		if (dm.size() == 0) {

		} else {
			for (Detallemetodo dms : dm) {
				try {
					detalleMetodoI.delete(dms);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}

	/** Editar Método **/

	public void editMetodo() {

		RequestContext context = RequestContext.getCurrentInstance();

		try {
			if (metodo.getNombreMt().equals(getNombreM())) {

				metodoI.update(metodo);
				tblMetodos();
				mensajeInfo("El Método (" + metodo.getNombreMt() + ") se actualizado exitosamente");

				context.execute("PF('editMetodo').hide();");

			} else if (buscarMetodos(metodo.getNombreMt()) == false) {

				metodoI.update(metodo);
				tblMetodos();
				mensajeInfo("El Laboratorio (" + metodo.getNombreMt() + ") se actualizado exitosamente");

				context.execute("PF('editMetodo').hide();");

			} else {
				tblMetodos();
				mensajeError("El Método (" + metodo.getNombreMt() + ") ya existe");

			}

		} catch (Exception e) {

			mensajeError("Ha ocurrido un problema");

		}
	}

	private boolean buscarMetodos(String valor) {

		try {
			tblMetodos();
		} catch (Exception e) {

			e.printStackTrace();
		}

		boolean resultado = false;
		for (Metodo m : metodos) {
			if (m.getNombreMt().equals(valor)) {
				resultado = true;
				break;
			} else {
				resultado = false;
			}
		}

		return resultado;
	}

	public void pasarNombre(String nombre) {
		setNombreM(nombre);

	}

	/****** Set y Get ****/

	public Metodo getMetodo() {
		return metodo;
	}

	public void setMetodo(Metodo metodo) {
		this.metodo = metodo;
	}

	public List<Metodo> getMetodos() {
		return metodos;
	}

	public void setMetodos(List<Metodo> metodos) {
		this.metodos = metodos;
	}

	public Metodo getNuevoMetodo() {
		return nuevoMetodo;
	}

	public void setNuevoMetodo(Metodo nuevoMetodo) {
		this.nuevoMetodo = nuevoMetodo;
	}

	public List<Metodo> getFilterMetodos() {
		return filterMetodos;
	}

	public void setFilterMetodos(List<Metodo> filterMetodos) {
		this.filterMetodos = filterMetodos;
	}

	public List<Metodo> getNuevoMetodos() {
		return nuevoMetodos;
	}

	public void setNuevoMetodos(List<Metodo> nuevoMetodos) {
		this.nuevoMetodos = nuevoMetodos;
	}

	public List<Servicio> getServicios() {
		return servicios;
	}

	public void setServicios(List<Servicio> servicios) {
		this.servicios = servicios;
	}

	public Servicio getServicioSelect() {
		return servicioSelect;
	}

	public void setServicioSelect(Servicio servicioSelect) {
		this.servicioSelect = servicioSelect;
	}

	public Existencia getExistencia() {
		return existencia;
	}

	public void setExistencia(Existencia existencia) {
		this.existencia = existencia;
	}

	public List<Existencia> getExistencias() {
		return existencias;
	}

	public void setExistencias(List<Existencia> existencias) {
		this.existencias = existencias;
	}

	public List<Existencia> getFilterExistencia() {
		return filterExistencia;
	}

	public void setFilterExistencia(List<Existencia> filterExistencia) {
		this.filterExistencia = filterExistencia;
	}

	public Existencia getSelectExistencia() {
		return selectExistencia;
	}

	public void setSelectExistencia(Existencia selectExistencia) {
		this.selectExistencia = selectExistencia;
	}

	public Detallemetodo getNuevoDetalleMetodoAux() {
		return nuevoDetalleMetodoAux;
	}

	public void setNuevoDetalleMetodoAux(Detallemetodo nuevoDetalleMetodoAux) {
		this.nuevoDetalleMetodoAux = nuevoDetalleMetodoAux;
	}

	public Detallemetodo getDetalleMetodo() {
		return detalleMetodo;
	}

	public void setDetalleMetodo(Detallemetodo detalleMetodo) {
		this.detalleMetodo = detalleMetodo;
	}

	public List<Detallemetodo> getNuevoDetalleMetodos() {
		return nuevoDetalleMetodos;
	}

	public void setNuevoDetalleMetodos(List<Detallemetodo> nuevoDetalleMetodos) {
		this.nuevoDetalleMetodos = nuevoDetalleMetodos;
	}

	public List<Detallemetodo> getTempDetalleMetodos() {
		return tempDetalleMetodos;
	}

	public void setTempDetalleMetodos(List<Detallemetodo> tempDetalleMetodos) {
		this.tempDetalleMetodos = tempDetalleMetodos;
	}

	public List<Detallemetodo> getDetalleMetodos() {
		return detalleMetodos;
	}

	public void setDetalleMetodos(List<Detallemetodo> detalleMetodos) {
		this.detalleMetodos = detalleMetodos;
	}

	public Detallemetodo getNuevoDetalleMetodo() {
		return nuevoDetalleMetodo;
	}

	public void setNuevoDetalleMetodo(Detallemetodo nuevoDetalleMetodo) {
		this.nuevoDetalleMetodo = nuevoDetalleMetodo;
	}

	public List<Existencia> getTempExistencias() {
		return tempExistencias;
	}

	public void setTempExistencias(List<Existencia> tempExistencias) {
		this.tempExistencias = tempExistencias;
	}

	public List<Detallemetodo> getTempDetalleMetodo() {
		return tempDetalleMetodo;
	}

	public void setTempDetalleMetodo(List<Detallemetodo> tempDetalleMetodo) {
		this.tempDetalleMetodo = tempDetalleMetodo;
	}

	public LaboratorioLab getLaboratorio() {
		return laboratorio;
	}

	public void setLaboratorio(LaboratorioLab laboratorio) {
		this.laboratorio = laboratorio;
	}

	public String getNombreM() {
		return nombreM;
	}

	public void setNombreM(String nombreM) {
		this.nombreM = nombreM;
	}

	public List<Detallemetodo> getTempDetalleMetodosEdit() {
		return tempDetalleMetodosEdit;
	}

	public void setTempDetalleMetodosEdit(List<Detallemetodo> tempDetalleMetodosEdit) {
		this.tempDetalleMetodosEdit = tempDetalleMetodosEdit;
	}
}
