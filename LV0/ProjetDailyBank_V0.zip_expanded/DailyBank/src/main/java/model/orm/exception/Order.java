package model.orm.exception;

/**
 * Ordres (opérations) possibles sur la base de données
 */
public enum Order {

	SELECT, UPDATE, DELETE, INSERT,

	OTHER // Accès à la BD hors des 4 cas ci-dessus (par ex connexion ...)
}
