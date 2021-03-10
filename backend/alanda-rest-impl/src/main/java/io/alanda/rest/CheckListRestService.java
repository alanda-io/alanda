package io.alanda.rest;

import io.alanda.base.service.checklist.dto.CheckListDto;
import io.alanda.base.service.checklist.dto.CheckListItemDefinitionDto;
import io.alanda.base.service.checklist.dto.CheckListTemplateDto;
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
    Iterable<CheckListTemplateDto> getAllCheckListTemplates();

    @GET
    @Path("/template/{templateId}")
    CheckListTemplateDto getCheckListTemplate(@PathParam("templateId") Long templateId);

    @POST
    @Path("/template")
    CheckListTemplateDto createCheckListTemplate(CheckListTemplateDto templateDto);

    @PUT
    @Path("/template/{templateId}")
    CheckListTemplateDto updateCheckListTemplate(@PathParam("templateId") Long templateId, CheckListTemplateDto templateDto);

    @DELETE
    @Path("/template/{templateId}")
    Response deleteCheckListTemplate(@PathParam("templateId") Long checkListTemplateId);

    @GET
    @Path("/userTask/{taskInstanceGuid}")
    List<CheckListDto> getCheckListsForUserTaskInstance(@PathParam("taskInstanceGuid") String taskInstanceGuid);

    @PUT
    @Path("/{checkListId}/{key}")
    Response updateCheckListItemStatus(@PathParam("checkListId") Long checkListId, @PathParam("key") String key, Boolean status);

    @POST
    @Path("/{checkListId}/definitions")
    Response addCheckListItemToChecklist(@PathParam("checkListId") Long checkListId, CheckListItemDefinitionDto checkListItemDefinitionDto);

    @DELETE
    @Path("/{checkListId}/definition/{itemKey}")
    Response deleteCheckListItemFromChecklist(@PathParam("checkListId") Long checkListId, @PathParam("itemKey") String itemKey);
}
