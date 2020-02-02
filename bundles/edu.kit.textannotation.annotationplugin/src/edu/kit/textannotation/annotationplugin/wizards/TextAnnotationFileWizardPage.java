package edu.kit.textannotation.annotationplugin.wizards;

import edu.kit.textannotation.annotationplugin.ComboSelectionListener;
import edu.kit.textannotation.annotationplugin.EclipseUtils;
import edu.kit.textannotation.annotationplugin.PluginConfig;
import edu.kit.textannotation.annotationplugin.profile.AnnotationProfileRegistry;
import edu.kit.textannotation.annotationplugin.textmodel.InvalidAnnotationProfileFormatException;
import edu.kit.textannotation.annotationplugin.textmodel.SchemaValidator;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.osgi.framework.FrameworkUtil;

/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one (mpe).
 */

public class TextAnnotationFileWizardPage extends WizardPage {
	private Text containerText;

	private Text fileText;

	private Text templateFileText;

	private ISelection selection;

	private Combo profile;

	/**
	 * Constructor for TextAnnotationFileWizardPage.
	 */
	public TextAnnotationFileWizardPage(ISelection selection) {
		super("wizardPage");
		setTitle("Annotatable Text File");
		setDescription("This wizard creates a new file which can be annotated with metadata.");
		this.selection = selection;
	}

	@Override
	public void createControl(Composite parent) {
		AnnotationProfileRegistry registry = AnnotationProfileRegistry.createNew(FrameworkUtil.getBundle(this.getClass()));

		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 3;
		layout.verticalSpacing = 9;

		// Specify folder
		Label label = new Label(container, SWT.NULL);
		label.setText("&Container:");

		containerText = new Text(container, SWT.BORDER | SWT.SINGLE);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		containerText.setLayoutData(gd);
		containerText.addModifyListener(e -> dialogChanged());

		Button button = new Button(container, SWT.PUSH);
		button.setText("Browse...");
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleBrowseContainer(containerText, "Select new file container");
			}
		});

		// Define file name
		label = new Label(container, SWT.NULL);
		label.setText("&File name:");

		fileText = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		fileText.setLayoutData(gd);
		fileText.addModifyListener(e -> dialogChanged());

		label = new Label(container, SWT.NULL);
		label.setText("&");

		// Use existing annotation profile
		label = new Label(container, SWT.NULL);
		label.setText("&Annotation Profile:");

		profile = new Combo(container, SWT.DROP_DOWN | SWT.BORDER);
		try {
			registry.getProfiles().forEach(p -> profile.add(p.getName()));
		} catch (InvalidAnnotationProfileFormatException e) {
			e.printStackTrace();
			EclipseUtils.reportError("Profile is not properly formatted.");
		}
		gd = new GridData(GridData.FILL_HORIZONTAL);
		profile.setLayoutData(gd);
		ComboSelectionListener.create(profile, v -> dialogChanged());

		label = new Label(container, SWT.NULL);
		label.setText("&");

		// Init from existing text file
		label = new Label(container, SWT.NULL);
		label.setText("&Initialize from existing text file:");

		templateFileText = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		templateFileText.setLayoutData(gd);
		templateFileText.addModifyListener(e -> dialogChanged());

		button = new Button(container, SWT.PUSH);
		button.setText("Browse...");
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleBrowseFile(templateFileText, "Select existing text file");
			}
		});

		initialize();
		dialogChanged();
		setControl(container);
	}

	/**
	 * Tests if the current workbench selection is a suitable container to use.
	 */

	private void initialize() {
		if (selection != null && selection.isEmpty() == false
				&& selection instanceof IStructuredSelection) {
			IStructuredSelection ssel = (IStructuredSelection) selection;
			if (ssel.size() > 1)
				return;
			Object obj = ssel.getFirstElement();
			if (obj instanceof IResource) {
				IContainer container;
				if (obj instanceof IContainer)
					container = (IContainer) obj;
				else
					container = ((IResource) obj).getParent();
				containerText.setText(container.getFullPath().toString());
			}
		}
		fileText.setText("newAnnotationFile." + PluginConfig.ANNOTATABLE_FILE_EXTENSION);
	}

	/**
	 * Uses the standard container selection dialog to choose the new value for
	 * the container field.
	 */

	private void handleBrowseContainer(Text target, String message) {
		ContainerSelectionDialog dialog = new ContainerSelectionDialog(
				getShell(), ResourcesPlugin.getWorkspace().getRoot(), false,
				message);
		if (dialog.open() == ContainerSelectionDialog.OK) {
			Object[] result = dialog.getResult();
			if (result.length == 1) {
				target.setText(((Path) result[0]).toString());
			}
		}
	}

	private void handleBrowseFile(Text target, String message) {
		FileDialog dialog = new FileDialog(getShell());
		dialog.setText(message);
		// dialog.setFilterExtensions(new String[] {"txt", "md", "xml"});
		String result = dialog.open();
		target.setText(result);
	}

	/**
	 * Ensures that both text fields are set.
	 */

	private void dialogChanged() {
		IResource container = ResourcesPlugin.getWorkspace().getRoot()
				.findMember(new Path(getContainerName()));
		IResource templateFile = (getContainerName().length() != 0) ? ResourcesPlugin.getWorkspace().getRoot()
				.findMember(new Path(getContainerName())) : null;
		String fileName = getFileName();

		if (getContainerName().length() == 0) {
			updateStatus("File container must be specified");
			return;
		}
		if (container == null
				|| (container.getType() & (IResource.PROJECT | IResource.FOLDER)) == 0) {
			updateStatus("File container must exist");
			return;
		}
		if (!container.isAccessible()) {
			updateStatus("Project must be writable");
			return;
		}
		if (templateFile == null || !templateFile.isAccessible()) {
			updateStatus("Text file does not exist");
			return;
		}
		if (fileName.length() == 0) {
			updateStatus("File name must be specified");
			return;
		}
		if (fileName.replace('\\', '/').indexOf('/', 1) > 0) {
			updateStatus("File name must be valid");
			return;
		}
		if (getProfile() == null || getProfile().length() == 0) {
			updateStatus("Profile must be specified.");
			return;
		}
		int dotLoc = fileName.lastIndexOf('.');
		if (dotLoc != -1) {
			String ext = fileName.substring(dotLoc + 1);
			if (!ext.equalsIgnoreCase(PluginConfig.ANNOTATABLE_FILE_EXTENSION)) {
				updateStatus("File extension must be \"" + PluginConfig.ANNOTATABLE_FILE_EXTENSION + "\"");
				return;
			}
		}
		updateStatus(null);
	}

	private void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}

	String getContainerName() {
		return containerText.getText();
	}

	String getTemplateFileName() {
		return templateFileText.getText();
	}

	String getFileName() {
		return fileText.getText();
	}

	String getProfile() {
		return profile.getText();
	}
}