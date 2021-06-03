package com.husker.gradle.ncompile.compile

import java.util.function.Consumer;

import static com.husker.gradle.ncompile.PluginConfig.*

class GraalVM {

    static String configFolderName = "native-image-config"

    static String getConfigPath(){
        return "$project.buildDir/$configFolderName"
    }

    static void createConfig(ArrayList<String> args = []){
        project.exec {
            commandLine 'cmd', '/c', "${extension.graalVM.get()}\\bin\\java -agentlib:native-image-agent=config-output-dir=$project.buildDir\\$configFolderName\\META-INF\\native-image ${String.join(" ", args)}"
        }
        def graalArgs = [
                '-H:+ReportUnsupportedElementsAtRuntime',
                '-H:+ReportExceptionStackTraces',
                '-H:ReflectionConfigurationResources=${.}/reflect-config.json',
                '-H:DynamicProxyConfigurationResources=${.}/proxy-config.json',
                '-H:JNIConfigurationResources=${.}/jni-config.json',
                '-H:ResourceConfigurationResources=${.}/resource-config.json'
        ]
        graalArgs.addAll(extension.graalArgs.get())
        project.file("$project.buildDir/$configFolderName/META-INF/native-image/native-image.properties")
                .text = "Args = ${String.join(" \\\n       ", graalArgs)}"
    }

    static File createImage(File jar, Consumer<Integer> progress){
        progress.accept(0)
        PlatformCompiler compiler = PlatformCompiler.getDefaultCompiler()
        File outputFile = project.file("${compiler.dir}/${extension.outputName.get()}")
        project.mkdir compiler.dir

        Process process = compiler.runScript([
                "${extension.graalVM.get()}\\bin\\native-image",
                "-jar",
                "\"${jar.getPath()}\"",
                outputFile.getPath(),
                "--no-fallback"
        ].join(" "))

        process.outputStream = new LineOutputReader(){
            def onLine(String line){
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
        }
        process.waitFor()

        compiler.applySettings(outputFile)
        return outputFile
    }

    private abstract class LineOutputReader extends ByteArrayOutputStream {
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
