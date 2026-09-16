package com.example.demo.service;


import com.example.demo.file.BucketComponent;
import com.example.demo.model.Submission;
import com.example.demo.repository.SubmissionRepository;
import jakarta.transaction.Transactional;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import javax.imageio.ImageIO;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.crt.s3.S3Client;

@Service
@RequiredArgsConstructor
public class SubmissionService {

    private final SubmissionRepository repository;
    private final BucketComponent bucketComponent;

    @Transactional
    public Submission createSubmission(MultipartFile file, String email) {
        String id = UUID.randomUUID().toString();

        Submission submission = Submission.builder()
                .id(id)
                .email(email)
                .thumbnailKey(null)
                .createdAt(Instant.now())
                .build();

        Submission saved = repository.save(submission);

        processImageAsync(saved.getId(), file);

        return saved;
    }

    @Async
    public void processImageAsync(String submissionId, MultipartFile file) {
        try {
            BufferedImage originalImage = ImageIO.read(file.getInputStream());
            BufferedImage resizedImage = new BufferedImage(256, 256, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = resizedImage.createGraphics();
            g.drawImage(originalImage, 0, 0, 256, 256, null);
            g.dispose();

            File tempFile = File.createTempFile("thumbnail_", ".jpg");
            ImageIO.write(resizedImage, "jpg", tempFile);

            String thumbnailKey = "thumbnails/" + submissionId + ".jpg";
            bucketComponent.upload(tempFile, thumbnailKey);

            Submission submission = repository.findById(submissionId).orElseThrow();
            submission.setThumbnailKey(thumbnailKey);
            repository.save(submission);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Submission> getAllSubmissions() {
        return repository.findAll();
    }
}