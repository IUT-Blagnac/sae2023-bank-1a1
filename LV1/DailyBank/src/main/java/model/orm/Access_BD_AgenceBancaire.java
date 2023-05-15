package model.orm;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import model.data.AgenceBancaire;
import model.orm.exception.DataAccessException;
import model.orm.exception.DatabaseConnexionException;
import model.orm.exception.Order;
import model.orm.exception.RowNotFoundOrTooManyRowsException;
import model.orm.exception.Table;

/*
 * Attention :
 * -----------
 * Classe programmée avec des objets de type Statement prenant en paramètre de leurs méthodes la chaine de caractères ("en dur") de la requête.
 * Fait ici uniquement pour montrer que le texte d'une instruction SQL tappée en intéractif dans sqlDeveloper (ou tout terminal SQL)
 * peut servir pour réaliser une interrogation de base de données depuis un langage de programmation.
 * On préfèrera les PreparedStatement (cf. autres classes).
 */

/**
 * Classe d'accès aux AgenceBancaire en BD Oracle.
 *
 */
public class Access_BD_AgenceBancaire {

	/**
	 * Recherche de toutes les agences bancaires.
	 *
	 * @return Liste des AgenceBancaire existantes
	 * @throws DataAccessException        Erreur d'accès aux données (requête mal
	 *                                    formée ou autre)
	 * @throws DatabaseConnexionException Erreur de connexion
	 */
	public ArrayList<AgenceBancaire> getAgenceBancaires() throws DataAccessException, DatabaseConnexionException {

		ArrayList<AgenceBancaire> alResult = new ArrayList<>();

		try {
			Connection con = LogToDatabase.getConnexion();
			Statement st = con.createStatement();
			String query = "SELECT * FROM AgenceBancaire ORDER BY nomAg";
			ResultSet rs = st.executeQuery(query);
			while (rs.next()) {
				int idAg = rs.getInt("idAg");
				String nomAg = rs.getString("nomAg");
				nomAg = (nomAg == null ? "" : nomAg);
				String adressePostaleAg = rs.getString("adressePostaleAg");
				adressePostaleAg = (adressePostaleAg == null ? "" : adressePostaleAg);
				int idEmployeChefAg = rs.getInt("idEmployeChefAg");

				alResult.add(new AgenceBancaire(idAg, nomAg, adressePostaleAg, idEmployeChefAg));
			}
			rs.close();
			st.close();
		} catch (SQLException e) {
			throw new DataAccessException(Table.AgenceBancaire, Order.SELECT, "Erreur accès AgenceBancaire", e);
		}

		return alResult;
	}

	/**
	 * Recherche AgenceBancaire par son id.
	 *
	 * @return une AgenceBancaire ou null si non trouvé
	 * @param idAg id de l'agence recherchée (clé primaire)
	 * @throws RowNotFoundOrTooManyRowsException La requête renvoie plus de 1 ligne
	 * @throws DataAccessException               Erreur d'accès aux données (requête
	 *                                           mal formée ou autre)
	 * @throws DatabaseConnexionException        Erreur de connexion
	 */
	public AgenceBancaire getAgenceBancaire(int idAg)
			throws DataAccessException, DatabaseConnexionException, RowNotFoundOrTooManyRowsException {

		AgenceBancaire a;

		try {
			Connection con = LogToDatabase.getConnexion();
			Statement st = con.createStatement();
			String query = "SELECT * FROM AgenceBancaire" + " where idAg = " + idAg;
			ResultSet rs = st.executeQuery(query);
			if (rs.next()) {
				int idAgTRouve = rs.getInt("idAg");
				String nomAg = rs.getString("nomAg");
				nomAg = (nomAg == null ? "" : nomAg);
				String adressePostaleAg = rs.getString("adressePostaleAg");
				adressePostaleAg = (adressePostaleAg == null ? "" : adressePostaleAg);
				int idEmployeChefAg = rs.getInt("idEmployeChefAg");

				a = new AgenceBancaire(idAgTRouve, nomAg, adressePostaleAg, idEmployeChefAg);
			} else {
				rs.close();
				st.close();
				return null;
			}

			if (rs.next()) {
				rs.close();
				st.close();
				throw new RowNotFoundOrTooManyRowsException(Table.AgenceBancaire, Order.SELECT,
						"Recherche anormale (en trouve au moins 2)", null, 2);
			}
			rs.close();
			st.close();

		} catch (SQLException e) {
			throw new DataAccessException(Table.AgenceBancaire, Order.SELECT, "Erreur accès AgenceBancaire", e);
		}

		return a;
	}
}
