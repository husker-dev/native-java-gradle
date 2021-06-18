package ru.mail.huskerdev.gradle.ncompile.core.tools

import static ru.mail.huskerdev.gradle.ncompile.PluginConfig.*

abstract class Tool {

    public static String toolsFolderName = "native-tools"

    static String getToolsPath(){
        "${System.getProperty("user.home")}/.gradle/caches/$toolsFolderName"
    }

    protected static existFile(String name){
        project.file("$toolsPath/$name").exists()
    }

    static void loadTool(Class<Tool> clazz){
        def instance = clazz.getDeclaredConstructor().newInstance()
        if (instance.needDownload())
            instance.download()
    }

    abstract boolean needDownload();
    abstract void download();
}
