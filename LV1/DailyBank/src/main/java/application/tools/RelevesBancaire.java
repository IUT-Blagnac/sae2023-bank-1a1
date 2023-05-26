package application.tools;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import application.DailyBankState;
import application.control.ExceptionDialog;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import model.data.Client;
import model.data.CompteCourant;
import model.data.Operation;
import model.orm.Access_BD_Operation;
import model.orm.exception.DataAccessException;
import model.orm.exception.DatabaseConnexionException;

/**
 *
 * Classe utilitaire permettant de générer des pdf de relevés bancaire
 *
 * @author illan
 *
 */
public class RelevesBancaire {

	private DailyBankState dailyBankState;
	private Stage primaryStage;

	private Client client;
	private CompteCourant compteCourant;

	private Date dateDebut;
	private Date dateFin;

	// Valeur définitives concernant la taille des titres et sous-titres et petit texte

	private static float TITRE = 20;
	private static float SOUS_TITRE = 16;
	private static float PETIT_TEXTE = 8;


	public RelevesBancaire(DailyBankState _dailyBankState , Stage _primaryStage,Client client, CompteCourant compteCourant ,Date dateDebut, Date dateFin) {
		this.dailyBankState = _dailyBankState;
		this.primaryStage = _primaryStage;
		this.client = client;
		this.compteCourant = compteCourant;
		this.dateDebut = dateDebut;
		this.dateFin = dateFin;
	}

