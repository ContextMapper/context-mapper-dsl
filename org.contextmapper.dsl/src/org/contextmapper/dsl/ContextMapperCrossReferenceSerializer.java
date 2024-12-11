package org.contextmapper.dsl;

import com.google.inject.Inject;
import org.contextmapper.tactic.dsl.tacticdsl.Reference;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.Assignment;
import org.eclipse.xtext.CrossReference;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.serializer.diagnostic.ISerializationDiagnostic;
import org.eclipse.xtext.serializer.tokens.CrossReferenceSerializer;
import org.eclipse.xtext.serializer.tokens.ICrossReferenceSerializer;

public class ContextMapperCrossReferenceSerializer implements ICrossReferenceSerializer {

	private static final String REF_AT = "@";
	private final ICrossReferenceSerializer delegate;

	@Inject
	public ContextMapperCrossReferenceSerializer(CrossReferenceSerializer delegate) {
		this.delegate = delegate;
	}

	@Override
	public boolean isValid(EObject context, CrossReference crossref, EObject target, INode node,
			ISerializationDiagnostic.Acceptor errorAcceptor) {
		return delegate.isValid(context, crossref, target, node, errorAcceptor);
	}

	@Override
	public String serializeCrossRef(EObject context, CrossReference crossref, EObject target, INode node,
			ISerializationDiagnostic.Acceptor errorAcceptor) {
		var serialized = delegate.serializeCrossRef(context, crossref, target, node, errorAcceptor);
		if (serialized != null && !serialized.startsWith(REF_AT)) {
			if (node == null || !(node.hasPreviousSibling() && node.getPreviousSibling().getText().contains(REF_AT))) {
				if (context instanceof Reference) {
					serialized = REF_AT + serialized;
				} else {
					if (crossref.eContainer() instanceof Assignment) {
						Assignment assignment = (Assignment) crossref.eContainer();
						var feature = assignment.getFeature();
						if (feature.equals("traits")) {
							serialized = REF_AT + serialized;
						}
					}
				}
			}
		}
		return serialized;
	}
}
