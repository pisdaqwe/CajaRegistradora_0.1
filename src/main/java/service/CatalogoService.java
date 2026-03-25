package service;

import app.AppContext;
import dao.CategoriaDao;
import dao.ProductoDao;
import dao.ProductoTamanoDao;
import dao.SubcategoriaDao;
import dtoS.CategoriaDTO;
import dtoS.ProductoBusquedaRowDTO;
import dtoS.ProductoCatalogoDTO;
import dtoS.ProductoDTO;
import dtoS.SubCategoriaDTO;
import dtoS.TamanoPrecioDTO;

import java.util.List;
import java.util.Optional;

public class CatalogoService {

    private final CategoriaDao categoriaDao;
    private final SubcategoriaDao subcategoriaDao;
    private final ProductoDao productoDao;
    private final ProductoTamanoDao productoTamanoDao;

    public CatalogoService(
            CategoriaDao categoriaDao,
            SubcategoriaDao subcategoriaDao,
            ProductoDao productoDao,
            ProductoTamanoDao productoTamanoDao
    ) {
        this.categoriaDao = categoriaDao;
        this.subcategoriaDao = subcategoriaDao;
        this.productoDao = productoDao;
        this.productoTamanoDao = productoTamanoDao;
    }

    // =====================================================
    // CATÁLOGO BASE
    // =====================================================

    public List<CategoriaDTO> getCategoriasTpv() {
        return categoriaDao.findActivasOrdenadas();
    }

    public List<SubCategoriaDTO> getSubcategoriasByCategoria(int idCategoria) {
        return subcategoriaDao.findActivasByCategoriaOrdenadas(idCategoria);
    }

    /**
     * Catálogo base por subcategoría.
     *
     * IMPORTANTE:
     * Esta versión NO aplica disponibilidad operativa por sucursal.
     */
    public List<ProductoDTO> getProductosBySubcategoria(int idSubcategoria) {
        return productoDao.findBySubcategoriaOrdenados(idSubcategoria);
    }

    /**
     * Versión TOP del catálogo base.
     *
     * IMPORTANTE:
     * Esta versión NO aplica disponibilidad operativa por sucursal.
     */
    public List<ProductoDTO> getTopProductosBySubcategoria(int idSubcategoria, int limit) {
        return productoDao.findTopBySubcategoriaOrdenados(idSubcategoria, limit);
    }

    // =====================================================
    // CATÁLOGO OPERATIVO POR SUCURSAL
    // =====================================================

    public List<ProductoCatalogoDTO> getProductosCatalogoBySubcategoria(int idSubcategoria) {
        int idSucursal = AppContext.getSesionCajaActual().getIdSucursal();
        return productoDao.findCatalogoBySubcategoriaYSucursal(idSubcategoria, idSucursal);
    }

    public List<ProductoCatalogoDTO> getTopProductosCatalogoBySubcategoria(int idSubcategoria, int limit) {
        int idSucursal = AppContext.getSesionCajaActual().getIdSucursal();
        return productoDao.findTopCatalogoBySubcategoriaYSucursal(idSubcategoria, idSucursal, limit);
    }

    public List<ProductoCatalogoDTO> getProductosCatalogoBySubcategoria(int idSubcategoria, int idSucursal) {
        return productoDao.findCatalogoBySubcategoriaYSucursal(idSubcategoria, idSucursal);
    }

    public List<ProductoCatalogoDTO> getTopProductosCatalogoBySubcategoria(int idSubcategoria, int idSucursal, int limit) {
        return productoDao.findTopCatalogoBySubcategoriaYSucursal(idSubcategoria, idSucursal, limit);
    }

    // =====================================================
    // BÚSQUEDA DE PRODUCTOS
    // =====================================================

    public Optional<ProductoCatalogoDTO> buscarProductoCatalogoPorSku(String sku) {
        if (sku == null || sku.isBlank()) {
            return Optional.empty();
        }

        int idSucursal = AppContext.getSesionCajaActual().getIdSucursal();
        return productoDao.findCatalogoBySkuYSucursal(sku.trim(), idSucursal);
    }

    public List<ProductoBusquedaRowDTO> getFilasBusquedaProductoOperativa() {
        int idSucursal = AppContext.getSesionCajaActual().getIdSucursal();
        return productoDao.findFilasBusquedaProductoBySucursal(idSucursal);
    }

    // =====================================================
    // TAMAÑO DEFAULT + PRECIO
    // =====================================================

    public TamanoPrecioDTO getTamanoDefaultYPrecio(int idProducto) {
        return productoTamanoDao.selectTamanoDefaultYPrecio(idProducto)
                .orElseThrow(() -> new IllegalStateException(
                        "El producto " + idProducto + " no tiene tamaños configurados en producto_tamano"
                ));
    }
}