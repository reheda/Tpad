package ua.pp.hak.update;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Version implements Comparable<Version> {

	private String version;
	final static Logger logger = LogManager.getLogger(Version.class);

	public final String get() {
		return this.version;
	}

	public Version(String version) {
		if (version == null) {
			logger.error("Version can not be null");
			throw new IllegalArgumentException("Version can not be null");

		}
		if (!version.matches("[0-9]+(\\.[0-9]+)*")) {
			logger.error("Invalid version format");
			throw new IllegalArgumentException("Invalid version format");
		}
		this.version = version;
	}

	@Override
	public int compareTo(Version that) {
		if (that == null)
			return 1;
		String[] thisParts = this.get().split("\\.");
		String[] thatParts = that.get().split("\\.");
		int length = Math.max(thisParts.length, thatParts.length);
		for (int i = 0; i < length; i++) {
			int thisPart = i < thisParts.length ? Integer.parseInt(thisParts[i]) : 0;
			int thatPart = i < thatParts.length ? Integer.parseInt(thatParts[i]) : 0;
			if (thisPart < thatPart)
				return -1;
			if (thisPart > thatPart)
				return 1;
		}
		return 0;
	}

	@Override
	public boolean equals(Object that) {
		if (this == that)
			return true;
		if (that == null)
			return false;
		if (this.getClass() != that.getClass())
			return false;
		return this.compareTo((Version) that) == 0;
	}

}
