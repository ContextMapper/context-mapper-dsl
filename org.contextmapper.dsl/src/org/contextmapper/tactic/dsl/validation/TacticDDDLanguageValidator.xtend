/*
 * Copyright 2013 The Sculptor Project Team, including the original 
 * author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License")
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
package org.contextmapper.tactic.dsl.validation

import java.util.HashSet
import java.util.regex.Pattern
import org.contextmapper.tactic.dsl.tacticdsl.AnyProperty
import org.contextmapper.tactic.dsl.tacticdsl.Attribute
import org.contextmapper.tactic.dsl.tacticdsl.BasicType
import org.contextmapper.tactic.dsl.tacticdsl.CollectionType
import org.contextmapper.tactic.dsl.tacticdsl.DomainObject
import org.contextmapper.tactic.dsl.tacticdsl.DtoAttribute
import org.contextmapper.tactic.dsl.tacticdsl.DtoReference
import org.contextmapper.tactic.dsl.tacticdsl.Entity
import org.contextmapper.tactic.dsl.tacticdsl.Enum
import org.contextmapper.tactic.dsl.tacticdsl.EnumAttribute
import org.contextmapper.tactic.dsl.tacticdsl.EnumValue
import org.contextmapper.tactic.dsl.tacticdsl.Event
import org.contextmapper.tactic.dsl.tacticdsl.Parameter
import org.contextmapper.tactic.dsl.tacticdsl.Property
import org.contextmapper.tactic.dsl.tacticdsl.Reference
import org.contextmapper.tactic.dsl.tacticdsl.Repository
import org.contextmapper.tactic.dsl.tacticdsl.RepositoryOperation
import org.contextmapper.tactic.dsl.tacticdsl.Service
import org.contextmapper.tactic.dsl.tacticdsl.ServiceOperation
import org.contextmapper.tactic.dsl.tacticdsl.SimpleDomainObject
import org.contextmapper.tactic.dsl.tacticdsl.ValueObject
import org.eclipse.emf.ecore.EObject
import org.eclipse.xtext.validation.Check

import static java.util.Arrays.*
import static org.contextmapper.tactic.dsl.tacticdsl.TacticdslPackage.Literals.*

import static extension org.contextmapper.tactic.dsl.TacticDslExtensions.*
import static extension org.eclipse.emf.ecore.util.EcoreUtil.*

/**
 * Custom validation rules. 
 *
 * see http://www.eclipse.org/Xtext/documentation.html#validation
 */
class TacticDDDLanguageValidator extends AbstractTacticDDDLanguageValidator implements IssueCodes {
	
val DIGITS_PATTERN = Pattern.compile("[0-9]+[0-9]*")
	val SUPPORTED_PRIMITIVE_TYPES = new HashSet<String>(asList("int", "long", "float", "double", "boolean"))
	val SUPPORTED_TEMPORAL_TYPES = new HashSet<String>(asList("Date", "DateTime", "Timestamp"))
	val SUPPORTED_NUMERIC_TYPES = new HashSet<String>(
		asList("int", "long", "float", "double", "Integer", "Long", "Float", "Double", "BigInteger", "BigDecimal"))
	val SUPPORTED_BOOLEAN_TYPES = new HashSet<String>(asList("Boolean", "boolean"))

	@Check
	def checkServiceNameStartsWithUpperCase(Service service) {
		if (service.name === null) {
			return
		}
		if (!Character.isUpperCase(service.name.charAt(0))) {
			warning("The service name should begin with an upper case letter", SERVICE_REPOSITORY_OPTION__NAME,
					CAPITALIZED_NAME, service.name)
		}
	}

	@Check
	def checkRepositoryNameStartsWithUpperCase(Repository repository) {
		if (repository.name === null) {
			return
		}
		if (!Character.isUpperCase(repository.name.charAt(0))) {
			warning("The repository name should begin with an upper case letter", SERVICE_REPOSITORY_OPTION__NAME,
					CAPITALIZED_NAME, repository.name)
		}
	}

