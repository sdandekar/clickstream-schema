package io.kafka101.clickstream.schema.domain.avro;

import io.kafka101.clickstream.schema.domain.Click;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class AvroTranslatorTest {

    private static final String namespaceLessSchema = "{\n"
            + "  \"type\":\"record\",\n"
            + "  \"name\":\"Click\",\n"
            + "  \"fields\":[\n"
            + "    {\n"
            + "      \"name\":\"time\",\n"
            + "      \"type\":\"string\"\n"
            + "    },\n"
            + "    {\n"
            + "      \"name\":\"ip\",\n"
            + "      \"type\":\"string\"\n"
            + "    },\n"
            + "    {\n"
            + "      \"name\":\"page\",\n"
            + "      \"type\":\"string\"\n"
            + "    }\n"
            + "  ]\n"
            + "}";

    private static final String namespacedSchema = "{\n"
            + "  \"type\":\"record\",\n"
            + "  \"name\":\"Click\",\n"
            + "  \"namespace\":\"io.kafka101.clickstream.schema.domain\",\n"
            + "  \"fields\":[\n"
            + "    {\n"
            + "      \"name\":\"time\",\n"
            + "      \"type\":\"string\"\n"
            + "    },\n"
            + "    {\n"
            + "      \"name\":\"ip\",\n"
            + "      \"type\":\"string\"\n"
            + "    },\n"
            + "    {\n"
            + "      \"name\":\"page\",\n"
            + "      \"type\":\"string\"\n"
            + "    }\n"
            + "  ]\n"
            + "}";

    private AvroTranslator translator;

    @Before
    public void setUp() {
        translator = new AvroTranslator();
    }

    @Test
    public void namespacedSerialization() throws IOException {
        Schema reflectiveSchema = translator.namespacedSchemaFor(Click.class);
        Schema parsedSchema = new Schema.Parser().parse(namespacedSchema);
        assertThat(reflectiveSchema, is(equalTo(parsedSchema)));
    }

    @Test
    public void namespaceLessSerialization() throws IOException {
        Schema reflectiveSchema = translator.namespacelessSchemaFor(Click.class);
        Schema parsedSchema = new Schema.Parser().parse(namespaceLessSchema);
        assertThat(reflectiveSchema, is(equalTo(parsedSchema)));
    }

    @Test
    public void translation() throws IOException {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
        df.setTimeZone(tz);
        String nowAsISO = df.format(new Date());
        Click click = new Click(nowAsISO, "192.168.0.1", "index.html");

        GenericRecord record = translator.toAvro(click);
        Click click2 = translator.toObject(record, Click.class);
        assertThat(click.ip, is(equalTo(click2.ip)));
        assertThat(click.time, is(equalTo(click2.time)));
        assertThat(click.page, is(equalTo(click2.page)));
    }
}
