package service;

import app.AppContext;
import dao.ExtraDao;
import dao.PersonalizacionDao;
import dao.ProductoTamanoDao;
import dao.ProductoTipoCafeDao;
import dtoS.ExtraDTO;
import dtoS.PersonalizacionDTO;
import dtoS.ProductoCustomizationDTO;
import dtoS.TamanoDTO;
import dtoS.TamanoPrecioDTO;
import dtoS.TipoCafeDTO;

import java.util.List;

/**
 * Servicio de customización de producto.
 *
 * ESTADO ANTERIOR:
 * - cargaba tamaños
 * - cargaba extras
 * - cargaba personalizaciones
 *
 * AÑADIDO AHORA:
 * - carga también los tipos de café permitidos para el producto
 *
 * OBJETIVO:
 * - seguir siendo el servicio único de customización que usa Ventas
 * - no crear un service paralelo solo para café
 * - devolver a la UI toda la configuración del producto en un único DTO
 *
 * IMPORTANTE:
 * - el bloque CAFÉ solo tendrá uso real en bebidas
 * - en productos que no tengan cafés configurados, devolverá lista vacía
 *
 * Referencia del estado anterior del servicio: :contentReference[oaicite:0]{index=0}
 */
public class ProductoPersonalizacionService {

    /**
     * DAO de tamaños y precios por producto.
     */
    private final ProductoTamanoDao productoTamanoDao;

    /**
     * DAO de extras del producto.
     */
    private final ExtraDao extraDao;

    /**
     * DAO de personalizaciones del producto.
     */
    private final PersonalizacionDao personalizacionDao;

    /**
     * NUEVO:
     * DAO que carga los tipos de café permitidos para el producto.
     */
    private final ProductoTipoCafeDao productoTipoCafeDao;

    public ProductoPersonalizacionService(
            ProductoTamanoDao productoTamanoDao,
            ExtraDao extraDao,
            PersonalizacionDao personalizacionDao,
            ProductoTipoCafeDao productoTipoCafeDao
    ) {
        this.productoTamanoDao = productoTamanoDao;
        this.extraDao = extraDao;
        this.personalizacionDao = personalizacionDao;
        this.productoTipoCafeDao = productoTipoCafeDao;
    }

    /**
     * Devuelve los tamaños disponibles para el producto.
     */
    public List<TamanoDTO> getTamanosByProducto(int idProducto) {
        return productoTamanoDao.findTamanosByProducto(idProducto);
    }

    /**
     * Devuelve los extras disponibles para el producto
     * en la sucursal actual.
     */
    public List<ExtraDTO> getExtrasByProducto(int idProducto) {
        int idSucursal = AppContext.getSesionCajaActual().getIdSucursal();
        return extraDao.findActivosByProductoYSucursal(idProducto, idSucursal);
    }

    /**
     * Devuelve las personalizaciones activas para el producto.
     */
    public List<PersonalizacionDTO> getPersonalizacionesByProducto(int idProducto) {
        return personalizacionDao.findActivasByProducto(idProducto);
    }

    /**
     * NUEVO:
     * Devuelve los tipos de café activos permitidos para el producto.
     *
     * En bebidas devolverá normalmente:
     * - Espresso
     * - Espresso Decaf
     * - cafés de campaña
     *
     * En otros productos puede devolver lista vacía.
     */
    public List<TipoCafeDTO> getTiposCafeByProducto(int idProducto) {
        return productoTipoCafeDao.findActivosByProducto(idProducto);
    }

    /**
     * Devuelve toda la configuración de customización del producto.
     *
     * ANTES:
     * - tamaños
     * - extras
     * - personalizaciones
     *
     * AÑADIDO AHORA:
     * - tiposCafe
     */
    public ProductoCustomizationDTO getCustomizationByProducto(int idProducto) {
        List<TamanoDTO> tamanos = getTamanosByProducto(idProducto);
        List<ExtraDTO> extras = getExtrasByProducto(idProducto);
        List<PersonalizacionDTO> personalizaciones = getPersonalizacionesByProducto(idProducto);

        // =====================================================
        // NUEVO BLOQUE AÑADIDO
        // =====================================================
        List<TipoCafeDTO> tiposCafe = getTiposCafeByProducto(idProducto);

        return new ProductoCustomizationDTO(
                tamanos,
                extras,
                personalizaciones,
                tiposCafe
        );
    }

    /**
     * Devuelve el precio real para producto + tamaño.
     */
    public TamanoPrecioDTO getPrecioByProductoYTamano(int idProducto, int idTamano) {
        return productoTamanoDao.findByProductoYTamano(idProducto, idTamano)
                .orElseThrow(() -> new IllegalStateException(
                        "No existe precio para producto " + idProducto + " y tamaño " + idTamano
                ));
    }
    
    //Informes//
    public List<ExtraDTO> getTodosLosExtrasActivosParaInformes() {
        return extraDao.findTodosActivosOrdenados();
    }
}