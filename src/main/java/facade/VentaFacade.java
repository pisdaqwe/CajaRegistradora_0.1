package facade;

import dtoS.RegistrarVentaRequest;
import dtoS.RegistrarVentaResultDTO;
import service.VentaService;

public class VentaFacade {

    private final VentaService ventaService;

    public VentaFacade(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    public RegistrarVentaResultDTO registrarVenta(RegistrarVentaRequest request) {
        return ventaService.registrarVenta(request);
    }
}
