

	package datenverarbeitung;

	import controller.Main;
	import datenaufnahme.Messreihe;

	/**
	 * Diese Klasse bereitet die Messdaten auf, sodass sie im
	 * Geschwindigkeit-Fahrstufen-Diagramm geplottet werden koennen
	 * 
	 * @author Manuel Eble
	 * @author Manuel Weber
	 */
	public class Plotter extends Thread {

		private final Messungsfenster messungsfenster;
		private final Messreihe messreihe;
		private Saeulendiagramm balkenDiagramm;
		
		private int fahrstufe = 0;
		private double weg = 0;
		private long zeit = 0;

		private Einheit wegeinheit = Einstellungen.getEinstellungen()
				.getWegeinheit();
		private Einheit zeiteinheit = Einstellungen.getEinstellungen()
				.getZeiteinheit();
		private double messeinheit = wegeinheit.konvertieren(Einstellungen
				.getEinstellungen().getMesseinheit());

		public PlotterVFS(Messreihe messreihe, Messungsfenster messungsfenster) {
			this.messungsfenster = messungsfenster;
			this.messreihe = messreihe;
			if (messungsfenster != null) {
				this.balkenDiagramm = messungsfenster.getDiagrammtabs()
						.getVfsDiagramm();
			} else {
				this.balkenDiagramm = Main.getFenster().getDiagrammTabs()
						.getVfsDiagramm();
			}
			balkenDiagramm.naechteDatenreihe(messreihe);
		}

		@Override
		public void run() {

			while (isInterrupted() == false) {

				messwerteVerarbeiten();

				if (messungsfenster != null) {
					return; // Werte aus der Datenbank werden auf einmal geplottet.
				}

				try {
					Thread.sleep(5);
				} catch (Exception e) {
					messwerteVerarbeiten();
					return;
				}
			}
		}

		/**
		 * Verarbeitet neu hinzugekommene Messwerte und traegt sie ins Diagramm ein
		 */
		private void messwerteVerarbeiten() {
		}

		/**
		 * Berechnet die Geschwindigkeit der momentan betrachteten Fahrstufe und
		 * aktualisiert das Saeulendiagramm.
		 */
		private void update() {
			// der Weg ist schon konvertiert (passende Messeinheit)
			double v = weg * 1000 / zeiteinheit.konvertieren(zeit);
			try {
				balkenDiagramm.punktAnhaengen(fahrstufe, v);
			} catch (Exception e) {
				System.err
						.println("Punkt konnte nicht ins vfs-Diagramm eingezeichnet werden: "
								+ e.getMessage());
			}
		}
	}


