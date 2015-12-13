package loksteuerung;

/**
 * Steuert eine Lok mittels des RMXnet-Protokolls.
 * 
 * @author Manuel Weber
 */
public class Steuerung {

	/**
	 * Lokadresse der Lok, die gesteuert wird.
	 */
	private int loknummer;

	protected Steuerung(String lokname) {
		TrainObject lok = null;
		try {
			lok = TrainDepotMap.getTrainDepot().getLok(lokname);
		} catch (TrainNotFoundException e) {
			controller.Funktionen.messungAbbrechen();
		}
		this.loknummer = lok.getTrainNumber();
		Send.sendPowerOn();
		fahrstufeEinstellen(0);
	}

	/**
	 * Aendert die Fahrstufe der Lok auf den uebergebenen Wert. Bei negativem
	 * Parameter wird der Betrag dessen und die Richtung auf rueckwaerts
	 * eingestellt.
	 * 
	 * @param fahrstufe
	 */
	protected void fahrstufeEinstellen(int fahrstufe) {
		if (fahrstufe < 0) {
			Send.send0x24(loknummer, -fahrstufe, (byte) 1);
		} else {
			Send.send0x24(loknummer, fahrstufe, (byte) 0);
		}
	}
	
	/**
	 * Sendet einen Nothalt-Befehl an den RMX-Server
	 */
	protected void nothalt() {
		 Send.sendPanic();
	}
}
