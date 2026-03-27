package service;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import dao.ComboDao;
import dao.ComboItemDao;
import dtoS.ComboDTO;
import dtoS.ComboItemDTO;
import model.ComboDefinition;

public class ComboService {

    private final ComboDao comboDao;
    private final ComboItemDao comboItemDao;

    public ComboService(ComboDao comboDao, ComboItemDao comboItemDao) {
        this.comboDao = Objects.requireNonNull(comboDao, "comboDao no puede ser null");
        this.comboItemDao = Objects.requireNonNull(comboItemDao, "comboItemDao no puede ser null");
    }

    public List<ComboDefinition> loadCombosActivos() throws SQLException {
        List<ComboDTO> combos = comboDao.findActivos();
        List<ComboDefinition> definitions = new ArrayList<>();

        for (ComboDTO combo : combos) {
            List<ComboItemDTO> items = comboItemDao.findByCombo(combo.getIdCombo());

            if (items.isEmpty()) {
                continue;
            }

            definitions.add(new ComboDefinition(combo, items));
        }

        return definitions;
    }
}