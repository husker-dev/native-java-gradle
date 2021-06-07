package com.husker.gradle.ncompile.compile

import com.husker.gradle.ncompile.compile.impl.WindowsCompiler

import static com.husker.gradle.ncompile.PluginConfig.*

abstract class PlatformCompiler {

    static ArrayList<PlatformCompiler> compilers = new ArrayList<>()

    static {
        compilers.add(new WindowsCompiler())
    }

    static PlatformCompiler getDefaultCompiler(){
        for(PlatformCompiler compiler : compilers)
            if(compiler.testCompatibility())
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
    abstract boolean testCompatibility()
}
