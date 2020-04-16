<#assign allEntityNames = [] />
<#assign oneToManyRefs = [] />
<#assign oneToOneRefs = [] />
<#list filterStructuralBoundedContexts(boundedContexts) as bc>
<#assign entities = [] />
<#assign entityNames = [] />
<#list bc.aggregates as agg>
	<#assign entities = entities + agg.domainObjects?filter(dob -> instanceOf(dob, Entity))>
</#list>
<#assign entityNames = entities?map(e -> e.name)>
<#assign allEntityNames = allEntityNames + entityNames>
<#if entities?has_content>

/* Bounded Context ${bc.name} */<#lt>
<#list entities as entity>

entity ${entity.name} {
<#list entity.attributes as attribute>
	${attribute.name} ${mapAttributeType(attribute.type)}
</#list>
}
<#list entity.references as reference>
	<#if reference.domainObjectType?has_content && instanceOf(reference.domainObjectType, Entity) && entityNames?seq_contains(reference.domainObjectType.name)>
		<#if reference.collectionType?has_content && reference.collectionType.name() != "NONE">
			<#assign oneToManyRefs = oneToManyRefs + [ entity.name + "{" + reference.name + "} to " + reference.domainObjectType.name ]>
		<#else>
			<#assign oneToOneRefs = oneToOneRefs + [ entity.name + "{"+ reference.name + "} to " + reference.domainObjectType.name ]>
		</#if>
	</#if>
</#list>

</#list>
microservice ${entityNames?join(", ")} with ${bc.name}<#lt>
</#if>
	
application {
	config {
		baseName ${bc.name},
		packageName org.contextmapper.generated.${bc.name?lower_case},
		applicationType microservice
	}
	<#if entityNames?has_content>
	entities ${entityNames?join(", ")}
	</#if>
}
</#list>

/* relationships */
<#if oneToManyRefs?has_content>
	relationship OneToMany {<#lt>
		<#list oneToManyRefs as reference>
			${reference}
		</#list>
	}<#lt>
</#if>
<#if oneToOneRefs?has_content>
	relationship OneToOne {<#lt>
		<#list oneToOneRefs as reference>
			${reference}
		</#list>
	}<#lt>
</#if>

/* microservice gateyway app */
application {
	config {
		baseName gateway,
		packageName org.contextmapper.generated.gateway,
		applicationType gateway
	}
	<#if allEntityNames?has_content>
	entities ${allEntityNames?join(", ")}
	</#if>
}

/* additional options */
dto * with mapstruct
service * with serviceImpl

<#-- Data type mapping -->
<#function mapAttributeType inputType>
  <#if inputType == "String">
  	<#return "String">
  <#elseif inputType == "int" || inputType == "Integer">
  	<#return "Integer">
  <#elseif inputType == "long" || inputType == "Long">
  	<#return "Long">
  <#elseif inputType == "boolean" || inputType == "Boolean">
  	<#return "Boolean">
  <#elseif inputType == "Date" || inputType == "DateTime" || inputType == "Timestamp">
  	<#return "LocalDate">
  <#elseif inputType == "BigDecimal" || inputType == "BigInteger">
  	<#return "BigDecimal">
  <#elseif inputType == "double" || inputType == "Double">
  	<#return "Double">
  <#elseif inputType == "float" || inputType == "Float">
  	<#return "Float">
  <#elseif inputType == "Key">
  	<#return "UUID">
  <#elseif inputType == "Blob" || inputType =="Object[]">
  	<#return "Blob">
  <#elseif inputType == "Clob">
  	<#return "TextBlob">
  <#else>
  	<#return "Blob">
  </#if>
</#function>
