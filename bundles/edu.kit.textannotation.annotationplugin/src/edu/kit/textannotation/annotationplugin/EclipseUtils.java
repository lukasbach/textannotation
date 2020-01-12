package edu.kit.textannotation.annotationplugin;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.*;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.internal.ui.packageview.PackageFragmentRootContainer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.*;
import org.eclipse.ui.internal.Workbench;
import org.osgi.framework.Bundle;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

public class EclipseUtils {
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

        // System.out.println(Arrays.stream(bundlePath.segments())
        //         .sorted(Collections.reverseOrder())
        //         .skip(2)
        //         .sorted(Collections.reverseOrder())
        //         .collect(Collectors.joining("" + Path.SEPARATOR)));
        // return Arrays.stream(bundlePath.segments())
        //         .sorted(Collections.reverseOrder())
        //         .skip(2)
        //         .sorted(Collections.reverseOrder())
        //         .collect(Collectors.joining("" + Path.SEPARATOR));
        // IPath result = new Path("");
//
        // System.out.println("!!!!!!!!!");
        // System.out.println(bundlePath);
        // System.out.println(openFilePath);
//
        // for (int i = 0; i < openFilePath.segmentCount(); i++) {
        //     if (bundlePath.segment(i).equals(openFilePath.segment(i))) {
        //         result.append(bundlePath.segment(i) + Path.SEPARATOR);
        //     }
        // }
//
        // System.out.println(result);
        // System.out.println("!!!!!!!!!");
//
        // return result.toString();
    }

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
