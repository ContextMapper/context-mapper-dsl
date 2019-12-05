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

import static ch.hsr.servicecutter.model.criteria.CouplingCriterion.AVAILABILITY;
import static ch.hsr.servicecutter.model.criteria.CouplingCriterion.CONSISTENCY;
import static ch.hsr.servicecutter.model.criteria.CouplingCriterion.CONSISTENCY_CONSTRAINT;
import static ch.hsr.servicecutter.model.criteria.CouplingCriterion.CONTENT_VOLATILITY;
import static ch.hsr.servicecutter.model.criteria.CouplingCriterion.IDENTITY_LIFECYCLE;
import static ch.hsr.servicecutter.model.criteria.CouplingCriterion.LATENCY;
import static ch.hsr.servicecutter.model.criteria.CouplingCriterion.PREDEFINED_SERVICE;
import static ch.hsr.servicecutter.model.criteria.CouplingCriterion.SECURITY_CONSTRAINT;
import static ch.hsr.servicecutter.model.criteria.CouplingCriterion.SECURITY_CONTEXUALITY;
import static ch.hsr.servicecutter.model.criteria.CouplingCriterion.SECURITY_CRITICALITY;
import static ch.hsr.servicecutter.model.criteria.CouplingCriterion.SEMANTIC_PROXIMITY;
import static ch.hsr.servicecutter.model.criteria.CouplingCriterion.SHARED_OWNER;
import static ch.hsr.servicecutter.model.criteria.CouplingCriterion.STORAGE_SIMILARITY;
import static ch.hsr.servicecutter.model.criteria.CouplingCriterion.STRUCTURAL_VOLATILITY;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.contextmapper.dsl.ui.handler.wizard.pages.components.SCLFileChooser;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import ch.hsr.servicecutter.solver.SolverAlgorithm;
import ch.hsr.servicecutter.solver.SolverConfiguration;
import ch.hsr.servicecutter.solver.SolverPriority;

public class GenerateNewServiceCutContextMapWizardPage extends ContextMapperWizardPage {

	private Composite container;
	private SolverConfiguration solverConfiguration;
	private SCLFileChooser sclFileChooser;
	private IFile initialSCLFile;
	private Map<String, Combo> allPriorityCombos = new HashMap<>();

	private Combo algorithmSelectionCombo;

	public GenerateNewServiceCutContextMapWizardPage(SolverConfiguration solverConfiguration, IFile initialSCLFile) {
		super("Service Cutter Solver Configuration Page");
		this.solverConfiguration = solverConfiguration;
		this.initialSCLFile = initialSCLFile;
	}

	@Override
	public String getTitle() {
		return "New Service Cut (Context Map) Generation";
	}

	@Override
	public String getDescription() {
		return "Configure the Service Cutter Solver";
	}

