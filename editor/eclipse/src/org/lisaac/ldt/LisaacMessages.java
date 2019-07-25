package org.lisaac.ldt;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class LisaacMessages {
	private static final String BUNDLE_NAME = "org.lisaac.ldt.messages"; //$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle(BUNDLE_NAME);

	private LisaacMessages() {
	}

	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