	@Check
	def checkDomainObjectNameStartsWithUpperCase(SimpleDomainObject domainObject) {
		if (domainObject.name === null) {
			return
		}
		if (!Character.isUpperCase(domainObject.name.charAt(0))) {
			warning("The domain object name should begin with an upper case letter", SIMPLE_DOMAIN_OBJECT__NAME,
					CAPITALIZED_NAME, domainObject.name)
		}
	}

	@Check
	def checkPropertyNameStartsWithLowerCase(AnyProperty prop) {
		if (prop.name === null) {
			return
		}
		if (!Character.isLowerCase(prop.name.charAt(0))) {
			warning("Attribute/reference should begin with a lower case letter", ANY_PROPERTY__NAME,
					UNCAPITALIZED_NAME, prop.name)
		}
	}

	@Check
	def checkParamterNameStartsWithLowerCase(Parameter param) {
		if (param.name === null) {
			return
		}
		if (!Character.isLowerCase(param.name.charAt(0))) {
			warning("Parameter should begin with a lower case letter", PARAMETER__NAME, UNCAPITALIZED_NAME,
					param.name)
		}
	}

	@Check
	def checkRequired(Property prop) {
		if (prop.notChangeable && prop.required) {
			warning("The combination not changeable and required doesn't make sense, remove required",
					ANY_PROPERTY__REQUIRED)
		}
	}

	@Check
	def checkKeyNotChangeable(Property prop) {
		if (prop.key && prop.isNotChangeable()) {
			warning("Key property is always not changeable", ANY_PROPERTY__NOT_CHANGEABLE)
		}
	}

	@Check
	def checkKeyRequired(Property prop) {
		if (prop.key && prop.isRequired()) {
			warning("Key property is always required", ANY_PROPERTY__REQUIRED)
		}
	}

	@Check
	def checkCollectionCache(Reference ref) {
		if (ref.isCache() && ref.collectionType == CollectionType.NONE) {
			error("Cache is only applicable for collections", REFERENCE__CACHE)
		}
	}

	@Check
	def checkInverse(Reference ref) {
		if (!ref.isInverse()) {
			return
		}
		if (!(ref.collectionType != CollectionType.NONE || (ref.getOppositeHolder() !== null
				&& ref.getOppositeHolder().getOpposite() !== null && ref.getOppositeHolder().getOpposite()
				.collectionType == CollectionType.NONE))) {
			error("Inverse is only applicable for references with cardinality many, or one-to-one",
					REFERENCE__INVERSE)
		}
	}

	@Check
	def checkJoinTable(Reference ref) {
		if (ref.getDatabaseJoinTable() === null) {
			return
		}

		if (isBidirectionalManyToMany(ref) && ref.getOppositeHolder().getOpposite().getDatabaseJoinTable() !== null) {
			warning("Define databaseJoinTable only at one side of the many-to-many association",
					REFERENCE__DATABASE_JOIN_TABLE)
		}

		if (!(isBidirectionalManyToMany(ref) || (isUnidirectionalToMany(ref) && !ref.isInverse()))) {
			error("databaseJoinTable is only applicable for bidirectional many-to-many, or unidirectional to-many without inverse",
					REFERENCE__DATABASE_JOIN_TABLE)
		}
	}

	@Check
	def checkJoinColumn(Reference ref) {
		if (ref.getDatabaseJoinColumn() === null) {
			return
		}

		if (!(isUnidirectionalToMany(ref) && !ref.isInverse())) {
			error("databaseJoinColumn is only applicable for unidirectional to-many without inverse",
					REFERENCE__DATABASE_JOIN_COLUMN)
		}
	}

	def private boolean isUnidirectionalToMany(Reference ref) {
		ref.collectionType != CollectionType.NONE && ref.getOppositeHolder() === null
	}

