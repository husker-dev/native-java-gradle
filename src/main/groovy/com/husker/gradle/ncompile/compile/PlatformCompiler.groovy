package com.husker.gradle.ncompile.compile

import com.husker.gradle.ncompile.compile.impl.UnsupportedPlatform
import com.husker.gradle.ncompile.compile.impl.WindowsCompiler

import static com.husker.gradle.ncompile.PluginConfig.*
import java.util.function.Predicate

abstract class PlatformCompiler {

    static HashMap<Predicate<String>, PlatformCompiler> compilers = new HashMap<>()

    static {
        // Windows
        compilers.put({os -> os.contains("win")}, new WindowsCompiler())

        // MacOS
        compilers.put({os -> os.contains("mac")}, new UnsupportedPlatform())

        // Linux
        compilers.put({os ->
            return os.contains("nix") || os.contains("nux") || os.contains("aix")
        }, new UnsupportedPlatform())
    }

    static PlatformCompiler getDefaultCompiler(){
        String os = System.getProperty("os.name")
        PlatformCompiler found = new UnsupportedPlatform()

        compilers.keySet().forEach({a ->
            if(a.test(os))
                found = compilers.get(a)
        })
        return found
    }

    String prefix
    PlatformCompiler(String prefix){
        this.prefix = prefix
    }

    String getDir(){
        return "$tmpFolder/$prefix"
    }

    abstract Process runScript(String script)
    abstract void applySettings(File file);
}
