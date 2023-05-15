package model.orm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import model.data.TypeOperation;
import model.orm.exception.DataAccessException;
import model.orm.exception.DatabaseConnexionException;
import model.orm.exception.Order;
import model.orm.exception.Table;

/**
 * Classe d'accès aux TypeOperation en BD Oracle.
 */
public class Access_BD_TypeOperation {

	public Access_BD_TypeOperation() {
	}

	/**
	 * Recherche tous les TypeOpertion existants.
	 *
	 * @return Liste de tous les TypeOperation existant
	 * @throws DataAccessException        Erreur d'accès aux données (requête mal
	 *                                    formée ou autre)
	 * @throws DatabaseConnexionException Erreur de connexion
	 */
	public ArrayList<TypeOperation> getTypeOperations() throws DataAccessException, DatabaseConnexionException {

		ArrayList<TypeOperation> alResult = new ArrayList<>();

		try {
			Connection con = LogToDatabase.getConnexion();
			String query = "SELECT * FROM TypeOperation ORDER BY idTypeOp";

			System.err.println(query);

			PreparedStatement pst = con.prepareStatement(query);

			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				String idTypeOp = rs.getString("idTypeOp");

				alResult.add(new TypeOperation(idTypeOp));
			}
			rs.close();
			pst.close();
		} catch (SQLException e) {
			throw new DataAccessException(Table.TypeOperation, Order.SELECT, "Erreur accès", e);
		}

		return alResult;
	}
}
