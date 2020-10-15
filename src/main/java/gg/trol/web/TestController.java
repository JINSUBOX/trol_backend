package gg.trol.web;

import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "테스트 컨트롤러")
@RestController
public class TestController {

    @GetMapping("/api/test")
    public String index(){
        return "test success";
    }
}
