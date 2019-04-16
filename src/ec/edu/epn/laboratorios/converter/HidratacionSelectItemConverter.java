package ec.edu.epn.laboratorios.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.FacesConverter;

import org.omnifaces.converter.SelectItemsConverter;
import ec.edu.epn.laboratorioBJ.entities.Hidratacion;


@FacesConverter(forClass=Hidratacion.class)
public class HidratacionSelectItemConverter extends SelectItemsConverter{
	
	@Override
	public String getAsString(FacesContext context, UIComponent component,
			Object value) {
		Integer id = (value instanceof Hidratacion) ? ((Hidratacion) value).getIdHidratacion()
				: null;
		return (id != null) ? String.valueOf(id) : null;

	}

}