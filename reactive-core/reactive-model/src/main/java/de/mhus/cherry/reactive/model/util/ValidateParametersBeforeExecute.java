package de.mhus.cherry.reactive.model.util;

import java.util.Map;

import de.mhus.lib.errors.ValidationException;

public interface ValidateParametersBeforeExecute {

	void validateParameters(Map<String, Object> parameters) throws ValidationException;
}
