package de.oliverwetterau.neo4j.examples.websockets.client;

import de.oliverwetterau.neo4j.websockets.core.i18n.ThreadLocale;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class ExampleThreadLocale implements ThreadLocale {
    protected static ThreadLocal<Locale> threadLocal = new ThreadLocal<>();
    protected static Locale defaultLocale = Locale.ENGLISH;

    @Override
    public void setLocale(Locale locale) {
        threadLocal.set(locale);
    }

    @Override
    public Locale getLocale() {
        Locale locale = threadLocal.get();

        return (locale == null) ? defaultLocale : locale;
    }
}
