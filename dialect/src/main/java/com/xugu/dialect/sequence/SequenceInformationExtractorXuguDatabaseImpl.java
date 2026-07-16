package com.xugu.dialect.sequence;

import org.hibernate.tool.schema.extract.internal.SequenceInformationExtractorLegacyImpl;

/**
 * Maps XuGu {@code ALL_SEQUENCES} columns for Hibernate schema validate.
 *
 * <p>Official view columns ({@code reference/system-view/all/all_sequences.md}):
 * {@code SEQ_NAME} (V11+V12), {@code MIN_VAL}, {@code MAX_VAL}, {@code STEP_VAL}.
 * {@code SEQUENCE_NAME} exists only on V12 — prefer {@code SEQ_NAME}.
 * Catalog / schema / start-value columns are not present as string qualifiers
 * ({@code SCHEMA_ID} is an integer id); return {@code null} so LegacyImpl skips them.
 */
public class SequenceInformationExtractorXuguDatabaseImpl extends SequenceInformationExtractorLegacyImpl {

	public static final SequenceInformationExtractorXuguDatabaseImpl INSTANCE =
			new SequenceInformationExtractorXuguDatabaseImpl();

	@Override
	protected String sequenceNameColumn() {
		return "seq_name";
	}

	@Override
	protected String sequenceCatalogColumn() {
		return null;
	}

	@Override
	protected String sequenceSchemaColumn() {
		return null;
	}

	@Override
	protected String sequenceStartValueColumn() {
		return null;
	}

	@Override
	protected String sequenceMinValueColumn() {
		return "min_val";
	}

	@Override
	protected String sequenceMaxValueColumn() {
		return "max_val";
	}

	@Override
	protected String sequenceIncrementColumn() {
		return "step_val";
	}
}
