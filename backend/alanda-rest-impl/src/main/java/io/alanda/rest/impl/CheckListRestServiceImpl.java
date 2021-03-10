package io.alanda.rest.impl;

import io.alanda.base.service.checklist.CheckListService;
import io.alanda.base.service.checklist.dto.CheckListDto;
import io.alanda.base.service.checklist.dto.CheckListItemDefinitionDto;
import io.alanda.base.service.checklist.dto.CheckListTemplateDto;
import io.alanda.rest.CheckListRestService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;


public class CheckListRestServiceImpl implements CheckListRestService {

    @Inject
    private CheckListService checkListService;

    @Override
    public Iterable<CheckListTemplateDto> getAllCheckListTemplates() {
        return checkListService.getAllCheckListTemplates();
    }

    @Override
    public CheckListTemplateDto getCheckListTemplate(Long templateId) {
        return checkListService.getCheckListTemplate(templateId)
                .orElseThrow(() -> new NotFoundException("Could not find template with id " + templateId));
    }

    @Override
    public CheckListTemplateDto createCheckListTemplate(CheckListTemplateDto templateDto) {
        return checkListService.upsertCheckListTemplate(templateDto);
    }

    @Override
    public CheckListTemplateDto updateCheckListTemplate(Long templateId, CheckListTemplateDto templateDto) {
        return checkListService.updateCheckListTemplate(templateDto);
    }

    @Override
    public Response deleteCheckListTemplate(Long checkListTemplateId) {
        return Response.ok().build();
    }

    @Override
    public List<CheckListDto> getCheckListsForUserTaskInstance(String taskInstanceGuid) {
        return checkListService.getCheckListsForUserTaskInstance(taskInstanceGuid);
    }

    @Override
    public Response updateCheckListItemStatus(Long checkListId, String key, Boolean status) {
        checkListService.updateCheckListItemStatus(checkListId, key, status);
        return Response.ok().build();
    }

    @Override
    public Response addCheckListItemToChecklist(Long checkListId, CheckListItemDefinitionDto checkListItemDefinitionDto) {
        checkListService.addCheckListItemToChecklist(checkListId, checkListItemDefinitionDto);
        /*final Optional<CheckListDto> checkList = checkListService.getCheckList(checkListId);
        checkList.ifPresent(cl -> {
            checkListService.
        });*/

        return Response.ok().build();
    }

    @Override
    public Response deleteCheckListItemFromChecklist(Long checkListId, String itemKey) {
        checkListService.removeCheckListItemFromChecklist(checkListId, itemKey);
        //final Optional<CheckListDto> checkList = checkListService.getCheckList(checkListId);
        /*checkList.map(cl -> {
            cl.setItemDefinitions(cl.getItemDefinitions().stream()
                    .filter(def -> !def.getKey().equals("itemKey"))
                    .collect(Collectors.toList()));

            return checkListService.saveCheckList(cl.getId(), cl);
        });*/

        return Response.ok().build();
    }
}
