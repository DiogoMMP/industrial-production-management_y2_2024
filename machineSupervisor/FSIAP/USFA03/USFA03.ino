#include "DHT.h"
#include <arduino-timer.h>

#define DHTPIN 16
#define DHTTYPE DHT11

#define LED_TEMP_PIN 4       // LED connected to GP4
#define LED_HUMIDITY_PIN 10  // LED connected to GP10

#define FAN_EXHAUST_PIN 6    // Exhaust fan connected to GP6
#define FAN_VENTILATION_PIN 7 // Ventilation fan connected to GP7
#define MQ2_PIN A0           // MQ-2 sensor connected to analog pin A0

#define TEMP_CHECK_PERIOD 2000
#define HUMIDITY_CHECK_PERIOD 2000
#define GAS_CHECK_PERIOD 2000
#define RATE_CHECK_PERIOD 1000

DHT dht(DHTPIN, DHTTYPE);
auto timer = timer_create_default(); // Create timer with default settings

float initialTemperature = -1000;  
float initialHumidity = -1000;     
int initialGasValue = -1;          

float lastTemp = -1000;  
float lastHumidity = -1000;  
int lastGasValue = -1;      

unsigned long lastTempTime = 0;
unsigned long lastHumidityTime = 0;
unsigned long lastGasTime = 0;

bool temperatureLedOn = false; 
bool humidityLedOn = false; 

// Function to activate a fan for a specified duration
void activateFan(int pin, unsigned long duration) {
    digitalWrite(pin, HIGH); // Turn on the fan
    delay(duration);         // Wait for the specified duration
    digitalWrite(pin, LOW);  // Turn off the fan
}

// Function to check for sudden changes exceeding 30% per minute
bool check_rate_of_change(void *) {
    unsigned long currentTime = millis();

    // Temperature rate of change
    float currentTemp = dht.readTemperature();
    if (!isnan(currentTemp) && lastTemp != -1000) {
        float tempRate = abs(currentTemp - lastTemp) / ((currentTime - lastTempTime) / 60000.0);
        if (tempRate > (initialTemperature * 0.3)) {
            Serial.println("Sudden temperature change detected! Activating both fans.");
            activateFan(FAN_EXHAUST_PIN, 10000);
            activateFan(FAN_VENTILATION_PIN, 10000);
        }
    }
    lastTemp = currentTemp;
    lastTempTime = currentTime;

    // Humidity rate of change
    float currentHumidity = dht.readHumidity();
    if (!isnan(currentHumidity) && lastHumidity != -1000) {
        float humidityRate = abs(currentHumidity - lastHumidity) / ((currentTime - lastHumidityTime) / 60000.0);
        if (humidityRate > (initialHumidity * 0.3)) {
            Serial.println("Sudden humidity change detected! Activating both fans.");
            activateFan(FAN_EXHAUST_PIN, 10000);
            activateFan(FAN_VENTILATION_PIN, 10000);
        }
    }
    lastHumidity = currentHumidity;
    lastHumidityTime = currentTime;

    // Gas rate of change
    int gasValue = analogRead(MQ2_PIN);
    if (lastGasValue != -1) {
        float gasRate = abs(gasValue - lastGasValue) / ((currentTime - lastGasTime) / 60000.0);
        if (gasRate > (initialGasValue * 0.3)) {
            Serial.println("Sudden gas change detected! Activating both fans.");
            activateFan(FAN_EXHAUST_PIN, 10000);
            activateFan(FAN_VENTILATION_PIN, 10000);
        }
    }
    lastGasValue = gasValue;
    lastGasTime = currentTime;

    return true;
}

void setup() {
    pinMode(LED_TEMP_PIN, OUTPUT);       
    pinMode(LED_HUMIDITY_PIN, OUTPUT);   
    pinMode(FAN_EXHAUST_PIN, OUTPUT);    
    pinMode(FAN_VENTILATION_PIN, OUTPUT); 
    Serial.begin(9600);                  
    dht.begin();                         

    // Schedule tasks for periodic checks
    timer.every(TEMP_CHECK_PERIOD, check_temperature);
    timer.every(HUMIDITY_CHECK_PERIOD, check_humidity);
    timer.every(GAS_CHECK_PERIOD, check_gas);
    timer.every(RATE_CHECK_PERIOD, check_rate_of_change); // Check for rate of change
}

void loop() {
    timer.tick(); // Process scheduled tasks
}
