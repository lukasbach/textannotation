package edu.kit.textannotation.annotationplugin.views;

import edu.kit.textannotation.annotationplugin.profile.ProfileNotFoundException;
import edu.kit.textannotation.annotationplugin.textmodel.InvalidAnnotationProfileFormatException;
import edu.kit.textannotation.annotationplugin.utils.ComboSelectionListener;
import edu.kit.textannotation.annotationplugin.utils.EclipseUtils;
import edu.kit.textannotation.annotationplugin.utils.EventManager;
import edu.kit.textannotation.annotationplugin.utils.LayoutUtilities;
import edu.kit.textannotation.annotationplugin.profile.AnnotationProfileRegistry;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.jgit.annotations.Nullable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.*;

import edu.kit.textannotation.annotationplugin.profile.AnnotationClass;
import edu.kit.textannotation.annotationplugin.profile.AnnotationProfile;

import org.eclipse.ui.PlatformUI;

import java.util.Arrays;
import java.util.UUID;
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

	private Button colorSelector;
	private Text itemName;
	private List annotationClassesList;

	private int newClassNameCounter = 1;

	/**
	 * Open a new edit profile dialog.
	 * @param registry an annotation profile registry which is used to resolve the profile class.
	 * @param profileId the ID of the profile which is edited. The profile registry is used to resolve the profile.
	 * @param onProfileChange a handler that is called when the profile is changed from within the editor, with the
	 *                        changed profile data as payload.
	 * @param selectedAnnotationClass the initially selected annotation class, or null if no annotation class should
	 *                                be selected from the start.
	 */
	public static void openWindow(AnnotationProfileRegistry registry,
								  String profileId,
								  Consumer<AnnotationProfile> onProfileChange,
								  @Nullable String selectedAnnotationClass) {
		try {
			Display display = PlatformUI.getWorkbench().getDisplay();
			EditProfileDialog shell = new EditProfileDialog(display, registry, profileId, p -> {
				registry.overwriteProfile(p);
				onProfileChange.accept(p);
			});

			if (selectedAnnotationClass != null) {
				shell.selectAnnotationClass(selectedAnnotationClass);
			}

			shell.open();
			shell.layout();
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
		} catch (Exception e) {
			EclipseUtils.logger().error(e);
			EclipseUtils.reportError(e.getMessage());
		}
	}

	/**
	 * Create the dialog shell.
	 * @param display the display which is used for the dialog
	 * @param editingProfileId the ID of the profile being edited
	 * @param registry the profile registry to resolve the profile information and save changes back to disk
	 * @param onSave a change handler that is invoked when the profile is changed, with the changed profile as payload
	 */
	public EditProfileDialog(Display display, AnnotationProfileRegistry registry,
							 String editingProfileId, Consumer<AnnotationProfile> onSave) {
		super(display, SWT.SHELL_TRIM);

		this.registry = registry;

		reloadProfiles(editingProfileId);

		this.selectedAnnotationClass = null;
		this.onSave = () -> onSave.accept(profile);

		createContents();
		rebuildContent(this);
	}

	private void reloadProfiles(String editingProfileId) {
		try {
			profile = registry.findProfile(editingProfileId);
			allProfiles = registry.getProfiles();
		} catch (ProfileNotFoundException | InvalidAnnotationProfileFormatException e) {
			EclipseUtils.reportError(e.getMessage());
		}
	}

	private void rebuildContent(Composite parent) {
		EclipseUtils.clearChildren(parent);

		setText(String.format("Editing Profile \"%s\"", profile.getId()));

		parent.setLayout(lu.gridLayout().withNumCols(1).get());

		Composite topContainer = new Composite(parent, SWT.NONE);
		topContainer.setLayout(lu.gridLayout().withNumCols(2).withEqualColumnWidth(false).get());

		new ProfileSelectorCombo(topContainer, allProfiles, (profile) -> {
			this.profile = profile;
			rebuildContent(parent);
		}, profile.getId());

		Button buttonNewProfile = new Button(topContainer, SWT.PUSH);
		buttonNewProfile.setText("New Profile");
		buttonNewProfile.addListener(SWT.Selection, e -> {
			close();
			String wizardId = "edu.kit.textannotation.annotationplugin.wizards.ProfileWizard";
			EclipseUtils.openWizard(wizardId);
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
		annotationClassesList.addListener(SWT.Selection, e -> selectAnnotationClass(annotationClassesList.getSelectionIndex()));

		Label seperator;

		if (selectedAnnotationClass != null) {
			annotationClassesList.setSelection(
					Arrays.asList(profile.getAnnotationClassIds()).indexOf(selectedAnnotationClass.getId())
			);

			itemName = new Text(rightContainer, SWT.BORDER);
			itemName.setLayoutData(lu.horizontalFillingGridData());
			itemName.setText(selectedAnnotationClass.getName());
			itemName.addModifyListener(e -> changeAnnotationClassName(itemName.getText()));

			colorSelector = new Button(rightContainer, SWT.PUSH);
			colorSelector.setBackground(selectedAnnotationClass.getColor());
			colorSelector.setLayoutData(lu.horizontalFillingGridData());
			colorSelector.setText("Change Color");
			colorSelector.addListener(SWT.Selection, e -> {
				ColorDialog dialog = new ColorDialog(parent.getShell());
				changeAnnotationColor(dialog.open());
			});

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

	private void selectAnnotationClass(int selectionIndex) {
		String annotationClassId = Arrays.asList(profile.getAnnotationClassIds()).get(selectionIndex);
		selectAnnotationClass(annotationClassId);
	}

	private void selectAnnotationClass(String annotationClassId) {
		try {
			selectedAnnotationClass = profile.getAnnotationClass(annotationClassId);
			rebuildContent(this);
		} catch (Exception e) {
			// TODO
			EclipseUtils.logger().error(e);
		}
	}

	private void changeAnnotationClassName(String name) {
		selectedAnnotationClass.setName(name);
		annotationClassesList.setItems(profile.getAnnotationClassNames());
		annotationClassesList.setSelection(new String[]{ name });
	}

	private void changeAnnotationColor(RGB col) {
		Color color = new Color(Display.getCurrent(), col.red, col.green, col.blue);
		selectedAnnotationClass.setColor(color);
		colorSelector.setBackground(color);
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
		String annotationClassName = "";

		InputDialog dialog = new InputDialog(getShell(), "Create new annotation class",
				"Set the name of the annotation class", "New Annotation Class " + newClassNameCounter++,
				t -> null);

		if (dialog.open() == Window.OK) {
			annotationClassName = dialog.getValue();
		}

		String annotationId = String.format(
				"%s_%s_%s",
				EclipseUtils.capString(profile.getName().toLowerCase().replaceAll("\\s", "-"), 8),
				EclipseUtils.capString(annotationClassName.toLowerCase().replaceAll("\\s", "-"), 8),
				EclipseUtils.capString(UUID.randomUUID().toString().toLowerCase().replaceAll("\\s", "-"), 4)
		);

		AnnotationClass newClass = new AnnotationClass(
				annotationId,
				annotationClassName,
				defaultColor
		);

		profile.addAnnotationClass(newClass);
		annotationClassesList.setItems(profile.getAnnotationClassNames());
		annotationClassesList.setSelection(new String[]{newClass.getId()});
		selectAnnotationClass(newClass.getId());
	}

	private void editAnnotationMetaData(AnnotationClass annotationClass) {
		Shell editWindow = new Shell(Display.getCurrent());
		editWindow.open();
		editWindow.setLayout(lu.gridLayout().withNumCols(1).get());

		EventManager<EventManager.EmptyEvent> relayout = new EventManager<>();
		Composite contentContainer = lu.createVerticalScrollComposite(editWindow, relayout);

		Header.withTitle("Edit Annotation Class Metadata")
				.withSubTitle("You are editing the metadata for the annotation class " + annotationClass.getId())
				.render(contentContainer);

		MetaDataView mdview = new MetaDataView(contentContainer, annotationClass.metaData, true, true, true, true);
		mdview.onChangedMetaData.addListener(e -> {
			onSave.run();

			// Refresh edit window
			selectAnnotationClass(selectedAnnotationClass.getId());
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
