package model.orm.exception;

/**
 * Classe générale des exceptions applicatives
 */

@SuppressWarnings("serial")
public class ApplicationException extends Exception {

	protected Table tablename;
	protected Order order;

	/**
	 * Constructeur.
	 *
	 * @param tablename nom de la table concernée
	 * @param order     ordre ayant échoué sur tablename
	 * @param message   message associé à l'erreur
	 * @param cause     éventuelle autre Exception associée (en cas de SQLException
	 *                  principalement)
	 */
	public ApplicationException(Table tablename, Order order, String message, Throwable cause) {
		super(message, cause);
		this.tablename = tablename;
		this.order = order;

		System.err.println("" + this.getClass().getName() + " -> " + this.getMessage());
	}

	@Override
	public String getMessage() {
		return super.getMessage() + " (" + this.tablename + " - " + this.order + " - " + this.getCause() + ")";
	}

	protected String getMessageAlone() {
		return super.getMessage();
	}

	public Table getTableName() {
		return this.tablename;
	}

	public Order getOrder() {
		return this.order;
	}

	// getCause () : héritée.
	// Ecrite ici UNIQUEMENT pour en disposer facilement

	@Override
	public Throwable getCause() {
		return super.getCause();
	}
}
