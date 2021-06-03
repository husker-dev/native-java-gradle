package com.husker.gradle.ncompile

import com.husker.gradle.ncompile.tasks.CreateBinary
import com.husker.gradle.ncompile.tasks.CreateGraalConfig
import com.husker.gradle.ncompile.tasks.PrepareNative
import org.gradle.api.Plugin
import org.gradle.api.Project
import static com.husker.gradle.ncompile.PluginConfig.*


class NativeCompilePlugin implements Plugin<Project> {

    void apply(Project project) {
        PluginConfig.project = project
        extension = project.extensions.create('nativeCompile', NativeCompilePluginExtension.class)
        tmpFolder = "$project.buildDir/tmp/native"

        project.task('createGraalConfig', type: CreateGraalConfig)
        project.task('createBinary', type: CreateBinary)
        project.task("prepareNative", type: PrepareNative)
    }

}

