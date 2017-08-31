# Managing Software Obsolescence in APIs

An Eclipse plugin for semi-automated software modernisation  to address the problem of software obsolescence in APIs.
<br/>

### Repository Structure

* **[Case Studies](https://github.com/gerasimou/SoftwareObsolescence/tree/master/CaseStudies)**: Sample software systems for trying out the API Modernisation plugin

* **[org.spg.refactoring](https://github.com/gerasimou/SoftwareObsolescence/tree/master/org.spg.refactoring)**: API modernisation Eclipse plugin  project  

* **[org.spg.refactoring.feature](https://github.com/gerasimou/SoftwareObsolescence/tree/master/org.spg.refactoring.feature)**: API modernisation Eclipse feature project  

* **[org.spg.refactoring.updateSite](https://github.com/gerasimou/SoftwareObsolescence/tree/master/org.spg.refactoring.updateSite)**: API modernisation Eclipse update site project  
---
<br/>

### Prerequisites
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
---
<br/>

### Install API Modernisation Plugin
To install the latest version of the API Modernisation plugin:
* Open Eclipse and go to _Help > Install New Software_; a new window will open.
* Paste the following address in the “Work with” field [http://www-users.cs.york.ac.uk/simos/Modernisation/Plugin](http://www-users.cs.york.ac.uk/~simos/Modernisation/Plugin)
* Select the API Modernisation element under Modernisation category
* Click Next twice, then click "I accept the terms of the license agreement", and finally click Finish. Wait until the API Modernisation plugin is installed.
* Restart Eclipse
<br/><br/>

#### Visualisation Depedency

The plugin employs a variant of [code city metaphor](https://wettel.github.io/codecity.html) to visualise the dependency level of a software system on the legacy API (library). To achieve this, the plugin uses the open-source application [JSCity](https://github.com/aserg-ufmg/JSCity). To start using JSCity install its dependencies (NodeJS and MySQL) and configure the application following the steps below:
* Install [NodeJS](https://nodejs.org/en) for the target operating system
<br>On Linux execute ```sudo apt-get install nodejs```
* Instal [MySQL](https://www.mysql.com)
<br>On Linux execute ```sudo apt-get install mysqlserver```
* Through a terminal connect to mysql
<br>On Linux execute ```mysql -u root -p```
* Run the script schema.sql located in org.spg.refactoring > JSCity > sql
<br>On Linux execute ```source org.spg.refactoring/JSCity/sql/schema.sql```
<br/><br/>You can now use the API Modernisation plugin
---
<br/>


### Extending API Modernisation Plugin
To extend the API Modernisation plugin, clone this repository and import the plugin project on Eclipse C/C++ IDE:
* Open Eclipse C/C++ IDE
* File > Import > Projects from Git > Next > Clone URI > Next
* Add the following in URI field [https://github.com/gerasimou/SoftwareObsolescence.git](https://github.com/gerasimou/SoftwareObsolescence.git) and click next
* Select the local directory to download the github repo and click next
* Select _Import existing Eclipse projects_, then select the plugin project _org.spg.refactoring_ and click finish

***
<br/>Should you have any comments, suggestions or questions, please email us at simos.gerasimou-at-york.ac.uk
