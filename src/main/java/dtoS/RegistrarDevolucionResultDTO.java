package dtoS;

import java.math.BigDecimal;

/**
 * Resultado final del registro de una devolución.
 */
public class RegistrarDevolucionResultDTO {

    private int idDevolucion;
    private int idVentaOriginal;
    private BigDecimal importeTotalDevuelto;
    private String metodoReembolso;
    private boolean ticketGenerado;

    public int getIdDevolucion() {
        return idDevolucion;
    }

    public void setIdDevolucion(int idDevolucion) {
        this.idDevolucion = idDevolucion;
    }

    public int getIdVentaOriginal() {
        return idVentaOriginal;
    }

    public void setIdVentaOriginal(int idVentaOriginal) {
        this.idVentaOriginal = idVentaOriginal;
    }

    public BigDecimal getImporteTotalDevuelto() {
        return importeTotalDevuelto;
    }

    public void setImporteTotalDevuelto(BigDecimal importeTotalDevuelto) {
        this.importeTotalDevuelto = importeTotalDevuelto;
    }

    public String getMetodoReembolso() {
        return metodoReembolso;
    }

    public void setMetodoReembolso(String metodoReembolso) {
        this.metodoReembolso = metodoReembolso;
    }

    public boolean isTicketGenerado() {
        return ticketGenerado;
    }

    public void setTicketGenerado(boolean ticketGenerado) {
        this.ticketGenerado = ticketGenerado;
    }
}
