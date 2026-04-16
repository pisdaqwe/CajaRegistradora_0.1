package service;

import facade.CajaFacade;
import facade.DevolucionFacade;
import facade.FichajeFacade;
import facade.MermaFacade;
import facade.VentaFacade;
import model.ComboDefinition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dao.InformesDao;

/**
 * Contenedor central de servicios de la aplicación.
 *
 * Objetivo:
 * - agrupar todas las dependencias de negocio en un único objeto
 * - pasarlo fácilmente a pantallas como VentasFrame y diálogos
 * - evitar que las pantallas creen servicios o DAOs por su cuenta
 *
 * Ajustes incluidos:
 * - soporte para combos automáticos
 * - cache en memoria de combos activos
 * - soporte para devoluciones
 * - soporte para lectura de ticket de devolución
 * - soporte para merma
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
    // 5) SERVICIOS DE DESCUENTOS
    // =====================================================

    public final DescuentoService descuentoService;

    // =====================================================
    // 6) SERVICIOS DE DEVOLUCIONES
    // =====================================================

    public final DevolucionService devolucionService;
    public final DevolucionFacade devolucionFacade;

    /**
     * Nuevo servicio lector del ticket de devolución.
     *
     * Uso previsto:
     * - abrir vista previa del ticket de devolución
     * - reutilizar desde diálogos/UI sin parsear JSON en pantalla
     */
    public final DevolucionTicketService devolucionTicketService;

    // =====================================================
    // 7) SERVICIOS DE MERMA
    // =====================================================

    /**
     * Facade principal del caso de uso de merma.
     *
     * Uso previsto:
     * - registrar mermas desde VentasFrame en ModoOperacion.MERMA
     * - reutilizar la misma arquitectura UI -> Facade -> Service -> DAO
     * 
     */
    public final MermaFacade mermaFacade;
    
    // =====================================================
    // 8) SERVICIOS DE INFORMES
    // =====================================================

   
    public final InformesService informesService;

    // =====================================================
    // 9) CONSTRUCTOR
    // =====================================================

    public AppServices(
            AuthService authService,
            FichajeFacade fichajeFacade,
            FichajeService fichajeService,
            SesionCajaService sesionCajaService,
            UsuarioRecordadoService usuarioRecordadoService,
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

            DescuentoService descuentoService,

            DevolucionService devolucionService,
            DevolucionFacade devolucionFacade,
            DevolucionTicketService devolucionTicketService,

            MermaFacade mermaFacade,
            InformesService informesService
    ) {
        // -----------------------------
        // 1) Servicios generales
        // -----------------------------
        this.authService = authService;
        this.fichajeFacade = fichajeFacade;
        this.fichajeService = fichajeService;
        this.sesionCajaService = sesionCajaService;
        this.usuarioRecordadoService = usuarioRecordadoService;
        this.cajaFacade = cajaFacade;
        this.usuarioService = usuarioService;

        // -----------------------------
        // 2) Servicios de ventas
        // -----------------------------
        this.catalogoService = catalogoService;
        this.productoPersonalizacionService = productoPersonalizacionService;
        this.ventaFacade = ventaFacade;
        this.colaImpresionService = colaImpresionService;
        this.ticketClienteService = ticketClienteService;

        // -----------------------------
        // 3) Servicios de disponibilidad / stock
        // -----------------------------
        this.disponibilidadProductoService = disponibilidadProductoService;
        this.disponibilidadExtraService = disponibilidadExtraService;

        // -----------------------------
        // 4) Servicios de combos
        // -----------------------------
        this.comboService = comboService;
        this.comboMatcherService = comboMatcherService;

        // Cargar combos activos al crear AppServices
        reloadCombosActivosCache();

        // -----------------------------
        // 5) Servicios de descuentos
        // -----------------------------
        this.descuentoService = descuentoService;

        // -----------------------------
        // 6) Servicios de devoluciones
        // -----------------------------
        this.devolucionService = devolucionService;
        this.devolucionFacade = devolucionFacade;
        this.devolucionTicketService = devolucionTicketService;

        // -----------------------------
        // 7) Servicios de merma
        // -----------------------------
        this.mermaFacade = mermaFacade;
        // -----------------------------
        // 8) Servicios de merma
        // -----------------------------
        this.informesService = informesService;
    }

    // =====================================================
    // 9) MÉTODOS DE SOPORTE PARA COMBOS
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