package resultSims;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.util.ShapeUtilities;

import dataset.DatasetData;

public class GraphChart extends JFrame {


	private DatasetData real;
	private DatasetData sim;
	private static final long serialVersionUID = 1L;

	/**
	 * Initializes the frame
	 * @param counterMeasure
	 */
	private void initialization(boolean counterMeasure) {
		XYDataset data = createDataset(counterMeasure);
		JFreeChart chart;
		if(real.isSmooth())chart = createSmoothChart(data);
		else chart = createChart(data);
		ChartPanel chartP = new ChartPanel(chart);
		chartP.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
		chartP.setBackground(Color.WHITE);

		this.setPreferredSize(new Dimension(750, 550));
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.add(chartP);
		this.pack();
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}

	/**
	 * Loads the datasets it's going to display.
	 * @param real real dataset
	 * @param simulation simulation dataset
	 */
	public GraphChart(DatasetData real, DatasetData simulation) {
		this.real = real;
		this.sim = simulation;

	}

	/**
	 * Loads the dataset it's going to display (only one dataset)
	 * @param real real dataset
	 */
	public GraphChart(DatasetData real) {
		this.real = real;

		initialization();
	}

	/**
	 * Creates one series for a specific array into a dataset.
	 * @param array array of values
	 * @param name name of the series
	 * @return series to display
	 */
	private XYSeries createDataset(ArrayList<Double> array, String name) {

		XYSeries obj = new XYSeries(name);
		for(int i = 0; i < array.size(); i ++) {
			obj.add(i, array.get(i));
		}
		return obj;
	}

	/**
	 * Creates the dataset it's going to be used for the display.
	 * @param real real dataset
	 * @param simulation simulation dataset
	 * @param counterMeasure checks if it's a countermeasure display, to show only endorsers.
	 * @return dataset with the points to be represented
	 */
	private XYDataset createDataset(boolean counterMeasure) {

		XYSeriesCollection dataset = new XYSeriesCollection();
		XYSeries realE = new XYSeries("real endorsers");
		for(int i = 0; i < real.getFirstArray().size(); i ++) {
			realE.add(i, real.getFirstArray().get(i));
		}

		XYSeries simE = new XYSeries("sim endorsers");

		for(int i = 0; i < real.getFirstArray().size(); i ++) {
			simE.add(i, sim.getFirstArray().get(i));
		}
		dataset.addSeries(realE);
		dataset.addSeries(simE);

		if(real.hasTwoValues() && !counterMeasure) {
			XYSeries realDe = new XYSeries("real deniers");

			for(int i = 0; i < real.getFirstArray().size(); i ++) {
				realDe.add(i, real.getSecondArray().get(i));
			}

			XYSeries simDe = new XYSeries("sim deniers");

			for(int i = 0; i < real.getFirstArray().size(); i ++) {
				simDe.add(i, sim.getSecondArray().get(i));
			}
			dataset.addSeries(realDe);
			dataset.addSeries(simDe);

		}
		if(counterMeasure) {
			realE.setKey("counter endorsers");
		} else if (!real.hasTwoValues()) {
			realE.setKey("real spread");
			simE.setKey("sim spread");
		}

		return dataset;
	}

