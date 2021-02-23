package org.emall.order.fsm;

/**
 * @author gaopeng 2021/1/28
 */
public enum OrderEvent {
    /**
     * 创建订单
     */
    Create,

    Confirm,
    Processing,
    Completed,
    Attention,
    Returned,
    Cancelled
}