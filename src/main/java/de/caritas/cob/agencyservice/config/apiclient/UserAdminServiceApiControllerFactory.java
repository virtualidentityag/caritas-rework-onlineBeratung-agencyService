package de.caritas.cob.agencyservice.config.apiclient;

import de.caritas.cob.agencyservice.useradminservice.generated.web.AdminUserControllerApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class UserAdminServiceApiControllerFactory {

  @Value("${user.admin.service.api.url}")
  private String userAdminServiceApiUrl;

  @Autowired private RestTemplate restTemplate;

  public AdminUserControllerApi createControllerApi() {
    var apiClient = new UserAdminApiClient(restTemplate).setBasePath(this.userAdminServiceApiUrl);
    return new AdminUserControllerApi(apiClient);
  }
}
