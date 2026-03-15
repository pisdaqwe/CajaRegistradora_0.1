package model;

import java.math.BigDecimal;
import enums.MetodoPago;
import enums.TipoServicio;


public class CobroSession {

	private TipoServicio tipoServicio;
    private String nombrePedido;
    private BigDecimal total;
    private BigDecimal importeRecibido;
    private MetodoPago metodoPago;
    private boolean pagoExacto;
    public CobroSession() {
        clear();
    }

    public void clear() {
        this.nombrePedido = "";
        this.total = BigDecimal.ZERO;
        this.importeRecibido = BigDecimal.ZERO;
        this.metodoPago = null;
        this.pagoExacto = false;
        this.tipoServicio = TipoServicio.PARA_TOMAR;
    }

    public TipoServicio getTipoServicio() {
		return tipoServicio;
	}

	public void setTipoServicio(TipoServicio tipoServicio) {
		this.tipoServicio = tipoServicio;
	}

	public String getNombrePedido() {
        return nombrePedido;
    }

    public void setNombrePedido(String nombrePedido) {
        this.nombrePedido = (nombrePedido != null) ? nombrePedido.trim() : "";
    }

    public BigDecimal getTotal() {
        return total;
    }
    public void setTotal(BigDecimal total) {
        this.total = (total != null) ? total : BigDecimal.ZERO;
    }

    public BigDecimal getImporteRecibido() {
        return importeRecibido;
    }

    public void setImporteRecibido(BigDecimal importeRecibido) {
        this.importeRecibido = (importeRecibido != null) ? importeRecibido : BigDecimal.ZERO;
    }

    public MetodoPago getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(MetodoPago metodoPago) {
        this.metodoPago = metodoPago;
    }

    public boolean isPagoExacto() {
        return pagoExacto;
    }

    public void setPagoExacto(boolean pagoExacto) {
        this.pagoExacto = pagoExacto;
    }

    
    public boolean hasNombrePedido() {
        return nombrePedido != null && !nombrePedido.isBlank();
    }

    
    public boolean hasMetodoPago() {
        return metodoPago != null;
    }
    public boolean hasTipoServicio() {
    	return tipoServicio !=null;
    	
    }
    
    public BigDecimal calcularCambio() {
        if (importeRecibido == null || total == null) {
            return BigDecimal.ZERO;
        }

        if (importeRecibido.compareTo(total) <= 0) {
            return BigDecimal.ZERO;
        }

        return importeRecibido.subtract(total);
    }
}
