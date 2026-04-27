package service;

import config.ConfigLoader;
import config.DbPool;
import dao.CajaDao;
import dtoS.CajaTerminalOptionDTO;
import dtoS.SistemaTecnicoInfoDTO;

import java.sql.Connection;
import java.util.List;
import java.util.Objects;

public class SistemaTecnicoService {

    private final CajaDao cajaDao;

    public SistemaTecnicoService(CajaDao cajaDao) {
        this.cajaDao = Objects.requireNonNull(cajaDao, "cajaDao no puede ser null");
    }

    public int getIdCajaTerminalActual() {
        return ConfigLoader.getTerminalIdCaja();
    }

    public List<CajaTerminalOptionDTO> getCajasConfigurables() {
        return cajaDao.findOpcionesTerminal(getIdCajaTerminalActual());
    }

    public void cambiarCajaTerminal(int idCajaNueva) {
        if (idCajaNueva <= 0) {
            throw new IllegalArgumentException("Selecciona una caja válida.");
        }

        cajaDao.findById(idCajaNueva)
                .orElseThrow(() -> new IllegalArgumentException("La caja seleccionada no existe."));

        ConfigLoader.updateTerminalIdCaja(idCajaNueva);
    }

    public boolean probarConexionBD() {
        try (Connection ignored = DbPool.getConnection()) {
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public SistemaTecnicoInfoDTO getInfoTecnica() {
        SistemaTecnicoInfoDTO dto = new SistemaTecnicoInfoDTO();

        dto.setAppName(ConfigLoader.getAppName());
        dto.setAppVersion(ConfigLoader.getAppVersion());
        dto.setTerminalIdCaja(ConfigLoader.getTerminalIdCaja());

        dto.setDbUrl(ConfigLoader.getDbUrl());
        dto.setDbUser(ConfigLoader.getDbUser());
        dto.setConexionBdOk(probarConexionBD());

        dto.setLogsPath(ConfigLoader.getLogsPath());
        dto.setTicketsPath(ConfigLoader.getTicketsPath());
        dto.setReportsPath(ConfigLoader.getReportsPath());

        dto.setJavaVersion(System.getProperty("java.version"));
        dto.setSistemaOperativo(System.getProperty("os.name"));
        dto.setUsuarioSistema(System.getProperty("user.name"));

        return dto;
    }
}