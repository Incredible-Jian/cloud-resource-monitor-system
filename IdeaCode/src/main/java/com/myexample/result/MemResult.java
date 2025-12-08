package com.myexample.result;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemResult {
    private String mydate;
    private int mem_total;
    private int mem_used;

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
