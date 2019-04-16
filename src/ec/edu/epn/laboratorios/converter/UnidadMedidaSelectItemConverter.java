package ec.edu.epn.laboratorios.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.FacesConverter;

import org.omnifaces.converter.SelectItemsConverter;
import ec.edu.epn.laboratorioBJ.entities.Unidadmedida;


@FacesConverter(forClass=Unidadmedida.class)
public class UnidadMedidaSelectItemConverter extends SelectItemsConverter{
	
	@Override
	public String getAsString(FacesContext context, UIComponent component,
			Object value) {
		Integer id = (value instanceof Unidadmedida) ? ((Unidadmedida) value).getIdUmedida()
				: null;
		return (id != null) ? String.valueOf(id) : null;

	}

}