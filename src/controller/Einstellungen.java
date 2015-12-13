package controller;

import java.awt.Color;
import java.text.DecimalFormat;
import java.util.Locale;
import java.util.Observable;
import java.util.prefs.Preferences;

import datenverarbeitung.DiagrammRendererFactory;
import datenverarbeitung.Einheit;

/**
 * Verwaltet diverse Programmeinstellungen. Diese werden beim Setzen
 * benutzerspezifisch in der Registry gesichert und Observer werden informiert.
 * 
 * @author Manuel Weber
 */
public class Einstellungen extends Observable {

	// Singleton-Pattern ---------------------------------------------
	private static Einstellungen singletonInstanz;

	public static Einstellungen getEinstellungen() {

		if (singletonInstanz == null) {
			singletonInstanz = new Einstellungen();
		}
		return singletonInstanz;
	}

	private Einstellungen() {
		laden();
	}

	// ---------------------------------------------------------------

	/**
	 * Knoten unter dem in der Registy die Einstellungen gespeichert oder
	 * ausgelesen werden.
	 */
	private final Preferences registry = Preferences.userRoot()
			.node("SOFTWARE").node("VisTrain");

	/**
	 * Umfang des Messrades in cm
	 */
	private double messradumfang;

	/**
	 * Anzahl der Markierungen auf dem Messrad
	 */
	private int markierungen;

	/**
	 * Anzahl der Messwerte, die zu einem Punkt im st/vt-Diagramm
	 * zusammengefasst werden sollen
	 */
	private int kompressionsfaktor;

	/**
	 * Benutzerdefinierte Farben der Diagrammkurven
	 */
	private int diagrammFarben[] = new int[5];

	private final int standardfarben[] = { 16721960, // rot
			2631935, // blau
			3342130, // gruen
			16711935, // lila
			0 }; // schwarz

	/**
	 * Wahrheitswert, ob selbststaendig nach einer Verbindung zu RMX-PC-Zentrale
	 * und Messstand gesucht werden soll
	 */
	private boolean autoConnect;

	/**
	 * Wahrheitswert, ob die im Messungsmenue angegebene Messdauer als
	 * Gesamtdauer fuer die Messung oder als Dauer pro Fahrstufe aufgefasst
	 * werden soll
	 */
	private boolean messdauerProFS;

	/**
	 * Zeit am Anfang einer Messung, bevor die Messwertaufnahme startet (in
	 * Millisekunden)
	 */
	private long vorbereitungszeit;

	/**
	 * Zeit beim Umschalten von Fahrstufen waehrend der Messung, fuer die die
	 * Messwertaufnahme aussetzt (in Millisekunden)
	 */
	private long vorbereitungszeitProFS;

	/**
	 * Einheit fuer zurueckgelegten Weg (Standardeinheit ist Zentimenter)
	 */
	private Einheit wegeinheit;

	/**
	 * Einheit fuer verstrichene Zeit (Standardeinheit ist Sekunde)
	 */
	private Einheit zeiteinheit;

	/**
	 * Massstab (1:?) zur Umrechnung vom Modell zum Vorbild
	 */
	private double massstab;

	/**
	 * RendererTyp der zum rendern des st- bzw. vt-Diagramms genutzt werden soll
	 */
	private DiagrammRendererFactory.RendererTyp xyRendererTyp;

	/**
	 * Zahlenformat mit deutschem Komma und Tausendertrennpunkt, gerundet auf 2
	 * Nachkommastellen
	 */
	public static final DecimalFormat zahlenformat = (DecimalFormat) DecimalFormat
			.getInstance(Locale.GERMANY);
	static {
		zahlenformat.setMaximumFractionDigits(2);
		zahlenformat.setGroupingSize(3);
	}

	/**
	 * Laedt die Einstellungen aus der Registry.
	 */
	private void laden() {
		messradumfang = registry.getFloat("Messradumfang", (float) 1.96);
		markierungen = registry.getInt("Markierungen", 1);
		kompressionsfaktor = registry.getInt("Kompressionsfaktor", 1);
		autoConnect = registry.getBoolean("autoConnect", false);
		messdauerProFS = registry.getBoolean("messdauerProFS", false);
		vorbereitungszeit = registry.getLong("Vorbereitungszeit", 0);
		vorbereitungszeitProFS = registry
				.getLong("Vorbereitungszeit pro FS", 0);
		massstab = registry.getDouble("Massstab", 1);
		setWegeinheit(Einheit.getEinheit(registry.get("Wegeinheit", "cm")));
		zeiteinheit = Einheit.getEinheit(registry.get("Zeiteinheit", "s"));
		xyRendererTyp = DiagrammRendererFactory.RendererTyp.valueOf(registry
				.get("xyRendererTyp", "SplineRenderer"));
		// Farben
		for (int i = 0; i < diagrammFarben.length; i++) {
			diagrammFarben[i] = registry.getInt("DiagrammFarbe" + i,
					standardfarben[i]);
		}
	}

