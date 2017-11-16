/*
 * Copyright (c) 2014, Araz Abishov
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package org.dhis2.mobile.utils.date.iterators;

import org.dhis2.mobile.utils.date.CustomDateIteratorClass;
import org.dhis2.mobile.utils.date.DateHolder;
import org.dhis2.mobile.utils.date.PeriodFilter;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Collections;

public class WeekWednesdayIterator extends CustomDateIteratorClass<ArrayList<DateHolder>> {
    private static final String DATE_FORMAT = "%s%s%s";
    private static final String DATE_LABEL_FORMAT = "%s%s %s - %s";
    private static final String W = "WedW";

    private int openFuturePeriods;
    private LocalDate cPeriod;
    private LocalDate checkDate;
    private LocalDate maxDate;

    public WeekWednesdayIterator(int openFP) {
        openFuturePeriods = openFP;
        cPeriod = new LocalDate(currentDate.withWeekOfWeekyear(1).withDayOfWeek(DateTimeConstants.WEDNESDAY));
        checkDate = new LocalDate(cPeriod);
        String day = PeriodFilter.getDayString(currentDate);
        maxDate = currentDate;
        if(currentDate.getDayOfWeek()!=DateTimeConstants.TUESDAY) {
            maxDate = new LocalDate(currentDate.withDayOfWeek(DateTimeConstants.TUESDAY));
            String fixedDay = PeriodFilter.getDayString(maxDate);
            if (Integer.parseInt(day) > Integer.parseInt(fixedDay)) {
                maxDate = maxDate.plusWeeks(1);
            }
        }
        for (int i = 0; i < openFuturePeriods -1; i++) {
            maxDate = maxDate.plusWeeks(1);
        }
    }

    @Override
    public boolean hasNext() {
        return hasNext(checkDate);
    }

    private boolean hasNext(LocalDate date) {
        if (openFuturePeriods > 0) {
            return checkDate.isBefore(maxDate);
        } else {
            return currentDate.isAfter(date.plusWeeks(1));
        }
    }

    @Override
    public ArrayList<DateHolder> current() {
        if (!hasNext()) {
            return previous();
        } else {
            return generatePeriod();
        }
    }

    @Override
    public ArrayList<DateHolder> next() {
        cPeriod = cPeriod.plusYears(1);
        cPeriod = cPeriod.plusMonths(1);
        cPeriod = cPeriod.withDayOfYear(1).withDayOfWeek(DateTimeConstants.WEDNESDAY);
        return generatePeriod();
    }

    @Override
    public ArrayList<DateHolder> previous() {
        cPeriod = cPeriod.minusYears(1);
        cPeriod = cPeriod.plusMonths(1);
        cPeriod = cPeriod.withDayOfYear(1).withDayOfWeek(DateTimeConstants.WEDNESDAY);
        return generatePeriod();
    }

    @Override
    protected ArrayList<DateHolder> generatePeriod() {
        ArrayList<DateHolder> dates = new ArrayList<DateHolder>();
        checkDate = new LocalDate(cPeriod);
        int counter = 0;
        int quantity = checkDate.weekOfWeekyear().getMaximumValue();
        while ((openFuturePeriods > 0 || currentDate.isAfter(checkDate.plusWeeks(1).minusDays(1))) && counter < quantity) {
            String year = checkDate.year().getAsString();
            String cWeekNumber = checkDate.weekOfWeekyear().getAsString();
            String cDate = checkDate.toString();
            String nDate = checkDate.plusWeeks(1).minusDays(1).toString();

            String date = String.format(DATE_FORMAT, year, W, cWeekNumber);
            String label = String.format(DATE_LABEL_FORMAT, W, cWeekNumber, cDate, nDate);

            if(checkDate.isBefore(maxDate)) {
                DateHolder dateHolder = new DateHolder(date, checkDate.toString(), label);
                dates.add(dateHolder);
            }

            counter++;
            checkDate = checkDate.plusWeeks(1);
        }

        Collections.reverse(dates);
        return dates;
    }
}
