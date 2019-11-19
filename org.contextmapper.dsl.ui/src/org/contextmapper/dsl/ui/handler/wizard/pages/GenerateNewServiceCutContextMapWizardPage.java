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
import java.util.List;
import java.util.stream.Collectors;

import org.contextmapper.dsl.ui.handler.wizard.pages.components.SCLFileChooser;
import org.eclipse.core.resources.IFile;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
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
		container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.verticalSpacing = 10;
		container.setLayout(layout);

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

		// Cohesiveness Criteria
		Group cohesivenessCriteriaGroup = new Group(container, SWT.NONE);
		GridLayout cohesivenessGridLayout = new GridLayout();
		cohesivenessGridLayout.numColumns = 2;
		cohesivenessGridLayout.makeColumnsEqualWidth = true;
		cohesivenessCriteriaGroup.setLayout(cohesivenessGridLayout);
		GridData cohesivenessCriteriaGroupGridData = new GridData(GridData.FILL_HORIZONTAL);
		cohesivenessCriteriaGroupGridData.horizontalSpan = 2;
		cohesivenessCriteriaGroup.setLayoutData(cohesivenessCriteriaGroupGridData);
		cohesivenessCriteriaGroup.setText("Cohesiveness Criteria");

		createCriteriaPrioritySelectionCombo(cohesivenessCriteriaGroup, IDENTITY_LIFECYCLE);
		createCriteriaPrioritySelectionCombo(cohesivenessCriteriaGroup, SEMANTIC_PROXIMITY);
		createCriteriaPrioritySelectionCombo(cohesivenessCriteriaGroup, SHARED_OWNER);
		createCriteriaPrioritySelectionCombo(cohesivenessCriteriaGroup, LATENCY);
		createCriteriaPrioritySelectionCombo(cohesivenessCriteriaGroup, SECURITY_CONTEXUALITY);

		// Compatibility Criteria
		Group compatibilityCriteriaGroup = new Group(container, SWT.NONE);
		GridLayout compatibilityGridLayout = new GridLayout();
		compatibilityGridLayout.numColumns = 2;
		compatibilityGridLayout.makeColumnsEqualWidth = true;
		compatibilityCriteriaGroup.setLayout(compatibilityGridLayout);
		GridData compatibilityCriteriaGroupGridData = new GridData(GridData.FILL_HORIZONTAL);
		compatibilityCriteriaGroupGridData.horizontalSpan = 2;
		compatibilityCriteriaGroup.setLayoutData(compatibilityCriteriaGroupGridData);
		compatibilityCriteriaGroup.setText("Compatibility Criteria");

		createCriteriaPrioritySelectionCombo(compatibilityCriteriaGroup, STRUCTURAL_VOLATILITY);
		createCriteriaPrioritySelectionCombo(compatibilityCriteriaGroup, CONSISTENCY);
		createCriteriaPrioritySelectionCombo(compatibilityCriteriaGroup, AVAILABILITY);
		createCriteriaPrioritySelectionCombo(compatibilityCriteriaGroup, CONTENT_VOLATILITY);
		createCriteriaPrioritySelectionCombo(compatibilityCriteriaGroup, STORAGE_SIMILARITY);
		createCriteriaPrioritySelectionCombo(compatibilityCriteriaGroup, SECURITY_CRITICALITY);

		// Constraints Criteria
		Group constraintsCriteriaGroup = new Group(container, SWT.NONE);
		GridLayout constraintsGridLayout = new GridLayout();
		constraintsGridLayout.numColumns = 2;
		constraintsGridLayout.makeColumnsEqualWidth = true;
		constraintsCriteriaGroup.setLayout(constraintsGridLayout);
		GridData constraintsCriteriaGroupGridData = new GridData(GridData.FILL_HORIZONTAL);
		constraintsCriteriaGroupGridData.horizontalSpan = 2;
		constraintsCriteriaGroup.setLayoutData(constraintsCriteriaGroupGridData);
		constraintsCriteriaGroup.setText("Constraints Criteria");

		createCriteriaPrioritySelectionCombo(constraintsCriteriaGroup, CONSISTENCY_CONSTRAINT);
		createCriteriaPrioritySelectionCombo(constraintsCriteriaGroup, PREDEFINED_SERVICE);
		createCriteriaPrioritySelectionCombo(constraintsCriteriaGroup, SECURITY_CONSTRAINT);

		setControl(container);
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
