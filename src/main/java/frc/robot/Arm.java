package frc.robot;

import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

public class Arm {
    private DoubleSolenoid m_solenoidOne;
    private DoubleSolenoid m_solenoidTwo;
    private boolean m_armsOut = false;

    Arm() {
        m_solenoidOne = new DoubleSolenoid(PneumaticsModuleType.REVPH, 0, 1);
        m_solenoidTwo = new DoubleSolenoid(PneumaticsModuleType.REVPH, 2, 3);
    }

    public void toggleArm() {
        if (m_armsOut) {
            m_armsOut = false;
            m_solenoidOne.set(Value.kReverse);
            m_solenoidTwo.set(Value.kReverse);
        } else {
            m_armsOut = true;
            m_solenoidOne.set(Value.kForward);
            m_solenoidTwo.set(Value.kForward);
        }
    }
}
