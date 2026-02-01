package service;

import java.util.Optional;

import dao.UsuarioDao;
import model.Usuario;

public class UsuarioService {
	   private final UsuarioDao usuarioDao;

	    public UsuarioService(UsuarioDao usuarioDao) {
	        this.usuarioDao = usuarioDao;
	    }

	    public Optional<Usuario> findByCodigo(String codigo) {
	        return usuarioDao.findByUsuario(codigo);
	    }
	}