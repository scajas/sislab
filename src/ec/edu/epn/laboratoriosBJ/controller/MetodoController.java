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
import ec.edu.epn.laboratorios.utilidades.Utilidades;
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
	private Existencia selectExDMTemp;
	private List<Existencia> tempExistencias = new ArrayList<>();

	private List<Detallemetodo> detalleMetodos = new ArrayList<>();
	private List<Detallemetodo> tempDetalleMetodos = new ArrayList<>();
	private List<Detallemetodo> tempDetalleMetodosEdit = new ArrayList<>();
	private List<Detallemetodo> dmAddEdit = new ArrayList<Detallemetodo>();
	private List<Detallemetodo> dmDelete = new ArrayList<Detallemetodo>();

	private Detallemetodo tempDetalleMetodoEdit;

	private List<Detallemetodo> nuevoDetalleMetodos = new ArrayList<>();
	private Detallemetodo nuevoDetalleMetodo;
	private Detallemetodo nuevoDMTempEdit;
	private Detallemetodo nuevoDetalleMetodoAux;
	private Detallemetodo detalleMetodo;

	private LaboratorioLab laboratorio;
	
	UnidadLabo unidadLabo;
	private String nombreM;
	private Utilidades utilidades;

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
			selectExDMTemp = new Existencia();

			// DetalleMetodo
			nuevoDetalleMetodos.clear();
			dmDelete.clear();
			tempDetalleMetodos.clear();
			tempDetalleMetodos = new ArrayList<>();

			dmAddEdit.clear();
			tempDetalleMetodosEdit.clear();
			tempDetalleMetodosEdit = new ArrayList<>();
			tempDetalleMetodoEdit = new Detallemetodo();

			nuevoDetalleMetodo = new Detallemetodo();
			nuevoDMTempEdit = new Detallemetodo();
			detalleMetodo = new Detallemetodo();

			tempExistencias = new ArrayList<Existencia>();
			
			utilidades = new Utilidades();

		} catch (Exception e) {
	
		}
	}

	/****** Tabla Metodos ****/

	public void tblMetodos() {
		try {
			UnidadLabo uni = new UnidadLabo();
			uni = (UnidadLabo) unidadI.getById(UnidadLabo.class, su.UNIDAD_USUARIO_LOGEADO);
			metodos = metodoI.getListMetodos(uni.getCodigoU());
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	/****** Limpiar Campos ****/

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
			utilidades.mensajeInfo("Se ha limpiado todo el formulario");

		} catch (Exception e) {

		}

	}

	/*
	 * private boolean buscarMetodos(String valor) {
	 * 
	 * try { tblMetodos(); } catch (Exception e) {
	 * 
	 * 
	 * boolean resultado = false; for (Metodo m : metodos) { if
	 * (m.getNombreMt().equals(valor)) { resultado = true; break; } else {
	 * resultado = false; } }
	 * 
	 * return resultado; }
	 */

	public void pasarNombre(String nombre) {
		setNombreM(nombre);

	}

	/*************************** NUEVO METODO ***************************/
	/****** Nuevo - Seleccionar Existencia ****/

	public void seleccionarExistencia() {

		try {

			if (buscarExistencia(selectExistencia.getIdExistencia()) == false) {

				RequestContext context = RequestContext.getCurrentInstance();
				getNuevoDetalleMetodo().setIdExistencia(getSelectExistencia().getIdExistencia());

				utilidades.mensajeInfo("Se seleccionó la Existencia (" + selectExistencia.getIdExistencia());
				existencia = selectExistencia;
				selectExistencia = new Existencia();
				filterExistencia = new ArrayList<Existencia>();
				context.execute("PF('listadoEx').hide();");

			} else {
				utilidades.mensajeError("La existencia (" + selectExistencia.getIdExistencia() + ") ya ha sido seleccionada");
				selectExistencia = new Existencia();
			}

		} catch (Exception e) {
			utilidades.mensajeError("No se ha seleccionado ninguna Existencia");

		}

	}

	/****** Nuevo - Listado Existencias ****/

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

	/****** Nuevo - Agregar Detalle Temp ****/

	public void agregarDetalleTemp() {

		RequestContext context = RequestContext.getCurrentInstance();

		try {

			if (nuevoDetalleMetodo.getIdExistencia() == null) {
				utilidades.mensajeError("No ha seleccionado Ninguna Existencia");
			} else {

				float cantidadMo = nuevoDetalleMetodo.getCantidadDmt();
				nuevoDetalleMetodo.setCantidadDmt(cantidadMo);

				String unidad = existencia.getUnidadmedida().getSiglaUm();

				nuevoDetalleMetodo.setIdUmedida(unidad);

				tempExistencias.add(getExistencia());
				nuevoDetalleMetodos.add(nuevoDetalleMetodo);
				setDetalleMetodos(nuevoDetalleMetodos);

				utilidades.mensajeInfo("Se ha almacenado (" + nuevoDetalleMetodo.getIdExistencia() + ") correctamente.");

				nuevoDetalleMetodo = new Detallemetodo();
				existencia = new Existencia();
				existencias = detalleMetodoI.listaExistencias(su.UNIDAD_USUARIO_LOGEADO);
				tempExistencias = new ArrayList<Existencia>();
				context.execute("PF('nuevaExistencia').hide();");
			}

		} catch (Exception e) {

		}

	}

	/****** Nuevo - Editar Temporal ****/

	public void editarTemp() {

		RequestContext context = RequestContext.getCurrentInstance();
		for (Detallemetodo dm : nuevoDetalleMetodos) {
			if (dm.getIdExistencia().equals(detalleMetodo.getIdExistencia())) {

				utilidades.mensajeInfo("Se ha editado el Detalle Método(" + detalleMetodo.getIdExistencia() + ")");
				context.execute("PF('editarDetalleTemp').hide();");

				break;
			} else {

			}

		}

	}

	/****** Nuevo- Eliminar Temporal ****/

	public void eliminarTemp() {

		try {
			nuevoDetalleMetodos.remove(detalleMetodo);
			setDetalleMetodos(nuevoDetalleMetodos);
			utilidades.mensajeInfo("Se ha eliminado el Detalle Método (" + detalleMetodo.getIdExistencia() + ")");
		} catch (Exception e) {
	
			utilidades.mensajeError("Ha ocurrido un error interno.");
		}

	}

	/****** Nuevo - Cargar Detalle Metodo ****/

	public void cargarDetalleMet(String id) {

		tempDetalleMetodos = detalleMetodoI.listaDetallesById(id);

	}

	/****** Nuevo - Guardar Metodo ******/

	public void guardarMetodo() {

		RequestContext context = RequestContext.getCurrentInstance();

		try {

			if (nuevoMetodo.getNombreMt() == "") {

				utilidades.mensajeError("Ingrese el Método");

			} else if (servicioSelect == null) {

				utilidades.mensajeError("Seleccione el Servicio");

			} else if (nuevoDetalleMetodos.size() == 0) {

				utilidades.mensajeError("Debe ingresar Detalle Metodo");

			} else {

				/** ID Metodo **/
				getIdMetodo();
				nuevoMetodo.setServicio(servicioSelect);
				metodoI.save(nuevoMetodo);

				/** ID Detalle Metodo **/
				obtenerIdDetalle(nuevoMetodo);
				guardarDetalleM();

				/** ID ORD INV - MOV INV **/

				utilidades.mensajeInfo("El Método (" + nuevoMetodo.getIdMetodo() + ") se ha almacenado exitosamente");
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

			utilidades.mensajeError("Ha ocurrido un error");
			
		}
	}

	/****** Nuevo - Construccion ID Metodo ******/

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
			utilidades.mensajeError("Ha ocurrido un error");
		
		}
	}

	/****** Nuevo - Actualizar Existencias BDD ******/
	public void actualizarExistencias() {

		for (Existencia existencia : tempExistencias) {
			try {
				existenciasI.update(existencia);
			} catch (Exception e) {

			}

		}
	}

	/****** Nuevo - Obtener Detalle Metodo ******/
	public void obtenerIdDetalle(Metodo metodo) {
		int i = 0;
		for (Detallemetodo dm : nuevoDetalleMetodos) {

			dm.setMetodo(metodo);

			nuevoDetalleMetodos.set(i, dm);

			i++;
		}
	}

	/****** Nuevo - Guardar Detalle Metodo ******/
	public void guardarDetalleM() {

		for (Detallemetodo dm : nuevoDetalleMetodos) {
			try {
				detalleMetodoI.save(dm);
			} catch (Exception e) {

			}

		}
	}

	/*************************** EDITAR METODO ***************************/
	/****** Editar - Seleccionar Existencia ******/
	public void seleccionarExistenciaEdit() {

		try {

			if (buscarExistenciaEdit(selectExDMTemp.getIdExistencia()) == false) {

				RequestContext context = RequestContext.getCurrentInstance();

				getTempDetalleMetodoEdit().setIdExistencia(getSelectExDMTemp().getIdExistencia());

				utilidades.mensajeInfo("Se seleccionó la Existencia (" + selectExDMTemp.getIdExistencia());
				existencia = selectExDMTemp;
				selectExDMTemp = new Existencia();
				filterExistencia = new ArrayList<Existencia>();
				context.execute("PF('listadoExTemp').hide();");

			} else {
				utilidades.mensajeError("La existencia (" + selectExDMTemp.getIdExistencia() + ") ya ha sido seleccionada");
				selectExDMTemp = new Existencia();
			}

		} catch (Exception e) {
			utilidades.mensajeError("No se ha seleccionado ninguna Existencia");
	
		}

	}

	/****** Editar - Listado de Existencias ******/

	public boolean buscarExistenciaEdit(String id) {

		boolean resultado = false;

		for (Detallemetodo detalleMetodos : tempDetalleMetodosEdit) {

			if (detalleMetodos.getIdExistencia().equals(id)) {
				resultado = true;
				break;
			} else {
				resultado = false;
			}

		}

		return resultado;
	}

	/****** Editar - Agregar Detalle Temp ******/

	public void agregarDetalleTempEdit() {

		RequestContext context = RequestContext.getCurrentInstance();

		try {

			if (tempDetalleMetodoEdit.getIdExistencia() == null) {
				utilidades.mensajeError("No ha seleccionado Ninguna Existencia");
			} else {

				float cantidadMo = tempDetalleMetodoEdit.getCantidadDmt();
				tempDetalleMetodoEdit.setCantidadDmt(cantidadMo);

				String unidad = existencia.getUnidadmedida().getSiglaUm();
				tempDetalleMetodoEdit.setIdUmedida(unidad);

				tempExistencias.add(getExistencia());
				tempDetalleMetodosEdit.add(tempDetalleMetodoEdit);
				setTempDetalleMetodosEdit(tempDetalleMetodosEdit);
				utilidades.mensajeInfo("Se ha almacenado (" + tempDetalleMetodoEdit.getIdExistencia() + ") correctamente.");

				tempDetalleMetodoEdit = new Detallemetodo();
				existencia = new Existencia();
				existencias = detalleMetodoI.listaExistencias(su.UNIDAD_USUARIO_LOGEADO);
				tempExistencias = new ArrayList<Existencia>();
				context.execute("PF('nuevaExTemp').hide();");
			}

		} catch (Exception e) {
		
		}

	}

	/****** Editar - Editar Detalle M. Temporal ******/

	public void editDMTemp() {

		RequestContext context = RequestContext.getCurrentInstance();
		for (Detallemetodo dm : tempDetalleMetodosEdit) {
			if (dm.getIdExistencia().equals(tempDetalleMetodoEdit.getIdExistencia())) {

				utilidades.mensajeInfo("Se ha editado el Detalle Método (" + tempDetalleMetodoEdit.getIdExistencia() + ")");
				context.execute("PF('editDMTemp').hide();");

				break;
			} else {

			}

		}

	}

	/****** Nuevo- Eliminar Temporal ****/

	public void eliminarDMTemp() {

		try {
			tempDetalleMetodosEdit.remove(tempDetalleMetodoEdit);
			setListasTempDelete();
			utilidades.mensajeInfo("Se ha eliminado el Detalle Método (" + tempDetalleMetodoEdit.getIdExistencia() + ")");
		} catch (Exception e) {

			utilidades.mensajeError("Ha ocurrido un error interno.");
		}

	}

	/****** Editar - Editar Método ******/

	public void editMetodo() {

		RequestContext context = RequestContext.getCurrentInstance();

		cambiarIDDetalleM(metodo);
		cambiarIDDetalleProAdd(metodo);

		// Eliminar A la base de datos
		eliminarDetallesM(dmDelete);

		// Agregar A la base de datos
		guardarDetalleM(dmAddEdit);

		// Modificar A la base de datos
		actualizarDetalleM(tempDetalleMetodosEdit);
		actualizarMetodo(metodo);

		utilidades.mensajeInfo("El Método (" + metodo.getNombreMt() + ") se actualizado exitosamente");
		tblMetodos();
		context.execute("PF('editMetodo').hide();");

	}

	/****** Editar - Guardar Detalle M ******/

	public void guardarDetalleM(List<Detallemetodo> dm) {

		if (dm.size() == 0) {
		} else {
			for (Detallemetodo detalleM : dm) {
				try {
					detalleMetodoI.save(detalleM);
				} catch (Exception e) {
				
				}

			}
		}

	}

	/****** Editar - Cargar Detalle Metodo ******/

	public void cargarDetalleMetEdit(String id) {

		tempDetalleMetodosEdit = detalleMetodoI.listaDetallesById(id);

	}

	/****** Editar - Cargar Detalle M ******/

	public void cambiarIDDetalleM(Metodo m) {

		int i = 0;
		for (Detallemetodo dmE : tempDetalleMetodosEdit) {

			dmE.setMetodo(m);
			tempDetalleMetodosEdit.set(i, dmE);

			i++;
		}
	}

	/****** Editar - Cargar Detalle M ******/

	public void cambiarIDDetalleProAdd(Metodo m) {
	
		int i = 0;
		if (dmAddEdit.size() == 0) {
		
		} else {
			for (Detallemetodo detalleME : dmAddEdit) {

				detalleME.setMetodo(m);
				dmAddEdit.set(i, detalleME);

				i++;
			}
		}

	}

	/****** Editar - Editar Detalle M ******/

	public void actualizarDetalleM(List<Detallemetodo> dm) {
		
		for (Detallemetodo detalleM : dm) {
			try {
				detalleMetodoI.update(detalleM);
			} catch (Exception e) {

			}

		}
	}

	/****** Editar - Editar Metodo ******/

	public void actualizarMetodo(Metodo m) {
		try {
			metodoI.update(m);
		} catch (Exception e) {
		
		}
	}

	/****** Editar - Eliminar Detalle M Temp ******/

	public void eliminarDetallesM(List<Detallemetodo> dm) {
	
		if (dm.size() == 0) {
		
		} else {
			for (Detallemetodo d : dm) {
				try {
					detalleMetodoI.delete(d);
				} catch (Exception e) {
				
				}
			}
		}

	}

	/****** Editar - Listas de Eliminar ******/

	public void setListasTempDelete() {

		if (buscarDetalleMAdd(tempDetalleMetodoEdit)) {
			dmAddEdit.remove(tempDetalleMetodoEdit);
	
		} else {
			dmDelete.add(tempDetalleMetodoEdit);
		
		}

	}

	public void eliminarDetalleM() {
		try {

			// Seteo de listas temporales
			setListasTempDelete();

			detalleMetodos.remove(tempDetalleMetodoEdit);

			utilidades.mensajeInfo("Se ha eliminado el detalle Metodo (" + tempDetalleMetodoEdit.getIdExistencia() + ")");

		} catch (Exception e) {
	
			utilidades.mensajeError("Ha ocurrido un error interno.");
		}
	}

	public boolean buscarDetalleMAdd(Detallemetodo dM) {
		// Realiza una busqueda en la lista temporal de Detalle Proforma
		boolean resultado = false;

		for (Detallemetodo dp : tempDetalleMetodosEdit) {

			if (dp.equals(dM)) {
			
				resultado = true;
				break;
			} else {
				resultado = false;
			}
		}

		return resultado;
	}

	/****** Busqueda de Existencias ******/
	public Existencia cambiarDatosExistencia(String id) {
		Existencia existenciatemp = detalleMetodoI.buscarExistencias(id);
		return existenciatemp;
	}

	/*************************** ELIMINAR METODO ***************************/
	/****** Eliminar Método ******/

	public void eliminarMetodo() {

		try {

			obtenerIdDetalleMetodo(metodo.getIdMetodo());

			eliminarDetalleMetodo(detalleMetodos);
			metodoI.delete(getMetodo());
			metodos.remove(metodo);
			utilidades.mensajeInfo("El Método (" + metodo.getNombreMt() + ") se ha eliminado correctamente.");

			tblMetodos();

		} catch (Exception e) {

			if (e.getMessage() == "Transaction rolled back") {

				utilidades.mensajeError("La tabla Método (" + metodo.getNombreMt() + ") tiene relación con otra tabla.");

			} else {

				utilidades.mensajeError("Ha ocurrido un problema.");
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
				
				}
			}
		}

	}

	/*************************** SETT Y GETT ***************************/

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

	public Detallemetodo getTempDetalleMetodoEdit() {
		return tempDetalleMetodoEdit;
	}

	public void setTempDetalleMetodoEdit(Detallemetodo tempDetalleMetodoEdit) {
		this.tempDetalleMetodoEdit = tempDetalleMetodoEdit;
	}

	public Detallemetodo getNuevoDMTempEdit() {
		return nuevoDMTempEdit;
	}

	public void setNuevoDMTempEdit(Detallemetodo nuevoDMTempEdit) {
		this.nuevoDMTempEdit = nuevoDMTempEdit;
	}

	public Existencia getSelectExDMTemp() {
		return selectExDMTemp;
	}

	public void setSelectExDMTemp(Existencia selectExDMTemp) {
		this.selectExDMTemp = selectExDMTemp;
	}

	public List<Detallemetodo> getDmAddEdit() {
		return dmAddEdit;
	}

	public void setDmAddEdit(List<Detallemetodo> dmAddEdit) {
		this.dmAddEdit = dmAddEdit;
	}

	public List<Detallemetodo> getDmDelete() {
		return dmDelete;
	}

	public void setDmDelete(List<Detallemetodo> dmDelete) {
		this.dmDelete = dmDelete;
	}
}
