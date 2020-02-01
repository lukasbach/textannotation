package edu.kit.textannotation.annotationplugin;

import java.util.Arrays;
import java.util.function.Consumer;

import edu.kit.textannotation.annotationplugin.editor.AnnotationTextEditor;
import edu.kit.textannotation.annotationplugin.profile.AnnotationProfileRegistry;
import edu.kit.textannotation.annotationplugin.profile.ProfileNotFoundException;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.part.*;

import edu.kit.textannotation.annotationplugin.profile.AnnotationClass;
import edu.kit.textannotation.annotationplugin.profile.AnnotationProfile;
import edu.kit.textannotation.annotationplugin.textmodel.TextModelData;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.ui.*;
import org.eclipse.swt.SWT;
import javax.inject.Inject;


public class AnnotationControlsView extends ViewPart {
	public static final String ID = "edu.kit.textannotation.annotationplugin.AnnotationControlsView";
	private GridLayout layout;
	private Combo profileSelector;
	private Button buttonEditProfile;
	private Button buttonNewProfile;
	private AnnotationTextEditor editor;
	private AnnotationProfileRegistry registry;
	private AnnotationEditorFinder finder;

	@Inject IWorkbench workbench;
	
	@Override
	public void createPartControl(Composite parent) {
		// TODO maybe checking ever 10-or-so seconds for profile updates might not be a bad idea
		finder = new AnnotationEditorFinder(workbench);
		finder.annotationEditorActivated.addListener(editor -> rebuildContent(parent, editor.getTextModelData()));
		if (finder.getAnnotationEditor() != null) {
			rebuildContent(parent, finder.getAnnotationEditor().getTextModelData());
		}
	}

	@Override
	public void setFocus() {
	}
	
	@Override
	public String getTitle() {
		return "Annotation Controls";
	}
	
	private void rebuildContent(Composite parent, TextModelData textModelData) {
		if (parent.isDisposed()) {
			return;
		}

		editor = finder.getAnnotationEditor();
		registry = editor.getAnnotationProfileRegistry();

		EclipseUtils.clearChildren(parent);
		
		layout = new GridLayout(1, false);
		parent.setLayout(layout);

		Composite selectorComposite = new Composite(parent, SWT.NONE);
		selectorComposite.setLayout(new GridLayout(3, false));

		profileSelector = new Combo(selectorComposite, SWT.DROP_DOWN | SWT.BORDER);
		registry.getProfiles().forEach(p -> profileSelector.add(p.getName()));
		profileSelector.select(registry.getProfiles().indexOf(new AnnotationProfile(textModelData.getProfileName())));
		ComboSelectionListener.create(profileSelector, (value) -> {
			textModelData.setProfileName(value);
			rebuildContent(parent, textModelData);
		});

		buttonEditProfile = new Button(selectorComposite, SWT.PUSH);
		buttonNewProfile = new Button(selectorComposite, SWT.PUSH);

		buttonEditProfile.setText("Edit Profile");
		buttonNewProfile.setText("New Profile");
		
		buttonEditProfile.addListener(SWT.Selection, event -> {
			EditProfileDialog.openWindow(registry, textModelData.getProfileName(), p -> rebuildContent(parent, textModelData));
		});
		
		for (Control c: Arrays.asList(selectorComposite, profileSelector)) {
			c.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		}

		try {
			AnnotationProfile profile = registry.findProfile(textModelData.getProfileName());

			for (AnnotationClass a: profile.getAnnotationClasses()) {
				Button b = new Button(parent, SWT.PUSH | SWT.FILL);
				b.setText(a.getName());
				b.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
				b.addListener(SWT.Selection, event -> new AnnotationEditorFinder(workbench).getAnnotationEditor().annotate(a));
			}
		} catch (ProfileNotFoundException e) {
			// TODO
			e.printStackTrace();
		}

		// TODO how to properly redraw parent s.t. widths are properly aligned?
		// parent.pack();
		parent.layout();
		// parent.redraw();
		// parent.update();
	}
}
