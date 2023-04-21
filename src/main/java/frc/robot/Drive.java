package frc.robot;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;


public class Drive {

    private CANSparkMax m_frontLeft;
    private CANSparkMax m_rearLeft;
    private MotorControllerGroup m_left;

    private CANSparkMax m_frontRight;
    private CANSparkMax m_rearRight;
    private MotorControllerGroup m_right;

    private DifferentialDrive m_drive;

    public boolean m_brakeMode;

    Drive() {
        m_frontLeft = new CANSparkMax(1, MotorType.kBrushless);
        m_rearLeft = new CANSparkMax(2, MotorType.kBrushless);
        m_left = new MotorControllerGroup(m_frontLeft, m_rearLeft);

        m_frontRight = new CANSparkMax(3, MotorType.kBrushless);
        m_rearRight = new CANSparkMax(4, MotorType.kBrushless);
        m_right = new MotorControllerGroup(m_frontRight, m_rearRight);

        m_drive = new DifferentialDrive(m_left, m_right);
        m_drive.setSafetyEnabled(false);

        m_brakeMode = false;
    }

    public void rotateDrive(double speed, double rotation) {
        m_drive.arcadeDrive(speed, rotation);
    }

    public void straightDrive(double speed) {
        m_drive.arcadeDrive(0, speed);
    }

    public void toggleMode() {
        if (m_brakeMode) {
            m_frontLeft.setIdleMode(IdleMode.kCoast);
            m_rearLeft.setIdleMode(IdleMode.kCoast);
            m_frontRight.setIdleMode(IdleMode.kCoast);
            m_rearRight.setIdleMode(IdleMode.kCoast);

            m_brakeMode = false;
        } else {
            m_frontLeft.setIdleMode(IdleMode.kBrake);
            m_rearLeft.setIdleMode(IdleMode.kBrake);
            m_frontRight.setIdleMode(IdleMode.kBrake);
            m_rearRight.setIdleMode(IdleMode.kBrake);

            m_brakeMode = true;
        }
    }
}