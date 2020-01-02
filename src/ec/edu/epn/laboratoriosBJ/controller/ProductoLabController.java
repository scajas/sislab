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

import ec.edu.epn.laboratorioBJ.beans.ProductoLabDAO;
import ec.edu.epn.laboratorioBJ.beans.RiesgoEspecificoDAO;
import ec.edu.epn.laboratorioBJ.beans.TipoProductoDAO;
import ec.edu.epn.laboratorioBJ.entities.ProductoLab;
import ec.edu.epn.laboratorioBJ.entities.Riesgoespecifico;
import ec.edu.epn.laboratorioBJ.entities.Servicio;
import ec.edu.epn.laboratorioBJ.entities.Tipoproducto;
import ec.edu.epn.seguridad.VO.SesionUsuario;

import javax.faces.application.FacesMessage;

@ManagedBean(name = "productoController")
@SessionScoped
public class ProductoLabController implements Serializable {

	/** VARIABLES DE SESION ***/
	private static final long serialVersionUID = 1L;
	FacesContext fc = FacesContext.getCurrentInstance();
	HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
	HttpSession session = request.getSession();
	SesionUsuario su = (SesionUsuario) session.getAttribute("sesionUsuario");

	/****************************************************************************/
	/** SERVICIOS **/
	@EJB(lookup = "java:global/ServiciosSeguridadEPN/ProductoLabDAOImplement!ec.edu.epn.laboratorioBJ.beans.ProductoLabDAO")
	private ProductoLabDAO productoI;

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/RiesgoEspecificoDAOImplement!ec.edu.epn.laboratorioBJ.beans.RiesgoEspecificoDAO")
	private RiesgoEspecificoDAO riesgoEspI;

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/TipoProductoDAOImplement!ec.edu.epn.laboratorioBJ.beans.TipoProductoDAO")
	private TipoProductoDAO tipoProI;

	/****************************************************************************/
	/** VARIABLES **/

	private String nombrePro;
	private List<ProductoLab> listarProductos = new ArrayList<>();
	private ProductoLab nuevoProducto;
	private ProductoLab productoLab;
	private List<Servicio> filtroProducto;

	// select de riesgo
	private Riesgoespecifico riesgoEspecificoSelect;
	private List<Riesgoespecifico> listarRiesgosEsp = new ArrayList<Riesgoespecifico>();
	private Riesgoespecifico riesgoEsp;

	// select de tipo producto
	private Tipoproducto tipoProductoSelect;
	private List<Tipoproducto> listarTipoProducto = new ArrayList<Tipoproducto>();
	private Tipoproducto tipoProducto;

	private String nombreTP;
	private String auxDes;
	private Integer riesgoS;
	private Integer riesgoI;
	private Integer riesgoR;

