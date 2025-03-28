package gson.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Duration;

public class DurationTypeAdapter extends TypeAdapter<Duration> {

    @Override
    public void write(JsonWriter out, Duration value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            // Duration обычно сериализуют в стандартный ISO-8601 (например, "PT30M")
            out.value(value.toString());
        }
    }

    @Override
    public Duration read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }
        // Парсим строку вида "PT30M", "PT1H15M" и т.п.
        String str = in.nextString();
        return Duration.parse(str);
    }
}
