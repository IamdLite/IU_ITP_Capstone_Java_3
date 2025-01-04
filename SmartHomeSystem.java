

import java.util.*;

/**
 * SmartHomeManagementSystem is the main class that controls the others.
 * It is the main file of the program.
 */
public class SmartHomeSystem {

    private static final Scanner scanner = new Scanner(System.in);

    private static final String TURN_ON = "TurnOn";
    private static final String TURN_OFF = "TurnOff";
    private static final String START_CHARGING = "StartCharging";
    private static final String STOP_CHARGING = "StopCharging";
    private static final String START_RECORDING = "StartRecording";
    private static final String STOP_RECORDING = "StopRecording";

    public static void main(String[] args) {
        List<SmartDevice> smartHome = initializeSmartDevices();

        boolean run = true;
        while (run) {
            String command = scanner.nextLine().trim(); // Handle extra whitespace
            String[] commandParts = command.split("\\s+"); // Split input by spaces


            if (commandParts[0].equals("DisplayAllStatus")) {
                handleDisplayAllStatus(smartHome);
            } else if (commandParts[0].equals(TURN_ON) || commandParts[0].equals(TURN_OFF)) {
                handleTurnOnOff(commandParts, smartHome);
            } else if (commandParts[0].equals(START_CHARGING) || commandParts[0].equals(STOP_CHARGING)) {
                handleStartStopCharging(commandParts, smartHome);
            } else if (commandParts[0].equals("SetTemperature")) {
                handleSetTemperature(commandParts, smartHome);
            } else if (commandParts[0].equals("SetBrightness")) {
                handleSetBrightness(commandParts, smartHome);
            } else if (commandParts[0].equals("SetColor")) {
                handleSetColor(commandParts, smartHome);
            } else if (commandParts[0].equals("SetAngle")) {
                handleSetAngle(commandParts, smartHome);
            } else if (commandParts[0].equals(START_RECORDING) || commandParts[0].equals(STOP_RECORDING)) {
                handleStartStopRecording(commandParts, smartHome);
            } else if (commandParts[0].equals("end") & commandParts.length == 1) {
                run = false;
                break;
            } else {
                Utils.generateErrorMessage(null, Utils.errorCodes.INVALIDCOMMAND);
            }
        }
    }

    /**
     * Initializes the smart devices.
     * @return A list of initialized smart devices.
     */
    private static List<SmartDevice> initializeSmartDevices() {
        List<SmartDevice> smartDevices = new ArrayList<>();
        LightColor yellow = LightColor.YELLOW;
    
        // Initialize lights
        for (int i = 0; i < 4; i++) {
            Light light = new Light(Status.ON, false, BrightnessLevel.LOW, yellow);
            // Set the device ID using the setter method in SmartDevice abstract class
            smartDevices.add(light);
            
        }
    
        // Initialize cameras
        for (int i = 4; i < 6; i++) {
            Camera camera = new Camera(Status.ON, false, false, 45);
            // Set the device ID using the setter method in SmartDevice
            smartDevices.add(camera);
        }
    
        // Initialize heaters
        for (int i = 6; i < 10; i++) {
            Heater heater = new Heater(Status.ON, 20);
           // Set the device ID using the setter method in SmartDevice
            smartDevices.add(heater);
        }
    
        return smartDevices;
    }
    
    /**
     * Displays the status of all devices.
     * @param smartHome The list of smart devices.
     */
    private static void handleDisplayAllStatus(List<SmartDevice> smartHome) {
        for (SmartDevice device : smartHome) {
            System.out.println(device.displayStatus());
        }
    }

    /**
     * Handles turning devices on or off.
     * @param commandParts The command parts.
     * @param smartHome The list of smart devices.
     */
    private static void handleTurnOnOff(String[] commandParts, List<SmartDevice> smartHome) {
        if (commandParts.length != 3) {
            Utils.generateErrorMessage(null, Utils.errorCodes.INVALIDCOMMAND);
            return;
        }

        try {
            int deviceId = Integer.parseInt(commandParts[2]);
            SmartDevice device = getDeviceById(deviceId, commandParts[1], smartHome);
            if (device == null) {
                Utils.generateErrorMessage(null, Utils.errorCodes.DEVICENOTFOUND);
                return;
            }

            if (commandParts[0].equals(TURN_ON)) {
                if (device.turnOn()) {
                    Utils.generateSuccessMessage(device, Utils.successCodes.DEVICETURNEDON);
                } else {
                    Utils.generateErrorMessage(device, Utils.errorCodes.DEVICEALREADYON);
                }
              
            } else if (commandParts[0].equals(TURN_OFF)) {
              if(device.turnOff()){
                Utils.generateSuccessMessage(device, Utils.successCodes.DEVICETURNEDOFF);
              } else {
                Utils.generateErrorMessage(device, Utils.errorCodes.DEVICEALREADYOFF);
              }
        }
    } catch (NumberFormatException e) {
            Utils.generateErrorMessage(null, Utils.errorCodes.INVALIDCOMMAND);
        }
    }

