package service;

import facade.CajaFacade;
import facade.FichajeFacade;
import facade.VentaFacade;
import model.ComboDefinition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Contenedor central de servicios de la aplicación.
 *
 * Objetivo:
 * - Agrupar todas las dependencias de negocio en un único objeto
 * - Pasarlo fácilmente a pantallas como VentasFrame
 * - Evitar que las pantallas creen servicios o DAOs por su cuenta
 *
 * Ajuste añadido:
 * - soporte para combos automáticos
 * - cache en memoria de combos activos
 */
public class AppServices {

    // =====================================================
    // 1) SERVICIOS GENERALES DE LA APP
    // =====================================================

    public final AuthService authService;
    public final FichajeFacade fichajeFacade;
    public final FichajeService fichajeService;
    public final SesionCajaService sesionCajaService;
    public final UsuarioRecordadoService usuarioRecordadoService;
    public final CajaFacade cajaFacade;
    public final UsuarioService usuarioService;

    // =====================================================
    // 2) SERVICIOS DEL MÓDULO DE VENTAS
    // =====================================================

    public final CatalogoService catalogoService;
    public final ProductoPersonalizacionService productoPersonalizacionService;
    public final VentaFacade ventaFacade;
    public final ColaImpresionService colaImpresionService;
    public final TicketClienteService ticketClienteService;
    
    // =====================================================
    // 3) SERVICIOS DE DISPONIBILIDAD / STOCK
    // =====================================================

    public final DisponibilidadProductoService disponibilidadProductoService;
    public final DisponibilidadExtraService disponibilidadExtraService;

    // =====================================================
    // 4) SERVICIOS DE COMBOS
    // =====================================================

    /**
     * Servicio que carga desde BD las definiciones de combos activas.
     */
    public final ComboService comboService;

    /**
     * Servicio que detecta automáticamente combos aplicables sobre el ticket.
     */
    public final ComboMatcherService comboMatcherService;

    /**
     * Cache en memoria de combos activos.
     *
     * Se carga al iniciar AppServices y se reutiliza durante la sesión
     * para no consultar la BD en cada cambio del ticket.
     */
    private final List<ComboDefinition> combosActivosCache = new ArrayList<>();
    
    // =====================================================
    // 5) SERVICIOS DE COMBOS
    // =====================================================
    public DescuentoService descuentoService;

    // =====================================================
    // 6) CONSTRUCTOR
    // =====================================================

    public AppServices(
            AuthService authService,
            FichajeService fichajeService,
            SesionCajaService sesionCajaService,
            UsuarioRecordadoService usuarioRecordadoService,
            FichajeFacade fichajeFacade,
            CajaFacade cajaFacade,
            UsuarioService usuarioService,
            CatalogoService catalogoService,
            ProductoPersonalizacionService productoPersonalizacionService,
            VentaFacade ventaFacade,
            ColaImpresionService colaImpresionService,
            TicketClienteService ticketClienteService,
            DisponibilidadProductoService disponibilidadProductoService,
            DisponibilidadExtraService disponibilidadExtraService,
            ComboService comboService,
            ComboMatcherService comboMatcherService,
            DescuentoService descuentoService
    ) {
        // -----------------------------
        // Servicios generales
        // -----------------------------
        this.authService = authService;
        this.fichajeFacade = fichajeFacade;
        this.fichajeService = fichajeService;
        this.sesionCajaService = sesionCajaService;
        this.usuarioRecordadoService = usuarioRecordadoService;
        this.cajaFacade = cajaFacade;
        this.usuarioService = usuarioService;

        // -----------------------------
        // Servicios de ventas
        // -----------------------------
        this.catalogoService = catalogoService;
        this.productoPersonalizacionService = productoPersonalizacionService;
        this.ventaFacade = ventaFacade;
        this.colaImpresionService = colaImpresionService;
        this.ticketClienteService = ticketClienteService;

        // -----------------------------
        // Servicios de disponibilidad
        // -----------------------------
        this.disponibilidadProductoService = disponibilidadProductoService;
        this.disponibilidadExtraService = disponibilidadExtraService;

        // -----------------------------
        // Servicios de combos
        // -----------------------------
        this.comboService = comboService;
        this.comboMatcherService = comboMatcherService;

        // Cargar combos activos al crear AppServices
        reloadCombosActivosCache();
        
        this.descuentoService = descuentoService;
    }

    // =====================================================
    // 6) MÉTODOS DE SOPORTE PARA COMBOS
    // =====================================================

    /**
     * Recarga desde BD la cache de combos activos.
     *
     * Útil:
     * - al iniciar la app
     * - si en el futuro un admin cambia combos y quieres refrescarlos
     */
    public void reloadCombosActivosCache() {
        try {
            combosActivosCache.clear();
            combosActivosCache.addAll(comboService.loadCombosActivos());
        } catch (Exception e) {
            throw new RuntimeException("No se pudieron cargar los combos activos.", e);
        }
    }

    /**
     * Devuelve la cache de combos activos en modo solo lectura.
     */
    public List<ComboDefinition> getCombosActivosCache() {
        return Collections.unmodifiableList(combosActivosCache);
    }
}
