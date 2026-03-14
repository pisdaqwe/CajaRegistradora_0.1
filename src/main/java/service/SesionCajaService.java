package service;

import dao.CajaDao;
import dao.SesionCajaDao;
import dtoS.CajaEstadoDTO;
import dtoS.SesionCajaRefDTO;
import enums.EstadoCaja;
import enums.EstadoSesionCaja;
import model.Caja;
import model.SesionCaja;


import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import dtoS.LoginRapidoButtonDTO;

import app.AppContext;

/**
 * Servicio de negocio para la gestión de sesiones de caja.
 * Controla apertura, uso y cierre de una caja.
 */
public class SesionCajaService {

    private final CajaDao cajaDao;
    private final SesionCajaDao sesionCajaDao;

    public SesionCajaService(CajaDao cajaDao, SesionCajaDao sesionCajaDao) {
        this.cajaDao = cajaDao;
        this.sesionCajaDao = sesionCajaDao;
    }

    
    public Optional<SesionCaja>findSesionAbiertaByUsuarioActual(){
    	int idUsuario = AppContext.getUsuarioId();

    	return  sesionCajaDao.findSesionCajaByIdUsuario(idUsuario);
    	
    }
    // =====================================================
    // CONSULTAS DE ESTADO
    // =====================================================

    /**
     * Indica si existe una sesión ABIERTA para una caja.
     */
    public boolean haySesionAbierta(int idCaja) {
        return sesionCajaDao.findSesionAbiertaByCaja(idCaja).isPresent();
    }

    /**
     * Devuelve la sesión ABIERTA de una caja, si existe.
     */
    public Optional<SesionCaja> getSesionAbierta(int idCaja) {
        return sesionCajaDao.findSesionAbiertaByCaja(idCaja);
    }

    /**
     * Devuelve la sesión ABIERTA o lanza excepción.
     */
    public SesionCaja getSesionAbiertaOrThrow(int idCaja) {
        return getSesionAbierta(idCaja)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "No hay ninguna sesión de caja abierta"
                        ));
    }

    // =====================================================
    // APERTURA DE SESIÓN
    // =====================================================

    /**
     * Abre una nueva sesión de caja.
     */
    public SesionCaja abrirSesionCaja(
            int idCaja,
            int idUsuario,
            BigDecimal importeInicial
    ) {

        // 1. Validar caja existente
        Caja caja = cajaDao.findById(idCaja)
                .orElseThrow(() ->
                        new IllegalStateException("La caja no existe"));

        // 2. Validar estado de la caja
        if (!caja.isActiva() || caja.getEstado() != EstadoCaja.OPERATIVA) {
            throw new IllegalStateException(
                    "La caja no está operativa"
            );
        }

        // 3. Validar que no haya sesión abierta para esa caja
        if (haySesionAbierta(idCaja)) {
            throw new IllegalStateException(
                    "Ya existe una sesión abierta para esta caja"
            );
        }

        // 4. Validar importe inicial
        if (importeInicial == null
                || importeInicial.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "El importe inicial no es válido"
            );
        }

      

        // 5. Crear sesión
        SesionCaja sesion = new SesionCaja();
        sesion.setIdCaja(idCaja);
        sesion.setIdUsuarioApertura(idUsuario);
        sesion.setImporteInicial(importeInicial);
        sesion.setTotalVentas(BigDecimal.ZERO);
        sesion.setEstado(EstadoSesionCaja.ABIERTA);

        // 7. Persistir
        sesionCajaDao.insert(sesion);

        // 8. Actualizar metadatos de la caja
        cajaDao.updateUltimaApertura(idCaja);

        return sesion;
    }


    // =====================================================
    // CIERRE DE SESIÓN
    // =====================================================

    /**
     * Cierra la sesión ABIERTA de una caja.
     */
    public void cerrarSesionCaja(
            int idCaja,
            int idUsuario,
            BigDecimal importeFinal,
            String observaciones
    ) {

        // 1. Obtener sesión abierta
        SesionCaja sesion = getSesionAbiertaOrThrow(idCaja);

        // 2. Validar importe final
        if (importeFinal == null
                || importeFinal.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "El importe final no es válido"
            );
        }

        // 3. Cerrar sesión
        sesion.setIdUsuarioCierre(idUsuario);
        sesion.setImporteFinal(importeFinal);
        sesion.setObservaciones(observaciones);
        sesion.setEstado(EstadoSesionCaja.CERRADA);

        sesionCajaDao.cerrarSesion(sesion);
    }

    // =====================================================
    // CÁLCULOS
    // =====================================================

    /**
     * Calcula el efectivo esperado en una sesión:
     * importe inicial + pagos en efectivo.
     */
    public BigDecimal calcularEfectivoEsperado(int idSesion) {

        BigDecimal importeInicial =
                sesionCajaDao.getImporteInicial(idSesion);

        BigDecimal totalEfectivo =
                sesionCajaDao.getTotalPagosEfectivo(idSesion);

        return importeInicial.add(totalEfectivo);
    }

    // =====================================================
    // GUARDS
    // =====================================================

    /**
     * Verifica que exista una sesión abierta antes de permitir ventas.
     */
    public void assertSesionAbiertaParaVenta(int idCaja) {
        if (!haySesionAbierta(idCaja)) {
            throw new IllegalStateException(
                    "No se puede vender sin una sesión de caja abierta"
            );
        }
    }
    public List<LoginRapidoButtonDTO> getBotonesLoginRapido(int idCaja) {
        if (idCaja <= 0) {
            throw new IllegalArgumentException("idCaja debe ser > 0");
        }

        return sesionCajaDao.selectBotonesLoginRapidoByCaja(idCaja);
    }
    

	public List<CajaEstadoDTO> getEstadoCajas() {
		// TODO Auto-generated method stub
		 return sesionCajaDao.findEstadoCajas();
	}
	public SesionCajaRefDTO requireSesionAbiertaPorUsuario(int idUsuario) {
	    return sesionCajaDao.selectRefAbiertaByUsuario(idUsuario)
	        .orElseThrow(() -> new IllegalStateException(
	            "No tienes una sesión de caja asignada/abierta. Pide al encargado que te abra una."
	        ));
	}
	
}
