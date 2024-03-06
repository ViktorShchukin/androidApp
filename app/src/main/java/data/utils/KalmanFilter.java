package data.utils;

public class KalmanFilter {

    private final double f;  // factor of real value to previous real value
    private final double q;  // measurement noise
    private final double h;  // factor of measured value to real value
    private final double r;  // environment noise

    private double state;
    private double covariance;
    private boolean firstValue = true;

    public KalmanFilter(double q, double r, double f, double h) {
        this.q = q;
        this.r = r;
        this.f = f;
        this.h = h;
    }

    public KalmanFilter(double q, double r, double f) {
        this(q,r,f,1);
    }

    public KalmanFilter(double q, double r) {
        this(q,r,1,1);
    }

    public void setState(double state, double covariance)
    {
        this.state = state;
        this.covariance = covariance;
    }

    public double correct(double data)
    {
        if (firstValue) {
            setState(data, 0.1);
            firstValue =false;
            return state;
        }
        //time update - prediction
        // predicted state
        double x0 = f * state;
        // predicted covariance
        double p0 = f * covariance * f + q;

        //measurement update - correction
        double K = h * p0 /(h * p0 * h + r);
        state = x0 + K*(data - h * x0);
        covariance = (1 - K* h)* p0;
        return state;
    }
}
