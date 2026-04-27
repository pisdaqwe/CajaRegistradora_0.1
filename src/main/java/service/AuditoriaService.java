package service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dao.AuditoriaDao;
import dtoS.AuditoriaFiltroDTO;
import dtoS.AuditoriaRowDTO;
import model.Auditoria;

import java.util.Collections;
import java.util.List;

public class AuditoriaService {

    private final AuditoriaDao auditoriaDao;
    private final ObjectMapper objectMapper;

    public AuditoriaService(AuditoriaDao auditoriaDao) {
        if (auditoriaDao == null) {
            throw new IllegalArgumentException("auditoriaDao no puede ser null");
        }
        this.auditoriaDao = auditoriaDao;
        this.objectMapper = new ObjectMapper();
    }

    public int registrarEvento(int idUsuario,
                               int idSucursal,
                               String accion,
                               Object detalles) {
        if (idUsuario <= 0) {
            throw new IllegalArgumentException("idUsuario inválido");
        }
        if (idSucursal <= 0) {
            throw new IllegalArgumentException("idSucursal inválido");
        }
        if (accion == null || accion.isBlank()) {
            throw new IllegalArgumentException("accion no puede estar vacía");
        }

        Auditoria auditoria = new Auditoria();
        auditoria.setIdUsuario(idUsuario);
        auditoria.setIdSucursal(idSucursal);
        auditoria.setAccion(accion.trim());
        auditoria.setDetalles(toJson(detalles));

        return auditoriaDao.insert(auditoria);
    }

    public List<AuditoriaRowDTO> buscarAuditoria(AuditoriaFiltroDTO filtro) {
        return auditoriaDao.findRowsByFiltro(filtro);
    }

    public List<String> getAccionesDisponibles() {
        return auditoriaDao.findAccionesDistinct();
    }

    private String toJson(Object detalles) {
        Object source = detalles != null ? detalles : Collections.emptyMap();

        try {
            return objectMapper.writeValueAsString(source);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("No se pudo serializar detalles de auditoría.", e);
        }
    }
}