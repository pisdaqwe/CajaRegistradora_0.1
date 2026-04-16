package service;

import dao.UsuarioDao;
import model.Usuario;

import java.util.List;
import java.util.Optional;

public class UsuarioService {

    private final UsuarioDao usuarioDao;

    public UsuarioService(UsuarioDao usuarioDao) {
        this.usuarioDao = usuarioDao;
    }

    public Optional<Usuario> findByCodigo(String codigo) {
        return usuarioDao.findByUsuario(codigo);
    }

    public List<Usuario> findActivosBySucursal(int idSucursal) {
        if (idSucursal <= 0) {
            throw new IllegalArgumentException("idSucursal debe ser > 0");
        }
        return usuarioDao.findActivosBySucursal(idSucursal);
    }
}