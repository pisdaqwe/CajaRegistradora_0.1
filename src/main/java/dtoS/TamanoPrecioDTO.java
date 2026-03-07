package dtoS;

import java.math.BigDecimal;

public final class TamanoPrecioDTO {
	private final TamanoDTO tamanoDTO;
	private final BigDecimal precio ;
	
	 public TamanoPrecioDTO(TamanoDTO tamanoDTO, BigDecimal precio) {
		
		this.tamanoDTO = tamanoDTO;
		this.precio = precio;
		
	}

	 public TamanoDTO getTamanoDTO() {
		 return tamanoDTO;
	 }

	 public BigDecimal getPrecio() {
		 return precio;
	 } 
	 
}
