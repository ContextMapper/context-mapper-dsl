/*
 * Copyright 2018 The Context Mapper Project Team
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

import java.util.List;

import org.eclipse.jface.fieldassist.AutoCompleteField;
import org.eclipse.jface.fieldassist.ComboContentAdapter;
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

public class TwoBoundedContextSelectionWizardPage extends ContextMapperWizardPage {

	private String initialBoundedContext1;
	private String initialBoundedContext2;
	private List<String> allBoundedContexts;
	private boolean textFieldsDisabled = false;

	private Combo comboBC1;
	private Combo comboBC2;
	private Button takeAttributesFromSecondBoundedContextCheckBox;
	private Composite container;

	public TwoBoundedContextSelectionWizardPage(String initialBoundedContext1, List<String> allBoundedContexts) {
		super("Bounded Context Selection Page");
		this.initialBoundedContext1 = initialBoundedContext1;
		this.allBoundedContexts = allBoundedContexts;
	}

	@Override
	public String getTitle() {
		return "Bounded Context Selection";
	}

	@Override
	public String getDescription() {
		return "Select Bounded Contexts to Merge";
	}

	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 2;

		// name label
		Label boundedContextNameLabel1 = new Label(container, SWT.NONE);
		boundedContextNameLabel1.setText("First Bounded Context:");

		// bounded context 1 selection field
		comboBC1 = new Combo(container, SWT.DROP_DOWN);
		comboBC1.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		comboBC1.setItems(this.allBoundedContexts.toArray(new String[this.allBoundedContexts.size()]));
		comboBC1.select(this.allBoundedContexts.indexOf(this.initialBoundedContext1));
		if (textFieldsDisabled)
			comboBC1.setEnabled(false);
		comboBC1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setPageComplete(isPageComplete());
			}
		});
		comboBC1.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				setPageComplete(isPageComplete());
			}
		});
		new AutoCompleteField(comboBC1, new ComboContentAdapter(), this.allBoundedContexts.toArray(new String[allBoundedContexts.size()]));

		// name label
		Label boundedContextNameLabel2 = new Label(container, SWT.NONE);
		boundedContextNameLabel2.setText("Second Bounded Context:");

		// bounded context 2 selection field
		comboBC2 = new Combo(container, SWT.DROP_DOWN);
		comboBC2.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		comboBC2.setItems(this.allBoundedContexts.toArray(new String[this.allBoundedContexts.size()]));
		if (this.initialBoundedContext2 != null)
			comboBC2.select(this.allBoundedContexts.indexOf(this.initialBoundedContext2));
		if (textFieldsDisabled)
			comboBC2.setEnabled(false);
		comboBC2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setPageComplete(isPageComplete());
			}
		});
		comboBC2.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				setPageComplete(isPageComplete());
			}
		});
		new AutoCompleteField(comboBC2, new ComboContentAdapter(), this.allBoundedContexts.toArray(new String[allBoundedContexts.size()]));

		// create a horizontal separator
		Label separator = new Label(container, SWT.HORIZONTAL | SWT.SEPARATOR);
		GridData separatorGridData = new GridData(GridData.FILL_HORIZONTAL);
		separatorGridData.horizontalSpan = 2;
		separator.setLayoutData(separatorGridData);

		// checkbox to inverse merging
		takeAttributesFromSecondBoundedContextCheckBox = new Button(container, SWT.CHECK);
		takeAttributesFromSecondBoundedContextCheckBox.setText("Take attributes which cannot be merged (incl. Bounded Context name) from second Bounded Context.");
		GridData buttonGroupGridData = new GridData(GridData.FILL_HORIZONTAL);
		buttonGroupGridData.horizontalSpan = 2;
		takeAttributesFromSecondBoundedContextCheckBox.setLayoutData(buttonGroupGridData);

		// label to explain checkbox
		Label checkboxExplanationLabel = new Label(container, SWT.NONE);
		checkboxExplanationLabel.setText("(By default we take attributes which cannot be merged from the first Bounded Context)");
		GridData checkboxExplanationLabelGridData = new GridData(GridData.FILL_HORIZONTAL);
		checkboxExplanationLabelGridData.horizontalSpan = 2;
		checkboxExplanationLabel.setLayoutData(checkboxExplanationLabelGridData);

		setControl(container);
		setPageComplete(false);
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		this.comboBC2.forceFocus();
	}

	public String getBoundedContext1() {
		return comboBC1.getText();
	}

	public String getBoundedContext2() {
		return comboBC2.getText();
	}
	
	public void setInitialBoundedContext2(String boundedContext2) {
		this.initialBoundedContext2 = boundedContext2;
	}
	
	public void disableTextFields() {
		this.textFieldsDisabled = true;
	}
	
	public boolean takeAttributesFromSecondBoundedContext() {
		return takeAttributesFromSecondBoundedContextCheckBox.getSelection();
	}

	@Override
	public boolean isPageComplete() {
		return this.allBoundedContexts.contains(comboBC1.getText()) && this.allBoundedContexts.contains(comboBC2.getText()) && !comboBC1.getText().equals(comboBC2.getText());
	}

	@Override
	public void performHelp() {
		Program.launch("https://contextmapper.org/docs/ar-merge-bounded-contexts/");
	}
}
