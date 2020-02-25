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

import org.contextmapper.dsl.ui.handler.wizard.pages.components.FreemarkerFileChooser;
import org.eclipse.core.resources.IFile;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class GenerateGenericTextFileWizardPage extends ContextMapperWizardPage {

	private Composite container;
	private FreemarkerFileChooser fileChooser;
	private Text targetFileNameTextBox;

	public GenerateGenericTextFileWizardPage() {
		super("Generate Arbitrary Textual File with Freemarker");
	}

	@Override
	public String getTitle() {
		return "Generic Text File Generator (Freemarker)";
	}

	@Override
	public String getDescription() {
		return "Generate Arbitrary Textual File with Freemarker";
	}

	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.verticalSpacing = 10;
		container.setLayout(layout);

		// Freemarker template selection
		Label fileSelectionLabel = new Label(container, SWT.NONE);
		fileSelectionLabel.setText("Freemarker template:");
		fileChooser = new FreemarkerFileChooser(container);

		// target filename
		Label targetFileNameLabel = new Label(container, SWT.NONE);
		targetFileNameLabel.setText("Target file name:");
		targetFileNameTextBox = new Text(container, SWT.BORDER | SWT.SINGLE);
		targetFileNameTextBox.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		targetFileNameTextBox.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				setPageComplete(isPageComplete());
			}
		});

		setControl(container);
		setPageComplete(false);
	}

	public IFile getFile() {
		return fileChooser.getFile();
	}

	public String getTargetFileName() {
		return targetFileNameTextBox.getText();
	}

	@Override
	public boolean isPageComplete() {
		return fileChooser.getFile() != null && fileChooser.getFile().exists() && !"".equals(targetFileNameTextBox.getText());
	}

	@Override
	public void performHelp() {
		Program.launch("https://contextmapper.org/docs/generic-template-based-generator/");
	}
}
