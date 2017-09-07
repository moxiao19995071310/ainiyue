package pay.weixin.annotation;

/**
 * 标记字段是可选的
 * @since 1.0.0
 */
public @interface Optional {

    /**
     * 是否任何情况下都可选
     */
    boolean any() default true;
}
