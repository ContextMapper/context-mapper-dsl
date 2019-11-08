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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.contextmapper.dsl.generator.contextmap.ContextMapFormat;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Spinner;

public class GenerateContextMapWizardPage extends ContextMapperWizardPage {

	private Combo formatSelectionCombo;
	private Composite container;
	private Button widthCheckBox;
	private Spinner widthSpinner;
	private Button heightCheckBox;
	private Spinner heightSpinner;

	private ContextMapFormat selectedFormat = ContextMapFormat.PNG;
	private int labelSpacingFactor = 5;
	private int width = 3600;
	private int height = 1500;

	public GenerateContextMapWizardPage() {
		super("Generate Context Map Configuration Page");
	}

	@Override
	public String getTitle() {
		return "Context Map Generation";
	}

	@Override
	public String getDescription() {
		return "Configure how to generate your Context Map";
	}

	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.verticalSpacing = 10;
		container.setLayout(layout);

		// name label
		Label formatSelectionLabel = new Label(container, SWT.NONE);
		formatSelectionLabel.setText("Format:");

		// selection field
		formatSelectionCombo = new Combo(container, SWT.DROP_DOWN | SWT.READ_ONLY);
		formatSelectionCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		List<String> selectionStrings = Arrays.asList(ContextMapFormat.values()).stream().map(l -> l.toString()).collect(Collectors.toList());
		formatSelectionCombo.setItems(selectionStrings.toArray(new String[selectionStrings.size()]));
		formatSelectionCombo.select(selectionStrings.indexOf(ContextMapFormat.PNG.toString()));
		formatSelectionCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectedFormat = ContextMapFormat.valueOf(formatSelectionCombo.getText());
				setPageComplete(isPageComplete());
			}
		});

		// fix width to custom value
		widthCheckBox = new Button(container, SWT.CHECK);
		widthCheckBox.setText("Fix image width:    ");
		widthCheckBox.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				widthSpinner.setEnabled(widthCheckBox.getSelection());
				if (widthCheckBox.getSelection()) {
					heightCheckBox.setSelection(false);
					heightSpinner.setEnabled(false);
				}
			}
		});
		widthSpinner = new Spinner(container, SWT.NONE);
		widthSpinner.setMinimum(1);
		widthSpinner.setMaximum(20000);
		widthSpinner.setSelection(width);
		widthSpinner.setIncrement(100);
		widthSpinner.setEnabled(false);
		widthSpinner.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				width = widthSpinner.getSelection();
			}
		});

		// fix height to custom value
		heightCheckBox = new Button(container, SWT.CHECK);
		heightCheckBox.setText("Fix image height:");
		heightCheckBox.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				heightSpinner.setEnabled(heightCheckBox.getSelection());
				if (heightCheckBox.getSelection()) {
					widthCheckBox.setSelection(false);
					widthSpinner.setEnabled(false);
				}
			}
		});
		heightSpinner = new Spinner(container, SWT.NONE);
		heightSpinner.setMinimum(1);
		heightSpinner.setMaximum(20000);
		heightSpinner.setSelection(height);
		heightSpinner.setIncrement(100);
		heightSpinner.setEnabled(false);
		heightSpinner.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				height = heightSpinner.getSelection();
			}
		});

		// spacing factor label
		Label labelSpacingFactorLabel = new Label(container, SWT.NONE);
		labelSpacingFactorLabel.setText("Spacing factor:");

		// spacing factor selection
		Scale scale = new Scale(container, SWT.HORIZONTAL);
		scale.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		scale.setSelection(labelSpacingFactor);
		scale.setMinimum(1);
		scale.setMaximum(20);
		scale.setIncrement(1);
		scale.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				labelSpacingFactor = scale.getSelection();
			}
		});

		// spacing factor hint
		new Label(container, SWT.NONE);
		Label spacingHintLabel1 = new Label(container, SWT.NONE);
		spacingHintLabel1.setText("  Hint: Increasing the spacing factor may improve the generated");
		new Label(container, SWT.NONE);
		Label spacingHintLabel2 = new Label(container, SWT.NONE);
		spacingHintLabel2.setText("  image in case it contains overlapping labels.");

		setControl(container);
		setPageComplete(false);
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		this.formatSelectionCombo.forceFocus();
	}

	public ContextMapFormat getSelectedFormat() {
		return selectedFormat;
	}

	public int getLabelSpacingFactor() {
		return labelSpacingFactor;
	}

	public boolean takeWidth() {
		return widthCheckBox.getSelection();
	}

	public int getWidth() {
		return width;
	}

	public boolean takeHeight() {
		return heightCheckBox.getSelection();
	}

	public int getHeight() {
		return height;
	}

	@Override
	public boolean isPageComplete() {
		return this.selectedFormat != null;
	}

	@Override
	public void performHelp() {
		Program.launch("https://contextmapper.org/docs/context-map-generator/");
	}
}
