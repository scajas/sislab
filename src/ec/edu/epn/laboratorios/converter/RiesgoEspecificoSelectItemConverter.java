package ec.edu.epn.laboratorios.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.FacesConverter;

import org.omnifaces.converter.SelectItemsConverter;
import ec.edu.epn.laboratorioBJ.entities.Riesgoespecifico;


@FacesConverter(forClass=Riesgoespecifico.class)
public class RiesgoEspecificoSelectItemConverter extends SelectItemsConverter{
	
	@Override
	public String getAsString(FacesContext context, UIComponent component,
			Object value) {
		String id = (value instanceof Riesgoespecifico) ? ((Riesgoespecifico) value).getIdRiesgoespecifico()
				: null;
		return (id != null) ? String.valueOf(id) : null;

	}

}