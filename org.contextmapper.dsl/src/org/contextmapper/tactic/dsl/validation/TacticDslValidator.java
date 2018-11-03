/*
 * Copyright 2013 The Sculptor Project Team, including the original 
 * author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.contextmapper.tactic.dsl.validation;

import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.validation.AbstractDeclarativeValidator;
import org.eclipse.xtext.validation.EValidatorRegistrar;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.Functions.Function1;

import static java.util.Arrays.asList;
import static org.contextmapper.tactic.dsl.tacticdsl.TacticdslPackage.Literals.ANY_PROPERTY__COLLECTION_TYPE;
import static org.contextmapper.tactic.dsl.tacticdsl.TacticdslPackage.Literals.ANY_PROPERTY__KEY;
import static org.contextmapper.tactic.dsl.tacticdsl.TacticdslPackage.Literals.ANY_PROPERTY__NAME;
import static org.contextmapper.tactic.dsl.tacticdsl.TacticdslPackage.Literals.ANY_PROPERTY__NOT_CHANGEABLE;
import static org.contextmapper.tactic.dsl.tacticdsl.TacticdslPackage.Literals.ANY_PROPERTY__NOT_EMPTY;
import static org.contextmapper.tactic.dsl.tacticdsl.TacticdslPackage.Literals.ANY_PROPERTY__NULLABLE;
import static org.contextmapper.tactic.dsl.tacticdsl.TacticdslPackage.Literals.ANY_PROPERTY__REQUIRED;
import static org.contextmapper.tactic.dsl.tacticdsl.TacticdslPackage.Literals.ANY_PROPERTY__SIZE;
import static org.contextmapper.tactic.dsl.tacticdsl.TacticdslPackage.Literals.ATTRIBUTE__ASSERT_FALSE;
import static org.contextmapper.tactic.dsl.tacticdsl.TacticdslPackage.Literals.ATTRIBUTE__ASSERT_TRUE;
import static org.contextmapper.tactic.dsl.tacticdsl.TacticdslPackage.Literals.ATTRIBUTE__CREDIT_CARD_NUMBER;
import static org.contextmapper.tactic.dsl.tacticdsl.TacticdslPackage.Literals.ATTRIBUTE__DIGITS;
import static org.contextmapper.tactic.dsl.tacticdsl.TacticdslPackage.Literals.ATTRIBUTE__EMAIL;
import static org.contextmapper.tactic.dsl.tacticdsl.TacticdslPackage.Literals.ATTRIBUTE__FUTURE;
import static org.contextmapper.tactic.dsl.tacticdsl.TacticdslPackage.Literals.ATTRIBUTE__LENGTH;
import static org.contextmapper.tactic.dsl.tacticdsl.TacticdslPackage.Literals.ATTRIBUTE__MAX;
import static org.contextmapper.tactic.dsl.tacticdsl.TacticdslPackage.Literals.ATTRIBUTE__MIN;
import static org.contextmapper.tactic.dsl.tacticdsl.TacticdslPackage.Literals.ATTRIBUTE__PAST;
import static org.contextmapper.tactic.dsl.tacticdsl.TacticdslPackage.Literals.ATTRIBUTE__RANGE;
import static org.contextmapper.tactic.dsl.tacticdsl.TacticdslPackage.Literals.BASIC_TYPE__NO_GAP_CLASS;
import static org.contextmapper.tactic.dsl.tacticdsl.TacticdslPackage.Literals.DOMAIN_OBJECT__ABSTRACT;
import static org.contextmapper.tactic.dsl.tacticdsl.TacticdslPackage.Literals.DOMAIN_OBJECT__BELONGS_TO;
import static org.contextmapper.tactic.dsl.tacticdsl.TacticdslPackage.Literals.DOMAIN_OBJECT__DISCRIMINATOR_VALUE;
import static org.contextmapper.tactic.dsl.tacticdsl.TacticdslPackage.Literals.DOMAIN_OBJECT__EXTENDS_NAME;
import static org.contextmapper.tactic.dsl.tacticdsl.TacticdslPackage.Literals.DOMAIN_OBJECT__NOT_AGGREGATE_ROOT;
import static org.contextmapper.tactic.dsl.tacticdsl.TacticdslPackage.Literals.DOMAIN_OBJECT__NO_GAP_CLASS;
import static org.contextmapper.tactic.dsl.tacticdsl.TacticdslPackage.Literals.DOMAIN_OBJECT__REPOSITORY;
import static org.contextmapper.tactic.dsl.tacticdsl.TacticdslPackage.Literals.DOMAIN_OBJECT__SCAFFOLD;
import static org.contextmapper.tactic.dsl.tacticdsl.TacticdslPackage.Literals.ENUM__ATTRIBUTES;
import static org.contextmapper.tactic.dsl.tacticdsl.TacticdslPackage.Literals.ENUM__VALUES;
import static org.contextmapper.tactic.dsl.tacticdsl.TacticdslPackage.Literals.MODULE__NAME;
import static org.contextmapper.tactic.dsl.tacticdsl.TacticdslPackage.Literals.PARAMETER__NAME;
import static org.contextmapper.tactic.dsl.tacticdsl.TacticdslPackage.Literals.PROPERTY__DATABASE_COLUMN;
import static org.contextmapper.tactic.dsl.tacticdsl.TacticdslPackage.Literals.REFERENCE__CACHE;
import static org.contextmapper.tactic.dsl.tacticdsl.TacticdslPackage.Literals.REFERENCE__CASCADE;
import static org.contextmapper.tactic.dsl.tacticdsl.TacticdslPackage.Literals.REFERENCE__DATABASE_JOIN_COLUMN;
import static org.contextmapper.tactic.dsl.tacticdsl.TacticdslPackage.Literals.REFERENCE__DATABASE_JOIN_TABLE;
import static org.contextmapper.tactic.dsl.tacticdsl.TacticdslPackage.Literals.REFERENCE__INVERSE;
import static org.contextmapper.tactic.dsl.tacticdsl.TacticdslPackage.Literals.REFERENCE__OPPOSITE_HOLDER;
import static org.contextmapper.tactic.dsl.tacticdsl.TacticdslPackage.Literals.REFERENCE__ORDER_BY;
import static org.contextmapper.tactic.dsl.tacticdsl.TacticdslPackage.Literals.REFERENCE__ORDER_COLUMN;
import static org.contextmapper.tactic.dsl.tacticdsl.TacticdslPackage.Literals.SERVICE_REPOSITORY_OPTION__NAME;
import static org.contextmapper.tactic.dsl.tacticdsl.TacticdslPackage.Literals.SERVICE_REPOSITORY_OPTION__NO_GAP_CLASS;
import static org.contextmapper.tactic.dsl.tacticdsl.TacticdslPackage.Literals.SIMPLE_DOMAIN_OBJECT__NAME;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.validation.Check;
import org.contextmapper.tactic.dsl.TacticDslHelper;
import org.contextmapper.tactic.dsl.tacticdsl.AnyProperty;
import org.contextmapper.tactic.dsl.tacticdsl.Attribute;
import org.contextmapper.tactic.dsl.tacticdsl.BasicType;
import org.contextmapper.tactic.dsl.tacticdsl.CollectionType;
import org.contextmapper.tactic.dsl.tacticdsl.DataTransferObject;
import org.contextmapper.tactic.dsl.tacticdsl.DomainObject;
import org.contextmapper.tactic.dsl.tacticdsl.DomainObjectOperation;
import org.contextmapper.tactic.dsl.tacticdsl.DtoAttribute;
import org.contextmapper.tactic.dsl.tacticdsl.DtoReference;
import org.contextmapper.tactic.dsl.tacticdsl.Entity;
import org.contextmapper.tactic.dsl.tacticdsl.Enum;
import org.contextmapper.tactic.dsl.tacticdsl.EnumAttribute;
import org.contextmapper.tactic.dsl.tacticdsl.EnumValue;
import org.contextmapper.tactic.dsl.tacticdsl.Module;
import org.contextmapper.tactic.dsl.tacticdsl.Parameter;
import org.contextmapper.tactic.dsl.tacticdsl.Property;
import org.contextmapper.tactic.dsl.tacticdsl.Reference;
import org.contextmapper.tactic.dsl.tacticdsl.Repository;
import org.contextmapper.tactic.dsl.tacticdsl.Service;
import org.contextmapper.tactic.dsl.tacticdsl.SimpleDomainObject;
import org.contextmapper.tactic.dsl.tacticdsl.TacticdslPackage;
import org.contextmapper.tactic.dsl.tacticdsl.ValueObject;

import com.google.common.base.Objects;
import com.google.common.collect.Sets;

public class TacticDslValidator extends AbstractDeclarativeValidator implements IssueCodes {

	@Override
	public void register(EValidatorRegistrar registrar) {
		// not needed for classes used as ComposedCheck
	}

	private static final Pattern DIGITS_PATTERN = Pattern.compile("[0-9]+[0-9]*");
	private static final Set<String> SUPPORTED_PRIMITIVE_TYPES = new HashSet<String>(
			asList("int", "long", "float", "double", "boolean"));
	private static final Set<String> SUPPORTED_TEMPORAL_TYPES = new HashSet<String>(
			asList("Date", "DateTime", "Timestamp"));
	private static final Set<String> SUPPORTED_NUMERIC_TYPES = new HashSet<String>(
			asList("int", "long", "float", "double", "Integer", "Long", "Float", "Double", "BigInteger", "BigDecimal"));
	private static final Set<String> SUPPORTED_BOOLEAN_TYPES = new HashSet<String>(asList("Boolean", "boolean"));

	@Check
	public void checkDomainObjectDuplicateName(final SimpleDomainObject obj) {
		if (obj.getName() == null) {
			return;
		}
		final Function1<SimpleDomainObject, Boolean> function = (SimpleDomainObject it) -> {
			return Objects.equal(it.getName(), obj.getName());
		};
		int size = IterableExtensions.size(IterableExtensions.<SimpleDomainObject>filter(
				EcoreUtil2.<SimpleDomainObject>eAllOfType(EcoreUtil.getRootContainer(obj), SimpleDomainObject.class),
				function));
		if (size > 1) {
			this.error("Duplicate name. There is already an existing Domain Object named \'" + obj.getName() + "\'.",
					TacticdslPackage.Literals.SIMPLE_DOMAIN_OBJECT__NAME, obj.getName());
		}
	}

	@Check
	public void checkServiceDuplicateName(final Service service) {
		if (service.getName() == null) {
			return;
		}
		final Function1<Service, Boolean> function = (Service it) -> {
			return Objects.equal(it.getName(), service.getName());
		};
		int size = IterableExtensions.size(IterableExtensions.<Service>filter(
				TacticDslExtensions.<Service>eAllOfClass(EcoreUtil.getRootContainer(service), Service.class),
				function));
		if (size > 1) {
			this.error("Duplicate name. There is already an existing Service named \'" + service.getName() + "\'.",
					TacticdslPackage.Literals.SERVICE_REPOSITORY_OPTION__NAME, service.getName());
		}
	}

	@Check
	public void checkModuleNameStartsWithLowerCase(Module module) {
		if (module.getName() == null) {
			return;
		}
		if (!Character.isLowerCase(module.getName().charAt(0))) {
			warning("The module name should begin with a lower case letter", MODULE__NAME, UNCAPITALIZED_NAME,
					module.getName());
		}
	}

	@Check
	public void checkServiceNameStartsWithUpperCase(Service service) {
		if (service.getName() == null) {
			return;
		}
		if (!Character.isUpperCase(service.getName().charAt(0))) {
			warning("The service name should begin with an upper case letter", SERVICE_REPOSITORY_OPTION__NAME,
					CAPITALIZED_NAME, service.getName());
		}
	}

	@Check
	public void checkRepositoryNameStartsWithUpperCase(Repository repository) {
		if (repository.getName() == null) {
			return;
		}
		if (!Character.isUpperCase(repository.getName().charAt(0))) {
			warning("The repository name should begin with an upper case letter", SERVICE_REPOSITORY_OPTION__NAME,
					CAPITALIZED_NAME, repository.getName());
		}
	}

	@Check
	public void checkDomainObjectNameStartsWithUpperCase(SimpleDomainObject domainObject) {
		if (domainObject.getName() == null) {
			return;
		}
		if (!Character.isUpperCase(domainObject.getName().charAt(0))) {
			warning("The domain object name should begin with an upper case letter", SIMPLE_DOMAIN_OBJECT__NAME,
					CAPITALIZED_NAME, domainObject.getName());
		}
	}

	@Check
	public void checkExtendsName(DataTransferObject domainObject) {
		checkExtendsName(domainObject, domainObject.getExtendsName());
	}

	@Check
	public void checkExtendsName(DomainObject domainObject) {
		checkExtendsName(domainObject, domainObject.getExtendsName());
	}

	private void checkExtendsName(SimpleDomainObject domainObject, String extendsName) {
		if (extendsName == null) {
			return;
		}
		if (extendsName.indexOf('.') != -1) {
			return;
		}

		if (TacticDslHelper.getExtends(domainObject) == null) {
			error("Couldn't resolve reference to '" + extendsName + "'", DOMAIN_OBJECT__EXTENDS_NAME);
		}
	}

	/**
	 * Validation: SimpleDomainObject must not have circular inheritances.
	 */
	@Check
	public void checkInheritanceHierarchy(SimpleDomainObject domainObject) {
		if (isInheritanceCycle(domainObject)) {
			error("Circular inheritance detected", SIMPLE_DOMAIN_OBJECT__NAME);
		}
	}

	private boolean isInheritanceCycle(SimpleDomainObject domainObject) {
		Set<SimpleDomainObject> visited = Sets.newHashSet();
		SimpleDomainObject current = domainObject;
		while (current != null) {
			if (visited.contains(current)) {
				return true;
			}
			visited.add(current);
			current = TacticDslHelper.getExtends(current);
		}
		return false;
	}

	@Check
	public void checkAbstract(DomainObject domainObject) {
		if (domainObject.isAbstract()) {
			return;
		}

		Set<String> result = new HashSet<String>();
		abstractOperations(domainObject, result);

		if (!result.isEmpty()) {
			error("The domain object should be declared abstract, since it defines abstract operations: " + result,
					DOMAIN_OBJECT__ABSTRACT);
		}
	}

	private void abstractOperations(DomainObject domainObject, Set<String> result) {
		if (!isInheritanceCycle(domainObject)) {
			DomainObject domainObjectExtends = (DomainObject) TacticDslHelper.getExtends(domainObject);
			if (domainObjectExtends != null) {
				abstractOperations(domainObjectExtends, result);
			}
		}
		for (DomainObjectOperation each : domainObject.getOperations()) {
			// we don't consider overloaded operations, only by name
			if (each.isAbstract()) {
				result.add(each.getName());
			} else {
				result.remove(each.getName());
			}
		}
	}

	@Check
	public void checkPropertyNameStartsWithLowerCase(AnyProperty prop) {
		if (prop.getName() == null) {
			return;
		}
		if (!Character.isLowerCase(prop.getName().charAt(0))) {
			warning("Attribute/reference should begin with a lower case letter", ANY_PROPERTY__NAME, UNCAPITALIZED_NAME,
					prop.getName());
		}
	}

	@Check
	public void checkParamterNameStartsWithLowerCase(Parameter param) {
		if (param.getName() == null) {
			return;
		}
		if (!Character.isLowerCase(param.getName().charAt(0))) {
			warning("Parameter should begin with a lower case letter", PARAMETER__NAME, UNCAPITALIZED_NAME,
					param.getName());
		}
	}

	@Check
	public void checkRequired(Property prop) {
		if (prop.isNotChangeable() && prop.isRequired()) {
			warning("The combination not changeable and required doesn't make sense, remove required",
					ANY_PROPERTY__REQUIRED);
		}
	}

	@Check
	public void checkKeyNotChangeable(Property prop) {
		if (prop.isKey() && prop.isNotChangeable()) {
			warning("Key property is always not changeable", ANY_PROPERTY__NOT_CHANGEABLE);
		}
	}

	@Check
	public void checkKeyRequired(Property prop) {
		if (prop.isKey() && prop.isRequired()) {
			warning("Key property is always required", ANY_PROPERTY__REQUIRED);
		}
	}

	@Check
	public void checkCollectionCache(Reference ref) {
		if (ref.isCache() && ref.getCollectionType() == CollectionType.NONE) {
			error("Cache is only applicable for collections", REFERENCE__CACHE);
		}
	}

	@Check
	public void checkInverse(Reference ref) {
		if (!ref.isInverse()) {
			return;
		}
		if (!(ref.getCollectionType() != CollectionType.NONE
				|| (ref.getOppositeHolder() != null && ref.getOppositeHolder().getOpposite() != null
						&& ref.getOppositeHolder().getOpposite().getCollectionType() == CollectionType.NONE))) {
			error("Inverse is only applicable for references with cardinality many, or one-to-one", REFERENCE__INVERSE);
		}
	}

	@Check
	public void checkJoinTable(Reference ref) {
		if (ref.getDatabaseJoinTable() == null) {
			return;
		}

		if (isBidirectionalManyToMany(ref) && ref.getOppositeHolder().getOpposite().getDatabaseJoinTable() != null) {
			warning("Define databaseJoinTable only at one side of the many-to-many association",
					REFERENCE__DATABASE_JOIN_TABLE);
		}

		if (!(isBidirectionalManyToMany(ref) || (isUnidirectionalToMany(ref) && !ref.isInverse()))) {
			error("databaseJoinTable is only applicable for bidirectional many-to-many, or unidirectional to-many without inverse",
					REFERENCE__DATABASE_JOIN_TABLE);
		}
	}

	@Check
	public void checkJoinColumn(Reference ref) {
		if (ref.getDatabaseJoinColumn() == null) {
			return;
		}

		if (!(isUnidirectionalToMany(ref) && !ref.isInverse())) {
			error("databaseJoinColumn is only applicable for unidirectional to-many without inverse",
					REFERENCE__DATABASE_JOIN_COLUMN);
		}
	}

	private boolean isUnidirectionalToMany(Reference ref) {
		return ref.getCollectionType() != CollectionType.NONE && ref.getOppositeHolder() == null;
	}

	private boolean isBidirectionalManyToMany(Reference ref) {
		return (ref.getCollectionType() != CollectionType.NONE && ref.getOppositeHolder() != null
				&& ref.getOppositeHolder().getOpposite() != null
				&& ref.getOppositeHolder().getOpposite().getCollectionType() != CollectionType.NONE);
	}

	@Check
	public void checkNullable(Reference ref) {
		if (ref.isNullable() && ref.getCollectionType() != CollectionType.NONE) {
			error("Nullable isn't applicable for references with cardinality many (" + ref.getCollectionType() + ")",
					ANY_PROPERTY__NULLABLE);
		}
	}

	/**
	 * For bidirectional one-to-many associations it should only be possible to
	 * define databaseColumn on the reference pointing to the one-side.
	 */
	@Check
	public void checkDatabaseColumnForBidirectionalOneToMany(Reference ref) {
		if (ref.getDatabaseColumn() == null) {
			return;
		}
		if (ref.getCollectionType() != CollectionType.NONE && ref.getOppositeHolder() != null
				&& ref.getOppositeHolder().getOpposite() != null
				&& ref.getOppositeHolder().getOpposite().getCollectionType() == CollectionType.NONE) {
			error("databaseColumn should be defined at the opposite side", PROPERTY__DATABASE_COLUMN);
		}
	}

	@Check
	public void checkOpposite(Reference ref) {
		if (ref.getOppositeHolder() == null || ref.getOppositeHolder().getOpposite() == null) {
			return;
		}
		if (!(ref.getOppositeHolder().getOpposite().getOppositeHolder() != null
				&& ref.getOppositeHolder().getOpposite().getOppositeHolder().getOpposite() == ref)) {
			error("Opposite should specify this reference as opposite: "
					+ ref.getOppositeHolder().getOpposite().getName() + " <-> " + ref.getName(),
					REFERENCE__OPPOSITE_HOLDER);
		}
	}

	@Check
	public void checkChangeableCollection(Reference ref) {
		if (ref.isNotChangeable() && ref.getCollectionType() != CollectionType.NONE) {
			warning("x-to-many references are never changeable, the content of the collection is always changeable",
					ANY_PROPERTY__NOT_CHANGEABLE);
		}
	}

	@Check
	public void checkOrderBy(Reference ref) {
		if (ref.getOrderBy() != null && (!isBag(ref) && !isList(ref))) {
			error("orderBy only applicable for Bag or List collections", REFERENCE__ORDER_BY);
		}
	}

	@Check
	public void checkOrderColumn(Reference ref) {
		if (ref.isOrderColumn() && !isList(ref)) {
			error("orderColumn only applicable for List collections", REFERENCE__ORDER_COLUMN);
		}
	}

	@Check
	public void checkOrderByOrOrderColumn(Reference ref) {
		if (ref.getOrderBy() != null && ref.isOrderColumn()) {
			error("use either orderBy or orderColumn for List collections", REFERENCE__ORDER_BY);
		}
	}

	private boolean isBag(Reference ref) {
		return ref.getCollectionType() == CollectionType.BAG;
	}

	private boolean isList(Reference ref) {
		return ref.getCollectionType() == CollectionType.LIST;
	}

	@Check
	public void checkNullableKey(Property prop) {
		if (prop.isKey() && prop.isNullable()) {
			EObject parent = prop.eContainer();
			if (!hasAtLeastOneNotNullableKeyElement(parent)) {
				error("Natural key must not be nullable. Composite keys must have at least one not nullable property.",
						ANY_PROPERTY__NULLABLE);
			}
		}
	}

	private boolean hasAtLeastOneNotNullableKeyElement(EObject parent) {
		int keyCount = 0;
		int nullableKeyCount = 0;
		for (EObject each : parent.eContents()) {
			if (each instanceof Attribute) {
				Attribute eachProp = (Attribute) each;
				if (eachProp.isKey()) {
					keyCount++;
					if (eachProp.isNullable()) {
						nullableKeyCount++;
					}
				}
			} else if (each instanceof Reference) {
				Reference eachProp = (Reference) each;
				if (eachProp.isKey()) {
					keyCount++;
					if (eachProp.isNullable()) {
						nullableKeyCount++;
					}
				}
			} else if (each instanceof DtoAttribute) {
				DtoAttribute eachProp = (DtoAttribute) each;
				if (eachProp.isKey()) {
					keyCount++;
					if (eachProp.isNullable()) {
						nullableKeyCount++;
					}
				}
			} else if (each instanceof DtoReference) {
				DtoReference eachProp = (DtoReference) each;
				if (eachProp.isKey()) {
					keyCount++;
					if (eachProp.isNullable()) {
						nullableKeyCount++;
					}
				}
			}
		}

		return (keyCount - nullableKeyCount) >= 1;
	}

	@Check
	public void checkKeyNotManyRefererence(Reference ref) {
		if (ref.isKey() && ref.getCollectionType() != CollectionType.NONE) {
			error("Natural key can't be a many refererence.", ANY_PROPERTY__KEY);
		}
	}

	@Check
	public void checkCascade(Reference ref) {
		if (ref.getCascade() != null && ref.getDomainObjectType() instanceof BasicType) {
			error("Cascade is not applicable for BasicType", REFERENCE__CASCADE);
		}
		if (ref.getCascade() != null && ref.getDomainObjectType() instanceof Enum) {
			error("Cascade is not applicable for enum", REFERENCE__CASCADE);
		}
	}

	@Check
	public void checkCache(Reference ref) {
		if (ref.isCache() && ref.getDomainObjectType() instanceof BasicType) {
			error("Cache is not applicable for BasicType", REFERENCE__CACHE);
		}
		if (ref.isCache() && ref.getDomainObjectType() instanceof Enum) {
			error("Cache is not applicable for enum", REFERENCE__CACHE);
		}
	}

	@Check
	public void checkRepositoryName(Repository repository) {
		if (repository.getName() != null && !repository.getName().endsWith("Repository")) {
			error("Name of repository must end with 'Repository'", SERVICE_REPOSITORY_OPTION__NAME);
		}
	}

	@Check
	public void checkEnumReference(Reference ref) {
		if (ref.getDomainObjectType() instanceof Enum && ref.getCollectionType() != CollectionType.NONE) {
			boolean notPersistentVO = ((ref.eContainer() instanceof ValueObject)
					&& ((ValueObject) ref.eContainer()).isNotPersistent());
			if (!notPersistentVO) {
				error("Collection of enum is not supported", ANY_PROPERTY__COLLECTION_TYPE);
			}
		}
	}

	@Check
	public void checkEnumValues(Enum dslEnum) {
		if (dslEnum.getValues().isEmpty()) {
			error("At least one enum value must be defined", ENUM__VALUES);
		}
	}

	@Check
	public void checkEnumAttributes(Enum dslEnum) {
		if (dslEnum.getValues().isEmpty()) {
			return;
		}
		if (dslEnum.getAttributes().isEmpty()) {
			return;
		}
		int attSize = dslEnum.getAttributes().size();
		for (EnumValue each : dslEnum.getValues()) {
			if (each.getParameters().size() != attSize) {
				error("Enum attribute not defined", ENUM__VALUES);
				return;
			}
		}
	}

	@Check
	public void checkEnumParameter(Enum dslEnum) {
		if (dslEnum.getValues().isEmpty()) {
			return;
		}
		int expectedSize = dslEnum.getValues().get(0).getParameters().size();
		for (EnumValue each : dslEnum.getValues()) {
			if (each.getParameters().size() != expectedSize) {
				error("Enum values must have same number of parameters", ENUM__VALUES);
				return;
			}
		}
	}

	@Check
	public void checkEnumImplicitAttribute(Enum dslEnum) {
		if (dslEnum.getValues().isEmpty()) {
			return;
		}
		if (!dslEnum.getAttributes().isEmpty()) {
			return;
		}
		for (EnumValue each : dslEnum.getValues()) {
			if (each.getParameters().size() > 1) {
				error("Only one implicit value attribute is allowed", ENUM__VALUES);
				return;
			}
		}
	}

	@Check
	public void checkEnumAttributeKey(Enum dslEnum) {
		if (dslEnum.getValues().isEmpty()) {
			return;
		}
		int count = 0;
		for (EnumAttribute each : dslEnum.getAttributes()) {
			if (each.isKey()) {
				count++;
			}
		}
		if (count > 1) {
			error("Only one enum attribute can be defined as key", ENUM__ATTRIBUTES);
		}
	}

	@Check
	public void checkEnumOrdinal(Enum dslEnum) {
		if (!dslEnum.getHint().contains("ordinal")) {
			return;
		}
		for (EnumAttribute attr : dslEnum.getAttributes()) {
			if (attr.isKey()) {
				error("ordinal is not allowed for enums with a key attribute", ENUM__ATTRIBUTES);
				return;
			}
		}
		for (EnumValue each : dslEnum.getValues()) {
			if (each.getParameters().size() == 1 && dslEnum.getAttributes().isEmpty()) {
				error("ordinal is not allowed for enum with implicit value", ENUM__VALUES);
				return;
			}
		}
	}

	@Check
	public void checkEnumOrdinalOrDatabaseLength(Enum dslEnum) {
		if (dslEnum.getHint().contains("ordinal") && dslEnum.getHint().contains("databaseLength")) {
			error("ordinal in combination with databaseLength is not allowed", ENUM__ATTRIBUTES);
		}
	}

	@Check
	public void checkEnumDatabaseLength(Enum dslEnum) {
		if (!dslEnum.getHint().contains("databaseLength")) {
			return;
		}
		for (EnumAttribute attr : dslEnum.getAttributes()) {
			if (attr.isKey() && !attr.getType().equals("String")) {
				error("databaseLength is not allowed for enums not having a key of type String", ENUM__ATTRIBUTES);
				return;
			}
		}
	}

	@Check
	public void checkGap(Service service) {
		if (service.isGapClass() && service.isNoGapClass()) {
			error("Unclear specification of gap", SERVICE_REPOSITORY_OPTION__NO_GAP_CLASS);
		}
	}

	@Check
	public void checkGap(Repository repository) {
		if (repository.isGapClass() && repository.isNoGapClass()) {
			error("Unclear specification of gap", SERVICE_REPOSITORY_OPTION__NO_GAP_CLASS);
		}
	}

	@Check
	public void checkGap(DomainObject domainObj) {
		if (domainObj.isGapClass() && domainObj.isNoGapClass()) {
			error("Unclear specification of gap", DOMAIN_OBJECT__NO_GAP_CLASS);
		}
	}

	@Check
	public void checkGap(BasicType domainObj) {
		if (domainObj.isGapClass() && domainObj.isNoGapClass()) {
			error("Unclear specification of gap", BASIC_TYPE__NO_GAP_CLASS);
		}
	}

	@Check
	public void checkDiscriminatorValue(Entity domainObj) {
		if (domainObj.getDiscriminatorValue() != null && domainObj.getExtends() == null) {
			error("discriminatorValue can only be used when you extend another Entity",
					DOMAIN_OBJECT__DISCRIMINATOR_VALUE);
		}
	}

	@Check
	public void checkDiscriminatorValue(ValueObject domainObj) {
		if (domainObj.getDiscriminatorValue() != null && domainObj.getExtends() == null) {
			error("discriminatorValue can only be used when you extend another ValueObject",
					DOMAIN_OBJECT__DISCRIMINATOR_VALUE);
		}
	}

	@Check
	public void checkRepositoryOnlyForAggregateRoot(DomainObject domainObj) {
		if (domainObj.getRepository() != null && belongsToAggregate(domainObj)) {
			error("Only aggregate roots can have Repository", DOMAIN_OBJECT__REPOSITORY);
		}
	}

	@Check
	public void checkBelongsToRefersToAggregateRoot(DomainObject domainObj) {
		if (domainObj.getBelongsTo() != null && belongsToAggregate(domainObj.getBelongsTo())) {
			error("belongsTo should refer to the aggregate root DomainObject", DOMAIN_OBJECT__BELONGS_TO);
		}
	}

	private boolean belongsToAggregate(DomainObject domainObj) {
		return (domainObj.isNotAggregateRoot() || domainObj.getBelongsTo() != null);
	}

	@Check
	public void checkAggregateRootOnlyForPersistentValueObject(ValueObject domainObj) {
		if (belongsToAggregate(domainObj) && domainObj.isNotPersistent()) {
			error("not aggregateRoot is only applicable for persistent ValueObjects",
					DOMAIN_OBJECT__NOT_AGGREGATE_ROOT);
		}
	}

	@Check
	public void checkLength(Attribute attr) {
		if (attr.getLength() == null) {
			return;
		}
		if (!isString(attr)) {
			error("length is only relevant for strings", ATTRIBUTE__LENGTH);
		}
		if (!DIGITS_PATTERN.matcher(attr.getLength()).matches()) {
			error("length value should be numeric, e.g. length = \"10\"", ATTRIBUTE__LENGTH);
		}
	}

	@Check
	public void checkNullable(Attribute attr) {
		if (attr.isNullable() && isPrimitive(attr)) {
			error("nullable is not relevant for primitive types", ANY_PROPERTY__NULLABLE);
		}
	}

	@Check
	public void checkCreditCardNumber(Attribute attr) {
		if (attr.isCreditCardNumber() && !isString(attr)) {
			error("creditCardNumber is only relevant for strings", ATTRIBUTE__CREDIT_CARD_NUMBER);
		}
	}

	@Check
	public void checkEmail(Attribute attr) {
		if (attr.isEmail() && !isString(attr)) {
			error("email is only relevant for strings", ATTRIBUTE__EMAIL);
		}
	}

	@Check
	public void checkNotEmpty(Attribute attr) {
		if (attr.isNotEmpty() && !(isString(attr) || isCollection(attr))) {
			error("notEmpty is only relevant for strings or collection types", ANY_PROPERTY__NOT_EMPTY);
		}
	}

	@Check
	public void checkNotEmpty(Reference ref) {
		if (ref.isNotEmpty() && !isCollection(ref)) {
			error("notEmpty is only relevant for collection types", ANY_PROPERTY__NOT_EMPTY);
		}
	}

	@Check
	public void checkSize(Reference ref) {
		if (ref.getSize() == null) {
			return;
		}
		if (!isCollection(ref)) {
			error("size is only relevant for collection types", ANY_PROPERTY__SIZE);
		}
	}

	@Check
	public void checkPast(Attribute attr) {
		if (attr.isPast() && !isTemporal(attr)) {
			error("past is only relevant for temporal types", ATTRIBUTE__PAST);
		}
	}

	@Check
	public void checkFuture(Attribute attr) {
		if (attr.isFuture() && !isTemporal(attr)) {
			error("future is only relevant for temporal types", ATTRIBUTE__FUTURE);
		}
	}

	@Check
	public void checkMin(Attribute attr) {
		if (attr.getMin() == null) {
			return;
		}
		if (!isNumeric(attr)) {
			error("min is only relevant for numeric types", ATTRIBUTE__MIN);
		}
	}

	@Check
	public void checkMax(Attribute attr) {
		if (attr.getMax() == null) {
			return;
		}
		if (!isNumeric(attr)) {
			error("max is only relevant for numeric types", ATTRIBUTE__MAX);
		}
	}

	@Check
	public void checkRange(Attribute attr) {
		if (attr.getRange() != null && !isNumeric(attr)) {
			error("range is only relevant for numeric types", ATTRIBUTE__RANGE);
		}
	}

	@Check
	public void checkDigits(Attribute attr) {
		if (attr.getDigits() != null && !isNumeric(attr)) {
			error("digits is only relevant for numeric types", ATTRIBUTE__DIGITS);
		}
	}

	@Check
	public void checkAssertTrue(Attribute attr) {
		if (attr.isAssertTrue() && !isBoolean(attr)) {
			error("assertTrue is only relevant for boolean types", ATTRIBUTE__ASSERT_TRUE);
		}
	}

	@Check
	public void checkAssertFalse(Attribute attr) {
		if (attr.isAssertFalse() && !isBoolean(attr)) {
			error("assertFalse is only relevant for boolean types", ATTRIBUTE__ASSERT_FALSE);
		}
	}

	@Check
	public void checkScaffold(ValueObject domainObj) {
		if (domainObj.isScaffold() && domainObj.isNotPersistent()) {
			error("Scaffold not useful for not persistent ValueObject.", DOMAIN_OBJECT__SCAFFOLD);
		}
	}

	private boolean isString(Attribute attribute) {
		return "String".equals(attribute.getType()) && !isCollection(attribute);
	}

	private boolean isCollection(Attribute attribute) {
		return attribute.getCollectionType() != null && attribute.getCollectionType() != CollectionType.NONE;
	}

	private boolean isCollection(Reference ref) {
		return ref.getCollectionType() != null && ref.getCollectionType() != CollectionType.NONE;
	}

	private boolean isPrimitive(Attribute attribute) {
		return SUPPORTED_PRIMITIVE_TYPES.contains(attribute.getType()) && !isCollection(attribute);
	}

	private boolean isTemporal(Attribute attribute) {
		return SUPPORTED_TEMPORAL_TYPES.contains(attribute.getType()) && !isCollection(attribute);
	}

	private boolean isNumeric(Attribute attribute) {
		return SUPPORTED_NUMERIC_TYPES.contains(attribute.getType()) && !isCollection(attribute);
	}

	private boolean isBoolean(Attribute attribute) {
		return SUPPORTED_BOOLEAN_TYPES.contains(attribute.getType()) && !isCollection(attribute);
	}

}
