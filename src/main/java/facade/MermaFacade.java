package facade;

import dtoS.MermaRequest;
import dtoS.MermaResultDTO;
import service.MermaService;

/**
 * Facade del caso de uso de merma.
 *
 * RESPONSABILIDAD:
 * - exponer un punto de entrada simple para la UI
 * - delegar en MermaService
 *
 * IMPORTANTE:
 * - no contiene lógica de negocio
 * - no accede a DAOs
 * - no hace validaciones complejas
 *
 * Arquitectura:
 * UI -> Facade -> Service -> DAO
 */
public class MermaFacade {

    private final MermaService mermaService;

    public MermaFacade(MermaService mermaService) {
        if (mermaService == null) {
            throw new IllegalArgumentException("mermaService no puede ser null");
        }
        this.mermaService = mermaService;
    }

    /**
     * Registra una merma completa.
     */
    public MermaResultDTO registrarMerma(MermaRequest request) {
        return mermaService.registrarMerma(request);
    }
}