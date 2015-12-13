package loksteuerung;

/**
 * Connector fuer die Verbindung zur RMX-PC-Zentrale. Die Kommunikation erfolgt
 * via RMXnet-Protokoll.
 * 
 * @author Manuel Weber
 */
public class RMXconnector implements ILoksystemConnector{

	// Singelton-Pattern --------------------------------------------

	/**
	 * Singelton-Objekt
	 */
	private static RMXconnector rmxConnectorInstanz;

	/**
	 * Privater Konstruktor verhindert die Instanziierung von aussen.
	 */
	private RMXconnector() {

	}

	/**
	 * Gibt die Verbindung zurueck.
	 * 
	 * @return Connector-Objekt (Singleton-Objekt)
	 */
	public static synchronized RMXconnector getRMXconnector() {
		if (rmxConnectorInstanz == null) {
			rmxConnectorInstanz = new RMXconnector();
		}
		return rmxConnectorInstanz;
	}

	/**
	 * Unterbindet das Klonen des Objekts (Singleton)
	 */
	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

	// Ende Singleton-Pattern ---------------------------------------

	private Connection connection = Connection.getConnection();

	@Override
	public void verbinden() {
		connection.setConnecting();
	}

	@Override
	public void trennen() {
		connection.setDisconnecting();
	}

	@Override
	public int getVerbindungsStatus() {
		return connection.getConnectionStatus();
	}

	@Override
	public int getLoknummer(String lokname) throws TrainNotFoundException {
		return TrainDepotMap.getTrainDepot().getLok(lokname).getTrainNumber();
	}

	@Override
	public int getMaximaleFahrstufe(String lokname)
			throws TrainNotFoundException {
		return TrainDepotMap.getTrainDepot().getLok(lokname)
				.getMaxRunningNotch();
	}
}
