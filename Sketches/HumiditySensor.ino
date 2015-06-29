#include <SPI.h>
#include <MySensor.h>  
#include <DHT.h>  

#define NODE_ID 3
#define CHILD_ID_HUM 0
#define CHILD_ID_TEMP 1
#define HUMIDITY_SENSOR_DIGITAL_PIN 4
unsigned long SLEEP_TIME = 30000; // Sleep time between reads (in milliseconds)

MySensor ms;
DHT dht;
float lastTemp;
float lastHum;
boolean metric = true; 
MyMessage msgHum(CHILD_ID_HUM, V_HUM);
MyMessage msgTemp(CHILD_ID_TEMP, V_TEMP);

void setup()  
{ 
  ms.begin(NULL, NODE_ID);
  dht.setup(HUMIDITY_SENSOR_DIGITAL_PIN); 

  // Send the Sketch Version Information to the Gateway
  ms.sendSketchInfo("Humidity", "1.0");

  // Register all sensors to ms (they will be created as child devices)
  ms.present(CHILD_ID_HUM, S_HUM);
  ms.present(CHILD_ID_TEMP, S_TEMP);
  
  metric = ms.getConfig().isMetric;
}

void loop()      
{  
  delay(dht.getMinimumSamplingPeriod());

  float temperature = dht.getTemperature();
  if (isnan(temperature)) {
      Serial.println("Failed reading temperature from DHT");
  } else if (temperature != lastTemp) {
    lastTemp = temperature;
    if (!metric) {
      temperature = dht.toFahrenheit(temperature);
    }
    ms.send(msgTemp.set(temperature, 1));
    Serial.print("T: ");
    Serial.println(temperature);
  }
  
  float humidity = dht.getHumidity();
  if (isnan(humidity)) {
      Serial.println("Failed reading humidity from DHT");
  } else if (humidity != lastHum) {
      lastHum = humidity;
      ms.send(msgHum.set(humidity, 1));
      Serial.print("H: ");
      Serial.println(humidity);
  }

  ms.sleep(SLEEP_TIME); //sleep a bit
}
