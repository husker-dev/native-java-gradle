package com.husker.gradle.ncompile.tasks

import com.husker.gradle.ncompile.compile.GraalVM
import org.gradle.api.tasks.TaskAction

import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

import static com.husker.gradle.ncompile.PluginConfig.*

class CreateBinary extends ProgressTask{

    CreateBinary(){
        description = 'Compiles .exe file for Windows'
    }

    @TaskAction
    void run(){
        File jarFile = project.file("$tmpFolder/lib.jar")
        addToZip(extension.jarPath.get(), jarFile.getPath(), project.file("$GraalVM.configPath/META-INF"))

        File compiled = GraalVM.createImage(jarFile, {percent -> setProgress("Compilation: $percent")})
        project.copy {
            from compiled.getPath()
            into "$project.buildDir/native"
        }
    }

    private static void addToZip(String zipFile, String newZip, File file){
        ZipFile zip = new ZipFile(zipFile)
        ZipOutputStream append = new ZipOutputStream(new FileOutputStream(newZip))

        // first, copy contents from existing war
        Enumeration<? extends ZipEntry> entries = zip.entries()
        while (entries.hasMoreElements()) {
            ZipEntry e = entries.nextElement()
            append.putNextEntry(e)
            if (!e.isDirectory())
                copyStream(zip.getInputStream(e), append)
            append.closeEntry()
        }

        // now append some extra content
        ZipEntry e = new ZipEntry(file.getPath().replace(file.parentFile.getPath(), ""))
        append.putNextEntry(e)
        copyStream(new FileInputStream(file), append)
        append.closeEntry()

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
