/*
 * Copyright 2020 The Context Mapper Project Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.contextmapper.dsl.ui.handler.wizard.pages;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.contextmapper.dsl.refactoring.ContextSplittingIntegrationType;
import org.contextmapper.dsl.ui.handler.wizard.DeriveBackendFrontendFromFeatureContext;
import org.contextmapper.dsl.validation.AbstractCMLValidator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class DeriveBackendFrontendSystemsFromFeatureWizardPage extends ContextMapperWizardPage {

	private DeriveBackendFrontendFromFeatureContext context;

	private Composite container;
	private Text frontendNameText;
	private Text backendNameText;
	private Text frontendImplementationTechnology;
	private Text backendImplementationTechnology;
	private Text relationshipImplementationTechnology;
	private Combo typeSelectionCombo;
	private Button deriveViewModelCheckbox;
	private boolean hasError = true;

	public DeriveBackendFrontendSystemsFromFeatureWizardPage(DeriveBackendFrontendFromFeatureContext context) {
		super("Derive Frontend/Backend Systems And Integration Relationship");
		this.context = context;
	}

	@Override
	public String getTitle() {
		return "Derive Frontend/Backend Systems and Integration Relationship";
	}

	@Override
	public String getDescription() {
		return "Configure the derived Bounded Contexts and integration relationship:";
	}

	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 2;

		Label frontendNameLabel = new Label(container, SWT.NONE);
		frontendNameLabel.setText("Frontend System Name:");
		frontendNameText = new Text(container, SWT.BORDER | SWT.SINGLE);
		frontendNameText.setText(context.getFrontendName());
		frontendNameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		frontendNameText.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				validate();
				setPageComplete(isPageComplete());
			}
		});

		Label backendNameLabel = new Label(container, SWT.NONE);
		backendNameLabel.setText("Backend System Name:");
		backendNameText = new Text(container, SWT.BORDER | SWT.SINGLE);
		backendNameText.setText(context.getBackendName());
		backendNameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		backendNameText.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				validate();
				setPageComplete(isPageComplete());
			}
		});

		Label frontendImplTechnologyLabel = new Label(container, SWT.NONE);
		frontendImplTechnologyLabel.setText("Frontend Implementation Technology:");
		frontendImplementationTechnology = new Text(container, SWT.BORDER | SWT.SINGLE);
		frontendImplementationTechnology.setText(context.getFrontendImplementationTechnology());
		frontendImplementationTechnology.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		frontendImplementationTechnology.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				validate();
				setPageComplete(isPageComplete());
			}
		});

		Label backendImplTechnologyLabel = new Label(container, SWT.NONE);
		backendImplTechnologyLabel.setText("Backend Implementation Technology:");
		backendImplementationTechnology = new Text(container, SWT.BORDER | SWT.SINGLE);
		backendImplementationTechnology.setText(context.getBackendImplementationTechnology());
		backendImplementationTechnology.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		backendImplementationTechnology.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				validate();
				setPageComplete(isPageComplete());
			}
		});

		Label typeSelectionLabel = new Label(container, SWT.NONE);
		typeSelectionLabel.setText("Integration Type:");

		typeSelectionCombo = new Combo(container, SWT.DROP_DOWN | SWT.READ_ONLY);
		typeSelectionCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		List<String> selectionStrings = Arrays.asList(ContextSplittingIntegrationType.values()).stream().map(l -> l.toString()).collect(Collectors.toList());
		typeSelectionCombo.setItems(selectionStrings.toArray(new String[selectionStrings.size()]));
		typeSelectionCombo.select(selectionStrings.indexOf(ContextSplittingIntegrationType.CONFORMIST.toString()));
		typeSelectionCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				validate();
				setPageComplete(isPageComplete());
			}
		});
		typeSelectionCombo.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				validate();
				setPageComplete(isPageComplete());
			}
		});

		new Label(container, SWT.NONE);
		Label hintLabel1 = new Label(container, SWT.NONE);
		hintLabel1.setText("Hint: With the CONFORMIST integration type, the frontend system conforms to");
		new Label(container, SWT.NONE);
		Label hintLabel2 = new Label(container, SWT.NONE);
		hintLabel2.setText("the domain model of the backend.");

		new Label(container, SWT.NONE);
		Label hintLabel3 = new Label(container, SWT.NONE);
		hintLabel3.setText("Hint: With the ACL integration type, the frontend system domain model differs");
		new Label(container, SWT.NONE);
		Label hintLabel4 = new Label(container, SWT.NONE);
		hintLabel4.setText("from the backend one, and the frontend needs a translation/anticorruption layer.");

		Label relationshipImplTechnologyLabel = new Label(container, SWT.NONE);
		relationshipImplTechnologyLabel.setText("Relationship Impl. Technology:");
		relationshipImplementationTechnology = new Text(container, SWT.BORDER | SWT.SINGLE);
		relationshipImplementationTechnology.setText(context.getRelationshipImplementationTechnology());
		relationshipImplementationTechnology.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		relationshipImplementationTechnology.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				validate();
				setPageComplete(isPageComplete());
			}
		});

		new Label(container, SWT.NONE);
		deriveViewModelCheckbox = new Button(container, SWT.CHECK);
		deriveViewModelCheckbox.setSelection(context.deriveViewModelInFrontend());
		deriveViewModelCheckbox.setText("Derive ViewModel in frontend system (initially just a copy of existing model).");

		setControl(container);
		validate();
		setPageComplete(false);
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		this.frontendNameText.forceFocus();
	}

	public String getFrontendName() {
		return frontendNameText.getText();
	}

	public String getBackendName() {
		return backendNameText.getText();
	}

	public String getFrontendImplementationTechnology() {
		return frontendImplementationTechnology.getText();
	}

	public String getRelationshipImplementationTechnology() {
		return relationshipImplementationTechnology.getText();
	}

	public String getBackendImplementationTechnology() {
		return backendImplementationTechnology.getText();
	}

	public ContextSplittingIntegrationType getRelationshipType() {
		return ContextSplittingIntegrationType.valueOf(this.typeSelectionCombo.getText());
	}

	public boolean deriveViewModelInFrontend() {
		return deriveViewModelCheckbox.getSelection();
	}

	private void validate() {
		setErrorMessage(null);
		hasError = false;

		if (!backendNameText.getText().matches(AbstractCMLValidator.ID_VALIDATION_PATTERN)) {
			setError("The Bounded Context name '" + backendNameText.getText() + "' is not valid. Allowed characters are: a-z, A-Z, 0-9, _");
			return;
		}
		if (!frontendNameText.getText().matches(AbstractCMLValidator.ID_VALIDATION_PATTERN)) {
			setError("The Bounded Context name '" + frontendNameText.getText() + "' is not valid. Allowed characters are: a-z, A-Z, 0-9, _");
			return;
		}
		if (this.context.getAllBoundedContextNames().contains(backendNameText.getText())) {
			setError("A Bounded Context with the name '" + backendNameText.getText() + "' already exists.");
			return;
		}
		if (this.context.getAllBoundedContextNames().contains(frontendNameText.getText())) {
			setError("A Bounded Context with the name '" + frontendNameText.getText() + "' already exists.");
			return;
		}
	}

	private void setError(String message) {
		hasError = true;
		setErrorMessage(message);
	}

	@Override
	public boolean isPageComplete() {
		return !hasError && !"".equals(backendNameText.getText()) && !"".equals(frontendNameText.getText()) && getRelationshipType() != null;
	}

	@Override
	public void performHelp() {
		Program.launch("https://contextmapper.org/docs/rapid-ooad/");
	}
}
