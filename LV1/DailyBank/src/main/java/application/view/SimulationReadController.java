package application.view;

import application.tools.ConstantesIHM;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.LigneTableauEmprunt;
import model.data.Simulation;


/* Classe permettant de lire le résultat d'une simulation
 *
 * @author illan
 */
public class SimulationReadController {

	// Fenêtre physique ou est la scène contenant le fichier xml contrôlé par this
	private Stage primaryStage;

	// Données

	private Simulation simulationEnCours;

	// Manipulation de la fenêtre
	public void initContext(Stage _containingStage, Simulation simulation) {
		this.primaryStage = _containingStage;
		this.simulationEnCours = simulation;
		this.configure();
	}

	private void configure() {
		this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));
	}

	// Gestion du stage
	private Object closeWindow(WindowEvent e) {
		this.doRetour();
		e.consume();
		return null;
	}

	// Attributs de la scene + actions

	@FXML
	private BorderPane paneRoot;
	@FXML
	private Button btnRetour;

	@FXML
	private void doRetour() {
		this.primaryStage.close();
	}


	public void displayDialog() {

		String strLbl;
		if (this.simulationEnCours.typePeriode.equals(ConstantesIHM.TYPE_PERIODE_1)) { // Mensualités
			strLbl = "Montant des mensualités";
		}
		else { // Annuités
			strLbl = "Montant des annuités";
		}

		if (simulationEnCours.typeSimulation.equals(ConstantesIHM.TYPE_SIMUL_1)) {



			TableColumn<LigneTableauEmprunt, Integer> periodeColonne = new TableColumn<>("Periode");
			periodeColonne.setCellValueFactory(new PropertyValueFactory<>("periode"));

			TableColumn<LigneTableauEmprunt, Double> capitalRestantDebutColonne = new TableColumn<>("Capital Restant dû au début");
			capitalRestantDebutColonne.setCellValueFactory(new PropertyValueFactory<>("capitalRestantDebut"));

			TableColumn<LigneTableauEmprunt, Double> interetsColonne = new TableColumn<>("Intérêts");
			interetsColonne.setCellValueFactory(new PropertyValueFactory<>("interets"));

			TableColumn<LigneTableauEmprunt, Double> principalColonne = new TableColumn<>("Principal");
			principalColonne.setCellValueFactory(new PropertyValueFactory<>("principal"));

			TableColumn<LigneTableauEmprunt, Double> montantARembourserColonne = new TableColumn<>(strLbl);
			montantARembourserColonne.setCellValueFactory(new PropertyValueFactory<>("montantARembourser"));

			TableColumn<LigneTableauEmprunt, Double> capitalRestantFinColonne = new TableColumn<>("Capital Restant dû à la fin");
			capitalRestantFinColonne.setCellValueFactory(new PropertyValueFactory<>("capitalRestantFin"));


			// Création de la table
			TableView<LigneTableauEmprunt> tableView = new TableView<>();

			tableView.getColumns().add(periodeColonne);
			tableView.getColumns().add(capitalRestantDebutColonne);
			tableView.getColumns().add(principalColonne);
			tableView.getColumns().add(montantARembourserColonne);
			tableView.getColumns().add(capitalRestantFinColonne);

			tableView.getItems().setAll(this.simulationEnCours.alSimulation);

			tableView.resizeColumn(tableView.getColumns().get(0), 80);
			for (int i = 1 ; i < tableView.getColumns().size() ; i ++) {
				tableView.resizeColumn(tableView.getColumns().get(i), 150);
			}

			this.paneRoot.setCenter(tableView);
		}
		else {
			System.err.println("Assurance TODO");
			return;
		}



		this.primaryStage.showAndWait();

	}
}