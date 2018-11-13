package asl.sensor.gui;

import asl.sensor.ExperimentFactory;
import asl.sensor.experiment.VoltageExperiment;
import asl.sensor.input.DataStore;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYTitleAnnotation;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.block.BlockContainer;
import org.jfree.chart.block.FlowArrangement;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.title.CompositeTitle;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.VerticalAlignment;

public class VoltagePanel extends ExperimentPanel {

 private int plotCount;

  public VoltagePanel(ExperimentFactory experiment) {
    super(experiment);

    plotCount = 0;

    for (int i = 0; i < panelsNeeded(); ++i) {
      channelType[i] = "Sensor under calibration (RESP required)";

    }

    String xAxisTitle = "Sample number";
    String yAxisTitle = "Digital counts (abs. val.)";
    xAxis = new NumberAxis(xAxisTitle);
    Font bold = xAxis.getLabelFont();
    bold = bold.deriveFont(Font.BOLD, bold.getSize() + 2);
    xAxis.setLabelFont(bold);
    yAxis = new NumberAxis(yAxisTitle);
    ((NumberAxis) yAxis).setAutoRangeIncludesZero(false);
    yAxis.setLabelFont(bold);
    applyAxesToChart();
  }

  @Override
  protected void drawCharts() {
    VoltageExperiment voltage = (VoltageExperiment) expResult;
    XYSeriesCollection series = voltage.getData().get(0);
    JFreeChart chart = ChartFactory.createScatterPlot(getName(),
        "", "", series);
    XYPlot xyPlot = chart.getXYPlot();
    xyPlot.setRangeAxis(yAxis);
    xyPlot.setDomainAxis(xAxis);

    double[] meanValues = voltage.getAllSensitivities();
    // convert meanValues from sensitivity to mean value (multiply by 10)
    for (int i = 0; i < plotCount; ++i) {
      meanValues[i] *= 10.;
      Color lineColor = COLORS[i % 3].darker().darker();
      Marker meanMarker = new ValueMarker(meanValues[i]);
      meanMarker.setLabel("MEAN VALUE " + (String) series.getSeriesKey(i));
      meanMarker.setLabelAnchor(RectangleAnchor.TOP);
      meanMarker.setStroke(new BasicStroke((float) 2.0));
      meanMarker.setPaint(lineColor);
      xyPlot.addRangeMarker(meanMarker);
    }

    this.chart = chart;
    chartPanel.setChart(chart);
    setTitle();
    chartPanel.setMouseZoomable(true);
  }

  @Override
  public int panelsNeeded() {
    return 3;
  }

  /**
   * Displays the statistic results when the calculate button is hit
   * in an inset box on the chart, also used as text in report generation
   */
  private void setTitle() {
    // chart = chartPanel.getChart();
    /*
    String results = expResult.getInsetStrings()[0];
    XYPlot plot = chart.getXYPlot();
    TextTitle result = getDefaultTextTitle();
    result.setText(results);
    XYTitleAnnotation title = new XYTitleAnnotation(0.98, 0.98, result,
        RectangleAnchor.TOP_RIGHT);
    plot.clearAnnotations();
    plot.addAnnotation(title);
    */

    BlockContainer bc = new BlockContainer(new FlowArrangement());
    for (int i = 0; i < plotCount; ++i) {
      String insetString = expResult.getInsetStrings()[i];
      TextTitle result = getDefaultTextTitle();
      result.setText(insetString);
      bc.add(result);
    }
    CompositeTitle ct = new CompositeTitle(bc);
    ct.setVerticalAlignment(VerticalAlignment.BOTTOM);
    ct.setPosition(RectangleEdge.BOTTOM);
    chart.addSubtitle(ct);

  }

  @Override
  protected void updateData(DataStore dataStore) {
    expResult.runExperimentOnData(dataStore);

    plotCount = 0;
    for (int i = 0; i < panelsNeeded(); ++i) {
      if (dataStore.bothComponentsSet(i)) {
        ++plotCount;
      }
    }

    expResult.runExperimentOnData(dataStore);

    XYSeriesCollection timeseries = expResult.getData().get(0);

    for (int i = 0; i < plotCount; ++i) {
      String name = (String) timeseries.getSeriesKey(i);
      if (null == name) {
        continue;
      }
      Color plotColor = COLORS[i % 3];
      seriesColorMap.put(name, plotColor);
    }

    set = true;
    /*
    List<XYSeriesCollection> xyscList = expResult.getData();
    chart = ChartFactory.createXYLineChart(expType.getName(),
        "Data timing (ms)", "Digital counts", xyscList.get(0));
    setTitle();
    */
  }
}
