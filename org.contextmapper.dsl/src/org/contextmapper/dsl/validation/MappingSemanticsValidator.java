package org.contextmapper.dsl.validation;

import static org.contextmapper.dsl.validation.ValidationMessages.AGGREGATE_DOES_NOT_BELONG_TO_BOUNDED_CONTEXT;
import static org.contextmapper.dsl.validation.ValidationMessages.VALUE_OBJECT_DOES_NOT_BELONG_TO_AGGREGATE;
import static org.contextmapper.dsl.validation.ValidationMessages.MAPPED_BOUNDED_CONTEXT_IS_NOT_UPSTREAM;

import org.contextmapper.dsl.cml.CMLModelObjectsResolvingHelper;

import static org.contextmapper.dsl.validation.ValidationMessages.MAPPING_NAME_NOT_UNIQUE;

import org.contextmapper.dsl.contextMappingDSL.Aggregate;
import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.ContextMap;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingDSLPackage;
import org.contextmapper.dsl.contextMappingDSL.Mapping;
import org.contextmapper.dsl.contextMappingDSL.UpstreamDownstreamRelationship;
import org.contextmapper.tactic.dsl.tacticdsl.ValueObject;
import org.eclipse.xtext.validation.Check;
import org.eclipse.xtext.validation.EValidatorRegistrar;

public class MappingSemanticsValidator extends AbstractCMLValidator {

	@Override
	public void register(EValidatorRegistrar registrar) {
		// not needed for classes used as ComposedCheck
	}
	
	@Check
	public void ValidateMappingNameIsUnique(final Mapping mapping) {
		BoundedContext boundedContext = (BoundedContext) mapping.eContainer();
		
		if (boundedContext.getMappings().stream()
			.filter(mapp -> mapp.getName().equals(mapping.getName()))
			.count() > 1) {
			error(String.format(MAPPING_NAME_NOT_UNIQUE, mapping.getName()), 
					mapping, ContextMappingDSLPackage.Literals.MAPPING__NAME);
		}
	}
	
	@Check
	public void ValidateBoundedContextIsAccessible(final Mapping mapping) {
		BoundedContext mappedBoundedContext = mapping.getBoundedContext();
		BoundedContext mappingBoundedContext = (BoundedContext) mapping.eContainer();
		
		ContextMap contextMap = new CMLModelObjectsResolvingHelper(getRootCMLModel(mapping))
				.getContextMap(mappedBoundedContext);
		
		if (contextMap.getRelationships().stream()
			.filter(UpstreamDownstreamRelationship.class::isInstance)
			.map(UpstreamDownstreamRelationship.class::cast)
			.anyMatch(relationship -> relationship.getUpstream().equals(mappingBoundedContext)
								&& relationship.getDownstream().equals(mappedBoundedContext))) {
			error(String.format(MAPPED_BOUNDED_CONTEXT_IS_NOT_UPSTREAM, mappedBoundedContext.getName()), 
					mapping, ContextMappingDSLPackage.Literals.MAPPING__BOUNDED_CONTEXT);
		}
	}
	
	@Check
	public void ValidateAggregateBelongsToBoundedContext(final Mapping mapping) {
		BoundedContext boundedContext = mapping.getBoundedContext();
		Aggregate aggregate = mapping.getAggregate();
		
		if (boundedContext.getAggregates().stream()
				.noneMatch(agg -> agg.getName().equals(aggregate.getName()))) {
			error(String.format(AGGREGATE_DOES_NOT_BELONG_TO_BOUNDED_CONTEXT, boundedContext.getName()), 
					mapping, ContextMappingDSLPackage.Literals.MAPPING__AGGREGATE);
		}
	}
	
	@Check
	public void ValidateValueObjectBelongsToAggregate(final Mapping mapping) {
		BoundedContext boundedContext = mapping.getBoundedContext();
		ValueObject valueObject = mapping.getValueObject();
		
		Aggregate aggregate = boundedContext.getAggregates().stream()
			.filter(agg -> agg.getName().equals(mapping.getAggregate().getName()))
			.findAny()
			.orElse(null);
		
		if (aggregate != null && aggregate.getDomainObjects().stream()
				.filter(ValueObject.class::isInstance)
				.map(ValueObject.class::cast)
				.noneMatch(vo -> vo.getName().equals(valueObject.getName()))) {
			error(String.format(VALUE_OBJECT_DOES_NOT_BELONG_TO_AGGREGATE, aggregate.getName()), 
					mapping, ContextMappingDSLPackage.Literals.MAPPING__VALUE_OBJECT);
		}
	
	}
}
