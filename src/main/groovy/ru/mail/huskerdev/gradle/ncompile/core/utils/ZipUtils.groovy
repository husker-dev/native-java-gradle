package ru.mail.huskerdev.gradle.ncompile.core.utils

import java.nio.file.Files
import java.nio.file.Paths
import java.util.function.Consumer
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream
import static ru.mail.huskerdev.gradle.ncompile.PluginConfig.project

class ZipUtils {

    static void foreachFile(String filePath, Consumer<ZipEntry> listener){
        ZipFile zipFile = new ZipFile(filePath)
        Enumeration<? extends ZipEntry> entries = zipFile.entries()
        while(entries.hasMoreElements()){
            ZipEntry entry = entries.nextElement()
            listener.accept(entry)
        }
        zipFile.close()
    }

    static void copyZipWithAppend(String from, File to, String append){
        copyZipWithAppend(project.file(from), to, project.file(append))
    }

    static void copyZipWithAppend(File from, File to, File append){
        copyZipWithAppend(from.absolutePath, to.absolutePath, append)
    }

    static void copyZipWithAppend(String from, String to, File append){
        if (project.file(to).exists())
            Files.delete(Paths.get(project.file(to).absolutePath))

        ZipFile fromZip = new ZipFile(project.file(from))
        ZipOutputStream toZip = new ZipOutputStream(new FileOutputStream(project.file(to)))

        // Copy content
        Enumeration<? extends ZipEntry> entries = fromZip.entries()
        while (entries.hasMoreElements()) {
            ZipEntry oldEntry = entries.nextElement()
            ZipEntry newEntry = new ZipEntry(oldEntry.name)
            applyZipEntryAttributes(oldEntry, newEntry)

            toZip.putNextEntry(newEntry)
            copyStream(fromZip.getInputStream(oldEntry), toZip)
            toZip.closeEntry()
        }
        fromZip.close()

        // Append file
        String parent = normalizePath(append.absolutePath) + File.separator
        for(File file : project.fileTree(append)){
            if(file.directory)
                continue
            String path = normalizePath(file.absolutePath).replace(parent, "")

            ZipEntry entry = new ZipEntry(path)
            toZip.putNextEntry(entry)
            copyStream(new FileInputStream(file), toZip)
            toZip.closeEntry()
        }
        toZip.close()
    }

    private static byte[] BUFFER = new byte[4096 * 1024]
    private static void copyStream(InputStream input, OutputStream output) throws IOException {
        int bytesRead
        while ((bytesRead = input.read(BUFFER))!= -1)
            output.write(BUFFER, 0, bytesRead)
    }

    private static String normalizePath(String path){
        return path.replace("/", File.separator).replace("\\", File.separator)
    }

    private static void applyZipEntryAttributes(ZipEntry from, ZipEntry to){
        to.comment = from.comment
        if(from.creationTime != null)
            to.creationTime = from.creationTime
        if(from.lastAccessTime != null)
            to.lastAccessTime = from.lastAccessTime
        if(from.lastModifiedTime != null)
            to.lastModifiedTime = from.lastModifiedTime
        to.extra = from.extra
    }
}
