package application.view;

import java.util.ArrayList;

import application.DailyBankState;
import application.control.ClientsManagement;
import application.control.DailyBankMainFrame;
import application.control.EmployeEditorPane;
import application.control.EmployeManagement;
import javafx.stage.Stage;
import application.tools.AlertUtilities;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.AgenceBancaire;
import model.data.Client;
import model.data.Employe;

public class EmployeManagementController {
	private DailyBankState dailyBankState;

	// Contrôleur de Dialogue associé à ClientsManagementController
	private EmployeManagement emEmployeManagement;

	// Fenêtre physique ou est la scène contenant le fichier xml contrôlé par this
	private Stage primaryStage;

	// Données de la fenêtre
	private ObservableList<Employe> oListEmploye;

	// Manipulation de la fenêtre
	public void initContext(Stage _containingStage, EmployeManagement _em, DailyBankState _dbstate) {
		this.emEmployeManagement = _em;
		this.primaryStage = _containingStage;
		this.dailyBankState = _dbstate;
	}
	
	private void configure() {
		this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));
		
		this.oListEmploye = FXCollections.observableArrayList();
		this.lvEmploye.setItems(this.oListEmploye);
	}
	
	public void displayDialog()
	{
		primaryStage.showAndWait();
	}
	
	private Object closeWindow(WindowEvent e)
	{
		this.doCancel();
		e.consume();
		return null;
	}
	
	@FXML
	private TextField txtNum;
	@FXML
	private TextField txtNom;
	@FXML
	private TextField txtPrenom;
	@FXML
	private ListView<Employe> lvEmploye;
	@FXML
	private Button btnDesactEmploye;
	@FXML
	private Button btnModifEmploye;
	@FXML
	private Button btnNouveauEmploye;
	
	@FXML
	private void doCancel() {
		this.primaryStage.close();
	}

	@FXML
	private void doRechercher() {

	}
	
	@FXML
	private void doNouveauEmploye()
	{
		EmployeEditorPane edp = new EmployeEditorPane(this.primaryStage, this.dailyBankState);
		edp.doEmployeManagementDialog(new Employe());
	}
	
	@FXML
	private void doModifierEmploye()
	{
		
	}
	
	@FXML
	private void doDesactiverEmploye(){
		 
	}
	
	private Employe getEmployeSelectionne()
	{
		
		
		return new Employe();
	}
}
