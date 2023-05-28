package model.orm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import model.data.PrelevementAutomatique;
import model.orm.exception.DatabaseConnexionException;

public class Access_BD_PrelevementAutomatique {
	public Access_BD_PrelevementAutomatique() {
	}
	
	public ArrayList<PrelevementAutomatique> getAllPrelevements(int idCompte){
		ArrayList<PrelevementAutomatique> output = new ArrayList<PrelevementAutomatique>();
		
		
		String idNumCompte = idCompte + "";
		int idCompteNormalize = Integer.parseInt(idNumCompte.replaceFirst("0", ""));
		System.out.println(idCompteNormalize);
		
		Connection con;
		try {
			con = LogToDatabase.getConnexion();

			String query = "SELECT * FROM prelevementautomatique WHERE idnumcompte = " + idCompteNormalize;
			PreparedStatement pst = con.prepareStatement(query);

			ResultSet rs = pst.executeQuery();

			while(rs.next()) {
				PrelevementAutomatique prelevement = new PrelevementAutomatique(rs.getInt(1),rs.getDouble(2),rs.getInt(3),rs.getString(4),rs.getInt(5));
				output.add(prelevement);
			}

		} catch (DatabaseConnexionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return output;
	}
	
	public void deletePrelevement(int idPrelevement) {
		Connection con;
		try {
			con = LogToDatabase.getConnexion();

			String query = "DELETE FROM prelevementautomatique WHERE idprelev = " + idPrelevement;
			PreparedStatement pst = con.prepareStatement(query);

			ResultSet rs = pst.executeQuery();
			
		} catch (DatabaseConnexionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void insertPrelevement(double montant, int dateRecurrente, String beneficiaire, int idCompte) {
		
		Connection con;
		try {
			con = LogToDatabase.getConnexion();

			String query2 = "INSERT INTO prelevementautomatique VALUES(seq_id_prelevauto.NEXTVAL,?,?,?,?)";
			PreparedStatement pst2 = con.prepareStatement(query2);
			
			pst2.setDouble(1, montant);
			pst2.setInt(2, dateRecurrente);
			pst2.setString(3, beneficiaire);
			pst2.setInt(4,idCompte);

			ResultSet rs2 = pst2.executeQuery();
			
		} catch (DatabaseConnexionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
