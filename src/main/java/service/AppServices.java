package service;

import service.AuthService;
import service.FichajeFacade;
import service.FichajeService;
import service.SesionCajaService;
import service.UsuarioRecordadoService;

public class AppServices {

    public final AuthService authService;
    public final FichajeFacade fichajeFacade;
    public final FichajeService fichajeService;
    public final SesionCajaService sesionCajaService;
    public final UsuarioRecordadoService usuarioRecordadoService;

    public AppServices(
            AuthService authService,
            FichajeFacade fichajeFacade,
            FichajeService fichajeService,
            SesionCajaService sesionCajaService,
            UsuarioRecordadoService usuarioRecordadoService
    ) {
        this.authService = authService;
        this.fichajeFacade = fichajeFacade;
        this.fichajeService = fichajeService;
        this.sesionCajaService = sesionCajaService;
        this.usuarioRecordadoService = usuarioRecordadoService;
    }
}
