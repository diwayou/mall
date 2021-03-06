package org.emall.brand.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.diwayou.core.bean.Convertible;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author gaopeng 2021/2/24
 */
@Data
@NoArgsConstructor
public class BrandCreateRequest implements Convertible {

    @NotNull(message = "名称不能为空")
    @Size(message = "品牌名称长度范围1~50", min = 1, max = 50)
    private String name;

    @NotNull(message = "logo不能为空")
    @Size(message = "logo长度过长", min = 1, max = 100)
    private String logo;

    @Size(message = "商标注册证编号长度范围1~50", min = 1, max = 50)
    @ApiModelProperty(value = "商标注册证编号")
    private String certificateCode;

    @Size(message = "品牌介绍长度范围1~300", min = 1, max = 300)
    private String desc;
}
