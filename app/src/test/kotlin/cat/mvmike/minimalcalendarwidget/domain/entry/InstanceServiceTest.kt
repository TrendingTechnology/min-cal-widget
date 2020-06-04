// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.entry

import cat.mvmike.minimalcalendarwidget.BaseTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.Mockito
import java.time.LocalDate
import java.util.concurrent.TimeUnit
import java.util.stream.Collectors
import java.util.stream.Stream

internal class InstanceServiceTest : BaseTest() {
    @Test
    fun getInstancesWithTimeout_shouldReturnEmptyIfNotPermission() {
        Mockito.`when`(systemResolver.isReadCalendarPermitted(context)).thenReturn(false)
        val instances = InstanceService.getInstancesWithTimeout(context, 1000, TimeUnit.MILLISECONDS)
        Assertions.assertTrue(instances.isNotEmpty())
        Assertions.assertTrue(instances.isEmpty())
    }

    @Test
    fun getInstancesWithTimeout_shouldReturnEmptyIfTimedOut() {
        Mockito.`when`(systemResolver.isReadCalendarPermitted(context)).thenReturn(true)
        val instances = InstanceService.getInstancesWithTimeout(context, 0, TimeUnit.MILLISECONDS)
        Assertions.assertFalse(instances.isNotEmpty())
    }

    @Test
    fun getInstancesWithTimeout_shouldReadAllInstances() {
        val expectedInstances: MutableSet<Instance> = Stream.of(
                Instance(1543190400000L, 1543276800000L, 0, 0),  // 11/26 all day
                Instance(1543881600000L, 1543968000000L, 0, 0) // 12/4 all day
        ).collect(Collectors.toCollection { HashSet() })
        Mockito.`when`(systemResolver.isReadCalendarPermitted(context)).thenReturn(true)
        Mockito.`when`(systemResolver.getSystemLocalDate()).thenReturn(LocalDate.of(2018, 12, 4))
        Mockito.`when`(systemResolver.getInstances(context, 1539982800000L, 1547758800000L)).thenReturn(expectedInstances)
        val instances = InstanceService.getInstancesWithTimeout(context, 200, TimeUnit.MILLISECONDS)
        Assertions.assertTrue(instances.isNotEmpty())
        Assertions.assertEquals(expectedInstances, instances)
    }

    @ParameterizedTest
    @MethodSource("getInstancesBetweenInstants")
    fun readAllInstances_shouldFetchAllInstancesBetweenInstances(expectedInstances: MutableSet<Instance>) {
        Mockito.`when`(systemResolver.isReadCalendarPermitted(context)).thenReturn(true)
        Mockito.`when`(systemResolver.getSystemLocalDate()).thenReturn(LocalDate.of(2018, 12, 4))
        Mockito.`when`(systemResolver.getInstances(context, 1539982800000L, 1547758800000L)).thenReturn(expectedInstances)
        Assertions.assertEquals(expectedInstances, InstanceService.readAllInstances(context))
    }

    @ParameterizedTest
    @MethodSource("getLocalDateAndBeginningOfSystemTimezoneInMillis")
    fun toStartOfDayInEpochMilli_shouldReturnStartOfDayOfSystemTimeZone(localDate: LocalDate, millis: Long) {
        Assertions.assertEquals(millis, InstanceService.toStartOfDayInEpochMilli(localDate))
    }

        private fun getInstancesBetweenInstants(): Stream<Arguments> {
            return Stream.of(
                    Arguments.of(Stream.of(
                            Instance(1543190400000L, 1543276800000L, 0, 0),  // 11/26 all day
                            Instance(1543881600000L, 1543968000000L, 0, 0) // 12/4 all day
                    ).collect(Collectors.toCollection { HashSet() })),
                    Arguments.of(Stream.of(
                            Instance(1546300800000L, 1546387200000L, 0, 0),  // 01/01 all day
                            Instance(1546387200000L, 1546473600000L, 0, 0),  // 01/02 all day
                            Instance(1546646400000L, 1546732800000L, 0, 0) // 01/05 all day
                    ).collect(Collectors.toCollection { HashSet() })),
                    Arguments.of(Stream.of(
                            Instance(1546646400000L, 1546732800000L, 0, 0) // 01/05 all day
                    ).collect(Collectors.toCollection { HashSet() })),
                    Arguments.of(java.util.HashSet<Any>()),
                    Arguments.of(Stream.of(
                            Instance(1543190400000L, 1543276800000L, 0, 0),  // 11/26 all day
                            Instance(1543363200000L, 1543449600000L, 0, 0),  // 11/28 all day
                            Instance(1543449600000L, 1543536000000L, 0, 0),  // 11/29 all day
                            Instance(1543795200000L, 1543967999000L, 0, 0),  // 12/3 all day
                            Instance(1543881600000L, 1543968000000L, 0, 0) // 12/4 all day
                    ).collect(Collectors.toCollection { HashSet() }))
            )
        }

        private fun getLocalDateAndBeginningOfSystemTimezoneInMillis(): Stream<Arguments> {
            return Stream.of(
                    Arguments.of("2016-01-01", 1451595600000L),
                    Arguments.of("2018-10-11", 1539205200000L),
                    Arguments.of("2018-12-04", 1543870800000L),
                    Arguments.of("2019-06-06", 1559768400000L),
                    Arguments.of("3000-12-31", 32535118800000L)
            )
        }

}