	def private boolean isBidirectionalManyToMany(Reference ref) {
		(ref.collectionType != CollectionType.NONE && ref.getOppositeHolder() !== null
				&& ref.getOppositeHolder().getOpposite() !== null && ref.getOppositeHolder().getOpposite()
				.collectionType != CollectionType.NONE)
	}

	@Check
	def checkNullable(Reference ref) {
		if (ref.nullable && ref.collectionType != CollectionType.NONE) {
			error("Nullable isn't applicable for references with cardinality many (" + ref.collectionType + ")",
					ANY_PROPERTY__NULLABLE)
		}
	}

	/**
	 * For bidirectional one-to-many associations it should only be possible to
	 * define databaseColumn on the reference pointing to the one-side.
	 */
	@Check
	def checkDatabaseColumnForBidirectionalOneToMany(Reference ref) {
		if (ref.getDatabaseColumn() === null) {
			return
		}
		if (ref.collectionType != CollectionType.NONE && ref.getOppositeHolder() !== null
				&& ref.getOppositeHolder().getOpposite() !== null
				&& ref.getOppositeHolder().getOpposite().collectionType == CollectionType.NONE) {
			error("databaseColumn should be defined at the opposite side", PROPERTY__DATABASE_COLUMN)
		}
	}

	@Check
	def checkOpposite(Reference ref) {
		if (ref.getOppositeHolder() === null || ref.getOppositeHolder().getOpposite() === null) {
			return
		}
		if (!(ref.getOppositeHolder().getOpposite().getOppositeHolder() !== null && ref.getOppositeHolder()
				.getOpposite().getOppositeHolder().getOpposite() == ref)) {
			error("Opposite should specify this reference as opposite: "
					+ ref.getOppositeHolder().getOpposite().name + " <-> " + ref.name,
					REFERENCE__OPPOSITE_HOLDER)
		}
	}

	@Check
	def checkChangeableCollection(Reference ref) {
		if (ref.isNotChangeable() && ref.collectionType != CollectionType.NONE) {
			warning("x-to-many references are never changeable, the content of the collection is always changeable",
					ANY_PROPERTY__NOT_CHANGEABLE)
		}
	}

	@Check
	def checkOrderBy(Reference ref) {
		if (ref.getOrderBy() !== null && (!isBag(ref) && !isList(ref))) {
			error("orderBy only applicable for Bag or List collections", REFERENCE__ORDER_BY)
		}
	}

	@Check
	def checkOrderColumn(Reference ref) {
		if (ref.isOrderColumn() && !isList(ref)) {
			error("orderColumn only applicable for List collections", REFERENCE__ORDER_COLUMN)
		}
	}

	@Check
	def checkOrderByOrOrderColumn(Reference ref) {
		if (ref.getOrderBy() !== null && ref.isOrderColumn()) {
			error("use either orderBy or orderColumn for List collections", REFERENCE__ORDER_BY)
		}
	}

	def private boolean isBag(Reference ref) {
		return ref.collectionType == CollectionType.BAG
	}

	def private boolean isList(Reference ref) {
		return ref.collectionType == CollectionType.LIST
	}

	@Check
	def checkNullableKey(Property prop) {
		if (prop.key && prop.nullable) {
			val parent = prop.eContainer()
			if (!hasAtLeastOneNotNullableKeyElement(parent)) {
				error("Natural key must not be nullable. Composite keys must have at least one not nullable property.",
						ANY_PROPERTY__NULLABLE)
			}
		}
	}

	def private boolean hasAtLeastOneNotNullableKeyElement(EObject parent) {
		var keyCount = 0
		var nullableKeyCount = 0
		for (EObject each : parent.eContents()) {
			if (each instanceof Attribute) {
				if (each.key) {
					keyCount = keyCount + 1
					if (each.nullable) {
						nullableKeyCount = nullableKeyCount + 1
					}
				}
			} else if (each instanceof Reference) {
				if (each.key) {
					keyCount = keyCount + 1
					if (each.nullable) {
						nullableKeyCount = nullableKeyCount + 1
					}
				}
			} else if (each instanceof DtoAttribute) {
				if (each.key) {
					keyCount = keyCount + 1
					if (each.nullable) {
						nullableKeyCount = nullableKeyCount + 1
					}
				}
			} else if (each instanceof DtoReference) {
				if (each.key) {
					keyCount = keyCount + 1
					if (each.nullable) {
						nullableKeyCount = nullableKeyCount + 1
					}
				}
			}
		}

		return (keyCount - nullableKeyCount) >= 1
	}

