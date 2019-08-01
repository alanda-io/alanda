/**
 * 
 */
package io.alanda.base.mapper;

import org.dozer.CustomConverter;
import org.dozer.DozerConverter;

import io.alanda.base.dto.SimplePhaseDto;
import io.alanda.base.entity.PmcProjectPhase;

/**
 * @author jlo
 */
public class PhaseMapper extends DozerConverter<PmcProjectPhase, SimplePhaseDto> implements CustomConverter {

  /**
   * 
   */
  public PhaseMapper() {
    super(PmcProjectPhase.class, SimplePhaseDto.class);
  }

  @Override
  public SimplePhaseDto convertTo(PmcProjectPhase source, SimplePhaseDto destination) {
    if (source == null)
      return null;
    return new SimplePhaseDto(
      source.getPmcProjectPhaseDefinition().getIdName(),
      source.getEnabled(),
      source.getActive(),
      source.getStartDate(),
      source.getEndDate(),
      source.getFrozen());
  }

  @Override
  public PmcProjectPhase convertFrom(SimplePhaseDto source, PmcProjectPhase destination) {
    return null;
  }

}
