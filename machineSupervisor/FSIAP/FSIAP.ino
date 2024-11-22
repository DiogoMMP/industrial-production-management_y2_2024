#include "DHT.h"
#include <arduino-timer.h>

#define DHTPIN 16
#define DHTTYPE DHT11

#define LED_TEMP_PIN 4    // LED ligado ao GP4
#define LED_HUMIDITY_PIN 10 // LED ligado ao GP10

#define TOGGLE_LED_PERIOD 1000 // milliseconds
#define TEMP_CHECK_PERIOD 2000
#define HUMIDITY_CHECK_PERIOD 2000
#define DATA_LENGTH 100

DHT dht(DHTPIN, DHTTYPE);
auto timer = timer_create_default(); // Create a timer with default settings

float initialTemperature = -1000;  // Value to store the initial temperature
float initialHumidity = -1000;     // Value to store the initial humidity

bool temperatureLedOn = false; // Track if temperature LED is already on
bool humidityLedOn = false;    // Track if humidity LED is already on

enum sensor_type { TEMPERATURE, HUMIDITY };
enum sensor_unit { CELSIUS, PERCENT };

const char *sensor_types[] = { "TEMPERATURE", "HUMIDITY" };
const char *sensor_units[] = { "C", "%" };

void send_data(int id, sensor_type t, float r, sensor_unit u, unsigned long m) {
    char buffer[DATA_LENGTH];
    snprintf(buffer, DATA_LENGTH - 1,
             "sensor_id:%d#type:%s#value:%.2f#unit:%s#time:%lu",
             id, sensor_types[t], r, sensor_units[u], m);
    Serial.println(buffer);
}

bool check_temperature(void *) {
    float currentTemp = dht.readTemperature();
    if (!isnan(currentTemp)) {
        // Enviar dados do sensor para o Serial
        send_data(1, TEMPERATURE, currentTemp, CELSIUS, millis());

        // Set the initial temperature if not yet initialized
        if (initialTemperature == -1000) {
            initialTemperature = currentTemp;
        }

        // Check if the temperature increased by 5 or more
        if (currentTemp >= initialTemperature + 5) {
            if (!temperatureLedOn) { // Only print "LED ON" when turning on
                Serial.println("Temperature LED ON (increased 5ºC) ");
                temperatureLedOn = true;
            }
            digitalWrite(LED_TEMP_PIN, HIGH); // Turn on temperature LED
        } else if (currentTemp < initialTemperature + 5) {
            temperatureLedOn = false; // Reset the flag when below threshold
            Serial.println("Temperature LED OFF");
            digitalWrite(LED_TEMP_PIN, LOW); // Turn off temperature LED
        }
    }
    return true; // Keep the timer active
}

bool check_humidity(void *) {
    float currentHumidity = dht.readHumidity();
    if (!isnan(currentHumidity)) {
        // Enviar dados do sensor para o Serial
        send_data(2, HUMIDITY, currentHumidity, PERCENT, millis());

        // Set the initial humidity if not yet initialized
        if (initialHumidity == -1000) {
            initialHumidity = currentHumidity;
        }

        // Check if the humidity increased by 5% or more
        if (currentHumidity >= initialHumidity + 5) {
            if (!humidityLedOn) { // Only print "LED ON" when turning on
                Serial.println("Humidity LED ON (increased 5%)");
                humidityLedOn = true;
            }
            digitalWrite(LED_HUMIDITY_PIN, HIGH); // Turn on humidity LED
        } else if (currentHumidity < initialHumidity + 5) {
            humidityLedOn = false; // Reset the flag when below threshold
            Serial.println("Humidity LED OFF");
            digitalWrite(LED_HUMIDITY_PIN, LOW); // Turn off humidity LED
        }
    }
    return true; // Keep the timer active
}

void setup() {
    pinMode(LED_TEMP_PIN, OUTPUT);       // Configure temperature LED pin as output
    pinMode(LED_HUMIDITY_PIN, OUTPUT);   // Configure humidity LED pin as output
    Serial.begin(9600);
    dht.begin();

    // Set up periodic tasks
    timer.every(TEMP_CHECK_PERIOD, check_temperature);
    timer.every(HUMIDITY_CHECK_PERIOD, check_humidity);
}

void loop() {
    timer.tick(); // Process scheduled tasks
}
