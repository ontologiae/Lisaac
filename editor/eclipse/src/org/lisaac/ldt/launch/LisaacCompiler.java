package org.lisaac.ldt.launch;

import java.util.ArrayList;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.jface.text.IRegion;
import org.lisaac.ldt.builder.LisaacBuilder;
import org.lisaac.ldt.model.AbstractLisaacParser;
import org.lisaac.ldt.model.LisaacModel;
import org.lisaac.ldt.model.Position;
import org.lisaac.ldt.model.items.Prototype;

public class LisaacCompiler {

	protected String inputFile;
	protected String lipFile;

	protected ArrayList<String> options;

	protected LisaacModel model;

	protected String outputName;

	// for lisaac <input_file> <lip_file> [options]
	public LisaacCompiler(LisaacModel model, String inputFile, String outputName, String lipFile) {
		this.inputFile = inputFile;
		this.lipFile = lipFile;
		this.outputName = outputName;
		this.model = model;
	}

	// for lisaac <lip_file> --p
	public LisaacCompiler(String lipFile) {
		this.lipFile = lipFile;
	}

	public IContainer launchInConsole(final ILaunch launch, IProject project, IProgressMonitor monitor) throws CoreException {
		
		String prototypePath = model.getPathManager()
			.getFullPath(LisaacModel.extractPrototypeName(inputFile));
		
		if (prototypePath != null) {
			IPath location = new Path(prototypePath);
			IPath projectLocation = project.getLocation();
			IFile file = null;

			if (projectLocation.isPrefixOf(location)) {
				// the file is inside the workspace
				location = location.removeFirstSegments(projectLocation.segmentCount());
				file = project.getFile(location);
				
				IContainer container = file.getParent();
				if (LisaacLauncher.executeCommandInConsole(launch, monitor, container, this.toCommandLineArray(),this)) {
					return container;
				}
			}
		}
		return null;
	}

	public Process launch(IProject project, IProgressMonitor monitor) throws CoreException {
		return LisaacLauncher.executeCommand(monitor, project, this.toCommandLineArray());
	}

	public String[] toCommandLineArray() {
		ArrayList<String> cmd = new ArrayList<String>();

		cmd.add("lisaac ");
		if (lipFile != null) {
			cmd.add(lipFile);
		}
		if (inputFile != null) {
			cmd.add(inputFile);
		}
		if (options != null) {
			cmd.addAll(options);
		}
		return cmd.toArray(new String[cmd.size()]);
	}

	public String toCommandLine() {
		StringBuffer result = new StringBuffer();

		result.append("lisaac ");
		if (lipFile != null) {
			result.append(lipFile);
		}
		if (inputFile != null) {
			result.append(" "+inputFile);
		}
		if (options != null) {
			for (int i=0; i<options.size(); i++) {
				result.append(" "+options.get(i));
			}
		}
		return result.toString();
	}

	public void addOption(String option) {
		if (options == null) {
			options = new ArrayList<String>();
		}
		options.add(option);
	}

	public void moveFiles(IContainer output) throws CoreException {
		IProject project = model.getProject();

		// refresh the generated files
		output.refreshLocal(IResource.DEPTH_INFINITE, null);

		// move the generated files to bin/
		IFolder bin = project.getFolder("bin");
		if (! bin.exists()) {
			bin.create(false,true,null);
		}
		IPath generatedPath = new Path(outputName);
		IFile executable = output.getFile(generatedPath);
		if (! executable.exists()) {
			generatedPath = new Path(outputName+".exe");// FIXME platform-dependant
			executable = output.getFile(generatedPath);
		}
		if (executable.exists()) {
			IFile destFile = bin.getFile(generatedPath);
			if (destFile.exists()) {
				destFile.delete(true, null);
			}
			executable.move(bin.getFullPath().append(generatedPath), true, null);
		}
		moveFile(outputName+".o", output, bin);// FIXME .java, etc..
		moveFile(outputName+".c", output, bin);

		//
		project.refreshLocal(IResource.DEPTH_INFINITE, null); // TODO usefull? try to remove
		//

		model.setCompiled(true);
	}

	private void moveFile(String filename, IContainer origin, IContainer destination) throws CoreException {
		IPath path = new Path(filename);
		IFile originFile = origin.getFile(path);

		if (originFile.exists()) {
			IFile destFile = destination.getFile(path);
			if (destFile.exists()) {
				destFile.delete(true, null);
			}
			originFile.move(destination.getFullPath().append(path), true, null);
		}
	}

	public void cleanFiles() throws CoreException {
		IContainer bin = model.getProject().getFolder("bin");

		IFile executable = bin.getFile(new Path(outputName));
		if (executable.exists()) {
			executable.delete(true, null);
		}
		executable = bin.getFile(new Path(outputName+".exe"));// FIXME platform-dependant
		if (executable.exists()) {
			executable.delete(true, null);	
		}
		IFile file = bin.getFile(new Path(outputName+".o"));
		if (file.exists()) {
			file.delete(true, null);	
		}
		file = bin.getFile(new Path(outputName+".c")); // FIXME .java, etc..
		if (file.exists()) {
			file.delete(true, null);	
		}
	}

	public void showErrors(String output) throws CoreException {
		new CompilerOutputParser(output).run(model);
	}
}

class CompilerOutputParser extends AbstractLisaacParser {
	public CompilerOutputParser(String contents) {
		super(contents);
	}

	public void run(LisaacModel model) throws CoreException {
		int startOffset = source.indexOf("--");
		while (startOffset != -1) {
			setPosition(startOffset+2);

			if (! readCapIdentifier()) {
				break;
			}
			int line, column;
			String prototypeName;
			String msg;
			int severity = 0;

			if (lastString.compareTo("WARNING") == 0) {
				readWord("----------");
				severity = 1;
			} else if (lastString.compareTo("SYNTAX") == 0) {
				readWord("-----------");
				severity = 2;
			} else if (lastString.compareTo("SEMANTIC") == 0) {
				readWord("---------");
				severity = 2;
			}
			readLine();
			msg = new String(lastString);
			msg = msg.replace('\r', ' ');
			readWord("Line");
			readInteger();
			line = (int) lastInteger;
			readWord("column");
			readInteger();
			column = (int) lastInteger;
			readWord("in");
			readCapIdentifier();
			prototypeName = new String(lastString);
			
			// add marker to file
			Prototype prototype = model.getPrototype(prototypeName);
			if (prototype != null) {
				IFile file = prototype.getFile();
				IRegion region = prototype.getRegionAt(line, column);
				
				Position position = new Position(line, column, region.getOffset(), region.getLength());
				LisaacBuilder.addMarker(file, msg, position, severity);
			}
			startOffset = source.indexOf("--", position);
		}
	}
	private void readLine() {
		if (readSpace()) {
			string_tmp = "";
			while (lastCharacter() != 0 && lastCharacter() != '\n') {
				string_tmp += lastCharacter();
				if (lastCharacter() == '\\') {
					position = position+1;
					readEscapeCharacter();
				} else {
					position = position+1;
				}
			}
			position = position+1;
			lastString = string_tmp;
		}
	}
}
