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
package org.contextmapper.dsl.ui.handler.wizard.pages.components;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ListDialog;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import com.google.common.collect.Lists;

/**
 * Component to choose an SCL (Service Cutter user representations) file.
 * 
 * @author Stefan Kapferer
 */
public class SCLFileChooser extends Composite {

	private Text fileText;
	private Button chooseButton;
	private IFile sclFile;

	public SCLFileChooser(Composite parent) {
		super(parent, SWT.NONE);
		createContent();
	}

	private void createContent() {
		GridLayout layout = new GridLayout(2, false);
		setLayout(layout);

		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;

		setLayoutData(gridData);

		fileText = new Text(this, SWT.SINGLE | SWT.BORDER);
		fileText.setLayoutData(gridData);
		fileText.setEditable(false);
		fileText.setEnabled(false);
		updateText();

		chooseButton = new Button(this, SWT.NONE);
		chooseButton.setText("...");
		chooseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				IFile newFile = open();
				if (newFile != null)
					sclFile = newFile;
				updateText();
			}
		});
	}

	private void updateText() {
		if (this.sclFile == null)
			this.fileText.setText("no SCL file selected");
		else
			this.fileText.setText(new WorkbenchLabelProvider().getText(sclFile));
	}

	public IFile open() {
		final List<IResource> sclResources = findSCLResources();
		ListDialog listDialog = new ListDialog(getShell());
		listDialog.setContentProvider(ArrayContentProvider.getInstance());
		listDialog.setLabelProvider(new LabelProvider());
		listDialog.setTitle("Choose SCL file");
		listDialog.setMessage("Select the SCL file with your user representations:");
		listDialog.setInput(sclResources);

		int result = listDialog.open();
		if (result == Window.OK) {
			Object[] selection = listDialog.getResult();
			if (selection != null && selection.length == 1 && selection[0] instanceof IFile) {
				return (IFile) selection[0];
			}
		}
		return null;
	}

	private List<IResource> findSCLResources() {
		final List<IResource> filteredResources = Lists.newArrayList();
		try {
			ResourcesPlugin.getWorkspace().getRoot().accept(new IResourceVisitor() {
				@Override
				public boolean visit(IResource resource) throws CoreException {
					if (resource instanceof IFile) {
						String fileExtension = ((IFile) resource).getFileExtension();
						if ("scl".equals(fileExtension)) {
							filteredResources.add(resource);
						}
					}
					return true;
				}
			});
		} catch (CoreException e) {
			throw new RuntimeException("Could not find SCL files.", e);
		}
		return filteredResources;
	}

	private class LabelProvider extends org.eclipse.jface.viewers.LabelProvider {

		private ILabelProvider delegate = new WorkbenchLabelProvider();

		@Override
		public String getText(Object object) {
			if (object instanceof IFile) {
				IFile file = (IFile) object;
				return delegate.getText(file) + " - " + file.getParent().getFullPath();
			}
			return delegate.getText(object);
		}

		@Override
		public Image getImage(Object element) {
			return delegate.getImage(element);
		}
	}

	public IFile getSCLFile() {
		return sclFile;
	}

	public void setSclFile(IFile sclFile) {
		this.sclFile = sclFile;
		updateText();
	}
}
