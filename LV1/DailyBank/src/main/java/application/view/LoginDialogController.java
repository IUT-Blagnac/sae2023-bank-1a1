package application.view;

import application.DailyBankState;
import application.GlobalSettings;
import application.control.LoginDialog;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.Employe;

/**
 * Controller JavaFX de la view logindialog.
 *
 */
public class LoginDialogController {

	// Etat courant de l'application
	private DailyBankState dailyBankState;

	// Contrôleur de Dialogue associé à LoginDialogController
	private LoginDialog ldDialogController;

	// Fenêtre physique ou est la scène contenant le fichier xml contrôlé par this
	private Stage primaryStage;

	// private String login,password;


	// Données de la fenêtre

	// Manipulation de la fenêtre

	/**
	 * Initialisation du contrôleur de vue LoginDialogController.
	 *
	 * @param _containingStage Stage qui contient le fichier xml contrôlé par
	 *                         LoginDialogController
	 * @param _ld              Contrôleur de Dialogue qui réalisera les opérations
	 *                         de navigation ou calcul
	 * @param _dbstate         Etat courant de l'application
	 */
	public void initContext(Stage _containingStage, LoginDialog _ld, DailyBankState _dbstate) {
		this.primaryStage = _containingStage;
		this.ldDialogController = _ld;
		this.dailyBankState = _dbstate;
		this.configure();
	}

	/**
	 * Affichage de la fenêtre.
	 */
	public void displayDialog() {
		this.primaryStage.showAndWait();
	}

	/*
	 * Configuration de LoginDialogController. Fermeture par la croix.
	 */
	private void configure() {
		this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));
	}

	/*
	 * Méthode de fermeture de la fenêtre par la croix.
	 *
	 * @param e Evénement associé (inutilisé pour le moment)
	 *
	 * @return null toujours (inutilisé)
	 */
	private Object closeWindow(WindowEvent e) {
		this.doCancel();
		e.consume();
		return null;
	}

	// Attributs de la scene
	@FXML
	private TextField txtLogin;
	@FXML
	private PasswordField txtPassword;
	@FXML
	private Label lblMessage;

	// Actions

	/*
	 * Action quitter (annuler le login et fermer la fenêtre)
	 */
	@FXML
	private void doCancel() {
		this.ldDialogController.annulerLogin();
		this.primaryStage.close();
	}

	/*
	 * Action login.
	 *
	 * Vérifier que login/password non vides. Lancer la recherche par le contrôleur
	 * de dialogue Si employé trouvé : fermer la fenêtre (sinon continuer)
	 */
	@FXML
	private void doOK() {
		GlobalSettings.login = this.txtLogin.getText().trim();
		GlobalSettings.password = new String(this.txtPassword.getText().trim());
		if (GlobalSettings.login.length() == 0 || GlobalSettings.password.length() == 0) {
			this.afficheErreur("Identifiants incorrects :");
		} else {
			Employe e;
			e = this.ldDialogController.chercherEmployeParLogin(GlobalSettings.login, GlobalSettings.password);

			if (e == null) {
				this.afficheErreur("Identifiants incorrects :");
			} else {
				this.primaryStage.close();
			}
		}
	}

	/*
	 * Affichage d'un message d'erreur
	 *
	 * @param texte Texte à afficher
	 */
	private void afficheErreur(String texte) {
		this.lblMessage.setText(texte);
		this.lblMessage.setStyle("-fx-text-fill:red; -fx-font-weight: bold;");
	}
}
