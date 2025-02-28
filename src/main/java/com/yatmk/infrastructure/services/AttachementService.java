package com.yatmk.infrastructure.services;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import org.apache.commons.io.IOUtils;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import com.yatmk.common.utility.FileUtility;
import com.yatmk.infrastructure.config.UploadFolder;
import com.yatmk.persistence.exception.config.ServerSideException;
import com.yatmk.persistence.exception.file.FileNotFoundException;
import com.yatmk.persistence.exception.file.FileUnStreamableException;
import com.yatmk.persistence.models.Attachement;
import com.yatmk.persistence.models.Range;
import com.yatmk.persistence.models.enums.FileExtension;
import com.yatmk.persistence.presentation.ApiDownloadInput;
import com.yatmk.persistence.presentation.ApiDownloadInputLarge;
import com.yatmk.persistence.presentation.ApiPartialInput;
import com.yatmk.persistence.repositories.AttachementRepository;

import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Service
@RequiredArgsConstructor
public class AttachementService {

  private final UploadFolder uploadFolder;

  private final AttachementRepository attachementRepository;

  public Path uploadFile(@Valid @NotNull MultipartFile file, String ext) {

    String realEXT = Optional.ofNullable(ext).map(String::trim).filter(e -> !e.isEmpty()).map("."::concat)
        .orElseGet(String::new);
    Path filePath = Paths.get(uploadFolder.getUploadFolderPath(),
        UUID.randomUUID().toString() + realEXT).toAbsolutePath().normalize();
    Try.run(() -> file.transferTo(filePath.toFile())).onFailure(ServerSideException::reThrow);
    return filePath;

  }

  public <T extends Attachement> T uploadAttachement(@Valid @NotNull MultipartFile fileToUpload,
      @Valid @NotNull T attachment) {

    attachment.setExt(FileUtility.getFileExtension(fileToUpload.getOriginalFilename()));
    attachment.setExtension(FileExtension.getByValue(attachment.getExt()));
    attachment.setFileType(attachment.getExtension().getType());
    attachment.setOriginalFileName(FileUtility.getFileNameWithoutExtension(fileToUpload.getOriginalFilename()));
    attachment.setPath(uploadFile(fileToUpload, attachment.getExt()).toString());
    attachment.setFileSize(fileToUpload.getSize());
    return attachment;

  }

  public String getOriginalFileNameFromFile(@Valid @NotNull Attachement attachement) {
    return Optional.ofNullable(attachement)
        .map(f -> f.getOriginalFileName() + "." + f.getExt())
        .orElseGet(String::new);

  }

  public Optional<Attachement> getById(@Valid @NotNull String attachmentId) {
    return attachementRepository.findById(attachmentId);
  }

  public ApiDownloadInput downloadFile(@Valid @NotNull String attachmentId) {
    log.info("download attachment with id : {}", attachmentId);
    return Optional.of(attachmentId)
        .map(attachementRepository::findById)
        .flatMap(Function.identity())
        .map(this::downloadAttachement)
        .orElseThrow(FileNotFoundException::new);

  }

  public ApiDownloadInputLarge downloadAttachementLarge(@Valid @NotNull String attachmentId) {
    log.info("download attachment with id : {}", attachmentId);
    return Optional.of(attachmentId)
        .map(attachementRepository::findById)
        .flatMap(Function.identity())
        .map(this::downloadAttachementLarge)
        .orElseThrow(FileNotFoundException::new);

  }

  public ApiDownloadInput downloadAttachement(@Valid @NotNull Attachement attachment) {

    log.info("download file with id : {}", attachment.getId());
    return ApiDownloadInput.builder()
        .bytes(Try.of(() -> getFileBytes(attachment)).onFailure(ServerSideException::reThrow).get())
        .fileName(attachment.getOriginalFileName())
        .ext(attachment.getExt())
        .build();

  }

