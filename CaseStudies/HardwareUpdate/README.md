# Managing Software Obsolescence in APIs: Case Studies - HardwareUpdate


### Description
This case study simulates a scenario in which a hardware infrastructure (e.g., embedded system) undergoes a partial update of some of its components. During this update, hardware components within the infrastructure are replaced with functionally-equivalent components with presumably more capabilities and improved features (e.g., lower power consumption, better accuracy, enhanced functionality).

We developed this case study using a simple hardware infrastructure that comprises an Arduino Uno microprocessor, a set of temperature sensors and a screen. To simulate the update scenario, we replace one of the sensors and the screen with a pair of functionally-equivalent sensor and screen.

### Instructions

* Clone  repository (skip this step if you have already cloned the repo)
<br/>Using terminal type ```git clone https://github.com/gerasimou/SoftwareObsolescence.git```

* Launch Eclipse C/C++ IDE

* Import Eclipse project
<br/> File > Import > Existing Project into Workspace > Next, and then browse to the location where HarwareUpdate is located

* After generating the abstraction layer (adapter) and populating the layer with suitable code, to start exercising the new sensor and screen, you can upload the code to an Arduino using a suitable Arduino IDE (https://www.arduino.cc/en/main/software)
---
<br/>

To instal the Eclipse API Modernisation plugin, please read the instructions on the
[main repository page](https://github.com/gerasimou/SoftwareObsolescence#managing-software-obsolescence-in-apis).

***
<br/>Should you have any comments, suggestions or questions, please email us at simos.gerasimou-at-york.ac.uk
