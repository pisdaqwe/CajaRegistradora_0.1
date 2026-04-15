package dtoS;

import java.util.ArrayList;
import java.util.List;

/**
 * Resultado del caso de uso "registrar merma".
 *
 * Devuelve la información mínima necesaria después de persistir:
 * - id de la merma creada
 * - ids de las líneas insertadas
 *
 * Este DTO sirve para:
 * - mostrar confirmación en UI
 * - enlazar auditoría posterior
 * - facilitar futuras ampliaciones
 */
public class MermaResultDTO {

    /**
     * ID de la cabecera de merma persistida.
     */
    private int idMerma;

    /**
     * Líneas persistidas de la merma.
     */
    private List<MermaItemResultDTO> itemsPersistidos = new ArrayList<>();

    public int getIdMerma() {
        return idMerma;
    }

    public void setIdMerma(int idMerma) {
        this.idMerma = idMerma;
    }

    public List<MermaItemResultDTO> getItemsPersistidos() {
        return itemsPersistidos;
    }

    public void setItemsPersistidos(List<MermaItemResultDTO> itemsPersistidos) {
        this.itemsPersistidos = itemsPersistidos != null
                ? itemsPersistidos
                : new ArrayList<>();
    }
}