package application.view;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import application.DailyBankState;
import application.tools.AlertUtilities;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import model.data.CompteCourant;
import model.data.Operation;
import model.data.PrelevementAutomatique;
import model.orm.Access_BD_PrelevementAutomatique;
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
			
			if(this.validateComponent()) {
				Connection con;
				try {
					con = LogToDatabase.getConnexion();
					
					if(this.prelevement != null && modifier == "O") {
						Access_BD_PrelevementAutomatique prelevement = new Access_BD_PrelevementAutomatique();
						prelevement.deletePrelevement(idCompteNormalize);
					}
					
					Access_BD_PrelevementAutomatique prelevement = new Access_BD_PrelevementAutomatique();
					prelevement.insertPrelevement(Double.parseDouble(this.txtMontant.getText()),Integer.parseInt(this.txtDateRecurrente.getText()), this.txtBeneficiaire.getText(), this.compte.idNumCompte);

					this.primaryStage.close();

				} catch (DatabaseConnexionException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
			}
			
		}
		
		@FXML
		private void doCancel() {
			this.primaryStage.close();
		}
		
		private boolean validateComponent() {
			if(this.txtMontant.getText().isEmpty()) {
				AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "Le montant ne peux pas être vide",
						AlertType.WARNING);
				return false;
			}
			
			int montant = Integer.parseInt(this.txtMontant.getText());
			if(montant <= 0) {
				AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "Le montant ne peux pas être négatif ou égale à 0",
						AlertType.WARNING);
				return false;
			}
			
			if(this.txtDateRecurrente.getText().isEmpty()) {
				AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "La date recurrente ne peux pas être vide",
						AlertType.WARNING);
				return false;
			}
			
			if(this.txtBeneficiaire.getText().isEmpty()) {
				AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "Le bénéficiaire ne peux pas être vide",
						AlertType.WARNING);
				return false;
			}
			
			int currentDate = Integer.parseInt(this.txtDateRecurrente.getText());
			if(currentDate < 1 || currentDate > 28) {
				AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "La date recurrente doit être entre 1 et 28",
						AlertType.WARNING);
				return false;
			}
			
			return true;
		}
}
