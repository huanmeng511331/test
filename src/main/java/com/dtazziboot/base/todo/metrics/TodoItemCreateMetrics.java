package com.dtazziboot.base.todo.metrics;

import com.dtazziboot.base.common.constant.CommonErrorCodeConstants;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Component;

/**
 * 新增待办接口埋点(系分 §7.1 可监控,CR P0-3):
 * 调用计数(QPS)、处理耗时(RT)、按处理结果(成功/各错误码)分流的失败计数,DB 写入失败单独计数。
 *
 * <p>指标契约:</p>
 * <ul>
 * <li>{@code todo_item_create_total}:调用计数,tags = source/result/code</li>
 * <li>{@code todo_item_create_cost_milliseconds}:处理耗时分布,tags = source</li>
 * </ul>
 */
@Component
public class TodoItemCreateMetrics {

    /** 调用来源:oneapi Web 控制台 */
    private static final String SOURCE_ONEAPI = "oneapi";

    /** 处理结果:成功 */
    private static final String RESULT_SUCCESS = "success";

    /** 处理结果:业务失败(参数校验/开关等 BizException,按错误码细分) */
    private static final String RESULT_BIZ_FAIL = "biz_fail";

    /** 处理结果:DB 写入失败(单独计数) */
    private static final String RESULT_DB_FAIL = "db_fail";

    private static final String METRIC_CREATE_TOTAL = "todo_item_create_total";

    private static final String METRIC_CREATE_COST = "todo_item_create_cost_milliseconds";

    private final MeterRegistry meterRegistry;

    public TodoItemCreateMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    /**
     * 记录一次成功调用。
     */
    public void recordSuccess(long costMillis) {
        record(RESULT_SUCCESS, "OK", costMillis);
    }

    /**
     * 记录一次业务失败(按错误码细分)。
     */
    public void recordBizFailure(String errorCode, long costMillis) {
        record(RESULT_BIZ_FAIL, errorCode, costMillis);
    }

    /**
     * 记录一次 DB 写入失败(单独计数)。
     */
    public void recordDbFailure(long costMillis) {
        record(RESULT_DB_FAIL, CommonErrorCodeConstants.SYS_ERROR, costMillis);
    }

    private void record(String result, String errorCode, long costMillis) {
        meterRegistry.counter(METRIC_CREATE_TOTAL, "source", SOURCE_ONEAPI, "result", result, "code", errorCode)
                .increment();
        meterRegistry.timer(METRIC_CREATE_COST, "source", SOURCE_ONEAPI)
                .record(costMillis, TimeUnit.MILLISECONDS);
    }
}
