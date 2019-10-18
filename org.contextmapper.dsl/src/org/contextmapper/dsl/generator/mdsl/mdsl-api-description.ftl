<#if serviceSpecification.hasUpstreamDomainVisionStatement()>
/*
 * ${serviceSpecification.upstreamDomainVisionStatement}
 */
</#if>
API description ${serviceSpecification.name}
<#if serviceSpecification.usageContext?has_content>
usage context ${serviceSpecification.usageContext.toString()} for BACKEND_INTEGRATION
</#if>

<#if serviceSpecification.dataTypeProtectedRegion?has_content>
// PROTECTED REGION DATA TYPES BEGIN
${serviceSpecification.dataTypeProtectedRegion}
// PROTECTED REGION DATA TYPES END
</#if>

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

<#if serviceSpecification.endpointProtectedRegion?has_content>
// PROTECTED REGION ENDPOINTS BEGIN
${serviceSpecification.endpointProtectedRegion}
// PROTECTED REGION ENDPOINTS END
</#if>

<#list serviceSpecification.endpoints as endpoint>
endpoint type ${endpoint.name}
	<#if endpoint.servesAs?has_content>
	serves as <#if endpoint.isServesAsPatternMatched()>${endpoint.servesAs}<#else>"${endpoint.servesAs}"</#if>
	</#if>
	<#if endpoint.operations?has_content>
	exposes
	<#else>
// Your aggregate root does not specify any methods/operations. Therefore we can not generate any endpoint operations.
	</#if>
		<#list endpoint.operations as operation>
		operation ${operation.name}
			<#if operation.endpointResponsibility?has_content>
			with responsibility <#if operation.isEndpointResponsibilityPatternMatched()>${operation.endpointResponsibility}<#else>"${operation.endpointResponsibility}"</#if>
			</#if>
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

<#if serviceSpecification.providerProtectedRegion?has_content>
// PROTECTED REGION PROVIDERS BEGIN
${serviceSpecification.providerProtectedRegion}
// PROTECTED REGION PROVIDERS END
</#if>

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

<#if serviceSpecification.clientProtectedRegion?has_content>
// PROTECTED REGION CLIENTS BEGIN
${serviceSpecification.clientProtectedRegion}
// PROTECTED REGION CLIENTS END
</#if>

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