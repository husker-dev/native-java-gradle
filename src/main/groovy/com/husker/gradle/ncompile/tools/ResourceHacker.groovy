package com.husker.gradle.ncompile.tools

import static com.husker.gradle.ncompile.PluginConfig.*

class ResourceHacker extends Tool {

    static void runExe(String exe, ArrayList<String> args = []){
        def newName = exe.replace(".exe", "_tmp.exe")

        run(exe, newName, args)

        project.delete{
            delete(exe)
        }
        project.file(newName).renameTo(project.file(exe))
    }

    static void run(String openFile, String saveFile, ArrayList<String> args = []){
        args.add(0, "-open \"$openFile\"")
        args.add(1, "-save \"$saveFile\"")
        project.exec {
            commandLine 'cmd', '/c', "$toolsPath\\windows\\ResourceHacker.exe ${String.join(" ", args)}"
        }
        while (!project.file(saveFile).exists()){
            Thread.sleep(5)
        }
    }

    boolean checkForExist() {
        return existFile("ResourceHacker.exe")
    }

    void download() {
        project.download {
            src extension.resourceHackerDownloadLink.get()
            dest project.file("$toolsPath/windows/resourceHacker.zip")
        }
        project.copy {
            from project.zipTree("$toolsPath/windows/resourceHacker.zip")
            include "*.exe"
            into "$toolsPath/windows/native-tools"
        }
        project.delete{
            delete("$toolsPath/windows/resourceHacker.zip")
        }
    }
}
