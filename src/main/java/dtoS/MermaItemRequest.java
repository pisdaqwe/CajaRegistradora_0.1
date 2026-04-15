package dtoS;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa una línea individual dentro de una merma.
 *
 * Esta clase describe exactamente qué producto se ha perdido
 * y cómo estaba configurado en el momento de la merma.
 *
 * Puede usarse para dos casos:
 *
 * 1) Producto terminado que usa receta
 *    Ejemplo:
 *    - Latte Grande con avena y vainilla
 *
 * 2) Producto que no usa receta
 *    Ejemplo:
 *    - botella empaquetada
 *    - merch
 *    - producto retail
 *
 * IMPORTANTE:
 * - si usarReceta = true, el backend resolverá receta final
 *   y descontará ingredientes
 * - si usarReceta = false, el backend podrá descontar stock_producto
 */
public class MermaItemRequest {

    /**
     * Producto afectado por la merma.
     */
    private int idProducto;

    /**
     * Nombre visible del producto en el momento de registrar la merma.
     * Sirve como snapshot para trazabilidad.
     */
    private String nombreProductoSnapshot;

    /**
     * Tamaño seleccionado, si aplica.
     * Puede ser null en productos sin tamaño.
     */
    private Integer idTamano;

    /**
     * Nombre visible del tamaño.
     */
    private String nombreTamanoSnapshot;

    /**
     * Tipo de café seleccionado, si aplica.
     */
    private Integer idTipoCafeSeleccionado;

    /**
     * Nombre visible del tipo de café.
     */
    private String nombreTipoCafeSnapshot;

    /**
     * Ingrediente real asociado al tipo de café seleccionado.
     *
     * Esto es importante para RecipeResolverService.
     */
    private Integer idIngredienteTipoCafeSeleccionado;

    /**
     * Suplemento económico del café elegido.
     *
     * Aunque merma no cobre, puede ser útil para snapshot/auditoría.
     */
    private BigDecimal suplementoTipoCafe = BigDecimal.ZERO;

    /**
     * Cantidad de producto mermado.
     *
     * En bebidas/comida construida normalmente será 1.
     * Se deja preparado para casos futuros.
     */
    private BigDecimal cantidad = BigDecimal.ONE;

    /**
     * Indica si esta línea debe usar receta.
     *
     * true  -> resolver receta final y descontar ingredientes
     * false -> no usar receta; normalmente se descontará stock_producto
     */
    private boolean usarReceta;

    /**
     * Extras seleccionados en esta línea.
     */
    private List<MermaExtraRequest> extras = new ArrayList<>();

    /**
     * Personalizaciones seleccionadas en esta línea.
     */
    private List<MermaPersonalizacionRequest> personalizaciones = new ArrayList<>();

    /**
     * Notas tipo Ask Me asociadas al item.
     */
    private List<String> askMes = new ArrayList<>();

    /**
     * JSON de configuración preparado para persistir en merma_item.configuracion_json.
     *
     * Este campo puede construirse en la UI o en un mapper.
     */
    private String configuracionJson;

    /**
     * Texto legible preparado para persistir en merma_item.descripcion_snapshot.
     *
     * Ejemplo:
     * "Latte Grande + Avena + Vainilla + Extra Shot"
     */
    private String descripcionSnapshot;

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombreProductoSnapshot() {
        return nombreProductoSnapshot;
    }

    public void setNombreProductoSnapshot(String nombreProductoSnapshot) {
        this.nombreProductoSnapshot = nombreProductoSnapshot;
    }

    public Integer getIdTamano() {
        return idTamano;
    }

    public void setIdTamano(Integer idTamano) {
        this.idTamano = idTamano;
    }

    public String getNombreTamanoSnapshot() {
        return nombreTamanoSnapshot;
    }

    public void setNombreTamanoSnapshot(String nombreTamanoSnapshot) {
        this.nombreTamanoSnapshot = nombreTamanoSnapshot;
    }

    public Integer getIdTipoCafeSeleccionado() {
        return idTipoCafeSeleccionado;
    }

    public void setIdTipoCafeSeleccionado(Integer idTipoCafeSeleccionado) {
        this.idTipoCafeSeleccionado = idTipoCafeSeleccionado;
    }

    public String getNombreTipoCafeSnapshot() {
        return nombreTipoCafeSnapshot;
    }

    public void setNombreTipoCafeSnapshot(String nombreTipoCafeSnapshot) {
        this.nombreTipoCafeSnapshot = nombreTipoCafeSnapshot;
    }

    public Integer getIdIngredienteTipoCafeSeleccionado() {
        return idIngredienteTipoCafeSeleccionado;
    }

    public void setIdIngredienteTipoCafeSeleccionado(Integer idIngredienteTipoCafeSeleccionado) {
        this.idIngredienteTipoCafeSeleccionado = idIngredienteTipoCafeSeleccionado;
    }

    public BigDecimal getSuplementoTipoCafe() {
        return suplementoTipoCafe != null ? suplementoTipoCafe : BigDecimal.ZERO;
    }

    public void setSuplementoTipoCafe(BigDecimal suplementoTipoCafe) {
        this.suplementoTipoCafe = suplementoTipoCafe != null ? suplementoTipoCafe : BigDecimal.ZERO;
    }

    public BigDecimal getCantidad() {
        return cantidad != null ? cantidad : BigDecimal.ONE;
    }

    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad != null ? cantidad : BigDecimal.ONE;
    }

    public boolean isUsarReceta() {
        return usarReceta;
    }

    public void setUsarReceta(boolean usarReceta) {
        this.usarReceta = usarReceta;
    }

    public List<MermaExtraRequest> getExtras() {
        return extras;
    }

    public void setExtras(List<MermaExtraRequest> extras) {
        this.extras = extras != null ? extras : new ArrayList<>();
    }

    public List<MermaPersonalizacionRequest> getPersonalizaciones() {
        return personalizaciones;
    }

    public void setPersonalizaciones(List<MermaPersonalizacionRequest> personalizaciones) {
        this.personalizaciones = personalizaciones != null ? personalizaciones : new ArrayList<>();
    }

    public List<String> getAskMes() {
        return askMes;
    }

    public void setAskMes(List<String> askMes) {
        this.askMes = askMes != null ? askMes : new ArrayList<>();
    }

    public String getConfiguracionJson() {
        return configuracionJson;
    }

    public void setConfiguracionJson(String configuracionJson) {
        this.configuracionJson = configuracionJson;
    }

    public String getDescripcionSnapshot() {
        return descripcionSnapshot;
    }

    public void setDescripcionSnapshot(String descripcionSnapshot) {
        this.descripcionSnapshot = descripcionSnapshot;
    }
}