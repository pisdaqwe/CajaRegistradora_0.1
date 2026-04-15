// =====================================================
//MermaDialogResult.java
//Sustituir la clase actual por esta versión simplificada
//=====================================================

package ui.dialog;

public class MermaDialogResult {

 private final boolean confirmed;
 private final String motivo;
 private final String observaciones;

 private MermaDialogResult(boolean confirmed, String motivo, String observaciones) {
     this.confirmed = confirmed;
     this.motivo = motivo;
     this.observaciones = observaciones;
 }

 public static MermaDialogResult confirmed(String motivo, String observaciones) {
     return new MermaDialogResult(true, motivo, observaciones);
 }

 public static MermaDialogResult cancelled() {
     return new MermaDialogResult(false, null, null);
 }

 public boolean isConfirmed() {
     return confirmed;
 }

 public String getMotivo() {
     return motivo;
 }

 public String getObservaciones() {
     return observaciones;
 }
}