    /**
     * Handles starting or stopping charging for chargeable devices.
     * @param commandParts The command parts.
     * @param smartHome The list of smart devices.
     */
    private static void handleStartStopCharging(String[] commandParts, List<SmartDevice> smartHome) {
        if (commandParts.length != 3) {
            Utils.generateErrorMessage(null, Utils.errorCodes.INVALIDCOMMAND);
            return;
        }
        // Check if device name is a string
        // if (!commandParts[1].matches("[a-zA-Z]+")) {
        //     Utils.generateErrorMessage(null, Utils.errorCodes.INVALIDCOMMAND);
        //     return;
        // }

        try {
            int deviceId = Integer.parseInt(commandParts[2]);
            SmartDevice device = getDeviceById(deviceId, commandParts[1], smartHome);
            if(device == null){
                Utils.generateErrorMessage(null, Utils.errorCodes.DEVICENOTFOUND);
                return;
            }
            if (!(device instanceof Chargeable)) {
                Utils.generateErrorMessage(device, Utils.errorCodes.DEVICENOTCHARGEABLE);
                return;
            }

            Chargeable chargeableDevice = (Chargeable) device;
            if (commandParts[0].equals(START_CHARGING)) {
                if(chargeableDevice.isCharging()){
                    Utils.generateErrorMessage(device, Utils.errorCodes.DEVICEALREADYCHARGING);
                } else {
                    chargeableDevice.startCharging();
                    Utils.generateSuccessMessage(device, Utils.successCodes.CHARGINGSTARTED);
                }
            } else if (commandParts[0].equals(STOP_CHARGING)) {
               
                if(!chargeableDevice.isCharging()){
                    Utils.generateErrorMessage(device, Utils.errorCodes.DEVICENOTCHARGING);
                } else {
                chargeableDevice.stopCharging();
                Utils.generateSuccessMessage(device, Utils.successCodes.CHARGINGSTOPPED);
            }}
        } catch (NumberFormatException e) {
            Utils.generateErrorMessage(null, Utils.errorCodes.INVALIDCOMMAND);
        }
    }

    /**
     * Handles setting the temperature for heaters.
     * @param commandParts The command parts.
     * @param smartHome The list of smart devices.
     */
    private static void handleSetTemperature(String[] commandParts, List<SmartDevice> smartHome) {
        // Validate command length
        if (commandParts.length != 4) {
            Utils.generateErrorMessage(null, Utils.errorCodes.INVALIDCOMMAND);
            return;
        }
        // // Check if device name is a string
        // if (!commandParts[1].matches("[a-zA-Z]+")) {
        //     Utils.generateErrorMessage(null, Utils.errorCodes.INVALIDCOMMAND);
        //     return;
        // }
        // Check if temperature is a positive
        if (!commandParts[3].matches("-?\\d+")) {
            Utils.generateErrorMessage(null, Utils.errorCodes.INVALIDCOMMAND);
            return;
        }
    
        try {
            // Parse the device ID
            int deviceId = Integer.parseInt(commandParts[2]);
    
            // Retrieve the device by ID and name
            SmartDevice device = getDeviceById(deviceId, commandParts[1], smartHome);
    
            // Check if the device was found
            if (device == null) {
                Utils.generateErrorMessage(null, Utils.errorCodes.DEVICENOTFOUND);
                return;
            }


            // Check if device is on or off
            if(!device.checkStatusAccess()){
                Utils.generateErrorMessage(device, Utils.errorCodes.CANNOTCHANGEATTRIBUTE);
                return;
            }
    
            // Check if the device is a heater
            if (!(device instanceof Heater)) {
                Utils.generateErrorMessage(device, Utils.errorCodes.DEVICENOTHEATER);
                return;
            }


            // Parse the temperature
            int temperature = Integer.parseInt(commandParts[3]);
    
            // Set the temperature for the heater
            Heater heater = (Heater) device;
            if (!heater.setTemperature(temperature)) {
                Utils.generateErrorMessage(device, Utils.errorCodes.TEMPERATUREOUTOFRANGE);
            } else {
                Utils.generateSuccessMessage(device, Utils.successCodes.TEMPERATURESET);
            }
        } catch (NumberFormatException e) {
            // Handle invalid device ID or temperature format
            Utils.generateErrorMessage(null, Utils.errorCodes.INVALIDCOMMAND);
        }
    }
    

