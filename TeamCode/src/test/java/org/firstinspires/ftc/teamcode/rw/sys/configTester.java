package org.firstinspires.ftc.teamcode.rw.sys;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class configTester {
    private static String q(String s) { return s == null ? "null" : "\"" + s + "\""; }

    // One motorConfig entry: "key": [name, direction, runMode, zeroPower]
    private static String motor(String key, String name, String dir, String mode, String zero) {
        return "\"" + key + "\": [" + q(name) + ", " + q(dir) + ", " + q(mode) + ", " + q(zero) + "]";
    }

    // Conjoin the not null parts with commas
    private static String join(String... parts) {
        StringBuilder sb = new StringBuilder();
        for (String p : parts) {
            if (p == null) continue;
            if (sb.length() > 0) sb.append(", ");
            sb.append(p);
        }
        return sb.toString();
    }

    private static final String FL = motor("fl", "FrontLeft", "REVERSE", "RUN_WITHOUT_ENCODER", "BRAKE");
    private static final String FR = motor("fr", "FrontRight", "FORWARD", "RUN_WITHOUT_ENCODER", "BRAKE");
    private static final String BL = motor("bl", "BackLeft", "REVERSE", "RUN_WITHOUT_ENCODER", "BRAKE");
    private static final String BR = motor("br", "BackRight", "FORWARD", "RUN_WITHOUT_ENCODER", "BRAKE");
    private static final String INTAKE = motor("intake", "Intake", "REVERSE", "RUN_WITHOUT_ENCODER", "BRAKE");
    private static final String OUTTAKE = motor("outtake", "Outtake", "FORWARD", "RUN_WITHOUT_ENCODER", "FLOAT");
    private static final String ALL_MOTORS = join(FL, FR, BL, BR, INTAKE, OUTTAKE);

    private static final String LIMITS = "\"fl\": 0.7, \"fr\": 0.7, \"bl\": 0.7, \"br\": 0.7";
    private static final String CONTROLS = "{ \"scheme\": \"joystick\", \"invert\": false, \"deadZone\": 0.1 }";

    // config.motorPowers.* keys/values
    private static String powers(String limits, String intake, String outtake) {
        return join(
                limits == null ? null : "\"speedLimits\": { " + limits + " }",
                intake == null ? null : "\"intake\": [" + intake + "]",
                outtake == null ? null : "\"outtake\": [" + outtake + "]");
    }

    private static final String VALID_POWERS = powers(LIMITS, "0.7", "0.45, 0.65, 0.8");
    private static String cfg(String motors, String motorPowers, String controls) {
        StringBuilder sb = new StringBuilder();
        sb.append("{ \"meta\": { \"configName\": \"test\" },\n");
        sb.append("  \"hardware\": { \"motorConfig\": { ").append(motors).append(" } }");
        if (motorPowers != null) sb.append(",\n  \"motorPowers\": { ").append(motorPowers).append(" }");
        if (controls != null) sb.append(",\n  \"controls\": ").append(controls);
        sb.append("\n}");
        return sb.toString();
    }

    private static String good() { return cfg(ALL_MOTORS, VALID_POWERS, CONTROLS); }
    private static String withFl(String flEntry) {
        return cfg(join(flEntry, FR, BL, BR, INTAKE, OUTTAKE), VALID_POWERS, CONTROLS);
    }

    private static configBuilder load(String json) {
        configBuilder b = new configBuilder();
        b.loadAndValidate(json, "test.jsonc", new biobuzzConfigurer());
        return b;
    }

    private static void assertNoErrors(configBuilder b) {
        assertTrue("unexpected errors: " + b.errors, b.errors.isEmpty());
    }

    // Passes if SOME message has all the stuff
    private static void assertAny(String what, List<String> messages, String... needles) {
        for (String m : messages) {
            boolean all = true;
            for (String n : needles) {
                if (!m.contains(n)) { all = false; break; }
            }
            if (all) return;
        }
        fail("expected a " + what + " containing " + Arrays.toString(needles) + " but got: " + messages);
    }

    private static void assertError(configBuilder b, String... needles) { assertAny("error", b.errors, needles); }
    private static void assertWarning(configBuilder b, String... needles) { assertAny("warning", b.warnings, needles); }

    @Test
    public void goodConfig_hasNoErrorsOrWarnings() {
        configBuilder b = load(good());
        assertNoErrors(b);
        assertTrue("unexpected warnings: " + b.warnings, b.warnings.isEmpty());
        assertNotNull(b.config);
    }

    @Test
    public void bareMinimumConfig_loadsWithoutASeasonConfigurer() {
        // Four wheels, speed limits, controls.
        String json = cfg(join(FL, FR, BL, BR), powers(LIMITS, null, null), CONTROLS);
        configBuilder b = new configBuilder();
        b.loadAndValidate(json, "bare.jsonc", null);
        assertNoErrors(b);
        assertTrue("unexpected warnings: " + b.warnings, b.warnings.isEmpty());
    }

    @Test
    public void comments_areAllowed() {
        String json = "// leading comment\n"
                + good().replace("\"test\"", "\"test\" /* inline comment */");
        assertNoErrors(load(json));
    }

    // Broken Files
    @Test
    public void brokenJson_reportsOneBadJsonError() {
        configBuilder b = load("{ this is not json");
        assertEquals(b.errors.toString(), 1, b.errors.size());
        assertError(b, "Bad JSON");
    }

    @Test
    public void wrongTopLevelType_reportsBadJson() {
        configBuilder b = load("[1, 2, 3]");
        assertEquals(b.errors.toString(), 1, b.errors.size());
        assertError(b, "Bad JSON");
    }

    @Test
    public void emptyFile_reportsOneEmptyError() {
        configBuilder b = load("");
        assertEquals(b.errors.toString(), 1, b.errors.size());
        assertError(b, "empty");
    }

    @Test
    public void whitespaceOnlyFile_reportsOneEmptyError() {
        configBuilder b = load("   \n  \n");
        assertEquals(b.errors.toString(), 1, b.errors.size());
        assertError(b, "empty");
    }

    @Test
    public void missingHardwareBlock_isReported() {
        configBuilder b = load("{ \"meta\": { \"configName\": \"test\" } }");
        assertEquals(b.errors.toString(), 1, b.errors.size());
        assertError(b, "motorConfig");
    }

    // Motor stuff

    @Test
    public void missingWheel_isReported() {
        // bl is gone from motorConfig but still has a speed limit, two things are wrong then
        configBuilder b = load(cfg(join(FL, FR, BR, INTAKE, OUTTAKE), VALID_POWERS, CONTROLS));
        assertError(b, "Required motor 'bl'");
        assertError(b, "'bl'", "has limit but not in 'motorConfig'");
    }

    @Test
    public void shortMotorEntry_isReported() {
        configBuilder b = load(withFl("\"fl\": [\"FrontLeft\", \"REVERSE\", \"RUN_WITHOUT_ENCODER\"]"));
        assertEquals(b.errors.toString(), 1, b.errors.size());
        assertError(b, "'fl'", "needs 4 values", "found 3");
    }

    @Test
    public void badDirection_namesTheMotorAndTheValue() {
        configBuilder b = load(withFl(motor("fl", "FrontLeft", "REVRSE", "RUN_WITHOUT_ENCODER", "BRAKE")));
        assertEquals(b.errors.toString(), 1, b.errors.size());
        assertError(b, "'fl'", "REVRSE", "direction");
    }

    @Test
    public void badRunMode_isReported() {
        configBuilder b = load(withFl(motor("fl", "FrontLeft", "REVERSE", "RUN_FAST", "BRAKE")));
        assertEquals(b.errors.toString(), 1, b.errors.size());
        assertError(b, "'fl'", "RUN_FAST", "run mode");
    }

    @Test
    public void badZeroPower_isReported() {
        configBuilder b = load(withFl(motor("fl", "FrontLeft", "REVERSE", "RUN_WITHOUT_ENCODER", "COAST")));
        assertEquals(b.errors.toString(), 1, b.errors.size());
        assertError(b, "'fl'", "COAST", "zero power");
    }

    @Test
    public void emptyHardwareName_isReported() {
        configBuilder b = load(withFl(motor("fl", "", "REVERSE", "RUN_WITHOUT_ENCODER", "BRAKE")));
        assertEquals(b.errors.toString(), 1, b.errors.size());
        assertError(b, "'fl'", "hardware name");
    }

    @Test
    public void nullDirection_isReported() {
        configBuilder b = load(withFl(motor("fl", "FrontLeft", null, "RUN_WITHOUT_ENCODER", "BRAKE")));
        assertEquals(b.errors.toString(), 1, b.errors.size());
        assertError(b, "'fl'", "direction is null");
    }

    @Test
    public void extraMotorEntries_areCheckedToo() {
        // Not a required key, but every entry in the file will be set up, so every entry is validated.
        String spare = motor("spare", "Spare", "SIDEWAYS", "RUN_WITHOUT_ENCODER", "BRAKE");
        configBuilder b = load(cfg(join(ALL_MOTORS, spare), VALID_POWERS, CONTROLS));
        assertEquals(b.errors.toString(), 1, b.errors.size());
        assertError(b, "'spare'", "SIDEWAYS");
    }

    @Test
    public void severalProblems_areAllReportedInOneRun() {
        String badFl = motor("fl", "FrontLeft", "REVRSE", "RUN_WITHOUT_ENCODER", "BRAKE"); // bad direction
        String limits = "\"fl\": 7, \"fr\": 0.7, \"bl\": 0.7, \"br\": 0.7";               // fl limit out of range
        // br is missing from motorConfig, but still has a limit
        configBuilder b = load(cfg(join(badFl, FR, BL), powers(limits, "0.7", "0.45"), CONTROLS));
        assertError(b, "'fl'", "REVRSE");
        assertError(b, "Required motor 'br'");
        assertError(b, "fl", "outside of [> 0 to 1]");
        assertError(b, "'br'", "has limit but not in 'motorConfig'");
    }

    // Speed limit stuff

    @Test
    public void speedLimitAboveOne_isAnError() {
        configBuilder b = load(cfg(ALL_MOTORS,
                powers("\"fl\": 7, \"fr\": 0.7, \"bl\": 0.7, \"br\": 0.7", "0.7", "0.45"), CONTROLS));
        assertEquals(b.errors.toString(), 1, b.errors.size());
        assertError(b, "fl", "outside of [> 0 to 1]");
    }

    @Test
    public void negativeSpeedLimit_isAnError() {
        configBuilder b = load(cfg(ALL_MOTORS,
                powers("\"fl\": -0.1, \"fr\": 0.7, \"bl\": 0.7, \"br\": 0.7", "0.7", "0.45"), CONTROLS));
        assertEquals(b.errors.toString(), 1, b.errors.size());
        assertError(b, "fl", "outside of [> 0 to 1]");
    }

    @Test
    public void nullSpeedLimit_warnsAndUsesDefault() {
        configBuilder b = load(cfg(ALL_MOTORS,
                powers("\"fl\": null, \"fr\": 0.7, \"bl\": 0.7, \"br\": 0.7", "0.7", "0.45"), CONTROLS));
        assertNoErrors(b);
        assertWarning(b, "null value", "fl");
        assertEquals(0.8, b.config.motorPowers.speedLimits.get("fl"), 1e-9);
    }

    @Test
    public void speedLimitForUnknownMotor_isAnError() {
        configBuilder b = load(cfg(ALL_MOTORS, powers(LIMITS + ", \"fll\": 0.7", "0.7", "0.45"), CONTROLS));
        assertEquals(b.errors.toString(), 1, b.errors.size());
        assertError(b, "'fll'", "has limit but not in 'motorConfig'");
    }

    @Test
    public void wheelWithNoLimit_getsTheDefault() {
        configBuilder b = load(cfg(ALL_MOTORS,
                powers("\"fl\": 0.7, \"fr\": 0.7, \"br\": 0.7", "0.7", "0.45"), CONTROLS));
        assertNoErrors(b);
        assertEquals(0.8, b.getDefaultSpeedLimit("bl"), 1e-9);
        assertEquals(0.7, b.getDefaultSpeedLimit("fl"), 1e-9);
    }

    @Test
    public void missingSpeedLimitsBlock_isAnError() {
        configBuilder b = load(cfg(ALL_MOTORS, powers(null, "0.7", "0.45"), CONTROLS));
        assertEquals(b.errors.toString(), 1, b.errors.size());
        assertError(b, "speedLimits");
    }

    @Test
    public void missingMotorPowersBlock_isAnErrorNotACrash() {
        configBuilder b = load(cfg(ALL_MOTORS, null, CONTROLS));
        assertError(b, "speedLimits");
    }

    // Control tets stuff

    @Test
    public void missingControlsBlock_warnsAndUsesDefaults() {
        configBuilder b = load(cfg(ALL_MOTORS, VALID_POWERS, null));
        assertNoErrors(b);
        assertWarning(b, "Controls");
        assertEquals(0.15, b.config.controls.deadZone, 1e-9);
        assertFalse(b.config.controls.invert);
    }

    @Test
    public void missingDeadZone_warnsAndUsesDefault() {
        configBuilder b = load(cfg(ALL_MOTORS, VALID_POWERS, "{ \"scheme\": \"joystick\", \"invert\": false }"));
        assertNoErrors(b);
        assertWarning(b, "deadZone");
        assertEquals(0.15, b.config.controls.deadZone, 1e-9);
    }

    @Test
    public void deadZoneOutOfRange_isAnError() {
        configBuilder b = load(cfg(ALL_MOTORS, VALID_POWERS,
                "{ \"scheme\": \"joystick\", \"invert\": false, \"deadZone\": 5 }"));
        assertEquals(b.errors.toString(), 1, b.errors.size());
        assertError(b, "Dead Zone");
    }

    @Test
    public void missingInvert_warnsAndDefaultsToFalse() {
        configBuilder b = load(cfg(ALL_MOTORS, VALID_POWERS, "{ \"scheme\": \"joystick\", \"deadZone\": 0.1 }"));
        assertNoErrors(b);
        assertWarning(b, "Invert");
        assertFalse(b.config.controls.invert);
    }

    // Season specific (biobuzz)

    @Test
    public void missingIntakePresets_warnAndUseDefault() {
        configBuilder b = load(cfg(ALL_MOTORS, powers(LIMITS, null, "0.45, 0.65, 0.8"), CONTROLS));
        assertNoErrors(b);
        assertWarning(b, "intake");
        assertEquals(Arrays.asList(0.7), b.config.motorPowers.intake);
    }

    @Test
    public void emptyOuttakePresets_warnAndUseDefaults() {
        configBuilder b = load(cfg(ALL_MOTORS, powers(LIMITS, "0.7", ""), CONTROLS));
        assertNoErrors(b);
        assertWarning(b, "outtake");
        assertEquals(Arrays.asList(0.45, 0.65, 0.8), b.config.motorPowers.outtake);
    }

    @Test
    public void outtakePresetOutOfRange_isAnError() {
        configBuilder b = load(cfg(ALL_MOTORS, powers(LIMITS, "0.7", "0.45, 7"), CONTROLS));
        assertEquals(b.errors.toString(), 1, b.errors.size());
        assertError(b, "outtake");
    }

    @Test
    public void nullIntakePreset_isAnError() {
        configBuilder b = load(cfg(ALL_MOTORS, powers(LIMITS, "0.7, null", "0.45"), CONTROLS));
        assertEquals(b.errors.toString(), 1, b.errors.size());
        assertError(b, "intake");
    }

    @Test
    public void fullPowerPresets_areAllowed() {
        configBuilder b = load(cfg(ALL_MOTORS, powers(LIMITS, "1.0, -1.0", "1.0"), CONTROLS));
        assertNoErrors(b);
    }
}
