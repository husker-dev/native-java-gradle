package ru.mail.huskerdev.gradle.ncompile.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import ru.mail.huskerdev.gradle.ncompile.core.compile.GraalVM

import static ru.mail.huskerdev.gradle.ncompile.PluginConfig.extension

class AppendGraalConfig extends DefaultTask {

    AppendGraalConfig(){
        description = 'Creates compile configuration which include: JNI, Reflection, Serialization, Proxy, Resources, Graal arguments'
        dependsOn('prepareNative')
        group = "native-config"
    }

    @TaskAction
    void run(){
        GraalVM.createConfig(extension.jarPath.get(), true)
    }
}
