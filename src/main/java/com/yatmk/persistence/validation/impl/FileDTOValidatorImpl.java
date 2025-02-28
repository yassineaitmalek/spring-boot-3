package com.yatmk.persistence.validation.impl;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import com.yatmk.persistence.dto.FileDTO;
import com.yatmk.persistence.validation.FileDTOValidator;

public class FileDTOValidatorImpl implements ConstraintValidator<FileDTOValidator, FileDTO> {

	@Override
	public boolean isValid(FileDTO fileDTO, ConstraintValidatorContext context) {

		return Stream.of(checkNull(fileDTO), checkFile(fileDTO))
				.noneMatch(Boolean.TRUE::equals);

	}

	private boolean checkNull(FileDTO fileDTO) {
		return Objects.isNull(fileDTO);
	}

	private boolean checkFile(FileDTO fileDTO) {
		return Optional.ofNullable(fileDTO)
				.map(e -> Objects.isNull(e.getFile()))
				.orElseGet(() -> Boolean.TRUE);
	}

}