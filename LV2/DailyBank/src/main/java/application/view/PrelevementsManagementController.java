package application.view;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import application.DailyBankState;
import application.control.ComptesManagement;
import application.control.PrelevementEditorPane;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import model.data.Client;
import model.data.CompteCourant;
import model.data.Operation;
import model.orm.LogToDatabase;
import model.orm.exception.DatabaseConnexionException;

public class PrelevementsManagementController {

	// Etat courant de l'application
		private DailyBankState dailyBankState;

		// Fenêtre physique ou est la scène contenant le fichier xml contrôlé par this
		private Stage primaryStage;
		
		private ObservableList<Operation> oListOperation;
		
		private CompteCourant compte;
		
		private Operation operation;
		
	public void initContext(Stage _containingStage, DailyBankState _dbstate, CompteCourant _compte) {
		this.primaryStage = _containingStage;
		this.dailyBankState = _dbstate;
		this.compte = _compte;
		this.btnModifierPrelevement.setDisable(true);
		this.btnSupprimerPrelevement.setDisable(true);
		
		lvPrelevements.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Operation>() {

			@Override
			public void changed(ObservableValue<? extends Operation> observable, Operation oldValue,
					Operation newValue) {
				btnModifierPrelevement.setDisable(false);
				btnSupprimerPrelevement.setDisable(false);
				operation = lvPrelevements.getSelectionModel().getSelectedItem();
			}
			
		});
		
	}
	
	public void displayDialog() {
		updateListPrelevements();
		System.out.println(this.compte);
		System.out.println(this.oListOperation);
		this.primaryStage.showAndWait();
	}
	
	public void updateListPrelevements() {
		oListOperation = FXCollections.observableArrayList(this.getAllOperation());
		this.lvPrelevements.setItems(oListOperation);
	}
	
	@FXML
	private Button btnNouveauPrelevement;
	@FXML
	private Button btnModifierPrelevement;
	@FXML
	private Button btnSupprimerPrelevement;
	@FXML
	private Button btnLirePrelevement;
	@FXML
	private ListView<Operation> lvPrelevements;
	
	@FXML
	private void doCancel() {
		this.primaryStage.close();
	}
	
	@FXML
	private void doNouveauPrelevement() {
		PrelevementEditorPane pen = new PrelevementEditorPane(primaryStage, dailyBankState, compte,operation,"N");
		updateListPrelevements();
	}
	
	@FXML
	private void doModifierPrelevement() {
		PrelevementEditorPane pen = new PrelevementEditorPane(primaryStage, dailyBankState, compte,operation,"O");
		updateListPrelevements();
	}
	
	@FXML
	private void doSupprimerPrelevement() {
		Connection con;
		try {
			con = LogToDatabase.getConnexion();

			String query = "DELETE FROM operation WHERE idoperation = " + this.operation.idOperation;
			PreparedStatement pst = con.prepareStatement(query);

			ResultSet rs = pst.executeQuery();
			
			this.updateListPrelevements();

		} catch (DatabaseConnexionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public ArrayList<Operation> getAllOperation() {
		ArrayList<Operation> output = new ArrayList<Operation>();
		
		String idCompte = this.compte.idNumCompte + "";
		int idCompteNormalize = Integer.parseInt(idCompte.replaceFirst("0", ""));
		System.out.println(idCompteNormalize);
		
		Connection con;
		try {
			con = LogToDatabase.getConnexion();

			String query = "SELECT * FROM operation WHERE idnumcompte = " + idCompteNormalize + " AND idtypeop = 'Prélèvement automatique'";
			PreparedStatement pst = con.prepareStatement(query);

			ResultSet rs = pst.executeQuery();

			while(rs.next()) {
				Operation operation = new Operation(rs.getInt(1),rs.getDouble(2),rs.getDate(3),rs.getDate(4),rs.getInt(5),rs.getString(6));
				output.add(operation);
			}

		} catch (DatabaseConnexionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return output;
	}
}