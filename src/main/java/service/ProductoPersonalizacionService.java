package service;

import dao.ExtraDao;
import dao.PersonalizacionDao;
import dao.ProductoTamanoDao;
import dtoS.ExtraDTO;
import dtoS.PersonalizacionDTO;
import dtoS.ProductoCustomizationDTO;
import dtoS.TamanoDTO;
import dtoS.TamanoPrecioDTO;

import java.util.List;

public class ProductoPersonalizacionService {

    private final ProductoTamanoDao productoTamanoDao;
    private final ExtraDao extraDao;
    private final PersonalizacionDao personalizacionDao;

    public ProductoPersonalizacionService(
            ProductoTamanoDao productoTamanoDao,
            ExtraDao extraDao,
            PersonalizacionDao personalizacionDao
    ) {
        this.productoTamanoDao = productoTamanoDao;
        this.extraDao = extraDao;
        this.personalizacionDao = personalizacionDao;
    }

    public List<TamanoDTO> getTamanosByProducto(int idProducto) {
        return productoTamanoDao.findTamanosByProducto(idProducto);
    }

    public List<ExtraDTO> getExtrasByProducto(int idProducto) {
        return extraDao.findActivosByProducto(idProducto);
    }

    public List<PersonalizacionDTO> getPersonalizacionesByProducto(int idProducto) {
        return personalizacionDao.findActivasByProducto(idProducto);
    }

    public ProductoCustomizationDTO getCustomizationByProducto(int idProducto) {
        List<TamanoDTO> tamanos = getTamanosByProducto(idProducto);
        List<ExtraDTO> extras = getExtrasByProducto(idProducto);
        List<PersonalizacionDTO> personalizaciones = getPersonalizacionesByProducto(idProducto);

        return new ProductoCustomizationDTO(tamanos, extras, personalizaciones);
    }

    public TamanoPrecioDTO getPrecioByProductoYTamano(int idProducto, int idTamano) {
        return productoTamanoDao.findByProductoYTamano(idProducto, idTamano)
                .orElseThrow(() -> new IllegalStateException(
                        "No existe precio para producto " + idProducto + " y tamaño " + idTamano
                ));
    }
}