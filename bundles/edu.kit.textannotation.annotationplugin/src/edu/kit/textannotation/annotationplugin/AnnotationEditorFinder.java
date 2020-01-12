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
	private String activeEditorId;
	private AnnotationTextEditor lastEditor;
	
	public final EventManager<AnnotationTextEditor> annotationEditorActivated = new EventManager<AnnotationTextEditor>("editor activated");
	public final EventManager<AnnotationTextEditor> annotationEditorDeactivated = new EventManager<AnnotationTextEditor>("editor deactivated");
	
	public AnnotationEditorFinder(IWorkbench workbench) {
		this.workbench = workbench;
		this.activeEditorId = "";
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
					String editorId = ((AnnotationTextEditor) activeEditor).getId();
					if (!activeEditorId.equals(editorId)) {
						annotationEditorActivated.fire((AnnotationTextEditor) activeEditor);
						activeEditorId = editorId;
						lastEditor = (AnnotationTextEditor) activeEditor;
					}
				} else if (lastEditor != null) {
					annotationEditorDeactivated.fire(lastEditor);
				}
			}
		});
	}
	
	public AnnotationTextEditor getAnnotationEditor() {
		IEditorPart activeEditor = workbench
				.getActiveWorkbenchWindow()
				.getActivePage()
				.getActiveEditor();
		
		if (activeEditor instanceof AnnotationTextEditor) {
			return (AnnotationTextEditor) activeEditor;
		}
		
		return null;
	}
}
