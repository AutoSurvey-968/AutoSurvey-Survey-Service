package com.revature.autosurvey.surveys.utils;

import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;

import reactor.core.publisher.Flux;


public class FileReaderUtil {
	
	private FileReaderUtil() {/* Empty */}
	
	
	public static Flux<String> readFile(FilePart file){
		return file.content().map(buffer -> {
			byte[] bytes = new byte[buffer.readableByteCount()];
			buffer.read(bytes);
			DataBufferUtils.release(buffer);

			return new String(bytes);
		});
	}
}
