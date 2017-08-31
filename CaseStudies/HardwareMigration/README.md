# Managing Software Obsolescence in APIs: Case Studies - HardwareMigration

### Description
This case study simulates a microprocessor hardware migration scenario. In this scenario, an entire hardware infrastructure, or parts of it (except the microprocessor), needs to be migrated to another microprocessor.

The objective in this case study is to migrate a hardware infrastructure from an Arduino microprocessor to a Raspberry Pi microprocessor.

We developed this case study using a simple hardware infrastructure that comprises an Arduino Uno microprocessor, a set of temperature sensors and a screen. To simulate the hardware migration scenario,
we migrate this infrastructure (minus the Arduino) to a Raspberry Pi microprocessor.

Despite that the Raspberry Pi is compatible with the migrated components, several changes should happen to the software so that the Pi can start using the components.


### Instructions

* Clone  repository (skip this step if you have already cloned the repo)
<br/>Using terminal type ```git clone https://github.com/gerasimou/SoftwareObsolescence.git```

* Launch Eclipse C/C++ IDE

* Import Eclipse project
<br/> File > Import > Existing Project into Workspace > Next, and then browse to the location where HardwareMigration is located

* After generating the abstraction layer (adapter) and populating the layer with suitable code, to start exercising the infrastructure on RPi, you should upload the code to RPi and compile it.
---
<br/>

To instal the Eclipse API Modernisation plugin, please read the instructions on the
[main repository page](https://github.com/gerasimou/SoftwareObsolescence#managing-software-obsolescence-in-apis).

***
<br/>Should you have any comments, suggestions or questions, please email us at simos.gerasimou-at-york.ac.uk
