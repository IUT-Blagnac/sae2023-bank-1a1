package application.view;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import application.DailyBankState;
import application.GlobalSettings;
import application.control.EmployeManagement;
import application.tools.AlertUtilities;
import application.tools.EditionMode;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.Employe;
import model.orm.LogToDatabase;
import model.orm.exception.DatabaseConnexionException;

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
		txtIdAgence.setDisable(true);
		txtIdEmploye.setDisable(true);
	}
	
	private void configure() {
		this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));
	}
	
	@FXML
	private void doAjouter()
	{
		if(isSaisieValide()) {
			Connection con;
			try {
				con = LogToDatabase.getConnexion();
				
				String query = "INSERT INTO EMPLOYE VALUES (" + "?" + ", " + "?" + ", " + "?" + ", "
						+ "?" + ", " + "?" + ", " + "?" + ", " + "?" + ")";
				PreparedStatement pst = con.prepareStatement(query);
				pst.setInt(1, getMaximumId() + 1);
				pst.setString(2, this.txtNom.getText());
				pst.setString(3, this.txtPrenom.getText());
				pst.setString(4,"guichetier");
				pst.setString(5, this.txtLogin.getText());
				pst.setString(6, this.txtLogin.getText());
				pst.setInt(7, GlobalSettings.currentChefAgence.idAg);
				
				System.err.println(query);	

				int result = pst.executeUpdate();
				
				pst.close();
				
				this.primaryStage.close();
				
			} catch (DatabaseConnexionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
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
	
	public void displayDialog(Employe employe)
	{
		int maxId = this.getMaximumId();
		
		if(employe == null)
		{
			this.employe = new Employe(maxId,"","","guichetier","","",0);
		} else
		{
			this.employe = new Employe(employe);
		}
		
		this.txtIdEmploye.setText("" + this.employe.idEmploye);
		this.txtPrenom.setText(this.employe.prenom);
		this.txtNom.setText(this.employe.nom);
		this.txtLogin.setText(this.employe.login);
		this.txtMotDePasse.setText(this.employe.motPasse);
		this.txtIdAgence.setText("" + GlobalSettings.currentChefAgence.idAg);
	
		this.primaryStage.showAndWait();
	}
	
	
	
	private boolean isSaisieValide()
	{
		this.employe.nom = this.txtNom.getText().trim();
		this.employe.prenom = this.txtPrenom.getText().trim();
		this.employe.login = this.txtLogin.getText().trim();
		this.employe.motPasse = this.txtMotDePasse.getText().trim();
		
		if(this.employe.nom.isEmpty()) {
			AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "Le nom ne doit pas être vide",
					AlertType.WARNING);
			return false;
		}
		
		if(this.employe.prenom.isEmpty()) {
			AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "Le prénom ne doit pas être vide",
					AlertType.WARNING);
			return false;
		}
		
		if(this.employe.login.isEmpty()) {
			AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "Le login ne doit pas être vide",
					AlertType.WARNING);
			return false;
		}
		
		if(this.employe.motPasse.isEmpty()) {
			AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "Le mot de passe ne doit pas être vide",
					AlertType.WARNING);
			return false;
		}
		
		return true;
	}
	
	public int getMaximumId()
	{
		int idEmployeTrouve = 0;
		
		Connection con;
		PreparedStatement pst;
		try {
			
			con = LogToDatabase.getConnexion();
			String query = "select max(idemploye) AS max_id FROM employe";

			pst = con.prepareStatement(query);

			ResultSet rs = pst.executeQuery();

			System.err.println(query);
			
			if(rs.next()) {
				idEmployeTrouve = rs.getInt("max_id");
			}

			rs.close();
			pst.close();
		} catch (DatabaseConnexionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return idEmployeTrouve;
	}
}