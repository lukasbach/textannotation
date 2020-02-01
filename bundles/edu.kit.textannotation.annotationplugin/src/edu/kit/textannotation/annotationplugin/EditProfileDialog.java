package edu.kit.textannotation.annotationplugin;

import edu.kit.textannotation.annotationplugin.profile.AnnotationProfileRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;

import edu.kit.textannotation.annotationplugin.profile.AnnotationClass;
import edu.kit.textannotation.annotationplugin.profile.AnnotationProfile;

import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import java.util.Arrays;
import java.util.function.Consumer;

public class EditProfileDialog extends Shell {
	private Text txtSelectedItemName;
	private AnnotationProfile profile;
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
	
	public static void openWindow(AnnotationProfileRegistry registry, String profileName,
								  Consumer<AnnotationProfile> onProfileChange) {
		try {
			Display display = PlatformUI.getWorkbench().getDisplay();
			AnnotationProfile profile = registry.findProfile(profileName);
			EditProfileDialog shell = new EditProfileDialog(display, profile, p -> {
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
			e.printStackTrace();
		}
	}

	/**
	 * Create the shell.
	 * @param display
	 */
	public EditProfileDialog(Display display, AnnotationProfile profile, Consumer<AnnotationProfile> onSave) {
		super(display, SWT.SHELL_TRIM);
		this.profile = profile;
		this.selectedAnnotationClass = null;
		this.onSave = () -> onSave.accept(profile);

		rebuildContent(this);
		createContents();
	}

	private void rebuildContent(Composite parent) {
		EclipseUtils.clearChildren(parent);

		// Composite mainContainer = new Composite(parent, SWT.NONE);
		parent.setLayout(lu.gridLayout().withNumCols(2).withEqualColumnWidth(false).get());

		Composite leftContainer = new Composite(parent, SWT.NONE);
		leftContainer.setLayout(lu.gridLayout().withNumCols(1).get());
		leftContainer.setLayoutData(lu.completelyFillingGridData());

		Composite rightContainer = new Composite(parent, SWT.NONE);
		rightContainer.setLayout(lu.gridLayout().withNumCols(1).get());
		rightContainer.setLayoutData(lu.verticallyFillingGridData());

		annotationClassesList = new List(leftContainer, SWT.BORDER);
		annotationClassesList.setLayoutData(lu.completelyFillingGridData());
		annotationClassesList.setItems(profile.getAnnotationClassNames());
		annotationClassesList.addListener(SWT.Selection, e -> selectAnnotationClass(annotationClassesList.getSelection()[0]));

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

		// CLabel canBeMatchedWithLabel = new CLabel(rightContainer, SWT.NONE);
		// canBeMatchedWithLabel.setText("Can be matched with:");

		// List canBeMatchedWithList = new List(rightContainer, SWT.BORDER);
		// canBeMatchedWithList.setItems("Verb", "Objective", "Something", "Else");
		// canBeMatchedWithList.setLayoutData(lu.gridData().withVerticalAlignment(SWT.FILL).get());
		// canBeMatchedWithList.setLayoutData(lu.completelyFillingGridData());

		Button btnRemoveClass = new Button(rightContainer, SWT.NONE);
		btnRemoveClass.setLayoutData(lu.horizontalFillingGridData());
		btnRemoveClass.setText("Remove selected Class");
		btnRemoveClass.addListener(SWT.Selection, e -> removeCurrentClass());

		Label seperator = new Label(rightContainer, SWT.SEPARATOR | SWT.HORIZONTAL);
		seperator.setLayoutData(lu.horizontalFillingGridData());

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
		setSize(550, 350);
	}

	private void selectAnnotationClass(String annotationClassName) {
		try {
			selectedAnnotationClass = profile.getAnnotationClass(annotationClassName);
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
		annotationClassesList.setItems(profile.getAnnotationClassNames());
		annotationClassesList.setSelection(0);
	}

	private void addNewClass() {
		Color defaultColor = new Color(Display.getCurrent(), 52, 152, 219);
		AnnotationClass newClass = new AnnotationClass("New Annotation Class " + newClassNameCounter++, defaultColor);
		profile.addAnnotationClass(newClass);
		annotationClassesList.setItems(profile.getAnnotationClassNames());
		annotationClassesList.setSelection(new String[]{newClass.getName()});
		selectAnnotationClass(newClass.getName());
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
