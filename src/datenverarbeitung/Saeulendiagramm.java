package datenverarbeitung;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.text.TextBlock;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;

import datenaufnahme.Messreihe;

/**
 * Diese Klasse erzeugt ein Saeulendiagramm. Sie fungiert selbst nur als
 * Wrapper. Das Diagram kann mit getChart() abgerufen werden.
 * 
 * @author Manuel Eble, Manel Weber
 */
public class Saeulendiagramm {

	/**
	 * Signalisiert ob eine Warnung bei Aufruf des
	 * Fahrstufen-Geschwindigkeits-Diagramms ausgegeben werden soll.
	 */
	private boolean warnung = false;

	/**
	 * Nummer der Datenreihe in Bearbeitung (beginnend bei 0)
	 */
	private int datenreiheNr = -1;

	/**
	 * Anzahl der im Diagramm dargestellten Fahrstufen
	 */
	private int fahrstufen = 0;

	/**
	 * maximale Anzahl an Datenreihen die im Diagramm dargestellt werden koennen
	 */
	private final int maxDatenreihen = 5;

	/**
	 * Den dargestellten Datenreihen zugeordnete Messreihen
	 */
	private Messreihe[] messreihen = new Messreihe[maxDatenreihen];

	/**
	 * Chart das von der Klasse verwaltet wird
	 */
	private final JFreeChart chart;

	/**
	 * Renderer fuer das Saeulendiagramm
	 */
	private SaeulendiagrammRenderer renderer;

	/**
	 * Dataset das angezeigt wird
	 */
	private DefaultCategoryDataset dataset = new DefaultCategoryDataset();

	/**
	 * Vollstaendiges Dataset als Backup bei Bereichseinschraenkungen der
	 * Anzeige
	 */
	private DefaultCategoryDataset backupDataset = new DefaultCategoryDataset();

	/**
	 * Die hoechste angezeigte Fahrstufe
	 */
	private int obereAnzeigeGrenze = Integer.MAX_VALUE;

	/**
	 * Die niedrigste angezeigte Fahrstufe
	 */
	private int untereAnzeigeGrenze = Integer.MIN_VALUE;

	/**
	 * Abkuerzung der Einheit der Geschiwndigkeit (Weg/Zeit)
	 */
	private String geschwindigkeitsEinheit;

	// Darstellungseigenschaften
	private final Font achsenSchriftart = new Font("Arial", 12, 12);
	private final Font achsenTickSchriftart = new Font("Arial", 10, 10);
	private final Font titelSchriftart = new Font("Arial", 20, 20);
	private final Color textFarbe = Color.BLACK;

	public Saeulendiagramm() {
		chart = ChartFactory.createBarChart(
				"Geschwindigkeit-Fahrstufen-Diagramm", // Graph Titel
				"Fahrstufe", // X-Achsen-Beschriftung
				"Geschwindigkeit in cm/s", // Y-Achsen-Beschriftung
				dataset, // Werte
				PlotOrientation.VERTICAL, // Vertikale Orientierung
				false, // Legende
				true, // Tooltips
				false // URLs
				);
		renderer = new SaeulendiagrammRenderer(this);
		chart.getCategoryPlot().setRenderer(renderer);
		chart.setBackgroundPaint(null);
		chart.getCategoryPlot().setRangeGridlinePaint(Color.GRAY);
		chart.getPlot().setBackgroundPaint(Color.WHITE);
		chart.getTitle().setFont(titelSchriftart);
		chart.getCategoryPlot().getRangeAxis().setLabelFont(achsenSchriftart);
		chart.getCategoryPlot().getRangeAxis()
				.setTickLabelFont(achsenTickSchriftart);
		chart.getCategoryPlot().getRangeAxis().setLabelPaint(textFarbe);
		chart.getCategoryPlot().getRangeAxis().setTickLabelPaint(textFarbe);
		chart.getCategoryPlot().getRangeAxis().setAutoRange(true);
		chart.getCategoryPlot().setDomainAxis(new SelektiveCategoryAxis());
	}

	/**
	 * Angepasste Achse, die bei zu vielen Fahrstufen nicht mehr alle
	 * beschriftet
	 */
	public class SelektiveCategoryAxis extends CategoryAxis {

		private static final long serialVersionUID = 1L;

		/**
		 * Fahrstufen die auf jeden Fall angezeigt werden sollen (auch die
		 * dazugehoerigen negativen werden beachtet)
		 */
		private final int[] gaengigeFahrstufen = { 126, 26, 14 };

		private SelektiveCategoryAxis() {
			setLabel("Fahrstufe");
			setLabelFont(achsenSchriftart);
			setLabelPaint(textFarbe);
			setTickLabelInsets(new RectangleInsets(0, 10, 0, 10));
		}

