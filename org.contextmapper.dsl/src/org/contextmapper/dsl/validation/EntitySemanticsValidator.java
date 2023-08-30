package org.contextmapper.dsl.validation;

import static org.contextmapper.dsl.validation.ValidationMessages.AGGREGATE_DOES_NOT_BELONG_TO_BOUNDED_CONTEXT;
import static org.contextmapper.dsl.validation.ValidationMessages.AGGREGATE_ROOT_CANNOT_USE_VAlUE_OBJECT;
import static org.contextmapper.dsl.validation.ValidationMessages.MAPPED_BOUNDED_CONTEXT_IS_NOT_UPSTREAM;
import static org.contextmapper.dsl.validation.ValidationMessages.NO_RELATIONSHIP_BETWEEN_BOUNDED_CONTEXTS;
import static org.contextmapper.dsl.validation.ValidationMessages.VALUE_OBJECT_DOES_NOT_BELONG_TO_AGGREGATE;
import static org.contextmapper.dsl.validation.ValidationMessages.BOUNDED_CONTEXT_IS_NOT_DEFINED;
import static org.contextmapper.dsl.validation.ValidationMessages.USES_ENTITY_HAS_NOT_BODY;
import static org.contextmapper.tactic.dsl.tacticdsl.TacticdslPackage.Literals.DOMAIN_OBJECT__AGGREGATE_ROOT;
import static org.contextmapper.tactic.dsl.tacticdsl.TacticdslPackage.Literals.ENTITY__BOUNDED_CONTEXT;
import static org.contextmapper.tactic.dsl.tacticdsl.TacticdslPackage.Literals.ENTITY__AGGREGATE;
import static org.contextmapper.tactic.dsl.tacticdsl.TacticdslPackage.Literals.ENTITY__VALUE_OBJECT;
import static org.contextmapper.tactic.dsl.tacticdsl.TacticdslPackage.Literals.ENTITY__USES;

import org.contextmapper.dsl.cml.CMLModelObjectsResolvingHelper;
import org.contextmapper.dsl.contextMappingDSL.Aggregate;
import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.ContextMap;
import org.contextmapper.dsl.contextMappingDSL.CustomerSupplierRelationship;
import org.contextmapper.dsl.contextMappingDSL.Partnership;
import org.contextmapper.dsl.contextMappingDSL.Relationship;
import org.contextmapper.dsl.contextMappingDSL.SharedKernel;
import org.contextmapper.dsl.contextMappingDSL.UpstreamDownstreamRelationship;
import org.contextmapper.tactic.dsl.tacticdsl.Entity;
import org.contextmapper.tactic.dsl.tacticdsl.ValueObject;
import org.eclipse.xtext.validation.Check;
import org.eclipse.xtext.validation.EValidatorRegistrar;

public class EntitySemanticsValidator extends AbstractCMLValidator {

	@Override
	public void register(EValidatorRegistrar registrar) {
		// not needed for classes used as ComposedCheck
	}
	
	@Check
	public void ValidateAggregateRootDoesNotUseValueObject(final Entity entity) {
		if (entity.isAggregateRoot() && entity.isUses()) {
			error(String.format(AGGREGATE_ROOT_CANNOT_USE_VAlUE_OBJECT, entity.getName()), 
					entity, DOMAIN_OBJECT__AGGREGATE_ROOT);
		}
	}
	