    /**
     * Handles setting the brightness for lights.
     * @param commandParts The command parts.
     * @param smartHome The list of smart devices.
     */
    private static void handleSetBrightness(String[] commandParts, List<SmartDevice> smartHome) {
        // Check if the command length is correct
        if (commandParts.length != 4) {
            Utils.generateErrorMessage(null, Utils.errorCodes.INVALIDCOMMAND);
            return;
        }
        // // Check if device name is a string
        // if (!commandParts[1].matches("[a-zA-Z]+")) {
        //     Utils.generateErrorMessage(null, Utils.errorCodes.INVALIDCOMMAND);
        //     return;
        // }
        // check if brightness level is a string
        if (!commandParts[3].matches("[a-zA-Z]+")) {
            Utils.generateErrorMessage(null, Utils.errorCodes.INVALIDCOMMAND);
            return;
        }


        try {
            // Parse the device ID
            int deviceId = Integer.parseInt(commandParts[2]);
    
            // Find the device by ID and name
            SmartDevice device = getDeviceById(deviceId, commandParts[1], smartHome);
    
            // Check if the device exists
            if (device == null) {
                Utils.generateErrorMessage(null, Utils.errorCodes.DEVICENOTFOUND);
                return;
            }

            // Check if device is on or off
            if(!device.checkStatusAccess()){
                Utils.generateErrorMessage(device, Utils.errorCodes.CANNOTCHANGEATTRIBUTE);
                return;
            }

            // Check if the device is a Light
            if (!(device instanceof Light)) {
                Utils.generateErrorMessage(device, Utils.errorCodes.DEVICENOTLIGHT);
                return;
            }



            // Check if the BrightnessLevel exists
            BrightnessLevel brightness = BrightnessLevel.valueOf(commandParts[3]);


    
            // Set the brightness level
            Light light = (Light) device;
            light.setBrightnessLevel(brightness);
            Utils.generateSuccessMessage(device, Utils.successCodes.BRIGHTNESSSET);
    
        } catch (NumberFormatException e) {
            // Handle invalid device ID format
            Utils.generateErrorMessage(null, Utils.errorCodes.INVALIDCOMMAND);
        } catch (IllegalArgumentException e) {
            // Handle invalid BrightnessLevel
            Utils.generateErrorMessage(null, Utils.errorCodes.BRIGHTNESSOUTBOUND);
        }
    }
    

    /**
     * Handles setting the color for lights.
     * @param commandParts The command parts.
     * @param smartHome The list of smart devices.
     */
    private static void handleSetColor(String[] commandParts, List<SmartDevice> smartHome) {
        // Check if the command length is correct
        if (commandParts.length != 4) {
            Utils.generateErrorMessage(null, Utils.errorCodes.INVALIDCOMMAND);
            return;
        }
        // // Check if device name is a string
        // if (!commandParts[1].matches("[a-zA-Z]+")) {
        //     Utils.generateErrorMessage(null, Utils.errorCodes.INVALIDCOMMAND);
        //     return;
        // }
        // check if color is a string
        if (!commandParts[3].matches("[a-zA-Z]+")) {
            Utils.generateErrorMessage(null, Utils.errorCodes.INVALIDCOMMAND);
            return;
        }
    
        try {
            // Parse the device ID
            int deviceId = Integer.parseInt(commandParts[2]);
    
            // Find the device by ID and name
            SmartDevice device = getDeviceById(deviceId, commandParts[1], smartHome);
    
            // Check if the device exists
            if (device == null) {
                Utils.generateErrorMessage(null, Utils.errorCodes.DEVICENOTFOUND);
                return;
            }
 
            // Check if device is on or off
            if(!device.checkStatusAccess()){
                Utils.generateErrorMessage(device, Utils.errorCodes.CANNOTCHANGEATTRIBUTE);
                return;
            }

            // Check if the device is a Light
            if (!(device instanceof Light)) {
                Utils.generateErrorMessage(device, Utils.errorCodes.DEVICENOTLIGHT);
                return;
            }

            // Validate the LightColor
            LightColor color = LightColor.valueOf(commandParts[3]);
    
            // Set the color for the light
            ((Light) device).setLightColor(color);
            Utils.generateSuccessMessage(device, Utils.successCodes.COLORSET);
    
        } catch (NumberFormatException e) {
            // Handle invalid device ID format
            Utils.generateErrorMessage(null, Utils.errorCodes.INVALIDCOMMAND);
        } catch (IllegalArgumentException e) {
            // Handle invalid LightColor
            Utils.generateErrorMessage(null, Utils.errorCodes.INVALIDCOLOR);
        }
    }
    
