package imageeditor;

import java.util.List;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.interpolation.UnivariateInterpolator;
import org.apache.commons.math3.stat.StatUtils;

public class Contour {
	double[] x = new double[14];
	double[] y = new double[14];
	double[] XC;
	double[] YC;

	public Contour(List<Point> pointList){

		int i = 0;
		for(Point p : pointList){
			y[i] = p.getX();
			x[i] = p.getY();
			i++;
		}

		int n = (int) Math.abs((StatUtils.max(x)-StatUtils.min(x))/0.1);
		double[] xc = new double[n];
		double[] yc = new double[n];

		UnivariateInterpolator interpolador = new SplineInterpolator();

		UnivariateFunction polinomio = interpolador.interpolate(x, y);
		double xi = StatUtils.min(x);

		for(i=0 ; i<xc.length ; i++){
			xc[i] = xi+0.1*i;
			yc[i] = polinomio.value(xc[i]);
		}

		XC = xc;
		YC = yc;
	}

}
