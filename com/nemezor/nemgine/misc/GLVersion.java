package com.nemezor.nemgine.misc;

public class GLVersion {

	private int major, minor;
	
	public GLVersion(int major, int minor) {
		this.major = major;
		this.minor = minor;
	}
	
	public int getMajorVersion() {
		return major;
	}
	
	public int getMinorVersion() {
		return minor;
	}
	
	public Compare compare(GLVersion version) {
		if (version.major == major) {
			if (version.minor > minor) {
				return Compare.LARGER;
			}else if (version.minor < minor) {
				return Compare.SMALLER;
			}
			return Compare.EQUAL;
		}
		if (version.major > major) {
			return Compare.LARGER;
		}
		return Compare.SMALLER;
	}
	
	public GLVersion clone() {
		return new GLVersion(major, minor);
	}
}
