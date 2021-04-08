package project.utils;

import java.util.ArrayList;

public class Calculator {

    public Calculator() {}

    /**
     * Calculate the average of a list of double number
     * @param lst a list of double number
     * @return the average of the lst
     */
    public double mean(ArrayList<Double> lst) {
        double sum = sum(lst);
        return sum/lst.size();
    }

    /**
     * Calculate the sum of a list of double number
     * @param lst a list of double number
     * @return the sum of the lst
     */
    public double sum(ArrayList<Double> lst) {
        double sum = 0;
        for (double number: lst) {
            sum += number;
        }
        return sum;
    }

    /**
     * Calculate the (sample) standard deviation of a list of double number
     * @param lst a list of double number
     * @return the standard deviation of the list
     */
    public double stdDev(ArrayList<Double> lst) {
        double mean = mean(lst);
        ArrayList<Double> sqrtDistance = new ArrayList<Double>();
        int n = lst.size();
        for (int i = 0; i < n; i++) {
            double dataPoint = Math.abs(lst.get(i) - mean);
            sqrtDistance.add(dataPoint * dataPoint);
        }
        double sum = sum(sqrtDistance);
        return Math.sqrt(sum/(n - 1));
    }

    /**
     * Calculate the confidence interval of a list of double number for a certain confidence
     * @param lst a list of double number
     * @param confidence confidence level
     * @return the confidence interval of the list
     */
    public double confidenceInterval(ArrayList<Double> lst, double confidence) {
        double z = -1;
        if (confidence == 80) {
            z = 1.282;
        } else if (confidence == 85) {
            z = 1.44;
        } else if (confidence == 90) {
            z = 1.645;
        } else if (confidence == 95) {
            z = 1.96;
        } else if (confidence == 99) {
            z = 2.576;
        } else if (confidence == 99.5) {
            z = 2.807;
        } else if (confidence == 99.9) {
            z = 3.291;
        } else {
            return -1;
        }
        double stdDev = stdDev(lst);
        return z * stdDev / Math.sqrt(lst.size());
    }
}
