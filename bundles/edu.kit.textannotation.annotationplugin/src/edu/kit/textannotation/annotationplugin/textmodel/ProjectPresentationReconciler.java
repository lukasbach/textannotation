	package edu.kit.textannotation.annotationplugin.textmodel;

import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

import edu.kit.textannotation.annotationplugin.profile.AnnotationClass;
import edu.kit.textannotation.annotationplugin.profile.AnnotationProfile;

import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.presentation.IPresentationDamager;
import org.eclipse.jface.text.presentation.IPresentationRepairer;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.TextViewer;

public class ProjectPresentationReconciler extends PresentationReconciler {

    private final TextAttribute tagAttribute = new TextAttribute(new Color(Display.getCurrent(), new RGB(0,0, 255)));
    private final TextAttribute headerAttribute = new TextAttribute(new Color(Display.getCurrent(), new RGB(128,128,128)));
    private final TextAttribute invisbleAttribute = new TextAttribute(new Color(Display.getCurrent(), new RGB(128,128,128)));
    
    private class CustomDamagerRepairer implements IPresentationDamager, IPresentationRepairer  {
    	private IDocument document;
    	private IPresentationDamager damager = this;
    	private IPresentationRepairer repairer = this;
    	private TextModelParser parser;
    	
		@Override
		public void setDocument(IDocument document) {
			System.out.println("Reset document");
			this.document = document;
			this.parser = new TextModelParser(document);
		}

		@Override
		public IRegion getDamageRegion(ITypedRegion partition, DocumentEvent event,
				boolean documentPartitioningChanged) {
			/*
			 * event.getOffset: total offset where change happened
			 * event.getLength: 
			 */
			
			// System.out.println("offset" + event.getOffset() + " length" + event.getLength() + " text" + event.getText() + " plen" + partition.getLength() + " poff" + partition.getOffset()); 
			return partition;
 		}

		@Override
		public void createPresentation(TextPresentation presentation, ITypedRegion damage) {
			AnnotationProfile profile = parser.parseAnnotationProfile();
			SingleAnnotation[] annotations = parser.parseAnnotationData();
			
			for (SingleAnnotation an: annotations) {
				try {
					AnnotationClass ac = profile.getAnnotationClass(an.getAnnotationIdentifier());
					presentation.addStyleRange(new StyleRange(
							an.getOffset(), 
							an.getLength(), 
							ac.getColor(), 
							new Color(Display.getCurrent(), new RGB(255, 255, 255))
							));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			
			System.out.println(
					"offset=" + damage.getOffset() +  
					"length=" + damage.getLength() + 
					"type=" + damage.getType()
					);
			if (damage.getLength() > 10) {
				// presentation.addStyleRange(new StyleRange(2, 3, new Color(Display.getCurrent(), new RGB(0,0, 255)), new Color(Display.getCurrent(), new RGB(0,0, 50))));
			}
			
		}
    	
    }
    
    

    public ProjectPresentationReconciler() {
        // TODO this is logic for .project file to color tags in blue. Replace with your language logic!
    	// RuleBasedScanner scanner= new RuleBasedScanner();
    	// IRule[] rules = new IRule[3];
    	// rules[1]= new SingleLineRule("<", ">", new Token(tagAttribute));
    	// rules[0]= new SingleLineRule("<?", "?>", new Token(headerAttribute));
    	// rules[2]= new MultiLineRule("##1##", "##/1##", new Token(tagAttribute));
    	// scanner.setRules(rules);
    	// DefaultDamagerRepairer dr= new DefaultDamagerRepairer(scanner);
    	// this.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
    	// this.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);
        
        CustomDamagerRepairer dr = new CustomDamagerRepairer();
        this.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
        this.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);
    }
}