/*
 * Title: ExpRegMaths.java
 * Project: telegramJ
 * Creator: Georgii Mikriukov
 */

/**
 * The author of this code: Ata Amini
 * Taken from here:
 * https://www.codeproject.com/Articles/1175992/Implementation-of-Gauss-Newton-Algorithm-in-Java
 */

package crawler.implementation.maths.gaussnewton;

public class ExpRegMaths {

    /**
     * Counts initial values for optimization. Fits values to exponential function. returns [B, r] parameters of exponential function y(x) = B*exp(-r*x)
     * @param x x-axis values
     * @param y y-axis values
     */
    public static double[] expRegInitVals(double[] x, double[] y) {
        double[] result = new double[2];
        //variables for sums
        double sx2y = 0;
        double sylny = 0;
        double sxy = 0;
        double sxylny = 0;
        double sy = 0;
        double sxy_2 = 0;
        int n = x.length;
        double lny[] = new double[n]; //create an array where we will store ln(yi)
        for (int i = 0; i < n; i++) {
            //Calculate the values of ln(yi)
        }
        for (int i = 0; i < n; i++) {
            lny[i] = Math.log(y[i]);
            sx2y += x[i]*x[i]*y[i];
            sylny += y[i]*lny[i];
            sxy += x[i]*y[i];
            sxylny += x[i]*y[i]*lny[i];
            sy += y[i];
        }
        sxy_2 += Math.pow(sxy, 2);
        result[1] = -(sy*sxylny-sxy*sylny)/(sy*sx2y-sxy_2); // r
        result[0] = Math.exp((sx2y*sylny-sxy*sxylny)/(sy*sx2y-sxy_2)); // B

        return result;

    }

    public static double mathTimeThresholdCount(double r, double p){
        return -Math.log(1-(1-p))/r;
    }

}
