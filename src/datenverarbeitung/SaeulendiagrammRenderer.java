package datenverarbeitung;

import java.util.Date;

import org.jfree.chart.labels.CategoryToolTipGenerator;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.CategoryDataset;

import controller.Einstellungen;
import datenaufnahme.Messreihe;

/**
 * Definiert wie die Saeulendiagramme gerendert werden sollen. Farben werden aus
 * den Einstellungen geladen.
 * 
 * @author Manuel Weber
 */
public class SaeulendiagrammRenderer extends BarRenderer {

	private static final long serialVersionUID = -3795293286984371644L;

	/**
	 * Maximale Anzahl an Kurven
	 */
	public final int maxKurven = 5;

	/**
	 * Diagramm das genrendert werden soll
	 */
	private final Saeulendiagramm diagramm;

	public SaeulendiagrammRenderer(Saeulendiagramm diagramm) {
		this.diagramm = diagramm;
		for (int i = 0; i < maxKurven; i++) {
			// Farben
			setSeriesPaint(i, Einstellungen.getEinstellungen()
					.getDiagrammFarbe(i));
			// Breite
			setMaximumBarWidth(.20);
			setBarPainter(new StandardBarPainter());
			// Tooltips
			setBaseToolTipGenerator((CategoryToolTipGenerator) new SaeulenTooltipGenerator());
		}
	}

	/**
	 * Ein dynamischer TooltipGenerator, der die Metadaten der jeweiligen
	 * Messreihen in den Tooltip schreibt.
	 */
	private class SaeulenTooltipGenerator extends
			StandardCategoryToolTipGenerator {

		private static final long serialVersionUID = -4502845754090482422L;

		@Override
		public String generateToolTip(CategoryDataset dataset, int row,
				int column) {

			Messreihe m = diagramm.getMessreihen()[row];

			String lok = "<br>" + m.getLokname();
			@SuppressWarnings("deprecation")
			String zeitpunkt = "<br>"
					+ new Date(m.getStartzeitpunkt()).toLocaleString();
			String fahrstufen = "<br>Startfahrstufe: " + m.getFahrstufe0()
					+ "<br>Endfahrstufe: " + m.getFahrstufe1();

			String messdauer = "<br>Messdauer: "
					+ String.valueOf(Einstellungen.zahlenformat
							.format(Einstellungen.getEinstellungen()
									.getZeiteinheit()
									.konvertieren(m.getMessdauer() / 1000)));
			messdauer += Einstellungen.getEinstellungen().getZeiteinheit()
					.toString();

			String messungsInfos = "<br>-----------<br>Datenreihe " + row
					+ ": <i>" + lok + zeitpunkt + fahrstufen + messdauer +"</i>";

			String u = String.valueOf(m.getAnzahlUmdrehungen(Integer
					.parseInt((String) dataset.getColumnKey(column))));
			if(u.equals("1"))
				u += " Umdrehung";
			else 
				u += " Umdrehungen";
			
			String saeule = "Fahrstufe "
					+ dataset.getColumnKey(column)
					+ "<br>Geschwindigkeit: "
					+ Einstellungen.zahlenformat
							.format(dataset.getValue(row, column))
					+ " " + diagramm.getGeschwindigkeitsEinheit()
					+ "<br>(aus "
					+ u
					+")";

			String tooltip = "<html>" + saeule;
			if (diagramm.getDatenreiheNr() > 0) {
				tooltip += messungsInfos;
			}
			tooltip += "</html>";
			return tooltip;
		}
	}

}
