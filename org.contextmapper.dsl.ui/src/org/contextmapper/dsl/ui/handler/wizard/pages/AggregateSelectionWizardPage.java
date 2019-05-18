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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

public class AggregateSelectionWizardPage extends ContextMapperWizardPage {

	private String initialBoundedContextName;
	private List<String> allAggregates;

	private Text boundedContextName;
	private Composite container;
	private Table selectionTable;

	public AggregateSelectionWizardPage(String initialBoundedContextName, List<String> allAggregates) {
		super("Aggregate Selection Page");
		this.initialBoundedContextName = initialBoundedContextName;
		this.allAggregates = allAggregates;
	}

	@Override
	public String getTitle() {
		return "Aggregate Selection";
	}

	@Override
	public String getDescription() {
		return "Select Aggregates to Extract";
	}

	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 2;

		// name label
		Label boundedContextNameLabel = new Label(container, SWT.NONE);
		boundedContextNameLabel.setText("Name for new Bounded Context:");

		// name text field
		boundedContextName = new Text(container, SWT.BORDER | SWT.SINGLE);
		boundedContextName.setText(initialBoundedContextName);
		boundedContextName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		boundedContextName.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				setPageComplete(isPageComplete());
			}
		});

		// create a horizontal separator
		Label separator = new Label(container, SWT.HORIZONTAL | SWT.SEPARATOR);
		GridData separatorGridData = new GridData(GridData.FILL_HORIZONTAL);
		separatorGridData.horizontalSpan = 2;
		separator.setLayoutData(separatorGridData);

		// selection label
		Label selectionLabel = new Label(container, SWT.NONE);
		selectionLabel.setText("Extracted Aggregates:");
		GridData selectionLabelGridData = new GridData();
		selectionLabelGridData.horizontalSpan = 2;
		selectionLabel.setLayoutData(selectionLabelGridData);

		// selection table
		selectionTable = new Table(container, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL);
		for (String aggregate : allAggregates) {
			TableItem item = new TableItem(selectionTable, SWT.NONE);
			item.setText(aggregate);
		}
		GridData tableGridData = new GridData(GridData.FILL_HORIZONTAL);
		tableGridData.horizontalSpan = 2;
		tableGridData.heightHint = 200;
		selectionTable.setLayoutData(tableGridData);
		selectionTable.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				if (event.detail == SWT.CHECK) {
					setPageComplete(isPageComplete());
					updateWarningMessage();
				}
			}
		});

		setControl(container);
		setPageComplete(false);
	}

	private void updateWarningMessage() {
		setMessage(null);
		if (getSelectedAggregates().size() == this.allAggregates.size()) {
			setMessage("Note that extracting all aggregates leads to the current Bounded Context being empty. Maybe just rename the current Bounded Context instead.",
					WARNING);
		}
	}

	public String getBoundedContextName() {
		return boundedContextName.getText();
	}

	public List<String> getSelectedAggregates() {
		List<TableItem> selectedItems = Arrays.asList(selectionTable.getItems()).stream().filter(item -> item.getChecked()).collect(Collectors.toList());
		return selectedItems.stream().map(item -> item.getText()).collect(Collectors.toList());
	}

	@Override
	public boolean isPageComplete() {
		return boundedContextName.getText() != null && !"".equals(boundedContextName.getText()) && getSelectedAggregates().size() >= 1;
	}

	@Override
	public void performHelp() {
		Program.launch("https://contextmapper.github.io/docs/ar-extract-aggregates-by-nfr/");
	}
}
