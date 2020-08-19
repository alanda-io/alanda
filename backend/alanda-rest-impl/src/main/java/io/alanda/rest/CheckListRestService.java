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
    CheckListTemplateVM getCheckListTemplate(Long templateId);

    @POST
    @Path("/template")
    Response createCheckListTemplate(CheckListTemplateVM templateVM);

    @PUT
    @Path("/template/{templateId}")
    Response updateCheckListTemplate(Long templateId, CheckListTemplateVM templateVM);

    @DELETE
    @Path("/template/{templateId}")
    Response deleteCheckListTemplate(Long checkListTemplateId);

    @GET
    @Path("/userTask/{taskInstanceGuid}")
    List<CheckListVM> getCheckListsForUserTaskInstance(String taskInstanceGuid);

    @PUT
    @Path("/{checkListId}/{key}")
    Response updateCheckListItemStatus(Long checkListId, String key, Boolean status);

    @POST
    @Path("/{checkListId}/definitions")
    Response addCheckListItemToChecklist(Long checkListId, CheckListItemDefinitionVM checkListItemDefinitionVM);

    @DELETE
    @Path("/{checkListId}/definition/{itemKey}")
    Response deleteCheckListItemFromChecklist(Long checkListId, String itemKey);
}
