package com.egouletlang.logmein;

import com.google.common.collect.ImmutableList;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.service.Tag;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableMongoRepositories
@EnableSwagger2
public class AppEntry {

    public static void main(String[] args) {
        SpringApplication.run(AppEntry.class, args);
    }

    @Bean
    public Docket productApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                        .select()
                        .apis(RequestHandlerSelectors.basePackage("com.egouletlang.logmein"))
                        .build()
                        .useDefaultResponseMessages(false)
                        .globalResponseMessage(RequestMethod.GET, ImmutableList.of(new ResponseMessageBuilder().code(500).message("Error Message").build()))
                        .globalResponseMessage(RequestMethod.POST, ImmutableList.of(new ResponseMessageBuilder().code(500).message("Error Message").build()))
                        .globalResponseMessage(RequestMethod.DELETE, ImmutableList.of(new ResponseMessageBuilder().code(500).message("Error Message").build()))
                        .apiInfo(apiInfo());


//        .globalResponseMessage(RequestMethod.GET, ImmutableList.of(new ResponseMessage(200, "Some global OK message",null)));
    }



    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                        .title("LogMeIn Interview Assignment API")
                        .description("This API manages the resources required to create multi-user card games")
                        .version("1.0.0")
                        .license("MIT License")
                        .licenseUrl("http://opensource.org/licenses/MIT")
                        .contact(new Contact("Etienne Goulet-Lang", "https://www.linkedin.com/in/etienne-goulet-lang-b9489925/", "logmein@egouletlang.com"))
                        .build();
    }

}