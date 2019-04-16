package ec.edu.epn.laboratorios.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.FacesConverter;

import org.omnifaces.converter.SelectItemsConverter;

import ec.edu.epn.laboratorioBJ.entities.Presentacion;
import ec.edu.epn.laboratorioBJ.entities.Tipoproducto;


@FacesConverter(forClass=Presentacion.class)
public class PresentacionSelectItemConverter extends SelectItemsConverter{
	
	@Override
	public String getAsString(FacesContext context, UIComponent component,
			Object value) {
		Integer id = (value instanceof Presentacion) ? ((Presentacion) value).getIdPresentacion()
				: null;
		return (id != null) ? String.valueOf(id) : null;

	}

}