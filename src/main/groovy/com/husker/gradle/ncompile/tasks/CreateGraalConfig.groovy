package com.husker.gradle.ncompile.tasks

import com.husker.gradle.ncompile.compile.GraalVM
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import static com.husker.gradle.ncompile.PluginConfig.*

class CreateGraalConfig extends DefaultTask{

    CreateGraalConfig(){
        description = 'Creates compile configuration which include: JNI, Reflection, Serialization, Proxy, Resources, Graal arguments'
        dependsOn('prepareNative')
        group = "native"
    }

    @TaskAction
    void run(){
        GraalVM.createConfig(["-jar ${extension.jarPath.get()}"])
    }
}
