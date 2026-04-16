package service;

import dao.SucursalDao;
import model.Sucursal;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class SucursalService {

    private final SucursalDao sucursalDao;

    public SucursalService(SucursalDao sucursalDao) {
        this.sucursalDao = Objects.requireNonNull(sucursalDao, "sucursalDao no puede ser null");
    }

    public Optional<Sucursal> findById(int idSucursal) {
        if (idSucursal <= 0) {
            throw new IllegalArgumentException("idSucursal debe ser > 0");
        }
        return sucursalDao.findById(idSucursal);
    }

    public Sucursal findByIdOrThrow(int idSucursal) {
        return findById(idSucursal)
                .orElseThrow(() -> new IllegalStateException("No existe la sucursal con id " + idSucursal));
    }

    public List<Sucursal> findAll() {
        return sucursalDao.findAll();
    }
}