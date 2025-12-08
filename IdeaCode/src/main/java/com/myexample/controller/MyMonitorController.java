package com.myexample.controller;


import com.myexample.pojo.Monitor;
import com.myexample.service.MyMonitorService;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/")
public class MyMonitorController {
    @Autowired
    private MyMonitorService mymonitor;
    @RequestMapping("/getsystem")
    public String getmonitorSystem(){
        String url = "smb://root:123456@192.168.152.141/root/system.txt";
        List<Monitor> mylists = new ArrayList<>();
        try {
            SmbFile myfile = new SmbFile(url);
            InputStream myis = new SmbFileInputStream(myfile);
            InputStreamReader myreader = new InputStreamReader(myis);
            BufferedReader mybuffer = new BufferedReader(myreader);
            String line = "";
            while((line=mybuffer.readLine())!=null){
                String mylines = line.replace("|",",");
                String[] mymonitors = mylines.split(",");
                Monitor monitor = new Monitor();
                monitor.setIp(mymonitors[2]);
                monitor.setMydate(mymonitors[0]);
                monitor.setCpu_us(Double.parseDouble(mymonitors[5]));
                monitor.setCpu_sys(Double.parseDouble(mymonitors[6]));
                monitor.setCpu_id(Double.parseDouble(mymonitors[7]));
                monitor.setMem_total(Integer.parseInt(mymonitors[8]));
                monitor.setMem_used(Integer.parseInt(mymonitors[9]));
                monitor.setDisk_rate(Integer.parseInt(mymonitors[10]));
                mymonitor.insertMonitorData(monitor);
                mylists.add(monitor);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "index";
    }
}