	/**
	 * Méthode permettant de générer le relevé bancaire associé au compteCourant depuis une date dateDebut jusqu'à une date dateFin
	 *
	 * @param nomFichier
	 * @return
	 */
	public Document genererReleveBancaire(String nomFichier) {

		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

		Paragraph barre = new Paragraph("_____________________________________________________________________________\n");
		barre.setAlignment(Element.ALIGN_CENTER);

		try {
			// Initialiser le document PDF

			Document document = new Document();
			PdfWriter pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(nomFichier));


			document.addTitle("Relevé Bancaire");
			document.addSubject("Relevé Bancaire");
			document.addCreator(dailyBankState.getEmployeActuel().nom +" "+ dailyBankState.getEmployeActuel().prenom);
			document.addCreationDate();
			document.addHeader("Relevé Bancaire"
					, "Relevé bancaire compte : " + this.compteCourant.idNumCompte
					+ " du "+dateFormat.format(dateDebut)+" au "+dateFormat.format(this.dateFin));

			// Ouvrir le document
			document.open();


			// Ajouter le titre
			Paragraph titre = new Paragraph("Relevé bancaire",FontFactory.getFont(FontFactory.HELVETICA_BOLD, TITRE));
			titre.setAlignment(Element.ALIGN_CENTER);
			document.add(titre);

			// Ajouter le sous-titre avec les informations de période
			String periode = "Période : " + dateFormat.format(dateDebut) + " - " + dateFormat.format(this.dateFin);
			Paragraph sousTitre = new Paragraph(periode,FontFactory.getFont(FontFactory.HELVETICA_BOLD, SOUS_TITRE));
			sousTitre.setAlignment(Element.ALIGN_CENTER);
			document.add(sousTitre);

			document.add(new Paragraph("\n"));
			document.add(barre);

			// Ajouter les informations du compte bancaire
			document.add(new Paragraph("Informations du compte",FontFactory.getFont(FontFactory.HELVETICA_BOLD, SOUS_TITRE))) ;
			document.add(new Paragraph("Numéro de compte : " + this.compteCourant.idNumCompte));
			document.add(new Paragraph("Découvert autorisé : " + this.compteCourant.debitAutorise));
			document.add(new Paragraph("Solde actuel : " + this.compteCourant.solde));

			String tmpCloturerOuOuvert = "Ouvert";
			if (this.compteCourant.estCloture.equals("O")) {
				tmpCloturerOuOuvert = "Clôturé";
			}
			document.add(new Paragraph("Etat du compte : " + tmpCloturerOuOuvert));

			document.add(barre);

			// Ajouter les informations du client
			document.add(new Paragraph("Informations du client",FontFactory.getFont(FontFactory.HELVETICA_BOLD, SOUS_TITRE))) ;
			document.add(new Paragraph("Numéro client : " + this.client.idNumCli));

			String tmpActifouInactif = "Inactif";
			if (ConstantesIHM.estActif(this.client)) {
				tmpActifouInactif = "Actif";
			}
			document.add(new Paragraph("Etat du client : " + tmpActifouInactif));


			document.add(new Paragraph("Nom : " + this.client.nom));
			document.add(new Paragraph("Prenom : " + this.client.prenom));
			document.add(new Paragraph("Adresse : " + this.client.adressePostale));
			document.add(new Paragraph("Email : " + this.client.email));

			// Ajouter le tableau des opérations du relevé bancaire


			document.add(barre);
			document.add(new Paragraph("\n"));

			titre = new Paragraph("Opérations du compte durant la période",FontFactory.getFont(FontFactory.HELVETICA_BOLD, TITRE));
			titre.setAlignment(Element.ALIGN_CENTER);
			document.add(titre) ;

			document.add(new Paragraph("\n"));

			document.add(creaTabOperations());

			document.add(barre);
			document.add(new Paragraph("\n\n"));

			// Ajout du pied de page

			Paragraph footer = new Paragraph("DailyBank\n"
					+ "La banque DailyBank n'est pas responsable en cas de mauvaise affichage des opérations de votre compte\n"
					+ "Si vous pensez constater une erreur dans votre relevé bancaire veuillez contacter votre conseiller\n"
					+ "Relevé bancaire généré par ordinateur par le programme DailyBankApp le : " + dateFormat.format(new Date())
					,FontFactory.getFont(FontFactory.HELVETICA_BOLD,PETIT_TEXTE));
			footer.setAlignment(Element.ALIGN_CENTER);
			document.add(footer);

			
			// Fermeture du document et du writer

			document.close();
			pdfWriter.close();

			return document;

		}
		catch (DocumentException e ) {
			AlertUtilities.showAlert(primaryStage, "Erreur dans la création du fichier"
					,"Le fichier n'a pas pu être créé"
					, "Votre fichier de relevé : " + nomFichier + "n'a pas pu être créé"
					, AlertType.ERROR);
			
		}
		catch(FileNotFoundException e) {
			AlertUtilities.showAlert(primaryStage, "Erreur dans la création du fichier"
					,"Le chemin du fichier n'a pas été trouvé"
					, "Votre fichier de relevé : " + nomFichier + "n'a pas pu être créé"
					, AlertType.ERROR);
			
		}
		return null;
	}

	/**
	 * Méthode permettant de créer le tableau des operations d'un relevé bancaire
	 *
	 * @return tableau d'opération
	 */
	private PdfPTable creaTabOperations () {
		try {
			DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

			// Créer le tableau des opérations
			PdfPTable table = new PdfPTable(new float[]{2, 2, 4, 2, 2,2});
			table.setWidthPercentage(100);

			// En-têtes du tableau
			table.addCell(createCell("Date opération", true));
			table.addCell(createCell("Date Valeur", true));
			table.addCell(createCell("Type d'opération", true));
			table.addCell(createCell("Crédit", true));
			table.addCell(createCell("Débit", true));
			table.addCell(createCell("Solde", true));


			// Récupération de toutes les opérations de la période
			ArrayList<Operation> alOperationsPeriode = this.getOperationsPeriode();
			Operation opDebut = alOperationsPeriode.get(0);
			Operation opFin = alOperationsPeriode.get(alOperationsPeriode.size()-1);

			// Somme des crédits/débits

			double sommeDebits = 0 ;
			double sommeCredits = 0;
			double soldeActuel = opDebut.montant;


			// Rajout du solde initial

			createLine(table,"","",opDebut.idTypeOp,"","",String.format("%10.02f", opDebut.montant),true);



			// Création des lignes pour chaque opération
			for (int i = 1 ; i < alOperationsPeriode.size()-1 ; i++) {

				Operation tmpOperation = alOperationsPeriode.get(i);

				soldeActuel += tmpOperation.montant;

				if (tmpOperation.montant >= 0) {

					createLine(table
							,dateFormat.format(tmpOperation.dateOp),dateFormat.format(tmpOperation.dateValeur)
							,tmpOperation.idTypeOp,String.format("%10.02f", tmpOperation.montant),""
							,String.format("%10.02f", soldeActuel),false)
					;


					sommeCredits += tmpOperation.montant;
				}
				else {

					createLine(table
							,dateFormat.format(tmpOperation.dateOp),dateFormat.format(tmpOperation.dateValeur)
							,tmpOperation.idTypeOp,"",String.format("%10.02f", tmpOperation.montant)
							,String.format("%10.02f", soldeActuel),false)
					;
					sommeDebits += tmpOperation.montant;
				}

			}

			//Rajout du total des opérations

			createLine(table,"","","Total des opérations : "
					,String.format("%10.02f", sommeCredits),String.format("%10.02f", sommeDebits)
					,"",true);

			// Rajout du solde à la fin

			createLine(table,"","",opFin.idTypeOp,"","",String.format("%10.02f", opFin.montant),true);



			return table;
		}
		catch (DataAccessException |DatabaseConnexionException e ) {
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, e);
			ed.doExceptionDialog();
			return null;
		}
	}

	/**
	 * Permet de créer une cellule à mettre dans une table PdfPTable
	 *
	 * @param content Contenu à mettre dans la cellule
	 * @param principale La cellule est est-elle une cellule principale
	 *
	 * @return la cellule créée
	 */
	private static PdfPCell createCell(String content, boolean principale) {
		PdfPCell cell = new PdfPCell(new Phrase(content, principale ? FontFactory.getFont(FontFactory.HELVETICA_BOLD) : FontFactory.getFont(FontFactory.HELVETICA)));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setPadding(6);
		if (principale) {
			cell.setBackgroundColor(com.itextpdf.text.BaseColor.LIGHT_GRAY);
		}
		return cell;
	}

	/**
	 * Permet de créer une ligne dans la pdfTable table représentant un tableau d'opérations d'un relevé bancaire
	 *
	 * @param table tableau d'opérations
	 * @param dateEnregistrement Date d'enregistrement de l'opération
	 * @param dateValeur Date de valeur de l'operation
	 * @param typeOp Type de l'opération
	 * @param credit Valeur de crédit
	 * @param debit Valeur de débit
	 * @param solde Valeur du solde
	 * @param principale La ligne est-elle une ligne principale
	 */
	private static void createLine(PdfPTable table ,String dateEnregistrement, String dateValeur, String typeOp, String credit, String debit, String solde, boolean principale){
		table.addCell(createCell(dateEnregistrement, principale));
		table.addCell(createCell(dateValeur, principale));
		table.addCell(createCell(typeOp, principale));
		table.addCell(createCell(credit, principale));
		table.addCell(createCell(debit, principale));
		table.addCell(createCell(solde, principale));
	}

	/**
	 * Permet de récupérer toutes les opérations de compteCourant avec leurs dates de valeur entre dateDebut et dateFin avec dateFin compris
	 *
	 * @return ArrayList<Operation> ArrayList d'opérations entre dateDebut et dateFin avec dateFin compris
	 *
	 * @throws DatabaseConnexionException
	 * @throws DataAccessException
	 */
	private ArrayList<Operation> getOperationsPeriode () throws DataAccessException, DatabaseConnexionException{
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

		// Déclaration d'une arrayList temporaire pour stocker les opérations
		ArrayList<Operation> tmpAlOperations;
		// Initialisation de l'arrayList
		ArrayList<Operation> alOperationsPeriode = new ArrayList<>();

		// Création de l'opération étant le solde de départ
		Operation opSoldeDebut = new Operation(-2, 0, null, null, this.compteCourant.idNumCli , "Solde au "+ dateFormat.format(this.dateDebut));
		Operation opSoldeFin = new Operation(-3, 0, null, null, this.compteCourant.idNumCli , "Solde au "+ dateFormat.format(this.dateFin));
		alOperationsPeriode.add(opSoldeDebut);

		// Instanciation d'un accès à la BD
		Access_BD_Operation accBdOperation = new Access_BD_Operation();

		// Récupération des opérations

		tmpAlOperations = accBdOperation.getOperations(this.compteCourant.idNumCompte);

		//Inilisation de somme qui permettra de calculer le dépot à l'ouverture du compte
		double soldeTotalOperations = 0;

		// Récupération des opération intéressantes dans l'arraylist à retourner et calcul du montant de la solde
		for (Operation tmpOperation : tmpAlOperations) {

			if (tmpOperation.dateValeur.compareTo(this.dateDebut) > 0 & tmpOperation.dateValeur.compareTo(this.dateFin) <= 0) {

				alOperationsPeriode.add(tmpOperation);
				opSoldeFin.montant += tmpOperation.montant;
			}

			else if (tmpOperation.dateValeur.compareTo(this.dateDebut) < 0) {
				opSoldeDebut.montant += tmpOperation.montant;
			}

			soldeTotalOperations += tmpOperation.montant;

		}
		double montantDepotInitial = this.compteCourant.solde - soldeTotalOperations;

		opSoldeDebut.montant += montantDepotInitial;
		opSoldeFin.montant += opSoldeDebut.montant;

		alOperationsPeriode.add(opSoldeFin);

		return alOperationsPeriode;
	}

}