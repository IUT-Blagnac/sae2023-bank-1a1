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
import model.data.PrelevementAutomatique;
import model.orm.LogToDatabase;
import model.orm.exception.DatabaseConnexionException;

public class PrelevementEditorController {
	// Etat courant de l'application
			private DailyBankState dailyBankState;

			// Fenêtre physique ou est la scène contenant le fichier xml contrôlé par this
			private Stage primaryStage;
			
			private CompteCourant compte;
			
			private PrelevementAutomatique prelevement;
			
			private String modifier;
									
		public void initContext(Stage _containingStage, DailyBankState _dbstate, CompteCourant _compte, PrelevementAutomatique _prelevement, String modifier) {
			this.primaryStage = _containingStage;
			this.dailyBankState = _dbstate;
			this.compte = _compte;
			this.prelevement = _prelevement;
			this.modifier = modifier;
		}
		
		public void displayDialog() {
			if(prelevement == null) {
				this.txtMontant.setText("0");
				this.txtBeneficiaire.setText("Prélèvement automatique");
				this.txtDateRecurrente.setText("" + 1);
			} else {
				this.txtMontant.setText(this.prelevement.montant + "");
				this.txtBeneficiaire.setText(this.prelevement.beneficiaire);
				this.txtDateRecurrente.setText(this.prelevement.dateRecurrente + "");
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
		private TextField txtDateRecurrente;
		@FXML
		private TextField txtBeneficiaire;
		
		@FXML
		private void doAjouter() {
			
			String idCompte = this.compte.idNumCompte + "";
			int idCompteNormalize = Integer.parseInt(idCompte.replaceFirst("0", ""));
			
			Connection con;
			try {
				con = LogToDatabase.getConnexion();
				
				if(this.prelevement != null && modifier == "O") {
					String query = "DELETE FROM prelevementautomatique WHERE idprelev = " + this.prelevement.idPrelevement;
					PreparedStatement pst = con.prepareStatement(query);
					ResultSet rs = pst.executeQuery();
				}
				
				String query2 = "INSERT INTO prelevementautomatique VALUES(seq_id_prelevauto.NEXTVAL,?,?,?,?)";
				PreparedStatement pst2 = con.prepareStatement(query2);
				
				pst2.setDouble(1, Double.parseDouble(this.txtMontant.getText()));
				pst2.setInt(2, Integer.parseInt(this.txtDateRecurrente.getText()));
				pst2.setString(3, this.txtBeneficiaire.getText());
				pst2.setInt(4,idCompteNormalize);

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
