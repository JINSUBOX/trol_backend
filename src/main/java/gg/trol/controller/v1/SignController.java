package gg.trol.controller.v1;

import gg.trol.advice.exception.CEmailSigninFailedException;
import gg.trol.advice.exception.CUserExistException;
import gg.trol.advice.exception.CUserNotFoundException;
import gg.trol.config.security.JwtTokenProvider;
import gg.trol.entity.user.User;
import gg.trol.entity.user.UserRepository;
import gg.trol.model.response.CommonResult;
import gg.trol.model.response.SingleResult;
import gg.trol.model.social.KakaoProfile;
import gg.trol.service.ResponseService;
import gg.trol.service.user.KakaoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Optional;

@Api(tags = {"1. Sign"})
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/v1")
public class SignController {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final ResponseService responseService;
    private final KakaoService kakaoService;

    /*    @ApiOperation(value = "로그인", notes = "이메일 회원 로그인을 한다.")
        @PostMapping(value = "/signin")
        public SingleResult<String> signin(@ApiParam(value = "회원ID : 이메일", required = true) @RequestParam String id,
                                           @ApiParam(value = "비밀번호", required = true) @RequestParam String password) {
            User user = userRepository.findByUid(id).orElseThrow(CEmailSigninFailedException::new);
            if (!passwordEncoder.matches(password, user.getPassword()))
                throw new CEmailSigninFailedException();

            return responseService.getSingleResult(jwtTokenProvider.createToken(String.valueOf(user.getMsrl()), user.getRoles()));

        }

        @ApiOperation(value = "가입", notes = "회원가입을 한다.")
        @PostMapping(value = "/signup")
        public CommonResult signin(@ApiParam(value = "회원ID : 이메일", required = true) @RequestParam String id,
                                   @ApiParam(value = "비밀번호", required = true) @RequestParam String password,
                                   @ApiParam(value = "이름", required = true) @RequestParam String name) {

            userJpaRepo.save(User.builder()
                    .uid(id)
                    .password(passwordEncoder.encode(password))
                    .name(name)
                    .roles(Collections.singletonList("ROLE_USER"))
                    .build());
            return responseService.getSuccessResult();
        }
        */
    @ApiOperation(value = "소셜 로그인", notes = "소셜 회원 로그인을 한다.")
    @PostMapping(value = "/signin/{provider}")
    public SingleResult<String> signinByProvider(
            @ApiParam(value = "서비스 제공자 provider", required = true, defaultValue = "kakao") @PathVariable String provider,
            @ApiParam(value = "소셜 access_token", required = true) @RequestParam String accessToken) {

        KakaoProfile profile = kakaoService.getKakaoProfile(accessToken);
        User user = userRepository.findByUidAndProvider(String.valueOf(profile.getId()), provider).orElseThrow(CUserNotFoundException::new);
        return responseService.getSingleResult(jwtTokenProvider.createToken(String.valueOf(user.getMsrl()), user.getRoles()));
    }

    @ApiOperation(value = "소셜 계정 가입", notes = "소셜 계정 회원가입을 한다.")
    @PostMapping(value = "/signup/{provider}")
    public CommonResult signupProvider(@ApiParam(value = "서비스 제공자 provider", required = true, defaultValue = "kakao") @PathVariable String provider,
                                       @ApiParam(value = "소셜 access_token", required = true) @RequestParam String accessToken,
                                       @ApiParam(value = "이름", required = true) @RequestParam String name) {

        KakaoProfile profile = kakaoService.getKakaoProfile(accessToken);
        Optional<User> user = userRepository.findByUidAndProvider(String.valueOf(profile.getId()), provider);
        if(user.isPresent())
            throw new CUserExistException();

        userRepository.save(User.builder()
                .uid(String.valueOf(profile.getId()))
                .provider(provider)
                .name(name)
                .roles(Collections.singletonList("ROLE_USER"))
                .build());
        return responseService.getSuccessResult();
    }
}