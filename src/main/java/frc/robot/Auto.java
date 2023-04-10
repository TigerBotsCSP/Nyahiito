package frc.robot;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import com.google.gson.Gson;

public class Auto {
    // Array of commands
    ArrayList<ArrayList<Double>> m_schedule = new ArrayList<>(3);
    int m_index = 0;

    Auto() {
    }

    public void loadPath(String path) {
        m_schedule.clear();

        try {
            // Load file into a String
            String pathData = new String(Files.readAllBytes(Paths.get(path)));
            Gson gson = new Gson();

            // Put the actions into the Array
            m_schedule = gson.fromJson(pathData, ArrayList.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Main autonomous loop
    public void schedule() {
        try {
            if (m_schedule.get(m_index).get(4) == 1) {
                RobotContainer.m_arm.open();
            } else if (m_schedule.get(m_index).get(4) == 2) {
                RobotContainer.m_arm.close();
            }

            // Drive functionality
            if (m_schedule.get(m_index).get(5) == 1) {
                RobotContainer.m_drive.rotateDrive(0, 0);
            } else {
                double speedX = RobotContainer.limit(-m_schedule.get(m_index).get(1), Constants.driveSpeed);
                double speedY = RobotContainer.limit(-m_schedule.get(m_index).get(0), Constants.driveSpeed);
                RobotContainer.m_drive.rotateDrive(speedX, speedY);
            }

            // Arm rotation
            double armSpeed = RobotContainer.limit(-m_schedule.get(m_index).get(2), Constants.armSpeed);
            RobotContainer.m_arm.setOrientation(armSpeed);

            // Arm length
            double armLength = RobotContainer.limit(-m_schedule.get(m_index).get(3), Constants.armSpeed);
            RobotContainer.m_arm.setLength(-armLength);

            m_index++;
        } catch (Exception e) {

        }
    }

}
