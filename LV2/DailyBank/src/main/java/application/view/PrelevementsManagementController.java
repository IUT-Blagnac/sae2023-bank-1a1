package application.view;

import application.DailyBankState;
import application.control.ComptesManagement;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import model.data.Client;

public class PrelevementsManagementController {

	// Etat courant de l'application
		private DailyBankState dailyBankState;

		// Fenêtre physique ou est la scène contenant le fichier xml contrôlé par this
		private Stage primaryStage;
		
	public void initContext(Stage _containingStage, DailyBankState _dbstate) {
		this.primaryStage = _containingStage;
		this.dailyBankState = _dbstate;
	}
	
	public void displayDialog() {
		primaryStage.showAndWait();
	}
	
	@FXML
	private void doCancel() {
		this.primaryStage.close();
	}
	
	@FXML
	private void doNouveauPrelevement() {
		
	}
	
	@FXML
	private void doModifierPrelevement() {
		
	}
	
	@FXML
	private void doSupprimerPrelevement() {
		
	}
}