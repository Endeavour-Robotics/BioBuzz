package org.firstinspires.ftc.teamcode.RoboWarriors.System;

import com.qualcomm.robotcore.hardware.DcMotor;

import java.util.*;

public class bot {

    public configBuilder config = new configBuilder();
    public double flLimit;
    public double frLimit;
    public double blLimit;
    public double brLimit;
    public double inLimit;
    public DcMotor fl, fr, bl, br;
    public DcMotor in, out;

    public TeleOp teleop = new TeleOp();
    public Auto auto = new Auto();
    private double limitHelper(double speed, double limit) { return Math.abs(speed) / limit; }
    private void botDrive(double fb, double rt, double cw) {
        double flSpeed = Objects.requireNonNull(config.kinematics.get("fl")).calculate(fb, rt, cw);
        double frSpeed = Objects.requireNonNull(config.kinematics.get("fr")).calculate(fb, rt, cw);
        double blSpeed = Objects.requireNonNull(config.kinematics.get("bl")).calculate(fb, rt, cw);
        double brSpeed = Objects.requireNonNull(config.kinematics.get("br")).calculate(fb, rt, cw);

        // Prevent one wheel from being faster than the other //
        double flRatio = limitHelper(flSpeed, flLimit);
        double frRatio = limitHelper(frSpeed, frLimit);
        double blRatio = limitHelper(blSpeed, blLimit);
        double brRatio = limitHelper(brSpeed, brLimit);

        double worst = Math.max(Math.max(flRatio, frRatio), Math.max(blRatio, brRatio));

        if (worst > 1.0) { // One motor is above 1
            flSpeed /= worst;       // shrink all four by the same amount;; Needed for meccanum drive
            frSpeed /= worst;
            blSpeed /= worst;
            brSpeed /= worst;
        }
        //==========================//

        if (fl != null) fl.setPower(flSpeed);
        if (fr != null) fr.setPower(frSpeed);
        if (bl != null) bl.setPower(blSpeed);
        if (br != null) br.setPower(brSpeed);
    }

    public void init(configBuilder initializedConfig) {
        this.config = initializedConfig;

        this.fl = config.fl();
        this.fr = config.fr();
        this.bl = config.bl();
        this.br = config.br();
        this.in = config.has("intake") ? config.motor("intake") : null;
        this.out = config.has("outtake") ? config.motor("outtake") : null;

        this.flLimit = config.getDefaultSpeedLimit("fl");
        this.frLimit = config.getDefaultSpeedLimit("fr");
        this.blLimit = config.getDefaultSpeedLimit("bl");
        this.brLimit = config.getDefaultSpeedLimit("br");
        this.inLimit = config.config.motorPowers.intake.get(0);
    }

    @FunctionalInterface
    public interface MotorEquation { double calculate(double fb, double rt, double cw); }
    public class TeleOp {
        public void drive(double fb, double rt, double cw) {
            botDrive(fb, rt, cw);
        }

        public void take(String inout, double takePower) {
            if (inout.equals("in")) {
                if (in != null) in.setPower(takePower);
            } else if (inout.equals("out")) {
                if (out != null) out.setPower(takePower);
            }
        }
    }

    public class Auto {
        public void drive(double fb, double rt, double cw) {
            botDrive(fb, rt, cw);
        }

        public void spin(String inout, double speed) {
            if (inout.equals("in") || inout.equals("intake")) {
                double intake = limitHelper(speed, inLimit);
                if (in != null) in.setPower(intake);
            }

            if (inout.equals("out") || inout.equals("outtake") || /* Some people don't know */ inout.equals("outake")) {
                if (out != null) out.setPower(speed);
            }
        }
    }

    /** @param movement Stops the four movement motors
     *  @param inout Stops intake and outtake motors   */
    public void stop(boolean movement, boolean inout) {
        if (movement) {
            if (fl != null) fl.setPower(0);
            if (fr != null) fr.setPower(0);
            if (bl != null) bl.setPower(0);
            if (br != null) br.setPower(0);
        }

        if (inout){
            if (in != null) in.setPower(0);
            if (out != null) out.setPower(0);
        }
    }
}
