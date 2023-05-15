package application.tools;

/**
 *
 * Mode d'édition possibles pour des données (client, employé, compte, ...). 3
 * modes définis : - CREATION : l'employé va créer une nouvelle donnée. La
 * fenêtre sera a priori vide. L'employé pourra saisir et valider ou annuler. -
 * MODIFICATION : l'employé va modifier une donnée existante. La fenêtre
 * affichera au départ la donnée à modifier. L'employé pourra saisir et valider
 * ou annuler. - SUPPRESSION : l'employé va supprimer une donnée La fenêtre
 * affichera au départ la donnée à supprimer. Pas de saisie possible. L'employé
 * pourra valider ou annuler.
 */
public enum EditionMode {
	CREATION, MODIFICATION, SUPPRESSION
}