    /**
     * Handles setting the
     *      * angle for cameras.
     * @param commandParts The command parts.
     * @param smartHome The list of smart devices.
     */
    private static void handleSetAngle(String[] commandParts, List<SmartDevice> smartHome) {
        // Check if the command length is correct
        if (commandParts.length != 4) {
            Utils.generateErrorMessage(null, Utils.errorCodes.INVALIDCOMMAND);
            return;
        }
        // // Check if device name is a string
        // if (!commandParts[1].matches("[a-zA-Z]+")) {
        //     Utils.generateErrorMessage(null, Utils.errorCodes.INVALIDCOMMAND);
        //     return;
        // }
        // Check if angle is a positive or negative number
        if (!commandParts[3].matches("-?\\d+")) {
            Utils.generateErrorMessage(null, Utils.errorCodes.INVALIDCOMMAND);
            return;
        }
        try {
            // Parse the device ID
            int deviceId = Integer.parseInt(commandParts[2]);
    
            // Find the device by ID and name
            SmartDevice device = getDeviceById(deviceId, commandParts[1], smartHome);
    
            // Check if the device exists
            if (device == null) {
                Utils.generateErrorMessage(null, Utils.errorCodes.DEVICENOTFOUND);
                return;
            }

            // Check if device is on or off
            if(!device.checkStatusAccess()){
                Utils.generateErrorMessage(device, Utils.errorCodes.CANNOTCHANGEATTRIBUTE);
                return;
            }

            // Check if the device is a Camera
            if (!(device instanceof Camera)) {
                Utils.generateErrorMessage(device, Utils.errorCodes.DEVICENOTCAMERA);
                return;
            }

            // Check if device is on or off
            if(!device.checkStatusAccess()){
                Utils.generateErrorMessage(device, Utils.errorCodes.CANNOTCHANGEATTRIBUTE);
                return;
            }

            // Parse the angle value
            int angle = Integer.parseInt(commandParts[3]);
    
            // Set the angle for the camera
            Camera camera = (Camera) device;
            if(camera.setCameraAngle(angle)){
                Utils.generateSuccessMessage(device, Utils.successCodes.ANGLESET);
            } else {
                Utils.generateErrorMessage(device, Utils.errorCodes.CAMERAOUTOFRANGE);
            }
    
        } catch (NumberFormatException e) {
            // Handle invalid device ID or angle format
            Utils.generateErrorMessage(null, Utils.errorCodes.INVALIDCOMMAND);
        }
    }
    
    
    /**
     * Handles starting or stopping recording for cameras.
     * @param commandParts The command parts.
     * @param smartHome The list of smart devices.
     */
    private static void handleStartStopRecording(String[] commandParts, List<SmartDevice> smartHome) {
        // Check if the command length is correct
        if (commandParts.length != 3) {
            Utils.generateErrorMessage(null, Utils.errorCodes.INVALIDCOMMAND);
            return;
        }

        // // Check if device name is a string
        // if (!commandParts[1].matches("[a-zA-Z]+")) {
        //     Utils.generateErrorMessage(null, Utils.errorCodes.INVALIDCOMMAND);
        //     return;
        // }

    
        try {
            // Parse the device ID
            int deviceId = Integer.parseInt(commandParts[2]);
    
            // Find the device by ID and namen
            SmartDevice device = getDeviceById(deviceId, commandParts[1], smartHome);
    
            // Check if the device exists
            if (device == null) {
                Utils.generateErrorMessage(null, Utils.errorCodes.DEVICENOTFOUND);
                return;
            }
    
            // Check if the device is a Camera
            if (!(device instanceof Camera)) {
                Utils.generateErrorMessage(device, Utils.errorCodes.DEVICENOTCAMERA);
                return;
            }

            // Check if device is on or off
            if(!device.checkStatusAccess()){
                Utils.generateErrorMessage(device, Utils.errorCodes.CANNOTCHANGEATTRIBUTE);
                return;
            }
    
            // Cast the device to Camera
            Camera camera = (Camera) device;
    
            // Handle start/stop recording based on the command
            if (commandParts[0].equals(START_RECORDING)) {
                if (camera.isRecording()) {
                    Utils.generateErrorMessage(device, Utils.errorCodes.CAMERAALREADYRECORDING);
                } else {
                    camera.startRecording();
                    Utils.generateSuccessMessage(device, Utils.successCodes.RECORDINGSTARTED);
                }
            } else if (commandParts[0].equals(STOP_RECORDING)) {
                if (!camera.isRecording()) {
                    Utils.generateErrorMessage(device, Utils.errorCodes.CAMERANOTRECORDING);
                } else {
                    camera.stopRecording();
                    Utils.generateSuccessMessage(device, Utils.successCodes.RECORDINGSTOPPED);
                }
            } else {
                // Handle invalid commands
                Utils.generateErrorMessage(null, Utils.errorCodes.INVALIDCOMMAND);
            }
    
        } catch (NumberFormatException e) {
            // Handle invalid device ID format
            Utils.generateErrorMessage(null, Utils.errorCodes.INVALIDCOMMAND);
        }
    }
    

/**
 * Retrieves a device by its ID and checks if the device name matches the type of the device.
 * @param deviceId The ID of the device.
 * @param deviceName The name/type of the device (e.g., "Light", "Camera").
 * @param smartHome The list of smart devices.
 * @return The SmartDevice with the specified ID and name, or null if not found.
 */
private static SmartDevice getDeviceById(int deviceId, String deviceName, List<SmartDevice> smartHome) {
    for (SmartDevice item : smartHome) {
        // Check if the device ID matches
        if (item.getDeviceId() == deviceId && item.getClass().getSimpleName().equals(deviceName)) {
            

            return item; // Return the matching device
        }
    }
    // Return null if no matching device is found
    // This will trigger an error message in the calling method

    return null;
}

}

