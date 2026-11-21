package org.firstinspires.ftc.teamcode.RoboWarriors.System;

// Thrown when robot code asks for hardware the config doesn't define.
public class MissingHardwareException extends RuntimeException {
    public MissingHardwareException(String message) {
        super(message);
    }
}