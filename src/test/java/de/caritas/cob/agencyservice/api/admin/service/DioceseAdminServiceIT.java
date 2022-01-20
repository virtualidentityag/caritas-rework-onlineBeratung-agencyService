package de.caritas.cob.agencyservice.api.admin.service;

import de.caritas.cob.agencyservice.AgencyServiceApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AgencyServiceApplication.class)
@TestPropertySource(properties = "spring.profiles.active=testing")
@AutoConfigureTestDatabase(replace = Replace.ANY)
public class DioceseAdminServiceIT extends DioceseAdminServiceITBase {

  @Test
  public void findAllDioceses_Should_returnOneResult_When_perPageIsSetToOne() {
    super.findAllDioceses_Should_returnOneResult_When_perPageIsSetToOne();
  }

  @Test
  public void findAllDioceses_Should_returnOneResult_When_perPageIsSetToOneAndPageIsSetToOne() {
    super.findAllDioceses_Should_returnOneResult_When_perPageIsSetToOneAndPageIsSetToOne();
  }

  @Test
  public void findAllDioceses_Should_returnOneResult_When_paginationParamsAreZero() {
    super.findAllDioceses_Should_returnOneResult_When_paginationParamsAreZero();
  }

  @Test
  public void findAllDioceses_Should_returnOneResult_When_paginationParamsAreNegative() {
    super.findAllDioceses_Should_returnOneResult_When_paginationParamsAreNegative();
  }

  @Test
  public void findAllDioceses_Should_returnPaginatedEntities_When_paginationParamsAreSplitted() {
    super.findAllDioceses_Should_returnPaginatedEntities_When_paginationParamsAreSplitted();
  }

  @Test
  public void buildAgencyAdminSearchResult_Should_haveExpectedLinks_When_AllParamsAreProvided() {
    super.buildAgencyAdminSearchResult_Should_haveExpectedLinks_When_AllParamsAreProvided();
  }
}
