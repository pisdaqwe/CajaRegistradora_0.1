package facade;

import dtoS.RegistrarDevolucionRequest;
import dtoS.RegistrarDevolucionResultDTO;
import dtoS.VentaItemParaDevolucionDTO;
import dtoS.VentaParaDevolucionDTO;
import service.DevolucionService;

import java.util.List;

public class DevolucionFacade {

    private final DevolucionService devolucionService;

    public DevolucionFacade(DevolucionService devolucionService) {
        if (devolucionService == null) {
            throw new IllegalArgumentException("devolucionService no puede ser null");
        }
        this.devolucionService = devolucionService;
    }

    public VentaParaDevolucionDTO getVentaParaDevolucion(int idVenta) {
        return devolucionService.getVentaParaDevolucion(idVenta);
    }

    public List<VentaItemParaDevolucionDTO> getItemsParaDevolucion(int idVenta, int idSucursal) {
        return devolucionService.getItemsParaDevolucion(idVenta, idSucursal);
    }

    public RegistrarDevolucionResultDTO registrarDevolucion(RegistrarDevolucionRequest request) {
        return devolucionService.registrarDevolucion(request);
    }
}
