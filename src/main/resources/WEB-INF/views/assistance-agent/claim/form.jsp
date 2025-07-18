<%--
- form.jsp
-
- Copyright (C) 2012-2025 Rafael Corchuelo.
-
- In keeping with the traditional purpose of furthering education and research, it is
- the policy of the copyright owner to permit non-commercial use and redistribution of
- this software. It has been tested carefully, but it is not guaranteed for any particular
- purposes.  The copyright owner does not offer any warranties or representations, nor do
- they accept any liabilities with respect to them.
--%>

<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>



<acme:form >
	<acme:input-textbox code="assistanceAgent.claim.form.label.registrationMoment" path="registrationMoment" readonly="true"/>
	<acme:input-textbox code="assistanceAgent.claim.form.label.description" path="description"/>
	<acme:input-select code="assistanceAgent.claim.form.label.type" path="type"  choices="${typeChoice}"  />
	<acme:input-textbox code="assistanceAgent.claim.form.label.indicator" path="indicator" readonly="true"/>
	<acme:input-email code="assistanceAgent.claim.form.label.passengerEmail" path="passengerEmail" />
	<acme:input-select code="assistanceAgent.claim.form.label.linkedTo" path="linkedTo" choices="${linkedTo}"/>

	<jstl:choose>
				<jstl:when test="${acme:anyOf(_command, 'show|update|delete|publish') && draftMode == true}">
					<acme:button code="assistanceAgent.trackingLog.form.button.trackingLog" action="/assistance-agent/tracking-log/listByClaim?claimId=${id}"/>
					<acme:button code="assistanceAgent.leg.form.button.leg" action="/assistance-agent/leg/show?id=${id}"/>

					<acme:submit code="assistanceAgent.claim.form.button.update" action="/assistance-agent/claim/update"/>
					<acme:submit code="assistanceAgent.claim.form.button.delete" action="/assistance-agent/claim/delete"/>
					<acme:submit code="assistanceAgent.claim.form.button.publish" action="/assistance-agent/claim/publish"/>
				</jstl:when>
				<jstl:when test="${acme:anyOf(_command, 'show')  && draftMode == false }">
					<acme:button code="assistanceAgent.trackingLog.form.button.trackingLog" action="/assistance-agent/tracking-log/listByClaim?claimId=${id}"/>
					<acme:button code="assistanceAgent.leg.form.button.leg" action="/assistance-agent/leg/show?id=${id}"/>
				</jstl:when>
				<jstl:when test="${acme:anyOf(_command, 'create')  }">
					<acme:submit code="assistanceAgent.claim.form.button.create" action="/assistance-agent/claim/create"/>
				</jstl:when>
						
		</jstl:choose>	
</acme:form>
