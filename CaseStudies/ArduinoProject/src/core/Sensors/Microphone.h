// Only modify this file to include
// - function definitions (prototypes)
// - include files
// - extern variable definitions
// In the appropriate section

#ifndef _Microphone_H_
#define _Microphone_H_
#include "Arduino.h"
//add your includes for the project Microphone here

// LED
//#define LEDPIN 14

#define MICROPHONE_ANALOG_PIN 2

const int sampleWindow = 250; // Sample window width in mS (250 mS = 4Hz)
//unsigned int knock;


bool runMicroPhone();


//add your function definitions for the project Microphone here




//Do not add code below this line
#endif /* _Microphone_H_ */
