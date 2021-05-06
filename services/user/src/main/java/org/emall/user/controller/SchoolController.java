package org.emall.user.controller;

import org.diwayou.updown.annotation.RequestFile;
import org.emall.user.manager.SchoolManager;
import org.emall.user.model.domain.SchoolInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author gaopeng 2021/5/6
 */
@Controller
@RequestMapping(path = "/school")
public class SchoolController {

    @Autowired
    private SchoolManager schoolManager;

    @GetMapping("/sync/{id}")
    @ResponseBody
    public void sync(@PathVariable("id") Integer schoolId,
                     @RequestParam(name = "year", defaultValue = "2020") Integer year) {
        schoolManager.sync(schoolId, year);
    }

    @GetMapping("/syncAll")
    @ResponseBody
    public void syncAll() {
        schoolManager.syncAll();
    }

    @PostMapping("/upload")
    @ResponseBody
    public void upload(@RequestFile List<SchoolInfo> schoolInfos) {
        schoolManager.upload(schoolInfos);
    }
}
