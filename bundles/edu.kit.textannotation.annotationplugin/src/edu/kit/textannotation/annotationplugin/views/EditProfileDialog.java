package edu.kit.textannotation.annotationplugin.views;

import edu.kit.textannotation.annotationplugin.profile.ProfileNotFoundException;
import edu.kit.textannotation.annotationplugin.textmodel.InvalidAnnotationProfileFormatException;
import edu.kit.textannotation.annotationplugin.utils.ComboSelectionListener;
import edu.kit.textannotation.annotationplugin.utils.EclipseUtils;
import edu.kit.textannotation.annotationplugin.utils.EventManager;
import edu.kit.textannotation.annotationplugin.utils.LayoutUtilities;
import edu.kit.textannotation.annotationplugin.profile.AnnotationProfileRegistry;
import edu.kit.textannotation.annotationplugin.wizards.ProfileWizard;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.*;

import edu.kit.textannotation.annotationplugin.profile.AnnotationClass;
import edu.kit.textannotation.annotationplugin.profile.AnnotationProfile;

import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.ui.wizards.IWizardDescriptor;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.function.Consumer;

/**
 * This class specifies a dialog where a annotation profile can be edited. The profile must exist on disk
 * and must be discoverable by the {@link AnnotationProfileRegistry}.
 * The dialog can be opened with the {@link EditProfileDialog::openWindow} method.
 */
public class EditProfileDialog extends Shell {
	private AnnotationProfileRegistry registry;
	private AnnotationProfile profile;
	private java.util.List<AnnotationProfile> allProfiles;
	private AnnotationClass selectedAnnotationClass;
	private Runnable onSave;
	private LayoutUtilities lu = new LayoutUtilities();

	private CCombo colorSelector;
	private StyledText colorDisplay;
	private Text itemName;
	private List annotationClassesList;

	private int newClassNameCounter = 1;

	private String[] defaultColors = new String[] {
			"46, 204, 113",
			"52, 152, 219",
			"155, 89, 182",
			"231, 76, 60",
			"230, 126, 34",
			"243, 156, 18",
			"241, 196, 15"
	};

