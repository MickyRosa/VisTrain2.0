package datenverarbeitung;

import java.util.HashMap;
import java.util.Map;

/**
 * Ermoeglicht die Umrechnung von Werten aus den Standardeinheiten (Sekunde und
 * Zentimeter). Auf vordefinierte Einheitsobjekte kann statisch zugegriffen
 * werden.
 * 
 * @author Manuel Weber
 */
public class Einheit {

	public static final Einheit ZENTIMETER = new Einheit("cm", 1);
	public static final Einheit MILLIMETER = new Einheit("mm", 0.1);
	public static final Einheit METER = new Einheit("m", 100);
	public static final Einheit KILOMETER = new Einheit("km", 100000);

	public static final Einheit SEKUNDE = new Einheit("s", 1);
	public static final Einheit MINUTE = new Einheit("min", 60);
	public static final Einheit STUNDE = new Einheit("h", 3600);

	/**
	 * Dictionary zum "nachschlagen" von Einheiten
	 */
	private static Map<String, Einheit> einheiten = new HashMap<String, Einheit>();
	static {
		einheiten.put(ZENTIMETER.abkuerzung, ZENTIMETER);
		einheiten.put(MILLIMETER.abkuerzung, MILLIMETER);
		einheiten.put(METER.abkuerzung, METER);
		einheiten.put(KILOMETER.abkuerzung, KILOMETER);
		einheiten.put(SEKUNDE.abkuerzung, SEKUNDE);
		einheiten.put(MINUTE.abkuerzung, MINUTE);
		einheiten.put(STUNDE.abkuerzung, STUNDE);
	}

	/**
	 * Abkuerzung der Einheit
	 */
	private String abkuerzung;

	/**
	 * Koeffizient, durch den die Standardeinheit geteilt werden muss
	 */
	private double umrechnungskoeffizient;

	public Einheit(String abkuerzung, double umrechnungskoeffizient) {
		this.abkuerzung = abkuerzung;
		this.umrechnungskoeffizient = umrechnungskoeffizient;
	}

	@Override
	public String toString() {
		return abkuerzung;
	}

	/**
	 * Konvertiert einen Wert aus der Standardeiheit in die eigene Einheit
	 * 
	 * @param ausgangswert
	 *            (in cm bzw. s)
	 * @return wert in der jeweiligen Einheit
	 */
	public double konvertieren(double ausgangswert) {
		return ausgangswert / umrechnungskoeffizient;
	}

	/**
	 * Konvertiert einen Wert in der Einheit zurueck in die Standardeinheit
	 * 
	 * @param ausgangswert
	 *            in der Einheit des Objekts
	 * @return Wert in Standardeinheit
	 */
	public double rueckKonvertieren(double ausgangswert) {
		return ausgangswert * umrechnungskoeffizient;
	}

	/**
	 * @param abkuerzung
	 * @return die passende Einheit zu einer Abkuerzung
	 */
	public static Einheit getEinheit(String abkuerzung) {
		return einheiten.get(abkuerzung);
	}

	public double getUmrechnungskoeffizient() {
		return umrechnungskoeffizient;
	}
}
