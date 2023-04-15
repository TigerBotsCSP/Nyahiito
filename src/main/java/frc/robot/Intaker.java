package frc.robot;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

public class Intaker {
    public boolean m_in;
    private CANSparkMax m_intaker;

    Intaker() {
        m_intaker = new CANSparkMax(7, MotorType.kBrushless);
        m_in = true;
    }

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
