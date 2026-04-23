package service;

import dao.RolDao;
import model.Rol;

import java.util.List;
import java.util.Optional;

/**
 * Service simple para gestión de roles.
 *
 * Responsabilidades:
 * - Exponer roles a la UI y a otros services.
 * - Delegar en RolDao las lecturas necesarias.
 *
 * NO debe:
 * - Contener lógica de UI.
 * - Crear conexiones JDBC directamente.
 */
public class RolService {

    private final RolDao rolDao;

    public RolService(RolDao rolDao) {
        this.rolDao = rolDao;
    }

    public List<Rol> getRoles() {
        return rolDao.findAll();
    }

    public Optional<Rol> findById(int idRol) {
        return rolDao.findById(idRol);
    }

    public Rol requireById(int idRol) {
        return rolDao.findById(idRol)
                .orElseThrow(() -> new IllegalArgumentException("No existe el rol con id: " + idRol));
    }
}