	/**
	 * Creates a chart with the data to display (smoothed).
	 * @param data dataset created
	 * @return chart
	 */
	private JFreeChart createSmoothChart(XYDataset data) {

		String time = real.isDaily() ? "Days" : "Hours";
		JFreeChart graph = ChartFactory.createXYLineChart("Spread of news", time, "Percentage of users", data, 
				PlotOrientation.VERTICAL,true,true, false);

		XYSeriesCollection dataset2 = new XYSeriesCollection(); //non smoothed
		XYSeriesCollection dataset1 = new XYSeriesCollection(); //smoothed

		if(real.hasTwoValues()) {
			dataset1.addSeries(createDataset(real.getFirstArray(),"real endorsers"));
			dataset1.addSeries(createDataset(real.getSecondArray(),"real deniers"));

			dataset2.addSeries(createDataset(sim.getFirstArray(),"sim endorsers"));
			dataset2.addSeries(createDataset(sim.getSecondArray(),"sim deniers"));

		} else {
			dataset1.addSeries(createDataset(real.getFirstArray(),"real spread"));
			dataset2.addSeries(createDataset(sim.getFirstArray(),"sim spread"));
		}

		XYPlot plot = graph.getXYPlot();

		ValueAxis val = plot.getRangeAxis();


		if(real.getMinValue()<1) val.setLowerBound(real.getMinValue()-2); //added to minimum
		plot.setRangeAxis(val);

		plot.setDataset(0,dataset1); //smoothed
		plot.setDataset(1,dataset2);

		XYLineAndShapeRenderer rend = new XYLineAndShapeRenderer();

		XYSplineRenderer rend2 = new XYSplineRenderer(5);

		plot.setRenderer(0,rend2);
		plot.setRenderer(1,rend);

		rend2.setSeriesPaint(0, Color.BLACK); //smooth
		rend2.setSeriesStroke(0, new BasicStroke(1f));
		rend2.setSeriesShape(0, ShapeUtilities.createDiagonalCross(1,1));

		rend.setSeriesPaint(0, Color.GRAY);
		rend.setSeriesStroke(0, new BasicStroke(1f));
		rend.setSeriesShape(0, ShapeUtilities.createDiagonalCross(1,1));

		if(data.getSeriesCount() > 2) {
			rend2.setSeriesPaint(1, Color.RED); // smooth
			rend2.setSeriesStroke(1, new BasicStroke(1f));
			rend2.setSeriesShape(1, ShapeUtilities.createDiagonalCross(1,1));

			rend.setSeriesPaint(1, Color.MAGENTA);
			rend.setSeriesStroke(1, new BasicStroke(1f));
			rend.setSeriesShape(1, ShapeUtilities.createDiagonalCross(1,1));

		}

		plot.setBackgroundPaint(Color.WHITE);
		plot.setRangeGridlinesVisible(true);
		plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
		plot.setDomainGridlinesVisible(true);
		plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
		graph.getLegend().setFrame(BlockBorder.NONE);
		graph.setTitle(new TextTitle("Percentages of users' positions per time unit", JFreeChart.DEFAULT_TITLE_FONT));

		return graph;

	}
	/**
	 * Creates a chart with only the dataset to display (smoothed).
	 * @param data dataset created
	 * @return chart
	 */
	private JFreeChart createOnlySmoothChart(XYDataset data) {
		String time = real.isDaily() ? "Days" : "Hours";
		JFreeChart graph = ChartFactory.createXYLineChart("Spread of news", time, "Percentage of users", data, 
				PlotOrientation.VERTICAL,true,true, false);

		XYSeriesCollection dataset1 = new XYSeriesCollection();
		dataset1.addSeries(createDataset(real.getFirstArray(),"spread"));
		if(real.hasTwoValues())dataset1.addSeries(createDataset(real.getSecondArray(),"deniers"));

		XYPlot plot = graph.getXYPlot();

		ValueAxis val = plot.getRangeAxis();

		if(real.getMinValue()<1)val.setLowerBound(real.getMinValue()-2); //added to minimum

		plot.setRangeAxis(val);
		plot.setDataset(0,dataset1); //smoothed


		XYSplineRenderer rend2 = new XYSplineRenderer(5);

		plot.setRenderer(0,rend2);

		rend2.setSeriesPaint(0, Color.BLACK);
		rend2.setSeriesStroke(0, new BasicStroke(1f));
		rend2.setSeriesShape(0, ShapeUtilities.createDiagonalCross(1,1));

		if(data.getSeriesCount() > 2) {
			rend2.setSeriesPaint(1, Color.RED);
			rend2.setSeriesStroke(1, new BasicStroke(1f));
			rend2.setSeriesShape(1, ShapeUtilities.createDiagonalCross(1,1));
		}

		plot.setBackgroundPaint(Color.WHITE);
		plot.setRangeGridlinesVisible(true);
		plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
		plot.setDomainGridlinesVisible(true);
		plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
		graph.getLegend().setFrame(BlockBorder.NONE);
		graph.setTitle(new TextTitle("Percentages of users' positions per time unit", JFreeChart.DEFAULT_TITLE_FONT));

		return graph;

	}

