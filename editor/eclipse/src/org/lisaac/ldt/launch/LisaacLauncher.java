package org.lisaac.ldt.launch;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.IStreamListener;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStreamMonitor;
import org.eclipse.swt.widgets.Display;
import org.lisaac.ldt.views.ConsoleFactory;
import org.lisaac.ldt.views.LisaacConsole;


public class LisaacLauncher {

	public static boolean executeCommandInConsole(final ILaunch launch, IProgressMonitor monitorParent,
			final IContainer container, final String[] commandLine, final LisaacCompiler compiler) throws CoreException {
		// get & clear lisaac console
		final LisaacConsole console = ConsoleFactory.getConsole();
		console.clearConsole();
		
		if (compiler == null) {
			console.activate();// show console		
		}
		final IProgressMonitor monitor = new NullProgressMonitor();
		
		Runnable getStandardOutput = new Runnable() {
			public void run() {
				monitor.beginTask("Executing... ", 100);

				// 1. Clean existing files
				if (compiler != null) {
					try {
						compiler.cleanFiles();
					} catch (CoreException e) {
					}
					monitor.worked(10);
				}
				
				// 2. Launch process
				Process process = null;
				try {
					process = launchProcess(commandLine, container);
				} catch (IOException e) {
					e.printStackTrace();
				}

				monitor.worked(20);
				if (monitor.isCanceled()) {        
					return;
				}
				if (process != null) {
					monitor.setTaskName("Reading output...");
					monitor.worked(20);

					// attach process stream to Lisaac console
					IProcess eclipseProcess = DebugPlugin.newProcess(launch,
							process,"Lisaac Session");
		
					// get the process output
					final StringBuffer buffer = new StringBuffer();
					
					IStreamListener listener = new IStreamListener() {	
						public void streamAppended(String text,
								IStreamMonitor monitor) {
							buffer.append(text);
						}
					};
					eclipseProcess.getStreamsProxy().getOutputStreamMonitor().addListener(listener);
					eclipseProcess.getStreamsProxy().getErrorStreamMonitor().addListener(listener);
					
					// 3. clean files after process execution
					if (compiler != null) {
						try {
							process.waitFor();
						} catch (InterruptedException e) {
						}
						try {
							compiler.moveFiles(container);
							
							while (! eclipseProcess.isTerminated()) {}
							compiler.showErrors(buffer.toString());
							
						} catch (CoreException e) {
						}
					}
				}
				monitor.done();
			}
		};
		// execute action in a new thread UI safe
		Display.getDefault().asyncExec(getStandardOutput);
		return true;
	}

	public static Process executeCommand(IProgressMonitor monitor, IContainer container, String[] commandLine) throws CoreException {
		monitor.beginTask("Executing...", 100);

		Process process = null;
		try {
			process = launchProcess(commandLine, container);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		monitor.worked(20);
		if (monitor.isCanceled()) {        
			return null;
		}
		return process;
	}

	/**
	 * Executes the command line at the given project location, and returns the 
	 * corresponding process.
	 * @param commandLine the command line
	 * @param project the project in which the command line is executed
	 * @return the corresponding process
	 * @throws IOException if the command line can't be executed
	 */
	public static Process launchProcess(String[] commandLine, IContainer directory) throws IOException {
		File projectDirectory = 
			(directory != null) ? new File(directory.getLocationURI()) : null;

			String cmd = concatCommandLine(commandLine);
			
			Process process = Runtime.getRuntime().exec(cmd, null, projectDirectory);
			return process;
	}
	
	private static String concatCommandLine(String[] commandLine) {
		StringBuffer buf = new StringBuffer();
		for (int i=0; i<commandLine.length; i++) {
			buf.append(commandLine[i]);
			buf.append(' ');
		}
		return buf.toString();
	}
}
