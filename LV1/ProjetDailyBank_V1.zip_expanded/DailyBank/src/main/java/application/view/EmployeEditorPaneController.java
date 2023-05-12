package application.view;

import application.DailyBankState;
import application.control.EmployeManagement;
import application.tools.EditionMode;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.Employe;

public class EmployeEditorPaneController {
	
	private Stage primaryStage;
	
	private Employe employe;
	
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
	private Button butOk;
	@FXML
	private Button butCancel;
	
	public void initContext(Stage _containingStage, EmployeManagement _em, DailyBankState _dbstate) {
		this.primaryStage = _containingStage;
		this.dailyBankState = _dbstate;
	}
	
	private void configure() {
		this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));
		
	}
	
	@FXML
	private void doAjouter()
	{
		
	}
	
	@FXML
	private void doCancel()
	{
		this.primaryStage.close();
	}
	
	private Object closeWindow(WindowEvent e)
	{
		this.doCancel();
		e.consume();
		return null;
	}
	
	public Employe displayDialog(Employe employe)
	{
		Employe employeResultat;
		
		if(employe == null)
		{
			this.employe = new Employe(0,"","","guichetier","","",0);
		} else
		{
			this.employe = new Employe(employe);
		}
		
		this.txtIdEmploye.setText("" + this.employe.idEmploye);
		this.txtPrenom.setText(this.employe.prenom);
		this.txtNom.setText(this.employe.nom);
		this.txtLogin.setText(this.employe.login);
		this.txtMotDePasse.setText(this.employe.motPasse);
		this.txtIdAgence.setText("" + this.employe.idAg);
	
		employeResultat = this.employe;
		this.primaryStage.showAndWait();
		
		return employeResultat;
	}
	
	
	
	private boolean isSaisieValide()
	{
		
		
		
		return true;
	}
}
