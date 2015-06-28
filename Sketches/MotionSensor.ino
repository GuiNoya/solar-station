#include <MySensor.h>  
#include <SPI.h>

unsigned long SLEEP_TIME = 120000; // Sleep time between reports (in milliseconds)
#define DIGITAL_INPUT_SENSOR 4   // The digital input you attached your motion sensor.  (Only 2 and 3 generates interrupt!)
#define INTERRUPT DIGITAL_INPUT_SENSOR-2 // Usually the interrupt = pin -2 (on uno/nano anyway)
#define CHILD_ID 1
#define NODE_ID 15

MySensor ms;
// Initialize motion message
MyMessage msg(CHILD_ID, V_TRIPPED);

void setup()  
{  
  ms.begin(NULL, NODE_ID);

  // Send the sketch version information to the gateway and Controller
  ms.sendSketchInfo("Motion Sensor", "1.0");

  pinMode(DIGITAL_INPUT_SENSOR, INPUT);      // sets the motion sensor digital pin as input
  // Register all sensors to ms (they will be created as child devices)
  ms.present(CHILD_ID, S_MOTION);
  
}

void loop()     
{     
  // Read digital motion value
  boolean tripped = digitalRead(DIGITAL_INPUT_SENSOR) == HIGH; 
        
  Serial.println(tripped);
  ms.send(msg.set(tripped?"1":"0"));  // Send tripped value to ms
 
  // Sleep until interrupt comes in on motion sensor. Send update every two minute. 
  ms.sleep(INTERRUPT,CHANGE, SLEEP_TIME);
}


