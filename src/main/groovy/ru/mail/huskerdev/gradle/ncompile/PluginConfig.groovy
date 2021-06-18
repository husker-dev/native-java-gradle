package ru.mail.huskerdev.gradle.ncompile


import org.gradle.api.Project
import ru.mail.huskerdev.gradle.ncompile.extensions.DownloadsExtension
import ru.mail.huskerdev.gradle.ncompile.extensions.GraalVMExtension
import ru.mail.huskerdev.gradle.ncompile.extensions.InfoExtension
import ru.mail.huskerdev.gradle.ncompile.extensions.PluginExtension

class PluginConfig {

    public static PluginExtension extension
    public static GraalVMExtension graalExtension
    public static DownloadsExtension downloadsExtension
    public static InfoExtension infoExtension
    public static Project project
    public static String tmpFolder
    public static String nativeFolder
}
