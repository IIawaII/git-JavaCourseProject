package com.carrental.util;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Centralized logging wrapper.
 * By default it writes logs to logs/app.log and disables console parent handlers so nothing
 * is printed to the terminal.
 */
public final class AppLogger {
    private static final Logger LOG = Logger.getLogger("com.carrental");

    static {
        try {
            // Disable parent handlers to avoid console output
            LOG.setUseParentHandlers(false);

            java.io.File dir = new java.io.File("logs");
            if (!dir.exists()) dir.mkdirs();

            FileHandler fh = new FileHandler("logs/app.log", true);
            fh.setFormatter(new SimpleFormatter());
            LOG.addHandler(fh);
            LOG.setLevel(Level.INFO);
        } catch (IOException e) {
            // If file handler can't be created, disable logging to avoid terminal output
            LOG.setLevel(Level.OFF);
        }
    }

    private AppLogger() {}

    public static void info(String msg) {
        LOG.log(Level.INFO, msg);
    }

    public static void warn(String msg) {
        LOG.log(Level.WARNING, msg);
    }

    public static void error(String msg) {
        LOG.log(Level.SEVERE, msg);
    }

    public static void logException(String msg, Throwable t) {
        LOG.log(Level.SEVERE, msg, t);
    }
}
