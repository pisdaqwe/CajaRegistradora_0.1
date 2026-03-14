package service;

import dao.UsuarioDao;
import exceptions.AuthenticationException;
import model.Usuario;
import util.HashUtil;

public class AuthService {

    private final UsuarioDao usuarioDao;

    public AuthService(UsuarioDao usuarioDao) {
        if (usuarioDao == null) {
            throw new IllegalArgumentException("UsuarioDao no puede ser null");
        }
        this.usuarioDao = usuarioDao;
    }

    // =========================
    // LOGIN COMPLETO (usuario + PIN)
    // =========================
    public Usuario loginCompleto(String usuarioTxt, String pin) {

        if (usuarioTxt == null || usuarioTxt.isBlank()) {
            throw new AuthenticationException("Usuario requerido");
        }

        if (pin == null || pin.isBlank()) {
            throw new AuthenticationException("PIN requerido");
        }

        Usuario usuario = usuarioDao.findByUsuario(usuarioTxt.trim())
                .orElseThrow(() -> new AuthenticationException("Usuario no existe"));

        validarUsuarioActivo(usuario);
        validarPin(usuario, pin);

        return usuario;
    }

    // =========================
    // LOGIN RÁPIDO (botón amarillo + PIN)
    // =========================
    public Usuario loginRapido(int idUsuario, String pin) {

        if (idUsuario <= 0) {
            throw new AuthenticationException("Usuario inválido");
        }

        if (pin == null || pin.isBlank()) {
            throw new AuthenticationException("PIN requerido");
        }

        Usuario usuario = usuarioDao.findById(idUsuario)
                .orElseThrow(() -> new AuthenticationException("Usuario no existe"));

        validarUsuarioActivo(usuario);
        validarPin(usuario, pin);

        return usuario;
    }

    // =========================
    // VALIDACIONES INTERNAS
    // =========================
    private void validarUsuarioActivo(Usuario usuario) {
        if (!usuario.isActivo()) {
            throw new AuthenticationException("Usuario inactivo");
        }
    }

    private void validarPin(Usuario usuario, String pinPlano) {
        String pinHash = HashUtil.sha256(pinPlano);

        if (!pinHash.equals(usuario.getPinHash())) {
            throw new AuthenticationException("PIN incorrecto");
        }
    }
}

