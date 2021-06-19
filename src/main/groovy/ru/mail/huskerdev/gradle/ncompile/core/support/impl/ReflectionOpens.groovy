package ru.mail.huskerdev.gradle.ncompile.core.support.impl

import org.json.JSONArray
import org.json.JSONObject
import ru.mail.huskerdev.gradle.ncompile.core.support.ConfigSupport

import static ru.mail.huskerdev.gradle.ncompile.PluginConfig.extension

class ReflectionOpens extends ConfigSupport{

    protected void onBegin() {
        JSONArray reflections = getReflectionConfig()
        extension.reflectionOpens.get().each {
            reflections.put(new JSONObject()
                    .put("name", it)
                    /*
                    .put("allDeclaredConstructors", true)
                    .put("allPublicConstructors", true)
                    .put("allDeclaredFields", true)
                    .put("allPublicFields", true)
                    .put("allDeclaredMethods", true)
                    .put("allPublicMethods", true)
                     */
            )
        }
        setReflectionConfig(reflections)
    }

    protected boolean isCompatible() {
        return extension.reflectionOpens.get().size() > 0
    }

    protected String getName() {
        return "Reflection opens"
    }
}
