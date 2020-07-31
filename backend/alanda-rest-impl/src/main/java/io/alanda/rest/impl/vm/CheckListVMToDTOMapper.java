package io.alanda.rest.impl.vm;

import io.alanda.base.service.checklist.dto.CheckListDto;
import io.alanda.base.service.checklist.dto.CheckListItemDefinitionDto;
import io.alanda.base.service.checklist.dto.CheckListItemDto;
import io.alanda.base.service.checklist.dto.CheckListTemplateDto;

import java.util.stream.Collectors;

public class CheckListVMToDTOMapper {
    public static CheckListDto mapCheckListVMToDTO(CheckListVM checkListVM) {
        final CheckListDto checkListDTO = new CheckListDto();

        checkListDTO.setId(checkListVM.getId());
        checkListDTO.setName(checkListVM.getName());
        checkListDTO.setCheckListItems(checkListVM.getCheckListItems().stream()
                .map(CheckListVMToDTOMapper::mapCheckListItemVMToDTO).collect(Collectors.toList()));

        return checkListDTO;
    }

    public static CheckListItemDto mapCheckListItemVMToDTO(CheckListItemVM itemVM) {
        final CheckListItemDto itemDTO = new CheckListItemDto();

        itemDTO.setItemDefinition(mapCheckListItemDefinitionVMToDTO(itemVM.getDefinition()));
        itemDTO.setStatus(itemVM.getStatus());

        return itemDTO;
    }

    public static CheckListItemDefinitionDto mapCheckListItemDefinitionVMToDTO(CheckListItemDefinitionVM definitionVM) {
        final CheckListItemDefinitionDto definitionDTO = new CheckListItemDefinitionDto();

        definitionDTO.setKey(definitionVM.getKey());
        definitionDTO.setDisplayText(definitionVM.getDisplayText());
        definitionDTO.setRequired(definitionVM.getRequired());
        definitionDTO.setCustom(definitionVM.getCustom());

        return definitionDTO;
    }

    public static CheckListTemplateDto mapCheckListTemplateVMToDTO(CheckListTemplateVM templateVM) {
        final CheckListTemplateDto templateDTO = new CheckListTemplateDto();

        templateDTO.setId(templateVM.getId());
        templateDTO.setItemBackend(templateVM.getItemBackend());
        templateDTO.setName(templateVM.getName());
        templateDTO.setItemDefinitions(templateVM.getItemDefinitions().stream()
                .map(CheckListVMToDTOMapper::mapCheckListItemDefinitionVMToDTO)
                .collect(Collectors.toList()));

        for (int i = 0; i < templateDTO.getItemDefinitions().size(); i++) {
            templateDTO.getItemDefinitions().get(i).setSortOrder((long) i);
        }

        templateDTO.setUserTasks(templateVM.getUserTasks());

        return templateDTO;
    }
}
