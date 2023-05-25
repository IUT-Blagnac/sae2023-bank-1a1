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
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import application.DailyBankState;
import application.control.ExceptionDialog;
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
	


	public RelevesBancaire(DailyBankState _dailyBankState , Stage _primaryStage,Client client, CompteCourant compteCourant ,Date dateDebut, Date dateFin) {
		this.dailyBankState = _dailyBankState;
		this.primaryStage = _primaryStage;
		this.client = client;
		this.compteCourant = compteCourant;
		this.dateDebut = dateDebut;
		this.dateFin = dateFin;
	}

	public Document genererReleveBancaire(String nomFichier) {

		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

		try {
			// Initialiser le document PDF

			Document document = new Document();
			//PdfWriter pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(nomFichier));


			document.addTitle("Relevé Bancaire");
			document.addSubject("Relevé Bancaire");
			document.addCreator(dailyBankState.getEmployeActuel().nom +" "+ dailyBankState.getEmployeActuel().prenom);
			document.addCreationDate();
			document.addHeader("Relevé Bancaire"
					, "Relevé bancaire compte : " + compteCourant.idNumCompte
					+ " du "+dateFormat.format(dateDebut)+" au "+dateFormat.format(dateFin));

			// Ouvrir le document
			document.open();


			// Ajouter le titre
			Paragraph titre = new Paragraph("Relevé bancaire");
			titre.setAlignment(Element.ALIGN_CENTER);
			Font titreFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20);
			titre.setFont(titreFont);
			document.add(titre);


			// Ajouter le sous-titre avec les informations de période
			String periode = "Période : " + dateFormat.format(dateDebut) + " - " + dateFormat.format(dateFin);
			Paragraph sousTitre = new Paragraph(periode);
			sousTitre.setAlignment(Element.ALIGN_CENTER);
			Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
			font.setStyle(Font.BOLD);
			sousTitre.setFont(font);
			document.add(sousTitre);

			document.add(new Paragraph("\n"));

			// Ajouter les informations du compte bancaire
			document.add(new Paragraph("Informations du compte"));
			document.add(new Paragraph("Numéro de compte : " + compteCourant.idNumCompte));
			document.add(new Paragraph("Découvert autorisé : " + compteCourant.debitAutorise));
			document.add(new Paragraph("Solde actuel : " + compteCourant.solde));

			String tmpCloturerOuOuvert = "Ouvert";
			if (compteCourant.estCloture.equals("O")) {
				tmpCloturerOuOuvert = "Clôturé";
			}
			document.add(new Paragraph("Etat du compte : " + tmpCloturerOuOuvert));

			document.add(new Paragraph("\n"));

			// Ajouter les informations du client
			document.add(new Paragraph("Informations du client"));
			document.add(new Paragraph("Numéro client : " + client.idNumCli));

			String tmpActifouInactif = "Inactif";
			if (ConstantesIHM.estActif(client)) {
				tmpActifouInactif = "Actif";
			}
			document.add(new Paragraph("Etat du client : " + tmpActifouInactif));


			document.add(new Paragraph("Nom : " + client.nom));
			document.add(new Paragraph("Adresse : " + client.adressePostale));
			document.add(new Paragraph("Email : " + client.email));
			document.add(new Paragraph("\n"));

			// Ajouter les opérations du relevé bancaire


			document.add(new Paragraph("Opérations du relevé bancaire"));
			document.add(new Paragraph("\n"));



			document.add(creaTabOperations());


			document.close();

			return document;

		} 
		catch (DocumentException /*| FileNotFoundException*/ e ) {
			return null;
		}

	}

	/**
	 * Méthode permettant de créé le tableau des operation
	 * 
	 * @return
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
							,"",tmpOperation.idTypeOp,String.format("%10.02f", tmpOperation.montant)
							,String.format("%10.02f", soldeActuel),false)
					;
					sommeDebits += tmpOperation.montant;
				}

			}

			// Rajout du solde à la fin

			createLine(table,"","",opDebut.idTypeOp,"","",String.format("%10.02f", opFin.montant),true);


			createLine(table,"","","Total des opérations : "
					,String.format("%10.02f", sommeCredits),String.format("%10.02f", sommeDebits)
					,"",true);
			return table;
		}
		catch (DataAccessException |DatabaseConnexionException e ) {
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, e);
			ed.doExceptionDialog();
			return null;
		}
	}

	private static PdfPCell createCell(String content, boolean isHeader) {
		PdfPCell cell = new PdfPCell(new Phrase(content, isHeader ? FontFactory.getFont(FontFactory.HELVETICA_BOLD) : FontFactory.getFont(FontFactory.HELVETICA, 10)));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		if (isHeader) {
			cell.setBackgroundColor(com.itextpdf.text.BaseColor.LIGHT_GRAY);
		}
		return cell;
	}

	private static void createLine(PdfPTable table ,String dateEnregistrement, String dateValeur, String typeOp, String credit, String debit, String solde, boolean header){
		table.addCell(createCell(dateEnregistrement, header));
		table.addCell(createCell(dateValeur, header));
		table.addCell(createCell(typeOp, header));
		table.addCell(createCell(credit, header));
		table.addCell(createCell(debit, header));
		table.addCell(createCell(solde, header));
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
		Operation opSoldeDebut = new Operation(-2, 0, null, null, compteCourant.idNumCli , "Solde au "+ dateFormat.format(dateDebut));
		Operation opSoldeFin = new Operation(-3, 0, null, null, compteCourant.idNumCli , "Solde au "+ dateFormat.format(dateFin));
		alOperationsPeriode.add(opSoldeDebut);

		// Instanciation d'un accès à la BD
		Access_BD_Operation accBdOperation = new Access_BD_Operation();

		// Récupération des opérations

		tmpAlOperations = accBdOperation.getOperations(compteCourant.idNumCompte);

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



