package com.husker.gradle.ncompile.tasks

import com.husker.gradle.ncompile.compile.GraalVM
import com.husker.gradle.ncompile.tools.UPX
import com.husker.gradle.ncompile.utils.ProgressTask
import org.gradle.api.tasks.TaskAction

import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

import static com.husker.gradle.ncompile.PluginConfig.*

class CreateBinary extends ProgressTask {

    CreateBinary(){
        description = 'Compiles binary file for host OS'
        dependsOn('prepareNative')
        group = "native"
    }

    @TaskAction
    void run(){
        File jarFile = project.file("$tmpFolder/lib.jar")
        addToZip(extension.jarPath.get(), jarFile.getPath(), project.file("$GraalVM.configPath/META-INF"))

        startProgress("Compilation")
        File compiled = GraalVM.createImage(jarFile, {percent -> setProgress("Compilation: $percent%")})
        completeProgress()

        UPX.run(compiled.getPath())
        GraalVM.applyFileSettings(compiled)

        project.copy {
            from compiled.getPath()
            into "$project.buildDir/native"
        }
    }

    private void addToZip(String zipFile, String newZip, File file) {
        if (project.file(newZip).exists()) {
            project.delete {
                delete project.file(newZip)
            }
        }
        ZipFile zip = new ZipFile(zipFile)
        ZipOutputStream append = new ZipOutputStream(new FileOutputStream(newZip))

        try {

            // first, copy contents from existing war
            Enumeration<? extends ZipEntry> entries = zip.entries()
            while (entries.hasMoreElements()) {
                try {
                    ZipEntry e = entries.nextElement()
                    append.putNextEntry(e)
                    if (!e.isDirectory())
                        copyStream(zip.getInputStream(e), append)
                    append.closeEntry()
                } catch (Exception ignored) {}
            }

            for(File toZip : project.fileTree(file).files){
                def path = toZip.getPath().replace(file.parentFile.getPath(), "").substring(1).replace("\\", "/")

                ZipEntry e = new ZipEntry(path)
                append.putNextEntry(e)
                copyStream(new FileInputStream(toZip), append)
                append.closeEntry()
            }
        }catch(Exception ex){
            ex.printStackTrace()
        }

        // close
        zip.close()
        append.close()
    }

    static byte[] BUFFER = new byte[4096 * 1024]
    static void copyStream(InputStream input, OutputStream output) throws IOException {
        int bytesRead;
        while ((bytesRead = input.read(BUFFER))!= -1) {
            output.write(BUFFER, 0, bytesRead);
        }
    }
}