	/**
	 * Initializes the frame if only dataset (INTERNALLY USED)
	 */
	private void initialization() {
		XYSeriesCollection dataset = new XYSeriesCollection();
		XYSeries realE = new XYSeries("endorsers");
		for(int i = 0; i < real.getFirstArray().size(); i ++) {
			realE.add(i, real.getFirstArray().get(i)); //each value
		}
		dataset.addSeries(realE);


		if(real.hasTwoValues()) {
			XYSeries realDe = new XYSeries("deniers");

			for(int i = 0; i < real.getFirstArray().size(); i ++) {
				realDe.add(i, real.getSecondArray().get(i)); //each value
			}
			dataset.addSeries(realDe);
		} else realE.setKey("spread");
		JFreeChart chart;
		if(real.isSmooth()) chart = createOnlySmoothChart(dataset);
		else chart = createChart(dataset);
		ChartPanel chartP = new ChartPanel(chart);
		chartP.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
		chartP.setBackground(Color.WHITE);

		this.setPreferredSize(new Dimension(750, 550));
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.add(chartP);
		this.pack();
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}



	/**
	 * Creates a chart with the data to display (not smooth).
	 * @param data dataset created
	 * @return chart
	 */
	private JFreeChart createChart(XYDataset data) {
		String time = real.isDaily() ? "Days" : "Hours";
		JFreeChart graph = ChartFactory.createXYLineChart("Spread of news", time, "Percentage of users", data, 
				PlotOrientation.VERTICAL,true,true, false);

		XYPlot plot = graph.getXYPlot();

		XYLineAndShapeRenderer rend = new XYLineAndShapeRenderer();
		rend.setSeriesPaint(0, Color.BLACK);
		rend.setSeriesStroke(0, new BasicStroke(1f));
		rend.setSeriesShape(0, ShapeUtilities.createDiagonalCross(1,1));

		rend.setSeriesPaint(1, Color.GRAY);
		rend.setSeriesStroke(1, new BasicStroke(1f));
		rend.setSeriesShape(1, ShapeUtilities.createDiagonalCross(1,1));

		if(data.getSeriesCount() > 2) {
			rend.setSeriesPaint(2, Color.RED);
			rend.setSeriesStroke(2, new BasicStroke(1f));
			rend.setSeriesShape(2, ShapeUtilities.createDiagonalCross(1,1));

			rend.setSeriesPaint(3, Color.MAGENTA);
			rend.setSeriesStroke(3, new BasicStroke(1f));
			rend.setSeriesShape(3, ShapeUtilities.createDiagonalCross(1,1));

		}

		plot.setRenderer(rend);
		plot.setBackgroundPaint(Color.WHITE);
		plot.setRangeGridlinesVisible(true);
		plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
		plot.setDomainGridlinesVisible(true);
		plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
		graph.getLegend().setFrame(BlockBorder.NONE);
		graph.setTitle(new TextTitle("Percentages of users' positions per time unit", JFreeChart.DEFAULT_TITLE_FONT));

		return graph;

	}

	/**
	 * Initializes the graph, for a complex graph with endorsers and deniers.
	 * @param isCounter indicates if we show the countermeasures or normal model.
	 */
	public void generateGraph(boolean isCounter) {

		initialization(isCounter);

	}

}
