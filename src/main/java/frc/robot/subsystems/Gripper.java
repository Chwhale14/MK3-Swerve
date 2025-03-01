// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.GripperConstants;

public class Gripper extends SubsystemBase {
  private final CANSparkMax gripperMotor;
  private PIDController gripperPIDController;
  private double setPoint;

  /** Creates a new Gripper. */
  public Gripper() {
    gripperMotor = new CANSparkMax(GripperConstants.GRIPPER_MOTOR, MotorType.kBrushless);
    gripperPIDController = new PIDController(GripperConstants.GRIPPER_PID_CONSTANTS.kP,
        GripperConstants.GRIPPER_PID_CONSTANTS.kI, GripperConstants.GRIPPER_PID_CONSTANTS.kD);

    this.setPoint = 0;

    gripperPIDController.reset();

    gripperMotor.clearFaults();
    gripperMotor.restoreFactoryDefaults();
    gripperMotor.setIdleMode(IdleMode.kBrake);

    shuffleboardInit();
  }

  public void runGripper(double speed) {
    this.gripperMotor.set(speed);
  }

  public void gripCone() {
    this.setPoint = 6;
  }

  public void gripCube() {
    this.setPoint = 3;
  }

  public void rest() {
    this.setPoint = 0;
  }

  public void shuffleboardInit() {
    ShuffleboardTab gripperTab = Shuffleboard.getTab("Gripper");
    gripperTab.getLayout("Motor Values", BuiltInLayouts.kList).withPosition(0, 0).withSize(2, 4);
    gripperTab.getLayout("PID Values", BuiltInLayouts.kList).withPosition(2, 0).withSize(2, 4);

    gripperTab.getLayout("Motor Values").addDouble("Velocity", () -> gripperMotor.getEncoder().getVelocity());
    gripperTab.getLayout("Motor Values").addDouble("Position", () -> gripperMotor.getEncoder().getPosition());

    gripperTab.getLayout("PID Values").addDouble("Setpoint", () -> this.setPoint);
    gripperTab.getLayout("PID Values").addDouble("kP", ()->GripperConstants.GRIPPER_PID_CONSTANTS.kP);
    gripperTab.getLayout("PID Values").addDouble("kI", ()->GripperConstants.GRIPPER_PID_CONSTANTS.kI);
    gripperTab.getLayout("PID Values").addDouble("kD", ()->GripperConstants.GRIPPER_PID_CONSTANTS.kD);
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
    gripperMotor.set(gripperPIDController.calculate(gripperMotor.getEncoder().getPosition(), this.setPoint) * .5);
  }
}
