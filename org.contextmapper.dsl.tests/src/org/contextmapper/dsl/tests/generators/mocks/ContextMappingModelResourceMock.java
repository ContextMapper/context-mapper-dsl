/*
 * Copyright 2018 The Context Mapper Project Team
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
package org.contextmapper.dsl.tests.generators.mocks;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtext.EcoreUtil2;

public class ContextMappingModelResourceMock implements Resource {

	private ContextMappingModel contextMappingModel;
	private String filename;
	private String extension;
	private URI uri;
	private ResourceSet resourceSet;

	public ContextMappingModelResourceMock(ContextMappingModel contextMappingModel, String filename, String extension) {
		this.contextMappingModel = contextMappingModel;
		this.filename = filename;
		this.extension = extension;
		this.uri = null;
	}

	public ContextMappingModelResourceMock(ContextMappingModel contextMappingModel, URI uri) {
		this.contextMappingModel = contextMappingModel;
		this.uri = uri;
	}

	@Override
	public EList<Adapter> eAdapters() {
		return null;
	}

	@Override
	public boolean eDeliver() {
		return false;
	}

	@Override
	public void eSetDeliver(boolean deliver) {
	}

	@Override
	public void eNotify(Notification notification) {
	}

	@Override
	public ResourceSet getResourceSet() {
		return resourceSet;
	}
	
	public ContextMappingModelResourceMock setResourceSet(ResourceSet resourceSet) {
		this.resourceSet = resourceSet;
		return this;
	}

	@Override
	public URI getURI() {
		if (uri != null)
			return uri;
		return new URIMock(filename, extension);
	}

	@Override
	public void setURI(URI uri) {
	}

	@Override
	public long getTimeStamp() {
		return 0;
	}

	@Override
	public void setTimeStamp(long timeStamp) {
	}

	@Override
	public EList<EObject> getContents() {
		return this.contextMappingModel.eContents();
	}

	@Override
	public TreeIterator<EObject> getAllContents() {
		if (contextMappingModel == null)
			return emptyTreeIterator();
		return EcoreUtil2.eAll(contextMappingModel);
	}

	@Override
	public String getURIFragment(EObject eObject) {
		return null;
	}

	@Override
	public EObject getEObject(String uriFragment) {
		return null;
	}

	@Override
	public void save(Map<?, ?> options) throws IOException {
	}

	@Override
	public void load(Map<?, ?> options) throws IOException {
	}

	@Override
	public void save(OutputStream outputStream, Map<?, ?> options) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void load(InputStream inputStream, Map<?, ?> options) throws IOException {
	}

	@Override
	public boolean isTrackingModification() {
		return false;
	}

	@Override
	public void setTrackingModification(boolean isTrackingModification) {
	}

	@Override
	public boolean isModified() {
		return false;
	}

	@Override
	public void setModified(boolean isModified) {
	}

	@Override
	public boolean isLoaded() {
		return false;
	}

	@Override
	public void unload() {
	}

	@Override
	public void delete(Map<?, ?> options) throws IOException {
	}

	@Override
	public EList<Diagnostic> getErrors() {
		return null;
	}

	@Override
	public EList<Diagnostic> getWarnings() {
		return null;
	}

	private TreeIterator<EObject> emptyTreeIterator() {
		return new TreeIterator<EObject>() {

			@Override
			public boolean hasNext() {
				return false;
			}

			@Override
			public EObject next() {
				return null;
			}

			@Override
			public void prune() {
			}
		};
	}

}