	@Override
	public void createControl(Composite parent) {
		Composite mainComposite = new Composite(parent, SWT.NONE);
		mainComposite.setLayout(GridLayoutFactory.fillDefaults().create());

		ScrolledComposite scrollComp = new ScrolledComposite(mainComposite, SWT.V_SCROLL);
		scrollComp.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).hint(SWT.DEFAULT, 500).create());
		scrollComp.setLayout(new GridLayout(1, false));
		scrollComp.setExpandHorizontal(true);
		scrollComp.setExpandVertical(true);
		scrollComp.setAlwaysShowScrollBars(true);

		container = new Composite(scrollComp, SWT.NONE);
		scrollComp.setContent(container);

		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.verticalSpacing = 10;
		container.setLayout(layout);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		// SCL file selection
		new Label(container, SWT.NONE).setText("User representations:");
		sclFileChooser = new SCLFileChooser(container);
		sclFileChooser.setSclFile(initialSCLFile);

		// algorithm label
		new Label(container, SWT.NONE).setText("Algorithm:");

		// algorithm selection field
		algorithmSelectionCombo = new Combo(container, SWT.DROP_DOWN | SWT.READ_ONLY);
		algorithmSelectionCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		List<String> selectionStrings = Arrays.asList(SolverAlgorithm.values()).stream().map(l -> l.toString()).collect(Collectors.toList());
		algorithmSelectionCombo.setItems(selectionStrings.toArray(new String[selectionStrings.size()]));
		algorithmSelectionCombo.select(selectionStrings.indexOf(solverConfiguration.getAlgorithm().toString()));
		algorithmSelectionCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				solverConfiguration.setAlgorithm(SolverAlgorithm.valueOf(algorithmSelectionCombo.getText()));
				setPageComplete(isPageComplete());
			}
		});

		// Functional/Domain Model Criteria
		Group functionalCriteriaGroup = new Group(container, SWT.NONE);
		GridLayout functionalGridLayout = new GridLayout();
		functionalGridLayout.numColumns = 2;
		functionalGridLayout.makeColumnsEqualWidth = true;
		functionalCriteriaGroup.setLayout(functionalGridLayout);
		GridData functionalCriteriaGroupGridData = new GridData(GridData.FILL_HORIZONTAL);
		functionalCriteriaGroupGridData.horizontalSpan = 2;
		functionalCriteriaGroup.setLayoutData(functionalCriteriaGroupGridData);
		functionalCriteriaGroup.setText("Functional / Domain Model Criteria");

		createCriteriaPrioritySelectionCombo(functionalCriteriaGroup, IDENTITY_LIFECYCLE);
		createCriteriaPrioritySelectionCombo(functionalCriteriaGroup, SEMANTIC_PROXIMITY);
		createCriteriaPrioritySelectionCombo(functionalCriteriaGroup, STRUCTURAL_VOLATILITY);
		createCriteriaPrioritySelectionCombo(functionalCriteriaGroup, CONTENT_VOLATILITY);

		new Label(functionalCriteriaGroup, SWT.NONE);
		Button prioritizeFunctionalGroupButton = new Button(functionalCriteriaGroup, SWT.NONE);
		prioritizeFunctionalGroupButton.setText("Prioritize functional / domain model criteria");
		prioritizeFunctionalGroupButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		prioritizeFunctionalGroupButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				for (Map.Entry<String, Combo> priorityComboEntry : allPriorityCombos.entrySet()) {
					changePriority(priorityComboEntry.getKey(), SolverPriority.S);
				}
				changePriority(IDENTITY_LIFECYCLE, SolverPriority.XL);
				changePriority(SEMANTIC_PROXIMITY, SolverPriority.XL);
				changePriority(STRUCTURAL_VOLATILITY, SolverPriority.XL);
				changePriority(CONTENT_VOLATILITY, SolverPriority.XL);
			}
		});

		// Runtime Quality Criteria
		Group runtimeQualityCriteriaGroup = new Group(container, SWT.NONE);
		GridLayout runtimeQualityGridLayout = new GridLayout();
		runtimeQualityGridLayout.numColumns = 2;
		runtimeQualityGridLayout.makeColumnsEqualWidth = true;
		runtimeQualityCriteriaGroup.setLayout(runtimeQualityGridLayout);
		GridData runtimeQualityCriteriaGroupGridData = new GridData(GridData.FILL_HORIZONTAL);
		runtimeQualityCriteriaGroupGridData.horizontalSpan = 2;
		runtimeQualityCriteriaGroup.setLayoutData(runtimeQualityCriteriaGroupGridData);
		runtimeQualityCriteriaGroup.setText("Runtime Quality Criteria");

		createCriteriaPrioritySelectionCombo(runtimeQualityCriteriaGroup, LATENCY);
		createCriteriaPrioritySelectionCombo(runtimeQualityCriteriaGroup, AVAILABILITY);
		createCriteriaPrioritySelectionCombo(runtimeQualityCriteriaGroup, SECURITY_CONTEXUALITY);
		createCriteriaPrioritySelectionCombo(runtimeQualityCriteriaGroup, SECURITY_CRITICALITY);
		createCriteriaPrioritySelectionCombo(runtimeQualityCriteriaGroup, SECURITY_CONSTRAINT);

		new Label(runtimeQualityCriteriaGroup, SWT.NONE);
		Button prioritizeRuntimeQualityGroupButton = new Button(runtimeQualityCriteriaGroup, SWT.NONE);
		prioritizeRuntimeQualityGroupButton.setText("Prioritize runtime quality criteria");
		prioritizeRuntimeQualityGroupButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		prioritizeRuntimeQualityGroupButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				for (Map.Entry<String, Combo> priorityComboEntry : allPriorityCombos.entrySet()) {
					changePriority(priorityComboEntry.getKey(), SolverPriority.S);
				}
				changePriority(LATENCY, SolverPriority.XL);
				changePriority(AVAILABILITY, SolverPriority.XL);
				changePriority(SECURITY_CONTEXUALITY, SolverPriority.XL);
				changePriority(SECURITY_CRITICALITY, SolverPriority.XL);
				changePriority(SECURITY_CONSTRAINT, SolverPriority.XL);
			}
		});
		
		// Data Criteria
		Group dataCriteriaGroup = new Group(container, SWT.NONE);
		GridLayout dataGridLayout = new GridLayout();
		dataGridLayout.numColumns = 2;
		dataGridLayout.makeColumnsEqualWidth = true;
		dataCriteriaGroup.setLayout(dataGridLayout);
		GridData dataCriteriaGroupGridData = new GridData(GridData.FILL_HORIZONTAL);
		dataCriteriaGroupGridData.horizontalSpan = 2;
		dataCriteriaGroup.setLayoutData(dataCriteriaGroupGridData);
		dataCriteriaGroup.setText("Data Criteria");

		createCriteriaPrioritySelectionCombo(dataCriteriaGroup, CONSISTENCY);
		createCriteriaPrioritySelectionCombo(dataCriteriaGroup, CONSISTENCY_CONSTRAINT);
		createCriteriaPrioritySelectionCombo(dataCriteriaGroup, STORAGE_SIMILARITY);
		
		new Label(dataCriteriaGroup, SWT.NONE);
		Button prioritizeDataGroupButton = new Button(dataCriteriaGroup, SWT.NONE);
		prioritizeDataGroupButton.setText("Prioritize data criteria");
		prioritizeDataGroupButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		prioritizeDataGroupButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				for (Map.Entry<String, Combo> priorityComboEntry : allPriorityCombos.entrySet()) {
					changePriority(priorityComboEntry.getKey(), SolverPriority.S);
				}
				changePriority(CONSISTENCY, SolverPriority.XL);
				changePriority(CONSISTENCY_CONSTRAINT, SolverPriority.XL);
				changePriority(STORAGE_SIMILARITY, SolverPriority.XL);
			}
		});

		// Organizational Criteria
		Group organizationalCriteriaGroup = new Group(container, SWT.NONE);
		GridLayout organizationalGridLayout = new GridLayout();
		organizationalGridLayout.numColumns = 2;
		organizationalGridLayout.makeColumnsEqualWidth = true;
		organizationalCriteriaGroup.setLayout(organizationalGridLayout);
		GridData organizationalCriteriaGroupGridData = new GridData(GridData.FILL_HORIZONTAL);
		organizationalCriteriaGroupGridData.horizontalSpan = 2;
		organizationalCriteriaGroup.setLayoutData(organizationalCriteriaGroupGridData);
		organizationalCriteriaGroup.setText("Organizational Criteria");

		createCriteriaPrioritySelectionCombo(organizationalCriteriaGroup, SHARED_OWNER);
		createCriteriaPrioritySelectionCombo(organizationalCriteriaGroup, PREDEFINED_SERVICE);
		
		new Label(organizationalCriteriaGroup, SWT.NONE);
		Button prioritizeOrganizationalGroupButton = new Button(organizationalCriteriaGroup, SWT.NONE);
		prioritizeOrganizationalGroupButton.setText("Prioritize organizational criteria");
		prioritizeOrganizationalGroupButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		prioritizeOrganizationalGroupButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				for (Map.Entry<String, Combo> priorityComboEntry : allPriorityCombos.entrySet()) {
					changePriority(priorityComboEntry.getKey(), SolverPriority.S);
				}
				changePriority(SHARED_OWNER, SolverPriority.XL);
				changePriority(PREDEFINED_SERVICE, SolverPriority.XL);
			}
		});

		Button restoreDefaultPrioritiesButton = new Button(container, SWT.NONE);
		restoreDefaultPrioritiesButton.setText("Restore default priorities");
		restoreDefaultPrioritiesButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				for (Map.Entry<String, Combo> priorityComboEntry : allPriorityCombos.entrySet()) {
					changePriority(priorityComboEntry.getKey(), SolverPriority.M);
				}
			}
		});

		scrollComp.setMinSize(container.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		setControl(mainComposite);
		setPageComplete(false);
	}

	private void createCriteriaPrioritySelectionCombo(Composite parent, String criterionId) {
		new Label(parent, SWT.NONE).setText(criterionId + ": ");
		Combo priorityCombo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
		priorityCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		List<String> selectionStrings = Arrays.asList(SolverPriority.values()).stream().map(l -> l.name()).collect(Collectors.toList());
		priorityCombo.setItems(selectionStrings.toArray(new String[selectionStrings.size()]));
		priorityCombo.select(selectionStrings.indexOf(solverConfiguration.getPriorities().get(criterionId).name()));
		priorityCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				solverConfiguration.setPriority(criterionId, SolverPriority.valueOf(priorityCombo.getText()));
				setPageComplete(isPageComplete());
			}
		});
		allPriorityCombos.put(criterionId, priorityCombo);
	}

	private void changePriority(String criterionId, SolverPriority priority) {
		Combo priorityCombo = allPriorityCombos.get(criterionId);
		priorityCombo.select(priorityCombo.indexOf(priority.name()));
		solverConfiguration.setPriority(criterionId, SolverPriority.valueOf(priorityCombo.getText()));
		setPageComplete(isPageComplete());
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
	}

	public SolverConfiguration getSolverConfiguration() {
		return solverConfiguration;
	}

	public IFile getSCLFile() {
		return sclFileChooser.getSCLFile();
	}

	@Override
	public boolean isPageComplete() {
		return true;
	}

	@Override
	public void performHelp() {
		Program.launch("https://contextmapper.org/docs/service-cutter-context-map-suggestions/");
	}
}
