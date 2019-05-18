/*
 * Copyright 2019 The Context Mapper Project Team
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

public class TwoAggregatesSelectionWizardPage extends ContextMapperWizardPage {

	private String initialAggregate1;
	private List<String> allAggregates;

	private Combo comboAgg1;
	private Combo comboAgg2;
	private Button takeAttributesFromSecondAggregateCheckBox;
	private Composite container;

	public TwoAggregatesSelectionWizardPage(String initialAggregate1, List<String> allAggregates) {
		super("Bounded Context Selection Page");
		this.initialAggregate1 = initialAggregate1;
		this.allAggregates = allAggregates;
	}

	@Override
	public String getTitle() {
		return "Aggregate Selection";
	}

	@Override
	public String getDescription() {
		return "Select Aggregates to Merge";
	}

	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 2;

		// name label
		Label aggregateNameLabel1 = new Label(container, SWT.NONE);
		aggregateNameLabel1.setText("First Aggregate:");

		// aggregate 1 selection field
		comboAgg1 = new Combo(container, SWT.DROP_DOWN);
		comboAgg1.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		comboAgg1.setItems(this.allAggregates.toArray(new String[this.allAggregates.size()]));
		comboAgg1.select(this.allAggregates.indexOf(this.initialAggregate1));
		comboAgg1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setPageComplete(isPageComplete());
			}
		});
		comboAgg1.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				setPageComplete(isPageComplete());
			}
		});
		new AutoCompleteField(comboAgg1, new ComboContentAdapter(), this.allAggregates.toArray(new String[allAggregates.size()]));

		// name label
		Label aggregateNameLabel2 = new Label(container, SWT.NONE);
		aggregateNameLabel2.setText("Second Aggregate:");

		// aggregate 2 selection field
		comboAgg2 = new Combo(container, SWT.DROP_DOWN);
		comboAgg2.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		comboAgg2.setItems(this.allAggregates.toArray(new String[this.allAggregates.size()]));
		comboAgg2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setPageComplete(isPageComplete());
			}
		});
		comboAgg2.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				setPageComplete(isPageComplete());
			}
		});
		new AutoCompleteField(comboAgg2, new ComboContentAdapter(), this.allAggregates.toArray(new String[allAggregates.size()]));

		// checkbox to inverse merging
		takeAttributesFromSecondAggregateCheckBox = new Button(container, SWT.CHECK);
		takeAttributesFromSecondAggregateCheckBox.setText("Take attributes which cannot be merged (incl. Aggregate name) from second Aggregate.");
		GridData buttonGroupGridData = new GridData(GridData.FILL_HORIZONTAL);
		buttonGroupGridData.horizontalSpan = 2;
		takeAttributesFromSecondAggregateCheckBox.setLayoutData(buttonGroupGridData);

		// label to explain checkbox
		Label checkboxExplanationLabel = new Label(container, SWT.NONE);
		checkboxExplanationLabel.setText("(By default we take attributes which cannot be merged from the first Aggregate)");
		GridData checkboxExplanationLabelGridData = new GridData(GridData.FILL_HORIZONTAL);
		checkboxExplanationLabelGridData.horizontalSpan = 2;
		checkboxExplanationLabel.setLayoutData(checkboxExplanationLabelGridData);

		setControl(container);
		setPageComplete(false);
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		this.comboAgg2.forceFocus();
	}

	public String getAggregate1() {
		return comboAgg1.getText();
	}

	public String getAggregate2() {
		return comboAgg2.getText();
	}

	public boolean takeAttributesFromSecondAggregateCheckBox() {
		return takeAttributesFromSecondAggregateCheckBox.getSelection();
	}

	@Override
	public boolean isPageComplete() {
		return this.allAggregates.contains(comboAgg1.getText()) && this.allAggregates.contains(comboAgg2.getText()) && !comboAgg1.getText().equals(comboAgg2.getText());
	}

	@Override
	public void performHelp() {
		Program.launch("https://contextmapper.github.io/docs/ar-merge-aggregates/");
	}
}
