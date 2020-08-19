package io.alanda.rest;

import io.alanda.rest.impl.vm.*;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Tag(name = "CheckListRestService")
@Path("/app/checklist")
@Produces(MediaType.APPLICATION_JSON)
public interface CheckListRestService {

    @GET
    @Path("/templates")
    Iterable<CheckListTemplateVM> getAllCheckListTemplates();

    @GET
    @Path("/template/{templateId}")
    CheckListTemplateVM getCheckListTemplate(@PathParam("templateId") Long templateId);

    @POST
    @Path("/template")
    Response createCheckListTemplate(CheckListTemplateVM templateVM);

    @PUT
    @Path("/template/{templateId}")
    Response updateCheckListTemplate(@PathParam("templateId") Long templateId, CheckListTemplateVM templateVM);

    @DELETE
    @Path("/template/{templateId}")
    Response deleteCheckListTemplate(@PathParam("templateId") Long checkListTemplateId);

    @GET
    @Path("/userTask/{taskInstanceGuid}")
    List<CheckListVM> getCheckListsForUserTaskInstance(@PathParam("taskInstanceGuid") String taskInstanceGuid);

    @PUT
    @Path("/{checkListId}/{key}")
    Response updateCheckListItemStatus(@PathParam("checkListId") Long checkListId, @PathParam("key") String key, Boolean status);

    @POST
    @Path("/{checkListId}/definitions")
    Response addCheckListItemToChecklist(@PathParam("checkListId") Long checkListId, CheckListItemDefinitionVM checkListItemDefinitionVM);

    @DELETE
    @Path("/{checkListId}/definition/{itemKey}")
    Response deleteCheckListItemFromChecklist(@PathParam("checkListId") Long checkListId, @PathParam("itemKey") String itemKey);
}
