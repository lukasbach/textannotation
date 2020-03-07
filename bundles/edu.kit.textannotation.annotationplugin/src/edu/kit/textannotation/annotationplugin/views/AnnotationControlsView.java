package edu.kit.textannotation.annotationplugin.views;

import java.util.Arrays;
import java.util.List;

import edu.kit.textannotation.annotationplugin.editor.AnnotationEditorFinder;
import edu.kit.textannotation.annotationplugin.selectionstrategy.DefaultSelectionStrategy;
import edu.kit.textannotation.annotationplugin.selectionstrategy.SelectionStrategy;
import edu.kit.textannotation.annotationplugin.selectionstrategy.SentenceSelectionStrategy;
import edu.kit.textannotation.annotationplugin.selectionstrategy.WordSelectionStrategy;
import edu.kit.textannotation.annotationplugin.utils.EclipseUtils;
import edu.kit.textannotation.annotationplugin.editor.AnnotationTextEditor;
import edu.kit.textannotation.annotationplugin.profile.AnnotationProfileRegistry;
import edu.kit.textannotation.annotationplugin.profile.ProfileNotFoundException;
import edu.kit.textannotation.annotationplugin.textmodel.InvalidAnnotationProfileFormatException;
import edu.kit.textannotation.annotationplugin.utils.LayoutUtilities;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.part.*;

import edu.kit.textannotation.annotationplugin.profile.AnnotationClass;
import edu.kit.textannotation.annotationplugin.profile.AnnotationProfile;
import edu.kit.textannotation.annotationplugin.textmodel.TextModelData;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.ui.*;
import org.eclipse.swt.SWT;

import javax.inject.Inject;

/**
 * The control view which is contributed to the plugin. See the plugins documentation for more details on
 * its contributing views.
 */
public class AnnotationControlsView extends ViewPart {
	public static final String ID = "edu.kit.textannotation.annotationplugin.views.AnnotationControlsView";
	private GridLayout layout;
	private AnnotationTextEditor editor;
	private AnnotationProfileRegistry registry;
	private AnnotationEditorFinder finder;
	private LayoutUtilities lu = new LayoutUtilities();
	private List<SelectionStrategy> selectionStrategies;
	private SelectionStrategy activeSelectionStrategy;

	@Inject IWorkbench workbench;
	
	@Override
	public void createPartControl(Composite parent) {
		// TODO maybe checking ever 10-or-so seconds for profile updates might not be a bad idea
		loadSelectionStrategies();
		finder = new AnnotationEditorFinder(workbench);
		finder.onAnnotationEditorActivated.addListener(editor -> rebuildContent(parent, editor.getTextModelData()));
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

	private void loadSelectionStrategies() {
		selectionStrategies = Arrays.asList(
				new DefaultSelectionStrategy(),
				new WordSelectionStrategy(),
				new SentenceSelectionStrategy()
		);
		activeSelectionStrategy = new DefaultSelectionStrategy();
	}
	
	private void rebuildContent(Composite parent, TextModelData textModelData) {
		if (parent.isDisposed()) {
			return;
		}

		editor = finder.getAnnotationEditor();
		registry = editor.getAnnotationProfileRegistry();
		List<AnnotationProfile> profiles;
		AnnotationProfile profile;

		try {
			profiles = registry.getProfiles();
			profile = registry.findProfile(textModelData.getProfileId());
		} catch (InvalidAnnotationProfileFormatException e) {
			EclipseUtils.reportError("Invalid profile format: " + e.getMessage());
			return;
		} catch (ProfileNotFoundException e) {
			EclipseUtils.reportError("Profile not found: " + e.getMessage());
			return;
		}

		EclipseUtils.clearChildren(parent);
		
		layout = new GridLayout(1, false);
		parent.setLayout(layout);

		Header
				.withTitle(profile.getName())
				.withButton("Change Profile", () -> {
					ElementListSelectionDialog dialog = new ElementListSelectionDialog(parent.getShell(), new LabelProvider());
					dialog.setTitle("Change Profile");
					dialog.setMessage("Change Profile for the current annotation text file");
					dialog.setElements(profiles.toArray());
					dialog.open();
					AnnotationProfile selectedProfile = (AnnotationProfile) dialog.getFirstResult();
					if (selectedProfile != null) {
						textModelData.setProfileId(selectedProfile.getId());
						try {
							editor.onProfileChange.fire(registry.findProfile(selectedProfile.getId()));
							editor.markDocumentAsDirty();
						} catch (ProfileNotFoundException | InvalidAnnotationProfileFormatException e) {
							EclipseUtils.reportError("Profile change error: " + e.getMessage());
						}
						rebuildContent(parent, textModelData);
					}

				})
				.withButton("Edit Profile", () -> {
					EditProfileDialog.openWindow(registry, textModelData.getProfileId(), p -> {
						rebuildContent(parent, textModelData);
						editor.onProfileChange.fire(p);
					}, null);
				})
				.render(parent);

		Label subtitleLabel = new Label(parent, SWT.WRAP | SWT.LEFT);
		subtitleLabel.setText(String.format("The annotatable text file currently uses the \"%s\" annotation profile, its ID is \"%s\".",
				profile.getName(), profile.getId()));
		subtitleLabel.setLayoutData(new GridData(SWT.HORIZONTAL, SWT.TOP, true, false, 1, 1));
		FontData[] fD = subtitleLabel.getFont().getFontData();
		fD[0].setHeight(10);
		subtitleLabel.setFont(new Font(Display.getDefault(), fD[0]));

		Header.withTitle("Selection Strategy").withSubTitle(activeSelectionStrategy.getDescription()).render(parent);
		Composite selectionStrategiesComposite = new Composite(parent, SWT.NONE);
		selectionStrategiesComposite.setLayoutData(lu.horizontalFillingGridData());
		selectionStrategiesComposite.setLayout(lu.fillLayout().withHorizontal().get());
		for (SelectionStrategy strategy : selectionStrategies) {
			Button strategyButton = new Button(selectionStrategiesComposite, SWT.PUSH);
			strategyButton.setEnabled(!strategy.getId().equals(activeSelectionStrategy.getId()));
			strategyButton.setText(strategy.getName());
			strategyButton.setToolTipText(strategy.getDescription());
			strategyButton.addListener(SWT.Selection, e -> {
				activeSelectionStrategy = strategy;
				rebuildContent(parent, textModelData);
			});
		}

		Header
			.withTitle("Annotation Classes")
			.withSubTitle("Click on an annotation class to annotate the selected text as the chosen annotation")
			.render(parent);

		for (AnnotationClass a: profile.getAnnotationClasses()) {
			Composite aclContainer = new Composite(parent, SWT.NONE);
			aclContainer.setLayout(lu.gridLayout().withNumCols(2).withEqualColumnWidth(false).get());
			aclContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

			StyledText colorDisplay = new StyledText(aclContainer, SWT.BORDER);

			Button button = new Button(aclContainer, SWT.PUSH | SWT.FILL);
			button.setText(a.getName());
			button.setToolTipText(a.getId());
			button.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			button.addListener(SWT.Selection, event -> {
				new AnnotationEditorFinder(workbench).getAnnotationEditor().annotate(a, activeSelectionStrategy);
			});

			colorDisplay.setLayoutData(lu.gridData().withWidthHint(12).withExcessVerticalSpace(true).get());
			colorDisplay.setEditable(false);
			colorDisplay.setBackground(a.getColor());
		}

		parent.layout();
	}
}