/**
 * This enum class shows level of brightness of Light
 */

 enum BrightnessLevel {

    HIGH, MEDIUM, LOW;
    
}



/**
 * Camera represents a smart device class for video recording at given angles.
 */
class Camera extends SmartDevice implements Chargeable {

    /**
     * These variables represent the status of the camera and its angle.
     * The camera can be charging, recording, and have an angle between -60 and 60.
     */
    private final int MAX_CAMERA_ANGLE = 60;
    private final int MIN_CAMERA_ANGLE = -60;
    private boolean charging;
    private boolean recording;
    private int angle;

    /**
     * Constructor for the Camera class.
     * 
     * @param status    The initial status of the camera.
     * @param charging  The charging state of the camera.
     * @param recording The recording state of the camera.
     * @param angle     The angle of the camera.
     */
    public Camera(Status status, boolean charging, boolean recording, int angle) {
        super(status);
        this.charging = charging;
        this.recording = recording;
        this.angle = angle;
    }

    /**
     * Returns the current camera angle.
     * 
     * @return The angle of the camera.
     */
    public int getAngle() {
        return this.angle;
    }

    /**
     * Changes the camera angle if within valid range.
     * 
     * @param angle The new angle to set.
     * @return True if the angle is successfully set, false otherwise.
     */
    public boolean setCameraAngle(int angle) {
        if (angle >= MIN_CAMERA_ANGLE && angle <= MAX_CAMERA_ANGLE) {
            this.angle = angle;
            return true;
        }
        return false;
    }

    /**
     * Starts recording if the camera is not already recording and is ON.
     * 
     * @return True if recording starts successfully, false otherwise.
     */
    public boolean startRecording() {
        if (!this.recording && this.getStatus() == Status.ON) {
            this.recording = true;
            return true;
        }
        return false;
    }

    /**
     * Stops recording if the camera is currently recording.
     * 
     * @return True if recording stops successfully, false otherwise.
     */
    public boolean stopRecording() {
        if (this.recording) {
            this.recording = false;
            return true;
        }
        return false;
    }

    /**
     * Checks if the camera is currently recording.
     * 
     * @return True if the camera is recording, false otherwise.
     */
    public boolean isRecording() {
        return this.recording;
    }

    /**
     * Checks if the camera is currently charging.
     * 
     * @return True if the camera is charging, false otherwise.
     */
    @Override
    public boolean isCharging() {
        return this.charging == true;
    }

    /**
     * Starts charging the camera if it is not already charging.
     * 
     * @return True if charging starts successfully, false otherwise.
     */
    @Override
    public boolean startCharging() {
        if (!this.charging) {
            this.charging = true;
            return true;
        }
        return false;
    }

    /**
     * Stops charging the camera if it is currently charging.
     * 
     * @return True if charging stops successfully, false otherwise.
     */
    @Override
    public boolean stopCharging() {
        if (this.charging) {
            this.charging = false;
            return true;
        }
        return false;
    }

    /**
     * Displays the current status of the camera.
     * 
     * @return A string representation of the camera's status.
     */
    @Override
    public String displayStatus() {
        return "Camera " + this.getDeviceId() + " is " + this.getStatus() +
               ", the angle is " + this.getAngle() +
               ", the charging status is " + this.isCharging() +
               ", and the recording status is " + this.isRecording() + ".";
    }

