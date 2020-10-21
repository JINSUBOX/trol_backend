package gg.trol.controller.v1;

import gg.trol.advice.exception.CUserNotFoundException;
import gg.trol.entity.user.Role;
import gg.trol.entity.user.User;
import gg.trol.entity.user.UserRepository;
import gg.trol.model.response.SingleResult;
import gg.trol.service.ResponseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = {"1. User"})
@RequiredArgsConstructor
@RestController // 결과값을 JSON으로 출력합니다.
@RequestMapping(value = "/v1")
public class UserController {
    private final UserRepository userRepository;
    private final ResponseService responseService;

    @ApiOperation(value = "전체 회원 조회", notes = "모든 회원을 조회한다")
    @GetMapping(value = "/user")
    public List<User> findAllUser() {
        return userRepository.findAll();
    }

    @ApiOperation(value = "회원 가입", notes = "회원가입을 한다.")
    @PostMapping(value = "/signup")
    public User save(@ApiParam(value = "회원아이디", required = true) @RequestParam String email,
                     @ApiParam(value = "회원이름", required = true) @RequestParam String nickName,
                     @ApiParam(value = "권한", required = true) @RequestParam Role role) {
        User user = User.builder()
                .email(email)
                .nickName(nickName)
                .role(role)
                .build();
        return userRepository.save(user);
    }

    @ApiOperation(value = "회원 단건 조회", notes = "userId로 회원을 조회한다")
    @GetMapping(value = "/user/{msrl}")
    public SingleResult<User> findUserById(@ApiParam(value = "회원ID", required = true) @PathVariable long msrl, @ApiParam(value = "언어", defaultValue = "ko") @RequestParam String lang) {
        // 결과데이터가 단일건인경우 getBasicResult를 이용해서 결과를 출력한다.
        return responseService.getSingleResult(userRepository.findById(msrl).orElseThrow(CUserNotFoundException::new));
    }
}