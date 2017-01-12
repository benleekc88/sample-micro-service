package com.spring.sample.service;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface WorkUnitsSink {
    String CHANNEL_NAME = "worksChannel";

    @Input
    SubscribableChannel worksChannel();
}