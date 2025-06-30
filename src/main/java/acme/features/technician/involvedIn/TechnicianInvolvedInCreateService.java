
package acme.features.technician.involvedIn;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.maintanenceRecords.InvolvedIn;
import acme.entities.maintanenceRecords.MaintanenceRecord;
import acme.entities.maintanenceRecords.Task;
import acme.realms.Technician;

@GuiService
public class TechnicianInvolvedInCreateService extends AbstractGuiService<Technician, InvolvedIn> {
	// Internal state ---------------------------------------------------------

	@Autowired
	private TechnicianInvolvedInRepository repository;


	@Override
	public void authorise() {

		boolean status = true;
		Task task;
		Technician tech;

		if (super.getRequest().hasData("maintanenceRecord")) {
			Integer recordId;
			String isInteger;
			isInteger = super.getRequest().getData("maintanenceRecord", String.class).trim();
			if (!isInteger.isBlank() && isInteger.chars().allMatch((e) -> e > 47 && e < 58))
				recordId = Integer.valueOf(isInteger);
			else
				recordId = Integer.valueOf(-1);

			MaintanenceRecord record = this.repository.findRecordById(recordId);
			tech = record != null ? record.getTechnician() : null;
			//aqui miro tb que no esté publicado
			status = tech == null ? recordId.equals(Integer.valueOf(0)) : super.getRequest().getPrincipal().hasRealm(tech) && record.isDraftMode();

		} else if (super.getRequest().getMethod().equals("POST"))
			status = false;

		if (!status) {
			super.getResponse().setAuthorised(false);
			return;
		}

		if (super.getRequest().hasData("task")) {
			Integer id;

			String isInteger2;
			isInteger2 = super.getRequest().getData("task", String.class);
			if (!isInteger2.isBlank() && isInteger2.chars().allMatch((e) -> e > 47 && e < 58))
				id = Integer.valueOf(isInteger2);
			else
				id = Integer.valueOf(-1);

			task = this.repository.findTaskById(id);
			tech = task == null ? null : task.getTechnician();
			status = tech == null ? id.equals(Integer.valueOf(0)) : super.getRequest().getPrincipal().hasRealm(tech);
		} else if (super.getRequest().getMethod().equals("POST"))
			status = false;

		if (!status) {
			super.getResponse().setAuthorised(false);
			return;
		}

		if (super.getRequest().hasData("id")) {
			Integer id;
			String isInteger;
			isInteger = super.getRequest().getData("id", String.class).trim();
			if (!isInteger.isBlank() && isInteger.chars().allMatch((e) -> e > 47 && e < 58))
				id = Integer.valueOf(isInteger);
			else
				id = Integer.valueOf(-1);

			if (!id.equals(Integer.valueOf(0)))
				status = false;

		} else if (super.getRequest().getMethod().equals("POST"))
			status = false;

		super.getResponse().setAuthorised(status);

	}

	@Override
	public void load() {
		InvolvedIn involved;

		involved = new InvolvedIn();

		super.getBuffer().addData(involved);
	}

	@Override
	public void bind(final InvolvedIn involved) {

		super.bindObject(involved, "maintanenceRecord", "task");
	}

	@Override
	public void validate(final InvolvedIn involved) {
		MaintanenceRecord maintanenceRecord;
		Task task;

		maintanenceRecord = involved.getMaintanenceRecord();
		task = involved.getTask();

		super.state(maintanenceRecord != null, "*", "technician.involved-in.create.error.null-record");
		super.state(task != null, "*", "technician.involved-in.create.error.null-task");

		boolean exists = this.repository.existsByRecordAndTask(maintanenceRecord, task);
		super.state(!exists, "*", "technician.involved-in.create.error.duplicate-record-task");

	}

	@Override
	public void perform(final InvolvedIn involved) {
		this.repository.save(involved);
	}

	@Override
	public void unbind(final InvolvedIn involved) {
		Dataset dataset;

		Technician tech = (Technician) super.getRequest().getPrincipal().getActiveRealm();

		SelectChoices recordChoices;
		SelectChoices taskChoices;

		//aqui estarian todas las tasks que existen en el sistema
		//solo puede hacerlo sobre sus maintanenceRecords
		Collection<Task> tasks = this.repository.findAllTasks();

		Collection<MaintanenceRecord> records = this.repository.findNotPublishRecord(tech.getId(), true);

		taskChoices = SelectChoices.from(tasks, "description", involved.getTask());

		recordChoices = SelectChoices.from(records, "maintanenceMoment", involved.getMaintanenceRecord());

		dataset = super.unbindObject(involved, "maintanenceRecord", "task");
		dataset.put("maintanenceRecord", recordChoices);
		dataset.put("task", taskChoices);
		dataset.put("draftMode", true);

		super.addPayload(dataset, involved, "maintanenceRecord", "task");
		super.getResponse().addData(dataset);

	}
}
