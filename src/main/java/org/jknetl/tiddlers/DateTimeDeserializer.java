package org.jknetl.tiddlers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDateTime;

public class DateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

    @Override
    public LocalDateTime deserialize(JsonParser arg0, DeserializationContext arg1) throws IOException, JsonProcessingException {

        //date is in following format: "20160223124517851"
        String text = arg0.getText();
        int year = Integer.valueOf(text.substring(0,4));
        int month = Integer.valueOf(text.substring(4,6));
        int day = Integer.valueOf(text.substring(6,8));
        int hour = Integer.valueOf(text.substring(8,10));
        int minute = Integer.valueOf(text.substring(10,12));
        int seconds = Integer.valueOf(text.substring(12,14));
        int nanos = Integer.valueOf(text.substring(14));


        return LocalDateTime.of(year, month, day, hour, minute, seconds, nanos);
    }

}
