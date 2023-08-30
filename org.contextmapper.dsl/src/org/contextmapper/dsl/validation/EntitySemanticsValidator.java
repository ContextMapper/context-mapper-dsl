package org.contextmapper.dsl.validation;

import static org.contextmapper.dsl.validation.ValidationMessages.AGGREGATE_ROOT_CANNOT_USE_VAlUE_OBJECT;
import static org.contextmapper.dsl.validation.ValidationMessages.MAPPING_DOES_NOT_EXIST_FOR_USE;
import static org.contextmapper.dsl.validation.ValidationMessages.USED_VALUE_OBJECT_DOES_NOT_MAP_KEY_ATTRIBUTE;
import static org.contextmapper.tactic.dsl.tacticdsl.TacticdslPackage.Literals.DOMAIN_OBJECT__AGGREGATE_ROOT;
import static org.contextmapper.tactic.dsl.tacticdsl.TacticdslPackage.Literals.ENTITY__MAPPING_ID;

import org.contextmapper.dsl.contextMappingDSL.Aggregate;
import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
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
	public void ValidateWellFormedEntityUsesTranslation(final Entity entity) {
		if (entity.isUses()) {
			Aggregate aggregate = (Aggregate) entity.eContainer();
			BoundedContext boundedContext = (BoundedContext) aggregate.eContainer();
			
			if (boundedContext.getMappings().stream()
				.noneMatch(mapping -> mapping.getName().equals(entity.getMappingId()))) {
				error(String.format(MAPPING_DOES_NOT_EXIST_FOR_USE, entity.getName()), 
						entity, ENTITY__MAPPING_ID);
			}
		}
	}
	

}