		@Override
		protected TextBlock createLabel(
				@SuppressWarnings("rawtypes") Comparable category, float width,
				RectangleEdge edge, Graphics2D g2) {
			TextBlock block = new TextBlock();
			String label;
			int divisor = 50; // fuer jede sovielte Fs soll es 1 Label geben

			if (fahrstufen <= 100) {
				divisor = 4;
			}
			if (fahrstufen <= 60) {
				divisor = 2;
			}
			if (fahrstufen <= 20) {
				divisor = 1; // bis zu 30 werden vollstaendig angezeigt
			}
			int fahrstufe = Integer.parseInt(category.toString());
			if (fahrstufe % divisor == 0) {
				label = category.toString();
			} else {
				label = "";
				// gaengige Fahrstufen trotzdem anzeigen
				for (int fs : gaengigeFahrstufen) {
					if ((fahrstufe == fs) || (fahrstufe == -fs)) {
						label = category.toString();
					}
				}
			}

			block.addLine(label, achsenTickSchriftart, textFarbe);
			return block;
		}
	}

	/**
	 * Erzeugt eine neue Saeule im Diagramm
	 * 
	 * @param xWert
	 * @param yWert
	 */
	public void punktAnhaengen(int xWert, double yWert) {
		fahrstufen++;
		backupDataset.addValue(yWert, String.valueOf(datenreiheNr + 1),
				String.valueOf(xWert));
		if (xWert <= obereAnzeigeGrenze && xWert >= untereAnzeigeGrenze) {
			dataset.addValue(yWert, String.valueOf(datenreiheNr + 1),
					String.valueOf(xWert));
		}
	}

	/**
	 * Leert das Saeulendiagramm
	 */
	public void leeren() {
		dataset.clear();
		backupDataset.clear();
		setWarnung(false);
	}

	/**
	 * Eroeffnet eine neue Datenreihe an die die Werte bei Aufruf von
	 * punktHinzufuegen() angehaengt werden. Schatten werden bei mehr als einer
	 * Datenreihe zur besseren Uebersichtlichkeit deaktiviert.
	 */
	public void naechteDatenreihe(Messreihe messreihe) {
		datenreiheNr++;
		messreihen[datenreiheNr] = messreihe;
		if (datenreiheNr > 0) {
			renderer.setShadowVisible(false);
		}
	}

	/**
	 * @return Wahrheitswert ob eine Warnung bei Aufruf des
	 *         Fahrstufen-Geschwindigkeits-Diagramms angezeigt werden soll
	 */
	public boolean isWarnung() {
		return warnung;
	}

	/**
	 * Setzt den Zustand des Attributs warnung
	 * 
	 * @param warnung
	 */
	public void setWarnung(Boolean warnung) {
		this.warnung = warnung;
	}

	/**
	 * Legt die Geschwindigkeits-Einheit fest (wegeinheit/zeiteinheit) und
	 * beschriftet gleichzeitig die Achsen des Diagramms entsprechend.
	 * 
	 * @param wegeinheit
	 * @param zeiteinheit
	 */
	public void setEinheiten(Einheit wegeinheit, Einheit zeiteinheit) {
		this.geschwindigkeitsEinheit = wegeinheit.toString() + "/"
				+ zeiteinheit.toString();
		this.getChart().getCategoryPlot().getDomainAxis().setLabel("Fahrstufe");
		this.getChart().getCategoryPlot().getRangeAxis()
				.setLabel("Geschwindigkeit in " + geschwindigkeitsEinheit);
	}

	/**
	 * Setzt die obere/untere Grenze des sichtbaren Fahrstufenbereichs und
	 * aktualisiert die Anzeige
	 * 
	 * @param isObereGrenze
	 *            Wahrheitswert, ob die oberere Grenze uebermittelt wird
	 *            (ansonsten die untere)
	 * @param fahrstufe
	 *            neue Grenzfahrstufe
	 */
	public void fahrstufenBereichEinschraenken(boolean isObereGrenze,
			int fahrstufe) {
		if (isObereGrenze) {
			obereAnzeigeGrenze = fahrstufe;
		} else {
			untereAnzeigeGrenze = fahrstufe;
		}
		DefaultCategoryDataset angezeigtesDataset = new DefaultCategoryDataset();
		for (int s = 0; s < backupDataset.getRowCount(); s++) {
			for (int i = 0; i < backupDataset.getColumnCount(); i++) {
				double wert = 0;
				try {
					wert = backupDataset.getValue(s, i).doubleValue();
				} catch (Exception e) {
				}
				String fs = (String) backupDataset.getColumnKey(i);
				if (Integer.parseInt(fs) <= obereAnzeigeGrenze
						&& Integer.parseInt(fs) >= untereAnzeigeGrenze) {
					angezeigtesDataset.addValue(wert, String.valueOf(s),
							String.valueOf(fs));
				}
			}
		}
		dataset = angezeigtesDataset;
		this.getChart().getCategoryPlot().setDataset(dataset);
	}

	/**
	 * Hebt eventuelle Einschraenkungen im sichtbaren Fahrstufenbereich (durch
	 * fahrstufenBereichEinschraenken) auf
	 */
	public void bereichseinschraenkungAufheben() {
		obereAnzeigeGrenze = Integer.MAX_VALUE;
		untereAnzeigeGrenze = Integer.MIN_VALUE;
		dataset = backupDataset;
		this.getChart().getCategoryPlot().setDataset(dataset);
	}

	// Getter

	public JFreeChart getChart() {
		return chart;
	}

	public Messreihe[] getMessreihen() {
		return messreihen;
	}

	public int getDatenreiheNr() {
		return datenreiheNr;
	}

	public String getGeschwindigkeitsEinheit() {
		return geschwindigkeitsEinheit;
	}
}
