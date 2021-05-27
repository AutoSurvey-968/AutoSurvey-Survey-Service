package com.revature.autosurvey.surveys.security;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeUnit;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.revature.autosurvey.surveys.beans.TokenVerifierRequest;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

@Component
public class FirebaseUtil {
	
	private Logger log = LoggerFactory.getLogger(FirebaseUtil.class);
	
	@Value("${google.firebase.apikey}")
	private String firebaseKey;
	
	
	public Mono<FirebaseToken> getDetailsFromCustomToken(String token) {
		TokenVerifierRequest request = new TokenVerifierRequest();
		request.setToken(token);
		request.setReturnSecureToken(true);
		HttpClient httpClient = HttpClient.create().option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
				.responseTimeout(Duration.ofMillis(5000))
				.doOnConnected(conn -> conn.addHandlerLast(new ReadTimeoutHandler(5000, TimeUnit.MILLISECONDS))
						.addHandlerLast(new WriteTimeoutHandler(5000, TimeUnit.MILLISECONDS)));
		WebClient client = WebClient.builder().clientConnector(new ReactorClientHttpConnector(httpClient)).build();
		@SuppressWarnings("rawtypes")
		Mono<Map> res = client.post()
				.uri("https://identitytoolkit.googleapis.com/v1/accounts:signInWithCustomToken?key=" + firebaseKey)
				.contentType(MediaType.APPLICATION_JSON).bodyValue(request).retrieve().bodyToMono(Map.class)
				.onErrorReturn(Collections.emptyMap());

		return res.flatMap(result -> {
			if (result.get("idToken") == null) {
				return Mono.empty();
			}
			try {
				return Mono.just(FirebaseAuth.getInstance().verifyIdToken((String) result.get("idToken")));
			} catch (FirebaseAuthException e) {
				log.error("error thrown: ", e);
				return Mono.empty();
			}
		});
	}
}
