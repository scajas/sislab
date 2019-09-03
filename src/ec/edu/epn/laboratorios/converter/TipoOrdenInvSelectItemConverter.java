package ec.edu.epn.laboratorios.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.FacesConverter;

import org.omnifaces.converter.SelectItemsConverter;

import ec.edu.epn.laboratorioBJ.entities.LaboratorioLab;
import ec.edu.epn.laboratorioBJ.entities.Tipordeninv;
import ec.edu.epn.laboratorioBJ.entities.Tiposervicio;


@FacesConverter(forClass=Tipordeninv.class)
public class TipoOrdenInvSelectItemConverter extends SelectItemsConverter{
	
	@Override
	public String getAsString(FacesContext context, UIComponent component,
			Object value) {
		Integer id = (value instanceof Tipordeninv) ? ((Tipordeninv) value).getIdTipordeninv()
				: null;
		return (id != null) ? String.valueOf(id) : null; //hola

	}

}
