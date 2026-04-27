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
 * - soporte base para auditoría
 */
public class AppServices {

    // =====================================================
    // 1) SERVICIOS GENERALES DE LA APP
    // =====================================================
    public final SistemaTecnicoService sistemaTecnicoService;
    public final AuthService authService;
    public final FichajeFacade fichajeFacade;
    public final FichajeService fichajeService;
    public final SesionCajaService sesionCajaService;
    public final CajaFacade cajaFacade;
    public final UsuarioService usuarioService;
    public final AuditoriaService auditoriaService;

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
    public final ComboService comboService;
    public final ComboMatcherService comboMatcherService;

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
    public final DevolucionTicketService devolucionTicketService;

    // =====================================================
    // 7) SERVICIOS DE MERMA
    // =====================================================
    public final MermaFacade mermaFacade;

    // =====================================================
    // 8) SERVICIOS DE INFORMES / AUXILIARES
    // =====================================================
    public final InformesService informesService;
    public final InformePdfService informePdfService;
    public final SucursalService sucursalService;
    public final RolService rolService;

    // =====================================================
    // CONSTRUCTOR
    // =====================================================
    public AppServices(
            AuthService authService,
            FichajeFacade fichajeFacade,
            FichajeService fichajeService,
            SesionCajaService sesionCajaService,
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
            InformesService informesService,
            SucursalService sucursalService,
            RolService rolService,
            SistemaTecnicoService sistemaTecnicoService,
            AuditoriaService auditoriaService,
            InformePdfService informePdfService
    ) {
        // -----------------------------
        // 1) Servicios generales
        // -----------------------------
        this.authService = authService;
        this.fichajeFacade = fichajeFacade;
        this.fichajeService = fichajeService;
        this.sesionCajaService = sesionCajaService;
        this.cajaFacade = cajaFacade;
        this.usuarioService = usuarioService;
        this.sistemaTecnicoService = sistemaTecnicoService;
        this.auditoriaService = auditoriaService;

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
        // 8) Servicios de informes / auxiliares
        // -----------------------------
        this.informesService = informesService;
        this.sucursalService = sucursalService;
        this.rolService = rolService;
        this.informePdfService = informePdfService;
    }

    // =====================================================
    // SOPORTE PARA COMBOS
    // =====================================================
    public void reloadCombosActivosCache() {
        try {
            combosActivosCache.clear();
            combosActivosCache.addAll(comboService.loadCombosActivos());
        } catch (Exception e) {
            throw new RuntimeException("No se pudieron cargar los combos activos.", e);
        }
    }

    public List<ComboDefinition> getCombosActivosCache() {
        return Collections.unmodifiableList(combosActivosCache);
    }
}