package frc.robot;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.I2C;

import com.kauailabs.navx.frc.AHRS;

public class Gyro {
    AHRS m_gyro;
    PIDController m_pid;
 
    // PID constants for gyro correction
    double kP = 0.03;
    double kI = 0.00;
    double kD = 0.00;
    
    Gyro() {
        m_gyro = new AHRS(I2C.Port.kMXP);
        m_pid = new PIDController(kP, kI, kD);
    }

    public void init() {
        m_pid.setSetpoint(0.0);
        m_pid.setIntegratorRange(-180.0, 180.0);
        m_gyro.calibrate();
    }

    public double getAngle() {
        return m_gyro.getAngle();
    }

    public double getTurnRate() {
        double angle = m_gyro.getAngle();
        return m_pid.calculate(angle);
    }

    public void reset() {
        m_gyro.reset();
    }

    public AHRS getGyro() {
        return m_gyro;
    }
}
