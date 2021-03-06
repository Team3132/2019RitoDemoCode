/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

//import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
/*
 * If there is an error on this line (com.ctre is highlighted in red) it means that you have not added the Cross The Road Electronics libraries
 * to your build environment. Add them using the steps outlined in https://phoenix-documentation.readthedocs.io/en/latest/ch05a_CppJava.html
 */

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.commands.AutoMoveCommand;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.WidgetSubsystem;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  private Joystick driverInput, operatorInput;
  private DifferentialDrive drive;
  private SpeedController leftController, rightController;
  private DriveSubsystem driveSubsystem;
  private WidgetSubsystem widgetSubsystem;
  public static OI m_oi;

  Command m_autonomousCommand;
  SendableChooser<Command> m_chooser = new SendableChooser<>();

  /**
   * This function is run when the robot is first started up and should be
   * used for any initialization code.
   */
  @Override
  public void robotInit() {
    driverInput = new Joystick(RobotMap.DRIVER_GAMEPAD);
    operatorInput = new Joystick(RobotMap.OPERATOR_GAMEPAD);

    /*
     * These two lines are for one or move PWM style drive controllers.
     * Uncomment the lines and add (or remove) Spark definitions as necessary
     */
    leftController = new SpeedControllerGroup(new Spark(RobotMap.DRIVE_LEFT1_PWM_ID), new Spark(RobotMap.DRIVE_LEFT2_PWM_ID));
    rightController = new SpeedControllerGroup(new Spark(RobotMap.DRIVE_RIGHT1_PWM_ID), new Spark(RobotMap.DRIVE_RIGHT2_PWM_ID));

    /*
     * These two lines are for CTRE Talon SRX CAN Bus style drive controllers.
     * Uncomment the lines and add (or remove) WPI_TalonSRX definitions as necessary
     * Note: you will also need to add the CTRE phoenix libraries (see above)
     */
    //leftController = new SpeedControllerGroup(new WPI_TalonSRX(RobotMap.DRIVE_LEFT1_CAN_ID), new WPI_TalonSRX(RobotMap.DRIVE_LEFT2_CAN_ID), new WPI_TalonSRX(RobotMap.DRIVE_LEFT3_CAN_ID));
    //rightController = new SpeedControllerGroup(new WPI_TalonSRX(RobotMap.DRIVE_RIGHT1_CAN_ID), new WPI_TalonSRX(RobotMap.DRIVE_RIGHT2_CAN_ID), new WPI_TalonSRX(RobotMap.DRIVE_RIGHT3_CAN_ID));

    drive = new DifferentialDrive(leftController, rightController);
    /*
     * These drive subsystem definitions are defining how the driver's controlls affect the motor.
     * You need ONE of these uncommented, so depending on which style you want chose the appropriate line.
     */
    driveSubsystem = new DriveSubsystem(driverInput::getY, driverInput::getTwist, drive, RobotMap.DriveStyle.DRIVE_STYLE_ARCADE);   // single flight stick with twist for turning
    //driveSubsystem = new DriveSubsystem(() -> driverInput.getRawAxis(1), () -> driverInput.getRawAxis(5), drive, RobotMap.DriveStyle.DRIVE_STYLE_TANK); // single gamepad using thumb sticks as tank control

    /*
      *  create a widget subsystem. This is code that controls some widget. In the example code it is just a simple motor.
      *  We create a speed controller for the motor, and this needs to be to the subsystem to be manipulate.
      *  
      *  Here we use the Victor SP, a PWM controller that will be available. Yes, we can mix and match our motor controllers,
      *  although it is better to use the same controller when working in groups (such as left and right on the drive train).
      */
    widgetSubsystem = new WidgetSubsystem(new VictorSP(RobotMap.WIDGET_CONTROLLER_ID));


    m_oi = new OI(driverInput, operatorInput, widgetSubsystem);
    m_chooser.setDefaultOption("Default Auto", new AutoMoveCommand(driveSubsystem, 0.5, 0, 0.5));
    // chooser.addOption("My Auto", new MyAutoCommand());
    SmartDashboard.putData("Auto mode", m_chooser);

    /*
     * Start a camera server - this allows you to have a camera mounted on your robot and the image being shown on the drivers startion.
     * https://wpilib.screenstepslive.com/s/currentCS/m/vision/l/669166-using-the-cameraserver-on-the-roborio for details.
     * 
     * if you don't want a camera server comment out this line.
     */
    CameraServer.getInstance().startAutomaticCapture();
  }

  /**
   * This function is called every robot packet, no matter the mode. Use
   * this for items like diagnostics that you want ran during disabled,
   * autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before
   * LiveWindow and SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
  }

  /**
   * This function is called once each time the robot enters Disabled mode.
   * You can use it to reset any subsystem information you want to clear when
   * the robot is disabled.
   */
  @Override
  public void disabledInit() {
  }

  @Override
  public void disabledPeriodic() {
    Scheduler.getInstance().run();
  }

  /**
   * This autonomous (along with the chooser code above) shows how to select
   * between different autonomous modes using the dashboard. The sendable
   * chooser code works with the Java SmartDashboard. If you prefer the
   * LabVIEW Dashboard, remove all of the chooser code and uncomment the
   * getString code to get the auto name from the text box below the Gyro
   *
   * <p>You can add additional auto modes by adding additional commands to the
   * chooser code above (like the commented example) or additional comparisons
   * to the switch structure below with additional strings & commands.
   */
  @Override
  public void autonomousInit() {
    m_autonomousCommand = m_chooser.getSelected();
    driveSubsystem.setEnabled(true);

    /*
     * String autoSelected = SmartDashboard.getString("Auto Selector",
     * "Default"); switch(autoSelected) { case "My Auto": autonomousCommand
     * = new MyAutoCommand(); break; case "Default Auto": default:
     * autonomousCommand = new ExampleCommand(); break; }
     */

    // schedule the autonomous command (example)
    if (m_autonomousCommand != null) {
      m_autonomousCommand.start();
    }
  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
    Scheduler.getInstance().run();
  }

  @Override
  public void teleopInit() {
    // This makes sure that the autonomous stops running when
    // teleop starts running. If you want the autonomous to
    // continue until interrupted by another command, remove
    // this line or comment it out.
    if (m_autonomousCommand != null) {
      m_autonomousCommand.cancel();
    }
    driveSubsystem.setEnabled(true);
  }

  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {
    Scheduler.getInstance().run();
  }

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }
}
