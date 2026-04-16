package ui.informes;

import dtoS.InformeFiltroDTO;
import enums.ModoVistaInforme;
import enums.TipoInforme;

public interface InformeFilterModule {

    void setTipoInforme(TipoInforme tipoInforme);

    void reset();

    ModoVistaInforme getModoVista();

    String buildSummary();

    InformeFiltroDTO buildFiltroDTO();
}
