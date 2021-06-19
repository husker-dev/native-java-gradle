package ru.mail.huskerdev.gradle.ncompile.core.compile

import org.gradle.api.GradleException
import ru.mail.huskerdev.gradle.ncompile.core.compile.platforms.WindowsCompiler

import static ru.mail.huskerdev.gradle.ncompile.PluginConfig.*

abstract class PlatformCompiler {

    static ArrayList<PlatformCompiler> compilers = new ArrayList<>()

    static {
        compilers.add(new WindowsCompiler())
    }

    static PlatformCompiler getDefaultCompiler(){
        for(PlatformCompiler compiler : compilers)
            if(compiler.isCompatible())
                return compiler
    }

    static String getOSName(){
        return System.getProperty("os.name").toLowerCase()
    }

    String prefix
    PlatformCompiler(String prefix){
        this.prefix = prefix
    }

    String getDir(){
        return "$tmpFolder/$prefix"
    }

    abstract String getRunnableExtension()
    abstract Process runScript(String script)
    abstract void applySettings(File file)
    abstract boolean isCompatible()
    abstract void setSystemVariable(String name, String value)
    abstract Process runCommand(String command)
    abstract String getCmdExtension()

    String readContextFile(String path){
        return new BufferedReader(new InputStreamReader(PlatformCompiler.class.getResourceAsStream("/$prefix/$path")))
                .lines()
                .toArray({a -> new String[a]})
                .join("\n")
    }

    static class Unsupported extends PlatformCompiler{

        private static void throwUnsupported(){
            throw new GradleException("Unsupported OS (${System.getProperty("os.name")})")
        }

        Unsupported(){
            super("unsupported")
        }

        String getRunnableExtension() {
            return ""
        }

        Process runScript(String script) {
            throwUnsupported()
        }

        void applySettings(File file) {
            throwUnsupported()
        }

        boolean isCompatible() {
            return true
        }

        void setSystemVariable(String name, String value) {
            throwUnsupported()
        }

        Process runCommand(String command) {
            throwUnsupported()
        }

        String getCmdExtension() {
            throwUnsupported()
        }
    }
}
