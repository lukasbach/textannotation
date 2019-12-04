package edu.kit.textannotation.annotationplugin;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPageListener;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;

import edu.kit.textannotation.annotationplugin.editor.AnnotationTextEditor;

public class AnnotationEditorFinder {
	private IWorkbench workbench;
	private IPageListener pageListener;
	private IWorkbenchWindow oldWindow;
	
	public final EventManager<AnnotationTextEditor> annotationEditorActivated = new EventManager<AnnotationTextEditor>();
	
	public AnnotationEditorFinder(IWorkbench workbench) {
		this.workbench = workbench;
		workbench.addWindowListener(new IWindowListener() {
			@Override public void windowOpened(IWorkbenchWindow window) {}			
			@Override public void windowDeactivated(IWorkbenchWindow window) {}			
			@Override public void windowClosed(IWorkbenchWindow window) {}			
			@Override public void windowActivated(IWorkbenchWindow window) {
				onWindowChange(window);
			}
		});
	}
	
	public AnnotationTextEditor getAnnotationEditor() {
		IEditorPart part = workbench.getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		
		if (part instanceof AnnotationTextEditor) {
			return (AnnotationTextEditor) part;
		}
		
		return null;
	}
	
	private void onWindowChange(IWorkbenchWindow window) {
		System.out.println("ONWINDOWCHANGE");
		if (pageListener != null && oldWindow != null) {
			oldWindow.removePageListener(pageListener);
		}
		oldWindow = window;
		
		if (window.getActivePage().getActiveEditor() instanceof AnnotationTextEditor) {
			annotationEditorActivated.fire((AnnotationTextEditor) window.getActivePage().getActiveEditor());
		}
		
		pageListener = new IPageListener() {
			@Override public void pageOpened(IWorkbenchPage page) {}			
			@Override public void pageClosed(IWorkbenchPage page) {}			
			@Override public void pageActivated(IWorkbenchPage page) {
				System.out.println("ONPAGECHANGE");
				// TODO does not properly work
				if (page.getActiveEditor() instanceof AnnotationTextEditor) {
					annotationEditorActivated.fire((AnnotationTextEditor) page.getActiveEditor());
				}
			}
		};
		
		window.addPageListener(pageListener);
	}
}
