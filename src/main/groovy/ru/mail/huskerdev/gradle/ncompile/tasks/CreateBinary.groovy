package ru.mail.huskerdev.gradle.ncompile.tasks


import org.gradle.api.tasks.TaskAction
import ru.mail.huskerdev.gradle.ncompile.core.compile.GraalVM
import ru.mail.huskerdev.gradle.ncompile.core.tools.UPX

import static ru.mail.huskerdev.gradle.ncompile.PluginConfig.*

class CreateBinary extends ProgressTask {

    CreateBinary(){
        description = 'Compiles binary file for host OS'
        dependsOn('prepareNative')
        group = "native"
    }

    @TaskAction
    void run(){
        startProgress("Compilation")
        File compiled = GraalVM.createImage(project.file(extension.jarPath.get()), {setProgress("Compilation: $it%")})
        completeProgress()

        if(extension.compress.get())
            UPX.run(compiled.getPath())
        GraalVM.applyFileSettings(compiled)

        project.copy {
            from compiled.getPath()
            into "$project.buildDir/native"
        }
        println "Compiled file: $project.buildDir\\native\\${compiled.name}"
    }



}
