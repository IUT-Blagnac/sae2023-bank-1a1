package application.tools;

/**
 * Classe qui définit une paire de valeurs appelées 'left' et 'right'.
 *
 * Elle permet d'encapsuler 2 valeurs de retour dans un seul objet pour
 * certaines méthodes qui nécessitent un retour de deux valeurs simultanément
 * (une méthode ne peut retourner que un seul objet).
 *
 * @param <T> Type de la première valeur (left)
 * @param <U> Type de la deuxième valeur (right)
 */
public class PairsOfValue<T, U> {
	private T left;
	private U right;

	public PairsOfValue(T _l, U _r) {
		this.left = _l;
		this.right = _r;
	}

	public T getLeft() {
		return this.left;
	}

	public U getRight() {
		return this.right;
	}
}
