package frc.robot;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

public class Intaker {
    public boolean m_in; // For motor
    public boolean m_down; // For position

    private CANSparkMax m_intakerMotor;
    private DoubleSolenoid m_intakerPusher;

    Intaker() {
        m_intakerMotor = new CANSparkMax(12, MotorType.kBrushless);
        m_intakerPusher = new DoubleSolenoid(PneumaticsModuleType.REVPH, 6, 7);
        m_in = true;
        m_down = false;
    }

    public void toggleMotor() {
        if (m_in) {
            m_intakerMotor.set(Constants.intakerSpeed);
            m_in = false;
        } else {
            m_intakerMotor.set(-Constants.intakerSpeed);
            m_in = true;
        }
    }

    public void togglePusher() {
        if (m_down) {
            m_intakerPusher.set(Value.kReverse);
            m_down = false;
        } else {
            m_intakerPusher.set(Value.kForward);
            m_down = true;
        }
    }

    public double getCurrent() {
        return m_intakerMotor.getOutputCurrent();
    }

    public void setSpeed(double speed) {
        m_intakerMotor.set(speed);
    }
}