        /**
     * Turns the device OFF if it is currently ON.
     * @return True if the device was successfully turned OFF, false if it was already OFF.
     */
    @Override
    public boolean turnOff() {
        if (this.getStatus() == Status.OFF) {
            return false;
        } else {
            this.setStatus(Status.OFF);
            this.charging = false;
            return true;
        }
    }
}

/**
 * Chargeable interface describes charging in smart devices
 * no attributes only methods
 */

 interface Chargeable{

    public boolean isCharging();
    public boolean startCharging();
    public boolean stopCharging();

 }

/**
 * Controllable interface describes controlling in smart devices
 * no attributes only methods
 */
 interface Controllable {

    boolean turnOn();
    boolean turnOff();
    boolean isOn();
    
}


/**
 * Heater
 * 
 * This class represents a heater that can be turned on and off and can increase or decrease its temperature.
 * @params temperature : int
 * @params MAX_TEMP : int
 * @params MIN_TEMP : int 
 */

class Heater extends SmartDevice {

    /**
     * These variables represent the status of the heater and its temperature.
     */
    private int temperature;
    static final int MAX_TEMP = 30;
    static final int MIN_TEMP = 15;

    /**
     * This is the constructor for the Heater class.
     * @params status
     * @params temperature
     */

    public Heater(Status status, int temperature){
        super(status);
        this.temperature = temperature;
    }


    /**
     * This method increases the temperature of the heater by 1.
     * @noparams
     */
    public int getTemperature(){
        return this.temperature;
    }


    /**
     * This method increases the temperature of the heater by value of the parameter.
     * @param temperature
     * @return
     * 
     */
    public boolean setTemperature(int temperature){
        if(temperature > MAX_TEMP || temperature < MIN_TEMP){
            return false;
        }
        this.temperature = temperature;
        return true;
    }


    /**
     * This method displays current state of heater
     * @noparams
     */
    @Override
    public String displayStatus(){
        return "Heater " + this.getDeviceId() + " is " + this.getStatus() + " and the temperature is " + this.getTemperature()  + "."; 

    }
    
}

/**
 * Light represents a smart light that can adjust brightness level  and color
 * @param status : Status 
 * @param charging : Boolean
 * @param brightnessLevel : BrightnessLevel
 * @param lightColor : LightColor
 */
class Light extends SmartDevice implements Chargeable {

    /**
     * Class attributes
     */
    private boolean charging;
    private BrightnessLevel brightnessLevel;
    private LightColor lightColor;

    /**
     * Constructor for Light objects
     */
    public Light(Status status, boolean charging, BrightnessLevel brightnessLevel, LightColor lightColor){
        super(status);
        this.charging = charging;
        this.brightnessLevel = brightnessLevel;
        this.lightColor = lightColor;
    }

        /**
         * The method getLightColor returns the Light's color
         */
        public LightColor getLightColor(){
            return this.lightColor;
        }

        /**
         * The method setLightColor obviously returns it
         */
        public void setLightColor(LightColor lightColor){
            this.lightColor = lightColor;
        }

        /**
         * The method getBrightnessLevel obviously returns it
         */
        public BrightnessLevel getBrightnessLevel(){
            return this.brightnessLevel;
        }

         /**
         * The method setBrightnessLevel obviously sets it
         */
        public void setBrightnessLevel(BrightnessLevel brightnessLevel){
             this.brightnessLevel = brightnessLevel;

        }

        @Override
        /**
         * The method isCharging returns the charge state
         */
        public boolean isCharging(){
            return this.charging == true;
        }

        @Override
    /**
     * Starts charging the Light if it is not already charging.
     * 
     * @return True if charging starts successfully, false otherwise.
     */
    public boolean startCharging() {
        if (!this.charging) {
            this.charging = true;
            return true;
        }
        return false;
    }

        @Override
        /**
         * The method stopCharging  activates charging state
         */
        public boolean stopCharging() {
            this.charging = false;
            return true;
        }

         /**
         * The method displayStatus returns current Light status
         */
        @Override
        public String displayStatus(){
                return "Light " + this.getDeviceId() + " is " + this.getStatus() + ", the color is " + this.getLightColor() + ", the charging status is " + this.charging + ", and the brightness level is " + this.brightnessLevel + "."; 
        }

    /**
     * Turns the device OFF if it is currently ON.
     * @return True if the device was successfully turned OFF, false if it was already OFF.
     */
    @Override
    public boolean turnOff() {
        if (this.getStatus() == Status.OFF) {
            return false;
        } else {
            this.setStatus(Status.OFF);
            this.charging = false;
            return true;
        }
    }
            
}

