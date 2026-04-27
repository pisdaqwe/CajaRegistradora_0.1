package service;

import app.AppContext;
import dao.ExtraDao;
import dtoS.StockExtraDisponibilidadDTO;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DisponibilidadExtraService {

    private final ExtraDao extraDao;
    private final AuditoriaService auditoriaService;

    public DisponibilidadExtraService(ExtraDao extraDao,
                                      AuditoriaService auditoriaService) {
        if (extraDao == null) {
            throw new IllegalArgumentException("extraDao no puede ser null");
        }
        if (auditoriaService == null) {
            throw new IllegalArgumentException("auditoriaService no puede ser null");
        }
        this.extraDao = extraDao;
        this.auditoriaService = auditoriaService;
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
        int idUsuario = AppContext.getUsuarioId();

        StockExtraDisponibilidadDTO actual = getDisponibilidadExtraSucursalActual(idExtra);

        extraDao.updateDisponibilidadExtra(idSucursal, idExtra, disponible);

        auditarSeguro(
                idUsuario,
                idSucursal,
                "DISPONIBILIDAD_EXTRA_ACTUALIZADA",
                detallesCambio(actual, disponible)
        );
    }

    private Map<String, Object> detallesCambio(StockExtraDisponibilidadDTO actual,
                                               boolean disponibleNuevo) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("idExtra", actual.getIdExtra());
        data.put("nombreExtra", actual.getNombreExtra());
        data.put("tipoExtra", actual.getTipoExtra());
        data.put("disponibleAnterior", actual.isDisponible());
        data.put("disponibleNuevo", disponibleNuevo);
        return data;
    }

    private void auditarSeguro(int idUsuario,
                               int idSucursal,
                               String accion,
                               Map<String, Object> detalles) {
        try {
            auditoriaService.registrarEvento(idUsuario, idSucursal, accion, detalles);
        } catch (Exception ex) {
            System.err.println("[AUDITORIA] No se pudo registrar evento " + accion + ": " + ex.getMessage());
        }
    }
}