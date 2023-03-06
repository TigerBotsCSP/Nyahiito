package frc.robot;

import edu.wpi.first.wpilibj.Encoder;

public class Encoders {
    public static final double ENCODER_COUNTS_PER_INCH = 3173/200;//2050/130;
    Encoder m_encoderLeft;
    Encoder m_encoderRight;

    private static final double cpr = 5; // If am-3314a
    private static final double whd = 6; // For 6 inch wheel

    Encoders() {
        m_encoderLeft = new Encoder(9,8);
        m_encoderRight = new Encoder(7,6);

        m_encoderRight.setDistancePerPulse(Math.PI*whd/cpr); // Distance per pulse is pi * (wheel diameter / counts per revolution)-q21`
    }

    public void pushPeriodic() {
        double dist = m_encoderLeft.getDistance();
        System.out.println(dist);
        m_encoderLeft.getRate();
    }

    public double encoderAverage() {
        return (m_encoderLeft.get() + m_encoderRight.get()) / 2;
    }
    
    public double getDistance(){
        return (m_encoderLeft.get() + m_encoderRight.get()) / (ENCODER_COUNTS_PER_INCH * 2);
    }
    
    public void encoderReset() {
        m_encoderLeft.reset();
        m_encoderRight.reset();
    }
}