/**
 * contains the states of Light
 */

 enum LightColor {

    WHITE, YELLOW;
    
}



/**
 * This is the abstract class for all smart devices.
 */
abstract class SmartDevice implements Controllable{

    /**
     * The status of the device (ON or OFF).
     */
    private Status status;

    /**
     * The ID of the device.
     */
    private int deviceId;

    /**
     * The number of devices.
     */
    private static int numberOfDevices = 0;

    /**
     * Constructor to initialize the device with a given status.
     * @param status The initial status of the device.
     */
    public SmartDevice(Status status) {
        this.status = status;
        setDeviceId();
        numberOfDevices++;
    }

    /**
     * Abstract method to display the device's status.
     * Must be implemented by all subclasses.
     * @return A string representation of the device's status.
     */
    public abstract String displayStatus();

    /**
     * Retrieves the unique ID of the device.
     * @return The device ID.
     */
    public int getDeviceId() {
        return this.deviceId;
    }

    /**
     * Sets the unique ID of the device.
     * @param deviceId The device ID to set.
     */
    public void setDeviceId() {
        this.deviceId = numberOfDevices;
    }

    /**
     * Retrieves the current status of the device.
     * @return The device's status (ON or OFF).
     */
    public Status getStatus() {
        return this.status;
    }

    /**
     * Sets the current status of the device.
     * @param status The new status to set (ON or OFF).
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * Turns the device OFF if it is currently ON.
     * @return True if the device was successfully turned OFF, false if it was already OFF.
     */
     @Override
     public boolean turnOff() {
        if (!this.checkStatusAccess()) {
            return false;
        } else {
            this.setStatus(Status.OFF);
            return true;
        }
    }

    /**
     * Turns the device ON if it is currently OFF.
     * @return True if the device was successfully turned ON, false if it was already ON.
     */
    @Override
    public boolean turnOn() {
        if (this.checkStatusAccess()) {
            return false;
        } else {
            this.setStatus(Status.ON);
            return true;
        }
    }

    /**
     * Checks if the device is currently ON.
     * @return True if the device is ON, false otherwise.
     */
    @Override
    public boolean isOn() {
        return this.status == Status.ON;
    }

    /**
     * Checks the status access of the device.
     * @return True if the status is ON, false otherwise.
     */
    public boolean checkStatusAccess() {
        if(this.isOn()){
            return true;
        } else return false;
    }

}

enum Status  {
    ON, OFF;
}

class Utils {

    enum successCodes {
        DEVICETURNEDON,
        DEVICETURNEDOFF,
        CHARGINGSTARTED,
        CHARGINGSTOPPED,
        TEMPERATURESET,
        BRIGHTNESSSET,
        COLORSET,
        ANGLESET,
        RECORDINGSTARTED,
        RECORDINGSTOPPED
    }

    enum errorCodes {
        INVALIDCOMMAND,
        DEVICENOTFOUND,
        DEVICENOTCHARGEABLE,
        DEVICENOTHEATER,
        BRIGHTNESSOUTBOUND,
        DEVICENOTLIGHT,
        INVALIDCOLOR,
        DEVICENOTCAMERA,
        DEVICEALREADYOFF,
        DEVICEALREADYON,
        CANNOTCHANGEATTRIBUTE,
        CAMERAOUTOFRANGE,
        CAMERAALREADYRECORDING,
        CAMERANOTRECORDING,
        DEVICEALREADYCHARGING,
        DEVICENOTCHARGING,
        TEMPERATUREOUTOFRANGE
    }

    enum validCommands {
        DISPLAYALLSTATUS,
        TURNONOFF,
        STARTSTOPCHARGING,
        SETTEMPERATURE,
        SETBRIGHTNESS,
        SETCOLOR,
        SETANGLE,
        STARTSTOPRECORDING,
        END
    }

