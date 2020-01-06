package edu.kit.textannotation.annotationplugin;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.internal.ui.packageview.PackageFragmentRootContainer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.Workbench;

public class EclipseUtils {
    public static IPath getCurrentProjectDirectory() {
        // https://stackoverflow.com/a/6895220/2692307
        /*IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window != null) {
            IStructuredSelection selection = (IStructuredSelection) window.getSelectionService().getSelection();
            Object firstElement = selection.getFirstElement();
            if (firstElement instanceof IAdaptable)
            {
                IProject project = (IProject)((IAdaptable)firstElement).getAdapter(IProject.class);
                IPath path = project.getLocation();
                return path;
            }
        }

        return null;*/

        // https://stackoverflow.com/a/17190882/2692307
        ISelectionService selectionService =
                Workbench.getInstance().getActiveWorkbenchWindow().getSelectionService();

        ISelection selection = selectionService.getSelection();

        IProject project = null;
        if(selection instanceof IStructuredSelection) {
            Object element = ((IStructuredSelection)selection).getFirstElement();

            if (element instanceof IResource) {
                project= ((IResource)element).getProject();
            } else if (element instanceof PackageFragmentRootContainer) {
                IJavaProject jProject =
                        ((PackageFragmentRootContainer)element).getJavaProject();
                project = jProject.getProject();
            } else if (element instanceof IJavaElement) {
                IJavaProject jProject= ((IJavaElement)element).getJavaProject();
                project = jProject.getProject();
            }
        }
        return project.getLocation().makeAbsolute();
    }
}
