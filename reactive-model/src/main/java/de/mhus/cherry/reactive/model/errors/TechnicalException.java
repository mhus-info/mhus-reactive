package de.mhus.cherry.reactive.model.errors;

import de.mhus.cherry.reactive.model.util.NoActivity;

/**
 * This exception will in every case set the case to failed.
 * No default error activity will be called.
 * 
 * @author mikehummel
 *
 */
public class TechnicalException extends TaskException {

	public TechnicalException(String msg) {
		super(msg, NoActivity.class);
	}

	private static final long serialVersionUID = 1L;

}