	@Check
	def checkKeyNotManyRefererence(Reference ref) {
		if (ref.key && ref.collectionType != CollectionType.NONE) {
			error("Natural key can't be a many refererence.", ANY_PROPERTY__KEY)
		}
	}

	@Check
	def checkCascade(Reference ref) {
		if (ref.getCascade() !== null && ref.getDomainObjectType() instanceof BasicType) {
			error("Cascade is not applicable for BasicType", REFERENCE__CASCADE)
		}
		if (ref.getCascade() !== null && ref.getDomainObjectType() instanceof Enum) {
			error("Cascade is not applicable for enum", REFERENCE__CASCADE)
		}
	}

	@Check
	def checkCache(Reference ref) {
		if (ref.isCache() && ref.getDomainObjectType() instanceof BasicType) {
			error("Cache is not applicable for BasicType", REFERENCE__CACHE)
		}
		if (ref.isCache() && ref.getDomainObjectType() instanceof Enum) {
			error("Cache is not applicable for enum", REFERENCE__CACHE)
		}
	}

	@Check
	def checkRepositoryName(Repository repository) {
		if (repository.name !== null && !repository.name.endsWith("Repository")) {
			error("Name of repository must end with 'Repository'", SERVICE_REPOSITORY_OPTION__NAME)
		}
	}

	@Check
	def checkEnumValues(Enum dslEnum) {
		if (dslEnum.values.isEmpty()) {
			error("At least one enum value must be defined", ENUM__VALUES)
		}
	}

	@Check
	def checkEnumAttributes(Enum dslEnum) {
		if (dslEnum.values.isEmpty()) {
			return
		}
		if (dslEnum.attributes.isEmpty()) {
			return
		}
		val attSize = dslEnum.attributes.size()
		for (EnumValue each : dslEnum.values) {
			if (each.getParameters().size() != attSize) {
				error("Enum attribute not defined", ENUM__VALUES)
				return
			}
		}
	}

	@Check
	def checkEnumParameter(Enum dslEnum) {
		if (dslEnum.values.isEmpty()) {
			return
		}
		val expectedSize = dslEnum.values.get(0).parameters.size
		for (EnumValue each : dslEnum.values) {
			if (each.getParameters().size() != expectedSize) {
				error("Enum values must have same number of parameters", ENUM__VALUES)
				return
			}
		}
	}

	@Check
	def checkEnumImplicitAttribute(Enum dslEnum) {
		if (dslEnum.values.isEmpty()) {
			return
		}
		if (!dslEnum.attributes.isEmpty()) {
			return
		}
		for (EnumValue each : dslEnum.values) {
			if (each.getParameters().size() > 1) {
				error("Only one implicit value attribute is allowed", ENUM__VALUES)
				return
			}
		}
	}

	@Check
	def checkEnumAttributeKey(Enum dslEnum) {
		if (dslEnum.values.isEmpty()) {
			return
		}
		var count = 0
		for (EnumAttribute each : dslEnum.attributes) {
			if (each.key) {
				count = count + 1
			}
		}
		if (count > 1) {
			error("Only one enum attribute can be defined as key", ENUM__ATTRIBUTES)
		}
	}

