<#import "entities/jdl-entity.ftl" as entityMacro>
<#assign allEntityNames = [] />
<#list boundedContexts as bc>
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
			<@entityMacro.jdlEntity entity />
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

dto * with mapstruct
service * with serviceImpl