	/* Setter - Methoden (setzen auch in der Registry) */

	public void setMessradumfang(double messradumfang) {
		this.messradumfang = messradumfang;
		registry.putDouble("Messradumfang", messradumfang);
	}

	public void setMarkierungen(int markierungen) {
		this.markierungen = markierungen;
		registry.putInt("Markierungen", markierungen);
	}

	public void setKompressionsfaktor(int kompressionsfaktor) {
		this.kompressionsfaktor = kompressionsfaktor;
		registry.putInt("Kompressionsfaktor", kompressionsfaktor);
	}

	public void setDiagrammFarbe(int datenreiheNr, Color farbe) {
		diagrammFarben[datenreiheNr] = farbe.getRGB();
		registry.putInt("DiagrammFarbe" + datenreiheNr,
				diagrammFarben[datenreiheNr]);
	}

	public void setAutoConnect(boolean autoConnect) {
		this.autoConnect = autoConnect;
		registry.putBoolean("autoConnect", autoConnect);
	}

	public void setMessdauerProFS(boolean messdauerProFS) {
		this.messdauerProFS = messdauerProFS;
		registry.putBoolean("messdauerProFS", messdauerProFS);
	}

	public void setVorbereitungszeit(long vorbereitungszeit) {
		this.vorbereitungszeit = vorbereitungszeit;
		registry.putLong("Vorbereitungszeit", vorbereitungszeit);
	}

	public void setVorbereitungszeitProFS(long vorbereitungszeitProFS) {
		this.vorbereitungszeitProFS = vorbereitungszeitProFS;
		registry.putLong("Vorbereitungszeit pro FS", vorbereitungszeitProFS);
	}

	/**
	 * Setzt die Wegeinheit und ergaenzt diese dabei um den Massstab
	 * 
	 * @param wegeinheit
	 */
	public void setWegeinheit(Einheit wegeinheit) {
		this.wegeinheit = new Einheit(wegeinheit.toString(),
				wegeinheit.getUmrechnungskoeffizient() / massstab);
		registry.put("Wegeinheit", wegeinheit.toString());
	}

	public void setZeiteinheit(Einheit zeiteinheit) {
		this.zeiteinheit = zeiteinheit;
		registry.put("Zeiteinheit", zeiteinheit.toString());
		setChanged();
		notifyObservers();
	}

	/**
	 * Setzt den Massstab und aendert damit auch die Wegeinheit
	 * 
	 * @param massstab
	 */
	public void setMassstab(double massstab) {
		this.massstab = massstab;
		registry.putDouble("Massstab", massstab);
		setWegeinheit(Einheit.getEinheit(wegeinheit.toString()));
	}

	public void setXyRendererTyp(
			DiagrammRendererFactory.RendererTyp xyRendererTyp) {
		this.xyRendererTyp = xyRendererTyp;
		registry.put("xyRendererTyp", xyRendererTyp.toString());
	}

	/* Getter - Methoden */

	/**
	 * @return Laenge einer gemessenen Einheit in cm (Messradumfang geteilt
	 *         durch die Anzahl Markierungen)
	 */
	public double getMesseinheit() {
		return Double.valueOf(messradumfang) / Double.valueOf(markierungen);
	}

	public double getMessradumfang() {
		return messradumfang;
	}

	public int getMarkierungen() {
		return markierungen;
	}

	public int getKompressionsfaktor() {
		return kompressionsfaktor;
	}

	public Color getDiagrammFarbe(int datenreiheNr) {
		return new Color(Integer.valueOf(diagrammFarben[datenreiheNr]));
	}

	public boolean isAutoConnect() {
		return autoConnect;
	}

	public boolean isMessdauerProFS() {
		return messdauerProFS;
	}

	public long getVorbereitungszeit() {
		return vorbereitungszeit;
	}

	public Einheit getWegeinheit() {
		return wegeinheit;
	}

	public Einheit getZeiteinheit() {
		return zeiteinheit;
	}

	public DiagrammRendererFactory.RendererTyp getXyRendererTyp() {
		return xyRendererTyp;
	}

	public double getMassstab() {
		return massstab;
	}

	public long getVorbereitungszeitProFS() {
		return vorbereitungszeitProFS;
	}
}
