<#--
 Makro that prints a JDL entity and all its relationships.
 -->
<#macro jdlEntity entity>
entity ${entity.name} {
<#list entity.attributes as attribute>
	${attribute.name} ${mapAttributeType(attribute.type)}
</#list>
}
<#assign oneToManyRefs = [] />
<#assign oneToOneRefs = [] />
<#list entity.references as reference>
	<#if reference.domainObjectType?has_content && instanceOf(reference.domainObjectType, Entity)>
		<#if reference.collectionType?has_content && reference.collectionType.name() != "NONE">
			<#assign oneToManyRefs = oneToManyRefs + [ reference ]>
		<#else>
			<#assign oneToOneRefs = oneToOneRefs + [ reference ]>
		</#if>
	</#if>
</#list>
<#if oneToManyRefs?has_content>
	relationship OneToMany {<#lt>
	<#list oneToManyRefs as reference>
	${entity.name}{${reference.name}} to ${reference.domainObjectType.name}
	</#list>
	}<#lt>
</#if>
<#if oneToOneRefs?has_content>
	relationship OneToOne {<#lt>
	<#list oneToOneRefs as reference>
	${entity.name}{${reference.name}} to ${reference.domainObjectType.name}
	</#list>
	}<#lt>
</#if>
</#macro>

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
