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

import java.util.HashSet;
import java.util.Set;

import org.contextmapper.dsl.generator.contextmap.ContextMapFormat;
import org.contextmapper.dsl.ui.handler.wizard.GenerateContextMapContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Spinner;

public class GenerateContextMapWizardPage extends ContextMapperWizardPage {

	private Composite container;
	private Button widthCheckBox;
	private Spinner widthSpinner;
	private Button heightCheckBox;
	private Spinner heightSpinner;
	private Button generateLabelsCheckBox;
	private Button clusterTeamsCheckBox;

	private GenerateContextMapContext context;
	private Set<ContextMapFormat> selectedFormats;
	private int labelSpacingFactor;
	private int width;
	private int height;

	public GenerateContextMapWizardPage(GenerateContextMapContext context) {
		super("Generate Context Map Configuration Page");
		this.context = context;
		this.selectedFormats = new HashSet<>();
		this.selectedFormats.addAll(context.getFormats());
		this.labelSpacingFactor = context.getLabelSpacingFactor();
		this.width = context.getWidth();
		this.height = context.getHeight();
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
		formatSelectionLabel.setText("Generated formats:");

		// format selection checkboxes
		Group formatSelectionGroup = new Group(container, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		formatSelectionGroup.setLayout(gridLayout);
		formatSelectionGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		SelectionListener formatSelectionListener = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				Button button = ((Button) event.widget);
				ContextMapFormat format = ContextMapFormat.valueOf(button.getText());
				if (button.getSelection())
					selectedFormats.add(format);
				else if (selectedFormats.contains(format))
					selectedFormats.remove(format);
				setPageComplete(isPageComplete());
			};
		};

		for (ContextMapFormat format : ContextMapFormat.values()) {
			Button button = new Button(formatSelectionGroup, SWT.CHECK);
			button.setText(format.toString());
			button.setSelection(true);
			button.addSelectionListener(formatSelectionListener);
		}

		// fix width to custom value
		widthCheckBox = new Button(container, SWT.CHECK);
		widthCheckBox.setText("Fix image width:    ");
		widthCheckBox.setSelection(context.isFixWidth());
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
		widthSpinner.setEnabled(context.isFixWidth());
		widthSpinner.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				width = widthSpinner.getSelection();
			}
		});

		// fix height to custom value
		heightCheckBox = new Button(container, SWT.CHECK);
		heightCheckBox.setText("Fix image height:");
		heightCheckBox.setSelection(context.isFixHeight());
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
		heightSpinner.setEnabled(context.isFixHeight());
		heightSpinner.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				height = heightSpinner.getSelection();
			}
		});

		// generate labels checkbox
		Label generateLabelsLabel = new Label(container, SWT.NONE);
		generateLabelsLabel.setText("Generate labels:");
		generateLabelsCheckBox = new Button(container, SWT.CHECK);
		generateLabelsCheckBox.setText("Labels for relationship names and implementation technologies");
		generateLabelsCheckBox.setSelection(context.generateAdditionalLabels());

		// cluster teams checkbox
		Label clusterTeamsLabel = new Label(container, SWT.NONE);
		clusterTeamsLabel.setText("Cluster team contexts:");
		clusterTeamsCheckBox = new Button(container, SWT.CHECK);
		clusterTeamsCheckBox.setText("Separate team/generic Bounded Contexts into clusters (team maps only)");
		clusterTeamsCheckBox.setSelection(context.clusterTeams());

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
	}

	public Set<ContextMapFormat> getSelectedFormats() {
		return new HashSet<>(selectedFormats);
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

	public boolean generateLabels() {
		return generateLabelsCheckBox.getSelection();
	}

	public boolean clusterTeams() {
		return clusterTeamsCheckBox.getSelection();
	}

	@Override
	public boolean isPageComplete() {
		return this.selectedFormats.size() > 0;
	}

	@Override
	public void performHelp() {
		Program.launch("https://contextmapper.org/docs/context-map-generator/");
	}
}
