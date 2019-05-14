API description ${serviceSpecification.name}

<#list serviceSpecification.dataTypes as dataType>
<#if dataType.isTuple()>
data type ${dataType.name} ( ${dataType.getTupleTypesString()} )
<#else>
data type ${dataType.name} P
</#if>
</#list>

<#list serviceSpecification.endpoints as endpoint>
endpoint type ${endpoint.name}
	<#if endpoint.operations?has_content>
	exposes
	</#if>
		<#list endpoint.operations as operation>
		operation ${operation.name}
			<#if operation.expectingPayload?has_content>
			expecting
				payload ${operation.expectingPayload.name}
			</#if>
			<#if operation.deliveringPayload?has_content>
			delivering
				payload ${operation.deliveringPayload.name}
			</#if>
		</#list>
</#list>

<#list serviceSpecification.providers as provider>
API provider ${provider.name}
	<#list provider.endpointOffers as offer>
	offers ${offer.offeredEndpoint.name}
	at endpoint location "${offer.location}"
		via protocol "${offer.protocol}"
	</#list>
</#list>

<#list serviceSpecification.clients as client>
API client ${client.name}
	<#list client.consumedOfferNames as offername>
	consumes ${offername}
	</#list>
</#list>

IPA