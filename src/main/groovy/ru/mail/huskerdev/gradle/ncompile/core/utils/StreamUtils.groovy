package ru.mail.huskerdev.gradle.ncompile.core.utils

import java.nio.charset.StandardCharsets
import java.util.function.Consumer

class StreamUtils {

    static void readStream(Consumer<String> listener, InputStream... streams){
        streams.each {
            readStream(it, listener)
        }
    }

    static void readStream(InputStream stream, Consumer<String> listener){
        new Thread({
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))
            String line
            while ((line = reader.readLine()) != null)
                listener.accept(line)
        }).start()
    }
}
