package com.jaoow.crates.model;

import com.google.common.collect.Lists;
import com.jaoow.crates.model.crate.Crate;
import com.jaoow.crates.settings.Config;
import lombok.Data;
import org.apache.commons.lang.time.DateUtils;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
public class CrateUser {

    private final String playerName;
    private final UUID uniqueId;

    private final List<Long> openings = Lists.newArrayList();
    private long delayTime = -1;

    public void setDelayTime(long delayTime) {
        this.delayTime = System.currentTimeMillis() + delayTime;
    }

    public boolean isDelayed() {
        return getDelayTime() > System.currentTimeMillis();
    }

    public int getDailyOpenings() {
        return (int) this.openings.stream().filter(time -> DateUtils.isSameDay(new Date(time), new Date())).count();
    }

    public boolean reachedLimit() {
        return Config.MAX_CRATES_PER_DAY > getDailyOpenings();
    }

    public void open() {
        this.openings.add(System.currentTimeMillis());
    }
}
