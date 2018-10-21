package org.tlh.springcloud.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by 离歌笑tlh/hu ping on 18/10/21
 * <p>
 * Github: https://github.com/tlhhup
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RateLimit {

    private String tokenKey;
    private int replenishRate;
    private int burstCapacity;
    private int requested;

}
