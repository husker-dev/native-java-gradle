package com.husker.gradle.ncompile.compile.impl

import com.husker.gradle.ncompile.compile.PlatformCompiler
import com.husker.gradle.ncompile.tools.ResourceHacker

import static com.husker.gradle.ncompile.PluginConfig.*

class WindowsCompiler extends PlatformCompiler {

    WindowsCompiler(){
        super("win")
    }

    Process runScript(String script) {
        String command = [
                "@echo off",
                "call \"${extension.vcVars.get()}\"",
                "call $script"
        ].join("\n")
        File file = project.file("$dir/build.bat")
        file.text = command

        return Runtime.getRuntime().exec("\"${file.getPath()}\"")
    }

    void applySettings(File file) {
        project.file("$dir/resources.rc").text = """
VS_VERSION_INFO VERSIONINFO
FILEVERSION ${extension.fileVersion.get().replace('.', ',')}
PRODUCTVERSION ${extension.productVersion.get().replace('.', ',')}
{
BLOCK "StringFileInfo"
{
    BLOCK "040904E4"
    {
        VALUE "CompanyName", "${extension.companyName.get()}\\0"
        VALUE "FileDescription", "${extension.fileDescription.get()}\\0"
        VALUE "FileVersion", "${extension.fileVersion.get()}\\0"
        VALUE "ProductName", "${extension.productName.get()}\\0"
        VALUE "InternalName", "${extension.productName.get()}\\0"
        VALUE "LegalCopyright", "${extension.copyright.get()}\\0"
        VALUE "OriginalFilename", "${extension.outputName.get()}.exe\\0"
        VALUE "ProductVersion", "${extension.productVersion.get()}\\0"
    }
}

BLOCK "VarFileInfo"
{
    VALUE "Translation", 0x0409 0x04E4  
}
}
"""
        // Compile resources
        ResourceHacker.run(
                "$dir/resources.rc",
                "$dir/resources.res",
                ['-action compile']
        )
        // Apply resources
        ResourceHacker.runExe(
                file.getPath(),
                ['-action addoverwrite', "-resource \"$dir/resources.res\""]
        )
        if (extension.icon.get() != "#undefined") {
            // Setting icon
            ResourceHacker.runExe(
                    file.getPath(),
                    ['-action addskip', "-res \"${extension.icon.get()}\"", '-mask ICONGROUP,MAINICON,']
            )
        }
        if(extension.console.get())
            runScript("editbin /SUBSYSTEM:WINDOWS \"${file.getPath()}\"")

        project.delete{
            delete("$dir/resources.rc")
        }
        project.delete{
            delete("$dir/resources.res")
        }
    }


}
