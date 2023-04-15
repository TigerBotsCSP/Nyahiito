package frc.robot;

import edu.wpi.first.wpilibj.PneumaticsModuleType;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

public class Arm {
    private DoubleSolenoid m_solenoid;

    private CANSparkMax m_armRotation;
    private CANSparkMax m_armLength;

    Arm() {
        m_solenoid = new DoubleSolenoid(PneumaticsModuleType.REVPH, 0, 1);
        m_armLength = new CANSparkMax(5, MotorType.kBrushed);
        m_armRotation = new CANSparkMax(6, MotorType.kBrushed);
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
