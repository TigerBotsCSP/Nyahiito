package frc.robot;

import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.motorcontrol.PWMSparkMax;

public class Arm {
    private DoubleSolenoid m_solenoid;
    private DoubleSolenoid m_solenoid2;

    private PWMSparkMax m_armRotation;
    private PWMSparkMax m_armLength;
    private PWMSparkMax m_

    Arm() {
        m_solenoid = new DoubleSolenoid(PneumaticsModuleType.REVPH, 0, 1);
        m_armLength = new PWMSparkMax(0);
        m_armRotation = new PWMSparkMax(1);
    }

    public void open() {
        m_solenoid.set(Value.kForward);
    }

    public void close() {
        m_solenoid.set(Value.kReverse);
    }

    public void setOrientation(double value) {
        m_armRotation.set(value);
    }

    public void setLength(double value) {
        m_armLength.set(value);
    }
}
