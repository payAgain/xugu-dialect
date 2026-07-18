package com.xugu.demo.entity;

import java.util.UUID;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Boot-side UUID storage for A-TYP-012.
 * <p>
 * Xugu JDBC rejects Hibernate {@code UUIDJdbcType} extract ({@code getObject(..., UUID.class)})
 * with {@code [E50044] Required type conversion not allowed}. Dialect IT stores GUID as text;
 * this converter mirrors that pattern via VARCHAR + string round-trip.
 */
@Converter(autoApply = false)
public class UuidAsVarcharConverter implements AttributeConverter<UUID, String> {

	@Override
	public String convertToDatabaseColumn(UUID attribute) {
		return attribute == null ? null : attribute.toString();
	}

	@Override
	public UUID convertToEntityAttribute(String dbData) {
		if ( dbData == null || dbData.isBlank() ) {
			return null;
		}
		String normalized = dbData.trim();
		if ( normalized.length() == 32 && normalized.indexOf( '-' ) < 0 ) {
			normalized = normalized.substring( 0, 8 )
					+ "-" + normalized.substring( 8, 12 )
					+ "-" + normalized.substring( 12, 16 )
					+ "-" + normalized.substring( 16, 20 )
					+ "-" + normalized.substring( 20 );
		}
		return UUID.fromString( normalized );
	}
}
