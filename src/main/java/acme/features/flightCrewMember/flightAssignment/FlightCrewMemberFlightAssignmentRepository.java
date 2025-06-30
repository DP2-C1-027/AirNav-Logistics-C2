
package acme.features.flightCrewMember.flightAssignment;

import java.util.Collection;
import java.util.Date;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.flightAssignment.ActivityLog;
import acme.entities.flightAssignment.Duty;
import acme.entities.flightAssignment.FlightAssignment;
import acme.entities.legs.Leg;

@Repository
public interface FlightCrewMemberFlightAssignmentRepository extends AbstractRepository {

	@Query("SELECT fa FROM FlightAssignment fa WHERE fa.id = :flightAssignmentId")
	FlightAssignment findFlightAssignmentById(int flightAssignmentId);

	// List my completed assignments
	@Query("SELECT fa FROM FlightAssignment fa WHERE fa.flightCrewMember.id = :flightCrewMemberId AND fa.leg.scheduledArrival < :moment")
	Collection<FlightAssignment> findAllCompletedFlightAssignments(Date moment, int flightCrewMemberId);

	// List my planned assignments
	@Query("SELECT fa FROM FlightAssignment fa WHERE fa.flightCrewMember.id = :flightCrewMemberId AND fa.leg.scheduledDeparture >= :moment")
	Collection<FlightAssignment> findAllPlannedFlightAssignments(Date moment, int flightCrewMemberId);

	// List published assignments
	@Query("SELECT fa FROM FlightAssignment fa WHERE fa.draftMode = true")
	Collection<FlightAssignment> findAllPublishedFlightAssignments();

	// Show all the legs in the select choices
	@Query("SELECT l FROM Leg l WHERE l.aircraft.airline.id = :airlineId AND l.scheduledDeparture > :moment AND l.draftMode = false AND l.flight.draftMode = false ORDER BY l.scheduledDeparture ASC")
	Collection<Leg> findAllLegsByAirlineId(Date moment, int airlineId);

	// Find all the activity logs to remove the flight assignment
	@Query("SELECT a FROM ActivityLog a WHERE a.flightAssignment.id = :flightAssignmentId")
	Collection<ActivityLog> findAllActivityLogs(int flightAssignmentId);

	@Query("SELECT fa FROM FlightAssignment fa WHERE fa.leg.id = :legId AND fa.duty = :duty")
	FlightAssignment findFlightAssignmentByLegAndDuty(int legId, Duty duty);

	@Query("SELECT COUNT(fa) > 0 FROM FlightAssignment fa WHERE fa.leg.id = :legId AND fa.duty IN ('PILOT', 'CO_PILOT') AND fa.duty = :duty AND fa.id != :id AND fa.draftMode = false")
	Boolean hasDutyAssigned(int legId, Duty duty, int id);

	@Query("SELECT COUNT(fa) > 0 FROM FlightAssignment fa WHERE fa.flightCrewMember.id = :flightCrewMemberId AND fa.draftMode = false AND (fa.leg.scheduledDeparture < :end AND fa.leg.scheduledArrival > :start)")
	Boolean hasConflictingFlightAssignment(int flightCrewMemberId, Date start, Date end);

	@Query("SELECT l FROM Leg l WHERE l.aircraft.airline.id = :airlineId")
	Collection<Leg> findAllLegsFromAirline(int airlineId);

	@Query("SELECT l FROM Leg l")
	Collection<Leg> findAllLegs();

	@Query("SELECT fa FROM FlightAssignment fa WHERE fa.leg.flight.id=:flightId")
	Collection<FlightAssignment> findFlightAssignmentsByFlightId(int flightId);

	@Query("SELECT fa FROM FlightAssignment fa WHERE fa.leg.id=:legId")
	Collection<FlightAssignment> findFlightAssignmentsByLegId(int legId);
}
