package io.alanda.rest.impl;

import io.alanda.base.service.checklist.CheckListService;
import io.alanda.base.service.checklist.dto.CheckListDto;
import io.alanda.base.service.checklist.dto.CheckListItemDefinitionDto;
import io.alanda.base.service.checklist.dto.CheckListTemplateDto;
import io.alanda.rest.impl.vm.CheckListItemVM;
import io.alanda.rest.impl.vm.CheckListItemDefinitionVM;
import io.alanda.rest.impl.vm.CheckListTemplateVM;
import io.alanda.rest.impl.vm.CheckListVM;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Tag(name = "CheckListRestService")
@Path("/app/checklist")
@Produces(MediaType.APPLICATION_JSON)
public class CheckListRestService {
    @Inject
    private CheckListService checkListService;

    @GET
    @Path("/templates")
    public List<CheckListTemplateVM> getAllCheckListTemplates() {
        final List<CheckListTemplateDto> allCheckListTemplates = checkListService.getAllCheckListTemplates();

        return null;
    }


    @GET
    @Path("/template/{templateId}")
    public CheckListTemplateVM getCheckListTemplate(Long templateId) {
        final CheckListTemplateDto checkListTemplate = checkListService.getCheckListTemplate(templateId).orElse(null);

        return null;
    }

    @POST
    @Path("/template")
    public Response createCheckListTemplate(CheckListTemplateVM templateVM) {
        checkListService.saveCheckListTemplate(null);

        return Response.ok().build();
    }

    @PUT
    @Path("/template/{templateId}")
    public Response updateCheckListTemplate(Long templateId, CheckListTemplateVM templateVM) {
        checkListService.saveCheckListTemplate(null);

        return Response.ok().build();
    }

    @DELETE
    @Path("/template/{templateId}")
    public Response deleteCheckListTemplate(Long checkListTemplateId) {
        return Response.ok().build();
    }

    @GET
    @Path("/userTask/{taskInstanceGuid}")
    public List<CheckListVM> getCheckListsForUserTaskInstance(String taskInstanceGuid) {
        checkListService.getCheckListsForUserTaskInstance(taskInstanceGuid);
        return null;
    }

    @PUT
    @Path("/{checkListId}/{key}")
    public Response updateCheckListItemStatus(Long checkListId, String key, Boolean status) {
        checkListService.updateCheckListItemStatus(checkListId, key, status);
        return Response.ok().build();
    }

    @POST
    @Path("/{checkListId}/definitions")
    public Response addCheckListItemToChecklist(Long checkListId, CheckListItemDefinitionVM checkListItemDefinitionVM) {
        final Optional<CheckListDto> checkList = checkListService.getCheckList(checkListId);
        checkList.ifPresent(cl -> {
            final CheckListItemDefinitionDto definitionDto = null;
            cl.getItemDefinitions().add(definitionDto);
        });

        return Response.ok().build();
    }

    @DELETE
    @Path("/{checkListId}/definition/{itemKey}")
    public Response deleteCheckListItemFromChecklist(Long checkListId, String itemKey) {
        final Optional<CheckListDto> checkList = checkListService.getCheckList(checkListId);
        checkList.map(cl -> {
            cl.setItemDefinitions(cl.getItemDefinitions().stream()
                    .filter(def -> !def.getKey().equals("itemKey"))
                    .collect(Collectors.toList()));

            return checkListService.saveCheckList(cl.getId(), cl);
        });

        return Response.ok().build();
    }
}
