package ru.bey_sviatoslav.android.vkcupfinal.groups.utils

import java.util.*

fun Long.isOld() : Boolean {
    val mydate = Calendar.getInstance()
    mydate.timeInMillis = this * 1000
    val delta = Calendar.getInstance().timeInMillis - mydate.timeInMillis
    return Calendar.getInstance().timeInMillis - mydate.timeInMillis >= 365*24*60*60*1000.toLong()
}

fun Int.roundFollowers() : String{
    when {
        this >= 1000 && this < 1_000_000 -> return "${(this - this % 100)/1000.0}K подписчиков"
        this >= 1_000_000 -> return "${(this - this % 100_000)/1_000_000.0}M подписчиков"
        else -> {
            val lastNumber = this % 10
            when(lastNumber) {
                1 -> {
                    if(this != 11)
                        return "${this} подписчик"
                    else
                        return "${this} подписчиков"
                }
                2,3,4 -> {
                    if(this / 10 != 1)
                        return "${this} подписчика"
                    else
                        return "${this} подписчиков"
                }
                else -> {
                    return "${this} подписчиков"
                }

            }
        }
    }
}
fun Int.roundFriends() : String{
    when {
        this >= 1000 && this < 1_000_000 -> return "${(this - this % 100)/1000.0}K друзей"
        this >= 1_000_000 -> return "${(this - this % 100_000)/1_000_000.0}M друзей"
        else -> {
            val lastNumber = this % 10
            when(lastNumber) {
                1 -> {
                    if(this != 11)
                        return "${this} друг"
                    else
                        return "${this} друзей"
                }
                2,3,4 -> {
                    if(this / 10 != 1)
                        return "${this} друга"
                    else
                        return "${this} друзей"
                }
                else -> {
                    return "${this} друзей"
                }

            }
        }
    }
}

fun Long.toDate() : String {
    val mydate = Calendar.getInstance()
    mydate.timeInMillis = this * 1000
    val delta = Calendar.getInstance().timeInMillis - mydate.timeInMillis
    if(delta <= 7*24*60*60*1000L){
        when{
            delta < 24*60*60*1000L -> {
                return "сегодня"
            }
            delta > 24*60*60*1000L && delta < 2*24*60*60*1000 -> {
                return "вчера"
            }
            else -> {
                val days = this.div(24*60*60*1000).toInt()
                return days.toString() + " " + days.getDayAddition() + " назад"
            }
        }
    }else {
        return "${mydate.get(Calendar.DAY_OF_MONTH)} ${mydate.getDisplayName(
            Calendar.MONTH,
            Calendar.LONG,
            Locale("ru")
        )}"
    }
}

fun Int.getDayAddition(): String {

    val preLastDigit = this % 100 / 10
    if (preLastDigit == 1) {
        return "дней"
    }

    when (this % 10) {
        1 -> return "день"
        2, 3, 4 -> return "дня"
        else -> return "дней"
    }
}