package ru.mail.huskerdev.gradle.ncompile.core.utils

class Platform {

    static private String OS = System.getProperty("os.name").toLowerCase()

    static boolean isWindows() {
        return OS.contains("win")
    }

    static boolean isMac() {
        return OS.contains("mac")
    }

    static boolean isUnix() {
        return OS.contains("nix") || OS.contains("nux") || OS.contains("aix")
    }
}
