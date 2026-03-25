package service;

import facade.CajaFacade;
import facade.FichajeFacade;
import facade.VentaFacade;

public class AppServices {

    public final AuthService authService;
    public final FichajeFacade fichajeFacade;
    public final FichajeService fichajeService;
    public final SesionCajaService sesionCajaService;
    public final UsuarioRecordadoService usuarioRecordadoService;
    public final CajaFacade cajaFacade;
    public final UsuarioService usuarioService;
    public final CatalogoService catalogoService;
    public final ProductoPersonalizacionService productoPersonalizacionService;
    public final VentaFacade ventaFacade;
    public final ColaImpresionService colaImpresionService;
    public final TicketClienteService ticketClienteService;
    public final DisponibilidadProductoService disponibilidadProductoService;
    public final DisponibilidadExtraService disponibilidadExtraService;
    public AppServices(
    		
            AuthService authService,
            FichajeService fichajeService,
            SesionCajaService sesionCajaService,
            UsuarioRecordadoService usuarioRecordadoService,
            FichajeFacade fichajeFacade,
            CajaFacade cajaFacade,
            UsuarioService usuarioService,
            CatalogoService catalogoService,
            ProductoPersonalizacionService productoPersonalizacionService,
            VentaFacade ventaFacade,
            ColaImpresionService colaImpresionService,
            TicketClienteService ticketClienteService,
            DisponibilidadProductoService disponibilidadProductoService,
            DisponibilidadExtraService disponibilidadExtraService
    ) {
        this.authService = authService;
        this.fichajeFacade = fichajeFacade;
        this.fichajeService = fichajeService;
        this.sesionCajaService = sesionCajaService;
        this.usuarioRecordadoService = usuarioRecordadoService;
        this.cajaFacade = cajaFacade;
        this.usuarioService = usuarioService;
        this.catalogoService = catalogoService;
        this.productoPersonalizacionService = productoPersonalizacionService;
        this.ventaFacade = ventaFacade;
        this.colaImpresionService = colaImpresionService;
        this.ticketClienteService = ticketClienteService;
        this.disponibilidadProductoService = disponibilidadProductoService;
        this.disponibilidadExtraService = disponibilidadExtraService;
    }
}
