package application.view;

import java.util.ArrayList;

import application.DailyBankState;
import application.control.ComptesManagement;
import application.tools.ConstantesIHM;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.Client;
import model.data.CompteCourant;

public class ComptesManagementController {

	// Etat courant de l'application
	private DailyBankState dailyBankState;

	// Contrôleur de Dialogue associé à ComptesManagementController
	private ComptesManagement cmDialogController;

	// Fenêtre physique ou est la scène contenant le fichier xml contrôlé par this
	private Stage primaryStage;
	

	// Données de la fenêtre
	private Client clientDesComptes;
	private ObservableList<CompteCourant> oListCompteCourant;

	// Manipulation de la fenêtre
	public void initContext(Stage _containingStage, ComptesManagement _cm, DailyBankState _dbstate, Client client) {
		this.cmDialogController = _cm;
		this.primaryStage = _containingStage;
		this.dailyBankState = _dbstate;
		this.clientDesComptes = client;
		this.configure();
	}

	private void configure() {
		String info;

		this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));

		this.oListCompteCourant = FXCollections.observableArrayList();
		this.lvComptes.setItems(this.oListCompteCourant);
		this.lvComptes.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		this.lvComptes.getFocusModel().focus(-1);
		this.lvComptes.getSelectionModel().selectedItemProperty().addListener(e -> this.validateComponentState());

		info = this.clientDesComptes.nom + "  " + this.clientDesComptes.prenom + "  (id : "
				+ this.clientDesComptes.idNumCli + ")";
		this.lblInfosClient.setText(info);

		this.loadList();
		this.validateComponentState();
	}

	public void displayDialog() {
		this.primaryStage.showAndWait();
	}

	// Gestion du stage
	private Object closeWindow(WindowEvent e) {
		this.doCancel();
		e.consume();
		return null;
	}

	// Attributs de la scene + actions

	@FXML
	private Label lblInfosClient;
	@FXML
	private ListView<CompteCourant> lvComptes;
	@FXML
	private Button btnVoirOpes;
	@FXML
	private Button btnModifierCompte;
	@FXML
	private Button btnSupprCompte;
	@FXML
	private Button btnClôtureCompte;
	@FXML
	private Button btnNouveauCompte;
	

	@FXML
	private void doCancel() {
		this.primaryStage.close();
	}

	@FXML
	private void doVoirOperations() {
		int selectedIndice = this.lvComptes.getSelectionModel().getSelectedIndex();
		if (selectedIndice >= 0) {
			CompteCourant cpt = this.oListCompteCourant.get(selectedIndice);
			this.cmDialogController.gererOperationsDUnCompte(cpt);
			
		}
		this.loadList();
		this.validateComponentState();
	}
	
	private void afficheText(CompteCourant cc) {
		
		if(cc.estCloture.equals("O")) {
			btnClôtureCompte.setText("ReOuvrir");
		}else {
			btnClôtureCompte.setText("Cloturer");
		}
	}
	@FXML
	private void doClôtureCompte() {
		int selectedIndice = this.lvComptes.getSelectionModel().getSelectedIndex();
		CompteCourant cpt = this.oListCompteCourant.get(selectedIndice);
		if (selectedIndice >= 0) {
				this.cmDialogController.cloture(cpt);
			}
		this.loadList();
		this.validateComponentState();
	}

	@FXML
	private void doModifierCompte() {
	}

	@FXML
	/**
	 * Permet de supprimer le compte selectionné 
	 * 
	 * @author illan
	 */
	private void doSupprimerCompte() {
		
		int selectedIndice = this.lvComptes.getSelectionModel().getSelectedIndex();
		this.cmDialogController.suppressionCompte(this.oListCompteCourant.get(selectedIndice));
		this.loadList();
		validateComponentState();
	}

	@FXML
	private void doNouveauCompte() {
		CompteCourant compte;
		compte = this.cmDialogController.creerNouveauCompte();
		if (compte != null) {
			this.oListCompteCourant.add(compte);
		}
	}
	
	private void loadList() {
		ArrayList<CompteCourant> listeCpt;
		listeCpt = this.cmDialogController.getComptesDunClient();
		this.oListCompteCourant.clear();
		this.oListCompteCourant.addAll(listeCpt);
	}

	private void validateComponentState() {
		// Non implémenté => désactivé
		

		int selectedIndice = this.lvComptes.getSelectionModel().getSelectedIndex();
		if(ClientsManagementController.estInactif.equals("O")){
			this.btnNouveauCompte.setDisable(true);
		}else {
			this.btnNouveauCompte.setDisable(false);
		}
		if (selectedIndice >= 0) {
			CompteCourant cpt = this.oListCompteCourant.get(selectedIndice);
			afficheText(cpt);
		
		if (selectedIndice >= 0 ) {
			CompteCourant cpt1 = this.oListCompteCourant.get(selectedIndice);
			afficheText(cpt1);
			
			this.btnVoirOpes.setDisable(false);
			this.btnClôtureCompte.setDisable(false);
			this.btnModifierCompte.setDisable(false);
			
			if (cpt.estCloture.equals("O")) {
				this.btnSupprCompte.setDisable(false);
			}
			
			
		} else {
			this.btnClôtureCompte.setDisable(true);
			this.btnVoirOpes.setDisable(true);
			this.btnModifierCompte.setDisable(true);
			this.btnSupprCompte.setDisable(true);
		}
	}
}
}