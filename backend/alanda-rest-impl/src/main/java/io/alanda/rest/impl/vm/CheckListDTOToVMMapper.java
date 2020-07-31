package io.alanda.rest.impl.vm;

import io.alanda.base.service.checklist.dto.CheckListDto;
import io.alanda.base.service.checklist.dto.CheckListItemDefinitionDto;
import io.alanda.base.service.checklist.dto.CheckListItemDto;
import io.alanda.base.service.checklist.dto.CheckListTemplateDto;

import java.util.stream.Collectors;

public class CheckListDTOToVMMapper {
    public static CheckListVM mapCheckListDTOToVM(CheckListDto checkListDto) {
        final CheckListVM checkListVM = new CheckListVM();

        checkListVM.setId(checkListDto.getId());
        checkListVM.setName(checkListDto.getName());
        checkListVM.setCheckListItems(checkListDto.getCheckListItems().stream()
                .map(CheckListDTOToVMMapper::mapCheckListItemDTOToVM).collect(Collectors.toList()));

        return checkListVM;
    }

    public static CheckListItemVM mapCheckListItemDTOToVM(CheckListItemDto itemDto) {
        final CheckListItemVM itemVM = new CheckListItemVM();

        itemVM.setDefinition(mapCheckListItemDefinitionDTOToVM(itemDto.getItemDefinition()));
        itemVM.setStatus(itemDto.getStatus());

        return itemVM;
    }

    public static CheckListItemDefinitionVM mapCheckListItemDefinitionDTOToVM(CheckListItemDefinitionDto definitionDto) {
        final CheckListItemDefinitionVM definitionVM = new CheckListItemDefinitionVM();

        definitionVM.setKey(definitionDto.getKey());
        definitionVM.setDisplayText(definitionDto.getDisplayText());
        definitionVM.setRequired(definitionDto.getRequired());
        definitionVM.setCustom(definitionDto.getCustom());

        return definitionVM;
    }

    public static CheckListTemplateVM mapCheckListTemplateDTOToVM(CheckListTemplateDto templateDto) {
        final CheckListTemplateVM templateVM = new CheckListTemplateVM();

        templateVM.setId(templateDto.getId());
        templateVM.setItemBackend(templateDto.getItemBackend());
        templateVM.setName(templateDto.getName());
        templateVM.setItemDefinitions(templateDto.getItemDefinitions().stream()
                .map(CheckListDTOToVMMapper::mapCheckListItemDefinitionDTOToVM).collect(Collectors.toList()));
        templateVM.setUserTasks(templateDto.getUserTasks());

        return templateVM;
    }
}