	@PostConstruct
	public void init() {
		try {

			// setListarProductos(productoI.getAll(ProductoLab.class));
			listarProductos = productoI.getListPro();
			setListarRiesgosEsp(riesgoEspI.getAll(Riesgoespecifico.class));
			setListarTipoProducto(tipoProI.getAll(Tipoproducto.class));
			setNuevoProducto(new ProductoLab());
			productoLab = new ProductoLab();

			// riesgo

			listarRiesgosEsp = riesgoEspI.getAll(Riesgoespecifico.class);
			riesgoEsp = new Riesgoespecifico();

			// tipo producto
			listarTipoProducto = tipoProI.getAll(Tipoproducto.class);
			tipoProducto = new Tipoproducto();

		} catch (Exception e) {

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

	/****** Agregar Producto ****/

	public void agregarProducto() {
		RequestContext context = RequestContext.getCurrentInstance();

		try {
			if (buscarProducto(nuevoProducto.getNombrePr()) == true) {
				mensajeError("El Producto (" + nuevoProducto.getNombrePr() + ") ya existe.");

			} else {

				Integer idAuxliar = productoI.generarId("ProductoLab", "auxIdprod");
				String codigoAux = String.valueOf(idAuxliar);
				nuevoProducto.setAuxIdprod(idAuxliar);

				switch (codigoAux.length()) {
				case 1:
					nuevoProducto.setIdProducto("PR" + "-" + "0000" + codigoAux);
					break;
				case 2:
					nuevoProducto.setIdProducto("PR" + "-" + "000" + codigoAux);
					break;
				case 3:
					nuevoProducto.setIdProducto("PR" + "-" + "00" + codigoAux);
					break;
				case 4:
					nuevoProducto.setIdProducto("PR" + "-" + "0" + codigoAux);
					break;
				case 5:
					nuevoProducto.setIdProducto("PR" + "-" + codigoAux);
					break;

				default:
					break;
				}

				nuevoProducto.setRiesgoespecifico(riesgoEspecificoSelect);
				nuevoProducto.setTipoproducto(tipoProductoSelect);

				nuevoProducto.setRiesgoPr("S" + nuevoProducto.getRiesgosaludPr() + "I"
						+ nuevoProducto.getRiesgoinflamabilidadPr() + "R" + nuevoProducto.getRiesgoreactividadPr());

				nuevoProducto.setRiesgosaludPr("S" + nuevoProducto.getRiesgosaludPr());
				nuevoProducto.setRiesgoinflamabilidadPr("I" + nuevoProducto.getRiesgoinflamabilidadPr());
				nuevoProducto.setRiesgoreactividadPr("R" + nuevoProducto.getRiesgoreactividadPr());

				productoI.save(nuevoProducto);
				listarProductos = productoI.getListPro();

				mensajeInfo("El producto (" + nuevoProducto.getNombrePr() + ") se ha almacenado exitosamente.");

				nuevoProducto = new ProductoLab();
				riesgoEspecificoSelect = new Riesgoespecifico();
				tipoProductoSelect = new Tipoproducto();

				context.execute("PF('nuevoProducto').hide();");

			}

		} catch (Exception e) {
			mensajeError("Ha ocurrido un problema.");
		}

	}

	/****** Modificar Producto ****/

	public void modificarProducto() {
		RequestContext context = RequestContext.getCurrentInstance();
		try {
			if (productoLab.getNombrePr().equals(getNombreTP())) {

				productoLab.setRiesgoPr("S" + riesgoS.toString() + "I" + riesgoI.toString() + "R" + riesgoR.toString());

				productoLab.setRiesgosaludPr("S" + riesgoS.toString());
				productoLab.setRiesgoinflamabilidadPr("I" + riesgoI.toString());
				productoLab.setRiesgoreactividadPr("R" + riesgoR.toString());
				productoI.update(productoLab);
				listarProductos = productoI.getListPro();

				mensajeInfo("El Producto (" + productoLab.getNombrePr() + ") se ha actualizado exitosamente.");

				context.execute("PF('modificarProducto').hide();");

			} else if (buscarProducto(productoLab.getNombrePr()) == false) {

				productoLab.setRiesgoPr("S" + riesgoS.toString() + "I" + riesgoI.toString() + "R" + riesgoR.toString());

				productoLab.setRiesgosaludPr("S" + riesgoS.toString());
				productoLab.setRiesgoinflamabilidadPr("I" + riesgoI.toString());
				productoLab.setRiesgoreactividadPr("R" + riesgoR.toString());
				listarProductos = productoI.getListPro();

				productoI.update(productoLab);
				listarProductos = productoI.getListPro();

				mensajeInfo("El Producto (" + productoLab.getNombrePr() + ") se ha actualizado exitosamente.");

				context.execute("PF('modificarProducto').hide();");

			} else {
				listarProductos = productoI.getListPro();
				mensajeError("El Producto (" + productoLab.getNombrePr() + ") ya existe.");
			}

		} catch (Exception e) {
			mensajeError("Ha ocurrido un problema");
		}
	}

	/****** Eliminar Producto ****/

	public void eliminarProducto() {

		try {

			productoI.delete(productoLab);
			listarProductos = productoI.getListPro();
			mensajeInfo("El Producto (" + productoLab.getNombrePr() + ") se ha eliminado correctamente.");

		} catch (Exception e) {

			if (e.getMessage() == "Transaction rolled back") {
				mensajeError("La tabla Producto (" + productoLab.getNombrePr() + ") tiene relación con otra tabla.");
			} else {
				mensajeError("Ha ocurrido un problema.");
			}

		}

	}

	/****** Busqueda Global ****/

	public void busquedaGlobal() {

		try {
			setListarProductos(productoI.filtrarLista(getNombrePro()));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/****** Busqueda de Producto ****/

	private boolean buscarProducto(String valor) {

		try {
			listarProductos = productoI.getAll(ProductoLab.class);
		} catch (Exception e) {

			e.printStackTrace();
		}

		boolean resultado = false;
		for (ProductoLab tipo : listarProductos) {
			if (tipo.getNombrePr().equals(valor)) {
				resultado = true;
				break;
			} else {
				resultado = false;
			}
		}

		return resultado;
	}

	public void pasarNombre(String nombre, Riesgoespecifico riesgoE, Tipoproducto tipoPro) {
		setNombreTP(nombre);
		riesgoEsp = riesgoE;
		tipoProducto = tipoPro;
		String riesgo1 = productoLab.getRiesgosaludPr();
		String riesgo2 = productoLab.getRiesgoinflamabilidadPr();
		String riesgo3 = productoLab.getRiesgoreactividadPr();
		riesgoS = Integer.parseInt(riesgo1.substring(1, 2));
		riesgoI = Integer.parseInt(riesgo2.substring(1, 2));
		riesgoR = Integer.parseInt(riesgo3.substring(1, 2));

	}

	/****** Getter y Setter ****/

	public List<ProductoLab> getListarProductos() {
		return listarProductos;
	}

	public void setListarProductos(List<ProductoLab> listarProductos) {
		this.listarProductos = listarProductos;
	}

	public ProductoLab getNuevoProducto() {
		return nuevoProducto;
	}

	public void setNuevoProducto(ProductoLab nuevoProducto) {
		this.nuevoProducto = nuevoProducto;
	}

	public ProductoLab getProductoLab() {
		return productoLab;
	}

	public void setProductoLab(ProductoLab productoLab) {
		this.productoLab = productoLab;
	}

	public String getNombreTP() {
		return nombreTP;
	}

	public void setNombreTP(String nombreTP) {
		this.nombreTP = nombreTP;
	}

	public Riesgoespecifico getRiesgoEspecificoSelect() {
		return riesgoEspecificoSelect;
	}

	public void setRiesgoEspecificoSelect(Riesgoespecifico riesgoEspecificoSelect) {
		this.riesgoEspecificoSelect = riesgoEspecificoSelect;
	}

	public List<Riesgoespecifico> getListarRiesgosEsp() {
		return listarRiesgosEsp;
	}

	public void setListarRiesgosEsp(List<Riesgoespecifico> listarRiesgosEsp) {
		this.listarRiesgosEsp = listarRiesgosEsp;
	}

	public Riesgoespecifico getRiesgoEsp() {
		return riesgoEsp;
	}

	public void setRiesgoEsp(Riesgoespecifico riesgoEsp) {
		this.riesgoEsp = riesgoEsp;
	}

	public List<Tipoproducto> getListarTipoProducto() {
		return listarTipoProducto;
	}

	public void setListarTipoProducto(List<Tipoproducto> listarTipoProducto) {
		this.listarTipoProducto = listarTipoProducto;
	}

	public Tipoproducto getTipoProductoSelect() {
		return tipoProductoSelect;
	}

	public void setTipoProductoSelect(Tipoproducto tipoProductoSelect) {
		this.tipoProductoSelect = tipoProductoSelect;
	}

	public Tipoproducto getTipoProducto() {
		return tipoProducto;
	}

	public void setTipoProducto(Tipoproducto tipoProducto) {
		this.tipoProducto = tipoProducto;
	}

	public String getAuxDes() {
		return auxDes;
	}

	public void setAuxDes(String auxDes) {
		this.auxDes = auxDes;
	}

	public Integer getRiesgoS() {
		return riesgoS;
	}

	public void setRiesgoS(Integer riesgoS) {
		this.riesgoS = riesgoS;
	}

	public Integer getRiesgoI() {
		return riesgoI;
	}

	public void setRiesgoI(Integer riesgoI) {
		this.riesgoI = riesgoI;
	}

	public Integer getRiesgoR() {
		return riesgoR;
	}

	public void setRiesgoR(Integer riesgoR) {
		this.riesgoR = riesgoR;
	}

	public String getNombrePro() {
		return nombrePro;
	}

	public void setNombrePro(String nombrePro) {
		this.nombrePro = nombrePro;
	}

	public List<Servicio> getFiltroProducto() {
		return filtroProducto;
	}

	public void setFiltroProducto(List<Servicio> filtroProducto) {
		this.filtroProducto = filtroProducto;
	}

}
