<#if serviceSpecification.hasUpstreamDomainVisionStatement()>
/*
 * ${serviceSpecification.upstreamDomainVisionStatement}
 */
</#if>
API description ${serviceSpecification.name}
<#if serviceSpecification.usageContext?has_content>
usage context ${serviceSpecification.usageContext.toString()} for BACKEND_INTEGRATION
</#if>

// PROTECTED REGION DATA TYPES BEGIN
// Hint: move data types which should not be overwritten by the generator into this section.

// PROTECTED REGION END

<#macro renderDataTypeAttributesRecursive attributes>{ <#list attributes as attribute><#if attribute.hasChildren()>"${attribute.getName()}":<@renderDataTypeAttributesRecursive attribute.getChildren() /><#if attribute.isCollection()>*<#elseif attribute.isNullable()>?</#if><#else>"${attribute.getName()}":${attribute.getType()}<#if attribute.isCollection()>*<#elseif attribute.isNullable()>?</#if></#if><#if attribute_index < attributes?size - 1>, </#if></#list> }</#macro>
<#list serviceSpecification.dataTypes as dataType>
	<#if !dataType.isPrimitiveType()>
		<#if dataType.hasComments()>
// ${dataType.getCommentsString()}
		</#if>
		<#if dataType.isAbstractDataType()>
data type ${dataType.name} P
		<#else>
data type ${dataType.name} <@renderDataTypeAttributesRecursive dataType.getChildren() />
		</#if>
	</#if>
</#list>

// PROTECTED REGION ENDPOINTS BEGIN
// Hint: move endpoints which should not be overwritten by the generator into this section.

// PROTECTED REGION END

<#list serviceSpecification.endpoints as endpoint>
endpoint type ${endpoint.name}
	<#if endpoint.operations?has_content>
	exposes
	<#else>
// Your aggregate root does not specify any methods/operations. Therefore we can not generate any endpoint operations.
	</#if>
		<#list endpoint.operations as operation>
		operation ${operation.name}
			<#if operation.expectingPayload?has_content>
			expecting
				payload ${operation.expectingPayload.name}<#if operation.expectingCollection()>*</#if>
			</#if>
			<#if operation.deliveringPayload?has_content>
			delivering
				payload ${operation.deliveringPayload.name}<#if operation.deliveringCollection()>*</#if>
			</#if>
		</#list>
</#list>

// PROTECTED REGION PROVIDERS BEGIN
// Hint: move providers which should not be overwritten by the generator into this section.

// PROTECTED REGION END

<#list serviceSpecification.providers as provider>
<#if provider.hasComments()>
	<#if provider.hasMultipleComments()>
/* 
		<#list provider.comments as comment>
 * ${comment}
		</#list>
 */
	<#else>
/* ${provider.comments[0]} */
	</#if>
</#if>
API provider ${provider.name}
	<#list provider.endpointOffers as offer>
	offers ${offer.offeredEndpoint.name}
	at endpoint location "${offer.location}"
		via protocol "${offer.protocol}"<#if offer.hasProtocolComment()> // ${offer.getProtocolComment()}</#if>
	</#list>
</#list>

// PROTECTED REGION CLIENTS BEGIN
// Hint: move providers which should not be overwritten by the generator into this section.

// PROTECTED REGION END

<#list serviceSpecification.clients as client>
<#if client.hasComments()>
	<#if client.hasMultipleComments()>
/* 
		<#list client.comments as comment>
 * ${comment}
		</#list>
 */
	<#else>
/* ${client.comments[0]} */
	</#if>
</#if>
API client ${client.name}
	<#list client.consumedOfferNames as offername>
	consumes ${offername}
	</#list>
</#list>

IPA