    /**
     * Generates a success message based on the success code.
     * @param device The device for which the operation succeeded.
     * @param successCode The code representing the type of success.
     */
    public static void generateSuccessMessage(SmartDevice device, successCodes successCode) {

        switch (successCode) {

            case DEVICETURNEDON:
                System.out.println(device.getClass().getSimpleName() + " " + device.getDeviceId() + " is on");
                break;

            case DEVICETURNEDOFF:
                System.out.println(device.getClass().getSimpleName() + " " + device.getDeviceId() + " is off");
                break;

            case CHARGINGSTARTED:
                System.out.println(device.getClass().getSimpleName() + " " + device.getDeviceId() + " is charging");
                break;

            case CHARGINGSTOPPED:
                System.out.println(device.getClass().getSimpleName() + " " + device.getDeviceId() + " stopped charging");
                break;

            case TEMPERATURESET:
                // Retrieves temperature from the Heater subclass.
                if (device instanceof Heater) {
                    Heater heater = (Heater) device;
                    System.out.println(device.getClass().getSimpleName() + " " + device.getDeviceId() + " temperature is set to " + heater.getTemperature());
                }
                break;

            case BRIGHTNESSSET:
                // Retrieves brightness from the Light subclass.
                if (device instanceof Light) {
                    Light light = (Light) device;
                    System.out.println(device.getClass().getSimpleName() + " " + device.getDeviceId() + " brightness level is set to " + light.getBrightnessLevel());
                }
                break;

            case COLORSET:
                // Retrieves color from the Light subclass.
                if (device instanceof Light) {
                    Light light = (Light) device;
                    System.out.println(device.getClass().getSimpleName() + " " + device.getDeviceId() + " color is set to " + light.getLightColor());
                }
                break;

            case ANGLESET:
                // Retrieves angle from the Camera subclass.
                if (device instanceof Camera) {
                    Camera camera = (Camera) device;
                    System.out.println(device.getClass().getSimpleName() + " " + device.getDeviceId() + " angle is set to " + camera.getAngle());
                }
                break;

            case RECORDINGSTARTED:
                System.out.println(device.getClass().getSimpleName() + " " + device.getDeviceId() + " started recording");
                break;

            case RECORDINGSTOPPED:
                System.out.println(device.getClass().getSimpleName() + " " + device.getDeviceId() + " stopped recording");
                break;

            default:
                break;
        }
    }

    /**
     * Generates an error message based on the error code.
     * @param device The device for which the error occurred.
     * @param errorCode The code representing the type of error.
     */
    public static void generateErrorMessage(SmartDevice device, errorCodes errorCode) {

        switch (errorCode) {

            case INVALIDCOMMAND:
                System.out.println("Invalid command");
                break;

            case DEVICENOTFOUND:
                System.out.println("The smart device was not found");
                break;

            case DEVICENOTCHARGEABLE:
                System.out.println(device.getClass().getSimpleName() + " " + device.getDeviceId() + " is not chargeable");
                break;

            case DEVICENOTHEATER:
                System.out.println(device.getClass().getSimpleName() + " " + device.getDeviceId() + " is not a heater");
                break;

            case BRIGHTNESSOUTBOUND:
                System.out.println("The brightness can only be one of \"LOW\", \"MEDIUM\", or \"HIGH\"");
                break;

            case DEVICENOTLIGHT:
                System.out.println(device.getClass().getSimpleName() + " " + device.getDeviceId() + " is not a light");
                break;

            case INVALIDCOLOR:
                System.out.println("The light color can only be \"YELLOW\" or \"WHITE\"");
                break;

            case DEVICENOTCAMERA:
                System.out.println(device.getClass().getSimpleName() + " " + device.getDeviceId() + " is not a camera");
                break;

            case DEVICEALREADYOFF:
                System.out.println(device.getClass().getSimpleName() + " " + device.getDeviceId() + " is already off");
                break;

            case DEVICEALREADYON:
                System.out.println(device.getClass().getSimpleName() + " " + device.getDeviceId() + " is already on");
                break;

            case CANNOTCHANGEATTRIBUTE:
                System.out.println("You can't change the status of the " + device.getClass().getSimpleName() + " " + device.getDeviceId() + " while it is off");
                break;

            case CAMERAOUTOFRANGE:
                System.out.println(device.getClass().getSimpleName() + " " + device.getDeviceId() + " angle should be in the range [-60, 60]");
                break;

            case CAMERAALREADYRECORDING:
                System.out.println(device.getClass().getSimpleName() + " " + device.getDeviceId() + " is already recording");
                break;

            case CAMERANOTRECORDING:
                System.out.println(device.getClass().getSimpleName() + " " + device.getDeviceId() + " is not recording");
                break;

            case DEVICEALREADYCHARGING:
                System.out.println(device.getClass().getSimpleName() + " " + device.getDeviceId() + " is already charging");
                break;

            case DEVICENOTCHARGING:
                System.out.println(device.getClass().getSimpleName() + " " + device.getDeviceId() + " is not charging");
                break;

            case TEMPERATUREOUTOFRANGE:
                System.out.println("Heater " + device.getDeviceId() + " temperature should be in the range [15, 30]");
                break;

            default:
                break;
        }
    }
}
