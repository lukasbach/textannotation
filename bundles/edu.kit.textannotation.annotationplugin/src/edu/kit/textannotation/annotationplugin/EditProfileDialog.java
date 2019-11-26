package edu.kit.textannotation.annotationplugin;

import org.eclipse.swt.SWT;
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
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class EditProfileDialog extends Shell {
	private Text txtSelectedItemName;
	private AnnotationProfile profile;
	private AnnotationClass selectedAnnotationClass;
	
	public static void openWindow(AnnotationProfile profile) {
		
		try {
			Display display = PlatformUI.getWorkbench().getDisplay();
			EditProfileDialog shell = new EditProfileDialog(display, profile);
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
	public EditProfileDialog(Display display, AnnotationProfile profile) {
		super(display, SWT.SHELL_TRIM);
		this.profile = profile;
		this.selectedAnnotationClass = null;
		
		List annotationClassesList = new List(this, SWT.BORDER);
		annotationClassesList.setItems(profile.getAnnotationClassNames());
		annotationClassesList.setBounds(10, 10, 246, 291);
		
		txtSelectedItemName = new Text(this, SWT.BORDER);
		txtSelectedItemName.setText("Selected Item Name");
		txtSelectedItemName.setBounds(262, 10, 263, 21);
		
		CCombo colorSelector = new CCombo(this, SWT.BORDER);
		colorSelector.setText("#abcdef");
		colorSelector.setBounds(262, 37, 240, 21);
		
		StyledText colorDisplay = new StyledText(this, SWT.BORDER);
		colorDisplay.setBackground(SWTResourceManager.getColor(SWT.COLOR_DARK_GREEN));
		colorDisplay.setBounds(504, 37, 21, 21);
		
		Label seperator = new Label(this, SWT.SEPARATOR | SWT.HORIZONTAL);
		seperator.setBounds(262, 64, 263, 2);
		
		CLabel canBeMatchedWithLabel = new CLabel(this, SWT.NONE);
		canBeMatchedWithLabel.setBounds(262, 66, 263, 21);
		canBeMatchedWithLabel.setText("Can be matched with:");
		
		List canBeMatchedWithList = new List(this, SWT.BORDER);
		canBeMatchedWithList.setItems(new String[] {"Verb", "Objective", "Something", "Else"});
		canBeMatchedWithList.setBounds(262, 90, 263, 180);
		
		Button btnSave = new Button(this, SWT.NONE);
		btnSave.setBounds(262, 276, 263, 25);
		btnSave.setText("Save");
		createContents();
		
		// Hooks
		annotationClassesList.addListener(SWT.Selection, e -> {
			try {
				AnnotationClass ac = profile.getAnnotationClass(annotationClassesList.getSelection()[0]);
				selectedAnnotationClass = ac;
				txtSelectedItemName.setText(ac.getName());
				canBeMatchedWithList.setItems(
						profile
							.getAnnotationClasses()
							.stream()
							.filter(acl -> !acl.getName().equals(ac.getName()))
							.map(acl -> acl.getName())
							.toArray(n -> new String[n])
						);
			} catch(Exception e1) {
				e1.printStackTrace();
			}
		});
		
		txtSelectedItemName.addModifyListener(e -> {
			int selectionIndex = annotationClassesList.getSelectionIndex();
			selectedAnnotationClass.setName(txtSelectedItemName.getText());
			annotationClassesList.setItems(profile.getAnnotationClassNames());
			annotationClassesList.setSelection(selectionIndex);
		});
		
		colorSelector.addListener(SWT.Selection, e -> {
			// TODO colorDisplay.setBackground(SWTResourceManager.getColor(SWT.COLOR_DARK_GREEN));
		});
	}

	/**
	 * Create contents of the shell.
	 */
	protected void createContents() {
		setText("Edit Profile");
		setSize(551, 350);

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
