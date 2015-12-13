package datenaufnahme;

import gui.GuiAktualisieren;
import loksteuerung.Bewegungsablauf;
import loksteuerung.RMXconnector;
import loksteuerung.TrainNotFoundException;
import controller.Einstellungen;
import controller.Funktionen;
import controller.Main;
import datenverarbeitung.Plotter;

/**
 * Definiert den Messvogang, der in einem separaten Thread durchgefuehrt werden
 * kann. Eine gestartete Messung startet Plotter und Bewegungsablauf selbst. Vor
 * Start des Bewegungsablaufs faehrt die Lok eine benutzerdefinierte Zeit lang
 * an. Bewegungsablauf und Messwertaufnahme starten mit dem ersten Impuls nach
 * dieser Vorbereitungszeit. Ist die Startfahrstufe 0, so wird das Messrad
 * zunaechst auf 1 bzw. -1 in Position gedreht. Ein interrupt beendet auch
 * Bewegungsablauf und Plotter kontrolliert.
 * 
 * @author Manuel Weber
 */
public class Messung extends Thread {

	/**
	 * Messreihe in der die gemessenen Daten gespeichert werden und die Vorgaben
	 * fuer die Messung enthaelt.
	 */
	private Messreihe messreihe;

	/**
	 * Bewegungsablauf den die Lok ausfuehren soll.
	 */
	private Bewegungsablauf bewegungsablauf;

	/**
	 * Plotter der die Messung in Echtzeit plottet
	 */
	private Plotter plotter;

	/**
	 * Wahrheitswert, ob ein Verbindungsfehler aufgetreten ist und die
	 * Verbindung nach Abbruch der Messung getrennt werden muss
	 */
	private boolean verbindungsfehler = false;

	/**
	 * Zeitpunkt an dem die Messung begonnen hat
	 */
	private long startzeit = 0;

	/**
	 * Dauer der Messung (inklusive Einfahrzeiten fuer Fahrstufen)
	 */
	private long dauer;

	private int impulse;
	private int status;
	private int letzterStatus;

	public Messung(Messreihe messreihe) throws TrainNotFoundException {
		this.messreihe = messreihe;
		this.bewegungsablauf = new Bewegungsablauf(messreihe.getLokname(),
				messreihe.getFahrstufe0(), messreihe.getFahrstufe1(), dauer,
				messreihe.isGleichmaessigBeschleunigt());
		this.plotter = new Plotter(messreihe, null);
	}

	@Override
	public void run() {

	
	}
}