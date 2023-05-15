package model.orm;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;

import model.orm.exception.DatabaseConnexionException;

/*
 * ---------------------------------------------------------------------
 * Code source à modifier pour user/mdp/hote/port/SID (attributs privés)
 * ---------------------------------------------------------------------
 */

/**
 * Classe qui gère la connexion à la BD
 *
 */
public class LogToDatabase {

	// Gloabl : accès à la base de donnée : à ajuster selon le compte
	private static final String user = "G1A1S2";
	private static final String passwd = "BMI23iut-tlse2";

	private static final String hoteOracle = "oracle.iut-blagnac.fr";
	private static final String portOracle = "1521";
	private static final String SIDOracle = "db11g";

	// Local
	private static Connection currentConnexion = null;

	/*
	 * Load du driver. Code dit "static" (hors d'une méthode) => exécuté au
	 * chargement de la classe. Permet de retrouver le diver => arrêt si driver non
	 * trouvé.
	 */
	{
		// driver oracle
		String nomDriver = "oracle.jdbc.driver.OracleDriver";

		try {
			Class.forName(nomDriver);
		} catch (ClassNotFoundException cnfe) {
			System.err.println("La classe " + nomDriver + " n'a pas été trouvée");
			System.exit(0);
		}
	}

	/**
	 * Méthode statique pour demander une connexion à la BD.
	 *
	 * Retourne la dernière connexion active ou une nouvelle connexion si besoin.
	 * Connexion paramétrée SANS COMMIT AUTOMATIQUE.
	 *
	 * Appel : Connection c = LogToDatabase.getConnexion();
	 *
	 * @return Une connexion à la base de donnée SANS commit automatique
	 * @throws DatabaseConnexionException Erreur de connexion
	 */
	public static Connection getConnexion() throws DatabaseConnexionException {

		if (LogToDatabase.currentConnexion != null) {
			try {
				if (LogToDatabase.currentConnexion.isValid(0)) {
					return LogToDatabase.currentConnexion;
				}
			} catch (SQLException e) {
				// Let's continue to build a new connexion
			}
			try {
				LogToDatabase.currentConnexion.close();
			} catch (SQLException e) {
				// Let's continue to build a new connexion
			}
			LogToDatabase.currentConnexion = null;
		}

		String url = "jdbc:oracle:thin:" + "@" + LogToDatabase.hoteOracle + ":" + LogToDatabase.portOracle + ":"
				+ LogToDatabase.SIDOracle;

		try {
			LogToDatabase.currentConnexion = DriverManager.getConnection(url, LogToDatabase.user, LogToDatabase.passwd);
			LogToDatabase.currentConnexion.setAutoCommit(false);
		} catch (SQLTimeoutException e) {
			LogToDatabase.currentConnexion = null;
			throw new DatabaseConnexionException("Timeout sur connexion", e);
		} catch (SQLException e) {
			LogToDatabase.currentConnexion = null;
			throw new DatabaseConnexionException("Connexion Impossible", e);
		}

		return LogToDatabase.currentConnexion;
	}

	/**
	 * Méthode statique pour fermer une connexion.
	 *
	 * Nécessaire pour laisser le système "propre" (fermeture des connexions réseau,
	 * libération mémoire temporaires des drivers, ...).
	 *
	 * @throws DatabaseConnexionException Erreur de connexion
	 */
	public static void closeConnexion() throws DatabaseConnexionException {

		if (LogToDatabase.currentConnexion != null) {
			try {
				LogToDatabase.currentConnexion.close();
				LogToDatabase.currentConnexion = null;
			} catch (SQLException e) {
				throw new DatabaseConnexionException("Fermeture Connexion Impossible", e);
			}
		}
	}
}
