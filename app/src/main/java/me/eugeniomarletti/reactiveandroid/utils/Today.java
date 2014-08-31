package me.eugeniomarletti.reactiveandroid.utils;

import org.jetbrains.annotations.NotNull;
import org.joda.time.LocalDate;
import rx.Observable;

public class Today
{
    protected Today() { }

    @NotNull
    public static Observable<LocalDate> observe()
    {
        return Observable.just(new LocalDate()); //TODO wrap an alarm that runs everyday, at midnight
        //return Observable.interval(2, TimeUnit.SECONDS).map(n -> n + 1).startWith(0l)
        //                 .map(tick ->
        //                      {
        //                          final LocalDate today = new LocalDate();
        //                          return today.plusYears((int)(long)tick);
        //                      });
    }
}
