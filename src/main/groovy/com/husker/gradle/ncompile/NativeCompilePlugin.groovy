package com.husker.gradle.ncompile

import com.husker.gradle.ncompile.extensions.GraalVMExtension
import com.husker.gradle.ncompile.extensions.PluginExtension
import com.husker.gradle.ncompile.tasks.CreateBinary
import com.husker.gradle.ncompile.tasks.CreateGraalConfig
import com.husker.gradle.ncompile.tasks.PrepareNative
import org.gradle.api.Plugin
import org.gradle.api.Project

import static com.husker.gradle.ncompile.PluginConfig.*


class NativeCompilePlugin implements Plugin<Project> {

    void apply(Project project) {
        project.getPluginManager().apply("de.undercouch.download")

        PluginConfig.project = project
        extension = project.extensions.create('nativeCompile', PluginExtension.class)
        graalExtension = project.extensions.nativeCompile.extensions.create("graalvm", GraalVMExtension.class)
        tmpFolder = "$project.buildDir/tmp/native"
        nativeFolder = "$project.projectDir/native"

        project.task('createConfig', type: CreateGraalConfig)
        project.task('compile', type: CreateBinary)
        project.task("prepareNative", type: PrepareNative)
    }

}

