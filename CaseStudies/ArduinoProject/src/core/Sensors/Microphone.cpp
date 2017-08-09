/*
 * Microphone.cpp
 *
 *  Created on: 25 Mar 2017
 *      Author: sgerasimou
 */

// Do not remove the include below
#include "Microphone.h"

bool runMicroPhone() {
	unsigned long start = millis();  // Start of sample window
	unsigned int peakToPeak = 0;   // peak-to-peak level

	unsigned int signalMax = 0;
	unsigned int signalMin = 1024;

	// collect data for 250 miliseconds
	while (millis() - start < sampleWindow) {
		unsigned int knock = analogRead(MICROPHONE_ANALOG_PIN);
		if (knock < 1024){ //This is the max of the 10-bit ADC so this loop will include all readings
			if (knock > signalMax) {
				signalMax = knock;  // save just the max levels
			} else if (knock < signalMin) {
				signalMin = knock;  // save just the min levels
			}
		}
	}
	peakToPeak = signalMax - signalMin;  // max - min = peak-peak amplitude
	double volts = (peakToPeak * 3.3) / 1024;  // convert to volts

	Serial.println(volts);
	if (volts >= 1.0) {
		//turn on LED
//		digitalWrite(LEDPIN, HIGH);
//		delay(500);
//		Serial.println("Knock Knock");
		return true;
	} else {
		//turn LED off
//		digitalWrite(LEDPIN, LOW);
		return false;
	}
}

