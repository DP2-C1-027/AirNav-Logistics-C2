
package acme.features.technician.maintenanceRecord;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.maintanenceRecords.MaintanenceRecord;
import acme.realms.Technician;

@GuiController
public class TechnicianRecordController extends AbstractGuiController<Technician, MaintanenceRecord> {

	@Autowired
	private TechnicianRecordServiceShow			showService;

	@Autowired
	private TechnicianRecordServicePublishList	publishListService;

	@Autowired
	private TechnicianRecordServiceMyList		myListService;

	@Autowired
	private TechnicianRecordCreateService		createService;

	@Autowired
	private TechnicianRecordUpdateService		updateService;

	@Autowired
	private TechnicianRecordPublishService		publishService;

	@Autowired
	private TechnicianRecordDeleteService		deleteService;


	@PostConstruct
	protected void initialise() {
		super.addCustomCommand("publishList", "list", this.publishListService);
		super.addBasicCommand("list", this.myListService);
		super.addBasicCommand("show", this.showService);
		super.addBasicCommand("create", this.createService);
		super.addBasicCommand("update", this.updateService);
		super.addBasicCommand("delete", this.deleteService);
		super.addCustomCommand("publish", "update", this.publishService);
	}

}
