package application.view;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.itextpdf.text.List;

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
import model.orm.LogToDatabase;
import model.orm.exception.DatabaseConnexionException;

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
		
	}
	
	public void displayDialog()
	{
		oListEmploye = FXCollections.observableArrayList(this.getAllEmploye());
		this.lvEmploye.setItems(this.oListEmploye);
		
		System.out.println("TEST");
		for(Employe employe : oListEmploye) {
			System.out.println(employe.idEmploye);
		}
		primaryStage.showAndWait();
		
	}
	
	private Object closeWindow(WindowEvent e)
	{
		this.doCancel();
		e.consume();
		return null;
	}
	
	private ArrayList<Employe> getAllEmploye()
	{
		ArrayList<Employe> output = new ArrayList<Employe>();
		
		System.out.println("VALIDE");
		Connection con;
		try {
			con = LogToDatabase.getConnexion();
			
			String query = "SELECT * FROM employe";
			PreparedStatement pst = con.prepareStatement(query);
			
			ResultSet rs = pst.executeQuery();
			
			while(rs.next()) {
				Employe employe = new Employe(rs.getInt(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5),rs.getString(6),rs.getInt(7));
				output.add(employe);
			}

			this.primaryStage.close();
			
		} catch (DatabaseConnexionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return output;
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
		edp.doEmployeManagementDialog(new Employe(edp.getEmployeMaxId() + 1,"","","","","",0));
	}
	
	@FXML
	private void doModifierEmploye()
	{
		
	}
	
	@FXML
	private void doDesactiverEmploye(){
		 
	}
}
