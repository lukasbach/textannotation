package edu.kit.textannotation.annotationplugin.utils;

import edu.kit.textannotation.annotationplugin.PluginConfig;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.*;
import org.eclipse.ui.statushandlers.StatusManager;
import org.eclipse.ui.wizards.IWizardDescriptor;
import org.osgi.framework.Bundle;

public class EclipseUtils {
    public static void clearChildren(Composite parent) {
        for (Control child : parent.getChildren()) {
            child.dispose();
        }
    }

    public static void reportError(String message) {
        StatusManager.getManager().handle(new Status(IStatus.ERROR, PluginConfig.PLUGIN_ID, message),
                StatusManager.SHOW);
    }

    public static IFile getFileForEditor(IEditorPart editor) {
        IFile file = null;
        if (editor != null
                && editor.getEditorInput() instanceof IFileEditorInput) {
            IFileEditorInput input = (IFileEditorInput) editor.getEditorInput();
            file = input.getFile();
        }
        return file;
    }

    public static String getCurrentWorkspaceDirectory(Bundle bundle) {
        IPath bundlePath = Platform.getStateLocation(bundle); // workspace/.metadata/.plugins/.textannotation

        String[] pieces = bundlePath.toString().split("" + Path.SEPARATOR);
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < pieces.length - 3; i++) {
            result.append(pieces[i]).append(Path.SEPARATOR);
        }

        return result.substring(0, result.length() - 1);
    }

    public static void openWizard(String id) {
        // https://resheim.net/2010/07/invoking-eclipse-wizard.html

        // First see if this is a "new wizard".
        IWizardDescriptor descriptor = PlatformUI.getWorkbench()
                .getNewWizardRegistry().findWizard(id);
        // If not check if it is an "import wizard".
        if (descriptor == null) {
            descriptor = PlatformUI.getWorkbench().getImportWizardRegistry()
                    .findWizard(id);
        }
        // Or maybe an export wizard
        if (descriptor == null) {
            descriptor = PlatformUI.getWorkbench().getExportWizardRegistry()
                    .findWizard(id);
        }
        try {
            // Then if we have a wizard, open it.
            if (descriptor != null) {
                IWizard wizard = descriptor.createWizard();
                WizardDialog wd = new WizardDialog(
                        Display.getDefault().getActiveShell(), wizard);
                wd.setTitle(wizard.getWindowTitle());
                wd.open();
            }
        } catch (CoreException e) {
            e.printStackTrace();
        }
    }
}
