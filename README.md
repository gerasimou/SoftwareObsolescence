# Managing Software Obsolescence in APIs

An Eclipse plugin for semi-automated software modernisation  to address the problem of software obsolescence in APIs.

### Prerequisites
---
1. Eclipse IDE for C/C++
   * Download the application from [Eclipse download site](https://www.eclipse.org/downloads/eclipse-packages/)
<br/><br/>
2. Java JDK 1.8 (or higher)
  *  Follow these instructions for [Linux](https://www.linode.com/docs/development/install-java-on-ubuntu-16-04), [OSX](http://www.oracle.com/technetwork/java/javase/downloads/index.html), and [Windows](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
<br/><br/>
3. Eclipse CDT plugin
  * Open Eclipse and go to _Help > Install New Software_; a new window will open.
  * Paste the following address in the “Work with” field [http://download.eclipse.org/tools/cdt/releases/9.3](http://download.eclipse.org/tools/cdt/releases/9.3)
  * Select the elements in CDT Main Features category, i.e., C/C++ Development Tools and C/C++ Development Tools SDK
  * Click Next twice, then click "I accept the terms of the license agreement", and finally click Finish. Wait until  CDT is installed.
  * Restart Eclipse
<br/><br/>
4. Eclipse EGit plugin (optional)
  * Open Eclipse and go to _Help > Install New Software_; a new window will open.
  * Paste the following address in the “Work with” field [http://download.eclipse.org/egit/updates](http://download.eclipse.org/egit/updates)
  * Select the elements in Git Integration for Eclipse category
  * Click Next twice, then click "I accept the terms of the license agreement", and finally click Finish. Wait until  EGit is installed.
  * Restart Eclipse
<br/><br/>


### Install API Modernisation Plugin
---
To install the latest version of the API Modernisation plugin:
* Open Eclipse and go to _Help > Install New Software_; a new window will open.
* Paste the following address in the “Work with” field [http://www-users.cs.york.ac.uk/simos/Modernisation/Plugin](http://www-users.cs.york.ac.uk/~simos/Modernisation/Plugin)
* Select the API Modernisation element under Modernisation category
* Click Next twice, then click "I accept the terms of the license agreement", and finally click Finish. Wait until the API Modernisation plugin is installed.
* Restart Eclipse


### Extending API Modernisation Plugin
---
To extend the API Modernisation plugin:
* File > Import > Projects from Git > Next > Clone URI > Next
* Add the following in URI field [https://github.com/gerasimou/SoftwareObsolescence.git](https://github.com/gerasimou/SoftwareObsolescence.git) and click next
* Select the local directory to download the github repo and click next
* Select _Import existing Eclipse projects_, then select the plugin project _org.spg.refactoring_ and click finish
