package xyz.zzj.springbootxztxbackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

/**
 * 自定义swagger接口的配置
 */
@Configuration
@EnableSwagger2WebMvc
@Profile({"dev","test"})
public class SwaggerConfiguration {

    @Bean(value = "defaultApi2")
    public Docket defaultApi2() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .groupName("用户接口组")
                .select()
                //这里一定要标注你控制器的位置
                .apis(RequestHandlerSelectors.basePackage("xyz.zzj.springbootxztxbackend.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    /*
     * 可以创建其他bean来配置其他分组
     * 与createUserApi()类似
     * */
    private ApiInfo apiInfo(){
        return new ApiInfoBuilder()
                .title("用户中心")
                .description("接口文档")
                .termsOfServiceUrl("https://blog.csdn.net/weixin_51713937?spm=1001.2101.3001.5343")
                .contact(new Contact("zzj", "https://blog.csdn.net/weixin_51713937?spm=1001.2101.3001.5343", "2370865955@qq.com"))
                .version("1.0")
                .build();
    }

}
