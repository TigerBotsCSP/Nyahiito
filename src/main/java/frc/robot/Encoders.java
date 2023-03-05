package frc.robot;

import edu.wpi.first.wpilibj.Encoder;

public class Encoders {
    Encoder m_encoder;

    private static final double cpr = 5; // If am-3314a
    private static final double whd = 6; // For 6 inch wheel

    Encoders() {
        m_encoder = new Encoder(9,8);
        m_encoder.setDistancePerPulse(Math.PI*whd/cpr); // Distance per pulse is pi * (wheel diameter / counts per revolution)-q21`
    }

    public void pushPeriodic() {
        double dist = m_encoder.getDistance();
        System.out.println(dist);
        m_encoder.getRate();
    }
}
