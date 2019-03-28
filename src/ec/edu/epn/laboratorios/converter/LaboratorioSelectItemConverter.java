package ec.edu.epn.laboratorios.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.FacesConverter;

import org.omnifaces.converter.SelectItemsConverter;

import ec.edu.epn.laboratorioBJ.entities.LaboratorioLab;


@FacesConverter(forClass=LaboratorioLab.class)
public class LaboratorioSelectItemConverter extends SelectItemsConverter{
	
	@Override
	public String getAsString(FacesContext context, UIComponent component,
			Object value) {
		Integer id = (value instanceof LaboratorioLab) ? ((LaboratorioLab) value).getIdLaboratorio()
				: null;
		return (id != null) ? String.valueOf(id) : null;

	}

}
