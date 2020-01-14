package ec.edu.epn.laboratorios.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.FacesConverter;

import org.omnifaces.converter.SelectItemsConverter;

import ec.edu.epn.laboratorioBJ.entities.Presentacion;
import ec.edu.epn.laboratorioBJ.entities.ProveedorLab;
import ec.edu.epn.laboratorioBJ.entities.Tipoproducto;


@FacesConverter(forClass=ProveedorLab.class)
public class ProveedorSelectItemConverter extends SelectItemsConverter{
	
	@Override
	public String getAsString(FacesContext context, UIComponent component,
			Object value) {
		Integer id = (value instanceof ProveedorLab) ? ((ProveedorLab) value).getIdProveedor()
				: null;
		return (id != null) ? String.valueOf(id) : null;

	}

}