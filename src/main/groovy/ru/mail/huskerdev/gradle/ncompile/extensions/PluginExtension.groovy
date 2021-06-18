package ru.mail.huskerdev.gradle.ncompile.extensions

import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property

abstract class PluginExtension {

    abstract Property<String> getVisualStudio()

    abstract Property<String> getJarPath()
    abstract ListProperty<String> getUpxArgs()
    abstract Property<Boolean> getCompress()

    PluginExtension(){
        visualStudio.convention('#default')
        jarPath.convention('#default')

        upxArgs.convention(['-9'])
        compress.convention(true)
    }
}
