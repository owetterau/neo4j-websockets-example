package de.oliverwetterau.neo4j.examples.websockets.server.web;

import de.oliverwetterau.neo4j.websockets.core.i18n.ThreadLocale;

import java.util.Locale;

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
