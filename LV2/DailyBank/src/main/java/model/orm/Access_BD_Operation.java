package model.orm;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;

import model.data.Operation;
import model.orm.exception.DataAccessException;
import model.orm.exception.DatabaseConnexionException;
import model.orm.exception.ManagementRuleViolation;
import model.orm.exception.Order;
import model.orm.exception.RowNotFoundOrTooManyRowsException;
import model.orm.exception.Table;

/**
 * Classe d'accès aux Operation en BD Oracle.
 */
public class Access_BD_Operation {

	public Access_BD_Operation() {
	}

	/**
	 * Recherche de toutes les opérations d'un compte.
	 *
	 * @param idNumCompte id du compte dont on cherche toutes les opérations
	 * @return Toutes les opérations du compte, liste vide si pas d'opération
	 * @throws DataAccessException        Erreur d'accès aux données (requête mal
	 *                                    formée ou autre)
	 * @throws DatabaseConnexionException Erreur de connexion
	 */
	public ArrayList<Operation> getOperations(int idNumCompte) throws DataAccessException, DatabaseConnexionException {

		ArrayList<Operation> alResult = new ArrayList<>();

		try {
			Connection con = LogToDatabase.getConnexion();
			String query = "SELECT * FROM Operation where idNumCompte = ?";
			query += " ORDER BY dateOp";

			PreparedStatement pst = con.prepareStatement(query);
			pst.setInt(1, idNumCompte);

			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				int idOperation = rs.getInt("idOperation");
				double montant = rs.getDouble("montant");
				Date dateOp = rs.getDate("dateOp");
				Date dateValeur = rs.getDate("dateValeur");
				int idNumCompteTrouve = rs.getInt("idNumCompte");
				String idTypeOp = rs.getString("idTypeOp");

				alResult.add(new Operation(idOperation, montant, dateOp, dateValeur, idNumCompteTrouve, idTypeOp));
			}
			rs.close();
			pst.close();
			return alResult;
		} catch (SQLException e) {
			throw new DataAccessException(Table.Operation, Order.SELECT, "Erreur accès", e);
		}
	}









	/**
	 * Recherche d'une opération par son id.
	 *
	 * @param idOperation id de l'opération recherchée (clé primaire)
	 * @return une Operation ou null si non trouvé
	 * @throws RowNotFoundOrTooManyRowsException La requête renvoie plus de 1 ligne
	 * @throws DataAccessException               Erreur d'accès aux données (requête
	 *                                           mal formée ou autre)
	 * @throws DatabaseConnexionException        Erreur de connexion
	 */
	public Operation getOperation(int idOperation)
			throws RowNotFoundOrTooManyRowsException, DataAccessException, DatabaseConnexionException {

		Operation operationTrouvee;

		try {
			Connection con = LogToDatabase.getConnexion();
			String query = "SELECT * FROM Operation  where" + " idOperation = ?";

			PreparedStatement pst = con.prepareStatement(query);
			pst.setInt(1, idOperation);

			ResultSet rs = pst.executeQuery();

			if (rs.next()) {
				int idOperationTrouve = rs.getInt("idOperation");
				double montant = rs.getDouble("montant");
				Date dateOp = rs.getDate("dateOp");
				Date dateValeur = rs.getDate("dateValeur");
				int idNumCompteTrouve = rs.getInt("idNumCompte");
				String idTypeOp = rs.getString("idTypeOp");

				operationTrouvee = new Operation(idOperationTrouve, montant, dateOp, dateValeur, idNumCompteTrouve,
						idTypeOp);
			} else {
				rs.close();
				pst.close();
				return null;
			}

			if (rs.next()) {
				rs.close();
				pst.close();
				throw new RowNotFoundOrTooManyRowsException(Table.Operation, Order.SELECT,
						"Recherche anormale (en trouve au moins 2)", null, 2);
			}
			rs.close();
			pst.close();
			return operationTrouvee;
		} catch (SQLException e) {
			throw new DataAccessException(Table.Operation, Order.SELECT, "Erreur accès", e);
		}
	}

	/**
	 * methode qui enregistre un debit dans meme lorsque le debit depasse le découvert autoriser
	 * donnée même si le débit dépasse le découvert autoriser
	 * @param idNumCompte
	 * @param montant
	 * @param typeOp
	 * @author Kwadjani Bilon
	 */
	public void insertDebitExeptionnel(int idNumCompte, double montant, String typeOp) {

		try {
            // Appel de la procédure stockée
            Connection con = LogToDatabase.getConnexion();
			CallableStatement call;

			String q = "{call Debit_Exceptionnel(?, ?, ?, ?)}";
			// les ? correspondent aux paramètres : cf. déf procédure (4 paramètres)
			call = con.prepareCall(q);
			// Paramètres in
			call.setInt(1, idNumCompte);
			// 1 -> valeur du premier paramètre, cf. déf procédure
			call.setDouble(2, montant);
			call.setString(3, typeOp);
			// Paramètres out
			call.registerOutParameter(4, java.sql.Types.INTEGER);
			// 4 type du quatrième paramètre qui est déclaré en OUT, cf. déf procédure

			call.execute();

			int res = call.getInt(4);

			System.out.println(res);


        } catch (Exception e) {
            e.printStackTrace();
        }

	}



	/**
	 * Enregistrement d'un débit.
	 *
	 * Se fait par procédure stockée : - Vérifie que le débitAutorisé n'est pas
	 * dépassé <BR />
	 * - Enregistre l'opération <BR />
	 * - Met à jour le solde du compte. <BR />
	 *
	 * @param idNumCompte compte débité
	 * @param montant     montant débité
	 * @param typeOp      libellé de l'opération effectuée (cf TypeOperation)
	 * @throws DataAccessException        Erreur d'accès aux données (requête mal
	 *                                    formée ou autre)
	 * @throws DatabaseConnexionException Erreur de connexion
	 * @throws ManagementRuleViolation    Si dépassement découvert autorisé
	 */
	public void insertDebit(int idNumCompte, double montant, String typeOp)
			throws DatabaseConnexionException, ManagementRuleViolation, DataAccessException {
		try {
			Connection con = LogToDatabase.getConnexion();
			CallableStatement call;

			String q = "{call Debiter (?, ?, ?, ?)}";
			// les ? correspondent aux paramètres : cf. déf procédure (4 paramètres)
			call = con.prepareCall(q);
			// Paramètres in
			call.setInt(1, idNumCompte);
			// 1 -> valeur du premier paramètre, cf. déf procédure
			call.setDouble(2, montant);
			call.setString(3, typeOp);
			// Paramètres out
			call.registerOutParameter(4, java.sql.Types.INTEGER);
			// 4 type du quatrième paramètre qui est déclaré en OUT, cf. déf procédure

			call.execute();

			int res = call.getInt(4);

			System.out.println(res);

			if (res != 0) { // Erreur applicative
				throw new ManagementRuleViolation(Table.Operation, Order.INSERT,
						"Erreur de règle de gestion : découvert autorisé dépassé", null);
			}
		} catch (SQLException e) {
			throw new DataAccessException(Table.Operation, Order.INSERT, "Erreur accès", e);
		}
	}


	/**
	 * Enregistrement d'un crédit.
	 *
	 * - Enregistre l'opération <BR />
	 * - Met à jour le solde du compte. <BR />
	 *
	 * @author illan
	 *
	 * @param idNumCompte compte crédité
	 * @param montant     montant crédité
	 * @param typeOp      libellé de l'opération effectuée (cf TypeOperation)
	 * @throws DataAccessException        Erreur d'accès aux données (requête mal
	 *                                    formée ou autre)
	 * @throws DatabaseConnexionException Erreur de connexion
	 *
	 */
	public void insertCredit(int idNumCompte, double montant, String idTypeOp)
			throws DatabaseConnexionException, DataAccessException {
		try {
			Connection con = LogToDatabase.getConnexion();
			CallableStatement call;

			String q = "{call Crediter (?, ?, ?, ?)}";
			// les ? correspondent aux paramètres : cf. déf procédure (4 paramètres)
			call = con.prepareCall(q);
			// Paramètres in
			call.setInt(1, idNumCompte);
			// 1 -> valeur du premier paramètre, cf. déf procédure
			call.setDouble(2, montant);
			call.setString(3, idTypeOp);
			// Paramètres out
			call.registerOutParameter(4, java.sql.Types.INTEGER);
			// 4 type du quatrième paramètre qui est déclaré en OUT, cf. déf procédure

			System.err.println(q);

			call.execute();

		} catch (SQLException e) {
			throw new DataAccessException(Table.Operation, Order.INSERT, "Erreur accès", e);
		}
	}

	/**
	 * Enregistrement d'un transfert.
	 *
	 * - Enregistre l'opération <BR />
	 * - Met à jour le soldes des comptes. <BR />
	 *
	 * @author illan
	 *
	 * @param idNumCompte compte débité
	 * @param idNumCompteDestinataire compte crédité
	 * @param montant     montant du transfert
	 * @param typeOp      libellé de l'opération effectuée (cf TypeOperation)
	 * @throws DataAccessException        Erreur d'accès aux données (requête mal
	 *                                    formée ou autre)
	 * @throws DatabaseConnexionException Erreur de connexion
	 * @throws ManagementRuleViolation    Si dépassement découvert autorisé sur le compte débité
	 */
	public void insertTransfert(int idNumCompte, int idNumCompteDestinataire, double montant, String idTypeOp)
			throws DatabaseConnexionException, ManagementRuleViolation, DataAccessException {

		System.out.println("InsertTransfert : "+ montant +" " +idTypeOp);


		insertDebit(idNumCompte, montant, idTypeOp);

		System.out.println("Débit fait");

		insertCredit(idNumCompteDestinataire, montant, idTypeOp);

		System.out.println("Crédit fait");
	}
	/**
	 * Suppression toutes les opérations d'un compte.
	 *
	 *
	 * @author Illan GABARRA
	 *
	 * @param idNumCompte Compte pour lequel supprimer les opérations
	 * @throws DataAccessException        Erreur d'accès aux données (requête mal
	 *                                    formée ou autre)
	 * @throws DatabaseConnexionException Erreur de connexion
	 * @throws ManagementRuleViolation    Si dépassement découvert autorisé sur le compte débité
	 * @throws SQLException
	 * @throws RowNotFoundOrTooManyRowsException
	 */
	public void suppressionOperations(int idNumCompte)
			throws DatabaseConnexionException, DataAccessException, SQLException, RowNotFoundOrTooManyRowsException {

        Connection con = LogToDatabase.getConnexion();

        String query = "DELETE Operation WHERE idNumCompte = ?";

        System.err.println(query);

        PreparedStatement pst = con.prepareStatement(query);
        pst.setInt(1, idNumCompte);

        pst.executeUpdate();

        pst.close();

        con.commit();
	}

	/*
	 * Fonction utilitaire qui retourne un ordre sql "to_date" pour mettre une date
	 * dans une requête sql
	 *
	 * @param d Date (java.sql) à transformer
	 *
	 * @return Une chaine : TO_DATE ('j/m/a', 'DD/MM/YYYY') 'j/m/a' : jour mois an
	 * de d ex : TO_DATE ('25/01/2019', 'DD/MM/YYYY')
	 */
	private String dateToString(Date d) {
		String sd;
		Calendar cal;
		cal = Calendar.getInstance();
		cal.setTime(d);
		sd = "" + cal.get(Calendar.DAY_OF_MONTH) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR);
		sd = "TO_DATE( '" + sd + "' , 'DD/MM/YYYY')";
		return sd;
	}





}
