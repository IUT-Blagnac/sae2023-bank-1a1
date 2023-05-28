package application.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import com.itextpdf.text.DocumentException;

import application.DailyBankState;
import model.data.Client;
import model.data.CompteCourant;
import model.orm.Access_BD_Client;
import model.orm.Access_BD_CompteCourant;
import model.orm.exception.DataAccessException;
import model.orm.exception.DatabaseConnexionException;

/**
 * Classe permettant de réaliser toutes les opération du Batch c'est à dire, générer l'ensemble des relevés des comptes de l'agence de l'employé en cours
 * 
 * Pour cela un fichier de logs est créé afin de pouvoir y noter toutes les actions réalisé et les erreurs si il y en a 
 * 
 * @author illan
 *
 */
public class Batch implements Runnable{

	private DailyBankState dailyBankState;

	public Batch(DailyBankState _dailyBankState ) {

		this.dailyBankState = _dailyBankState;
	}

	@Override
	/**
	 * Méthode permettant de lancer le programme batch et de sauvegarder dans le fichier fichierLogs des logs des différentes actions réalisés et de leurs prolèmes éventuels 
	 * 
	 * <BR>
	 * 
	 * Si il est impossible de créer le fichier de logs alors un message est affiché dans la console et le fichier de logs n'est pas créé et le programme s'arrête 
	 */
	public void run() {
		System.out.println("run");
		try {
			DateTimeFormatter formatterDateEnFr = DateTimeFormatter
					.ofPattern("EEEE d MMMM yyyy HH:mm:ss", new Locale("fr"));


			File fichierLogs = new File("logBatch.txt");
			FileOutputStream fichierLogsOutputStream = new FileOutputStream(fichierLogs, true); // true est pour que le fichier n'écrase pas le fichier existant
			PrintStream outputLogs = new PrintStream(fichierLogsOutputStream);
			
			outputLogs.println(formatterDateEnFr.format(LocalDateTime.now())+"\n");
			outputLogs.println("Création pour l'agence "+dailyBankState.getAgenceActuelle().nomAg+" de tous les relevés de tous les comptes pour le mois précédent\n");
			try {
				this.creationReleves();
			} catch (DataAccessException | DatabaseConnexionException | IOException | DocumentException e) {
				outputLogs.println(new Date()+"\n");
				outputLogs.println(e.getStackTrace());
				outputLogs.close();
				return;
			}
			outputLogs.println(formatterDateEnFr.format(LocalDateTime.now())+"\n");
			outputLogs.println("Les relevés ont bien été créés\n");
			outputLogs.close();

		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			return;
		}
	}

	/**
	 * Méthode permettant de génrer les relevés de tous les comptes de l'agence de l'employé en cours 
	 * 
	 * @throws DatabaseConnexionException  Si une erreur de connexion à lieu
	 * @throws DataAccessException  S'il est impossible d'accéder à une donnée
	 * @throws IOException S'il une erreur a lieu lors la création des fichier ou répertoire des relevés
	 * @throws DocumentException S'il y a eut une erreur lors de l'écriture d'un document
	 * 
	 */
	private void creationReleves() throws DataAccessException, DatabaseConnexionException, IOException, DocumentException {
		Access_BD_Client ac_BD_Client = new Access_BD_Client();
		Access_BD_CompteCourant ac_BD_CompteCourant = new Access_BD_CompteCourant();


		// Date actuelle
		LocalDate dateActuelle = LocalDate.now();

		// Mois précédent
		LocalDate moisPrecedent = dateActuelle.minusMonths(1);

		// Premier jour du mois précédent
		LocalDate premierJour = moisPrecedent.withDayOfMonth(1);
		LocalDateTime premierJourDateTime = premierJour.atStartOfDay();
		Date premierJourDate = Date.from(premierJourDateTime.atZone(ZoneId.systemDefault()).toInstant());

		// Dernier jour du mois précédent
		LocalDate dernierJour = moisPrecedent.withDayOfMonth(moisPrecedent.lengthOfMonth());
		LocalDateTime dernierJourDateTime = dernierJour.atTime(23, 59, 59);
		Date dernierJourDate = Date.from(dernierJourDateTime.atZone(ZoneId.systemDefault()).toInstant());

		ArrayList<Client> alClient = ac_BD_Client.getClients(dailyBankState.getAgenceActuelle().idAg, -1, "", "");

		this.creationArbo(alClient);

		RelevesBancaire releveTmp;
		for (Client clientDeLagence : alClient) {
			for(CompteCourant compteDuClient : ac_BD_CompteCourant.getCompteCourants(clientDeLagence.idNumCli)) {
				File fichierReleveACreer = new File("Relevés-PDF_DAILYBANK-"+dailyBankState.getAgenceActuelle().nomAg+"/Client-"+clientDeLagence.idNumCli+"-"+clientDeLagence.nom+"_"+clientDeLagence.prenom+"/Compte-"+compteDuClient.idNumCompte+".pdf");
				releveTmp = new RelevesBancaire(dailyBankState, clientDeLagence, compteDuClient, premierJourDate, dernierJourDate);
				releveTmp.genererReleveBancaire(fichierReleveACreer);
			}
		}


	}

	/**
	 * Créateur de l'arborescence de répertoires où seront enregistré l'ensemble des relevés bancaires de tous les clients d'une agence <BR>
	 * S'il existait une arborescence elle est remplacé.<BR>
	 * 
	 * Elle est constitué d'un répertoire racine "Relevés-PDF_DAILYBANK" et pour chaque client un répertoire "Client-idClient-nomClient_prenomClient"
	 * 
	 * @param clients Clients de l'agence
	 * 
	 * @throws IOException Si il y a un problème lors de la création de l'arborescence
	 * 
	 */
	private void creationArbo(ArrayList<Client> clients) throws IOException{

		if (clients.isEmpty()) {
			return;
		}

		ArrayList<File> tabFolderAcreer  = new ArrayList<File>();

		File rootDirectory = new File("Relevés-PDF_DAILYBANK-"+dailyBankState.getAgenceActuelle().nomAg);
		tabFolderAcreer.add(rootDirectory);

		// Création des chemins pour les répertoires de chaque client

		for (Client client : clients) {
			File folder = new File(rootDirectory.getPath()+"/Client-"+client.idNumCli+"-"+client.nom+"_"+client.prenom);
			tabFolderAcreer.add(folder);
		}


		// Création des dossier pour chaque client

		for (File folder : tabFolderAcreer)
		{
			supprimerFichierRec(folder);

			if (!folder.mkdirs()) {
				throw new IOException("Impossible de créer le fichier "+folder);
			} 
		}
	}
	/**
	 * Méthode récursive permettant de supprimer les fichier et si c'est un répertoire tous les fichier qu'il contient
	 * 
	 * @param fichier Fichier à supprimer
	 * @throws IOException S'il est impossible de supprimer fichier
	 */
	private void supprimerFichierRec(File fichier) throws IOException {
		if (fichier.isDirectory()) {
			for (File fichierFils : fichier.listFiles()) {
				supprimerFichierRec(fichierFils);
			}
		}
		if (fichier.exists()) {
			if (!fichier.delete()) {
				throw new IOException("Impossible de supprimer le fichier "+fichier);
			}
		}
		return;
	}
}