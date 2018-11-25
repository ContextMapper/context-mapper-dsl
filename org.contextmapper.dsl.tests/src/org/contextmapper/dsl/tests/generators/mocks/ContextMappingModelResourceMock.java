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
import org.eclipse.emf.ecore.util.EcoreUtil.ContentTreeIterator;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.parsetree.reconstr.impl.NodeIterator;

public class ContextMappingModelResourceMock implements Resource {

	private ContextMappingModel contextMappingModel;
	private String filename;
	private String extension;

	public ContextMappingModelResourceMock(ContextMappingModel contextMappingModel, String filename, String extension) {
		this.contextMappingModel = contextMappingModel;
		this.filename = filename;
		this.extension = extension;
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
		return null;
	}

	@Override
	public URI getURI() {
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

}
