package ru.mail.huskerdev.gradle.ncompile.extensions

import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property

abstract class GraalVMExtension {

    abstract Property<String> getJava()
    abstract Property<String> getVersion()
    abstract Property<String> getArch()
    abstract Property<String> getPath()
    abstract ListProperty<String> getArgs()
    abstract ListProperty<String> getConfigArgs()
    abstract Property<Boolean> getAllResources()
    abstract Property<String> getCustomLaunchSettings()

    GraalVMExtension() {
        java.convention("11")
        version.convention("21.1.0")
        arch.convention("amd64")
        path.convention("#default")
        args.convention([])
        configArgs.convention([])
        allResources.convention(true)
        customLaunchSettings.convention("null")
    }

}
