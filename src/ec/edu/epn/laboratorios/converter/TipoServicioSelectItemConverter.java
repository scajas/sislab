package ec.edu.epn.laboratorios.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.FacesConverter;

import org.omnifaces.converter.SelectItemsConverter;

import ec.edu.epn.laboratorioBJ.entities.LaboratorioLab;
import ec.edu.epn.laboratorioBJ.entities.Tiposervicio;


@FacesConverter(forClass=Tiposervicio.class)
public class TipoServicioSelectItemConverter extends SelectItemsConverter{
	
	@Override
	public String getAsString(FacesContext context, UIComponent component,
			Object value) {
		Integer id = (value instanceof Tiposervicio) ? ((Tiposervicio) value).getIdTiposerv()
				: null;
		return (id != null) ? String.valueOf(id) : null;

	}

}