	@Check
	def checkEnumOrdinal(Enum dslEnum) {
		val hint = dslEnum.hint
		if (hint !== null && hint.contains("ordinal")) {
			for (EnumAttribute attr : dslEnum.attributes) {
				if (attr.key) {
					error("ordinal is not allowed for enums with a key attribute", ENUM__ATTRIBUTES)
					return
				}
			}
			for (EnumValue each : dslEnum.values) {
				if (each.getParameters().size() == 1 && dslEnum.attributes.isEmpty()) {
					error("ordinal is not allowed for enum with implicit value", ENUM__VALUES)
					return
				}
			}
		}
	}

	@Check
	def checkEnumOrdinalOrDatabaseLength(Enum dslEnum) {
		val hint = dslEnum.hint
		if (hint !== null && hint.contains("ordinal") && hint.contains("databaseLength")) {
			error("ordinal in combination with databaseLength is not allowed", ENUM__ATTRIBUTES)
		}
	}

	@Check
	def checkEnumDatabaseLength(Enum dslEnum) {
		val hint = dslEnum.hint
		if (hint !== null && hint.contains("databaseLength")) {
			for (EnumAttribute attr : dslEnum.attributes) {
				if (attr.key && !attr.type.equals("String")) {
					error("databaseLength is not allowed for enums not having a key of type String", ENUM__ATTRIBUTES)
					return
				}
			}
		}
	}

	@Check
	def checkGap(Service service) {
		if (service.gapClass && service.noGapClass) {
			error("Unclear specification of gap", SERVICE_REPOSITORY_OPTION__NO_GAP_CLASS)
		}
	}

	@Check
	def checkGap(Repository repository) {
		if (repository.gapClass && repository.noGapClass) {
			error("Unclear specification of gap", SERVICE_REPOSITORY_OPTION__NO_GAP_CLASS)
		}
	}

	@Check
	def checkGap(DomainObject domainObj) {
		if (domainObj.gapClass && domainObj.noGapClass) {
			error("Unclear specification of gap", DOMAIN_OBJECT__NO_GAP_CLASS)
		}
	}

	@Check
	def checkGap(BasicType domainObj) {
		if (domainObj.gapClass && domainObj.noGapClass) {
			error("Unclear specification of gap", BASIC_TYPE__NO_GAP_CLASS)
		}
	}

	@Check
	def checkDiscriminatorValue(Entity domainObj) {
		if (domainObj.discriminatorValue !== null && domainObj.^extends === null) {
			error("discriminatorValue can only be used when you extend another Entity",
					DOMAIN_OBJECT__DISCRIMINATOR_VALUE)
		}
	}

	@Check
	def checkDiscriminatorValue(ValueObject domainObj) {
		if (domainObj.discriminatorValue !== null && domainObj.^extends === null) {
			error("discriminatorValue can only be used when you extend another ValueObject",
					DOMAIN_OBJECT__DISCRIMINATOR_VALUE)
		}
	}

	@Check
	def checkRepositoryOnlyForAggregateRoot(DomainObject domainObj) {
		if (domainObj.getRepository() !== null && !domainObj.isAggregateRoot) {
			error("Only aggregate roots can have Repository", DOMAIN_OBJECT__REPOSITORY)
		}
	}

	@Check
	def checkBelongsToRefersToAggregateRoot(DomainObject domainObj) {
		if (domainObj.belongsTo !== null && !domainObj.belongsTo.aggregateRoot) {
			error("belongsTo should refer to the aggregate root DomainObject", DOMAIN_OBJECT__BELONGS_TO)
		}
	}

	@Check
	def checkAggregateRootOnlyForPersistentValueObject(ValueObject domainObj) {
		if (domainObj.aggregateRoot && domainObj.isNotPersistent()) {
			error("aggregateRoot is only applicable for persistent ValueObjects",
					DOMAIN_OBJECT__AGGREGATE_ROOT)
		}
	}

	@Check
	def checkLength(Attribute attr) {
		if (attr.getLength() === null) {
			return
		}
		if (!isString(attr)) {
			error("length is only relevant for strings", ATTRIBUTE__LENGTH)
		}
		if (!DIGITS_PATTERN.matcher(attr.getLength()).matches()) {
			error("length value should be numeric, e.g. length = \"10\"", ATTRIBUTE__LENGTH)
		}
	}

