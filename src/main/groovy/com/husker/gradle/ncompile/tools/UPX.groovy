package com.husker.gradle.ncompile.tools

import static com.husker.gradle.ncompile.PluginConfig.*

class UPX extends Tool {

    static {
        addTool(this)
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
        project.delete {
            delete folder
        }
        project.delete {
            delete("$toolsPath/upx.zip")
        }
    }
}
