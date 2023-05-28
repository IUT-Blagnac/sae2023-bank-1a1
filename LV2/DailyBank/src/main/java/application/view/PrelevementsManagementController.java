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
import model.data.PrelevementAutomatique;
import model.orm.Access_BD_PrelevementAutomatique;
import model.orm.LogToDatabase;
import model.orm.exception.DatabaseConnexionException;

public class PrelevementsManagementController {

	// Etat courant de l'application
		private DailyBankState dailyBankState;

		// Fenêtre physique ou est la scène contenant le fichier xml contrôlé par this
		private Stage primaryStage;
		
		private ObservableList<PrelevementAutomatique> oListPrelevements;
		
		private CompteCourant compte;
		
		private PrelevementAutomatique prelevement;
		
	public void initContext(Stage _containingStage, DailyBankState _dbstate, CompteCourant _compte) {
		this.primaryStage = _containingStage;
		this.dailyBankState = _dbstate;
		this.compte = _compte;
		this.btnModifierPrelevement.setDisable(true);
		this.btnSupprimerPrelevement.setDisable(true);
		updateListPrelevements();
		
		lvPrelevements.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<PrelevementAutomatique>() {

			@Override
			public void changed(ObservableValue<? extends PrelevementAutomatique> observable, PrelevementAutomatique oldValue,
					PrelevementAutomatique newValue) {
				btnModifierPrelevement.setDisable(false);
				btnSupprimerPrelevement.setDisable(false);
				prelevement = lvPrelevements.getSelectionModel().getSelectedItem();
			}
			
		});
		
	}
	
	public void displayDialog() {
		updateListPrelevements();
		System.out.println(this.compte);
		System.out.println(this.oListPrelevements);
		this.primaryStage.showAndWait();
	}
	
	public void updateListPrelevements() {
		oListPrelevements = FXCollections.observableArrayList(this.getAllOperation());
		this.lvPrelevements.setItems(oListPrelevements);
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
	private ListView<PrelevementAutomatique> lvPrelevements;
	
	@FXML
	private void doCancel() {
		this.primaryStage.close();
	}
	
	@FXML
	private void doNouveauPrelevement() {
		PrelevementEditorPane pen = new PrelevementEditorPane(primaryStage, dailyBankState, compte,prelevement,"N");
		updateListPrelevements();
	}
	
	@FXML
	private void doModifierPrelevement() {
		PrelevementEditorPane pen = new PrelevementEditorPane(primaryStage, dailyBankState, compte,prelevement,"O");
		updateListPrelevements();
	}
	
	@FXML
	private void doSupprimerPrelevement() {
		Access_BD_PrelevementAutomatique prelevement = new Access_BD_PrelevementAutomatique();
		prelevement.deletePrelevement(this.prelevement.idPrelevement);
		updateListPrelevements();

	}
	
	public ArrayList<PrelevementAutomatique> getAllOperation() {
		Access_BD_PrelevementAutomatique prelevement = new Access_BD_PrelevementAutomatique();
		
		return prelevement.getAllPrelevements(this.compte.idNumCompte);
	}
}