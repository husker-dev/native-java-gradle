package ru.mail.huskerdev.gradle.ncompile.core.support.impl

import org.json.JSONArray
import org.json.JSONObject
import ru.mail.huskerdev.gradle.ncompile.core.support.ConfigSupport


class JavaFXSupport extends ConfigSupport{

    private HashMap<String, String> classes = new HashMap<>()

    JavaFXSupport(){
        /*
            put(value, condition)
            if [condition] class found in reflection config, then add [value]
         */
        classes.put("javafx.scene.image.Image",         "javafx.scene.image.ImageView")
        classes.put("javafx.scene.transform.Transform", "javafx.scene.Node")
        classes.put("javafx.scene.shape.Shape",         "javafx.scene.Node")
        classes.put("javafx.scene.text.Text",           "javafx.scene.text.Font")
    }

    protected void onBegin() {
        JSONArray config = getReflectionConfig()
        ArrayList<JSONObject> toAdd = new ArrayList<>()

        for(int i = 0; i < config.length(); i++){
            JSONObject object = config.getJSONObject(i)
            String name = object.get("name")
            classes.forEach(){ key, value ->
                if(value == name)
                    toAdd.add(new JSONObject().put("name", key))
            }
        }
        toAdd.each {config.put(it)}
        setReflectionConfig(config)
    }

    protected boolean isCompatible() {
        return getReflectionConfigFile().text.contains("javafx.")
    }

    protected String getName() {
        return "JavaFX"
    }
}
