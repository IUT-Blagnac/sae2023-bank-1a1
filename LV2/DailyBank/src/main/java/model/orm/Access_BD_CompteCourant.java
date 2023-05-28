package model.orm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import application.tools.ConstantesIHM;
import model.data.Client;
import model.data.CompteCourant;
import model.orm.exception.DataAccessException;
import model.orm.exception.DatabaseConnexionException;
import model.orm.exception.ManagementRuleViolation;
import model.orm.exception.Order;
import model.orm.exception.RowNotFoundOrTooManyRowsException;
import model.orm.exception.Table;

/**
 *
 * Classe d'accès aux CompteCourant en BD Oracle.
 *
 */
public class Access_BD_CompteCourant {

	public Access_BD_CompteCourant() {
	}

	/**
	 * Recherche des CompteCourant d'un client à partir de son id.
	 *
	 * @param idNumCli id du client dont on cherche les comptes
	 * @return Tous les CompteCourant de idNumCli (ou liste vide)
	 * @throws DataAccessException        Erreur d'accès aux données (requête mal
	 *                                    formée ou autre)
	 * @throws DatabaseConnexionException Erreur de connexion
	 */
	public ArrayList<CompteCourant> getCompteCourants(int idNumCli)
			throws DataAccessException, DatabaseConnexionException {

		ArrayList<CompteCourant> alResult = new ArrayList<>();

		try {
			Connection con = LogToDatabase.getConnexion();
			String query = "SELECT * FROM CompteCourant where idNumCli = ?";
			query += " ORDER BY idNumCompte";

			PreparedStatement pst = con.prepareStatement(query);
			pst.setInt(1, idNumCli);
			System.err.println(query);

			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				int idNumCompte = rs.getInt("idNumCompte");
				int debitAutorise = rs.getInt("debitAutorise");
				double solde = rs.getDouble("solde");
				String estCloture = rs.getString("estCloture");
				int idNumCliTROUVE = rs.getInt("idNumCli");

				alResult.add(new CompteCourant(idNumCompte, debitAutorise, solde, estCloture, idNumCliTROUVE));
			}
			rs.close();
			pst.close();
		} catch (SQLException e) {
			throw new DataAccessException(Table.CompteCourant, Order.SELECT, "Erreur accès", e);
		}