	/**
	 * Open a new edit profile dialog.
	 * @param registry an annotation profile registry which is used to resolve the profile class.
	 * @param profileName the name of the profile which is edited. The profile registry is used to resolve the profile.
	 * @param onProfileChange a handler that is called when the profile is changed from within the editor, with the
	 *                        changed profile data as payload.
	 */
	public static void openWindow(AnnotationProfileRegistry registry, String profileName,
								  Consumer<AnnotationProfile> onProfileChange) {
		try {
			Display display = PlatformUI.getWorkbench().getDisplay();
			EditProfileDialog shell = new EditProfileDialog(display, registry, profileName, p -> {
				registry.overwriteProfile(p);
				onProfileChange.accept(p);
			});
			shell.open();
			shell.layout();
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
		} catch (Exception e) {
			EclipseUtils.reportError(e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog shell.
	 * @param display the display which is used for the dialog
	 * @param editingProfile the name of the profile being edited
	 * @param registry the profile registry to resolve the profile information and save changes back to disk
	 * @param onSave a change handler that is invoked when the profile is changed, with the changed profile as payload
	 */
	public EditProfileDialog(Display display, AnnotationProfileRegistry registry,
							 String editingProfile, Consumer<AnnotationProfile> onSave) {
		super(display, SWT.SHELL_TRIM);

		this.registry = registry;

		reloadProfiles(editingProfile);

		this.selectedAnnotationClass = null;
		this.onSave = () -> onSave.accept(profile);

		createContents();
		rebuildContent(this);
	}

	private void reloadProfiles(String editingProfile) {
		try {
			profile = registry.findProfile(editingProfile);
			allProfiles = registry.getProfiles();
		} catch (ProfileNotFoundException | InvalidAnnotationProfileFormatException e) {
			EclipseUtils.reportError(e.getMessage());
		}
	}

	private void rebuildContent(Composite parent) {
		EclipseUtils.clearChildren(parent);

		setText(String.format("Editing Profile \"%s\"", profile.getName()));

		parent.setLayout(lu.gridLayout().withNumCols(1).get());

		Composite topContainer = new Composite(parent, SWT.NONE);
		topContainer.setLayout(lu.gridLayout().withNumCols(2).withEqualColumnWidth(false).get());

		Combo profileSelector = new Combo(topContainer, SWT.DROP_DOWN | SWT.BORDER);
		allProfiles.forEach(p -> profileSelector.add(p.getName()));
		profileSelector.select(allProfiles.indexOf(new AnnotationProfile(profile.getName())));
		ComboSelectionListener.create(profileSelector, (value) -> {
			allProfiles
				.stream()
				.filter(p -> p.getName().equals(value))
				.findAny()
				.ifPresent(newProfile -> {
					profile = newProfile;
					rebuildContent(parent);
				});
		});

		Button buttonNewProfile = new Button(topContainer, SWT.PUSH);
		buttonNewProfile.setText("New Profile");
		buttonNewProfile.addListener(SWT.Selection, e -> {
			close();
			String wizardId = "edu.kit.textannotation.annotationplugin.wizards.ProfileWizard";
			EclipseUtils.openWizard(wizardId); // openwizard is blocking
			// reloadProfiles(profile.getName());
			// rebuildContent(parent);
		});

		Composite bottomContainer = new Composite(parent, SWT.NONE);
		bottomContainer.setLayoutData(lu.completelyFillingGridData());
		bottomContainer.setLayout(lu.gridLayout().withNumCols(2).withEqualColumnWidth(false).get());

		Composite leftContainer = new Composite(bottomContainer, SWT.NONE);
		leftContainer.setLayout(lu.gridLayout().withNumCols(1).get());
		leftContainer.setLayoutData(lu.completelyFillingGridData());

		Composite rightContainer = new Composite(bottomContainer, SWT.NONE);
		rightContainer.setLayout(lu.gridLayout().withNumCols(1).get());
		rightContainer.setLayoutData(lu.verticallyFillingGridData());

		annotationClassesList = new List(leftContainer, SWT.BORDER);
		annotationClassesList.setLayoutData(lu.completelyFillingGridData());
		annotationClassesList.setItems(profile.getAnnotationClassNames());
		annotationClassesList.addListener(SWT.Selection, e -> selectAnnotationClass(annotationClassesList.getSelection()[0]));

		Label seperator;

		if (selectedAnnotationClass != null) {
			itemName = new Text(rightContainer, SWT.BORDER);
			itemName.setLayoutData(lu.horizontalFillingGridData());
			itemName.setText("Selected Item Name");
			itemName.addModifyListener(e -> changeAnnotationClassName(itemName.getText()));

			Composite colorSelectorContainer = new Composite(rightContainer, SWT.NONE);
			colorSelectorContainer.setLayoutData(lu.horizontalFillingGridData());
			colorSelectorContainer.setLayout(lu.gridLayout().withNumCols(2).withEqualColumnWidth(false).get());

			colorSelector = new CCombo(colorSelectorContainer, SWT.BORDER);
			colorSelector.setLayoutData(lu.horizontalFillingGridData());
			colorSelector.setText("#abcdef");
			colorSelector.setLayoutData(lu.gridData().withExcessHorizontalSpace(true).withHorizontalAlignment(SWT.FILL).get());
			colorSelector.addModifyListener(e -> changeAnnotationColor(colorSelector.getText()));
			colorSelector.setItems(defaultColors);

			colorDisplay = new StyledText(colorSelectorContainer, SWT.BORDER);
			// colorDisplay.setBackground(SWTResourceManager.getColor(SWT.COLOR_DARK_GREEN));
			int i = colorSelector.getItemHeight();
			colorDisplay.setLayoutData(lu.gridData().withWidthHint(i).withHeightHint(i).get());
			colorDisplay.setEditable(false);

			seperator = new Label(rightContainer, SWT.SEPARATOR | SWT.HORIZONTAL);
			seperator.setLayoutData(lu.horizontalFillingGridData());

			Label metaDataInfo = new Label(rightContainer, SWT.NONE);
			metaDataInfo.setText(String.format("%s meta data entries", selectedAnnotationClass.metaData.size()));
			metaDataInfo.setLayoutData(lu.horizontalFillingGridData());

			Button editMetaData = new Button(rightContainer, SWT.NONE);
			editMetaData.setLayoutData(lu.horizontalFillingGridData());
			editMetaData.setText("Edit meta data");
			editMetaData.addListener(SWT.Selection, e -> editAnnotationMetaData(selectedAnnotationClass));

			Button editDescription = new Button(rightContainer, SWT.NONE);
			editDescription.setLayoutData(lu.horizontalFillingGridData());
			editDescription.setText("Edit Description");
			editDescription.addListener(SWT.Selection, e -> {
				InputDialog dialog = new InputDialog(getShell(), "Edit Description",
						"Edit Annotation Class Description", selectedAnnotationClass.getDescription(),
						t -> null);

				if (dialog.open() == Window.OK) {
					selectedAnnotationClass.setDescription(dialog.getValue());
					onSave.run();
				}
			});

			seperator = new Label(rightContainer, SWT.SEPARATOR | SWT.HORIZONTAL);
			seperator.setLayoutData(lu.horizontalFillingGridData());

			Button btnRemoveClass = new Button(rightContainer, SWT.NONE);
			btnRemoveClass.setLayoutData(lu.horizontalFillingGridData());
			btnRemoveClass.setText("Remove selected Class");
			btnRemoveClass.addListener(SWT.Selection, e -> removeCurrentClass());

			seperator = new Label(rightContainer, SWT.SEPARATOR | SWT.HORIZONTAL);
			seperator.setLayoutData(lu.horizontalFillingGridData());
		}

		Button btnAddClass = new Button(rightContainer, SWT.NONE);
		btnAddClass.setLayoutData(lu.horizontalFillingGridData());
		btnAddClass.setText("Add Class");
		btnAddClass.addListener(SWT.Selection, e -> addNewClass());

		Button btnSave = new Button(rightContainer, SWT.NONE);
		btnSave.setLayoutData(lu.completelyFillingGridData());
		btnSave.setText("Save");
		btnSave.addListener(SWT.Selection, e -> {
			onSave.run();
			close();
		});

		parent.layout();
	}

	private void createContents() {
		setText("Edit Profile");
		setSize(550, 400);
		setMinimumSize(550, 400);
	}

	private void selectAnnotationClass(String annotationClassName) {
		try {
			selectedAnnotationClass = profile.getAnnotationClass(annotationClassName);
			rebuildContent(this);
			colorDisplay.setBackground(selectedAnnotationClass.getColor());
			colorSelector.setText(selectedAnnotationClass.getColorAsTextModelString());
			itemName.setText(annotationClassName);
		} catch (Exception e) {
			// TODO
			e.printStackTrace();
		}
	}

	private void changeAnnotationClassName(String name) {
		selectedAnnotationClass.setName(name);
		annotationClassesList.setItems(profile.getAnnotationClassNames());
		annotationClassesList.setSelection(new String[]{ name });
	}

	private void changeAnnotationColor(String colorString) {
		try {
			Integer[] colorInts = Arrays.stream(colorString.split(", "))
					.map(Integer::parseInt).toArray(Integer[]::new);
			Color color = new Color(Display.getCurrent(), colorInts[0], colorInts[1], colorInts[2]);
			selectedAnnotationClass.setColor(color);
			colorDisplay.setBackground(color);
		} catch (Exception e) {
			System.out.println("Could not parse color: " + colorString);
			// If something goes wrong, the colors is probably incorrectly formatted. Just attempt reading it on
			// the next input
		}
	}

	private void removeCurrentClass() {
		profile.removeAnnotationClass(selectedAnnotationClass);
		selectedAnnotationClass = null;
		annotationClassesList.setItems(profile.getAnnotationClassNames());
		annotationClassesList.deselectAll();
		rebuildContent(this);
	}

	private void addNewClass() {
		Color defaultColor = new Color(Display.getCurrent(), 52, 152, 219);
		AnnotationClass newClass = new AnnotationClass("New Annotation Class " + newClassNameCounter++, defaultColor);
		profile.addAnnotationClass(newClass);
		annotationClassesList.setItems(profile.getAnnotationClassNames());
		annotationClassesList.setSelection(new String[]{newClass.getName()});
		selectAnnotationClass(newClass.getName());
	}

	private void editAnnotationMetaData(AnnotationClass annotationClass) {
		Shell editWindow = new Shell(Display.getCurrent());
		editWindow.open();
		editWindow.setLayout(lu.gridLayout().withNumCols(1).get());

		EventManager<EventManager.EmptyEvent> relayout = new EventManager<>();
		Composite contentContainer = lu.createVerticalScrollComposite(editWindow, relayout);

		Header.withTitle("Edit Annotation Class Metadata")
				.withSubTitle("You are editing the metadata for the annotation class " + annotationClass.getName())
				.render(contentContainer);

		MetaDataView mdview = new MetaDataView(contentContainer, annotationClass.metaData, true, true, true, true);
		mdview.onChangedMetaData.addListener(e -> {
			onSave.run();

			// Refresh edit window
			selectAnnotationClass(selectedAnnotationClass.getName());
		});
		mdview.onShouldResize.addListener(e -> {
			relayout.fire(new EventManager.EmptyEvent());

			contentContainer.layout();
			editWindow.layout();
		});

		relayout.fire(new EventManager.EmptyEvent());
		editWindow.pack();
		contentContainer.pack();
		contentContainer.layout();
		editWindow.layout();

		editWindow.setActive();
		editWindow.setMinimumSize(400, 200);
		editWindow.setSize(400, 280);
		editWindow.setText("Edit metadata");
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
