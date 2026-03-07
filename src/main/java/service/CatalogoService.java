package service;

import dao.CategoriaDao;
import dao.ProductoDao;
import dao.ProductoTamanoDao;
import dao.SubcategoriaDao;
import dtoS.CategoriaDTO;
import dtoS.ProductoDTO;
import dtoS.SubCategoriaDTO;
import dtoS.TamanoPrecioDTO;

import java.util.List;

public class CatalogoService {

    private final CategoriaDao categoriaDao;
    private final SubcategoriaDao subcategoriaDao;
    private final ProductoDao productoDao;
    private final ProductoTamanoDao productoTamanoDao;

    public CatalogoService(CategoriaDao categoriaDao, SubcategoriaDao subcategoriaDao, ProductoDao productoDao,ProductoTamanoDao productoTamanoDao) {
        this.categoriaDao = categoriaDao;
        this.subcategoriaDao = subcategoriaDao;
        this.productoDao = productoDao;
        this.productoTamanoDao = productoTamanoDao;
    }

    public List<CategoriaDTO> getCategoriasTpv() {
        return categoriaDao.findActivasOrdenadas();
    }

    public List<SubCategoriaDTO> getSubcategoriasByCategoria(int idCategoria) {
        return subcategoriaDao.findActivasByCategoriaOrdenadas(idCategoria);
    }

    public List<ProductoDTO> getProductosBySubcategoria(int idSubcategoria) {
        return productoDao.findBySubcategoriaOrdenados(idSubcategoria);
    }

    public List<ProductoDTO> getTopProductosBySubcategoria(int idSubcategoria, int limit) {
        return productoDao.findTopBySubcategoriaOrdenados(idSubcategoria, limit);
    }
 // =====================================================
    // TAMAÑO DEFAULT + PRECIO (Regla B)
    // =====================================================
    public TamanoPrecioDTO getTamanoDefaultYPrecio(int idProducto) {
        return productoTamanoDao.selectTamanoDefaultYPrecio(idProducto)
                .orElseThrow(() -> new IllegalStateException(
                        "El producto " + idProducto + " no tiene tamaños configurados en producto_tamano"
                ));
    }
}