package service;

import app.AppContext;
import dao.CajaDao;
import dao.SesionCajaDao;
import dtoS.CajaEstadoDTO;
import dtoS.CierreCajaResumenDTO;
import dtoS.LoginRapidoButtonDTO;
import dtoS.SesionCajaRefDTO;
import enums.EstadoCaja;
import enums.EstadoSesionCaja;
import model.Caja;
import model.SesionCaja;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Servicio de negocio para la gestión de sesiones de caja.
 * Controla apertura, uso y cierre de una caja.
 */
public class SesionCajaService {

    private final CajaDao cajaDao;
    private final SesionCajaDao sesionCajaDao;
    private final AuditoriaService auditoriaService;

    public SesionCajaService(CajaDao cajaDao,
                             SesionCajaDao sesionCajaDao,
                             AuditoriaService auditoriaService) {
        if (cajaDao == null || sesionCajaDao == null || auditoriaService == null) {
            throw new IllegalArgumentException("Dependencias no pueden ser null");
        }
        this.cajaDao = cajaDao;
        this.sesionCajaDao = sesionCajaDao;
        this.auditoriaService = auditoriaService;
    }

    public Optional<SesionCaja> findSesionAbiertaByUsuarioActual() {
        int idUsuario = AppContext.getUsuarioId();
        return sesionCajaDao.findSesionCajaByIdUsuario(idUsuario);
    }

    // =====================================================
    // CONSULTAS DE ESTADO
    // =====================================================

    public boolean existsSesionAbiertaPorUsuario(int idUsuario) {
        if (idUsuario <= 0) {
            throw new IllegalArgumentException("idUsuario debe ser > 0");
        }
        return sesionCajaDao.existeSesionAbiertaPorUsuario(idUsuario);
    }

    public boolean haySesionAbierta(int idCaja) {
        return sesionCajaDao.findSesionAbiertaByCaja(idCaja).isPresent();
    }

    public Optional<SesionCaja> getSesionAbierta(int idCaja) {
        return sesionCajaDao.findSesionAbiertaByCaja(idCaja);
    }

    public SesionCaja getSesionAbiertaOrThrow(int idCaja) {
        return getSesionAbierta(idCaja)
                .orElseThrow(() ->
                        new IllegalStateException("No hay ninguna sesión de caja abierta"));
    }

    // =====================================================
    // APERTURA DE SESIÓN
    // =====================================================

    public SesionCaja abrirSesionCaja(int idCaja,
                                      int idUsuario,
                                      BigDecimal importeInicial) {
        Caja caja = cajaDao.findById(idCaja)
                .orElseThrow(() -> new IllegalStateException("La caja no existe"));

        if (!caja.isActiva() || caja.getEstado() != EstadoCaja.OPERATIVA) {
            throw new IllegalStateException("La caja no está operativa");
        }

        if (haySesionAbierta(idCaja)) {
            throw new IllegalStateException("Ya existe una sesión abierta para esta caja");
        }

        if (importeInicial == null || importeInicial.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El importe inicial no es válido");
        }

        SesionCaja sesion = new SesionCaja();
        sesion.setIdCaja(idCaja);
        sesion.setIdUsuarioApertura(idUsuario);
        sesion.setImporteInicial(importeInicial);
        sesion.setTotalVentas(BigDecimal.ZERO);
        sesion.setEstado(EstadoSesionCaja.ABIERTA);

        sesionCajaDao.insert(sesion);
        cajaDao.updateUltimaApertura(idCaja);

        auditarSeguro(
                idUsuario,
                caja.getIdSucursal(),
                "SESION_CAJA_APERTURA_OK",
                detallesApertura(sesion, caja)
        );

        return sesion;
    }

    // =====================================================
    // CIERRE DE SESIÓN
    // =====================================================

    public void cerrarSesionCaja(int idCaja,
                                 int idUsuario,
                                 BigDecimal importeFinal,
                                 String observaciones) {
        SesionCaja sesion = getSesionAbiertaOrThrow(idCaja);

        if (importeFinal == null || importeFinal.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El importe final no es válido");
        }

        Caja caja = cajaDao.findById(idCaja)
                .orElseThrow(() -> new IllegalStateException("La caja no existe"));

        CierreCajaResumenDTO resumen = calcularResumenCierre(sesion.getIdSesion());
        BigDecimal efectivoEsperado = resumen.getEfectivoEsperado();
        BigDecimal desfase = importeFinal.subtract(efectivoEsperado);

        sesion.setIdUsuarioCierre(idUsuario);
        sesion.setImporteFinal(importeFinal);
        sesion.setObservaciones(observaciones);
        sesion.setEstado(EstadoSesionCaja.CERRADA);

        sesionCajaDao.cerrarSesion(sesion);

        String accion = desfase.compareTo(BigDecimal.ZERO) == 0
                ? "CIERRE_CAJA_SIN_DESFASE"
                : "CIERRE_CAJA_CON_DESFASE";

        auditarSeguro(
                idUsuario,
                caja.getIdSucursal(),
                accion,
                detallesCierre(sesion, caja, resumen, efectivoEsperado, importeFinal, desfase)
        );
    }

