package edu.kit.textannotation.annotationplugin.editor;

import edu.kit.textannotation.annotationplugin.utils.EventManager;
import org.eclipse.jgit.annotations.Nullable;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPartReference;

/**
 * This utility allows arbitrary clients to receive a reference of the relevant annotation editor
 * ({@link AnnotationTextEditor}), given a reference on the workbench ({@link IWorkbench}). Clients can also register
 * for events when the relevant annotation editor reference has changed.
 *
 * A "relevant annotation editor" refers to the latest instance of an annotation editor that was activated by the user.
 */
public class AnnotationEditorFinder {
	private IWorkbench workbench;
	private String activeEditorId;
	private AnnotationTextEditor lastEditor;

	/** Fires when a text editor was activated. The payload references the activated editor. */
	public final EventManager<AnnotationTextEditor> onAnnotationEditorActivated = new EventManager<>("editor activated");

	/** Fires when a text editor was deactivated. The payload references the deactivated editor. */
	public final EventManager<AnnotationTextEditor> onAnnotationEditorDeactivated = new EventManager<>("editor deactivated");

	/**
	 * Create a new finder instance. The hooks {@see onAnnotationEditorActivated} and {@see onAnnotationEditorDeactivated}
	 * can be used to get notified by change events.
	 * @param workbench the workbench of the eclipse instance.
	 */
	public AnnotationEditorFinder(IWorkbench workbench) {
		this.workbench = workbench;
		this.activeEditorId = "";

		AnnotationTextEditor editor = getAnnotationEditor();
		if (editor != null) {
			onAnnotationEditorActivated.fire(editor);
		}

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
					if (activeEditorId.equals(editorId)) {
						onAnnotationEditorActivated.fire((AnnotationTextEditor) activeEditor);
						activeEditorId = editorId;
						lastEditor = (AnnotationTextEditor) activeEditor;
					}
				} else if (lastEditor != null) {
					onAnnotationEditorDeactivated.fire(lastEditor);
				}
			}
		});
	}

	/**
	 * Attempt to receive a reference to a relevant annotation editor.
	 * @return the reference to a relevant annotation editor, or null if no editor could be found.
	 */
	@Nullable public AnnotationTextEditor getAnnotationEditor() {
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
