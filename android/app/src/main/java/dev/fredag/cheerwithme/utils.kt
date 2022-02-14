package dev.fredag.cheerwithme.happening

import java.time.Duration
import java.time.Instant

/**
 * Transforms a List to a Map given a function to extract a key from each element
 *
 * If byKey derives the same key from multiple entries in the list only the last entry will remain.
 *
 * @param byKey: Function that extracts the key to use from an entry in the list.
 */
fun <K, T> List<T>.toMapByKey(byKey: (T) -> K): Map<K, T> {
    return this.map { byKey(it) to it }.toMap()
}


fun durationToEventText(now: Instant, eventStartTime: Instant): String {
    val timeUntil = Duration.between(now, eventStartTime)
    if (timeUntil.abs().toMinutes() < 1) {
        return "starting now"
    }
    return if (timeUntil.isNegative) "started ${sensibleDurationString(timeUntil)} ago" else "starting in ${
        sensibleDurationString(
            timeUntil
        )
    }"
}

/**
 * Converts a duration to something that is easier to read
 *
 * If duration is less than a minute it returns "now"
 */
fun sensibleDurationString(duration: Duration): String {
    val builder = StringBuilder()
    val d = duration.abs()
    if (d.toDays() > 365) {
        val years = d.toDays() / 365
        val daysPart = d.toDays() % 365
        builder.append(years)
        builder.append(" year")
        if (years > 1) builder.append("s")
        if (daysPart > 0) {
            builder.append(" $daysPart day")
            if (daysPart > 1) builder.append("s")
        }

    } else if (d.toDays() > 0) {
        builder.append(d.toDays())
        builder.append(" day")
        if (d.toDays() > 1) builder.append("s")
    } else if (d.toHours() > 0) {
        val minutesPart = d.toMinutes() % 60
        builder.append(d.toHours())
        builder.append(" hour")
        if (d.toHours() > 1) builder.append("s")
        if (minutesPart > 0)builder.append(" $minutesPart minute")
        if (minutesPart > 1)builder.append("s")
    } else if (d.toMinutes() > 0) {
        builder.append(d.toMinutes())
        builder.append(" minute")
        if (d.toMinutes() > 1) builder.append("s")
    } else {
        builder.append("now")
    }
    return builder.toString()
}
