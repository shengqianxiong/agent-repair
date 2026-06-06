package com.sqx.modules.bar.service;

import com.sqx.common.PageResult;
import com.sqx.modules.bar.domain.bo.BookingAssignTableBo;
import com.sqx.modules.bar.domain.bo.BookingQueryBo;
import com.sqx.modules.bar.domain.bo.BookingSubmitBo;
import com.sqx.modules.bar.domain.vo.BookingVo;
import com.sqx.modules.bar.domain.vo.TimeSlotVo;

import java.util.List;

public interface BarBookingService {

    List<TimeSlotVo> timeSlots();

    BookingVo submit(Long userId, BookingSubmitBo bo);

    PageResult<BookingVo> page(BookingQueryBo queryBo);

    List<BookingVo> recent(int limit);

    BookingVo detail(Long id);

    void cancel(Long userId, Long id);

    void confirm(Long id);

    void adminCancel(Long id);

    void assignTable(BookingAssignTableBo bo);
}
