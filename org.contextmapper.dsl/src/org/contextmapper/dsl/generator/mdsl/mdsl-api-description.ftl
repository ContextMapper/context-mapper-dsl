<#if timestampString?has_content>
// ${timestampString}
</#if>
API description ${serviceSpecification.name}
<#if serviceSpecification.usageContext?has_content>
usage context ${serviceSpecification.usageContext.toString()} for BACKEND_INTEGRATION
</#if>

<#if serviceSpecification.dataTypeProtectedRegion??>
// ** BEGIN PROTECTED REGION for data types
${serviceSpecification.dataTypeProtectedRegion}
// ** END PROTECTED REGION for data types
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

<#if serviceSpecification.endpointProtectedRegion??>
// ** BEGIN PROTECTED REGION for endpoint types
${serviceSpecification.endpointProtectedRegion}
// ** END PROTECTED REGION for endpoint types
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

<#if serviceSpecification.providerProtectedRegion??>
// ** BEGIN PROTECTED REGION for API providers
${serviceSpecification.providerProtectedRegion}
// ** END PROTECTED REGION for API providers
</#if>

<#list serviceSpecification.providers as provider>
API provider ${provider.name}
<#if provider.hasComments()>
	<#list provider.comments as comment>
	// ${comment}
	</#list>
</#if>
	<#list provider.endpointOffers as offer>
	offers ${offer.offeredEndpoint.name}
	at endpoint location "${offer.location}"
		via protocol "${offer.protocol}"<#if offer.hasProtocolComment()> // ${offer.getProtocolComment()}</#if>
	</#list>
</#list>

<#if serviceSpecification.clientProtectedRegion??>
// ** BEGIN PROTECTED REGION for API clients
${serviceSpecification.clientProtectedRegion}
// ** END PROTECTED REGION for API clients
</#if>

<#list serviceSpecification.clients as client>
API client ${client.name}
<#if client.hasComments()>
	<#list client.comments as comment>
	// ${comment}
	</#list>
</#if>
	<#list client.consumedOfferNames as offername>
	consumes ${offername}
	</#list>
</#list>

IPA