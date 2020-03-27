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
import java.util.Set;

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
import org.eclipse.swt.widgets.Text;

public class DeriveSubdomainFromRequirementsWizardPage extends ContextMapperWizardPage {

	private String initialDomain;
	private Set<String> allDomains;

	private Combo comboDomains;
	private Text textSubdomain;
	private Composite container;

	public DeriveSubdomainFromRequirementsWizardPage(String initialDomain, Set<String> allDomains) {
		super("Subdomain Definition Page");
		this.initialDomain = initialDomain;
		this.allDomains = allDomains;
	}

	@Override
	public String getTitle() {
		return "Subdomain Definition";
	}

	@Override
	public String getDescription() {
		return "Define the name of the Subdomain to be created:";
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
		comboDomains.setItems(this.allDomains.toArray(new String[this.allDomains.size()]));
		comboDomains.select(new ArrayList<String>(this.allDomains).indexOf(this.initialDomain));
		comboDomains.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setPageComplete(isPageComplete());
			}
		});
		comboDomains.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				setPageComplete(isPageComplete());
			}
		});
		new AutoCompleteField(comboDomains, new ComboContentAdapter(), this.allDomains.toArray(new String[allDomains.size()]));

		Label subdomainNameLabel = new Label(container, SWT.NONE);
		subdomainNameLabel.setText("Subdomain name:");

		textSubdomain = new Text(container, SWT.BORDER | SWT.SINGLE);
		textSubdomain.setText("");
		textSubdomain.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		textSubdomain.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				setPageComplete(isPageComplete());
			}
		});

		setControl(container);
		setPageComplete(false);
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		this.textSubdomain.forceFocus();
	}

	public String getDomain() {
		return comboDomains.getText();
	}

	public String getSubdomain() {
		return textSubdomain.getText();
	}

	@Override
	public boolean isPageComplete() {
		return comboDomains.getText() != null && !"".equals(comboDomains.getText()) && textSubdomain.getText() != null && !"".equals(textSubdomain.getText());
	}

	@Override
	public void performHelp() {
		Program.launch("https://contextmapper.org/docs/derive-subdomains-from-user-requirements/");
	}
}
