<div align="center"><img width="145" height="124.5" alt="first_ftc_biobuzz_honeycomb" src="https://github.com/user-attachments/assets/f4300d7b-cfed-4a2d-b0d9-cf1ef4034d1e" /></div>
<h1 align="center">
  Endeavour Robotics BioBuzz
  <br>
  <sub>Codebase</sub>
</h1>

<p> This repository contains the work of the programming teams of the Endeavour Robotics community. Endeavour Robotics is made up of two teams, that being the RoboWarriors (31767) and TechTitans (18989), who work together to make it through the year. </p>

<p> There are many notable concepts and scripts in this repository, such as: </p>
<ul><li> A JSONC based auto-configurator</li><li> Unit tests for this configuring method </li><li> Pedro-Pathing autonomous </li><li> Organized TeleOP with Debug Modes </li></ul>

<p> These have been made over time and might be used in later seasons. If you wish to see how they work, just check the <code>TeamCode/src/main/java/org/firstinspires/ftc/teamcode/</code> directory and look for any file similarly named or containing config. The actual configurations exist on the Control Hub's <code>sd:/FIRST</code> folder and are not provided here, though the schema is provided and can be used in Android Studio. </p>

<p> This is an example of our JSONC config, to show how it should be structured. Comments are optional, but won't cause issues as they are stripped or read leniently. Any SEASON SPECIFIC keys are marked as so, and are set up in their respective <code>"$season" + "Configurer"</code> class. The schema prevents extra keys, so modify the schema itself to add extra for your team. Just know that this was all 31767 :) </p>

```jsonc
  {
    "$schema": "./biobuzz31767.schema.json",

    // All accessible via config.meta.$key
    "meta": {
      "configName": "example",
      "teamNumber": "31767", // Keep as string
      "teamName": "RoboWarriors",
      "version": "0.0.2", // Config Version
      "game": "BIOBUZZ", // Game Name;; 2025: DECODE; 2026: BIOBUZZ
      "year": 2026, // Add one for secondary year (ex 2026 -> 2027)
      "lastEdited": "11/21/2026",
      "prod": false // Production?
    },
  
    "hardware": {
      "motorConfig": {
        // fl, fr, bl, br are all required.
        "fl": ["FrontLeft", "REVERSE", "RUN_WITHOUT_ENCODER", "BRAKE"],
        "fr": ["FrontRight", "FORWARD", "RUN_WITHOUT_ENCODER", "BRAKE"],
        "bl": ["BackLeft", "REVERSE", "RUN_WITHOUT_ENCODER", "BRAKE"],
        "br": ["BackRight", "FORWARD", "RUN_WITHOUT_ENCODER", "BRAKE"],
  
        "intake": ["Intake", "REVERSE", "RUN_WITHOUT_ENCODER", "BRAKE"], // SEASON SPECIFIC
        "outtake": ["Outtake", "FORWARD", "RUN_WITHOUT_ENCODER", "FLOAT"] // SEASON SPECIFIC
      },
      "servoConfig": {
        // This has not been set up
      },
      "imuConfig": {
        // This has not been set up
      }
    },
  
    "motorPowers":{
      "speedLimits": {
        "fl": 0.7,
        "fr": 0.7,
        "bl": 0.7,
        "br": 0.7
      },
      // Each item of the array is added as a possible power, not as a range
      "intake": [0.7], // SEASON SPECIFIC
      "outtake": [0.45, 0.65, 0.8] // SEASON SPECIFIC
    },
  
    "controls": {
      "scheme": "joystick", // Only joystick has been implemented, others may come such as our DECODE control scheme
      "invert": false, // "invert": false = right stick for forward/backward/strafe; left stick for turn
      "deadZone": 0.25 // 0-1
    } 
  }
```

<p> If you are interested in how the contents of our teams' season code works, just look at our code or email us at <code>emsrobotics@newhaven.misd.net</code></p>
