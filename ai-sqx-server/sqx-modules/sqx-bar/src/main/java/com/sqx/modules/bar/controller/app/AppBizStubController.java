package com.sqx.modules.bar.controller.app;

import com.sqx.common.PageResult;
import com.sqx.common.Result;
import com.sqx.common.annotation.Login;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户端 - 业务接口占位（购物车/订单/钱包/积分等，待后续迭代完善）
 * 路径与 PRD §8.2 保持一致，保证联调契约统一
 */
@RestController
public class AppBizStubController {

    @PostMapping("/app/cart/add")
    @Login
    public Result<Void> cartAdd(@RequestBody Map<String, Object> body) {
        return Result.ok();
    }

    @GetMapping("/app/cart/summary")
    @Login
    public Result<Map<String, Object>> cartSummary() {
        Map<String, Object> data = new HashMap<>();
        data.put("items", Collections.emptyList());
        data.put("count", 0);
        data.put("itemCount", 0);
        data.put("totalAmount", 0);
        data.put("discountAmount", 0);
        return Result.ok(data);
    }

    @PostMapping("/app/order/preview")
    @Login
    public Result<Map<String, Object>> orderPreview(@RequestBody(required = false) Map<String, Object> body) {
        Map<String, Object> data = new HashMap<>();
        data.put("cart", cartSummary().getData());
        Map<String, Object> store = new HashMap<>();
        store.put("name", "云享酒吧 · 静安店");
        store.put("address", "上海市静安区南京西路1266号");
        store.put("tableNo", "A-08");
        store.put("dineType", "堂食");
        data.put("store", store);
        return Result.ok(data);
    }

    @PostMapping("/app/order/submit")
    @Login
    public Result<Map<String, String>> orderSubmit(@RequestBody Map<String, Object> body) {
        Map<String, String> data = new HashMap<>();
        data.put("orderId", "ORD20260607001");
        return Result.ok(data);
    }

    @PostMapping("/app/order/create")
    @Login
    public Result<Map<String, String>> orderCreate(@RequestBody Map<String, Object> body) {
        return orderSubmit(body);
    }

    @GetMapping("/app/order/list")
    @Login
    public Result<PageResult<Map<String, Object>>> orderList() {
        return Result.ok(PageResult.of(Collections.emptyList(), 0, 1, 10));
    }

    @GetMapping("/app/order/detail/{id}")
    @Login
    public Result<Map<String, Object>> orderDetail(@PathVariable String id) {
        Map<String, Object> data = new HashMap<>();
        data.put("id", id);
        data.put("status", "making");
        data.put("statusText", "制作中");
        return Result.ok(data);
    }

    @PostMapping("/app/order/reorder")
    @Login
    public Result<Void> orderReorder(@RequestBody Map<String, Object> body) {
        return Result.ok();
    }

    @GetMapping("/app/order/pickup-code/{id}")
    @Login
    public Result<Map<String, String>> pickupCode(@PathVariable String id) {
        Map<String, String> data = new HashMap<>();
        data.put("pickupCode", "A086");
        return Result.ok(data);
    }

    @PostMapping("/app/order/cancel")
    @Login
    public Result<Void> orderCancel(@RequestBody Map<String, Object> body) {
        return Result.ok();
    }

    @GetMapping("/app/payment/list")
    @Login
    public Result<Map<String, Object>> paymentList() {
        Map<String, Object> data = new HashMap<>();
        data.put("methods", Collections.emptyList());
        return Result.ok(data);
    }

    @GetMapping("/app/wallet/info")
    @Login
    public Result<Map<String, Object>> walletInfo() {
        Map<String, Object> data = new HashMap<>();
        data.put("balance", 0);
        return Result.ok(data);
    }

    @GetMapping("/app/wallet/bill/list")
    @Login
    public Result<PageResult<Map<String, Object>>> walletBillList() {
        return Result.ok(PageResult.of(Collections.emptyList(), 0, 1, 10));
    }

    @GetMapping("/app/wallet/withdraw/list")
    @Login
    public Result<PageResult<Map<String, Object>>> walletWithdrawList() {
        return Result.ok(PageResult.of(Collections.emptyList(), 0, 1, 10));
    }

    @GetMapping("/app/wallet/account/list")
    @Login
    public Result<Object> walletAccountList() {
        return Result.ok(Collections.emptyList());
    }

    @PostMapping("/app/wallet/withdraw")
    @Login
    public Result<Void> walletWithdraw(@RequestBody Map<String, Object> body) {
        return Result.ok();
    }

    @PostMapping("/app/wallet/recharge")
    @Login
    public Result<Void> walletRecharge(@RequestBody Map<String, Object> body) {
        return Result.ok();
    }

