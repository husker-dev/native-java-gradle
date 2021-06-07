package com.husker.gradle.ncompile.compile

import com.husker.gradle.ncompile.utils.Platform

import java.util.function.Consumer

import static com.husker.gradle.ncompile.PluginConfig.*

class GraalVM {

    static String configFolderName = "config"

    static {
        try {
            if (graalExtension.path.get() == "#default") {
                String osPrefix = "linux"
                String archiveExt = "tar.gz"
                String version = graalExtension.version.get()
                String arch = graalExtension.arch.get()
                String java = graalExtension.java.get()
                if (Platform.isWindows()) {
                    osPrefix = "windows"
                    archiveExt = "zip"
                }
                if (Platform.isMac())
                    osPrefix = "darwin"

                File file = project.file("$nativeFolder/graalvm/java$java-$osPrefix-$arch-$version")
                if (!file.exists()) {
                    File zip = project.file("$file.path.$archiveExt")
                    println "Downloading GraalVM..."
                    project.download {
                        src "https://github.com/graalvm/graalvm-ce-builds/releases/download/vm-$version/graalvm-ce-java$java-$osPrefix-$arch-$version.$archiveExt"
                        dest zip
                        onlyIfModified true
                    }
                    println "Unzipping GraalVM..."
                    project.copy {
                        from project.zipTree(zip)
                        into "$nativeFolder/graalvm"
                    }
                    project.delete{
                        delete zip
                    }
                    project.file("$nativeFolder/graalvm/graalvm-ce-java$java-$version").renameTo(file)
                }
                graalExtension.path.set(file.getPath())
            }
            String cmdExtension = ""
            if(Platform.isWindows())
                cmdExtension = ".cmd"
            project.exec {
                commandLine 'cmd', '/c', "\"${graalExtension.path.get()}\\bin\\gu$cmdExtension\" install native-image"
                standardOutput = new ByteArrayOutputStream()
            }
        }catch(Exception e){
            e.printStackTrace()
        }
    }

    static String getConfigPath(){
        return "$nativeFolder/$configFolderName"
    }

    static void createConfig(ArrayList<String> args = []){
        project.exec {
            commandLine 'cmd', '/c', "${graalExtension.path.get()}\\bin\\java -agentlib:native-image-agent=config-output-dir=$configPath\\META-INF\\native-image ${String.join(" ", args)}"
        }
        def graalArgs = [
                '-H:+ReportUnsupportedElementsAtRuntime',
                '-H:+ReportExceptionStackTraces',
                '-H:ReflectionConfigurationResources=${.}/reflect-config.json',
                '-H:DynamicProxyConfigurationResources=${.}/proxy-config.json',
                '-H:JNIConfigurationResources=${.}/jni-config.json',
                '-H:ResourceConfigurationResources=${.}/resource-config.json'
        ]
        graalArgs.addAll(graalExtension.graalArgs.get())
        project.file("$configPath/META-INF/native-image/native-image.properties")
                .text = "Args = ${String.join(" \\\n       ", graalArgs)}"
    }

    static File createImage(File jar, Consumer<Integer> progress){
        progress.accept(0)
        PlatformCompiler compiler = PlatformCompiler.getDefaultCompiler()
        File outputFile = project.file("${compiler.dir}/${extension.outputName.get()}.${compiler.runnableExtension}")
        project.mkdir compiler.dir

        Process process = compiler.runScript([
                "${graalExtension.path.get()}\\bin\\native-image",
                "-jar",
                "\"${jar.getPath()}\"",
                "\"${outputFile.getPath().replace(".${compiler.runnableExtension}", "")}\"",
                "--no-fallback"
        ].join(" "))

        // Error output
        new Thread({
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.errorStream))
            String line
            while ((line = reader.readLine()) != null)
                println line
        }).start()

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.inputStream))
        String line
        while ((line = reader.readLine()) != null) {
            def keys = [
                    'Environment initialized for:',
                    'classlist:',
                    '(cap):',
                    'setup:',
                    '(clinit):',
                    '(typeflow):',
                    '(objects):',
                    '(features):',
                    'analysis:',
                    'universe:',
                    '(parse):',
                    '(inline):',
                    '(compile):',
                    'compile:',
                    'image:',
                    'write:',
            ]
            def current = 0
            for(int i = 0; i < keys.size(); i++)
                if(line.contains(keys[i]))
                    current = i
            int percent = (int) (current / keys.size() * 100.0)
            if(current > 0)
                progress.accept(percent)
        }

        return outputFile
    }

    static void applyFileSettings(File file){
        PlatformCompiler.getDefaultCompiler().applySettings(file)
    }

    private static abstract class LineOutputReader extends ByteArrayOutputStream {
        StringBuilder builder = new StringBuilder()
        synchronized void write(byte[] b, int off, int len) {
            for(int i = off; i < len; i++){
                if(b[i] == '\n'.bytes[0]){
                    onLine(builder.toString())
                    builder = new StringBuilder()
                }else {
                    byte[] arr = [b[i]]
                    builder.append(new String(arr, "ascii"))
                }
            }
        }

        abstract def onLine(String line)
    }
}