    // =====================================================
    // CÁLCULOS
    // =====================================================

    public CierreCajaResumenDTO calcularResumenCierre(int idSesion) {
        CierreCajaResumenDTO dto = new CierreCajaResumenDTO();
        dto.setIdSesion(idSesion);
        dto.setImporteInicial(sesionCajaDao.getImporteInicial(idSesion));
        dto.setVentasEfectivo(sesionCajaDao.getTotalPagosEfectivo(idSesion));
        dto.setVentasTarjeta(sesionCajaDao.getTotalPagosTarjeta(idSesion));
        dto.setDevolucionesEfectivo(sesionCajaDao.getTotalDevolucionesEfectivo(idSesion));
        dto.setDevolucionesTarjeta(sesionCajaDao.getTotalDevolucionesTarjeta(idSesion));
        return dto;
    }

    /**
     * Efectivo esperado:
     * importe inicial + ventas en efectivo - devoluciones en efectivo.
     */
    public BigDecimal calcularEfectivoEsperado(int idSesion) {
        return calcularResumenCierre(idSesion).getEfectivoEsperado();
    }

    public List<Caja> findActivasBySucursal(int idSucursal) {
        if (idSucursal <= 0) {
            throw new IllegalArgumentException("idSucursal debe ser > 0");
        }
        return cajaDao.findActivasBySucursal(idSucursal);
    }

    // =====================================================
    // GUARDS
    // =====================================================

    public void assertSesionAbiertaParaVenta(int idCaja) {
        if (!haySesionAbierta(idCaja)) {
            throw new IllegalStateException("No se puede vender sin una sesión de caja abierta");
        }
    }

    public List<LoginRapidoButtonDTO> getBotonesLoginRapido(int idCaja) {
        if (idCaja <= 0) {
            throw new IllegalArgumentException("idCaja debe ser > 0");
        }

        return sesionCajaDao.selectBotonesLoginRapidoByCaja(idCaja);
    }

    public List<CajaEstadoDTO> getEstadoCajas() {
        return sesionCajaDao.findEstadoCajas();
    }

    public SesionCajaRefDTO requireSesionAbiertaPorUsuario(int idUsuario) {
        return sesionCajaDao.selectRefAbiertaByUsuario(idUsuario)
                .orElseThrow(() -> new IllegalStateException(
                        "No tienes una sesión de caja asignada/abierta. Pide al encargado que te abra una."
                ));
    }

    // =====================================================
    // DETALLES AUDITORÍA
    // =====================================================

    private Map<String, Object> detallesApertura(SesionCaja sesion, Caja caja) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("idSesion", sesion.getIdSesion());
        data.put("idCaja", sesion.getIdCaja());
        data.put("nombreCaja", caja.getNombre());
        data.put("idSucursal", caja.getIdSucursal());
        data.put("idUsuarioApertura", sesion.getIdUsuarioApertura());
        data.put("importeInicial", sesion.getImporteInicial());
        data.put("estado", sesion.getEstado() != null ? sesion.getEstado().name() : null);
        return data;
    }

    private Map<String, Object> detallesCierre(SesionCaja sesion,
                                               Caja caja,
                                               CierreCajaResumenDTO resumen,
                                               BigDecimal efectivoEsperado,
                                               BigDecimal importeContado,
                                               BigDecimal desfase) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("idSesion", sesion.getIdSesion());
        data.put("idCaja", sesion.getIdCaja());
        data.put("nombreCaja", caja.getNombre());
        data.put("idSucursal", caja.getIdSucursal());
        data.put("idUsuarioApertura", sesion.getIdUsuarioApertura());
        data.put("idUsuarioCierre", sesion.getIdUsuarioCierre());

        data.put("importeInicial", resumen.getImporteInicial());
        data.put("ventasEfectivo", resumen.getVentasEfectivo());
        data.put("ventasTarjeta", resumen.getVentasTarjeta());
        data.put("devolucionesEfectivo", resumen.getDevolucionesEfectivo());
        data.put("devolucionesTarjeta", resumen.getDevolucionesTarjeta());

        data.put("efectivoEsperado", efectivoEsperado);
        data.put("importeContado", importeContado);
        data.put("desfase", desfase);

        data.put("totalVentas", resumen.getTotalVentas());
        data.put("totalDevoluciones", resumen.getTotalDevoluciones());
        data.put("totalNeto", resumen.getTotalNeto());

        data.put("observaciones", sesion.getObservaciones());
        data.put("estado", sesion.getEstado() != null ? sesion.getEstado().name() : null);

        return data;
    }

    // =====================================================
    // AUDITORÍA SEGURA
    // =====================================================

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