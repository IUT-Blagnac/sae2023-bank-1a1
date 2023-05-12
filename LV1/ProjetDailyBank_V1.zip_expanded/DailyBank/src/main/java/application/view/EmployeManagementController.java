package application.view;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.itextpdf.text.List;

import application.DailyBankState;
import application.GlobalSettings;
import application.control.ClientsManagement;
import application.control.DailyBankMainFrame;
import application.control.EmployeEditorPane;
import application.control.EmployeManagement;
import application.control.EmployeRead;
import javafx.stage.Stage;
import application.tools.AlertUtilities;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.AgenceBancaire;
import model.data.Client;
import model.data.Employe;
import model.orm.Access_BD_Employe;
import model.orm.LogToDatabase;
import model.orm.exception.DataAccessException;
import model.orm.exception.DatabaseConnexionException;
import model.orm.exception.RowNotFoundOrTooManyRowsException;

public class EmployeManagementController {
	private DailyBankState dailyBankState;

	// Contrôleur de Dialogue associé à ClientsManagementController
	private EmployeManagement emEmployeManagement;

	// Fenêtre physique ou est la scène contenant le fichier xml contrôlé par this
	private Stage primaryStage;

	// Données de la fenêtre
	private ObservableList<Employe> oListEmploye;
	
	private Employe chefAgence;
	
	private Access_BD_Employe accessBd;
	
	private Employe currentEmploye;
	

	// Manipulation de la fenêtre
	public void initContext(Stage _containingStage, EmployeManagement _em, DailyBankState _dbstate) {
		this.emEmployeManagement = _em;
		this.primaryStage = _containingStage;
		this.dailyBankState = _dbstate;
		this.btnDesactEmploye.setDisable(true);
		this.btnModifEmploye.setDisable(true);
		accessBd = new Access_BD_Employe();
		try {
			chefAgence = accessBd.getEmploye(GlobalSettings.login, GlobalSettings.password);
			GlobalSettings.currentChefAgence = chefAgence;
		} catch (RowNotFoundOrTooManyRowsException | DataAccessException | DatabaseConnexionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		lvEmploye.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Employe>() {
			@Override
			public void changed(ObservableValue<? extends Employe> observable, Employe oldValue, Employe newValue) {
				btnDesactEmploye.setDisable(false);
				btnModifEmploye.setDisable(false);
				currentEmploye = lvEmploye.getSelectionModel().getSelectedItem();
				System.out.println(currentEmploye);
			}
		});
		
		lvEmploye.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent click) {
				if(click.getClickCount() == 2) {
					System.out.println("DOUBLE CLICK");
					EmployeRead emc = new EmployeRead(primaryStage,dailyBankState);
					emc.doEmployeReadDialog(currentEmploye);
				}
			}
			
		});
	}
	
	private void configure() {
		this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));
		
		this.oListEmploye = FXCollections.observableArrayList();
		
	}
	
	public void displayDialog()
	{
		updateListEmploye();
		
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
			
			String query = "SELECT * FROM employe WHERE idag = " + chefAgence.idAg;
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
	
	public void updateListEmploye() {
		oListEmploye = FXCollections.observableArrayList(this.getAllEmploye());
		this.lvEmploye.setItems(this.oListEmploye);
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
		Connection con;
		
		try {
			con = LogToDatabase.getConnexion();
			
			String query = "DELETE FROM employe WHERE idEmploye = " + currentEmploye.idEmploye;
			PreparedStatement pst = con.prepareStatement(query);
			
			int result = pst.executeUpdate();
			
			pst.close();
			
		} catch (DatabaseConnexionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		updateListEmploye();
	}
	
	public Employe getCurrentUser() {
		return this.chefAgence;
	}
}
