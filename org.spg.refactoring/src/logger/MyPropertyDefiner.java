package logger;

import ch.qos.logback.core.PropertyDefinerBase;

public class MyPropertyDefiner extends PropertyDefinerBase {

	public MyPropertyDefiner() {
	}

	@Override
	public String getPropertyValue() {
		return MyPropertyDefiner.class.getProtectionDomain().getCodeSource().getLocation().getFile();
	}

}
