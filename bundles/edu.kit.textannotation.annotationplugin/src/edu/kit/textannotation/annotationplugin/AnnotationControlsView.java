package edu.kit.textannotation.annotationplugin;

import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.part.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.*;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.SWT;
import org.eclipse.core.runtime.IAdaptable;
import javax.inject.Inject;


/**
 * This sample class demonstrates how to plug-in a new
 * workbench view. The view shows data obtained from the
 * model. The sample creates a dummy model on the fly,
 * but a real implementation would connect to the model
 * available either in this or another plug-in (e.g. the workspace).
 * The view is connected to the model using a content provider.
 * <p>
 * The view uses a label provider to define how model
 * objects should be presented in the view. Each
 * view can present the same model objects using
 * different labels and icons, if needed. Alternatively,
 * a single label provider can be shared between views
 * in order to ensure that objects of the same type are
 * presented in the same way everywhere.
 * <p>
 */

public class AnnotationControlsView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "edu.kit.textannotation.annotationplugin.AnnotationControlsView";
	private GridLayout layout;
	private Combo profileSelector;
	private Button ButtonMakeSubstantive;
	private Button ButtonMakeVerb;
	private Button ButtonMakeObjective;

	@Inject IWorkbench workbench;

	@Override
	public void createPartControl(Composite parent) {
		layout = new GridLayout(1, false);
		parent.setLayout(layout);

		profileSelector = new Combo(parent, SWT.DROP_DOWN | SWT.BORDER);
		ButtonMakeSubstantive = new Button(parent, SWT.PUSH | SWT.FILL);
		ButtonMakeVerb = new Button(parent, SWT.PUSH | SWT.FILL);
		ButtonMakeObjective = new Button(parent, SWT.PUSH | SWT.FILL);

        profileSelector.add("Requirements Engineering");
        profileSelector.add("Text Analysis");
        profileSelector.add("Add new profile...");

		ButtonMakeSubstantive.setText("Make Substantive");
		ButtonMakeVerb.setText("Make Verb");
		ButtonMakeObjective.setText("Make Objective");
		
		for (Control c: Arrays.asList(profileSelector, ButtonMakeSubstantive, ButtonMakeVerb, ButtonMakeObjective)) {
			c.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		}
	}

	@Override
	public void setFocus() {
	}
}