    @GetMapping("/app/points/info")
    @Login
    public Result<Map<String, Object>> pointsInfo() {
        Map<String, Object> data = new HashMap<>();
        data.put("balance", 0);
        data.put("totalPoints", 0);
        return Result.ok(data);
    }

    @GetMapping("/app/points/list")
    @Login
    public Result<PageResult<Map<String, Object>>> pointsList() {
        return Result.ok(PageResult.of(Collections.emptyList(), 0, 1, 10));
    }

    @GetMapping("/app/points/products")
    @Login
    public Result<PageResult<Map<String, Object>>> pointsProducts() {
        return Result.ok(PageResult.of(Collections.emptyList(), 0, 1, 10));
    }

    @PostMapping("/app/points/redeem")
    @Login
    public Result<Void> pointsRedeem(@RequestBody Map<String, Object> body) {
        return Result.ok();
    }

    @GetMapping("/app/points/history")
    @Login
    public Result<PageResult<Map<String, Object>>> pointsHistory() {
        return Result.ok(PageResult.of(Collections.emptyList(), 0, 1, 10));
    }

    @PostMapping("/app/points/checkin")
    @Login
    public Result<Map<String, Integer>> pointsCheckin() {
        Map<String, Integer> data = new HashMap<>();
        data.put("points", 10);
        return Result.ok(data);
    }

    @GetMapping("/app/distribution/info")
    @Login
    public Result<Map<String, Object>> distributionInfo() {
        Map<String, Object> data = new HashMap<>();
        data.put("totalEarnings", 0);
        data.put("inviteCount", 0);
        return Result.ok(data);
    }

    @GetMapping("/app/distribution/invitees")
    @Login
    public Result<PageResult<Map<String, Object>>> distributionInvitees() {
        return Result.ok(PageResult.of(Collections.emptyList(), 0, 1, 10));
    }

    @PostMapping("/app/distribution/withdraw")
    @Login
    public Result<Void> distributionWithdraw(@RequestBody Map<String, Object> body) {
        return Result.ok();
    }

    @GetMapping("/app/alcohol/stored/list")
    @Login
    public Result<PageResult<Map<String, Object>>> alcoholStoredList() {
        return Result.ok(PageResult.of(Collections.emptyList(), 0, 1, 10));
    }

    @PostMapping("/app/alcohol/stored/retrieve")
    @Login
    public Result<Void> alcoholRetrieve(@RequestBody Map<String, Object> body) {
        return Result.ok();
    }

    @PostMapping("/app/alcohol/stored/renew")
    @Login
    public Result<Void> alcoholRenew(@RequestBody Map<String, Object> body) {
        return Result.ok();
    }

    @GetMapping("/app/square/list")
    @Login
    public Result<PageResult<Map<String, Object>>> squareList() {
        return Result.ok(PageResult.of(Collections.emptyList(), 0, 1, 10));
    }

    @PostMapping("/app/square/like")
    @Login
    public Result<Void> squareLike(@RequestBody Map<String, Object> body) {
        return Result.ok();
    }

    @PostMapping("/app/square/create")
    @Login
    public Result<Void> squareCreate(@RequestBody Map<String, Object> body) {
        return Result.ok();
    }

    @GetMapping("/app/square/categories")
    @Login
    public Result<Map<String, Object>> squareCategories() {
        Map<String, Object> data = new HashMap<>();
        data.put("list", Collections.emptyList());
        return Result.ok(data);
    }

    @GetMapping("/app/dynamic/user_list")
    @Login
    public Result<Map<String, Object>> dynamicUserList() {
        Map<String, Object> data = new HashMap<>();
        data.put("dynamics", Collections.emptyList());
        return Result.ok(data);
    }

    @PostMapping("/app/chat/create")
    @Login
    public Result<Map<String, Long>> chatCreate(@RequestBody Map<String, Object> body) {
        Map<String, Long> data = new HashMap<>();
        data.put("chatId", 1L);
        return Result.ok(data);
    }

    @GetMapping("/app/table/detail")
    @Login
    public Result<Map<String, Object>> tableDetail() {
        Map<String, Object> data = new HashMap<>();
        data.put("tableNo", "A.红桌");
        data.put("totalSeats", 8);
        data.put("occupiedSeats", 0);
        return Result.ok(data);
    }

    @PostMapping("/app/table/seat/join")
    @Login
    public Result<Void> tableSeatJoin(@RequestBody Map<String, Object> body) {
        return Result.ok();
    }

    @PostMapping("/app/table/seat/leave")
    @Login
    public Result<Void> tableSeatLeave(@RequestBody Map<String, Object> body) {
        return Result.ok();
    }
}