	@Check
	public void ValidateBoundedContextIsAccessible(final Entity entity) {
		if (!entity.isUses()) return;
		
		BoundedContext mappedBoundedContext = getBoundedContextByName(entity, entity.getBoundedContext());
		BoundedContext mappingBoundedContext = getBoundedContextByEntity(entity);
		
		ContextMap contextMap = new CMLModelObjectsResolvingHelper(getRootCMLModel(entity))
				.getContextMap(mappedBoundedContext);
				
		boolean hasRelationhsip = false;
		for (Relationship relationship: contextMap.getRelationships()) {			
			if (relationship instanceof Partnership) {
				Partnership partnership = (Partnership) relationship;
				if ((partnership.getParticipant1().equals(mappedBoundedContext) 
						&& partnership.getParticipant2().equals(mappingBoundedContext))
						|| 
					(partnership.getParticipant1().equals(mappingBoundedContext) 
						&& partnership.getParticipant2().equals(mappedBoundedContext))) {
					hasRelationhsip = true;
				}
			} else if (relationship instanceof SharedKernel) {
				SharedKernel sharedKernel = (SharedKernel) relationship;
				if ((sharedKernel.getParticipant1().equals(mappedBoundedContext) 
						&& sharedKernel.getParticipant2().equals(mappingBoundedContext))
						|| 
					(sharedKernel.getParticipant1().equals(mappingBoundedContext) 
						&& sharedKernel.getParticipant2().equals(mappedBoundedContext))) {
					hasRelationhsip = true;
				}
			} else if (relationship instanceof UpstreamDownstreamRelationship) {
				UpstreamDownstreamRelationship upstreamDownstreamRelationship = (UpstreamDownstreamRelationship) relationship;
				if (upstreamDownstreamRelationship.getDownstream().equals(mappingBoundedContext) 
						&& upstreamDownstreamRelationship.getUpstream().equals(mappedBoundedContext)) {
					hasRelationhsip = true;
				}
						
				if (upstreamDownstreamRelationship.getUpstream().equals(mappingBoundedContext) 
						&& upstreamDownstreamRelationship.getDownstream().equals(mappedBoundedContext)) {
					hasRelationhsip = true;
					error(String.format(MAPPED_BOUNDED_CONTEXT_IS_NOT_UPSTREAM, mappedBoundedContext.getName()), 
							entity, ENTITY__BOUNDED_CONTEXT);
				}
			} else if (relationship instanceof CustomerSupplierRelationship) {
				CustomerSupplierRelationship customerSupplierRelationship = (CustomerSupplierRelationship) relationship;
				if (customerSupplierRelationship.getDownstream().equals(mappingBoundedContext) 
						&& customerSupplierRelationship.getUpstream().equals(mappedBoundedContext)){
					hasRelationhsip = true;
				}
				if (customerSupplierRelationship.getUpstream().equals(mappingBoundedContext) 
						&& customerSupplierRelationship.getDownstream().equals(mappedBoundedContext)) {
					hasRelationhsip = true;
					error(String.format(MAPPED_BOUNDED_CONTEXT_IS_NOT_UPSTREAM, mappedBoundedContext.getName()), 
							entity, ENTITY__BOUNDED_CONTEXT);
				}
			}
		}
		
		if (!hasRelationhsip) {
			error(String.format(NO_RELATIONSHIP_BETWEEN_BOUNDED_CONTEXTS), 
					entity, ENTITY__BOUNDED_CONTEXT);
		}
	}
	
	@Check
	public void ValidateBoundedContextIsDefined(final Entity entity) {
		if (!entity.isUses()) return;	
		
		if (getBoundedContextByName(entity, entity.getBoundedContext()) == null) {
			error(String.format(BOUNDED_CONTEXT_IS_NOT_DEFINED, entity.getBoundedContext()), 
					entity, ENTITY__BOUNDED_CONTEXT);
		}
	}
	
	@Check
	public void ValidateAggregateBelongsToBoundedContext(final Entity entity) {
		if (!entity.isUses()) return;	
		
		BoundedContext mappedBoundedContext = getBoundedContextByName(entity, entity.getBoundedContext());
		
		if (getAggregateByName(mappedBoundedContext, entity.getAggregate()) == null) {
			error(String.format(AGGREGATE_DOES_NOT_BELONG_TO_BOUNDED_CONTEXT, entity.getBoundedContext()), 
					entity, ENTITY__AGGREGATE);
		}
	}
	
	@Check
	public void ValidateValueObjectBelongsToAggregate(final Entity entity) {
		if (!entity.isUses()) return;	

		BoundedContext mappedBoundedContext = getBoundedContextByName(entity, entity.getBoundedContext());
		Aggregate mappedAggregate = getAggregateByName(mappedBoundedContext, entity.getAggregate());
		
		ValueObject valueObject = entity.getValueObject();
		
		if (mappedAggregate != null && mappedAggregate.getDomainObjects().stream()
				.filter(ValueObject.class::isInstance)
				.map(ValueObject.class::cast)
				.noneMatch(vo -> vo.getName().equals(valueObject.getName()))) {
			error(String.format(VALUE_OBJECT_DOES_NOT_BELONG_TO_AGGREGATE, mappedAggregate.getName()), 
					entity, ENTITY__VALUE_OBJECT);
		}
	
	}
	
	@Check
	public void ValidateEntityUsesHasNoAttributes(final Entity entity) {
		if (!entity.isUses()) return;
		
		if (!entity.getAttributes().isEmpty() 
				|| !entity.getAssociations().isEmpty()
				|| !entity.getOperations().isEmpty()
				|| !entity.getReferences().isEmpty()
				|| entity.getRepository() != null) {

			error(String.format(USES_ENTITY_HAS_NOT_BODY, entity.getName()), 
					entity, ENTITY__USES);
		}
	
	}
	
	private BoundedContext getBoundedContextByEntity(Entity entity) {
		Aggregate aggregate = (Aggregate) entity.eContainer();
		return (BoundedContext) aggregate.eContainer();
	}
	
	private BoundedContext getBoundedContextByName(Entity entity, String boundedContextName) {
		BoundedContext boundedContext = getBoundedContextByEntity(entity);
		
		ContextMap contextMap = new CMLModelObjectsResolvingHelper(getRootCMLModel(entity)).getContextMap(boundedContext);
		
		return contextMap.getBoundedContexts().stream()
			.filter(BoundedContext -> BoundedContext.getName().equals(boundedContextName))
			.findAny()
			.orElse(null);
	}
	
	private Aggregate getAggregateByName(BoundedContext boundedContext, String aggregateName) {
		return boundedContext.getAggregates().stream()
				.filter(aggregate -> aggregate.getName().equals(aggregateName))
				.findAny()
				.orElse(null);
	}


}
