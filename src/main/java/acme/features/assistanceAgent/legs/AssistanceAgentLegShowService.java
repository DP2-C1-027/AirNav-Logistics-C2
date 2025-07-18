/*
 * AssistanceAgentClaimShowService.java
 *
 * Copyright (C) 2012-2025 Rafael Corchuelo.
 *
 * In keeping with the traditional purpose of furthering education and research, it is
 * the policy of the copyright owner to permit non-commercial use and redistribution of
 * this software. It has been tested carefully, but it is not guaranteed for any particular
 * purposes. The copyright owner does not offer any warranties or representations, nor do
 * they accept any liabilities with respect to them.
 */

package acme.features.assistanceAgent.legs;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claims.Claim;
import acme.entities.legs.Leg;
import acme.realms.AssistanceAgent;

@GuiService
public class AssistanceAgentLegShowService extends AbstractGuiService<AssistanceAgent, Leg> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AssistanceAgentLegRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status = true;

		if (super.getRequest().hasData("id")) {
			AssistanceAgent assistance = (AssistanceAgent) super.getRequest().getPrincipal().getActiveRealm();

			Integer claimId;
			String isInteger;

			isInteger = super.getRequest().getData("id", String.class).trim();
			if (!isInteger.isBlank() && isInteger.chars().allMatch((e) -> e > 47 && e < 58))
				claimId = Integer.valueOf(isInteger);
			else
				claimId = Integer.valueOf(-1);

			Claim claim = claimId != null ? this.repository.findOneClaimById(claimId) : null;
			status = claim != null && super.getRequest().getPrincipal().hasRealm(assistance) && claim.getRegisteredBy().equals(assistance);
		} else
			status = false;

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Leg claim;
		int id;

		id = super.getRequest().getData("id", int.class);
		claim = this.repository.findLinkedLegByClaimId(id);

		super.getBuffer().addData(claim);
	}

	@Override
	public void unbind(final Leg leg) {
		Dataset dataset;

		dataset = super.unbindObject(leg, "flightNumber", "scheduledDeparture", "scheduledArrival", "duration", "status", "departureAirport", "arrivalAirport", "aircraft", "flight");

		super.getResponse().addData(dataset);
	}

}
