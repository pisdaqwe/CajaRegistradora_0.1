package ui.dialog;

import java.math.BigDecimal;

public class ConteoCajaDialogResult{
	BigDecimal cantidadContadaCaja;

	public ConteoCajaDialogResult(BigDecimal cantidad) {
		this.cantidadContadaCaja = cantidad;

	}

	public BigDecimal getCantidadContadaCaja() {
		return cantidadContadaCaja;
	}

	public void setCantidadContadaCaja(BigDecimal cantidadContadaCaja) {
		this.cantidadContadaCaja = cantidadContadaCaja;
	}

}
