package org.lisaac.ldt.launch;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorPart;

/**
 * Handle the 'Lisaac Application' run shortcut.
 */
public class LisaacApplicationShortcut implements ILaunchShortcut {

    public LisaacApplicationShortcut() {
    }
 
    public void launch(ISelection selection, String mode) {
        if(selection instanceof IStructuredSelection) {
            Object firstSelection = ((IStructuredSelection)selection).getFirstElement();
            if (firstSelection instanceof IFile) {
                ILaunchConfiguration config = findLaunchConfiguration((IFile)firstSelection, mode);
                try {
                    if(config != null)
                        config.launch(mode, null);
                }
                catch(CoreException coreexception) { }
            }
        }
    }

    public void launch(IEditorPart editor, String mode)
    {
		// make sure the file is saved
		editor.getEditorSite().getPage().saveEditor(editor,true);
        org.eclipse.ui.IEditorInput input = editor.getEditorInput();
        ISelection selection = new StructuredSelection(input.getAdapter(org.eclipse.core.resources.IFile.class));
        launch(selection, mode);
    }

    protected ILaunchConfiguration findLaunchConfiguration(IFile file, String mode) {
        ILaunchConfigurationType configType = getLaunchConfigType();
        List<ILaunchConfiguration> candidateConfigs = null;
        try {
            ILaunchConfiguration configs[] = getLaunchManager().getLaunchConfigurations(configType);
            candidateConfigs = new ArrayList<ILaunchConfiguration>(configs.length);
            for(int i = 0; i < configs.length; i++) {
                ILaunchConfiguration config = configs[i];
                
                if(config.getAttribute(LaunchConfigurationTab.LISAAC_LAUNCH_PROTOTYPE, "").equals(file.getFullPath().lastSegment()) &&
                		config.getAttribute(LaunchConfigurationTab.LISAAC_LAUNCH_PROJECT, "").equals(file.getProject().getFullPath().lastSegment()))
                    candidateConfigs.add(config);
            }
        }
        catch(CoreException coreexception) { }
        switch(candidateConfigs.size())
        {
        case 0: // '\0'
            return createConfiguration(file);

        case 1: // '\001'
        default:
            return (ILaunchConfiguration)candidateConfigs.get(0);
        }
    }

    protected ILaunchConfiguration createConfiguration(IFile file)
    {
        ILaunchConfiguration config = null;
        try
        {
            ILaunchConfigurationType configType = getLaunchConfigType();
            ILaunchConfigurationWorkingCopy wc = configType.newInstance(null, getLaunchManager().generateUniqueLaunchConfigurationNameFrom(file.getName()));
            wc.setAttribute(LaunchConfigurationTab.LISAAC_LAUNCH_PROJECT, file.getProject().getName());
            wc.setAttribute(LaunchConfigurationTab.LISAAC_LAUNCH_PROTOTYPE, file.getName());
            config = wc.doSave();
        }
        catch(CoreException coreexception) { }
        return config;
    }

    protected ILaunchConfigurationType getLaunchConfigType() {
        return getLaunchManager().getLaunchConfigurationType(LaunchConfiguration.TYPE_ID);
    }

    protected ILaunchManager getLaunchManager() {
        return DebugPlugin.getDefault().getLaunchManager();
    }
}
