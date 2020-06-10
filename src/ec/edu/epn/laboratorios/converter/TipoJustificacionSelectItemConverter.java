package ec.edu.epn.laboratorios.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.FacesConverter;

import org.omnifaces.converter.SelectItemsConverter;
import ec.edu.epn.laboratorioBJ.entities.TipoJustificacion;


@FacesConverter(forClass=TipoJustificacion.class)
public class TipoJustificacionSelectItemConverter extends SelectItemsConverter{
	
	@Override
	public String getAsString(FacesContext context, UIComponent component,
			Object value) {
		String id = (value instanceof TipoJustificacion) ? ((TipoJustificacion) value).getIdTipojust()
				: null;
		return (id != null) ? String.valueOf(id) : null;

	}

}