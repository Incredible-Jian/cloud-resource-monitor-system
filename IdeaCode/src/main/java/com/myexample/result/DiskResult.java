package com.myexample.result;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiskResult {
    private String mydate;
    private int disk_rate;

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
