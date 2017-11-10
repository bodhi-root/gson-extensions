package com.google.gson.serializers;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

/**
 * Serializers to convert Java Time objects into their ISO (String) formats.
 * This was inspired by the fatboyindustries project at:
 * 
 *   https://github.com/gkopff/gson-javatime-serialisers
 *   
 * but simplified into a single, portable file.  TypeAdapters were also used
 * instead of JsonSerializer and JsonDeserializer to provide a slight 
 * performance increase.
 */
public interface IsoTimeSerializers {

	public static final TypeAdapter<LocalDate> LOCAL_DATE 
		= new LocalDateAdapter();
	
	public static final TypeAdapter<LocalTime> LOCAL_TIME 
		= new LocalTimeAdapter();

	public static final TypeAdapter<OffsetTime> OFFSET_TIME 
	= new OffsetTimeAdapter();
	
	public static final TypeAdapter<LocalDateTime> LOCAL_DATE_TIME 
		= new LocalDateTimeAdapter();
	
	public static final TypeAdapter<OffsetDateTime> OFFSET_DATE_TIME 
		= new OffsetDateTimeAdapter();
	
	public static final TypeAdapter<ZonedDateTime> ZONED_DATE_TIME 
		= new ZonedDateTimeAdapter();
	
	public static final TypeAdapter<Instant> INSTANT 
		= new InstantAdapter();
	
	/**
	 * Extension of TypeAdapter that serializes a value as a primitive string.
	 * Implementations do not need to deal with the JSON.  Instead, they just
	 * implement:
	 * 
	 *   T readString(String text)
	 *   String writeString(T value)
	 *   
	 * The adapter automatically handles NULLs (serializing them as JsonNull)
	 * so that these methods are guaranteed to be called with non-null values.
	 * 
	 * NOTE: TypeAdapter was chosen over JsonSerializer and JsonDeserializer
	 *       because it is faster.  (It works off the raw stream instead of
	 *       having to build a Json tree in memory.)
	 */
	public abstract class JsonStringTypeAdapter<T> extends TypeAdapter<T> {
		
		public T read(JsonReader reader) throws IOException {
			if (reader.peek() == JsonToken.NULL) {
				reader.nextNull();
				return null;
			}
			
			String text = reader.nextString();
			return readString(text);
		}
		public void write(JsonWriter writer, T value) throws IOException {
			if (value == null) {
				writer.nullValue();
				return;
			}
			
			String jsonText = writeString(value);
			writer.value(jsonText);
		}
		
		protected abstract T readString(String text) throws IOException;
		protected abstract String writeString(T value) throws IOException;
		
	}
	
	/**
	 * ISO LocalDate serializer
	 */
	public static class LocalDateAdapter extends JsonStringTypeAdapter<LocalDate> {
		
		public LocalDate readString(String text) throws IOException {
			
			return LocalDate.parse(text, DateTimeFormatter.ISO_LOCAL_DATE);
		}
		
		public String writeString(LocalDate value) throws IOException {
			
			return value.format(DateTimeFormatter.ISO_LOCAL_DATE);
		}
		
	};
	
	/**
	 * ISO LocalDateTime serializer
	 */
	public static class LocalDateTimeAdapter extends JsonStringTypeAdapter<LocalDateTime> {
		
		public LocalDateTime readString(String text) throws IOException {
			
			return LocalDateTime.parse(text, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
		}
		
		public String writeString(LocalDateTime value) throws IOException {
			
			return value.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
		}
		
	};
	
	/**
	 * ISO OffsetDateTime serializer
	 */
	public static class OffsetDateTimeAdapter extends JsonStringTypeAdapter<OffsetDateTime> {
		
		public OffsetDateTime readString(String text) throws IOException {
			
			return OffsetDateTime.parse(text, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
		}
		
		public String writeString(OffsetDateTime value) throws IOException {
			
			return value.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
		}
		
	};
	
	/**
	 * ISO LocalTime serializer
	 */
	public static class LocalTimeAdapter extends JsonStringTypeAdapter<LocalTime> {
		
		public LocalTime readString(String text) throws IOException {
			
			return LocalTime.parse(text, DateTimeFormatter.ISO_LOCAL_TIME);
		}
		
		public String writeString(LocalTime value) throws IOException {
			
			return value.format(DateTimeFormatter.ISO_LOCAL_TIME);
		}
		
	};
	
	/**
	 * ISO OffsetTime serializer
	 */
	public static class OffsetTimeAdapter extends JsonStringTypeAdapter<OffsetTime> {
		
		public OffsetTime readString(String text) throws IOException {
			
			return OffsetTime.parse(text, DateTimeFormatter.ISO_OFFSET_TIME);
		}
		
		public String writeString(OffsetTime value) throws IOException {
			
			return value.format(DateTimeFormatter.ISO_OFFSET_TIME);
		}
		
	};
	
	/**
	 * ISO ZonedDateTime serializer
	 */
	public static class ZonedDateTimeAdapter extends JsonStringTypeAdapter<ZonedDateTime> {
		
		public ZonedDateTime readString(String text) throws IOException {
			
			return ZonedDateTime.parse(text, DateTimeFormatter.ISO_DATE_TIME);
		}
		
		public String writeString(ZonedDateTime value) throws IOException {
			
			return value.format(DateTimeFormatter.ISO_DATE_TIME);
		}
		
	};
	
	/**
	 * ISO Instant serializer
	 */
	public static class InstantAdapter extends JsonStringTypeAdapter<Instant> {
		
		public Instant readString(String text) throws IOException {
			//return Instant.parse(text, DateTimeFormatter.ISO_INSTANT);
			return DateTimeFormatter.ISO_INSTANT.parse(text, Instant::from);
		}
		
		public String writeString(Instant value) throws IOException {
			//return value.format(DateTimeFormatter.ISO_INSTANT);
			return DateTimeFormatter.ISO_INSTANT.format(value);
		}
		
	};
	
}
