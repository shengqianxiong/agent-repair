package com.sqx.modules.bar.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sqx.common.PageResult;
import com.sqx.common.exception.BusinessException;
import com.sqx.modules.bar.constant.BarConstants;
import com.sqx.modules.bar.db.entity.BarBooking;
import com.sqx.modules.bar.db.entity.BarTable;
import com.sqx.modules.bar.db.entity.BarUser;
import com.sqx.modules.bar.db.mapper.BarBookingMapper;
import com.sqx.modules.bar.db.mapper.BarTableMapper;
import com.sqx.modules.bar.db.mapper.BarUserMapper;
import com.sqx.modules.bar.domain.bo.BookingAssignTableBo;
import com.sqx.modules.bar.domain.bo.BookingQueryBo;
import com.sqx.modules.bar.domain.bo.BookingSubmitBo;
import com.sqx.modules.bar.domain.vo.BookingVo;
import com.sqx.modules.bar.domain.vo.TimeSlotVo;
import com.sqx.modules.bar.service.BarBookingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BarBookingServiceImpl implements BarBookingService {

    private static final List<String> DEFAULT_SLOTS = Arrays.asList(
            "18:00-20:00", "20:00-22:00", "22:00-24:00"
    );

    private final BarBookingMapper bookingMapper;
    private final BarUserMapper userMapper;
    private final BarTableMapper tableMapper;

    public BarBookingServiceImpl(BarBookingMapper bookingMapper,
                                 BarUserMapper userMapper,
                                 BarTableMapper tableMapper) {
        this.bookingMapper = bookingMapper;
        this.userMapper = userMapper;
        this.tableMapper = tableMapper;
    }

    @Override
    public List<TimeSlotVo> timeSlots() {
        List<TimeSlotVo> slots = new ArrayList<>();
        for (String slot : DEFAULT_SLOTS) {
            TimeSlotVo vo = new TimeSlotVo();
            vo.setSlot(slot);
            vo.setAvailable(true);
            slots.add(vo);
        }
        return slots;
    }

    @Override
    public BookingVo submit(Long userId, BookingSubmitBo bo) {
        if (bo.getBookingDate() == null) {
            throw new BusinessException("预约日期不能为空");
        }
        if (StrUtil.isBlank(bo.getTimeSlot())) {
            throw new BusinessException("预约时段不能为空");
        }
        if (bo.getGuestCount() == null || bo.getGuestCount() < 1) {
            throw new BusinessException("预约人数不能小于1");
        }
        Date now = new Date();
        BarBooking booking = new BarBooking();
        booking.setUserId(userId);
        booking.setBookingDate(bo.getBookingDate());
        booking.setTimeSlot(bo.getTimeSlot());
        booking.setGuestCount(bo.getGuestCount());
        booking.setSeatType(StrUtil.blankToDefault(bo.getSeatType(), "大厅"));
        booking.setStatus(BarConstants.BOOKING_STATUS_PENDING);
        booking.setCreateTime(now);
        booking.setUpdateTime(now);
        bookingMapper.insert(booking);
        return detail(booking.getId());
    }

    @Override
    public PageResult<BookingVo> page(BookingQueryBo queryBo) {
        int page = queryBo.getPage() == null || queryBo.getPage() < 1 ? 1 : queryBo.getPage();
        int pageSize = queryBo.getPageSize() == null || queryBo.getPageSize() < 1 ? 10 : queryBo.getPageSize();
        LambdaQueryWrapper<BarBooking> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(queryBo.getStatus())) {
            wrapper.eq(BarBooking::getStatus, queryBo.getStatus());
        }
        if (queryBo.getBookingDate() != null) {
            wrapper.eq(BarBooking::getBookingDate, queryBo.getBookingDate());
        }
        if (queryBo.getUserId() != null) {
            wrapper.eq(BarBooking::getUserId, queryBo.getUserId());
        }
        wrapper.orderByDesc(BarBooking::getCreateTime);
        Page<BarBooking> pageData = bookingMapper.selectPage(new Page<>(page, pageSize), wrapper);
        List<BookingVo> list = toVoList(pageData.getRecords());
        return PageResult.of(list, pageData.getTotal(), page, pageSize);
    }

    @Override
    public List<BookingVo> recent(int limit) {
        List<BarBooking> bookings = bookingMapper.selectList(new LambdaQueryWrapper<BarBooking>()
                .orderByDesc(BarBooking::getCreateTime)
                .last("LIMIT " + limit));
        return toVoList(bookings);
    }

    @Override
    public BookingVo detail(Long id) {
        BarBooking booking = getBookingOrThrow(id);
        return toVo(booking);
    }

    @Override
    public void cancel(Long userId, Long id) {
        BarBooking booking = getBookingOrThrow(id);
        if (!booking.getUserId().equals(userId)) {
            throw new BusinessException("无权操作该预约");
        }
        if (!BarConstants.BOOKING_STATUS_PENDING.equals(booking.getStatus())) {
            throw new BusinessException("仅待确认预约可取消");
        }
        booking.setStatus(BarConstants.BOOKING_STATUS_CANCELLED);
        booking.setUpdateTime(new Date());
        bookingMapper.updateById(booking);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirm(Long id) {
        BarBooking booking = getBookingOrThrow(id);
        if (!BarConstants.BOOKING_STATUS_PENDING.equals(booking.getStatus())) {
            throw new BusinessException("仅待确认预约可确认");
        }
        booking.setStatus(BarConstants.BOOKING_STATUS_CONFIRMED);
        booking.setUpdateTime(new Date());
        bookingMapper.updateById(booking);
    }

    @Override
    public void adminCancel(Long id) {
        BarBooking booking = getBookingOrThrow(id);
        booking.setStatus(BarConstants.BOOKING_STATUS_CANCELLED);
        booking.setUpdateTime(new Date());
        bookingMapper.updateById(booking);
        releaseTable(booking.getTableId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignTable(BookingAssignTableBo bo) {
        if (bo.getBookingId() == null || bo.getTableId() == null) {
            throw new BusinessException("参数不完整");
        }
        BarBooking booking = getBookingOrThrow(bo.getBookingId());
        BarTable table = tableMapper.selectById(bo.getTableId());
        if (table == null) {
            throw new BusinessException("桌位不存在");
        }
        if (booking.getTableId() != null && !booking.getTableId().equals(bo.getTableId())) {
            releaseTable(booking.getTableId());
        }
        booking.setTableId(bo.getTableId());
        booking.setStatus(BarConstants.BOOKING_STATUS_CONFIRMED);
        booking.setUpdateTime(new Date());
        bookingMapper.updateById(booking);
        table.setStatus(BarConstants.TABLE_STATUS_RESERVED);
        table.setUpdateTime(new Date());
        tableMapper.updateById(table);
    }

    private void releaseTable(Long tableId) {
        if (tableId == null) {
            return;
        }
        BarTable table = tableMapper.selectById(tableId);
        if (table != null) {
            table.setStatus(BarConstants.TABLE_STATUS_FREE);
            table.setUpdateTime(new Date());
            tableMapper.updateById(table);
        }
    }

    private BarBooking getBookingOrThrow(Long id) {
        BarBooking booking = bookingMapper.selectById(id);
        if (booking == null) {
            throw new BusinessException("预约不存在");
        }
        return booking;
    }

    private List<BookingVo> toVoList(List<BarBooking> bookings) {
        if (bookings.isEmpty()) {
            return new ArrayList<>();
        }
        Map<Long, String> userMap = userMapper.selectBatchIds(
                bookings.stream().map(BarBooking::getUserId).distinct().collect(Collectors.toList())
        ).stream().collect(Collectors.toMap(BarUser::getId, BarUser::getNickname, (a, b) -> a));
        Map<Long, String> tableMap = tableMapper.selectList(null).stream()
                .collect(Collectors.toMap(BarTable::getId, BarTable::getTableNo, (a, b) -> a));
        return bookings.stream().map(booking -> {
            BookingVo vo = new BookingVo();
            BeanUtil.copyProperties(booking, vo);
            vo.setUserNickname(userMap.get(booking.getUserId()));
            if (booking.getTableId() != null) {
                vo.setTableNo(tableMap.get(booking.getTableId()));
            }
            return vo;
        }).collect(Collectors.toList());
    }

    private BookingVo toVo(BarBooking booking) {
        BookingVo vo = new BookingVo();
        BeanUtil.copyProperties(booking, vo);
        BarUser user = userMapper.selectById(booking.getUserId());
        if (user != null) {
            vo.setUserNickname(user.getNickname());
        }
        if (booking.getTableId() != null) {
            BarTable table = tableMapper.selectById(booking.getTableId());
            if (table != null) {
                vo.setTableNo(table.getTableNo());
            }
        }
        return vo;
    }
}
