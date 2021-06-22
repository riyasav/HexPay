package io.thoughtbox.hamdan.injections;

import dagger.Module;
import dagger.Provides;
import io.thoughtbox.hamdan.global.Dictionary;


@Module
public class DictionaryModule {

    @Provides
    Dictionary getDictionary() {
        return Dictionary.getInstance();
    }
}
