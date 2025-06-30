
package acme.features.flightCrewMember.flightAssignment;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flightAssignment.CurrentStatus;
import acme.entities.flightAssignment.Duty;
import acme.entities.flightAssignment.FlightAssignment;
import acme.entities.legs.Leg;
import acme.realms.flightcrewmember.AvailabilityStatus;
import acme.realms.flightcrewmember.FlightCrewMember;

@GuiService
public class FlightCrewMemberFlightAssignmentPublishService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private FlightCrewMemberFlightAssignmentRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {

		boolean isAuthorised = false;

		if (super.getRequest().getPrincipal().hasRealmOfType(FlightCrewMember.class))

			// Only is allowed to publish a flight assignment if the creator is associated.
			// A flight assignment cannot be published if the assignment is in published mode and not in draft mode.
			if (super.getRequest().getMethod().equals("POST") && super.getRequest().getData("id", Integer.class) != null) {

				Integer flightAssignmentId = super.getRequest().getData("id", Integer.class);

				if (flightAssignmentId != null) {

					FlightAssignment flightAssignment = this.repository.findFlightAssignmentById(flightAssignmentId);
					FlightCrewMember flightCrewMember = (FlightCrewMember) super.getRequest().getPrincipal().getActiveRealm();

					// Only is allowed to publish a flight assignment if the leg selected is between the options shown.
					Collection<Leg> legs = this.repository.findAllLegsByAirlineId(MomentHelper.getCurrentMoment(), flightCrewMember.getAirline().getId());
					Leg legSelected = super.getRequest().getData("leg", Leg.class);

					isAuthorised = flightAssignment != null && flightAssignment.getDraftMode() && flightAssignment.getFlightCrewMember().equals(flightCrewMember) && (legSelected == null || legs.contains(legSelected));

				}

			}

		super.getResponse().setAuthorised(isAuthorised);
	}

	@Override
	public void load() {
		int flightAssignmentId = super.getRequest().getData("id", int.class);
		FlightAssignment flightAssignment = this.repository.findFlightAssignmentById(flightAssignmentId);

		super.getBuffer().addData(flightAssignment);
	}

	@Override
	public void bind(final FlightAssignment flightAssignment) {
		super.bindObject(flightAssignment, "duty", "currentStatus", "remarks", "leg");
	}

	@Override
	public void validate(final FlightAssignment flightAssignment) {
		if (flightAssignment.getFlightCrewMember() != null) {
			// Only flight crew members with an "AVAILABLE" status can be assigned 
			boolean isAvailable = flightAssignment.getFlightCrewMember().getAvailabilityStatus().equals(AvailabilityStatus.AVAILABLE);
			super.state(isAvailable, "flightCrewMember", "acme.validation.flightAssignment.flightCrewMember.available");

			// Cannot be assigned to multiple legs simultaneously
			boolean isAlreadyAssigned = this.repository.hasConflictingFlightAssignment(flightAssignment.getFlightCrewMember().getId(), flightAssignment.getLeg().getScheduledDeparture(), flightAssignment.getLeg().getScheduledArrival());
			super.state(!isAlreadyAssigned, "flightCrewMember", "acme.validation.flightAssignment.flightCrewMember.multipleLegs");
		}

		// Each leg can only have one pilot and one co-pilot
		if (flightAssignment.getDuty() != null && flightAssignment.getLeg() != null) {
			boolean isDutyAlreadyAssigned = this.repository.hasDutyAssigned(flightAssignment.getLeg().getId(), flightAssignment.getDuty(), flightAssignment.getId());
			super.state(!isDutyAlreadyAssigned, "duty", "acme.validation.flightAssignment.duty");
		}

	}

	@Override
	public void perform(final FlightAssignment flightAssignment) {
		flightAssignment.setDraftMode(false);
		flightAssignment.setMoment(MomentHelper.getCurrentMoment());
		this.repository.save(flightAssignment);
	}

	@Override
	public void unbind(final FlightAssignment flightAssignment) {
		Dataset dataset = super.unbindObject(flightAssignment, "duty", "moment", "currentStatus", "remarks", "draftMode", "flightCrewMember", "leg");

		FlightCrewMember flightCrewMember = flightAssignment.getFlightCrewMember();
		Leg leg = flightAssignment.getLeg();

		// Duty choices
		SelectChoices dutyChoices = SelectChoices.from(Duty.class, flightAssignment.getDuty());
		dataset.put("dutyChoices", dutyChoices);
		dataset.put("duty", dutyChoices.getSelected().getKey());

		// Status choices
		SelectChoices statusChoices = SelectChoices.from(CurrentStatus.class, flightAssignment.getCurrentStatus());
		dataset.put("statusChoices", statusChoices);
		dataset.put("status", statusChoices.getSelected().getKey());

		// Leg choices
		SelectChoices legChoices = new SelectChoices();
		Collection<Leg> legs = this.repository.findAllLegsByAirlineId(MomentHelper.getCurrentMoment(), flightCrewMember.getAirline().getId());
		for (Leg legChoice : legs) {
			String key = Integer.toString(legChoice.getId());
			String label = legChoice.getFlightNumber() + " (" + legChoice.getScheduledDeparture() + " - " + legChoice.getScheduledArrival() + ") ";
			boolean isSelected = legChoice.equals(flightAssignment.getLeg());
			legChoices.add(key, label, isSelected);
		}

		dataset.put("legChoices", legChoices);

		// Flight Crew Member details
		dataset.put("flightCrewMember", flightCrewMember.getIdentity().getFullName());
		dataset.put("codigo", flightCrewMember.getCodigo());
		dataset.put("phoneNumber", flightCrewMember.getPhoneNumber());
		dataset.put("languageSkills", flightCrewMember.getLanguageSkills());
		dataset.put("availabilityStatus", flightCrewMember.getAvailabilityStatus());
		dataset.put("salary", flightCrewMember.getSalary());
		dataset.put("yearsOfExperience", flightCrewMember.getYearsOfExperience());
		dataset.put("airline", flightCrewMember.getAirline().getName());

		// Leg details
		dataset.put("flightNumber", leg.getFlightNumber());
		dataset.put("scheduledDeparture", leg.getScheduledDeparture());
		dataset.put("scheduledArrival", leg.getScheduledArrival());
		dataset.put("status", leg.getStatus());
		dataset.put("duration", leg.getDuration());
		dataset.put("departureAirport", leg.getDepartureAirport().getName());
		dataset.put("arrivalAirport", leg.getArrivalAirport().getName());
		dataset.put("aircraft", leg.getAircraft().getRegistrationNumber());
		dataset.put("flight", leg.getFlight().getTag());
		dataset.put("legAirline", leg.getAircraft().getAirline().getName());

		super.getResponse().addData(dataset);
	}

}
