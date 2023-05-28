package application.view;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import application.DailyBankState;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.data.CompteCourant;
import model.data.Operation;
import model.orm.LogToDatabase;
import model.orm.exception.DatabaseConnexionException;

public class PrelevementEditorController {
	// Etat courant de l'application
			private DailyBankState dailyBankState;

			// Fenêtre physique ou est la scène contenant le fichier xml contrôlé par this
			private Stage primaryStage;
			
			private CompteCourant compte;
			
			private Operation operation;
			
			private String modifier;
									
		public void initContext(Stage _containingStage, DailyBankState _dbstate, CompteCourant _compte, Operation _operation, String modifier) {
			this.primaryStage = _containingStage;
			this.dailyBankState = _dbstate;
			this.compte = _compte;
			this.operation = _operation;
			this.modifier = modifier;
		}
		
		public void displayDialog() {
			if(operation == null) {
				this.txtMontant.setText("0");
			} else {
				this.txtMontant.setText(this.operation.montant + "");
			}
			this.primaryStage.showAndWait();
		}
		
		@FXML
		private Button btnOk;
		@FXML
		private Button btnCancel;
		@FXML
		private TextField txtMontant;
		
		@FXML
		private void doAjouter() {
			
			Date date = new Date();
			java.sql.Date sqlDate = new java.sql.Date(date.getTime());
			
			String idCompte = this.compte.idNumCompte + "";
			int idCompteNormalize = Integer.parseInt(idCompte.replaceFirst("0", ""));
			
			Connection con;
			try {
				con = LogToDatabase.getConnexion();
				
				if(this.operation != null && modifier == "O") {
					String query = "DELETE FROM operation WHERE idoperation = " + this.operation.idOperation;
					PreparedStatement pst = con.prepareStatement(query);
					ResultSet rs = pst.executeQuery();
				}
				
				String query2 = "INSERT INTO operation VALUES(seq_id_operation.NEXTVAL,?,?,?,?,?)";
				PreparedStatement pst2 = con.prepareStatement(query2);
				
				pst2.setDouble(1, Double.parseDouble(txtMontant.getText()));
				pst2.setDate(2, sqlDate);
				pst2.setDate(3, sqlDate);
				pst2.setInt(4,idCompteNormalize);
				pst2.setString(5, "Prélèvement automatique");

				ResultSet rs2 = pst2.executeQuery();


				this.primaryStage.close();

			} catch (DatabaseConnexionException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
					}
		
		@FXML
		private void doCancel() {
			this.primaryStage.close();
		}
}
