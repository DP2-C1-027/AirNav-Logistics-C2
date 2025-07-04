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
	<acme:input-textbox code="assistanceAgent.trackingLog.form.label.creationMoment" path="creationMoment" readonly="true"/>
	<acme:input-textbox code="assistanceAgent.trackingLog.form.label.lastUpdateMoment" path="lastUpdateMoment" readonly="true"/>
	<acme:input-textbox code="assistanceAgent.trackingLog.form.label.stepUndergoing" path="stepUndergoing"/>
	<acme:input-textbox code="assistanceAgent.trackingLog.form.label.resolutionPercentage" path="resolutionPercentage"/>
	<acme:input-select code="assistanceAgent.trackingLog.form.label.indicator" path="indicator" choices="${indicator}" />
	<acme:input-textbox code="assistanceAgent.trackingLog.form.label.resolutionDetails" path="resolutionDetails"/>
	<acme:input-textbox code="assistanceAgent.trackingLog.form.label.claim" path="claim" readonly="true"/>



	<jstl:choose>
				<jstl:when test="${acme:anyOf(_command, 'show|update|delete|publish') && draftMode == true}">
					<acme:button code="assistanceAgent.claim.form.button.claim" action="/assistance-agent/claim/showByTrackingLog?trackingLogId=${id}"/>
					<acme:submit code="assistanceAgent.trackingLog.form.button.update" action="/assistance-agent/tracking-log/update"/>
					<acme:submit code="assistanceAgent.trackingLog.form.button.delete" action="/assistance-agent/tracking-log/delete"/>
					<acme:submit code="assistanceAgent.trackingLog.form.button.publish" action="/assistance-agent/tracking-log/publish"/>
				</jstl:when>
				<jstl:when test="${acme:anyOf(_command, 'show')  && draftMode == false  }">
					<acme:button code="assistanceAgent.claim.form.button.claim" action="/assistance-agent/claim/showByTrackingLog?trackingLogId=${id}"/>
				</jstl:when>
				<jstl:when test="${acme:anyOf(_command, 'create')}">
					<acme:submit code="assistanceAgent.trackingLog.form.button.create" action="/assistance-agent/tracking-log/create?claimId=${claim.id}"/>
				</jstl:when>		
		</jstl:choose>	
</acme:form>
