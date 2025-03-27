package de.caritas.cob.agencyservice.api.admin.service;

import static de.caritas.cob.agencyservice.testHelper.TestConstants.AGENCY_ID;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.common.collect.Lists;
import de.caritas.cob.agencyservice.api.admin.service.agency.AgencyTopicEnrichmentService;
import de.caritas.cob.agencyservice.api.admin.service.agency.DataProtectionConverter;
import de.caritas.cob.agencyservice.api.admin.service.agency.DemographicsConverter;
import de.caritas.cob.agencyservice.api.admin.validation.DeleteAgencyValidator;
import de.caritas.cob.agencyservice.api.exception.httpresponses.NotFoundException;
import de.caritas.cob.agencyservice.api.model.AgencyAdminResponseDTO;
import de.caritas.cob.agencyservice.api.model.AgencyDTO;
import de.caritas.cob.agencyservice.api.model.DataProtectionContactDTO;
import de.caritas.cob.agencyservice.api.model.DataProtectionDTO;
import de.caritas.cob.agencyservice.api.model.DemographicsDTO;
import de.caritas.cob.agencyservice.api.model.UpdateAgencyDTO;
import de.caritas.cob.agencyservice.api.repository.agency.Agency;
import de.caritas.cob.agencyservice.api.repository.agency.AgencyTenantUnawareRepository;
import de.caritas.cob.agencyservice.api.repository.agency.DataProtectionResponsibleEntity;
import de.caritas.cob.agencyservice.api.service.AppointmentService;
import de.caritas.cob.agencyservice.api.util.AuthenticatedUser;
import de.caritas.cob.agencyservice.api.util.JsonConverter;
import java.util.List;
import java.util.Optional;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class AgencyAdminServiceTest {

  @InjectMocks AgencyAdminService agencyAdminService;

  @Mock AgencyTenantUnawareRepository agencyRepository;

  @Mock UserAdminService userAdminService;

  @Mock DeleteAgencyValidator deleteAgencyValidator;

  @Mock AgencyTopicMergeService mergeService;

  @Mock AgencyTopicEnrichmentService agencyTopicEnrichmentService;

  @Mock DemographicsConverter demographicsConverter;

  @Mock DataProtectionConverter dataProtectionConverter;

  @Mock AppointmentService appointmentService;

  @Mock private Logger logger;

  @Mock AuthenticatedUser authenticatedUser;

  @Captor private ArgumentCaptor<Agency> agencyArgumentCaptor;

  private EasyRandom easyRandom;

  @BeforeEach
  public void setup() {
    ReflectionTestUtils.setField(
        agencyAdminService, "agencyTopicEnrichmentService", agencyTopicEnrichmentService);
    ReflectionTestUtils.setField(
        agencyAdminService, "demographicsConverter", demographicsConverter);

    this.easyRandom = new EasyRandom();
  }

  @Test
  void updateAgency_Should_ThrowNotFoundException_WhenAgencyIsNotFound() {
    when(agencyRepository.findById(AGENCY_ID)).thenReturn(Optional.empty());

    var updateAgencyDTO = this.easyRandom.nextObject(UpdateAgencyDTO.class);

    assertThrows(
        NotFoundException.class, () -> agencyAdminService.updateAgency(AGENCY_ID, updateAgencyDTO));
  }

  @Test
  void createAgency_Should_CreateAgencyAndAddDefaultCounsellingRelations() {
    // given
    var agency = this.easyRandom.nextObject(Agency.class);
    agency.setCounsellingRelations(null);
    agency.setDataProtectionOfficerContactData(null);
    clearDataProtection(agency);
    var agencyDTO = this.easyRandom.nextObject(AgencyDTO.class);
    agencyDTO.setCounsellingRelations(null);
    agencyDTO.setConsultingType(1);
    agencyDTO.setDataProtection(new DataProtectionDTO());

    when(agencyRepository.save(any())).thenReturn(agency);
    // when
    agencyAdminService.createAgency(agencyDTO);
    // then
    verify(agencyRepository).save(agencyArgumentCaptor.capture());
    assertThat(
        agencyArgumentCaptor.getValue().getCounsellingRelations(),
        is("RELATIVE_COUNSELLING,SELF_COUNSELLING,PARENTAL_COUNSELLING"));
    verify(dataProtectionConverter)
        .convertToEntity(
            Mockito.any(DataProtectionDTO.class), Mockito.any(Agency.AgencyBuilder.class));
  }

  @Test
  void updateAgency_Should_SaveAgencyMandatoryChanges_WhenAgencyIsFound() {
    var agency = this.easyRandom.nextObject(Agency.class);
    clearDataProtection(agency);
    DataProtectionContactDTO dataProtectionContactDTO =
        this.easyRandom.nextObject(DataProtectionContactDTO.class);
    agency.setDataProtectionOfficerContactData(
        JsonConverter.convertToJson(dataProtectionContactDTO));
    agency.setDataProtectionAlternativeContactData(null);
    agency.setDataProtectionResponsibleEntity(
        DataProtectionResponsibleEntity.DATA_PROTECTION_OFFICER);

    agency.setCounsellingRelations(null);
    when(agencyRepository.findById(AGENCY_ID)).thenReturn(Optional.of(agency));
    when(agencyRepository.save(any())).thenReturn(agency);

    var updateAgencyDTO = this.easyRandom.nextObject(UpdateAgencyDTO.class);
    updateAgencyDTO.setConsultingType(null);
    agencyAdminService.updateAgency(AGENCY_ID, updateAgencyDTO);

    verify(agencyRepository).save(agencyArgumentCaptor.capture());
    var passedConsultingTypeId = agencyArgumentCaptor.getValue().getConsultingTypeId();
    assertEquals(agency.getConsultingTypeId(), passedConsultingTypeId);
  }

  private void clearDataProtection(Agency agency) {
    agency.setDataProtectionResponsibleEntity(null);
    agency.setDataProtectionAgencyResponsibleContactData(null);
    agency.setDataProtectionAlternativeContactData(null);
    agency.setDataProtectionOfficerContactData(null);
  }

  @Test
  void updateAgency_Should_SaveOptionalAgencyChanges_WhenAgencyIsFound() {
    var agency = easyRandom.nextObject(Agency.class);
    agency.setCounsellingRelations(
        AgencyAdminResponseDTO.CounsellingRelationsEnum.PARENTAL_COUNSELLING.getValue());
    agency.setDataProtectionResponsibleEntity(
        DataProtectionResponsibleEntity.ALTERNATIVE_REPRESENTATIVE);
    agency.setDataProtectionAlternativeContactData(
        JsonConverter.convertToJson(new DataProtectionContactDTO()));
    agency.setDataProtectionOfficerContactData(null);
    agency.setDataProtectionAgencyResponsibleContactData(null);
    when(agencyRepository.findById(AGENCY_ID)).thenReturn(Optional.of(agency));
    when(agencyRepository.save(any())).thenReturn(agency);

    var updateAgencyDTO = easyRandom.nextObject(UpdateAgencyDTO.class);
    updateAgencyDTO.setCounsellingRelations(
        Lists.newArrayList(UpdateAgencyDTO.CounsellingRelationsEnum.PARENTAL_COUNSELLING));

    agencyAdminService.updateAgency(AGENCY_ID, updateAgencyDTO);

    verify(agencyRepository).save(agencyArgumentCaptor.capture());
    var passedConsultingTypeId = agencyArgumentCaptor.getValue().getConsultingTypeId();
    assertEquals(updateAgencyDTO.getConsultingType(), passedConsultingTypeId);
    assertEquals("PARENTAL_COUNSELLING", agencyArgumentCaptor.getValue().getCounsellingRelations());
  }

  @Test
  void updateAgency_Should_SaveAgencyChanges_WhenAgencyIsFoundAndTopicFeatureEnabled() {
    // given
    ReflectionTestUtils.setField(agencyAdminService, "featureTopicsEnabled", true);
    var agency = this.easyRandom.nextObject(Agency.class);
    clearDataProtection(agency);
    agency.setCounsellingRelations(null);
    when(agencyRepository.findById(AGENCY_ID)).thenReturn(Optional.of(agency));
    when(agencyRepository.save(any())).thenReturn(agency);
    var updateAgencyDTO = this.easyRandom.nextObject(UpdateAgencyDTO.class);

    // when
    agencyAdminService.updateAgency(AGENCY_ID, updateAgencyDTO);

    // then
    verify(this.agencyRepository).save(any());
    verify(this.mergeService).getMergedTopics(Mockito.any(Agency.class), any(List.class));
    verify(this.agencyTopicEnrichmentService).enrichAgencyWithTopics(agency);
    ReflectionTestUtils.setField(agencyAdminService, "featureTopicsEnabled", false);
  }

  @Test
  void updateAgency_Should_SaveAgencyChanges_WhenAgencyIsFoundAndDemographicsFeatureIsEnabled() {
    // given
    ReflectionTestUtils.setField(agencyAdminService, "featureDemographicsEnabled", true);
    var agency = this.easyRandom.nextObject(Agency.class);
    clearDataProtection(agency);
    agency.setDataProtectionAgencyResponsibleContactData(null);
    agency.setDataProtectionResponsibleEntity(DataProtectionResponsibleEntity.AGENCY_RESPONSIBLE);
    agency.setCounsellingRelations(
        AgencyAdminResponseDTO.CounsellingRelationsEnum.PARENTAL_COUNSELLING.getValue());
    when(agencyRepository.findById(AGENCY_ID)).thenReturn(Optional.of(agency));
    when(agencyRepository.save(any())).thenReturn(agency);
    var updateAgencyDTO = this.easyRandom.nextObject(UpdateAgencyDTO.class);

    // when
    agencyAdminService.updateAgency(AGENCY_ID, updateAgencyDTO);

    // then
    verify(this.agencyRepository).save(any());
    verify(this.demographicsConverter)
        .convertToEntity(
            Mockito.any(DemographicsDTO.class), Mockito.any(Agency.AgencyBuilder.class));
    ReflectionTestUtils.setField(agencyAdminService, "featureDemographicsEnabled", false);
  }

  @Test
  void findAgencyById_Should_ThrowNotFoundException_WhenAgencyIsNotFound() {
    when(agencyRepository.findById(AGENCY_ID)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> agencyAdminService.findAgencyById(AGENCY_ID));
  }

  @Test
  void deleteAgency_Should_ThrowNotFoundException_WhenAgencyIsNotFound() {
    when(agencyRepository.findById(AGENCY_ID)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> agencyAdminService.deleteAgency(AGENCY_ID));
  }

  @Test
  void deleteAgency_Should_callDeleteAgencyValidatorAndSaveChangedAgency_When_AgencyIsFound() {
    var agency = this.easyRandom.nextObject(Agency.class);
    when(agencyRepository.findById(AGENCY_ID)).thenReturn(Optional.of(agency));

    agencyAdminService.deleteAgency(AGENCY_ID);

    verify(this.deleteAgencyValidator).validate(agency);
    verify(this.agencyRepository).save(any());
  }
}
