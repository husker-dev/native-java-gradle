package ru.mail.huskerdev.gradle.ncompile


import org.gradle.api.Plugin
import org.gradle.api.Project
import ru.mail.huskerdev.gradle.ncompile.core.support.ConfigSupport
import ru.mail.huskerdev.gradle.ncompile.core.support.javafx.JavaFXSupport
import ru.mail.huskerdev.gradle.ncompile.extensions.DownloadsExtension
import ru.mail.huskerdev.gradle.ncompile.extensions.GraalVMExtension
import ru.mail.huskerdev.gradle.ncompile.extensions.InfoExtension
import ru.mail.huskerdev.gradle.ncompile.extensions.PluginExtension
import ru.mail.huskerdev.gradle.ncompile.tasks.AppendGraalConfig
import ru.mail.huskerdev.gradle.ncompile.tasks.CreateBinary
import ru.mail.huskerdev.gradle.ncompile.tasks.CreateGraalConfig
import ru.mail.huskerdev.gradle.ncompile.tasks.PrepareNative

import static PluginConfig.*


class NativeCompilePlugin implements Plugin<Project> {

    void apply(Project project) {
        project.plugins.apply("de.undercouch.download")

        PluginConfig.project = project
        extension = project.extensions.create('nativeCompile', PluginExtension.class)
        graalExtension = project.extensions.nativeCompile.extensions.create("graalvm", GraalVMExtension.class)
        downloadsExtension = project.extensions.nativeCompile.extensions.create("downloads", DownloadsExtension.class)
        infoExtension = project.extensions.nativeCompile.extensions.create("output", InfoExtension.class)

        tmpFolder = "$project.buildDir/tmp/native"
        nativeFolder = "$project.projectDir/native"

        ConfigSupport.add(new JavaFXSupport())

        project.task('createConfig', type: CreateGraalConfig)
        project.task('appendConfig', type: AppendGraalConfig)
        project.task('compile', type: CreateBinary)
        project.task("prepareNative", type: PrepareNative)
    }

}

