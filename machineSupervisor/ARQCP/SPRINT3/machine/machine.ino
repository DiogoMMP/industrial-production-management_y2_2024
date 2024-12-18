#include "DHT.h" // Library for DHT sensor

// Define pins for binary LEDs (5 external LEDs for binary representation)
#define LED_BIN_0 0  // Binary LED (bit 0)
#define LED_BIN_1 1  // Binary LED (bit 1)
#define LED_BIN_2 2 // Binary LED (bit 2)
#define LED_BIN_3 3 // Binary LED (bit 3)
#define LED_BIN_4 4 // Binary LED (bit 4)

// DHT sensor setup
#define DHT_PIN 16       // GPIO pin for DHT sensor
#define DHT_TYPE DHT11   // Use DHT11 or DHT22 based on your sensor
DHT dht(DHT_PIN, DHT_TYPE); // Initialize the DHT sensor

String machineState = "OFF"; // Initial state of the machine

// Function to update the built-in LED for machine state
void update_machine_state(String state) {
    if (state == "OFF") {
        digitalWrite(LED_BUILTIN, LOW); // Turn OFF LED
        Serial.println("Machine OFF.");
    } else if (state == "ON") {
        digitalWrite(LED_BUILTIN, HIGH); // Turn ON LED
        Serial.println("Machine ON.");
    }
}

// Function to control binary LEDs
void update_leds(int bits[]) {
    digitalWrite(LED_BIN_0, bits[0]);
    digitalWrite(LED_BIN_1, bits[1]);
    digitalWrite(LED_BIN_2, bits[2]);
    digitalWrite(LED_BIN_3, bits[3]);
    digitalWrite(LED_BIN_4, bits[4]);

    // Log the LED states
    Serial.print("LED states: ");
    for (int i = 0; i < 5; i++) {
        Serial.print(bits[i]);
        if (i < 4) Serial.print(",");
    }
    Serial.println();
}

// Function to read and send temperature and humidity
void send_temp_humidity() {
    float temp = dht.readTemperature(); // Read temperature in Celsius
    float hum = dht.readHumidity();    // Read humidity in percentage

    // Check if reading is valid
    if (isnan(temp) || isnan(hum)) {
        Serial.println("Error reading temperature or humidity.");
        return;
    }

    // Format and send the data
    String data = "TEMP&unit:celsius&value:" + String(temp, 1) +
                  "#HUM&unit:percentage&value:" + String(hum, 1);
    Serial.println(data);
}

// Function to make the built-in LED blink while in OP state
void blink_builtin_led_while_op(int interval_ms) {
    while (machineState == "OP") {
        digitalWrite(LED_BUILTIN, HIGH);
        delay(interval_ms);               
        digitalWrite(LED_BUILTIN, LOW);   
        delay(interval_ms);               

        if (Serial.available() > 0) {
            String newCommand = Serial.readStringUntil('\n');
            newCommand.trim(); 
            int commaIndex = newCommand.indexOf(',');
            if (commaIndex > 0) {
                String newState = newCommand.substring(0, commaIndex);
                if (newState != "OP") {
                    machineState = newState;
                    update_machine_state(machineState);
                    break;
                }
            }
        }
    }
}

void wait_2_seconds_turn_off_leds(){
  // Wait 2 seconds and turn off LEDs
  delay(2000);
  digitalWrite(LED_BIN_0, LOW);
  digitalWrite(LED_BIN_1, LOW);
  digitalWrite(LED_BIN_2, LOW);
  digitalWrite(LED_BIN_3, LOW);
  digitalWrite(LED_BIN_4, LOW);
}

// Arduino setup function (runs once)
void setup() {
    // Initialize serial communication
    Serial.begin(9600);

    // Initialize binary LEDs
    pinMode(LED_BIN_0, OUTPUT);
    pinMode(LED_BIN_1, OUTPUT);
    pinMode(LED_BIN_2, OUTPUT);
    pinMode(LED_BIN_3, OUTPUT);
    pinMode(LED_BIN_4, OUTPUT);

    // Initialize built-in LED for machine state
    pinMode(LED_BUILTIN, OUTPUT);

    // Turn off all LEDs initially
    digitalWrite(LED_BIN_0, LOW);
    digitalWrite(LED_BIN_1, LOW);
    digitalWrite(LED_BIN_2, LOW);
    digitalWrite(LED_BIN_3, LOW);
    digitalWrite(LED_BIN_4, LOW);
    digitalWrite(LED_BUILTIN, LOW);

    // Initialize DHT sensor
    dht.begin();
    Serial.println("Machine initialized and ready for commands.");
}

// Arduino loop function (runs continuously)
void loop() {
    // Check if there is data available on the serial input
    if (Serial.available() > 0) {
        String command = Serial.readStringUntil('\n'); // Read command
        command.trim(); // Remove trailing spaces or newline characters

        // Parse the command (new format: "ON,1,1,1,1,1")
        int commaIndex = command.indexOf(',');

        if (commaIndex > 0) {
            String state = command.substring(0, commaIndex); // Extract STATE (ON/OFF/OP)

            // Extract binary bits
            int bits[5] = {0, 0, 0, 0, 0};
            int currentIndex = commaIndex + 1;
            for (int i = 0; i < 5; i++) {
                int nextComma = command.indexOf(',', currentIndex);
                if (nextComma == -1) nextComma = command.length();
                bits[i] = command.substring(currentIndex, nextComma).toInt();
                currentIndex = nextComma + 1;
            }

            // Handle OP command
            if (state == "OP") {
                machineState = "OP";
                Serial.println("Machine OP.");
                update_machine_state(machineState);
                update_leds(bits);
                wait_2_seconds_turn_off_leds();
                send_temp_humidity();
                blink_builtin_led_while_op(1000);  // Blinks built-in LED while in OP state
            } else {
                // Update machine state and LEDs
                machineState = state;
                update_machine_state(machineState);
                update_leds(bits);
            }

            // Send temperature and humidity
            send_temp_humidity();     

            wait_2_seconds_turn_off_leds();
            
        } else {
            Serial.println("Invalid command format.");
        }
    }
}
