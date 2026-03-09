package service;

import facade.CajaFacade;
import facade.FichajeFacade;

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

    public AppServices(
            AuthService authService,
            FichajeService fichajeService,
            SesionCajaService sesionCajaService,
            UsuarioRecordadoService usuarioRecordadoService,
            FichajeFacade fichajeFacade,
            CajaFacade cajaFacade,
            UsuarioService usuarioService,
            CatalogoService catalogoService,
            ProductoPersonalizacionService productoPersonalizacionService
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
    }
}
