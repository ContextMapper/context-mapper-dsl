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
package org.contextmapper.dsl.ui.quickfix.wizard.page;

import java.util.Arrays;
import java.util.Set;

import org.contextmapper.dsl.ui.handler.wizard.pages.ContextMapperWizardPage;
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
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import com.google.common.collect.Sets;

public class SelectUserStoryVerbsWizardPage extends ContextMapperWizardPage {

	private Composite container;
	private List list;
	private Set<String> initialVerbs;

	public SelectUserStoryVerbsWizardPage(Set<String> initialVerbs) {
		super("Story Verb / Operation Selection Page");
		this.initialVerbs = initialVerbs;
	}

	@Override
	public String getTitle() {
		return "Verb / Operation Selection";
	}

	@Override
	public String getDescription() {
		return "Select and / or add the verbs for the story split:";
	}

	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 2;

		// selection list
		list = new List(container, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
		for (String verb : initialVerbs)
			list.add(verb);
		GridData tableGridData = new GridData(GridData.FILL_HORIZONTAL);
		tableGridData.horizontalSpan = 2;
		tableGridData.grabExcessVerticalSpace = false;
		tableGridData.heightHint = 200;
		list.setLayoutData(tableGridData);
		list.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setPageComplete(isPageComplete());
			}
		});

		// adding new verb
		Text newVerbNameTextField = new Text(container, SWT.BORDER | SWT.SINGLE);
		GridData textGridData = new GridData(GridData.FILL_HORIZONTAL);
		newVerbNameTextField.setLayoutData(textGridData);
		Button addNewVerbButton = new Button(container, SWT.NONE);
		addNewVerbButton.setText("Add new verb ...");
		addNewVerbButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				if (newVerbNameTextField.getText() != null && !"".equals(newVerbNameTextField.getText().trim())) {
					String verb = formatVerb(newVerbNameTextField.getText());
					Set<String> existingVerbs = Sets.newHashSet(Arrays.asList(list.getItems()));
					if (!existingVerbs.contains(verb))
						list.add(verb);
				}
			}
		});

		setControl(container);
		setPageComplete(false);
	}

	private String formatVerb(String verb) {
		String s = verb.replace(" ", "_");
		if (s.substring(0, 1).matches("^[0-9]"))
			s = "_" + s;
		s = s.replaceAll("[^A-Za-z0-9_]", "");
		return s.length() > 1 ? s.substring(0, 1).toLowerCase() + s.substring(1) : s.toLowerCase();
	}

	public Set<String> getSelectedItems() {
		return Sets.newHashSet(Arrays.asList(list.getSelection()));
	}

	@Override
	public boolean isPageComplete() {
		return !Arrays.asList(list.getSelection()).isEmpty();
	}

	@Override
	public void performHelp() {
		Program.launch("https://contextmapper.org/docs/");
	}
}
