package service;

import dao.UsuarioDao;
import model.Fichaje;
import model.Usuario;

public class FichajeFacade {

    private final UsuarioDao usuarioDao;
    private final FichajeService fichajeService;

    public FichajeFacade(UsuarioDao usuarioDao, FichajeService fichajeService) {
        if (usuarioDao == null || fichajeService == null) {
            throw new IllegalArgumentException("Dependencias no pueden ser null");
        }
        this.usuarioDao = usuarioDao;
        this.fichajeService = fichajeService;
    }

    // =========================
    // FICHAR ENTRADA (SIN LOGIN)
    // =========================
    public Fichaje ficharEntradaPorUsuario(String usuario) {
        int idUsuario = resolverIdUsuario(usuario);
        return fichajeService.ficharEntrada(idUsuario);
    }

    // =========================
    // FICHAR SALIDA (SIN LOGIN)
    // =========================
    public Fichaje ficharSalidaPorUsuario(String usuario) {
        int idUsuario = resolverIdUsuario(usuario);
        return fichajeService.ficharSalida(idUsuario);
    }

    // =========================
    // RESOLUCIÓN DE IDENTIDAD
    // =========================
    private int resolverIdUsuario(String usuario) {

        Usuario u = usuarioDao.findByUsuario(usuario)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Usuario no existe"
                        )
                );

        if (!u.isActivo()) {
            throw new IllegalArgumentException(
                    "Usuario no está activo"
            );
        }

        return u.getIdUsuario();
    }
}


