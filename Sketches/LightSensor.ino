#include <SPI.h>
#include <MySensor.h>  

#define NODE_ID 1
#define CHILD_ID 0
#define LIGHT_SENSOR_ANALOG_PIN 0

unsigned long SLEEP_TIME = 10000; // Sleep time between reads (in milliseconds)

MySensor ms;
MyMessage msg(CHILD_ID, V_LIGHT_LEVEL);
int lastLightLevel;

void setup()  
{ 
  ms.begin(NULL, NODE_ID;

  // Send the sketch version information to the gateway and Controller
  ms.sendSketchInfo("Light Sensor", "1.0");

  // Register all sensors to gateway (they will be created as child devices)
  ms.present(CHILD_ID, S_LIGHT_LEVEL);
}

void loop()      
{     
  int lightLevel = (1023-analogRead(LIGHT_SENSOR_ANALOG_PIN))/10.23; 
  Serial.println(lightLevel);
  if (lightLevel != lastLightLevel) {
      ms.send(msg.set(lightLevel));
      lastLightLevel = lightLevel;
  }
  ms.sleep(SLEEP_TIME);
}
