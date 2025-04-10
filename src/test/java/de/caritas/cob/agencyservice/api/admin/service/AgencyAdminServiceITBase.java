package de.caritas.cob.agencyservice.api.admin.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.caritas.cob.agencyservice.api.model.AgencyAdminFullResponseDTO;
import de.caritas.cob.agencyservice.api.model.AgencyDTO;
import de.caritas.cob.agencyservice.api.model.DemographicsDTO;
import de.caritas.cob.agencyservice.api.model.UpdateAgencyDTO;
import de.caritas.cob.agencyservice.api.repository.agency.Agency;
import de.caritas.cob.agencyservice.api.repository.agency.AgencyRepository;
import de.caritas.cob.agencyservice.api.util.AuthenticatedUser;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

public class AgencyAdminServiceITBase {

  @Autowired protected AgencyAdminService agencyAdminService;
  @Autowired protected AgencyRepository agencyRepository;
  @MockBean protected AuthenticatedUser authenticatedUser;

  public void saveAgency_Should_PersistsAgency() {

    AgencyDTO agencyDTO = createAgencyDTO();
    agencyDTO.setTenantId(1L);
    AgencyAdminFullResponseDTO agencyAdminFullResponseDTO =
        agencyAdminService.createAgency(agencyDTO);
    Optional<Agency> agencyOptional =
        agencyRepository.findById(agencyAdminFullResponseDTO.getEmbedded().getId());
    Agency agency = agencyOptional.get();
    assertThat(0, is(agency.getConsultingTypeId()));
    assertEquals("12345", agency.getPostCode());
    assertEquals("Agency description", agency.getDescription());
    assertEquals("Agency name", agency.getName());
    assertTrue(agency.isOffline());
  }

  public void saveAgency_Should_SetOfflineToTrue_WhenPersistsAgency() {

    AgencyDTO agencyDTO = createAgencyDTO();

    AgencyAdminFullResponseDTO agencyAdminFullResponseDTO =
        agencyAdminService.createAgency(agencyDTO);

    Optional<Agency> agencyOptional =
        agencyRepository.findById(agencyAdminFullResponseDTO.getEmbedded().getId());
    Agency agency = agencyOptional.get();
    assertTrue(agency.isOffline());
  }

  public void saveAgency_Should_ProvideValidAgencyLinks() {

    AgencyDTO agencyDTO = createAgencyDTO();

    AgencyAdminFullResponseDTO agencyAdminFullResponseDTO =
        agencyAdminService.createAgency(agencyDTO);
    assertThat(agencyAdminFullResponseDTO.getLinks().getDelete(), notNullValue());
    assertThat(
        agencyAdminFullResponseDTO.getLinks().getDelete().getHref(),
        endsWith(
            String.format(
                "/agencyadmin/agencies/%s", agencyAdminFullResponseDTO.getEmbedded().getId())));
    assertThat(agencyAdminFullResponseDTO.getLinks().getSelf(), notNullValue());
    assertThat(
        agencyAdminFullResponseDTO.getLinks().getSelf().getHref(),
        endsWith(
            String.format(
                "/agencyadmin/agencies/%s", agencyAdminFullResponseDTO.getEmbedded().getId())));
    assertThat(agencyAdminFullResponseDTO.getLinks().getUpdate(), notNullValue());
    assertThat(
        agencyAdminFullResponseDTO.getLinks().getUpdate().getHref(),
        endsWith(
            String.format(
                "/agencyadmin/agencies/%s", agencyAdminFullResponseDTO.getEmbedded().getId())));
    assertThat(agencyAdminFullResponseDTO.getLinks().getPostcodeRanges(), notNullValue());
    assertThat(
        agencyAdminFullResponseDTO.getLinks().getPostcodeRanges().getHref(),
        endsWith(
            String.format(
                "/agencyadmin/postcoderanges/%s",
                agencyAdminFullResponseDTO.getEmbedded().getId())));
  }

