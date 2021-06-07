package com.husker.gradle.ncompile.compile.impl

import com.husker.gradle.ncompile.compile.PlatformCompiler
import org.gradle.api.GradleException

class UnsupportedPlatform extends PlatformCompiler{

    UnsupportedPlatform(){
        super("unsupported")
    }

    String getRunnableExtension() {
        return ""
    }

    Process runScript(String script) {
        throw new GradleException("Unsupported OS (${System.getProperty("os.name")})")
    }

    void applySettings(File file) {
        throw new GradleException("Unsupported OS (${System.getProperty("os.name")})")
    }

    boolean testCompatibility() {
        return true
    }
}