		return alResult;
	}

	/**
	 * Recherche d'un CompteCourant à partir de son id (idNumCompte).
	 *
	 * @param idNumCompte id du compte (clé primaire)
	 * @return Le compte ou null si non trouvé
	 * @throws RowNotFoundOrTooManyRowsException La requête renvoie plus de 1 ligne
	 * @throws DataAccessException               Erreur d'accès aux données (requête
	 *                                           mal formée ou autre)
	 * @throws DatabaseConnexionException        Erreur de connexion
	 */
	public CompteCourant getCompteCourant(int idNumCompte)
			throws RowNotFoundOrTooManyRowsException, DataAccessException, DatabaseConnexionException {
		try {
			CompteCourant cc;

			Connection con = LogToDatabase.getConnexion();

			String query = "SELECT * FROM CompteCourant where" + " idNumCompte = ?";

			PreparedStatement pst = con.prepareStatement(query);
			pst.setInt(1, idNumCompte);

			System.err.println(query);

			ResultSet rs = pst.executeQuery();

			if (rs.next()) {
				int idNumCompteTROUVE = rs.getInt("idNumCompte");
				int debitAutorise = rs.getInt("debitAutorise");
				double solde = rs.getDouble("solde");
				String estCloture = rs.getString("estCloture");
				int idNumCliTROUVE = rs.getInt("idNumCli");

				cc = new CompteCourant(idNumCompteTROUVE, debitAutorise, solde, estCloture, idNumCliTROUVE);
			} else {
				rs.close();
				pst.close();
				return null;
			}

			if (rs.next()) {
				throw new RowNotFoundOrTooManyRowsException(Table.CompteCourant, Order.SELECT,
						"Recherche anormale (en trouve au moins 2)", null, 2);
			}
			rs.close();
			pst.close();
			return cc;
		} catch (SQLException e) {
			throw new DataAccessException(Table.CompteCourant, Order.SELECT, "Erreur accès", e);
		}
	}

	/**
	 * Mise à jour d'un CompteCourant.
	 *
	 * cc.idNumCompte (clé primaire) doit exister seul cc.debitAutorise est mis à
	 * jour cc.solde non mis à jour (ne peut se faire que par une opération)
	 * cc.idNumCli non mis à jour (un cc ne change pas de client)
	 *
	 * @param cc IN cc.idNumCompte (clé primaire) doit exister seul
	 * @throws RowNotFoundOrTooManyRowsException La requête modifie 0 ou plus de 1
	 *                                           ligne
	 * @throws DataAccessException               Erreur d'accès aux données (requête
	 *                                           mal formée ou autre)
	 * @throws DatabaseConnexionException        Erreur de connexion
	 * @throws ManagementRuleViolation           Erreur sur le solde courant par
	 *                                           rapport au débitAutorisé (solde <
	 *                                           débitAutorisé)
	 */
	public void updateCompteCourant(CompteCourant cc) throws RowNotFoundOrTooManyRowsException, DataAccessException,
			DatabaseConnexionException, ManagementRuleViolation {
		try {

			CompteCourant cAvant = this.getCompteCourant(cc.idNumCompte);
			if (cc.debitAutorise > 0) {
				cc.debitAutorise = -cc.debitAutorise;
			}
			if (cAvant.solde < cc.debitAutorise) {
				throw new ManagementRuleViolation(Table.CompteCourant, Order.UPDATE,
						"Erreur de règle de gestion : sole à découvert", null);
			}
			Connection con = LogToDatabase.getConnexion();

			String query = "UPDATE CompteCourant SET " + "debitAutorise = ? " + "WHERE idNumCompte = ?";

			PreparedStatement pst = con.prepareStatement(query);
			pst.setInt(1, cc.debitAutorise);
			pst.setInt(2, cc.idNumCompte);

			System.err.println(query);

			int result = pst.executeUpdate();
			pst.close();
			if (result != 1) {
				con.rollback();
				throw new RowNotFoundOrTooManyRowsException(Table.CompteCourant, Order.UPDATE,
						"Update anormal (update de moins ou plus d'une ligne)", null, result);
			}
			con.commit();
		} catch (SQLException e) {
			throw new DataAccessException(Table.CompteCourant, Order.UPDATE, "Erreur accès", e);
		}
	}

	/**
	 *
	 * Cette méthode permet d'insérer un nouveau compte courant dans la base de
	 * données.
	 *
	 * @param cc Le compte courant à insérer.
	 * @throws RowNotFoundOrTooManyRowsException si l'insertion ne concerne pas une
	 *                                           seule ligne.
	 * @throws DataAccessException               si une erreur d'accès à la base de
	 *                                           données se produit.
	 */
	public void insertCompteC(CompteCourant cc)
			throws RowNotFoundOrTooManyRowsException, DataAccessException, DatabaseConnexionException {
		try {
			if (cc.debitAutorise > 0) {
				cc.debitAutorise = -cc.debitAutorise;
			}
			Connection con = LogToDatabase.getConnexion();
			String query = "INSERT INTO CompteCourant (idNumCompte, debitAutorise, solde, idNumCli, estCloture) VALUES (seq_id_compte.NEXTVAL, ?, ?, ?, ?)";
			PreparedStatement pst = con.prepareStatement(query);

			pst.setInt(1, cc.debitAutorise);
			pst.setDouble(2, cc.solde);
			pst.setInt(3, cc.idNumCli);
			pst.setString(4, cc.estCloture);

			System.err.println(query);
			int result = pst.executeUpdate();
			pst.close();
			if (result != 1) {
				con.rollback();
				throw new RowNotFoundOrTooManyRowsException(Table.CompteCourant, Order.INSERT,
						"Insert anormal (insert de moins ou plus d'une ligne)", null, result);
			}

			query = "SELECT seq_id_compte.CURRVAL from DUAL";
			System.err.println(query);
			PreparedStatement pst2 = con.prepareStatement(query);
			ResultSet rs = pst2.executeQuery();
			rs.next();
			int numCliBase = rs.getInt(1);
			con.commit();
			rs.close();
			pst2.close();
			cc.idNumCompte = numCliBase;
		} catch (SQLException e) {
			throw new DataAccessException(Table.CompteCourant, Order.SELECT, "Erreur accès", e);
		}
	}

	/**
	 * Cette méthode permet de clôturer un compte courant dans la base de données.
	 *
	 * @param numCompte Le numéro du compte à clôturer.
	 * @throws DataAccessException               si une erreur d'accès à la base de
	 *                                           données se produit.
	 * @throws RowNotFoundOrTooManyRowsException
	 * @throws DatabaseConnexionException
	 * @author Bilon
	 */
	public void cloturerCompte(int numCompte)
			throws DataAccessException, RowNotFoundOrTooManyRowsException, DatabaseConnexionException {
		try {
			Connection con = LogToDatabase.getConnexion();
			String query = "UPDATE CompteCourant SET estCloture = 'O' WHERE idNumCompte = ?";
			PreparedStatement pst = con.prepareStatement(query);

			pst.setInt(1, numCompte);

			int result = pst.executeUpdate();
			pst.close();

			if (result != 1) {
				con.rollback();
				throw new RowNotFoundOrTooManyRowsException(Table.CompteCourant, Order.UPDATE,
						"Update anormal (update de moins ou plus d'une ligne)", null, result);
			}
			con.commit();
		} catch (SQLException e) {
			throw new DataAccessException(Table.CompteCourant, Order.UPDATE, "Erreur accès", e);
		}
	}

	/**
	 * Cette méthode permet de supprimer un compte courant clôturé dans la base de
	 * données.
	 *
	 * @param compteSup Le compte à supprimer.
	 * @throws DataAccessException               si une erreur d'accès à la base de
	 *                                           données se produit.
	 * @throws RowNotFoundOrTooManyRowsException
	 * @throws DatabaseConnexionException
	 * @author Illan GABARRA
	 */
	public void supprimerCompte(CompteCourant compteSup)
			throws DataAccessException, RowNotFoundOrTooManyRowsException, DatabaseConnexionException {
		try {

			Access_BD_Operation accOperation = new Access_BD_Operation();

			accOperation.suppressionOperations(compteSup.idNumCompte);

			Connection con = LogToDatabase.getConnexion();

			String query = "DELETE CompteCourant WHERE idNumCompte = ?";

			System.err.println(query);
			PreparedStatement pst = con.prepareStatement(query);
			pst.setInt(1, compteSup.idNumCompte);

			int result = pst.executeUpdate();
			pst.close();

			if (result != 1) {
				con.rollback();
				throw new RowNotFoundOrTooManyRowsException(Table.CompteCourant, Order.DELETE, "Suppression anormal ",
						null, result);
			}
			con.commit();
		} catch (SQLException e) {
			throw new DataAccessException(Table.CompteCourant, Order.UPDATE, "Erreur accès", e);
		}
	}
	// /**
	// * Cette méthode permet de clôturer un compte courant dans la base de données.
	// * @param numCompte Le numéro du compte à clôturer.
	// * @throws DataAccessException si une erreur d'accès à la base de données se
	// produit.
	// * @throws RowNotFoundOrTooManyRowsException
	// * @throws DatabaseConnexionException
	// * @author Bilon Kwadjani
	// */
	// public void reOuvrirCompte(int numCompte) throws DataAccessException,
	// RowNotFoundOrTooManyRowsException, DatabaseConnexionException {
	// try {
	// Connection con = LogToDatabase.getConnexion();
	// String query = "UPDATE CompteCourant SET estCloture = 'N' WHERE idNumCompte =
	// ?";
	// PreparedStatement pst = con.prepareStatement(query);
	//
	// pst.setInt(1, numCompte);
	//
	// int result = pst.executeUpdate();
	// pst.close();
	//
	// if (result != 1) {
	// con.rollback();
	// throw new RowNotFoundOrTooManyRowsException(Table.CompteCourant,
	// Order.UPDATE, "Update anormal (update de moins ou plus d'une ligne)", null,
	// result);
	// }
	// con.commit();
	// } catch (SQLException e) {
	// throw new DataAccessException(Table.CompteCourant, Order.UPDATE, "Erreur
	// accès", e);
	// }
	// }

	/**
	 * Vérifie si le compte clientADesactiver est désactivable
	 *
	 * @author illan
	 *
	 * @param clientADesactiver
	 *
	 * @return
	 */
	public boolean isDesactivable(Client clientADesactiver) {

		if (!ConstantesIHM.estActif(clientADesactiver)) {
			return false;
		}

		try {
			for (CompteCourant compteDuClient : this.getCompteCourants(clientADesactiver.idNumCli)) {
				if (compteDuClient.estCloture.equals("N")) {
					return false;
				}
			}
		} catch (DataAccessException | DatabaseConnexionException e1) {
			e1.printStackTrace();
			return false;
		}

		return true;
	}
}
