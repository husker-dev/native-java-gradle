package com.husker.gradle.ncompile

import com.husker.gradle.ncompile.extensions.*
import org.gradle.api.Project

class PluginConfig {

    public static PluginExtension extension
    public static GraalVMExtension graalExtension
    public static DownloadsExtension downloadsExtension
    public static InfoExtension infoExtension
    public static Project project
    public static String tmpFolder
    public static String nativeFolder
}
