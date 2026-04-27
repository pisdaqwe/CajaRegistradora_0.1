package dtoS;

public class SistemaTecnicoInfoDTO {

    private String appName;
    private String appVersion;

    private int terminalIdCaja;

    private String dbUrl;
    private String dbUser;
    private boolean conexionBdOk;

    private String logsPath;
    private String ticketsPath;
    private String reportsPath;

    private String javaVersion;
    private String sistemaOperativo;
    private String usuarioSistema;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public int getTerminalIdCaja() {
        return terminalIdCaja;
    }

    public void setTerminalIdCaja(int terminalIdCaja) {
        this.terminalIdCaja = terminalIdCaja;
    }

    public String getDbUrl() {
        return dbUrl;
    }

    public void setDbUrl(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    public String getDbUser() {
        return dbUser;
    }

    public void setDbUser(String dbUser) {
        this.dbUser = dbUser;
    }

    public boolean isConexionBdOk() {
        return conexionBdOk;
    }

    public void setConexionBdOk(boolean conexionBdOk) {
        this.conexionBdOk = conexionBdOk;
    }

    public String getLogsPath() {
        return logsPath;
    }

    public void setLogsPath(String logsPath) {
        this.logsPath = logsPath;
    }

    public String getTicketsPath() {
        return ticketsPath;
    }

    public void setTicketsPath(String ticketsPath) {
        this.ticketsPath = ticketsPath;
    }

    public String getReportsPath() {
        return reportsPath;
    }

    public void setReportsPath(String reportsPath) {
        this.reportsPath = reportsPath;
    }

    public String getJavaVersion() {
        return javaVersion;
    }

    public void setJavaVersion(String javaVersion) {
        this.javaVersion = javaVersion;
    }

    public String getSistemaOperativo() {
        return sistemaOperativo;
    }

    public void setSistemaOperativo(String sistemaOperativo) {
        this.sistemaOperativo = sistemaOperativo;
    }

    public String getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(String usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }
}
