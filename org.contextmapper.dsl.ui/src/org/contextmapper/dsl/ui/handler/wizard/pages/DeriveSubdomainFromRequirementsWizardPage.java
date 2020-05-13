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

import java.util.ArrayList;
import java.util.Map;
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

import com.google.common.collect.Sets;

public class DeriveSubdomainFromRequirementsWizardPage extends ContextMapperWizardPage {

	private String initialDomain;
	private Map<String, Set<String>> domainSubdomainMapping;

	private Combo comboDomains;
	private Combo comboSubdomain;
	private AutoCompleteField subdomainAutoCompleteField;
	private Composite container;

	private boolean hasError = true;

	public DeriveSubdomainFromRequirementsWizardPage(String initialDomain, Map<String, Set<String>> domainSubdomainMapping) {
		super("Subdomain Definition Page");
		this.initialDomain = initialDomain;
		this.domainSubdomainMapping = domainSubdomainMapping;
	}

	@Override
	public String getTitle() {
		return "Domain and Subdomain Definition";
	}

	@Override
	public String getDescription() {
		return "Choose the name of the Domain and Subdomain to be created/updated:";
	}

	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 2;

		Label domainNameLabel = new Label(container, SWT.NONE);
		domainNameLabel.setText("Domain:");

		// domain selection field
		comboDomains = new Combo(container, SWT.DROP_DOWN);
		comboDomains.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		comboDomains.setItems(this.domainSubdomainMapping.keySet().toArray(new String[this.domainSubdomainMapping.size()]));
		comboDomains.select(new ArrayList<String>(this.domainSubdomainMapping.keySet()).indexOf(this.initialDomain));
		comboDomains.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateSubdomainCombo(domainSubdomainMapping.get(comboDomains.getText()));
				validate();
				setPageComplete(isPageComplete());
			}
		});
		comboDomains.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				validate();
				setPageComplete(isPageComplete());
			}
		});
		new AutoCompleteField(comboDomains, new ComboContentAdapter(), this.domainSubdomainMapping.keySet().toArray(new String[domainSubdomainMapping.size()]));

		Label subdomainNameLabel = new Label(container, SWT.NONE);
		subdomainNameLabel.setText("Subdomain name:");

		Set<String> initialSubdomains = domainSubdomainMapping.get(initialDomain);
		if (initialSubdomains == null)
			initialSubdomains = Sets.newHashSet();
		comboSubdomain = new Combo(container, SWT.DROP_DOWN);
		comboSubdomain.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		comboSubdomain.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				validate();
				setPageComplete(isPageComplete());
			}
		});
		comboSubdomain.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				validate();
				setPageComplete(isPageComplete());
			}
		});
		this.subdomainAutoCompleteField = new AutoCompleteField(comboSubdomain, new ComboContentAdapter(), initialSubdomains.toArray(new String[initialSubdomains.size()]));
		updateSubdomainCombo(initialSubdomains);

		setControl(container);
		validate();
		setPageComplete(false);
	}

	private void validate() {
		setErrorMessage(null);
		hasError = false;

		if (!comboDomains.getText().matches(AbstractCMLValidator.ID_VALIDATION_PATTERN)) {
			setError("The domain name '" + comboDomains.getText() + "' is not valid. Allowed characters are: a-z, A-Z, 0-9, _");
			return;
		}
		if (!comboSubdomain.getText().matches(AbstractCMLValidator.ID_VALIDATION_PATTERN)) {
			setError("The domain name '" + comboSubdomain.getText() + "' is not valid. Allowed characters are: a-z, A-Z, 0-9, _");
			return;
		}
	}

	private void setError(String message) {
		hasError = true;
		setErrorMessage(message);
	}

	private void updateSubdomainCombo(Set<String> subdomains) {
		if (subdomains == null) {
			comboSubdomain.setItems(new String[0]);
			subdomainAutoCompleteField.setProposals(new String[0]);
		}

		String[] items = subdomains.toArray(new String[subdomains.size()]);
		comboSubdomain.setItems(items);
		subdomainAutoCompleteField.setProposals(items);
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (this.initialDomain != null && !"".equals(this.initialDomain))
			this.comboSubdomain.forceFocus();
		else
			this.comboDomains.forceFocus();
	}

	public String getDomain() {
		return comboDomains.getText();
	}

	public String getSubdomain() {
		return comboSubdomain.getText();
	}

	@Override
	public boolean isPageComplete() {
		return !hasError && comboDomains.getText() != null && !"".equals(comboDomains.getText()) && comboSubdomain.getText() != null && !"".equals(comboSubdomain.getText());
	}

	@Override
	public void performHelp() {
		Program.launch("https://contextmapper.org/docs/rapid-ooad/");
	}
}
