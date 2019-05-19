API description ${serviceSpecification.name}

<#macro renderDataTypeAttributesRecursive attributes>{ <#list attributes as attribute><#if attribute.hasChildren()>"${attribute.getName()}":<@renderDataTypeAttributesRecursive attribute.getChildren() /><#if attribute.isCollection()>*</#if><#else>"${attribute.getName()}":${attribute.getType()}<#if attribute.isCollection()>*</#if></#if><#if attribute_index < attributes?size - 1>, </#if></#list> }</#macro>
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

<#list serviceSpecification.providers as provider>
API provider ${provider.name}
	<#list provider.endpointOffers as offer>
	offers ${offer.offeredEndpoint.name}
	at endpoint location "${offer.location}"
		via protocol "${offer.protocol}"<#if offer.hasProtocolComment()> // ${offer.getProtocolComment()}</#if>
	</#list>
</#list>

<#list serviceSpecification.clients as client>
API client ${client.name}
	<#list client.consumedOfferNames as offername>
	consumes ${offername}
	</#list>
</#list>

IPA