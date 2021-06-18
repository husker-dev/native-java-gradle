package ru.mail.huskerdev.gradle.ncompile.core.tools

import static ru.mail.huskerdev.gradle.ncompile.PluginConfig.*

class UPX extends Tool {

    static {
        loadTool(this)
    }

    static void run(String exe){
        println "Compressing..."
        project.exec {
            commandLine "$toolsPath/upx.exe", String.join(" ", extension.upxArgs.get()), exe
            standardOutput = new ByteArrayOutputStream()    // Mute output
        }
    }

    boolean needDownload() {
        return !existFile("upx.exe")
    }

    void download() {
        project.download {
            src downloadsExtension.upxDownloadLink.get()
            dest project.file("$toolsPath/upx.zip")
        }
        project.copy {
            from project.zipTree("$toolsPath/upx.zip")
            include "**/*.exe"
            into toolsPath
        }
        File folder = project.file(toolsPath).listFiles(new FilenameFilter(){
            boolean accept(File dir, String name) {
                return dir.isDirectory() && name.contains("upx")
            }
        })[0]
        project.copy {
            from new File(folder, 'upx.exe')
            into toolsPath
        }
        project.delete(folder)
        project.delete("$toolsPath/upx.zip")
    }
}