  protected AgencyDTO createAgencyDTO() {

    return new AgencyDTO("Agency name", 0, true)
        .postcode("12345")
        .description("Agency description")
        .url("https://www.domain.com")
        .demographics(new DemographicsDTO().ageFrom(15).ageTo(100).addGendersItem("MALE"));
  }

  public void updateAgency_Should_PersistsAgencyChanges() {

    UpdateAgencyDTO updateAgencyDTO = createUpdateAgencyDtoFromExistingAgency();
    AgencyAdminFullResponseDTO agencyAdminFullResponseDTO =
        agencyAdminService.updateAgency(0L, updateAgencyDTO);

    Optional<Agency> agencyOptional =
        agencyRepository.findById(agencyAdminFullResponseDTO.getEmbedded().getId());
    Agency agency = agencyOptional.orElseThrow(RuntimeException::new);
    assertEquals(updateAgencyDTO.getPostcode(), agency.getPostCode());
    assertEquals(updateAgencyDTO.getDescription(), agency.getDescription());
    assertEquals(updateAgencyDTO.getName(), agency.getName());
    assertEquals(updateAgencyDTO.getCity(), agency.getCity());
    assertEquals(updateAgencyDTO.getOffline(), agency.isOffline());
  }

  protected UpdateAgencyDTO createUpdateAgencyDtoFromExistingAgency() {

    Optional<Agency> agencyOptional = agencyRepository.findById(0L);
    Agency agency = agencyOptional.orElseThrow(RuntimeException::new);
    return new UpdateAgencyDTO(agency.getName() + "x", !agency.isOffline(), true)
        .description(agency.getDescription() + "x")
        .postcode("00000")
        .city(agency.getCity() + "x")
        .url("https://www.domain.com");
  }

  public void updateAgency_Should_ProvideValidAgencyLinks() {

    UpdateAgencyDTO updateAgencyDTO = createUpdateAgencyDtoFromExistingAgency();

    AgencyAdminFullResponseDTO agencyAdminFullResponseDTO =
        agencyAdminService.updateAgency(0L, updateAgencyDTO);
    assertThat(agencyAdminFullResponseDTO.getLinks().getDelete(), notNullValue());
    assertThat(
        agencyAdminFullResponseDTO.getLinks().getDelete().getHref(),
        endsWith(
            String.format(
                "/agencyadmin/agencies/%s", agencyAdminFullResponseDTO.getEmbedded().getId())));
    assertThat(agencyAdminFullResponseDTO.getLinks().getSelf(), notNullValue());
    assertThat(
        agencyAdminFullResponseDTO.getLinks().getSelf().getHref(),
        endsWith(
            String.format(
                "/agencyadmin/agencies/%s", agencyAdminFullResponseDTO.getEmbedded().getId())));
    assertThat(agencyAdminFullResponseDTO.getLinks().getUpdate(), notNullValue());
    assertThat(
        agencyAdminFullResponseDTO.getLinks().getUpdate().getHref(),
        endsWith(
            String.format(
                "/agencyadmin/agencies/%s", agencyAdminFullResponseDTO.getEmbedded().getId())));
    assertThat(agencyAdminFullResponseDTO.getLinks().getPostcodeRanges(), notNullValue());
    assertThat(
        agencyAdminFullResponseDTO.getLinks().getPostcodeRanges().getHref(),
        endsWith(
            String.format(
                "/agencyadmin/postcoderanges/%s",
                agencyAdminFullResponseDTO.getEmbedded().getId())));
  }

  public void getAgency_Should_returnExpectedAgency_When_agencyWithIdExists() {
    Long agencyId = 1L;

    AgencyAdminFullResponseDTO result = this.agencyAdminService.findAgency(agencyId);

    assertThat(result.getEmbedded(), notNullValue());
    assertThat(result.getEmbedded().getCity(), notNullValue());
    assertThat(result.getEmbedded().getId(), notNullValue());
    assertThat(result.getEmbedded().getDescription(), notNullValue());
    assertThat(result.getEmbedded().getConsultingType(), notNullValue());
    assertThat(result.getEmbedded().getName(), notNullValue());
  }
}
