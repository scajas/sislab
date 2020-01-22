package ec.edu.epn.laboratorios.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.FacesConverter;

import org.omnifaces.converter.SelectItemsConverter;

import ec.edu.epn.laboratorioBJ.entities.Servicio;

@FacesConverter(forClass = Servicio.class)
public class ServicioSelectItemConverter extends SelectItemsConverter {

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		String id = (value instanceof Servicio) ? ((Servicio) value).getIdServicio() : null;
		return (id != null) ? String.valueOf(id) : null;

	}

}