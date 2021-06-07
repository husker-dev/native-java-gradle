package com.husker.gradle.ncompile.tools

import static com.husker.gradle.ncompile.PluginConfig.*

abstract class Tool {

    public static String toolsFolderName = "tools"

    static String getToolsPath(){
        return "$nativeFolder/$toolsFolderName"
    }

    protected static existFile(String name){
        return project.file("$toolsPath/$name").exists()
    }

    static void addTool(Class<Tool> clazz){
        def instance = clazz.getDeclaredConstructor().newInstance()
        if (instance.needDownload())
            instance.download()
    }

    abstract boolean needDownload();
    abstract void download();
}
