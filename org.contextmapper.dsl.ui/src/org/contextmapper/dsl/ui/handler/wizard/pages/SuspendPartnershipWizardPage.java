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

import static org.contextmapper.dsl.ui.handler.wizard.SuspendPartnershipMode.EXTRACT_NEW_BOUNDED_CONTEXT;
import static org.contextmapper.dsl.ui.handler.wizard.SuspendPartnershipMode.MERGE_BOUNDED_CONTEXTS;
import static org.contextmapper.dsl.ui.handler.wizard.SuspendPartnershipMode.REPLACE_RELATIONSHIP_WITH_UPSTREAM_DOWNSTREAM;

import org.contextmapper.dsl.ui.handler.wizard.SuspendPartnershipMode;
import org.eclipse.jface.fieldassist.AutoCompleteField;
import org.eclipse.jface.fieldassist.ComboContentAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

public class SuspendPartnershipWizardPage extends ContextMapperWizardPage {

	private String boundedContext1;
	private String boundedContext2;

	private SuspendPartnershipMode suspendMode;

	private Composite container;
	private Group radioGroup;
	private Button mergeButton;
	private Button extractBCButton;
	private Button createUpstreamDownstreamButton;
	private Combo replaceModeUpstreamCombo;
	private Label replaceModeUpstreamLabel;

	public SuspendPartnershipWizardPage(String boundedContext1, String boundedContext2) {
		super("Suspend Partnership Page");
		this.boundedContext1 = boundedContext1;
		this.boundedContext2 = boundedContext2;
		this.suspendMode = MERGE_BOUNDED_CONTEXTS;
	}

	@Override
	public String getTitle() {
		return "Suspend Partnership";
	}

	@Override
	public String getDescription() {
		return "Choose how you want to suspend the partnership between '" + boundedContext1 + "' and\n'" + boundedContext2 + "'.";
	}

	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 2;

		radioGroup = new Group(container, SWT.NONE);
		radioGroup.setLayout(new RowLayout(SWT.VERTICAL));
		radioGroup.setText("Partnership Suspending Mode");
		GridData radioGroupLayoutData = new GridData(GridData.FILL_HORIZONTAL);
		radioGroupLayoutData.horizontalSpan = 2;
		radioGroup.setLayoutData(radioGroupLayoutData);

		mergeButton = new Button(radioGroup, SWT.RADIO);
		mergeButton.setText("Merge Bounded Contexts '" + boundedContext1 + "' and '" + boundedContext2 + "'.");
		mergeButton.setSelection(true);
		mergeButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				suspendMode = MERGE_BOUNDED_CONTEXTS;
				updateComboVisibility();
				setPageComplete(isPageComplete());
			}
		});
		new Label(radioGroup, NONE).setText("");

		extractBCButton = new Button(radioGroup, SWT.RADIO);
		extractBCButton.setText("Extract new Bounded Context.");
		extractBCButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				suspendMode = EXTRACT_NEW_BOUNDED_CONTEXT;
				updateComboVisibility();
				setPageComplete(isPageComplete());
			}
		});
		new Label(radioGroup, SWT.NONE).setText("    Hint: Creates a new Bounded Context for common model parts and establishes upstream-downstream");
		new Label(radioGroup, SWT.NONE).setText("    relationships between the new and the existing two Bounded Contexts.");
		new Label(radioGroup, NONE).setText("");

		createUpstreamDownstreamButton = new Button(radioGroup, SWT.RADIO);
		createUpstreamDownstreamButton.setText("Replace Partnership with Upstream-Downstream relationship.");
		createUpstreamDownstreamButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				suspendMode = REPLACE_RELATIONSHIP_WITH_UPSTREAM_DOWNSTREAM;
				updateComboVisibility();
				setPageComplete(isPageComplete());
			}
		});
		new Label(radioGroup, SWT.NONE).setText("    Hint: Simply replaces the Partnership relationship with an upstream-downstream relationship.");
		new Label(radioGroup, SWT.NONE).setText("    In this case you must specify which Bounded Context becomes upstream.");
		new Label(radioGroup, NONE).setText("");

		replaceModeUpstreamLabel = new Label(container, NONE);
		replaceModeUpstreamLabel.setText("Upstream Bounded Context: ");
		replaceModeUpstreamLabel.setVisible(false);
		replaceModeUpstreamCombo = new Combo(container, SWT.DROP_DOWN);
		replaceModeUpstreamCombo.setVisible(false);
		replaceModeUpstreamCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		replaceModeUpstreamCombo.setItems(new String[] { boundedContext1, boundedContext2 });
		replaceModeUpstreamCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setPageComplete(isPageComplete());
			}
		});
		replaceModeUpstreamCombo.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				setPageComplete(isPageComplete());
			}
		});
		new AutoCompleteField(replaceModeUpstreamCombo, new ComboContentAdapter(), new String[] { boundedContext1, boundedContext2 });

		setControl(container);
		setPageComplete(false);
	}

	public SuspendPartnershipMode getSuspendMode() {
		return suspendMode;
	}

	public String getReplaceModeUpstreamBoundedContext() {
		return replaceModeUpstreamCombo.getText();
	}

	private void updateComboVisibility() {
		replaceModeUpstreamCombo.setVisible(suspendMode == REPLACE_RELATIONSHIP_WITH_UPSTREAM_DOWNSTREAM);
		replaceModeUpstreamLabel.setVisible(suspendMode == REPLACE_RELATIONSHIP_WITH_UPSTREAM_DOWNSTREAM);
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		this.mergeButton.forceFocus();
	}

	@Override
	public boolean isPageComplete() {
		if (suspendMode == REPLACE_RELATIONSHIP_WITH_UPSTREAM_DOWNSTREAM)
			return replaceModeUpstreamCombo.getText() != null && !"".equals(replaceModeUpstreamCombo.getText())
					&& (boundedContext1.equals(replaceModeUpstreamCombo.getText()) || boundedContext2.equals(replaceModeUpstreamCombo.getText()));
		return suspendMode != null;
	}

	@Override
	public void performHelp() {
		Program.launch("https://contextmapper.org/docs/ar-suspend-partnership/");
	}
}
