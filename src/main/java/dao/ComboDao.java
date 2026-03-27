package dao;


import config.DbPool;
import dtoS.ComboDTO;
import dtoS.ComboDTO.DiaSemanaCombo;
import enums.ComboTipo;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

public class ComboDao {

    private static final String SQL_FIND_ACTIVOS = """
        SELECT
            id_combo,
            nombre,
            descripcion,
            tipo,
            valor,
            prioridad,
            activo,
            fecha_inicio,
            fecha_fin,
            hora_inicio,
            hora_fin,
            dias_semana
        FROM combo
        WHERE activo = 1
        ORDER BY prioridad DESC, nombre ASC, id_combo ASC
        """;

    private static final String SQL_FIND_BY_ID = """
        SELECT
            id_combo,
            nombre,
            descripcion,
            tipo,
            valor,
            prioridad,
            activo,
            fecha_inicio,
            fecha_fin,
            hora_inicio,
            hora_fin,
            dias_semana
        FROM combo
        WHERE id_combo = ?
        """;

    public List<ComboDTO> findActivos() throws SQLException {
        List<ComboDTO> combos = new ArrayList<>();

        try (Connection conn = DbPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_ACTIVOS);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                combos.add(mapRow(rs));
            }
        }

        return combos;
    }

    public Optional<ComboDTO> findById(int idCombo) throws SQLException {
        try (Connection conn = DbPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_ID)) {

            ps.setInt(1, idCombo);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        }

        return Optional.empty();
    }

    private ComboDTO mapRow(ResultSet rs) throws SQLException {
        ComboDTO dto = new ComboDTO();

        dto.setIdCombo(rs.getInt("id_combo"));
        dto.setNombre(rs.getString("nombre"));
        dto.setDescripcion(rs.getString("descripcion"));
        dto.setTipo(ComboTipo.valueOf(rs.getString("tipo")));
        dto.setValor(rs.getBigDecimal("valor"));
        dto.setPrioridad(rs.getInt("prioridad"));
        dto.setActivo(rs.getBoolean("activo"));

        Date fechaInicio = rs.getDate("fecha_inicio");
        if (fechaInicio != null) {
            dto.setFechaInicio(fechaInicio.toLocalDate());
        }

        Date fechaFin = rs.getDate("fecha_fin");
        if (fechaFin != null) {
            dto.setFechaFin(fechaFin.toLocalDate());
        }

        Time horaInicio = rs.getTime("hora_inicio");
        if (horaInicio != null) {
            dto.setHoraInicio(horaInicio.toLocalTime());
        }

        Time horaFin = rs.getTime("hora_fin");
        if (horaFin != null) {
            dto.setHoraFin(horaFin.toLocalTime());
        }

        dto.setDiasSemana(parseDiasSemana(rs.getString("dias_semana")));

        return dto;
    }

    private EnumSet<DiaSemanaCombo> parseDiasSemana(String raw) {
        EnumSet<DiaSemanaCombo> dias = EnumSet.noneOf(DiaSemanaCombo.class);

        if (raw == null || raw.isBlank()) {
            return dias;
        }

        String[] parts = raw.split(",");
        for (String part : parts) {
            String token = part == null ? "" : part.trim();
            if (token.isEmpty()) {
                continue;
            }
            dias.add(DiaSemanaCombo.valueOf(token));
        }

        return dias;
    }
}
