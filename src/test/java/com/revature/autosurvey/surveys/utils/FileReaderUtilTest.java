package com.revature.autosurvey.surveys.utils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.nio.ByteBuffer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
class FileReaderUtilTest {
	
	@Test
	void testReadFile() {
		FilePart filePart = Mockito.mock(FilePart.class);
		DataBuffer dataBuffer = Mockito.mock(DataBuffer.class);
		String string = "choices,choices,choices";
		byte[] byteArray = string.getBytes();
		ByteBuffer byteBuffer = ByteBuffer.allocate(byteArray.length);
		byteBuffer.put(byteArray);
		byteBuffer.position(0);
		Flux<DataBuffer> fluxDataBuffer = Flux.just(dataBuffer);
		when(filePart.content()).thenReturn(fluxDataBuffer);
		when(dataBuffer.readableByteCount()).thenReturn(byteArray.length);
		when(dataBuffer.read(any(byte[].class))).thenAnswer(invocation -> {
			Object[] args = invocation.getArguments();
			byteBuffer.get((byte[])args[0]);
			return null;
		});
		StepVerifier.create(FileReaderUtil.readFile(filePart))
		.expectNext(string)
		.verifyComplete();
	}
}
