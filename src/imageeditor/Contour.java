package imageeditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.interpolation.UnivariateInterpolator;
import org.apache.commons.math3.stat.StatUtils;
import org.math.plot.Plot2DPanel;
import org.math.plot.plotObjects.BaseLabel;

import javafx.scene.Group;

public class Contour {
	double[] x = new double[14];
	double[] y = new double[14];

	public Contour(List<Point> pointList, Group contourGroup){

		int i = 0;
		for(Point p : pointList){
			y[i] = p.getX();
			x[i] = p.getY();
			i++;
		}

		int n = (int) Math.abs((StatUtils.max(x)-StatUtils.min(x))/0.1);
		System.out.println(n);
		double[] xc = new double[n];
		double[] yc = new double[n];

		UnivariateInterpolator interpolador = new SplineInterpolator();
		Plot2DPanel plot = new Plot2DPanel();

		UnivariateFunction polinomio = interpolador.interpolate(x, y);
		double xi = StatUtils.min(x);

		for(i=0 ; i<xc.length ; i++){
			xc[i] = xi+0.1*i;
			yc[i] = polinomio.value(xc[i]);
		}

		plot.addLegend("SOUTH");
		plot.addScatterPlot("Datos", y, x);
		plot.addLinePlot("Interpolacion Spline", yc, xc);

		BaseLabel titulo = new BaseLabel("Interpolacion Spline", Color.BLUE, 0.5, 1.1);
		plot.addPlotable(titulo);

		JFrame frame = new JFrame("Interpolacion Spline");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(600,500);
		frame.add(plot, BorderLayout.CENTER);
		frame.setVisible(true);
	}

}
