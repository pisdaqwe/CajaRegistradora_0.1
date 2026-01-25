
package facade;

import java.math.BigDecimal;

import dtoS.CajaEstadoDTO;
import dtoS.FichajeActivoDTO;
import service.SesionCajaService;

public class CajaFacade {

    private final SesionCajaService sesionCajaService;

    public CajaFacade(SesionCajaService sesionCajaService) {
        this.sesionCajaService = sesionCajaService;
    }

    public void abrirSesionCaja(
            FichajeActivoDTO empleado,
            CajaEstadoDTO caja,
            BigDecimal importeInicial
    ) {
        sesionCajaService.abrirSesionCaja(
            caja.getIdCaja(),
            empleado.getIdUsuario(),
            importeInicial
        );
    }
}
