package loksteuerung;

import controller.Einstellungen;
import gui.Messungsmenue;

/**
 * Zentrale Schnittstelle fuer die Steuerung der Lok auf dem Pruefstand. Objekte
 * dieser Klasse stellen einen Bewegungsablauf dar, der waehrend der Messung in
 * einem separaten Thread ausgefuehrt wird. Am Ende wird die Endfahrstufe
 * gehalten, der Bewegungsablauf muss mit interrupt() beendet werden.
 * 
 * @author Manuel Weber
 */
public class Bewegungsablauf extends Thread {

	/**
	 * Steuerungsobjekt der Lok, die den Bewegungsablauf umsetzten soll
	 */
	private final Steuerung steuerung;

	/**
	 * Startfahrstufe am Anfang der Messung
	 */
	private int fahrstufe0;

	/**
	 * Fahrstufe am Ende der Messung
	 */
	private int fahrstufe1;

	/**
	 * Geplante Dauer des Bewegungsablaufs (inklusive eventueller Einfahrzeiten
	 * fuer Fahrstufen)
	 */
	private long dauer;

	/**
	 * Wahrheitswert ob gleichmaeﬂig beschleunigt werden soll. Bei false wird
	 * nach der Haelfte der Zeit sprunghaft beschleunigt. Bei true wird in
	 * gleichen Zeitabstaenden jede Fahrstufe von fahrstufe0 bis fahrstufe1
	 * durchgeschalten. (Bei konstanter Geschwindigkeit, wenn fahrstufe0 =
	 * fahrstufe1, spielt dieser parameter keine Rolle)
	 */
	private boolean gleichmaessigBeschleunigt;

	/**
	 * Fahrstufe in der sich die Lok momentan befindet
	 */
	private int fahrstufe;

	/**
	 * Gibt an ob sich der Bewegungsablauf momentan in einer Einfahrphase fuer
	 * eine Fahrstufe befindet
	 */
	private boolean einfahrphase = false;

	/**
	 * Dauer wie lange sich die Lok auf jeder Fahrstufe einfahren soll
	 */
	private long einfahrzeitProFs = Einstellungen.getEinstellungen()
			.getVorbereitungszeitProFS();

	public Bewegungsablauf(String lokname, int fahrstufe0, int fahrstufe1,
			long dauer, boolean gleichmaessigBeschleunigt) {
		this.steuerung = new Steuerung(lokname);
		this.fahrstufe = 0;
		this.fahrstufe0 = fahrstufe0;
		this.fahrstufe1 = fahrstufe1;
		this.dauer = dauer;
		this.gleichmaessigBeschleunigt = gleichmaessigBeschleunigt;
	}

	@Override
	public void run() {
		try {
			if (gleichmaessigBeschleunigt) {
				gleichmaessigeBeschleunigung();
			} else {
				abrupteBeschleunigung();
			}
		} catch (Exception e) {
			nothalt();
			return;
		}
		while (true) {
			if (Thread.interrupted()) {
				fahren(0);
				return;
			}
		}
	}

	/**
	 * Beschleunigt nach der halben Messdauer sprunghaft von fahrstufe0 auf
	 * fahrstufe1.
	 * 
	 * @param fahrstufe1
	 *            Fahrstufe auf die beschleunigt wird
	 * @param dauer
	 *            der Messung in Millisekunden
	 * @throws Exception
	 *             bei Nothalt
	 */
	private void abrupteBeschleunigung() throws Exception {

		long startzeit = System.currentTimeMillis();

		fahrstufeUmschalten(fahrstufe0);
		while (System.currentTimeMillis() < startzeit + dauer / 2) {
			if (Thread.interrupted()) {
				throw new Exception("Nothalt");
			}
		}
		fahrstufeUmschalten(fahrstufe1);
	}

	/**
	 * Beschleunigt, ausgedehnt ueber einen gegebenen Zeitraum, von fahrstufe0
	 * auf fahrstufe1.
	 * 
	 * @param fahrstufe0
	 *            Fahrstufe in der sich die Lok zu Beginn befinden soll
	 * @param fahrstufe1
	 *            Fahrstufe am Ende (auf die beschleunigt wird)
	 * @param dauer
	 *            der Messung in Millisekunden
	 * @throws Exception
	 *             bei Nothalt
	 */
	private void gleichmaessigeBeschleunigung() throws Exception {

		long endzeit = System.currentTimeMillis() + dauer;
		int fahrstufendifferenz = Math.abs(fahrstufe1 - fahrstufe0);

		int fs = fahrstufe0;

		while (fahrstufendifferenz >= 0) {

			fahrstufeUmschalten(fs);

			long t = System.currentTimeMillis();
			long verbleibendeZeit = endzeit - t;
			while (System.currentTimeMillis() < t
					+ (verbleibendeZeit / (fahrstufendifferenz + 1))) {
				if (Thread.interrupted()) {
					throw new Exception("Nothalt");
				}
			}

			if (fahrstufe1 > fahrstufe0) {
				fs++;
			} else {
				fs--;
			}
			fahrstufendifferenz--;
		}
	}

	/**
	 * Fungiert als Wrapper fuer die Methode Fahren.
	 * Schaltet auf eine neue Fahrstufe und blockiert dabei, bis die Einfahrzeit
	 * beendet ist. Waehrend diesem Zeitraum ist der Wahrheitswert 'einfahrphase'
	 * auf true.
	 * 
	 * @param fahrstufe
	 */
	private void fahrstufeUmschalten(int fahrstufe) {
		fahren(fahrstufe);
		long t = System.currentTimeMillis();
		einfahrphase = true;
		while (System.currentTimeMillis() < t + einfahrzeitProFs);
		einfahrphase = false;
	}

	/**
	 * Sendet einen Nothaltbefehl um Loks anzuhalten
	 */
	public void nothalt() {
		steuerung.nothalt();
		System.out.println("Lok angehalten");
	}

	/**
	 * Stellt eine Fahrstufe fuer die Lok ein und informiert die GUI
	 * 
	 * @param fahrstufe
	 */
	public void fahren(int fahrstufe) {
		this.fahrstufe = fahrstufe;
		steuerung.fahrstufeEinstellen(fahrstufe);
		Messungsmenue.getMessungsmenue().setFahrstufe(fahrstufe);
		System.out.println("FS " + fahrstufe);
	}

	public int getFahrstufe() {
		return fahrstufe;
	}

	public boolean isEinfahrphase() {
		return einfahrphase;
	}
}