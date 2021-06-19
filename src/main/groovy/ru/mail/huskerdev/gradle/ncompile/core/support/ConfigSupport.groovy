package ru.mail.huskerdev.gradle.ncompile.core.support

import org.json.JSONArray
import ru.mail.huskerdev.gradle.ncompile.core.compile.GraalVM

import static ru.mail.huskerdev.gradle.ncompile.PluginConfig.*

abstract class ConfigSupport implements Runnable{

    private static ArrayList<ConfigSupport> supports = new ArrayList<>()
    private static ArrayList<ConfigSupport> loaded = new ArrayList<>()

    static void add(ConfigSupport support){
        if(!supports.contains(supports))
            supports.add(support)
    }

    ConfigSupport(){
        GraalVM.addOnNativeBuildListener(this)
    }

    void run() {
        if(isCompatible()) {
            onBegin()
            if(supports.indexOf(this) == 0)
                loaded.clear()
            loaded.add(this)
            if(supports.size() > 0 && supports.indexOf(this) == supports.size() - 1){
                println("=========================")
                println "Loaded config support:"
                loaded.forEach {
                    println("   - " + it.name)
                }
                println("=========================")
            }
        }
    }

    protected static File getReflectionConfigFile(){
        project.file("$GraalVM.tmpConfigPath/reflect-config.json")
    }

    protected JSONArray getReflectionConfig(){
        return new JSONArray(getReflectionConfigFile().text)
    }

    protected void setReflectionConfig(JSONArray config){
        getReflectionConfigFile().text = config.toString(2)
    }

    protected abstract void onBegin()
    protected abstract boolean isCompatible()
    protected abstract String getName()

    boolean equals(o) {
        if (this.is(o)) return true
        if (!(o instanceof ConfigSupport)) return false

        ConfigSupport that = (ConfigSupport) o

        if (name != that.name) return false
        return true
    }

    int hashCode() {
        return (name != null ? name.hashCode() : 0)
    }
}
