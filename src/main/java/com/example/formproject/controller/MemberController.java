package com.example.formproject.controller;

import com.example.formproject.FinalValue;
import com.example.formproject.dto.request.LoginDto;
import com.example.formproject.dto.request.MailDto;
import com.example.formproject.dto.request.MemberInfoRequestDto;
import com.example.formproject.dto.request.MemberRequestDto;
import com.example.formproject.dto.response.AccountResponseDto;
import com.example.formproject.dto.response.JwtResponseDto;
import com.example.formproject.dto.response.MemberResponseDto;
import com.example.formproject.entity.Member;
import com.example.formproject.exception.AuthenticationException;
import com.example.formproject.exception.EmailConfirmException;
import com.example.formproject.repository.MemberRepository;
import com.example.formproject.security.MemberDetail;
import com.example.formproject.service.EmailService;
import com.example.formproject.service.MemberService;
import com.example.formproject.service.OAuthService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.bytebuddy.utility.RandomString;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@RestController
@RequiredArgsConstructor
@Tag(name = "Member Api", description = "?????? ?????? ?????? API(?????????/?????????)")
public class MemberController {
    private final MemberService memberService;

    private final OAuthService oAuthService;

    private final ObjectMapper mapper;

    // ?????????
    @PostMapping("/member/login")
    @Operation(summary = "????????? (?????????)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_OK, description = "?????? ??????",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class, example = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJpZCIsImlkIjo2NywiZXhwIjoxNjU3MDk4Mjg5LCJpYXQiOjE2NTcwODc0ODl9._J-jgRNaqMS2_X9aZV0Cj9SgKK_R-VJzzxlexVcj_Gs"))}),
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_BADREQUEST, description = "????????? ??????", content = @Content),
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_SERVERERROR, description = "?????? ??????", content = @Content)})
    public String loginMember(@RequestBody LoginDto dto, HttpServletResponse response) throws AuthenticationException, EmailConfirmException {
        JwtResponseDto token = memberService.login(dto, response);
        return "Bearer " + token.getToken();
    }

    @GetMapping("/member/email")
    @Operation(summary = "????????? ???????????? (?????????)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_OK, description = "?????? ?????? (payload: {key : abcdefg...})",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class, example = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJpZCIsImlkIjo2NywiZXhwIjoxNjU3MDk4Mjg5LCJpYXQiOjE2NTcwODc0ODl9._J-jgRNaqMS2_X9aZV0Cj9SgKK_R-VJzzxlexVcj_Gs")
                    )}
            ),
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_SERVERERROR, description = "?????? ??????", content = @Content)
    })
    public void emailToken(@RequestParam("id") Integer id, HttpServletResponse response) throws Exception {
        memberService.enableMember(id);
        response.sendRedirect(FinalValue.FRONT_URL+"/login");
    }

    @PostMapping("/member/auth")
    @Operation(summary = "Oauth Code??? ???????????? ?????????/???????????? (?????????)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_OK, description = "?????? ??????",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class, example = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJpZCIsImlkIjo2NywiZXhwIjoxNjU3MDk4Mjg5LCJpYXQiOjE2NTcwODc0ODl9._J-jgRNaqMS2_X9aZV0Cj9SgKK_R-VJzzxlexVcj_Gs")
                    )}
            ),
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_SERVERERROR, description = "?????? ??????", content = @Content)
    })
    public String accessTokenToMember(@RequestBody String t, HttpServletResponse response) {
        JwtResponseDto dto = oAuthService.kakaoLogin(t, response);

        return dto.getToken();
    }

    @PostMapping("/member")
    @Operation(summary = "???????????? (?????????)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_OK, description = "?????? ??????"),
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_BADREQUEST, description = "?????? ????????? ??????", content = @Content),
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_SERVERERROR, description = "?????? ??????", content = @Content)})
    public void joinMember(@RequestBody MemberRequestDto dto) throws IOException, MessagingException {
        memberService.save(dto);
    }

    @GetMapping("/member")
    @Operation(summary = "????????? ?????? ?????? (?????????)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_OK, description = "?????? ??????",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = MemberResponseDto.class))}),
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_FORBIDDEN, description = "????????? ??????", content = @Content),
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_SERVERERROR, description = "?????? ??????", content = @Content)})
    public MemberResponseDto getMember(@AuthenticationPrincipal MemberDetail memberDetail) {
        return memberService.makeMemberResponseDto(memberDetail.getMember());
    }

    @PutMapping(value = "/member")
    @Operation(summary = "???????????? ?????? (?????????)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_OK, description = "?????? ??????",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class, example = "??????????????? ?????????????????????."))}),
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_FORBIDDEN, description = "????????? ??????", content = @Content),
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_SERVERERROR, description = "?????? ??????", content = @Content)})
    @Parameter(in = ParameterIn.PATH, name = "memberid", description = "????????? id(database id)", example = "1", required = true)
    public ResponseEntity<?> updateMember(@RequestPart(value = "profileImage", required = false) MultipartFile profileImage,
                                          @RequestPart String data,
                                          @AuthenticationPrincipal MemberDetail memberDetails) throws JsonProcessingException {
        MemberInfoRequestDto requestDto = mapper.readValue(data, MemberInfoRequestDto.class);
        return memberService.updateMember(memberDetails.getMember().getId(), profileImage, requestDto, memberDetails.getUsername());
    }

    @ExceptionHandler(EmailConfirmException.class)
    public ResponseEntity handlingAuthExp(EmailConfirmException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
    }
}