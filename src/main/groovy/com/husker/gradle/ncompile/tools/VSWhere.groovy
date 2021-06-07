package com.husker.gradle.ncompile.tools

import static com.husker.gradle.ncompile.PluginConfig.*

class VSWhere extends Tool {

    static {
        addTool(this)
    }

    boolean needDownload() {
        return !existFile("vswhere.exe")
    }

    void download() {
        project.download {
            src extension.VSWhereDownloadLink.get()
            dest project.file("$toolsPath/vswhere.exe")
        }
    }

    static String getVSPath(){
        try {
            Process process = Runtime.runtime.exec("\"$toolsPath/vswhere.exe\" -latest -property installationPath")
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.inputStream))

            return reader.readLine()
        }catch(Exception ignored){
            return null
        }
    }
}
