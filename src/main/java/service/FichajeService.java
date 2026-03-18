package service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import dao.FichajeDao;
import dao.SesionCajaDao;
import dao.UsuarioDao;
import dtoS.FichajeActivoDTO;
import enums.EstadoFichaje;
import model.Fichaje;
import model.Usuario;

public class FichajeService {
    private final FichajeDao fichajeDao;
    private final SesionCajaDao sesionCajaDao;

    public FichajeService(FichajeDao fichajeDao, SesionCajaDao sesionCajaDao) {
        if (fichajeDao == null || sesionCajaDao == null) {
            throw new IllegalArgumentException("Dependencias no pueden ser null");
        }
        this.fichajeDao = fichajeDao;
        this.sesionCajaDao = sesionCajaDao;
    }
    

    // =========================
    // FICHAR ENTRADA
    // =========================
    public Fichaje ficharEntrada(int idUsuario) {
        validarIdUsuario(idUsuario);

        // No puede fichar si ya tiene uno abierto
        fichajeDao.findFichajeAbiertoByUsuario(idUsuario)
                .ifPresent(f -> {
                    throw new IllegalStateException(
                            "Usuario ya tiene un fichaje abierto"
                    );
                });

        Fichaje nuevo = new Fichaje();
        nuevo.setIdUsuario(idUsuario);
        nuevo.setEstado(EstadoFichaje.ABIERTO);
        nuevo.setObservaciones(null);

        // La BD se encarga de fecha_entrada (NOW)
        fichajeDao.insert(nuevo);

        // Recuperamos el fichaje recién abierto
        return fichajeDao.findFichajeAbiertoByUsuario(idUsuario)
                .orElseThrow(() ->
                        new IllegalStateException("Error al crear el fichaje"));
    }

    // =========================
    // FICHAR SALIDA
    // =========================
    public Fichaje ficharSalida(int idUsuario) {
        validarIdUsuario(idUsuario);

        Fichaje abierto = fichajeDao.findFichajeAbiertoByUsuario(idUsuario)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Usuario no tiene fichaje abierto"
                        )
                );
        if (sesionCajaDao.existeSesionAbiertaPorUsuario(idUsuario)) {
            throw new IllegalStateException(
                "No puedes fichar salida mientras tengas una sesión de caja abierta. Cierra primero la caja asignada."
            );
        }
        // La BD pone fecha_salida y calcula duración
        fichajeDao.cerrarFichaje(abierto.getIdFichaje());

        // Devolvemos el fichaje ya cerrado
        return fichajeDao.findById(abierto.getIdFichaje())
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Error al cerrar el fichaje"
                        )
                );
    }

    // =========================
    // VALIDACIONES
    // =========================
    private void validarIdUsuario(int idUsuario) {
        if (idUsuario <= 0) {
            throw new IllegalArgumentException("Id de usuario no válido");
        }
    }
 // ===== FichajeService (usar el DAO directamente) =====

    public List<FichajeActivoDTO> findFichajesActivos() {
        return fichajeDao.findFichajesActivosConUsuario();
    }


}

	


