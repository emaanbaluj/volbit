//package com.volbit.controller;
//
//import com.volbit.model.OptionRequest;
//
//
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/option")
//public class OptionController {
//
//    private final OptionService optionService;
//
//    public OptionController(OptionService optionService) {
//        this.optionService = optionService;
//    }
//
//    /**
//     * POST /api/option/spot
//     * body: { "coinId":"bitcoin", "strike":60000, "expiry":"2025-12-31T00:00:00Z", "isCall":true }
//     * returns: { "coin":"bitcoin", "spot":105000.5 }
//     */
//    @PostMapping("/spot")
//    public Map<String,Object> currentSpot(@RequestBody OptionRequest req) {
//        Double spot = optionService.fetchCurrentSpot(req);
//        return Map.of(
//                "coin", req.getCoinId(),
//                "spot", spot
//        );
//    }
//}
