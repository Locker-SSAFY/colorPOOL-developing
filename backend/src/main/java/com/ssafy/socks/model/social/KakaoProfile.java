package com.ssafy.socks.model.social;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class KakaoProfile {
	private Long id;
	private Properties properties;
	private String email;

	@Getter @Setter @ToString
	public static class Properties {
		private String nickname;
		private String thumbnail_image;
		private String profile_image;
	}
}