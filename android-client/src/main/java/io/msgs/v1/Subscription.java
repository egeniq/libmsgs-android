package io.msgs.v1;

import java.util.Date;

/**
 * Notification subscription.
 */
public class Subscription {
    public static final int SUNDAY = 1;
    public static final int MONDAY = 2;
    public static final int TUESDAY = 4;
    public static final int WEDNESDAY = 8;
    public static final int THURSDAY = 16;
    public static final int FRIDAY = 32;
    public static final int SATURDAY = 64;

    /**
     * Time data type.
     */
    public static class Time {
        private final int _hours;
        private final int _minutes;

        /**
         * Constructor.
         * 
         * @param hours
         * @param minutes
         */
        public Time(int hours, int minutes) {
            _hours = hours;
            _minutes = minutes;
        }

        /**
         * Get hours.
         * 
         * @return Hours.
         */
        public int getHours() {
            return _hours;
        }

        /**
         * Get minutes.
         * 
         * @return Minutes.
         */
        public int getMinutes() {
            return _minutes;
        }
    }

    private String _id;
    private String _channelId;
    private Date _startDate;
    private Date _endDate;
    private Time _startTime;
    private Time _endTime;
    private int _weekdays;

    /**
     * ID.
     * 
     * @return ID.
     */
    public String getId() {
        return _id;
    }

    /**
     * Sets the ID.
     * 
     * @param id ID.
     */
    public Subscription setId(String id) {
        _id = id;
        return this;
    }

    /**
     * Channel ID.
     * 
     * @return Channel ID.
     */
    public String getChannelId() {
        return _channelId;
    }

    /**
     * Sets the channel ID.
     * 
     * @param channelId Channel ID.
     */
    public Subscription setChannelId(String channelId) {
        _channelId = channelId;
        return this;
    }

    /**
     * Start date.
     * 
     * @return Start date.
     */
    public Date getStartDate() {
        return _startDate;
    }

    /**
     * End date.
     * 
     * @return End date.
     */
    public Date getEndDate() {
        return _endDate;
    }

    /**
     * Sets the date period.
     * 
     * @param startDate Start date.
     * @param endDate End date.
     */
    public Subscription setDatePeriod(Date startDate, Date endDate) {
        _startDate = startDate;
        _endDate = endDate;
        return this;
    }

    /**
     * Start time.
     * 
     * @return Start time.
     */
    public Time getStartTime() {
        return _startTime;
    }

    /**
     * End time.
     * 
     * @return End time.
     */
    public Time getEndTime() {
        return _endTime;
    }

    /**
     * Sets the time period.
     * 
     * @param startTime Start time.
     * @param endTime End time.
     */
    public Subscription setTimePeriod(Time startTime, Time endTime) {
        _startTime = startTime;
        _endTime = endTime;
        return this;
    }

    /**
     * Returns the weekdays.
     * 
     * @return Weekdays.
     */
    public int getWeekdays() {
        return _weekdays;
    }

    /**
     * Has weekday?
     * 
     * @param weekday Weekday.
     * 
     * @return Has weekday?
     */
    public boolean hasWeekday(int weekday) {
        return (_weekdays & weekday) == weekday;
    }

    /**
     * Sets the weekdays.
     * 
     * @param weekdays Weekdays.
     */
    public Subscription setWeekdays(int weekdays) {
        _weekdays = weekdays;
        return this;
    }
}
