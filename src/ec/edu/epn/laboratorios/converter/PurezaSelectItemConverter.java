package ec.edu.epn.laboratorios.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.FacesConverter;

import org.omnifaces.converter.SelectItemsConverter;
import ec.edu.epn.laboratorioBJ.entities.Pureza;


@FacesConverter(forClass=Pureza.class)
public class PurezaSelectItemConverter extends SelectItemsConverter{
	
	@Override
	public String getAsString(FacesContext context, UIComponent component,
			Object value) {
		String id = (value instanceof Pureza) ? ((Pureza) value).getIdPureza()
				: null;
		return (id != null) ? String.valueOf(id) : null;

	}

}