package org.usfirst.frc.team228.robot;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.Compressor;

import edu.wpi.first.wpilibj.drive.MecanumDrive;	//Imported if we need it
import edu.wpi.first.wpilibj.drive.DifferentialDrive;	//Needed for drive train

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.XboxController;

import org.usfirst.frc.team228.robot.commands.ExampleCommand;
import org.usfirst.frc.team228.robot.subsystems.ExampleSubsystem;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
	
	XboxController driverController, operatorController;
	DifferentialDrive robotDrive;
	VictorSP leftDrive1, leftDrive2, rightDrive1, rightDrive2;
	
	final String arcadeMode = "Arcade";
	final String tankMode = "Tank";
	final String GTAMode = "GTA";
	String driverMode;
	
	SendableChooser<String> selectedDriverMode;
	
	


	public static final ExampleSubsystem exampleSubsystem = new ExampleSubsystem();
	public static OI oi;

	Command autonomousCommand;
	SendableChooser<Command> chooser = new SendableChooser<>();

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	public void robotInit() {
		oi = new OI();
		chooser.addDefault("Default Auto", new ExampleCommand());
		// chooser.addObject("My Auto", new MyAutoCommand());
		SmartDashboard.putData("Auto mode", chooser);
		
		//Set up driver mode choices
		selectedDriverMode.addDefault("Arcade Drive", arcadeMode);
		selectedDriverMode.addObject("Tank Mode", tankMode);
		selectedDriverMode.addObject("GTA Mode", GTAMode);
		SmartDashboard.putData("Drive Options", selectedDriverMode);
		
		//Assign Victors
		leftDrive1 = new VictorSP(0);
		leftDrive2 = new VictorSP(1);
		rightDrive1 = new VictorSP(2);
		rightDrive2 = new VictorSP(3);
		
		//Since robotDrive can only take two SpeedControllers, we'll set up two
		//SpeedController Groups to make a four-motor drive
		SpeedControllerGroup leftDrive = new SpeedControllerGroup(leftDrive1, leftDrive2);
		SpeedControllerGroup rightDrive = new SpeedControllerGroup(rightDrive1, rightDrive2);
		
		//Assign the robot drive with the two SpeedController Groups
		robotDrive = new DifferentialDrive(leftDrive, rightDrive);
		
		//Assign the two controllers based on what port they're in
		driverController = new XboxController(0);
		operatorController = new XboxController(1);
	}

	/**
	 * This function is called once each time the robot enters Disabled mode.
	 * You can use it to reset any subsystem information you want to clear when
	 * the robot is disabled.
	 */
	public void disabledInit() {

	}

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
	 * You can add additional auto modes by adding additional commands to the
	 * chooser code above (like the commented example) or additional comparisons
	 * to the switch structure below with additional strings & commands.
	 */
	public void autonomousInit() {
		autonomousCommand = chooser.getSelected();		

		/*
		 * String autoSelected = SmartDashboard.getString("Auto Selector",
		 * "Default"); switch(autoSelected) { case "My Auto": autonomousCommand
		 * = new MyAutoCommand(); break; case "Default Auto": default:
		 * autonomousCommand = new ExampleCommand(); break; }
		 */

		// schedule the autonomous command (example)
		if (autonomousCommand != null)
			autonomousCommand.start();
	}

	/**
	 * This function is called periodically during autonomous
	 */
	public void autonomousPeriodic() {
		Scheduler.getInstance().run();
	}

	public void teleopInit() {
		// This makes sure that the autonomous stops running when
		// teleop starts running. If you want the autonomous to
		// continue until interrupted by another command, remove
		// this line or comment it out.
		if (autonomousCommand != null)
			autonomousCommand.cancel();
	}

	/**
	 * This function is called periodically during operator control
	 */
	public void teleopPeriodic() {
		//Scheduler.getInstance().run();
		robotTeleop();
		
	}
	/**
	 * This function is called periodically during test mode
	 */
	public void testPeriodic() {
	}
	/***
	 * This function is responsible for driving the robot during teleop period.  It covers
	 * driving functions as well as operator control
	 */
	public void robotTeleop() {
		
		//DRIVER CONTROLS
		double arcadeLeftStick;
		double tankLeftStick;
		double tankRightStick;
		//Set the selected driver mode
		driverMode = (String)selectedDriverMode.getSelected();
		
		//switch between different driver modes based on the selected one
		switch(driverMode) {
		//Arcade drive
		case arcadeMode:
			if(driverController.getRawAxis(1) >= 0) {
				arcadeLeftStick = Math.pow(driverController.getRawAxis(1), 2);
			}
			else {
				arcadeLeftStick = (-1 * Math.pow(driverController.getRawAxis(1), 2));
			}
			robotDrive.arcadeDrive(arcadeLeftStick, driverController.getRawAxis(4));
			break;
		//Tank drive
		case tankMode:
			if(driverController.getRawAxis(1) >= 0) {
				tankLeftStick = Math.pow(driverController.getRawAxis(1), 2);
			}
			else {
				tankLeftStick = (-1 * Math.pow(driverController.getRawAxis(1), 2));
			}
			if(driverController.getRawAxis(5) >= 0) {
				tankRightStick = Math.pow(driverController.getRawAxis(5), 2);
			}
			else {
				tankRightStick = (-1 * Math.pow(driverController.getRawAxis(5), 2));
			}
			robotDrive.tankDrive(tankLeftStick, tankRightStick);
			break;
		//GTA drive
		case GTAMode:
			break;
			
		}
		
		//OPERATOR CONTROLS
	}
}
