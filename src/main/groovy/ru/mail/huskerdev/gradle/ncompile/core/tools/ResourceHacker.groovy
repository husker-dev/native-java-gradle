package ru.mail.huskerdev.gradle.ncompile.core.tools

import static ru.mail.huskerdev.gradle.ncompile.PluginConfig.*

class ResourceHacker extends Tool {

    static {
        loadTool(this)
    }

    static void runExe(String exe, ArrayList<String> args = []){
        def newName = exe.replace(".exe", "_tmp.exe")

        run(exe, newName, args)

        project.delete(exe)
        project.file(newName).renameTo(project.file(exe))
    }

    static void run(String openFile, String saveFile, ArrayList<String> args = []){
        args.add(0, "-open \"$openFile\"")
        args.add(1, "-save \"$saveFile\"")
        try {
            project.exec {
                commandLine 'cmd', '/c', "call \"$toolsPath/ResourceHacker.exe\" ${String.join(" ", args)}"
            }
        }catch(Exception ignored){}

        long startTime = System.currentTimeMillis()
        while (!project.file(saveFile).exists() && System.currentTimeMillis() - startTime < 3000){
            Thread.sleep(5)
        }
    }

    boolean needDownload() {
        return !existFile("ResourceHacker.exe")
    }

    void download() {
        project.download {
            src downloadsExtension.resourceHackerDownloadLink.get()
            dest project.file("$toolsPath/resourceHacker.zip")
        }
        project.copy {
            from project.zipTree("$toolsPath/resourceHacker.zip")
            include "*.exe"
            into toolsPath
        }
        project.delete("$toolsPath/resourceHacker.zip")
    }
}
