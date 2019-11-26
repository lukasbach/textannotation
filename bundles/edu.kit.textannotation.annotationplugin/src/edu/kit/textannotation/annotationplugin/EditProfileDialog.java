package edu.kit.textannotation.annotationplugin;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;
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

	
	public static void openWindow() {
		try {
			Display display = PlatformUI.getWorkbench().getDisplay();
			EditProfileDialog shell = new EditProfileDialog(display);
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
	public EditProfileDialog(Display display) {
		super(display, SWT.SHELL_TRIM);
		
		List list = new List(this, SWT.BORDER);
		list.setItems(new String[] {"Substantive", "Verb", "Object", "Something", "Else"});
		list.setBounds(10, 10, 246, 291);
		
		txtSelectedItemName = new Text(this, SWT.BORDER);
		txtSelectedItemName.setText("Selected Item Name");
		txtSelectedItemName.setBounds(262, 10, 263, 21);
		
		CCombo combo = new CCombo(this, SWT.BORDER);
		combo.setText("#abcdef");
		combo.setBounds(262, 37, 240, 21);
		
		StyledText styledText = new StyledText(this, SWT.BORDER);
		styledText.setBackground(SWTResourceManager.getColor(SWT.COLOR_DARK_GREEN));
		styledText.setBounds(504, 37, 21, 21);
		
		Label label = new Label(this, SWT.SEPARATOR | SWT.HORIZONTAL);
		label.setBounds(262, 64, 263, 2);
		
		CLabel lblNewLabel = new CLabel(this, SWT.NONE);
		lblNewLabel.setBounds(262, 66, 263, 21);
		lblNewLabel.setText("Can be matched with:");
		
		List list_1 = new List(this, SWT.BORDER);
		list_1.setItems(new String[] {"Verb", "Objective", "Something", "Else"});
		list_1.setBounds(262, 90, 263, 180);
		
		Button btnSave = new Button(this, SWT.NONE);
		btnSave.setBounds(262, 276, 263, 25);
		btnSave.setText("Save");
		createContents();
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
