package org.diwayou.mq.memory.consumer;

import lombok.extern.slf4j.Slf4j;
import org.diwayou.core.annotation.AnnotationUtil;
import org.diwayou.mq.annotation.MqListener;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;
import org.springframework.messaging.handler.annotation.support.MessageHandlerMethodFactory;
import org.springframework.messaging.handler.invocation.InvocableHandlerMethod;

import java.lang.reflect.Method;

/**
 * @author gaopeng 2021/2/18
 */
@Slf4j
public class MqMemoryListenerAnnotationBeanPostProcessor implements BeanPostProcessor, Ordered, BeanFactoryAware,
        SmartInitializingSingleton, ApplicationContextAware {

    private BeanFactory beanFactory;

    private ConfigurableApplicationContext applicationContext;

    private RocketmqHandlerMethodFactoryAdapter handlerMethodFactory = new RocketmqHandlerMethodFactoryAdapter();

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        AnnotationUtil.processBean(bean, beanName, MqListener.class, this::processMqListener);

        return bean;
    }

    protected void processMqListener(MqListener mqListener, Method method, Object bean, String beanName) {
        InvocableHandlerMethod handlerMethod = handlerMethodFactory.createInvocableHandlerMethod(bean, method);

        MqListenerRegistry.I().register(mqListener, handlerMethod);
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void afterSingletonsInstantiated() {
        MqListenerRegistry.I().registerAll();
    }

    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = (ConfigurableApplicationContext) applicationContext;
    }

    private class RocketmqHandlerMethodFactoryAdapter implements MessageHandlerMethodFactory {

        private MessageHandlerMethodFactory handlerMethodFactory;

        public void setHandlerMethodFactory(MessageHandlerMethodFactory kafkaHandlerMethodFactory1) {
            this.handlerMethodFactory = kafkaHandlerMethodFactory1;
        }

        @Override
        public InvocableHandlerMethod createInvocableHandlerMethod(Object bean, Method method) {
            return getHandlerMethodFactory().createInvocableHandlerMethod(bean, method);
        }

        private MessageHandlerMethodFactory getHandlerMethodFactory() {
            if (this.handlerMethodFactory == null) {
                this.handlerMethodFactory = createDefaultMessageHandlerMethodFactory();
            }
            return this.handlerMethodFactory;
        }

        private MessageHandlerMethodFactory createDefaultMessageHandlerMethodFactory() {
            DefaultMessageHandlerMethodFactory defaultFactory = new DefaultMessageHandlerMethodFactory();

            defaultFactory.setBeanFactory(MqMemoryListenerAnnotationBeanPostProcessor.this.beanFactory);

            defaultFactory.afterPropertiesSet();

            return defaultFactory;
        }

    }
}
