package org.lisaac.ldt.views;

import java.util.Arrays;

import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleFactory;
import org.eclipse.ui.console.IConsoleListener;
import org.eclipse.ui.console.IConsoleManager;

public class ConsoleFactory implements IConsoleFactory{

	/** The console created by this factory if any. */
	private static LisaacConsole openConsole = null;

	public ConsoleFactory() {

		ConsolePlugin.getDefault().getConsoleManager().addConsoleListener(new IConsoleListener() {
			public void consolesAdded(IConsole[] consoles) {
			}
			public void consolesRemoved(IConsole[] consoles) {
				if (Arrays.asList(consoles).contains(openConsole)) {
					openConsole = null;
				}
			}
		});
	}

	/**
	 * Opens a new console.
	 */
	public void openConsole() {
		createConsole();
	}

	public static void createConsole() {
		IConsoleManager consoleManager = ConsolePlugin.getDefault().getConsoleManager();

		if (openConsole == null) {
			openConsole = new LisaacConsole();
			openConsole.initializeDocument();

			consoleManager.addConsoles(new IConsole[] {openConsole});
		}
		consoleManager.showConsoleView(openConsole);
	}
	
	public static LisaacConsole getConsole() {
		if (openConsole == null) {
			createConsole();
		}
		return openConsole;
	}
}
