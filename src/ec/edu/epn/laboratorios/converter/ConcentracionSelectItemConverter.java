package ec.edu.epn.laboratorios.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.FacesConverter;

import org.omnifaces.converter.SelectItemsConverter;
import ec.edu.epn.laboratorioBJ.entities.Concentracion;


@FacesConverter(forClass=Concentracion.class)
public class ConcentracionSelectItemConverter extends SelectItemsConverter{
	
	@Override
	public String getAsString(FacesContext context, UIComponent component,
			Object value) {
		Integer id = (value instanceof Concentracion) ? ((Concentracion) value).getIdConcentracion()
				: null;
		return (id != null) ? String.valueOf(id) : null;

	}

}