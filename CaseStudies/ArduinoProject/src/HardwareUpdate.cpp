// Do not remove the include below
#include "HardwareUpdate.h"

#include <stdio.h>

//Libraries
#include <DHT.h>
#include <Wire.h>
#include <LiquidCrystal.h>
#include <Microphone.h>
#include "PiezoBuzzer.h"



// Initialize DHT sensor for normal 16mhz Arduino
DHT dht(DHT_PIN, DHT_TYPE);


// Initialize the library with the numbers of the interface pins
LiquidCrystal lcd(LCD_RS, LCD_ENABLE, LCD_D0, LCD_D1, LCD_D2, LCD_D3);


//Variables
//int chk;
float hum; 	 				//Stores humidity value
float temp; 				//Stores temperature value

char output[5];				// char array to print to the screen
float temperature[3];
float humidity[3];


float getTMP102Temperature();


void setup()
{
  Serial.begin(9600);

  //led
  pinMode(LED_PIN, OUTPUT);  // Set digital pin 2 -> output

  //init DHT
  dht.begin();


  //init  LCD: set up  number of columns and rows:
  lcd.begin(16, 2);


  //print messages
  lcd.print("Waiting");
  Serial.println("Waiting!");
}


void runDHT22()
{
	//Read data and store it to variables hum and temp
	hum		= dht.readHumidity();
    temp	= dht.readTemperature();

    temperature[0]  = temp;
    humidity[0]		= hum;

    Serial.print("\tDHT\tT: ");
	Serial.print(temp);
	Serial.print("\tH: ");
	Serial.println(hum);
}

void runTMP102(){
	float temp = getTMP102Temperature();

	temperature[1] = temp;

	Serial.print("\tTMP\tT: ");
	Serial.println(temp);
}


float getTMP102Temperature(){
  Wire.requestFrom(TMP102_I2CADDR,2);

  byte MSB = Wire.read();
  byte LSB = Wire.read();

  //it's a 12bit int, using two's compliment for negative
  int TemperatureSum = ((MSB << 8) | LSB) >> 4;

  float celsius = TemperatureSum*0.0625;
  return celsius;
}


void showLCD(){

	lcd.setCursor(0, 0);
    lcd.print(millis() / 1000);   // print the number of seconds since reset:

    //Print temp and humidity values to lcd
    // set the cursor to column 0, line 0
	lcd.setCursor(6, 0);
    lcd.print(temperature[0]);

    lcd.setCursor(0, 1);
    lcd.print(temperature[1]);

	lcd.setCursor(6, 1);
	lcd.print(humidity[0]);

}


//int time window
unsigned long timeWindow = 10000;
unsigned long previous = -timeWindow*2;
unsigned long now = millis();

void loop(){
	//every 10s
	now      = millis();
	if (now - previous > timeWindow){
		digitalWrite(LED_PIN, HIGH);      // Pin 12 = 5 V, LED emits light
		//wait until a sound is captured
		while (!runMicroPhone());

		//play tune
		digitalWrite(LED_PIN, LOW);      // Pin 12 = 5 V, LED emits light
		delay(100);
		lcd.clear();
		playTune();

		previous = millis();
	}

	digitalWrite(LED_PIN, LOW);      // Pin 12 = 5 V, LED emits light
	delay(1000);
	digitalWrite(LED_PIN, HIGH);      // Pin 12 = 5 V, LED emits light
	Serial.println(millis()/1000);

	//run sensors
	runDHT22();
	runTMP102();

	//show messages on LCDs
	showLCD();
	delay(100);
}


void test(){

}
