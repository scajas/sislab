package ec.edu.epn.laboratorios.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.FacesConverter;

import org.omnifaces.converter.SelectItemsConverter;
import ec.edu.epn.laboratorioBJ.entities.Caracteristica;


@FacesConverter(forClass=Caracteristica.class)
public class CaracteristicaSelectItemConverter extends SelectItemsConverter{
	
	@Override
	public String getAsString(FacesContext context, UIComponent component,
			Object value) {
		Integer id = (value instanceof Caracteristica) ? ((Caracteristica) value).getIdCaracteristica()
				: null;
		return (id != null) ? String.valueOf(id) : null;

	}

}