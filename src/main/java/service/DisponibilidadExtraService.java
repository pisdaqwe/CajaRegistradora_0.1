package service;

import app.AppContext;
import dao.ExtraDao;
import dtoS.StockExtraDisponibilidadDTO;

import java.util.List;

public class DisponibilidadExtraService {

    private final ExtraDao extraDao;

    public DisponibilidadExtraService(ExtraDao extraDao) {
        this.extraDao = extraDao;
    }

    public List<StockExtraDisponibilidadDTO> getDisponibilidadExtrasSucursalActual() {
        int idSucursal = AppContext.getSesionCajaActual().getIdSucursal();
        return extraDao.findDisponibilidadBySucursal(idSucursal);
    }

    public StockExtraDisponibilidadDTO getDisponibilidadExtraSucursalActual(int idExtra) {
        int idSucursal = AppContext.getSesionCajaActual().getIdSucursal();

        return extraDao.findDisponibilidadByExtraYSucursal(idExtra, idSucursal)
                .orElseThrow(() -> new IllegalStateException(
                        "No existe configuración de disponibilidad para el extra " + idExtra
                                + " en la sucursal actual."
                ));
    }

    public void guardarDisponibilidadExtraSucursalActual(int idExtra, boolean disponible) {
        int idSucursal = AppContext.getSesionCajaActual().getIdSucursal();

        extraDao.updateDisponibilidadExtra(idSucursal, idExtra, disponible);
    }
}
