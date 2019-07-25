package org.lisaac.ldt.views;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.graphics.Font;
import org.eclipse.ui.console.IOConsole;
import org.lisaac.ldt.LisaacMessages;

public class LisaacConsole extends IOConsole  {

	public final static String CONSOLE_TYPE = "lisaacConsole"; //$NON-NLS-1$
	public static final String CONSOLE_FONT= "org.eclipse.debug.ui.consoleFont"; //$NON-NLS-1$

	public LisaacConsole() {
		super(LisaacMessages.getString("LisaacConsole.1"), CONSOLE_TYPE, null, true); //$NON-NLS-1$

		Font font = JFaceResources.getFont(CONSOLE_FONT);
		setFont(font);
	}

	/**
	 * Set the document's initial contents.
	 * Called by ConsoleFactory.
	 */
	void initializeDocument() {
		getDocument().set(""); //$NON-NLS-1$
	}
}
