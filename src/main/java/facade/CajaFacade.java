package facade;

import dao.SesionCajaDao;
import dao.CajaDao;
import dtoS.CajaEstadoDTO;
import dtoS.FichajeActivoDTO;
import service.FichajeService;

import java.math.BigDecimal;

public class CajaFacade {

    private final SesionCajaDao sesionCajaDao;
    private final FichajeService fichajeService;
    

    public CajaFacade(SesionCajaDao sesionCajaDao,FichajeService fichajeService) {
        this.sesionCajaDao = sesionCajaDao;
        this.fichajeService = fichajeService;
        
    }

    public void abrirSesionCaja(
            FichajeActivoDTO empleado,
            CajaEstadoDTO caja,
            BigDecimal importeInicial
    ) {

        if (empleado == null) {
            throw new IllegalStateException("Debe seleccionar un empleado");
        }

        if (caja == null) {
            throw new IllegalStateException("Debe seleccionar una caja");
        }

        if (importeInicial == null || importeInicial.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException("Importe inicial no válido");
        }

        // 1️⃣ comprobar que la caja sigue libre
        if (sesionCajaDao.existeSesionAbiertaEnCaja(caja.getIdCaja())) {
            throw new IllegalStateException("La caja ya tiene una sesión abierta");
        }

        // 2️⃣ comprobar que el empleado no tiene sesión
        if (sesionCajaDao.existeSesionAbiertaPorUsuario(empleado.getIdUsuario())) {
            throw new IllegalStateException("El empleado ya tiene una sesión de caja");
        }

        // 3️⃣ abrir sesión
        sesionCajaDao.abrirSesionCaja(
                caja.getIdCaja(),
                empleado.getIdUsuario(),
                importeInicial
        );
    }
}
