package dtoS;

import java.util.ArrayList;
import java.util.List;

public final class ProductoCustomizationDTO {

    private final List<TamanoDTO> tamanos;
    private final List<ExtraDTO> extras;
    private final List<PersonalizacionDTO> personalizaciones;

    public ProductoCustomizationDTO(
            List<TamanoDTO> tamanos,
            List<ExtraDTO> extras,
            List<PersonalizacionDTO> personalizaciones
    ) {
        this.tamanos = (tamanos != null) ? tamanos : new ArrayList<>();
        this.extras = (extras != null) ? extras : new ArrayList<>();
        this.personalizaciones = (personalizaciones != null) ? personalizaciones : new ArrayList<>();
    }

    public List<TamanoDTO> getTamanos() {
        return tamanos;
    }

    public List<ExtraDTO> getExtras() {
        return extras;
    }

    public List<PersonalizacionDTO> getPersonalizaciones() {
        return personalizaciones;
    }

    @Override
    public String toString() {
        return "ProductoCustomizationDTO{" +
                "tamanos=" + tamanos +
                ", extras=" + extras +
                ", personalizaciones=" + personalizaciones +
                '}';
    }
}
