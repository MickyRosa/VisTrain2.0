package loksteuerung;

/**
 * Exception die geworfen wird wenn die TrainDepotMap eine gesuchte Lok nicht
 * enthaelt.
 * 
 * @author Manuel Weber
 */
public class TrainNotFoundException extends Exception {
	private static final long serialVersionUID = -2126102883515585959L;

	public TrainNotFoundException() {
		super("Keine Lok ausgew\u00E4hlt");
	}
	
	public TrainNotFoundException(String lokname) {
			super("Es konnte keine Lok mit der Bezeichnung '"
					+ lokname + "' gefunden werden.");
	}
}