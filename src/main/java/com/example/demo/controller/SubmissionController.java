package com.example.demo.controller;

import com.example.demo.model.Submission;
import com.example.demo.service.SubmissionService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class SubmissionController {

    private final SubmissionService submissionService;

    @PostMapping("/submissions")
    @ResponseStatus(HttpStatus.CREATED)
    public Submission createSubmission(
            @RequestParam("file") MultipartFile file,
            @RequestParam("email") String email) {
        return submissionService.createSubmission(file, email);
    }

    @GetMapping("/submissions")
    public List<Submission> getSubmissions() {
        return submissionService.getAllSubmissions();
    }
}