	@Check
	def checkNullable(Attribute attr) {
		if (attr.nullable && isPrimitive(attr)) {
			error("nullable is not relevant for primitive types", ANY_PROPERTY__NULLABLE)
		}
	}

	@Check
	def checkCreditCardNumber(Attribute attr) {
		if (attr.isCreditCardNumber() && !isString(attr)) {
			error("creditCardNumber is only relevant for strings", ATTRIBUTE__CREDIT_CARD_NUMBER)
		}
	}

	@Check
	def checkEmail(Attribute attr) {
		if (attr.isEmail() && !isString(attr)) {
			error("email is only relevant for strings", ATTRIBUTE__EMAIL)
		}
	}

	@Check
	def checkNotEmpty(Attribute attr) {
		if (attr.isNotEmpty() && !(isString(attr) || isCollection(attr))) {
			error("notEmpty is only relevant for strings or collection types", ANY_PROPERTY__NOT_EMPTY)
		}
	}

	@Check
	def checkNotEmpty(Reference ref) {
		if (ref.isNotEmpty() && !isCollection(ref)) {
			error("notEmpty is only relevant for collection types", ANY_PROPERTY__NOT_EMPTY)
		}
	}

	@Check
	def checkSize(Reference ref) {
		if (ref.getSize() === null) {
			return
		}
		if (!isCollection(ref)) {
			error("size is only relevant for collection types", ANY_PROPERTY__SIZE)
		}
	}

	@Check
	def checkPast(Attribute attr) {
		if (attr.isPast() && !isTemporal(attr)) {
			error("past is only relevant for temporal types", ATTRIBUTE__PAST)
		}
	}

	@Check
	def checkFuture(Attribute attr) {
		if (attr.isFuture() && !isTemporal(attr)) {
			error("future is only relevant for temporal types", ATTRIBUTE__FUTURE)
		}
	}

	@Check
	def checkMin(Attribute attr) {
		if (attr.getMin() === null) {
			return
		}
		if (!isNumeric(attr)) {
			error("min is only relevant for numeric types", ATTRIBUTE__MIN)
		}
	}

	@Check
	def checkMax(Attribute attr) {
		if (attr.getMax() === null) {
			return
		}
		if (!isNumeric(attr)) {
			error("max is only relevant for numeric types", ATTRIBUTE__MAX)
		}
	}

	@Check
	def checkRange(Attribute attr) {
		if (attr.getRange() !== null && !isNumeric(attr)) {
			error("range is only relevant for numeric types", ATTRIBUTE__RANGE)
		}
	}

	@Check
	def checkDigits(Attribute attr) {
		if (attr.getDigits() !== null && !isNumeric(attr)) {
			error("digits is only relevant for numeric types", ATTRIBUTE__DIGITS)
		}
	}

	@Check
	def checkAssertTrue(Attribute attr) {
		if (attr.isAssertTrue() && !isBoolean(attr)) {
			error("assertTrue is only relevant for boolean types", ATTRIBUTE__ASSERT_TRUE)
		}
	}

	@Check
	def checkAssertFalse(Attribute attr) {
		if (attr.isAssertFalse() && !isBoolean(attr)) {
			error("assertFalse is only relevant for boolean types", ATTRIBUTE__ASSERT_FALSE)
		}
	}

	@Check
	def checkScaffoldValueObject(ValueObject valueObj) {
		if (valueObj.isScaffold() && valueObj.isNotPersistent()) {
			error("Scaffold not useful for not-persistent ValueObject.", DOMAIN_OBJECT__SCAFFOLD)
		}
	}

	@Check
	def checkScaffoldEvent(Event event) {
		if (event.isScaffold() && !event.isPersistent()) {
			error("Scaffold not useful for not-persistent event.", DOMAIN_OBJECT__SCAFFOLD, NON_PERSISTENT_EVENT,
				DOMAIN_OBJECT__SCAFFOLD.name)
		}
	}

