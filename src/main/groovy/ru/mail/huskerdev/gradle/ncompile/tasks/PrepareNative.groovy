package ru.mail.huskerdev.gradle.ncompile.tasks


import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import ru.mail.huskerdev.gradle.ncompile.core.compile.GraalVM
import ru.mail.huskerdev.gradle.ncompile.core.tools.Tool

import static ru.mail.huskerdev.gradle.ncompile.PluginConfig.*

class PrepareNative extends DefaultTask {

    PrepareNative(){
        dependsOn('jar')
        description = 'Prepares all folders and variables'
    }

    @TaskAction
    void run(){
        project.delete("$project.buildDir/tmp/native")

        project.mkdir Tool.toolsPath
        project.mkdir GraalVM.configPath
        project.mkdir nativeFolder
        project.mkdir tmpFolder

        if (extension.jarPath.get() == "#default")
            extension.jarPath.set(project.jar.archiveFile.get().asFile.toPath().toString())

        if (infoExtension.outputName.get() == "#default")
            infoExtension.outputName.set(new File(extension.jarPath.get()).getName().replace(".jar", ""))

        GraalVM.checkForGraalVM()
        File configFile = project.file(GraalVM.configPath)
        if(!configFile.exists() || configFile.length() == 0)
            GraalVM.createConfig(null)
    }
}
