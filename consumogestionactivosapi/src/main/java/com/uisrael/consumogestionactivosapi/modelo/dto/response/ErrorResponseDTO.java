package com.uisrael.consumogestionactivosapi.modelo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponseDTO {
	private boolean error;
	private int status;
	private String message;
	private String path;
}
