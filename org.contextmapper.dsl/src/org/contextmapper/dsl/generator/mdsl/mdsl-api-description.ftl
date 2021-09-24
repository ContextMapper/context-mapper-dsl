<#macro opbind opname oprespo>
<#if opname?starts_with("create")>
			operation ${opname} to POST
<#elseif opname?starts_with("replace")>
			operation ${opname} to PUT
<#elseif opname?starts_with("update")>
			operation ${opname} to PATCH
<#elseif opname?starts_with("get")>
			operation ${opname} to GET
<#elseif opname?starts_with("lookup")>
			operation ${opname} to GET
<#elseif opname?starts_with("retrieve")>
			operation ${opname} to GET
<#elseif opname?starts_with("read")>
			operation ${opname} to GET
<#elseif opname?starts_with("delete")>
			operation ${opname} to DELETE
<#elseif oprespo=="RETRIEVAL_OPERATION">
			operation ${opname} to GET
<#elseif oprespo=="STATE_CREATION_OPERATION">
			operation ${opname} to PUT
<#elseif oprespo=="STATE_TRANSITION_OPERATION">
			operation ${opname} to PATCH
<#elseif oprespo=="COMPUTATION_FUNCTION">
			operation ${opname} to POST
<#elseif oprespo=="GET">
			operation ${opname} to GET
<#elseif oprespo=="POST">
			operation ${opname} to POST
<#elseif oprespo=="PUT">
			operation ${opname} to PUT	
<#elseif oprespo=="DELETE">
			operation ${opname} to DELETE	
<#elseif oprespo=="PATCH">
			operation ${opname} to PATCH	
<#elseif oprespo=="HEAD">
			operation ${opname} to HEAD		
<#elseif oprespo=="OPTIONS">
			operation ${opname} to OPTIONS	
<#else>		
			operation ${opname} to POST // TODO map ${oprespo}
</#if>
</#macro>
<#if timestampString?has_content>
// ${timestampString}
</#if>
API description ${serviceSpecification.name}
<#if serviceSpecification.usageContext?has_content>
usage context ${serviceSpecification.usageContext.toString()} for BACKEND_INTEGRATION and FRONTEND_INTEGRATION
</#if>

<#if serviceSpecification.dataTypeProtectedRegion?has_content>
// ** BEGIN PROTECTED REGION for data types
${serviceSpecification.dataTypeProtectedRegion}
// ** END PROTECTED REGION for data types
</#if>

<#list serviceSpecification.dataTypes as dataType>
	<#if !dataType.isPrimitiveType()>
		<#if dataType.hasComments()>
// ${dataType.getCommentsString()}
		</#if>
		<#if dataType.isAbstractDataType()>
data type ${dataType.name} P // the type ${dataType.name} has not been specified or does not contain any attributes in CML
		<#elseif dataType.isEnumType()>
data type ${dataType.name} {${dataType.getEnumValuesString()}}
		<#else>
data type ${dataType.name} { <#list dataType.attributes as attribute>"${attribute.getName()}":${attribute.getType()}<#if attribute.isCollection()>*<#elseif attribute.isNullable()>?</#if><#if attribute_index < dataType.attributes?size - 1>, </#if></#list> }
		</#if>
	</#if>
</#list><#if serviceSpecification.eventTypes?has_content><#list serviceSpecification.eventTypes as event>
event type ${event}</#list></#if><#if serviceSpecification.commandTypes?has_content><#list serviceSpecification.commandTypes as command>
command type ${command}</#list></#if>
<#if serviceSpecification.endpointProtectedRegion?has_content>
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

<#if serviceSpecification.providerProtectedRegion?has_content>
// ** BEGIN PROTECTED REGION for API providers
${serviceSpecification.providerProtectedRegion}
// ** END PROTECTED REGION for API providers
</#if>

<#list serviceSpecification.providers as provider>
<#if provider.hasComments()>
	<#list provider.comments as comment>
// ${comment}
	</#list>
</#if>
API provider ${provider.name}
	<#if provider.domainVisionStatement?has_content>
	// ${provider.domainVisionStatement}
	</#if>
	<#list provider.endpointOffers as offer>
	offers ${offer.offeredEndpoint.name}
	at endpoint location "${offer.location}"
		via protocol <#if offer.protocol=="HTTP">${offer.protocol} binding resource ${offer.offeredEndpoint.name}Home at "/${offer.offeredEndpoint.name}"
<#list offer.offeredEndpoint.operations as operation><@opbind opname=operation.name oprespo=operation.endpointResponsibility!"POST"/></#list><#else>"${offer.protocol}"</#if><#if offer.hasProtocolComment()> // ${offer.getProtocolComment()}</#if>
	</#list>
</#list>

<#if serviceSpecification.clientProtectedRegion?has_content>
// ** BEGIN PROTECTED REGION for API clients
${serviceSpecification.clientProtectedRegion}
// ** END PROTECTED REGION for API clients
</#if>

<#list serviceSpecification.clients as client>
<#if client.hasComments()>
	<#list client.comments as comment>
// ${comment}
	</#list>
</#if>
API client ${client.name}
	<#if client.domainVisionStatement?has_content>
	// ${client.domainVisionStatement}
	</#if>
	<#list client.consumedOfferNames as offername>
	consumes ${offername}
	</#list>
</#list>
<#list serviceSpecification.flows as flow>
flow ${flow.name} type APPLICATION_FLOW
<#list flow.steps as step>
<#if step.isDepStep()>command ${step.command} emits event ${step.event}<#else>event ${step.event} triggers command ${step.command}</#if>
</#list>
</#list>
<#list serviceSpecification.scenarios as scenario>
scenario ${scenario.name} 
<#list scenario.stories as story>
story ${story.name}
  a "${story.persona}"
  wants to "${story.action}"
  so that "${story.goal}"
</#list>
</#list>
