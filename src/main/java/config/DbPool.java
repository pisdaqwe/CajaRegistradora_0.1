package config;

import java.sql.Connection;
import java.sql.SQLException;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public final class DbPool {
	
	private static HikariDataSource dataSource;
	private static boolean initialized;
	
	private DbPool () {
		
	}
	public static synchronized void init() {
		if(initialized){
			return;
			
		}
		try {
			HikariConfig hKconfig = new HikariConfig();
			 // --- Datos de conexión (desde ConfigLoader) ---
			hKconfig.setJdbcUrl(ConfigLoader.getDbUrl());
			hKconfig.setUsername(ConfigLoader.getDbUser());
			hKconfig.setPassword(ConfigLoader.getDbPassword());
			 // --- Configuración del pool ---
			hKconfig.setMaximumPoolSize(ConfigLoader.getDbPoolMaxSize());
			hKconfig.setMinimumIdle(ConfigLoader.getDbPoolMinIdle());
			hKconfig.setConnectionTimeout(ConfigLoader.getDbConnectionTimeout());
			hKconfig.setIdleTimeout(ConfigLoader.getDbIdleTimeout());
			hKconfig.setMaxLifetime(ConfigLoader.getDbMaxLifetime());
			
			hKconfig.setPoolName("TPV-HikariPool");
			hKconfig.setAutoCommit(true);
			
			dataSource = new HikariDataSource(hKconfig);
			initialized=true;
			
			System.out.print("[DB]Pool de conexiones inicializado");
			
			
		} catch (Exception e) {
			System.err.println("[ERROR] No se pudo inicializar el pool de conexiones (DbPool)");
			e.printStackTrace();
			System.exit(1);
		}
		
	}
		// =========================================
    	// OBTENER CONEXIÓN
		// =========================================
	public static Connection getConnection () throws SQLException {
		if(!initialized) {
			throw new IllegalStateException(
					"DbPool no inicializado. Llama al DbPool.init()"
			);

		}
		return dataSource.getConnection();
		
	}
	
    // =========================================
    // CIERRE DEL POOL
    // =========================================
	
	public static void shutdown() {
		if(dataSource!=null) {
			dataSource.close();
			System.out.print("[DB]Pool de conexiones cerrado");
			
		}
	}


}
