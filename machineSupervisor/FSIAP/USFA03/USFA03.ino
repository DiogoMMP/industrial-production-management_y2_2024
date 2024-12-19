#include "DHT.h"
#include <arduino-timer.h>

#define DHTPIN 16
#define DHTTYPE DHT11

#define LED_TEMP_PIN 0          // LED for temperature warning
#define LED_HUMIDITY_PIN 1      // LED for humidity warning
#define LED_FAN_EXHAUST_PIN 3    // LED simulating exhaust fan
#define LED_FAN_VENTILATION_PIN 4 // LED simulating ventilation fan
#define MQ2_PIN A0               // MQ-2 gas sensor analog pin

#define TEMP_CHECK_PERIOD 2000
#define HUMIDITY_CHECK_PERIOD 2000
#define GAS_CHECK_PERIOD 2000
#define RATE_CHECK_PERIOD 1000

DHT dht(DHTPIN, DHTTYPE);
auto timer = timer_create_default(); // Timer for periodic tasks

float initialTemperature = NAN;  
float initialHumidity = NAN;     
int initialGasValue = -1;        

float lastTemp = NAN;  
float lastHumidity = NAN;  
int lastGasValue = -1;      

// Function to activate a fan (LED) for a specified duration
void activateFanSequence(int firstPin, unsigned long firstDuration, int secondPin, unsigned long secondDuration) {
    digitalWrite(firstPin, HIGH);  // Turn on the first fan (LED)
    delay(firstDuration);          // Wait for the first duration
    digitalWrite(firstPin, LOW);   // Turn off the first fan (LED)
    digitalWrite(secondPin, HIGH); // Turn on the second fan (LED)
    delay(secondDuration);         // Wait for the second duration
    digitalWrite(secondPin, LOW);  // Turn off the second fan (LED)
}

// Function to activate both fans simultaneously
void activateBothFans(unsigned long duration) {
    digitalWrite(LED_FAN_EXHAUST_PIN, HIGH);  // Turn on exhaust fan
    digitalWrite(LED_FAN_VENTILATION_PIN, HIGH); // Turn on ventilation fan
    delay(duration); // Wait for the duration
    digitalWrite(LED_FAN_EXHAUST_PIN, LOW);   // Turn off exhaust fan
    digitalWrite(LED_FAN_VENTILATION_PIN, LOW); // Turn off ventilation fan
}

// Periodic task to check temperature
bool check_temperature(void *) {
    float currentTemp = dht.readTemperature();
    if (!isnan(currentTemp)) {
        Serial.print("Temperature: ");
        Serial.print(currentTemp);
        Serial.println(" °C");
        if (isnan(initialTemperature)) {
            initialTemperature = currentTemp;
        }
        if (currentTemp > initialTemperature + 5) { // 5ºC above initial reading
            Serial.println("Temperature threshold exceeded! Activating exhaust and ventilation fans.");
            activateFanSequence(LED_FAN_EXHAUST_PIN, 5000, LED_FAN_VENTILATION_PIN, 5000);
        }
        lastTemp = currentTemp;
    } else {
        Serial.println("Failed to read temperature.");
    }
    return true; // Repeat task
}

// Periodic task to check humidity
bool check_humidity(void *) {
    float currentHumidity = dht.readHumidity();
    if (!isnan(currentHumidity)) {
        Serial.print("Humidity: ");
        Serial.print(currentHumidity);
        Serial.println(" %");
        if (isnan(initialHumidity)) {
            initialHumidity = currentHumidity;
        }
        if (currentHumidity > initialHumidity + 5) { // 5% above initial reading
            Serial.println("Humidity threshold exceeded! Activating ventilation and exhaust fans.");
            activateFanSequence(LED_FAN_VENTILATION_PIN, 10000, LED_FAN_EXHAUST_PIN, 10000);
        }
        lastHumidity = currentHumidity;
    } else {
        Serial.println("Failed to read humidity.");
    }
    return true; // Repeat task
}

// Periodic task to check gas levels
bool check_gas(void *) {
    int gasValue = analogRead(MQ2_PIN);
    if (gasValue > 0) {
        Serial.print("Gas Value: ");
        Serial.println(gasValue);
        
        if (initialGasValue == -1) {
            initialGasValue = gasValue;
        }
        
        // 2% threshold logic adjusted to check for a larger change in gas value
        if (gasValue > initialGasValue + (initialGasValue * 0.05)) { // 5% above initial reading
            Serial.println("Gas threshold exceeded! Activating both fans.");
            activateBothFans(10000); // Activate both fans for 10 seconds
        }
        lastGasValue = gasValue;
    } else {
        Serial.println("Failed to read gas value.");
    }
    return true; // Repeat task
}

// Function to check for sudden sensor changes (> 30% per minute)
bool check_rate_of_change(void *) {
    unsigned long currentTime = millis();

    // Temperature rate of change
    float currentTemp = dht.readTemperature();
    if (!isnan(currentTemp) && !isnan(lastTemp)) {
        float tempRate = abs(currentTemp - lastTemp) / ((currentTime - lastTemp) / 60000.0);
        if (tempRate > (initialTemperature * 0.3)) {
            Serial.println("Sudden temperature change detected! Activating both fans.");
            activateBothFans(10000);
        }
    }
    lastTemp = currentTemp;

    // Humidity rate of change
    float currentHumidity = dht.readHumidity();
    if (!isnan(currentHumidity) && !isnan(lastHumidity)) {
        float humidityRate = abs(currentHumidity - lastHumidity) / ((currentTime - lastHumidity) / 60000.0);
        if (humidityRate > (initialHumidity * 0.3)) {
            Serial.println("Sudden humidity change detected! Activating both fans.");
            activateBothFans(10000);
        }
    }
    lastHumidity = currentHumidity;

    // Gas rate of change
    int gasValue = analogRead(MQ2_PIN);
    if (lastGasValue != -1) {
        float gasRate = abs(gasValue - lastGasValue) / ((currentTime - lastGasValue) / 60000.0);
        if (gasRate > (initialGasValue * 0.3)) {
            Serial.println("Sudden gas change detected! Activating both fans.");
            activateBothFans(10000);
        }
    }
    lastGasValue = gasValue;

    return true;
}

void setup() {
    pinMode(LED_TEMP_PIN, OUTPUT);
    pinMode(LED_HUMIDITY_PIN, OUTPUT);
    pinMode(LED_FAN_EXHAUST_PIN, OUTPUT);     // LED simulating exhaust fan
    pinMode(LED_FAN_VENTILATION_PIN, OUTPUT); // LED simulating ventilation fan
    Serial.begin(9600);
    dht.begin();

    // Schedule periodic tasks
    timer.every(TEMP_CHECK_PERIOD, check_temperature);
    timer.every(HUMIDITY_CHECK_PERIOD, check_humidity);
    timer.every(GAS_CHECK_PERIOD, check_gas);
    timer.every(RATE_CHECK_PERIOD, check_rate_of_change);
}

void loop() {
    timer.tick(); // Process scheduled tasks
}
