/*
 * Copyright 2012-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.boot.test.autoconfigure.restdocs;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentationConfigurer;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.restassured3.RestAssuredRestDocumentation;
import org.springframework.restdocs.restassured3.RestAssuredRestDocumentationConfigurer;
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation;
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentationConfigurer;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for Spring REST Docs.
 *
 * @author Andy Wilkinson
 * @author Eddú Meléndez
 * @author Roman Zaynetdinov
 * @since 1.4.0
 */
@Configuration
@EnableConfigurationProperties
@ConditionalOnWebApplication
public class RestDocsAutoConfiguration {

	@Configuration
	@ConditionalOnClass(MockMvcRestDocumentation.class)
	@ConditionalOnWebApplication(type = Type.SERVLET)
	static class RestDocsMockMvcAutoConfiguration {

		@Bean
		@ConditionalOnMissingBean(MockMvcRestDocumentationConfigurer.class)
		public MockMvcRestDocumentationConfigurer restDocsMockMvcConfigurer(
				ObjectProvider<RestDocsMockMvcConfigurationCustomizer> configurationCustomizerProvider,
				RestDocumentationContextProvider contextProvider) {
			MockMvcRestDocumentationConfigurer configurer = MockMvcRestDocumentation
					.documentationConfiguration(contextProvider);
			RestDocsMockMvcConfigurationCustomizer configurationCustomizer = configurationCustomizerProvider
					.getIfAvailable();
			if (configurationCustomizer != null) {
				configurationCustomizer.customize(configurer);
			}
			return configurer;
		}

		@Bean
		@ConfigurationProperties(prefix = "spring.test.restdocs")
		public RestDocsMockMvcBuilderCustomizer restDocumentationConfigurer(
				MockMvcRestDocumentationConfigurer configurer,
				ObjectProvider<RestDocumentationResultHandler> resultHandler) {
			return new RestDocsMockMvcBuilderCustomizer(configurer,
					resultHandler.getIfAvailable());
		}

	}

	@Configuration
	@ConditionalOnClass({ RequestSpecification.class,
			RestAssuredRestDocumentation.class })
	static class RestDocsRestAssuredAutoConfiguration {

		@Bean
		@ConditionalOnMissingBean(RequestSpecification.class)
		public RequestSpecification restDocsRestAssuredConfigurer(
				ObjectProvider<RestDocsRestAssuredConfigurationCustomizer> configurationCustomizerProvider,
				RestDocumentationContextProvider contextProvider) {
			RestAssuredRestDocumentationConfigurer configurer = RestAssuredRestDocumentation
					.documentationConfiguration(contextProvider);
			RestDocsRestAssuredConfigurationCustomizer configurationCustomizer = configurationCustomizerProvider
					.getIfAvailable();
			if (configurationCustomizer != null) {
				configurationCustomizer.customize(configurer);
			}
			return new RequestSpecBuilder().addFilter(configurer).build();
		}

		@Bean
		@ConfigurationProperties(prefix = "spring.test.restdocs")
		public RestDocsRestAssuredBuilderCustomizer restAssuredBuilderCustomizer(
				RequestSpecification configurer) {
			return new RestDocsRestAssuredBuilderCustomizer(configurer);
		}

	}

	@Configuration
	@ConditionalOnClass(WebTestClientRestDocumentation.class)
	@ConditionalOnWebApplication(type = Type.REACTIVE)
	static class RestDocsWebTestClientAutoConfiguration {

		@Bean
		@ConditionalOnMissingBean(WebTestClientRestDocumentationConfigurer.class)
		public WebTestClientRestDocumentationConfigurer restDocsWebTestClientConfigurer(
				ObjectProvider<RestDocsWebTestClientConfigurationCustomizer> configurationCustomizerProvider,
				RestDocumentationContextProvider contextProvider) {
			WebTestClientRestDocumentationConfigurer configurer = WebTestClientRestDocumentation
					.documentationConfiguration(contextProvider);
			RestDocsWebTestClientConfigurationCustomizer configurationCustomizer = configurationCustomizerProvider
					.getIfAvailable();
			if (configurationCustomizer != null) {
				configurationCustomizer.customize(configurer);
			}
			return configurer;
		}

		@Bean
		@ConfigurationProperties(prefix = "spring.test.restdocs")
		public RestDocsWebTestClientBuilderCustomizer restDocumentationConfigurer(
				WebTestClientRestDocumentationConfigurer configurer) {
			return new RestDocsWebTestClientBuilderCustomizer(configurer);
		}

	}

}
