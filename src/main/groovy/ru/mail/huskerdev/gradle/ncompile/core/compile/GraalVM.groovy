package ru.mail.huskerdev.gradle.ncompile.core.compile

import org.json.*
import ru.mail.huskerdev.gradle.ncompile.core.utils.Platform
import ru.mail.huskerdev.gradle.ncompile.core.utils.StreamUtils
import ru.mail.huskerdev.gradle.ncompile.core.utils.ZipUtils

import java.nio.file.Files
import java.nio.file.Paths
import java.util.function.Consumer

import static ru.mail.huskerdev.gradle.ncompile.PluginConfig.*

class GraalVM {

    private static String path = ""

    static final String systemVariable = "GRAALVM_HOME"
    static final String configFolderName = "config"

    static void createConfig(String filePath, boolean append = false){
        String argument = filePath == null ? "--version" : "${getLaunchSettings(project.file(filePath))} ${graalExtension.configArgs.get().join(" ")}"
        String agentSetting = append ? "config-merge-dir" : "config-output-dir"

        PlatformCompiler compiler = PlatformCompiler.getDefaultCompiler()
        Process process = compiler.runCommand("\"$path\\bin\\java\" -agentlib:native-image-agent=$agentSetting=\"$configPath\\META-INF\\native-image\" $argument")
        StreamUtils.readStream({println it}, process.errorStream, process.inputStream)
        process.waitFor()

        if(filePath != null && graalExtension.allResources.get()){
            HashSet<String> resources = new HashSet<>()
            File resourcesFile = new File("$configPath\\META-INF\\native-image\\resource-config.json")

            JSONObject fileJson = new JSONObject(resourcesFile.text)
            JSONArray includesArray = fileJson.getJSONObject("resources").getJSONArray("includes")
            /*
                Read generated config fields by agent.
                Each file written as:
                {"pattern":"\\Q...\\E"}
             */
            includesArray.forEach({JSONObject json ->
                resources.add(json.getString("pattern").replace("\\Q", "").replace("\\E", ""))
            })
            /*
                Add all jar files except .class
            */
            ZipUtils.foreachFile(filePath, {entry ->
                if(!entry.name.endsWith(".class") && !entry.directory)
                    resources.add(entry.name)
            })
            /*
                Add all found elements to json
             */
            includesArray.clear()
            resources.forEach({path ->
                includesArray.put(new JSONObject().put("pattern", "\\Q$path\\E"))
            })
            resourcesFile.text = fileJson.toString(2)
        }

        def graalArgs = [
                '-H:+ReportUnsupportedElementsAtRuntime',
                '-H:+ReportExceptionStackTraces',
                '-H:ReflectionConfigurationResources=${.}/reflect-config.json',
                '-H:DynamicProxyConfigurationResources=${.}/proxy-config.json',
                '-H:JNIConfigurationResources=${.}/jni-config.json',
                '-H:ResourceConfigurationResources=${.}/resource-config.json',
        ]
        project.file("$configPath/META-INF/native-image/native-image.properties")
                .text = "Args = ${String.join(" \\\n       ", graalArgs)}"
    }

    static File createImage(File jar, Consumer<Integer> progress){
        progress.accept(0)

        PlatformCompiler compiler = PlatformCompiler.getDefaultCompiler()
        File outputFile = project.file(compiler.dir + "/" + infoExtension.outputName.get())
        project.mkdir compiler.dir

        Process process = compiler.runScript([
                "$path\\bin\\native-image",
                getLaunchSettings(jar),
                "\"${outputFile.getPath()}\"",
                "--no-fallback",
                "--allow-incomplete-classpath",
                "-H:ConfigurationFileDirectories=\"$configPath/META-INF/native-image\"",
                graalExtension.args.get().join(" ")
        ].join(" "))

        def steps = [
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

        StreamUtils.readStream(process.errorStream, {
            println it
        })
        StreamUtils.readStream(process.inputStream, {
            println it
            def currentStep = 0
            steps.eachWithIndex{ String step, int i ->
                if(it.contains(step))
                    currentStep = i
            }
            progress.accept((int) (currentStep / steps.size() * 100.0))
        })
        process.waitFor()

        return project.file(outputFile.absolutePath + "." + compiler.runnableExtension)
    }

    static private String getLaunchSettings(File targetJar){
        return graalExtension.customLaunchSettings.get() == "null" ? "-jar \"${targetJar.getPath()}\"" : graalExtension.customLaunchSettings.get()
    }

    static String getConfigPath(){
        return "$nativeFolder/$configFolderName"
    }

    static String getPath(){
        checkForGraalVM()
        return path
    }

    static void applyFileSettings(File file){
        println "Patching..."
        PlatformCompiler.getDefaultCompiler().applySettings(file)
    }

    static void checkForGraalVM(){
        try {
            if(System.getenv().containsKey(systemVariable)){
                if(Files.exists(Paths.get(System.getenv(systemVariable))))
                    path = System.getenv(systemVariable)
            }
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

                def graalFolder = "${System.getProperty("user.home")}/.gradle/caches/graalvm"

                File file = project.file("$graalFolder/java$java-$osPrefix-$arch-$version")
                if (!file.exists()) {
                    File zipFile = project.file("${file.path}.$archiveExt")
                    println "Downloading GraalVM..."
                    project.download {
                        src "https://github.com/graalvm/graalvm-ce-builds/releases/download/vm-$version/graalvm-ce-java$java-$osPrefix-$arch-$version.$archiveExt"
                        dest zipFile
                        onlyIfModified true
                    }
                    println "Unzipping GraalVM..."
                    project.copy {
                        from project.zipTree(zipFile)
                        into graalFolder
                    }
                    project.delete(zipFile)
                    project.file("$graalFolder/graalvm-ce-java$java-$version").renameTo(file)
                }
                path = file.getPath()
            }

            PlatformCompiler compiler = PlatformCompiler.defaultCompiler
            compiler.setSystemVariable(systemVariable, path)
            compiler.runCommand("\"$path\\bin\\gu.cmd\" install native-image").waitFor()
        }catch(Exception e){
            e.printStackTrace()
        }
    }
}
