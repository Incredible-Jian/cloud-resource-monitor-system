package com.myexample.controller;

import com.myexample.pojo.BlockedIP;
import com.myexample.service.BlockedIPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/ip-block")
public class BlockedIPController {
    @Autowired
    private BlockedIPService blockedIPService;

    @GetMapping("/list")
    public String list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        Map<String, Object> result = blockedIPService.getAllActive(page, size);
        model.addAttribute("blockedIPs", result.get("content"));
        model.addAttribute("totalPages", result.get("totalPages"));
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);

        return "ip-block-list";
    }

    @GetMapping("/search")
    public String searchIPs(@RequestParam String keyword, Model model) {
        List<BlockedIP> searchResults = blockedIPService.searchIPs(keyword);
        model.addAttribute("blockedIPs", searchResults);
        model.addAttribute("currentPage", 1);
        model.addAttribute("totalPages", 1);
        return "ip-block-list";
    }

    @PostMapping("/block")
    public String blockIP(@RequestParam String ip,
                          @RequestParam String reason,
                          Model model) {
        if (blockedIPService.isIPBlocked(ip)) {
            model.addAttribute("error", "该IP已被封锁");
            return list(1, 10, model);
        }

        BlockedIP blockedIP = new BlockedIP();
        blockedIP.setIpAddress(ip);
        blockedIP.setBlockTime(new Date());
        blockedIP.setReason(reason);
        blockedIP.setStatus(1);

        // 设置24小时后自动解封
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR, 24);
        blockedIP.setUnblockTime(cal.getTime());

        blockedIPService.blockIP(blockedIP);
        return "redirect:/ip-block/list";
    }

    @PostMapping("/unblock/{id}")
    public String unblockIP(@PathVariable int id) {
        blockedIPService.unblockIP(id);
        return "redirect:/ip-block/list";
    }
}