package ua.pp.hak;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ua.pp.hak.ui.Notepad;

public class Runner {
	final static Logger logger = LogManager.getLogger(Runner.class);

	public static void main(String[] args) {
		logger.info("Start working...");
		Notepad.main(args);
	}
}
