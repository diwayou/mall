package org.emall.category.model.entity;

/**
 * 类目
 *
 * @author gaopeng 2021/2/9
 */
public class Category {

    /**
     * 类目id
     */
    private Integer id;

    /**
     * 父类目id
     */
    private Integer parentId;

    /**
     * 名称
     */
    private String name;

    /**
     * 层级
     */
    private Integer level;

    /**
     * 是否叶子节点
     */
    private Boolean leaf;

    /**
     * 备注
     */
    private String notes;

    /**
     * 扩展字段，JSON
     */
    private String features;

    /**
     * 排序值
     */
    private Integer sortOrder;

    /**
     * 状态
     */
    private Integer status;
}
