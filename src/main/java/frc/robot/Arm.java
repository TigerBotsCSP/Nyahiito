package frc.robot;

import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.motorcontrol.PWMSparkMax;

public class Arm {
    private DoubleSolenoid m_solenoidOne;
    private DoubleSolenoid m_solenoidTwo;

    private PWMSparkMax m_armRotation ;
    private PWMSparkMax m_armLength ;

    private boolean m_armsOut = false;

    Arm() {
        m_solenoidOne = new DoubleSolenoid(PneumaticsModuleType.REVPH, 0, 1);
        m_solenoidTwo = new DoubleSolenoid(PneumaticsModuleType.REVPH, 2, 3); // ! Not functioning
        m_armLength = new PWMSparkMax(0);
        m_armRotation = new PWMSparkMax(1);
    }

    public void toggleIntaker() {
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

    public void setOrientation(double value) {
        m_armRotation.set(value);
    }

    public void setLength(double value) {
        m_armLength.set(value);
    }
}
