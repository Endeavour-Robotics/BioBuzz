package org.firstinspires.ftc.teamcode.rw.sys;

import java.util.List;
import java.util.Map;

public class configClasses {
    public Meta meta;
    public Hardware hardware;
    public MotorPowers motorPowers;
    public Controls controls;

    public static class Meta {
        public String configName, teamNumber, teamName, version, game;
        public Integer year;
        public Boolean prod;
    }
    public static class Hardware {
        public Map<String, List<String>> motorConfig;   // [name, direction, runMode, zeroPower]
        public Map<String, List<String>> servoConfig;   // [name]
        public Map<String, Object> imuConfig;
    }
    public static class MotorPowers {
        public Map<String, Double> speedLimits;
        public List<Double> intake, outtake;
    }
    public static class Controls {
        public String scheme;
        public Boolean invert;
        public Double deadZone;
    }
}