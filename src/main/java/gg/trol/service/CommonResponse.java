package gg.trol.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CommonResponse {

    SUCCESS(0, "성공했습니다"),
    FAIL(-1, "실패했습니다");

    private final int code;
    private final String msg;
}
