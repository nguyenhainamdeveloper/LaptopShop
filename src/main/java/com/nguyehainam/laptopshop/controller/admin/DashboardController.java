package com.nguyehainam.laptopshop.controller.admin;

@Controller
public class DashboardController {

    @GetMapping("/admin")
    public String getDashboard() {
        return "admin/dashboard/show";
    }
}
