package org.lisaac.ldt.launch;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;
import org.eclipse.jface.dialogs.MessageDialog;
import org.lisaac.ldt.model.ILisaacModel;
import org.lisaac.ldt.model.LisaacModel;
import org.lisaac.ldt.model.items.Prototype;


public class LaunchConfiguration implements ILaunchConfigurationDelegate {

	public static final String TYPE_ID = "org.lisaac.ldt.launchConfiguration";

	protected LisaacCompiler compiler;

	public void launch(ILaunchConfiguration configuration, String mode,
			ILaunch launch, IProgressMonitor monitor) throws CoreException {

		// get project to run
		String projectName = configuration.getAttribute(LaunchConfigurationTab.LISAAC_LAUNCH_PROJECT, "");
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);

		// get run informations
		String mainPrototype = configuration.getAttribute(LaunchConfigurationTab.LISAAC_LAUNCH_PROTOTYPE, "main.li");
		String programArguments = configuration.getAttribute(LaunchConfigurationTab.LISAAC_LAUNCH_ARGUMENTS, "");

		boolean doAlwaysCompile = configuration.getAttribute(LaunchConfigurationTab.LISAAC_LAUNCH_COMPILER, true);
		boolean doRun = configuration.getAttribute(LaunchConfigurationTab.LISAAC_LAUNCH_PROGRAM, true);
		boolean hasCompiled = false;

		int index = mainPrototype.lastIndexOf('/');
		if (index != -1) {
			mainPrototype = mainPrototype.substring(index+1);
		}

		LisaacModel model = LisaacModel.getModel(project);
		if (model != null) {
			Prototype main = model.getPrototype(LisaacModel.extractPrototypeName(mainPrototype));
			if (main == null || main.getSlot("main") == null) {
				MessageDialog.openInformation(
						null,
						"Lisaac Plug-in",
						mainPrototype+ " : Slot 'main' is missing.");
				monitor.done();
				return;
			}
			String outputName = main.getName().toLowerCase();
			monitor.worked(5);

			// 1. Compile project
			if ((!doAlwaysCompile && model.needCompilation()) || doAlwaysCompile) {
				compiler = new LisaacCompiler(model, mainPrototype, outputName, "make.lip");

				// options
				int count = 0;
				String option;
				do {
					option = configuration.getAttribute(LaunchConfigurationTab.LISAAC_LAUNCH_OPTION+count, "");
					if (option != null && ! option.equals("")) {
						compiler.addOption("-"+option);

						// run / debug hack
						if (option.compareTo(ILisaacModel.slot_debug_mode) == 0) {
							compiler.addOption(mode);
						} else {
							String optionArg = configuration.getAttribute(LaunchConfigurationTab.LISAAC_LAUNCH_OPTION_ARG+count, "");
							if (! optionArg.equals("")) {
								compiler.addOption(optionArg);
							}
						}
					}
					count++;
				} while (option != null && ! option.equals(""));

				// compile & launch project;
				IContainer output = compiler.launchInConsole(launch, project, monitor);
				if (output == null) {
					MessageDialog.openInformation(
							null,
							"Lisaac Plug-in",
					"Compilation Error!");
				}
				monitor.worked(50);

				//
				hasCompiled = true;
				model.setCompiled(true);
				monitor.worked(1);
			}

			// 2. Run program
			if (doRun && !hasCompiled) {
				IContainer bin = project.getFolder("bin");
				if (bin == null) {
					bin = project;
				}
				IFile executable = bin.getFile(new Path(outputName));
				if (executable.exists()) {
					String[] commandLine = {executable.getLocation().toString(), programArguments};
					LisaacLauncher.executeCommandInConsole(launch, monitor, bin, commandLine, null);
				} else {
					model.setCompiled(false);
				}
			}
		}
		monitor.done();
	}


}
