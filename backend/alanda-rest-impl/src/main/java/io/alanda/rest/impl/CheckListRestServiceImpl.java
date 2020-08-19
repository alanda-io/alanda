package io.alanda.rest.impl;

import io.alanda.base.service.checklist.CheckListService;
import io.alanda.base.service.checklist.dto.CheckListDto;
import io.alanda.base.service.checklist.dto.CheckListItemDefinitionDto;
import io.alanda.base.service.checklist.dto.CheckListTemplateDto;
import io.alanda.rest.CheckListRestService;
import io.alanda.rest.impl.vm.*;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class CheckListRestServiceImpl implements CheckListRestService {

    @Inject
    private CheckListService checkListService;

    @Inject
    private CheckListDTOToVMMapper checkListDTOToVMMapper;

    @Inject
    private CheckListVMToDTOMapper checkListVMToDTOMapper;

    @Override
    public Iterable<CheckListTemplateVM> getAllCheckListTemplates() {
        final Iterable<CheckListTemplateDto> allCheckListTemplates = checkListService.getAllCheckListTemplates();
        return StreamSupport.stream(allCheckListTemplates.spliterator(), false).map(CheckListDTOToVMMapper::mapCheckListTemplateDTOToVM).collect(Collectors.toList());
    }


    @Override
    public CheckListTemplateVM getCheckListTemplate(Long templateId) {
        final CheckListTemplateDto checkListTemplate = checkListService.getCheckListTemplate(templateId).orElse(null);
        return checkListDTOToVMMapper.mapCheckListTemplateDTOToVM(checkListTemplate);

    }

    @Override
    public Response createCheckListTemplate(CheckListTemplateVM templateVM) {
        CheckListTemplateDto checkListTemplateDto = checkListVMToDTOMapper.mapCheckListTemplateVMToDTO(templateVM);
        checkListService.saveCheckListTemplate(checkListTemplateDto);

        return Response.ok().build();
    }

    @Override
    public Response updateCheckListTemplate(Long templateId, CheckListTemplateVM templateVM) {
        CheckListTemplateDto checkListTemplateDto = checkListVMToDTOMapper.mapCheckListTemplateVMToDTO(templateVM);
        checkListTemplateDto.setId(templateId);
        checkListService.saveCheckListTemplate(checkListTemplateDto);

        return Response.ok().build();
    }

    @Override
    public Response deleteCheckListTemplate(Long checkListTemplateId) {
        return Response.ok().build();
    }

    @Override
    public List<CheckListVM> getCheckListsForUserTaskInstance(String taskInstanceGuid) {
        List<CheckListDto> checkListDtos = checkListService.getCheckListsForUserTaskInstance(taskInstanceGuid);
        return checkListDtos.stream().map(CheckListDTOToVMMapper::mapCheckListDTOToVM).collect(Collectors.toList());
    }

    @Override
    public Response updateCheckListItemStatus(Long checkListId, String key, Boolean status) {
        checkListService.updateCheckListItemStatus(checkListId, key, status);
        return Response.ok().build();
    }

    @Override
    public Response addCheckListItemToChecklist(Long checkListId, CheckListItemDefinitionVM checkListItemDefinitionVM) {
        final Optional<CheckListDto> checkList = checkListService.getCheckList(checkListId);
        checkList.ifPresent(cl -> {
            final CheckListItemDefinitionDto definitionDto = null;
            //cl.getCheckListItems().add(definitionDto);
        });

        return Response.ok().build();
    }

    @Override
    public Response deleteCheckListItemFromChecklist(Long checkListId, String itemKey) {
        final Optional<CheckListDto> checkList = checkListService.getCheckList(checkListId);
        checkList.map(cl -> {
            /*cl.setItemDefinitions(cl.getItemDefinitions().stream()
                    .filter(def -> !def.getKey().equals("itemKey"))
                    .collect(Collectors.toList()));*/

            return checkListService.saveCheckList(cl.getId(), cl);
        });

        return Response.ok().build();
    }
}
