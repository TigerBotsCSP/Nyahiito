package frc.robot;

import edu.wpi.first.wpilibj.Encoder;

public class Encoders {
    Encoder m_encoderL;
    Encoder m_encoderR;
    Encoder m_encoderIO;
    Encoder m_encoderAR;

    private static final double cpr = 5; // If am-3314a
    private static final double whd = 6; // For 6 inch wheel

    Encoders() {
        m_encoderL = new Encoder(0,1); //LDrive
        m_encoderR = new Encoder(2,3); //RDrive
        m_encoderIO = new Encoder(4,5); //ArmIO
        m_encoderAR = new Encoder(6,7); //ArmRotate

        m_encoderL.setDistancePerPulse(Math.PI*whd/cpr); // Distance per pulse is pi * (wheel diameter / counts per revolution)
        m_encoderR.setDistancePerPulse(Math.PI*whd/cpr); // Distance per pulse is pi * (wheel diameter / counts per revolution)
        m_encoderIO.setDistancePerPulse(Math.PI*whd/cpr); // Distance per pulse is pi * (wheel diameter / counts per revolution)
        m_encoderAR.setDistancePerPulse(Math.PI*whd/cpr); // Distance per pulse is pi * (wheel diameter / counts per revolution)

    }

    public void pushPeriodic() {
        double dist = m_encoderL.getDistance();
        System.out.println(dist);
        m_encoderL.getRate();
    }
}
