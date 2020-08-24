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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class NewBoundedContextNameWizardPage extends ContextMapperWizardPage {

	private String initialBoundedContextName;

	private Text boundedContextName;
	private Composite container;

	public NewBoundedContextNameWizardPage(String initialBoundedContextName) {
		super("Bounded Context Name");
		this.initialBoundedContextName = initialBoundedContextName;
	}

	@Override
	public String getTitle() {
		return "New Bounded Context Name";
	}

	@Override
	public String getDescription() {
		return "Please define a name for the new Bounded Context.";
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

		setControl(container);
		setPageComplete(false);
	}

	public String getBoundedContextName() {
		return boundedContextName.getText();
	}

	@Override
	public boolean isPageComplete() {
		return boundedContextName.getText() != null && !"".equals(boundedContextName.getText());
	}

	@Override
	public void performHelp() {
		Program.launch("https://contextmapper.org/docs/systematic-service-decomposition/");
	}
}
