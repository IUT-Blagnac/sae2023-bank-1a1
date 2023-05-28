package application.view;

import java.awt.Desktop;
import java.io.File;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import com.itextpdf.text.Document;

import application.DailyBankState;
import application.control.ExceptionDialog;
import application.tools.AlertUtilities;
import application.tools.RelevesBancaire;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.Client;
import model.data.CompteCourant;

/* Classe permettant de saisir les dates et le chemin du fichier pour le relevé à générer
 *
 * @author illan
 */
public class ReleveEditorPaneController {

	// Fenêtre physique ou est la scène contenant le fichier xml contrôlé par this
	private DailyBankState dbState;
	private Stage primaryStage;


	// Données de la fenêtre 
	private Client clientDuCompte;
	private CompteCourant compte;

	public RelevesBancaire releveResultat;


	// Manipulation de la fenêtre
	public void initContext(DailyBankState _dbState,Stage _containingStage, Client  clientDuCompte, CompteCourant compte) {
		this.dbState = _dbState;
		this.primaryStage = _containingStage;
		this.clientDuCompte = clientDuCompte;
		this.compte = compte;
		this.configure();
	}

	private void configure() {
		this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));

		this.txtNumCompte.setText(""+this.compte.idNumCompte);
		
		dpDateDebut.requestFocus();

	}

	// Gestion du stage
	private Object closeWindow(WindowEvent e) {
		this.doAnnuler();
		e.consume();
		return null;
	}

	// Attributs de la scene + actions


	@FXML
	private TextField txtNumCompte;
	@FXML
	private Label lblDateDebut;
	@FXML
	private DatePicker dpDateDebut;
	@FXML
	private Label lblDateFin;
	@FXML
	private DatePicker dpDateFin;
	@FXML
	private Label lblChemin;
	@FXML
	private TextField txtChemin;
	@FXML
	private Button btnChemin;
	@FXML
	private Button btnValider;
	@FXML
	private Button btnAnnuler;


	private File fichierChoisit;

	@FXML
	private void doChemin() {


		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().add(new ExtensionFilter("Fichier PDF", "*.pdf"));


		if (fichierChoisit == null) {
			fileChooser.setInitialFileName("Relevé-Bancaire-Compte-" +this.compte.idNumCompte+ ".pdf");
			fileChooser.setInitialDirectory(new File("."));
		}
		else {
			fileChooser.setInitialFileName(fichierChoisit.getName());
			fileChooser.setInitialDirectory(fichierChoisit.getParentFile());
		}

		this.fichierChoisit = fileChooser.showSaveDialog(primaryStage);

		if (this.fichierChoisit != null) {
			this.txtChemin.setText(this.fichierChoisit.getName());
		}
	}

	@FXML
	private void doAnnuler() {
		this.primaryStage.close();
	}
	@FXML
	private void doValider() {
		Date dateDebut = null;
		Date dateFin = null;	

		boolean isSaisieValide = true;

		// Suppression de l'affichage en rouge des erreurs 

		this.lblDateDebut.getStyleClass().remove("borderred");
		this.dpDateDebut.getStyleClass().remove("borderred");

		this.lblDateFin.getStyleClass().remove("borderred");
		this.dpDateFin.getStyleClass().remove("borderred");

		this.lblChemin.getStyleClass().remove("borderred");
		this.txtChemin.getStyleClass().remove("borderred");
		this.btnChemin.getStyleClass().remove("borderred");


		// Test ET Conversion Date Debut
		try {
			if (dpDateDebut.getValue() == null) {
				throw new IllegalArgumentException();
			}
			LocalDate localDateDebut = dpDateDebut.getValue();

			dateDebut = Date.from(localDateDebut.atStartOfDay(ZoneId.systemDefault()).toInstant());
			
			if (dateDebut.after(new Date())) {
				throw new IllegalArgumentException();
			}

		}
		catch (IllegalArgumentException e) {
			this.lblDateDebut.getStyleClass().add("borderred");
			this.dpDateDebut.getStyleClass().add("borderred");
			isSaisieValide = false;
		}
		// Test ET Conversion Date Fin

		try {
			if (dpDateFin.getValue() == null) {
				throw new IllegalArgumentException();
			}
			LocalDate localDateFin = dpDateFin.getValue();

			dateFin = Date.from(localDateFin.atStartOfDay(ZoneId.systemDefault()).toInstant());
			if (dateFin != null) {
				if (dateFin.compareTo(dateDebut) <= 0 | dateFin.compareTo(new Date()) > 0 ) {
					throw new IllegalArgumentException();
				}
			}

		}
		catch (IllegalArgumentException e) {
			this.lblDateFin.getStyleClass().add("borderred");
			this.dpDateFin.getStyleClass().add("borderred");
			isSaisieValide = false;
		}

		// Test Chemin

		if (fichierChoisit == null) {

			this.lblChemin.getStyleClass().add("borderred");
			this.txtChemin.getStyleClass().add("borderred");
			this.btnChemin.getStyleClass().add("borderred");
			this.btnChemin.requestFocus();
			isSaisieValide = false;

		}

		if (!isSaisieValide) {
			return;
		}

		this.releveResultat = new RelevesBancaire( this.dbState, clientDuCompte, compte, dateDebut, dateFin);
		try {
			if (this.releveResultat.genererReleveBancaire(fichierChoisit) == null) {
				throw new Exception();
			}
			else {
				Desktop.getDesktop().open(fichierChoisit);
			}
		} catch (Exception e) {
			AlertUtilities.showAlert(primaryStage, "Erreur dans la génération du relevé", "Le relevé n'a pas pu être généré", "Vous pouvez essayer de changer le nom du fichier ou son emplacement\n"
					+ "pour résoudre le problème", AlertType.ERROR);
		}
		
	}
}