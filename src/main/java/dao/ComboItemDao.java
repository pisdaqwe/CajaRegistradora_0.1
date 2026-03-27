package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import config.DbPool;
import dtoS.ComboItemDTO;

public class ComboItemDao {

    private static final String SQL_FIND_BY_COMBO = """
        SELECT
            id_combo,
            id_producto,
            cantidad,
            orden
        FROM combo_item
        WHERE id_combo = ?
        ORDER BY orden ASC, id_producto ASC
        """;

    public List<ComboItemDTO> findByCombo(int idCombo) throws SQLException {
        List<ComboItemDTO> items = new ArrayList<>();

        try (Connection conn = DbPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_COMBO)) {

            ps.setInt(1, idCombo);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(mapRow(rs));
                }
            }
        }

        return items;
    }

    private ComboItemDTO mapRow(ResultSet rs) throws SQLException {
        ComboItemDTO dto = new ComboItemDTO();
        dto.setIdCombo(rs.getInt("id_combo"));
        dto.setIdProducto(rs.getInt("id_producto"));
        dto.setCantidad(rs.getInt("cantidad"));
        dto.setOrden(rs.getInt("orden"));
        return dto;
    }
}