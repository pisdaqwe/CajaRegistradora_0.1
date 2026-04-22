package dtoS;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class InformeDescuentosAplicadosResultDTO {

    private List<InformeDescuentosAplicadosRowDTO> rows = new ArrayList<>();

    private Integer totalUsos = 0;
    private BigDecimal totalBase = BigDecimal.ZERO;
    private BigDecimal totalImporteDescuento = BigDecimal.ZERO;

    private String descuentoMasUsado;
    private Integer usosDescuentoMasUsado = 0;

    public List<InformeDescuentosAplicadosRowDTO> getRows() {
        return rows;
    }

    public void setRows(List<InformeDescuentosAplicadosRowDTO> rows) {
        this.rows = rows;
    }

    public Integer getTotalUsos() {
        return totalUsos;
    }

    public void setTotalUsos(Integer totalUsos) {
        this.totalUsos = totalUsos;
    }

    public BigDecimal getTotalBase() {
        return totalBase;
    }

    public void setTotalBase(BigDecimal totalBase) {
        this.totalBase = totalBase;
    }

    public BigDecimal getTotalImporteDescuento() {
        return totalImporteDescuento;
    }

    public void setTotalImporteDescuento(BigDecimal totalImporteDescuento) {
        this.totalImporteDescuento = totalImporteDescuento;
    }

    public String getDescuentoMasUsado() {
        return descuentoMasUsado;
    }

    public void setDescuentoMasUsado(String descuentoMasUsado) {
        this.descuentoMasUsado = descuentoMasUsado;
    }

    public Integer getUsosDescuentoMasUsado() {
        return usosDescuentoMasUsado;
    }

    public void setUsosDescuentoMasUsado(Integer usosDescuentoMasUsado) {
        this.usosDescuentoMasUsado = usosDescuentoMasUsado;
    }
}