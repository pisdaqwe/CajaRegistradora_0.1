package service;

import dao.CategoriaDao;
import dao.ProductoDao;
import dao.SubcategoriaDao;
import dtoS.CategoriaDTO;
import dtoS.ProductoDTO;
import dtoS.SubCategoriaDTO;

import java.util.List;

public class CatalogoService {

    private final CategoriaDao categoriaDao;
    private final SubcategoriaDao subcategoriaDao;
    private final ProductoDao productoDao;

    public CatalogoService(CategoriaDao categoriaDao, SubcategoriaDao subcategoriaDao, ProductoDao productoDao) {
        this.categoriaDao = categoriaDao;
        this.subcategoriaDao = subcategoriaDao;
        this.productoDao = productoDao;
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
}