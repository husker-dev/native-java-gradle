package com.husker.gradle.ncompile.tasks

import com.husker.gradle.ncompile.compile.GraalVM
import com.husker.gradle.ncompile.tools.Tool
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import static com.husker.gradle.ncompile.PluginConfig.*

class PrepareNative extends DefaultTask {

    PrepareNative(){
        dependsOn('jar')
        description = 'Prepares all folders and variables'
    }

    @TaskAction
    void run(){
        project.delete {
            delete "$project.buildDir/tmp/native"
        }

        project.mkdir "$project.buildDir/native"
        project.mkdir Tool.toolsPath
        project.mkdir GraalVM.configPath
        project.mkdir nativeFolder
        project.mkdir "$project.buildDir/tmp/native"

        if (extension.jarPath.get() == "#default")
            extension.jarPath.set(project.jar.archiveFile.get().asFile.toPath().toString())

        if (extension.outputName.get() == "#default")
            extension.outputName.set(new File(extension.jarPath.get()).getName().replace(".jar", ""))

        File configFile = project.file("$GraalVM.configPath/META-INF/native-image")
        if(!configFile.exists() || configFile.length() == 0)
            GraalVM.createConfig(['--version'])
    }
}
