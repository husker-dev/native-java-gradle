package ru.mail.huskerdev.gradle.ncompile.core.compile.platforms

import ru.mail.huskerdev.gradle.ncompile.core.compile.PlatformCompiler
import org.gradle.api.GradleException
import ru.mail.huskerdev.gradle.ncompile.core.tools.ResourceHacker
import ru.mail.huskerdev.gradle.ncompile.core.tools.VSWhere

import static ru.mail.huskerdev.gradle.ncompile.PluginConfig.*

class WindowsCompiler extends PlatformCompiler {

    WindowsCompiler(){
        super("win")
    }

    String getRunnableExtension() {
        return "exe"
    }

    Process runScript(String script) {
        return runVCCommand("build", script)
    }

    void applySettings(File file) {
        project.file("$dir/resources.rc").text = readContextFile("resource_hacker_config.txt")
                .replace("[file_version1]", infoExtension.fileVersion.get().replace('.', ','))
                .replace("[product_version1]", infoExtension.productVersion.get().replace('.', ','))
                .replace("[company_name]", infoExtension.companyName.get())
                .replace("[file_description]", infoExtension.fileDescription.get())
                .replace("[file_version]", infoExtension.fileVersion.get())
                .replace("[product_name]", infoExtension.productName.get())
                .replace("[internal_name]", infoExtension.productName.get())
                .replace("[legal_copyright]", infoExtension.copyright.get())
                .replace("[original_file_name]", "${infoExtension.outputName.get()}.exe")
                .replace("[product_version]", infoExtension.productVersion.get())

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
        if (infoExtension.icon.get() != "#undefined") {
            // Setting icon
            ResourceHacker.runExe(
                    file.getPath(),
                    ['-action addskip', "-res \"${infoExtension.icon.get()}\"", '-mask ICONGROUP,MAINICON,']
            )
        }
        if(!infoExtension.console.get())
            runVCCommand("windows", "editbin /SUBSYSTEM:WINDOWS \"${file.getPath()}\"").waitFor()

        project.delete{
            delete("$dir/resources.rc")
        }
        project.delete{
            delete("$dir/resources.res")
        }
    }

    boolean isCompatible() {
        return OSName.contains("win")
    }

    void setSystemVariable(String name, String value) {
        runCommand("setx ${name} \"${value}\"")
    }

    Process runCommand(String command) {
        return Runtime.runtime.exec(command)
    }

    Process runVCCommand(String fileName, String script){
        String vs = extension.visualStudio.get()
        if(vs == "#default"){
            vs = VSWhere.getVSPath()
            if(vs == null)
                throw new GradleException("Can't find installed Visual Studio. Please specify property 'visualStudio' by yourself")
        }

        String command = [
                "@echo off",
                "call \"$vs\\VC\\Auxiliary\\Build\\vcvars64.bat\"",
                "call $script"
        ].join("\n")
        File file = project.file("$dir/${fileName}.bat")
        file.text = command

        return runCommand(file.getPath())
    }
}
