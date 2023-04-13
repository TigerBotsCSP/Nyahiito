package frc.robot;

import edu.wpi.first.wpilibj.motorcontrol.PWMSparkMax;

public class Intaker {
    public boolean m_in;
    private PWMSparkMax m_intaker;

    Intaker() {
        m_intaker = new PWMSparkMax(1);
        m_in = true;
    }

    // TODO: Test it out, might need to switch in/out or change speed
    public void toggle() {
        if (m_in) {
            m_intaker.set(-Constants.intakerSpeed);
            m_in = false;
        } else {
            m_intaker.set(Constants.intakerSpeed);
            m_in = true;
        }
    }
}
