package com.husker.gradle.ncompile.extensions

import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property

abstract class GraalVMExtension {

    abstract Property<String> getJava()
    abstract Property<String> getVersion()
    abstract Property<String> getArch()
    abstract Property<String> getPath()
    abstract ListProperty<String> getArgs()

    GraalVMExtension() {
        java.convention("11")
        version.convention("21.1.0")
        arch.convention("amd64")
        path.convention("#default")
        args.convention([])
    }

}
