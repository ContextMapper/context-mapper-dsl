package org.contextmapper.dsl.validation;

import static org.contextmapper.dsl.validation.ValidationMessages.RELATIONSHIP_CONTEXT_NOT_ON_MAP_ERROR_MESSAGE;

import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.ContextMap;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingDSLPackage;
import org.contextmapper.dsl.contextMappingDSL.Relationship;
import org.contextmapper.dsl.contextMappingDSL.SymmetricRelationship;
import org.contextmapper.dsl.contextMappingDSL.UpstreamDownstreamRelationship;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.xtext.validation.AbstractDeclarativeValidator;
import org.eclipse.xtext.validation.Check;
import org.eclipse.xtext.validation.EValidatorRegistrar;

public class ContextMapSemanticsValidator extends AbstractDeclarativeValidator {

	@Override
	public void register(EValidatorRegistrar registrar) {
		// not needed for classes used as ComposedCheck
	}

	@Check
	public void validateThatRelationshipContextArePartOfMap(final ContextMap map) {
		for (Relationship relationship : map.getRelationships()) {
			BoundedContext context1 = null;
			BoundedContext context2 = null;
			EReference attributeRelContext1 = null;
			EReference attributeRelContext2 = null;
			if (relationship instanceof SymmetricRelationship) {
				context1 = ((SymmetricRelationship) relationship).getParticipant1();
				context2 = ((SymmetricRelationship) relationship).getParticipant2();
				attributeRelContext1 = ContextMappingDSLPackage.Literals.SYMMETRIC_RELATIONSHIP__PARTICIPANT1;
				attributeRelContext2 = ContextMappingDSLPackage.Literals.SYMMETRIC_RELATIONSHIP__PARTICIPANT2;
			} else if (relationship instanceof UpstreamDownstreamRelationship) {
				context1 = ((UpstreamDownstreamRelationship) relationship).getUpstream().getContext();
				context2 = ((UpstreamDownstreamRelationship) relationship).getDownstream().getContext();
				attributeRelContext1 = ContextMappingDSLPackage.Literals.UPSTREAM_DOWNSTREAM_RELATIONSHIP__UPSTREAM;
				attributeRelContext2 = ContextMappingDSLPackage.Literals.UPSTREAM_DOWNSTREAM_RELATIONSHIP__DOWNSTREAM;
			}

			if (context1 != null && !isContextPartOfMap(map, context1))
				error(String.format(RELATIONSHIP_CONTEXT_NOT_ON_MAP_ERROR_MESSAGE, context1.getName()), relationship,
						attributeRelContext1);
			if (context2 != null && !isContextPartOfMap(map, context2))
				error(String.format(RELATIONSHIP_CONTEXT_NOT_ON_MAP_ERROR_MESSAGE, context2.getName()), relationship,
						attributeRelContext2);
		}
	}

	private boolean isContextPartOfMap(ContextMap map, BoundedContext context) {
		return map.getBoundedContexts().contains(context);
	}
}
