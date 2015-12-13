package loksteuerung;

/**
 * Interface fuer Connector-Klassen fuer spezielle Loksysteme. Der Connector
 * muss als Singleton implementiert werden.
 * 
 * Achtung: Die GUI muss ueber den Verbindungsstatus und hinzukommende Loks
 * informiert werden:
 * 
 * GuiAktualisieren.setVerbindungsstatus(); nach Verbindungsaufbau / -abbruch
 * 
 * GuiAktualisieren.lokHinzufuegen(); wenn eine neue Lok hinzugefuegt wird
 * 
 * @author Manuel Weber
 */
public interface ILoksystemConnector {

	// Konstanten des Verbindungsstatus
	public static final int NULL = 0;
	public static final int VERBINDEN = 1;
	public static final int VERBUNDEN = 2;
	public static final int TRENNEN = 3;
	public static final int GETRENNT = 4;

	/**
	 * Versucht eine Verbindung zum Loksystem aufzubauen.
	 */
	public void verbinden();

	/**
	 * Trennt eine Verbindung zum Loksystem.
	 */
	public void trennen();

	/**
	 * @return momentaner Verbindungsstautus
	 */
	public int getVerbindungsStatus();

	/**
	 * Gibt die Loknummer einer bestimmten Lok zurueck.
	 * 
	 * @param lokname
	 * @return loknummer
	 * @throws TrainNotFoundException
	 *             Wenn keine entsprechende Lok gefunden werden kann
	 */
	public int getLoknummer(String lokname) throws TrainNotFoundException;

	/**
	 * Gibt die maximale Fahrstufe einer bestimmten Lok zurueck.
	 * 
	 * @param lokname
	 * @return maximale Fahrstufe
	 * @throws TrainNotFoundException
	 *             Wenn keine entsprechende Lok gefunden werden kann
	 */
	public int getMaximaleFahrstufe(String lokname)
			throws TrainNotFoundException;
}