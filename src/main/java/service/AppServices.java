package service;

import facade.CajaFacade;
import facade.FichajeFacade;
import service.AuthService;
import service.FichajeService;
import service.SesionCajaService;
import service.UsuarioRecordadoService;

public class AppServices {

    public final AuthService authService;
    public final FichajeFacade fichajeFacade;
    public final FichajeService fichajeService;
    public final SesionCajaService sesionCajaService;
    public final UsuarioRecordadoService usuarioRecordadoService;
    public final CajaFacade  cajaFacade;

    public AppServices(
            AuthService authService,
            FichajeService fichajeService,
            SesionCajaService sesionCajaService,
            UsuarioRecordadoService usuarioRecordadoService,
            FichajeFacade fichajeFacade,
            CajaFacade  cajaFacade
    ) {
        this.authService = authService;
        this.fichajeFacade = fichajeFacade;
        this.fichajeService = fichajeService;
        this.sesionCajaService = sesionCajaService;
        this.usuarioRecordadoService = usuarioRecordadoService;
        this.cajaFacade = cajaFacade;
    }
}