	@Check
	def checkRepositoryEvent(Event event) {
		if (event.repository !== null && !event.isPersistent()) {
			error("Repository not useful for not-persistent event.", DOMAIN_OBJECT__REPOSITORY,
				NON_PERSISTENT_EVENT, DOMAIN_OBJECT__REPOSITORY.name)
		}
	}

	def private boolean isString(Attribute attribute) {
		return "String".equals(attribute.type) && !isCollection(attribute)
	}

	def private boolean isCollection(Attribute attribute) {
		return attribute.collectionType !== null && attribute.collectionType != CollectionType.NONE
	}

	def private boolean isCollection(Reference ref) {
		return ref.collectionType !== null && ref.collectionType != CollectionType.NONE
	}

	def private boolean isPrimitive(Attribute attribute) {
		return SUPPORTED_PRIMITIVE_TYPES.contains(attribute.type) && !isCollection(attribute)
	}

	def private boolean isTemporal(Attribute attribute) {
		return SUPPORTED_TEMPORAL_TYPES.contains(attribute.type) && !isCollection(attribute)
	}

	def private boolean isNumeric(Attribute attribute) {
		return SUPPORTED_NUMERIC_TYPES.contains(attribute.type) && !isCollection(attribute)
	}

	def private boolean isBoolean(Attribute attribute) {
		return SUPPORTED_BOOLEAN_TYPES.contains(attribute.type) && !isCollection(attribute)
	}

	@Check
	def checkRepositoryDuplicateName(Repository repository) {
		if (repository.name !== null && repository.rootContainer.eAllOfClass(typeof(Repository)).filter [it.name == repository.name].size > 1) {
			error("Duplicate name.  There is already an existing Repository named '"
				+ repository.name + "'.", SERVICE_REPOSITORY_OPTION__NAME, repository.name
			);  
		}
	}

	/**
	 * Type matches a domain object, but due to missing '-', comes in as a Attribute rather than a Reference
	 */
	@Check
	def checkMissingReferenceNotationWithNoCollection(Attribute attr) {
		if(attr.type !== null && attr.collectionType == CollectionType.NONE &&
			attr.domainObjectsForAttributeType.empty == false) {
			warning("Use - " + attr.type, ATTRIBUTE__TYPE, attr.type)
		}
	}

	/**
	 * Type for collection matches a domain object, but due to missing '-', comes in as a Attribute rather than a Reference
	 */
	@Check
	def checkMissingReferenceNotationWithCollection(Attribute attr) {
		if(attr.type !== null && attr.collectionType != CollectionType.NONE &&
			attr.domainObjectsForAttributeType.empty == false) {
			warning("Use - " + attr.collectionType + "<" + attr.type + ">", ATTRIBUTE__TYPE, attr.type)
		}
	}

	@Check
	def checkMissingDomainObjectInServiceOperationReturnType(ServiceOperation it) {
		if(returnType !== null && returnType.domainObjectType === null && returnType.type !== null &&
		   returnType.firstDomainObjectForType !== null) {
			warning("Use @" + returnType.type, SERVICE_OPERATION__RETURN_TYPE, returnType.type)
		}
	}

	@Check
	def checkMissingDomainObjectInRepositoryOperationReturnType(RepositoryOperation it) {
		if(returnType !== null && returnType.domainObjectType === null && returnType.type !== null &&
		   returnType.firstDomainObjectForType !== null) {
			warning("Use @" + returnType.type, REPOSITORY_OPERATION__RETURN_TYPE, returnType.type)
		}
	}

	@Check
	def checkMissingDomainObjectInParameter(Parameter it) {
		if(parameterType !== null && parameterType.domainObjectType === null && parameterType.type !== null &&
		   parameterType.firstDomainObjectForType !== null) {
			warning("Use @" + parameterType.type, PARAMETER__PARAMETER_TYPE, parameterType.type)
		}
	}

}
