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
import org.contextmapper.dsl.refactoring.SplitSystemIntoSubsystems.SplitBoundedContextRelationshipType;
import org.contextmapper.dsl.ui.handler.wizard.SplitSystemIntoSubsystemsContext;
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

public class SplitSystemIntoSubsystemsWizardPage extends ContextMapperWizardPage {

	private SplitSystemIntoSubsystemsContext context;

	private Composite container;
	private Text existingSubsystemNameText;
	private Text newSubsystemNameText;
	private Combo relationshipTypeSelectionCombo;
	private Combo integrationTypeSelectionCombo;
	private Text newSubsystemImplementationTechnology;
	private Text newRelationshipImplementationTechnology;
	private Button copyDomainModelCheckbox;
	private boolean hasError = true;

	public SplitSystemIntoSubsystemsWizardPage(SplitSystemIntoSubsystemsContext context) {
		super("Split System Context Into Two Subsystems");
		this.context = context;
	}

	@Override
	public String getTitle() {
		return "Split System Context Into Two Subsystems";
	}

	@Override
	public String getDescription() {
		return "Configure the two subsystems:";
	}

	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 2;

		Label existingSubsystemNameLabel = new Label(container, SWT.NONE);
		existingSubsystemNameLabel.setText("Existing Subsystem:");
		existingSubsystemNameText = new Text(container, SWT.BORDER | SWT.SINGLE);
		existingSubsystemNameText.setText(context.getExistingContextSubsystemName());
		existingSubsystemNameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		existingSubsystemNameText.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				validate();
				setPageComplete(isPageComplete());
			}
		});

		Label newSubsystemNameLabel = new Label(container, SWT.NONE);
		newSubsystemNameLabel.setText("New Subsystem:");
		newSubsystemNameText = new Text(container, SWT.BORDER | SWT.SINGLE);
		newSubsystemNameText.setText(context.getNewSubsystemName());
		newSubsystemNameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		newSubsystemNameText.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				validate();
				setPageComplete(isPageComplete());
			}
		});

		Label newSubsystemImplTechnologyLabel = new Label(container, SWT.NONE);
		newSubsystemImplTechnologyLabel.setText("New Subsystems Impl. Technology:");
		newSubsystemImplementationTechnology = new Text(container, SWT.BORDER | SWT.SINGLE);
		newSubsystemImplementationTechnology.setText(context.getNewSubsystemImplementationTechnology());
		newSubsystemImplementationTechnology.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		newSubsystemImplementationTechnology.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				validate();
				setPageComplete(isPageComplete());
			}
		});

		Label relationshipTypeLabel = new Label(container, SWT.NONE);
		relationshipTypeLabel.setText("New Subsystems Relationship Role:");

		relationshipTypeSelectionCombo = new Combo(container, SWT.DROP_DOWN | SWT.READ_ONLY);
		relationshipTypeSelectionCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		List<String> selectionStrings = Arrays.asList(SplitBoundedContextRelationshipType.values()).stream().map(l -> l.getLabel()).collect(Collectors.toList());
		relationshipTypeSelectionCombo.setItems(selectionStrings.toArray(new String[selectionStrings.size()]));
		relationshipTypeSelectionCombo.select(selectionStrings.indexOf(context.getRelationshipType().getLabel()));
		relationshipTypeSelectionCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				validate();
				setPageComplete(isPageComplete());
			}
		});
		relationshipTypeSelectionCombo.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				validate();
				setPageComplete(isPageComplete());
			}
		});

		Label integrationTypeLabel = new Label(container, SWT.NONE);
		integrationTypeLabel.setText("Downstream Integration Type:");

		integrationTypeSelectionCombo = new Combo(container, SWT.DROP_DOWN | SWT.READ_ONLY);
		integrationTypeSelectionCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		List<String> integrationSelectionStrings = Arrays.asList(ContextSplittingIntegrationType.values()).stream().map(l -> l.toString()).collect(Collectors.toList());
		integrationTypeSelectionCombo.setItems(integrationSelectionStrings.toArray(new String[integrationSelectionStrings.size()]));
		integrationTypeSelectionCombo.select(integrationSelectionStrings.indexOf(context.getIntegrationType().toString()));
		integrationTypeSelectionCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				validate();
				setPageComplete(isPageComplete());
			}
		});
		integrationTypeSelectionCombo.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				validate();
				setPageComplete(isPageComplete());
			}
		});

		new Label(container, SWT.NONE);
		Label hintLabel1 = new Label(container, SWT.NONE);
		hintLabel1.setText("Hint: With the CONFORMIST integration type, the downstream subsystem conforms to");
		new Label(container, SWT.NONE);
		Label hintLabel2 = new Label(container, SWT.NONE);
		hintLabel2.setText("the domain model of the upstream subsystem.");

		new Label(container, SWT.NONE);
		Label hintLabel3 = new Label(container, SWT.NONE);
		hintLabel3.setText("Hint: With the ACL integration type, the downstream subsystem domain model differs");
		new Label(container, SWT.NONE);
		Label hintLabel4 = new Label(container, SWT.NONE);
		hintLabel4.setText("from the upstream domain model, and the downstream needs a translation/anti-");
		new Label(container, SWT.NONE);
		Label hintLabel5 = new Label(container, SWT.NONE);
		hintLabel5.setText("corruption layer.");

		Label relationshipImplTechnologyLabel = new Label(container, SWT.NONE);
		relationshipImplTechnologyLabel.setText("Relationship Impl. Technology:");
		newRelationshipImplementationTechnology = new Text(container, SWT.BORDER | SWT.SINGLE);
		newRelationshipImplementationTechnology.setText(context.getNewRelationshipImplementationTechnology());
		newRelationshipImplementationTechnology.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		newRelationshipImplementationTechnology.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				validate();
				setPageComplete(isPageComplete());
			}
		});

		new Label(container, SWT.NONE);
		copyDomainModelCheckbox = new Button(container, SWT.CHECK);
		copyDomainModelCheckbox.setSelection(context.copyDomainModel());
		copyDomainModelCheckbox.setText("Copy domain model from existing subsystem to new subsystem.");

		setControl(container);
		validate();
		setPageComplete(false);
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		this.newSubsystemNameText.forceFocus();
	}

	public String getExistingSubsystemName() {
		return this.existingSubsystemNameText.getText();
	}

	public String getNewSubsystemName() {
		return this.newSubsystemNameText.getText();
	}

	public ContextSplittingIntegrationType getIntegrationType() {
		return ContextSplittingIntegrationType.valueOf(this.integrationTypeSelectionCombo.getText());
	}

	public SplitBoundedContextRelationshipType getRelationshipType() {
		return SplitBoundedContextRelationshipType.byLabel(this.relationshipTypeSelectionCombo.getText());
	}

	public String getNewSubsystemImplementationTechnology() {
		return this.newSubsystemImplementationTechnology.getText();
	}

	public String getNewRelationshipImplementationTechnology() {
		return this.newRelationshipImplementationTechnology.getText();
	}

	public boolean copyDomainModel() {
		return this.copyDomainModelCheckbox.getSelection();
	}

	private void validate() {
		setErrorMessage(null);
		hasError = false;

		if (!existingSubsystemNameText.getText().matches(AbstractCMLValidator.ID_VALIDATION_PATTERN)) {
			setError("The Bounded Context name '" + existingSubsystemNameText.getText() + "' is not valid. Allowed characters are: a-z, A-Z, 0-9, _");
			return;
		}
		if (!newSubsystemNameText.getText().matches(AbstractCMLValidator.ID_VALIDATION_PATTERN)) {
			setError("The Bounded Context name '" + newSubsystemNameText.getText() + "' is not valid. Allowed characters are: a-z, A-Z, 0-9, _");
			return;
		}
	}

	private void setError(String message) {
		hasError = true;
		setErrorMessage(message);
	}

	@Override
	public boolean isPageComplete() {
		return !hasError && !"".equals(existingSubsystemNameText.getText()) && !"".equals(newSubsystemNameText.getText());
	}

	@Override
	public void performHelp() {
		Program.launch("https://contextmapper.org/docs/rapid-ooad/");
	}
}
