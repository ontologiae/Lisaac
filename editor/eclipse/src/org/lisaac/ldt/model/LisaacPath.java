package org.lisaac.ldt.model;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.lisaac.ldt.launch.LisaacCompiler;
import org.lisaac.ldt.outline.OutlineImages;


public class LisaacPath {

	private HashMap<String,String> prototypesPath;

	private LisaacCompiler compiler;

	private String lipFile;


	public LisaacPath(final IProject project, String lipFile) {
		prototypesPath = new HashMap<String,String>();
		this.lipFile = lipFile;

		refreshPath(project);
	}

	public void refreshPath(final IProject project) {
		try {
			compiler = new LisaacCompiler(lipFile);
			compiler.addOption("--p");

			System.out.println("===> "+compiler.toCommandLine());

			try {
				final Process process = compiler.launch(project, new NullProgressMonitor());
				
				if (process != null) {
					try {
						// wait for end of process
						process.waitFor();
					} catch(InterruptedException e) {
					}
				}
				BufferedReader bufferIn = new BufferedReader(
						new InputStreamReader( 
								new FileInputStream(project.getLocation()+"/current_path.txt")));

				String line;
				while ((line = bufferIn.readLine()) != null) {
					createPath(line);
				}
				bufferIn.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (CoreException e) {
			//LisaacPlugin.log(status) // TODO log error
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getFullPath(String prototypeName) {
		if (prototypesPath.containsKey(prototypeName)) {
			return prototypesPath.get(prototypeName);
		}
		return null;
	}


	private void createPath(String fullPath) {
		int index = fullPath.lastIndexOf("/");
		String prototypeName = fullPath.substring(index+1);
		index = prototypeName.lastIndexOf(".");
		prototypeName = prototypeName.substring(0, index).toUpperCase();
		prototypesPath.put(prototypeName, fullPath);
	}


	public void getPathMatch(String prefix,
			ArrayList<ICompletionProposal> proposals, int baseOffset) {

		Collection<String> values = prototypesPath.keySet();
		Iterator<String> it = values.iterator() ;
		while (it.hasNext()) {
			String name = it.next();
			if (name.startsWith(prefix)) {
				int lenPrefix = prefix.length();
				int lenName = name.length();
				proposals.add(new CompletionProposal(name, baseOffset-lenPrefix, lenPrefix, lenName,
						OutlineImages.PROTOTYPE, name, null, null));
			}
		}
	}

	public void addPath(String prototypeName, IPath location) {
		prototypesPath.put(prototypeName, location.toString());
	}

	public int getSize() {
		return prototypesPath.size();
	}

	public Iterator<String> getPathIterator() {
		return prototypesPath.values().iterator();
	}
}