  public ApiDownloadInputLarge downloadAttachementLarge(@Valid @NotNull Attachement attachment) {

    log.info("download Large file with id : {}", attachment.getId());
    return ApiDownloadInputLarge.builder()
        .streamingResponseBody(getStreamingResponseBody(attachment))
        .fileName(attachment.getOriginalFileName())
        .ext(attachment.getExt()).size(attachment.getFileSize()).build();

  }

  public StreamingResponseBody getStreamingResponseBody(@Valid @NotNull Attachement attachment) {
    return Try.of(() -> attachment)
        .map(Attachement::getPath)
        .map(File::new)
        .mapTry(FileInputStream::new)
        .map(StreamingResponseBodyImpl::new)
        .get();
  }

  public void deleteAttachement(@Valid @NotNull String attachmentId) {
    log.info("delete file with id : {}", attachmentId);
    Optional.of(attachmentId)
        .map(attachementRepository::findById)
        .filter(e -> e.isPresent())
        .map(e -> e.get())
        .ifPresent(this::deleteAttachement);

  }

  public void deleteAttachement(@Valid @NotNull Attachement attachment) {

    Optional.of(attachment)
        .ifPresent(e -> {
          FileUtility.delete(attachment.getPath());
          attachementRepository.delete(e);
        });

  }

  public byte[] getFileBytes(@Valid @NotNull Attachement file) throws IOException {

    log.info("Getting stream of file with id {}", file.getId());
    InputStream inputStream = new FileInputStream(new File(file.getPath()));
    byte[] bytes = IOUtils.toByteArray(inputStream);
    IOUtils.closeQuietly(inputStream);
    return bytes;

  }

  public ApiPartialInput streamFile(@Valid @NotNull String fileId, String httpRangeList) {

    return attachementRepository.findById(fileId)
        .map(e -> streamFile(e, httpRangeList))
        .orElseThrow(FileNotFoundException::new);

  }

  public ApiPartialInput streamFile(@Valid @NotNull Attachement attachement, String httpRangeList) {
    if (!attachement.getFileType().isStreamable()) {
      throw new FileUnStreamableException();
    }
    Range ru = Range.getRange(httpRangeList, attachement.getFileSize(),
        attachement.getFileType().isImage());

    return ApiPartialInput
        .builder()
        .bytes(readByteRange(readPartialFileInputStream(attachement.getPath(), ru.getStart(), ru.len()), ru.len()))
        .start(ru.getStart())
        .end(ru.getEnd())
        .lenght(ru.len())
        .size(attachement.getFileSize())
        .content(attachement.getFileType().getValue())
        .ext(attachement.getExt())
        .build();

  }

  public InputStream readPartialFileInputStream(String filePath, long startRange, long rangeLength) {

    try (RandomAccessFile randomAccessFile = new RandomAccessFile(filePath, "r");
        FileChannel fileChannel = randomAccessFile.getChannel()) {

      fileChannel.position(startRange);
      ByteBuffer buffer = ByteBuffer.allocate((int) rangeLength);
      int bytesRead = fileChannel.read(buffer);
      if (bytesRead == -1) {
        throw new IOException("End of file reached before reading the specified range.");
      }
      byte[] data = new byte[bytesRead];
      buffer.flip();
      buffer.get(data);
      return new ByteArrayInputStream(data);
    } catch (Exception e) {
      throw new ServerSideException(e.getMessage(), e);

    }

  }

  public byte[] readByteRange(InputStream inputStream, long len) {
    return Try.of(() -> readByteRangeImpl(inputStream, len)).onFailure(ServerSideException::reThrow).get();

  }

  public byte[] readByteRangeImpl(InputStream inputStream, long len) throws IOException {

    ByteArrayOutputStream bufferedOutputStream = new ByteArrayOutputStream();
    byte[] data = new byte[Range.BYTE_RANGE];
    int nRead;
    while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
      bufferedOutputStream.write(data, 0, nRead);
    }
    bufferedOutputStream.flush();

    byte[] result = new byte[(int) len];
    System.arraycopy(bufferedOutputStream.toByteArray(), 0, result, 0, result.length);
    return result;

  }
}
