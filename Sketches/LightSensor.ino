#include <SPI.h>
#include <MySensor.h>  

#define NODE_ID 1
#define CHILD_ID 0
#define LIGHT_SENSOR_ANALOG_PIN 0

unsigned long SLEEP_TIME = 10000; // Sleep time between reads (in milliseconds)

MySensor gw;
MyMessage msg(CHILD_ID, V_LIGHT_LEVEL);
int lastLightLevel;

void setup()  
{ 
  gw.begin(NULL, NODE_ID;

  // Send the sketch version information to the gateway and Controller
  gw.sendSketchInfo("Light Sensor", "1.0");

  // Register all sensors to gateway (they will be created as child devices)
  gw.present(CHILD_ID, S_LIGHT_LEVEL);
}

void loop()      
{     
  int lightLevel = (1023-analogRead(LIGHT_SENSOR_ANALOG_PIN))/10.23; 
  Serial.println(lightLevel);
  if (lightLevel != lastLightLevel) {
      gw.send(msg.set(lightLevel));
      lastLightLevel = lightLevel;
  }
  gw.sleep(SLEEP_TIME);
}



