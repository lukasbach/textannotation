package edu.kit.textannotation.annotationplugin;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPageListener;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;

import edu.kit.textannotation.annotationplugin.EventManager.EmptyEvent;
import edu.kit.textannotation.annotationplugin.editor.AnnotationTextEditor;

public class AnnotationEditorFinder {
	private IWorkbench workbench;
	
	public final EventManager<AnnotationTextEditor> annotationEditorActivated = new EventManager<AnnotationTextEditor>();
	public final EventManager<EventManager.EmptyEvent> annotationEditorDeactivated = new EventManager<EventManager.EmptyEvent>();
	
	public AnnotationEditorFinder(IWorkbench workbench) {
		this.workbench = workbench;
		workbench.getActiveWorkbenchWindow().getActivePage().addPartListener(new IPartListener2() {
			@Override public void partVisible(IWorkbenchPartReference partRef) {}					
			@Override public void partOpened(IWorkbenchPartReference partRef) {}					
			@Override public void partInputChanged(IWorkbenchPartReference partRef) {}					
			@Override public void partHidden(IWorkbenchPartReference partRef) {}					
			@Override public void partDeactivated(IWorkbenchPartReference partRef) {}					
			@Override public void partClosed(IWorkbenchPartReference partRef) {}					
			@Override public void partBroughtToTop(IWorkbenchPartReference partRef) {}					
			@Override public void partActivated(IWorkbenchPartReference partRef) {
				IEditorPart activeEditor = workbench.getActiveWorkbenchWindow().getActivePage().getActiveEditor();
				if (activeEditor instanceof AnnotationTextEditor) {
					// TODO save and check if editor was already selected and did not change
					annotationEditorActivated.fire((AnnotationTextEditor) activeEditor);
				} else {
					annotationEditorDeactivated.fire(new EventManager.EmptyEvent());
				}
			}
		});
	}
	
	public AnnotationTextEditor getAnnotationEditor() {
		IEditorPart activeEditor = workbench.getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		
		if (activeEditor instanceof AnnotationTextEditor) {
			return (AnnotationTextEditor) activeEditor;
		}
		
		return null;
	}
}
