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

import java.util.Set;

import org.contextmapper.dsl.validation.AbstractCMLValidator;
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
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class DeriveBoundedContextFromSubdomainsWizardPage extends ContextMapperWizardPage {

	private String initialBoundedContextName;
	private Set<String> existingBoundedContexts;

	private Combo comboBCs;
	private Composite container;

	private boolean hasError = true;

	public DeriveBoundedContextFromSubdomainsWizardPage(String initialBoundedContextName, Set<String> existingBoundedContexts) {
		super("Bounded Context Definition Page");
		this.initialBoundedContextName = initialBoundedContextName;
		this.existingBoundedContexts = existingBoundedContexts;
	}

	@Override
	public String getTitle() {
		return "Bounded Context Name";
	}

	@Override
	public String getDescription() {
		return "Choose the name of the Bounded Context to be created/updated:";
	}

	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 2;

		Label boundedContextLabel = new Label(container, SWT.NONE);
		boundedContextLabel.setText("Bounded Context:");

		// BC selection field
		comboBCs = new Combo(container, SWT.DROP_DOWN);
		comboBCs.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		comboBCs.setItems(this.existingBoundedContexts.toArray(new String[this.existingBoundedContexts.size()]));
		comboBCs.setText(initialBoundedContextName);
		comboBCs.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				validate();
				setPageComplete(isPageComplete());
			}
		});
		comboBCs.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				validate();
				setPageComplete(isPageComplete());
			}
		});
		new AutoCompleteField(comboBCs, new ComboContentAdapter(), this.existingBoundedContexts.toArray(new String[existingBoundedContexts.size()]));

		setControl(container);
		validate();
		setPageComplete(false);
	}

	private void validate() {
		setErrorMessage(null);
		hasError = false;

		if (!comboBCs.getText().matches(AbstractCMLValidator.ID_VALIDATION_PATTERN)) {
			setError("The domain name '" + comboBCs.getText() + "' is not valid. Allowed characters are: a-z, A-Z, 0-9, _");
			return;
		}
	}

	private void setError(String message) {
		hasError = true;
		setErrorMessage(message);
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		this.comboBCs.forceFocus();
	}

	public String getBoundedContextName() {
		return comboBCs.getText();
	}

	@Override
	public boolean isPageComplete() {
		return !hasError && comboBCs.getText() != null && !"".equals(comboBCs.getText());
	}

	@Override
	public void performHelp() {
		Program.launch("https://contextmapper.org/docs/rapid-ooad/");
	}
}
