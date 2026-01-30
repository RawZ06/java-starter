package fr.rawz06.starter.web.controller.pub;

import fr.rawz06.starter.api.controller.PublicApi;
import fr.rawz06.starter.api.dto.AppInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PublicController implements PublicApi {

    @Override
    public ResponseEntity<AppInfoDto> getPublicInfo() {
        AppInfoDto info = new AppInfoDto();
        info.setAppName("Java Starter Kit");
        info.setVersion("1.0.0");
        info.setDescription("Powered by Angular");
        return ResponseEntity.ok(info);
    }
}
