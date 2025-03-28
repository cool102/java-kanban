package gson.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeTypeAdapter extends TypeAdapter<LocalDateTime> {

    // Формат, как в вашем Task (yyyy-MM-dd HH:mm)
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    public void write(JsonWriter out, LocalDateTime value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            // Превращаем LocalDateTime -> строку с нужным форматом
            String str = value.format(FORMATTER);
            out.value(str);
        }
    }

    @Override
    public LocalDateTime read(JsonReader in) throws IOException {
        // Проверяем, не пришёл ли JSON null
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }
        // Считываем строку, парсим по тому же формату
        String str = in.nextString();
        return LocalDateTime.parse(str, FORMATTER);
    }
}