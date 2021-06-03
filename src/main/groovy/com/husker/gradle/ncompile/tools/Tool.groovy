package com.husker.gradle.ncompile.tools

import static com.husker.gradle.ncompile.PluginConfig.*

abstract class Tool {

    static {
        def tools = [
                ResourceHacker, UPX
        ]

        for(def clazz : tools){
            def instance = clazz.getDeclaredConstructor().newInstance()
            if(instance.checkForExist())
                instance.download()
        }
    }

    public static String toolsFolderName = "native-tools"

    static String getToolsPath(){
        return "$project.buildDir/$toolsFolderName"
    }

    protected static existFile(String name){
        return project.file("$toolsPath/$name").exists()
    }

    abstract boolean checkForExist();
    abstract void download();
}
