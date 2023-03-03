package frc.robot.subsystems;

import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

public class Hand {
    private DoubleSolenoid m_solenoidOne;
    private DoubleSolenoid m_solenoidTwo;

    Hand() {
        m_solenoidOne = new DoubleSolenoid(PneumaticsModuleType.REVPH, 0, 1);
        m_solenoidTwo = new DoubleSolenoid(PneumaticsModuleType.REVPH, 2, 3);
    }

    public void toggleIntake() {
        if (m_solenoidOne.get() != Value.kForward && m_solenoidTwo.get() != Value.kForward) {
            m_solenoidOne.set(Value.kForward);
            m_solenoidTwo.set(Value.kForward);
            
        } else if (m_solenoidOne.get() != Value.kReverse && m_solenoidTwo.get() != Value.kReverse){
            m_solenoidOne.set(Value.kReverse);
            m_solenoidTwo.set(Value.kReverse);       
        }
    }

}
