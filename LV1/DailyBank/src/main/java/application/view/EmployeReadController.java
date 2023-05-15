package application.view;

import application.DailyBankState;
import application.GlobalSettings;
import application.control.EmployeManagement;
import application.tools.EditionMode;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.data.Employe;

public class EmployeReadController {
	
	private Stage primaryStage;
		
	private EditionMode editionMode;
	
	private DailyBankState dailyBankState;
	
	@FXML
	private TextField txtIdEmploye;
	@FXML
	private TextField txtPrenom;
	@FXML
	private TextField txtNom;
	@FXML
	private TextField txtLogin;
	@FXML
	private TextField txtMotDePasse;
	@FXML
	private TextField txtIdAgence;
	
	@FXML
	private Button butCancel;
	
	public void initContext(Stage _containingStage, DailyBankState _dbstate) {
		this.primaryStage = _containingStage;
		this.dailyBankState = _dbstate;
	}
	
	public void displayDialog(Employe employe)
	{
		this.txtIdEmploye.setText("" + employe.idEmploye);
		this.txtPrenom.setText(employe.prenom);
		this.txtNom.setText(employe.nom);
		this.txtLogin.setText(employe.login);
		this.txtMotDePasse.setText(employe.motPasse);
		this.txtIdAgence.setText("" + employe.idAg);
		
		primaryStage.showAndWait();
	}
	
	@FXML
	private void doCancel() {
		this.primaryStage.close();
	}
}
