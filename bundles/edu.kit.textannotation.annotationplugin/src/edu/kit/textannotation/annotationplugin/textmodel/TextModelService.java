package edu.kit.textannotation.annotationplugin.textmodel;

import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.ITextListener;
import org.eclipse.jface.text.TextEvent;
import org.eclipse.jface.text.source.ISourceViewer;

public class TextModelService {
	private ISourceViewer sourceViewer;
	
	public TextModelService(ISourceViewer sourceViewer) {
		this.sourceViewer = sourceViewer;
		
		this.setupEditorIntegration();
	}
	
	private void setupEditorIntegration() {
		sourceViewer.addTextListener(new ITextListener() {	
			@Override
			public void textChanged(TextEvent event) {

				// sv.setVisibleRegion(2, 5);
				System.out.println(event.getText() + event.getOffset() + ":" + event.getLength());
				
			}
		});
	}
}
