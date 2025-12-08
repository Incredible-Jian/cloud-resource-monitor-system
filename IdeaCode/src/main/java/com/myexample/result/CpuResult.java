package com.myexample.result;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CpuResult {
    private String mydate;
    private double cpu_id;

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
