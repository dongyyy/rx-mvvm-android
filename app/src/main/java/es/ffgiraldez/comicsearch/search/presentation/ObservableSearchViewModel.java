/*
 * Copyright (C) 2015 Fernando Franco Giráldez
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package es.ffgiraldez.comicsearch.search.presentation;

import com.fernandocejas.frodo.annotation.RxLogObservable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import es.ffgiraldez.comicsearch.model.SearchResult;
import es.ffgiraldez.comicsearch.rx.ObservableViewModel;
import es.ffgiraldez.comicsearch.rx.PropertyObservableProvider;
import es.ffgiraldez.comicsearch.rx.PropertySubscriber;
import rx.Observable;
import rx.functions.Action1;

public abstract class ObservableSearchViewModel extends AbstractSearchViewModel implements ObservableViewModel {
    public enum Property {QUERY, RESULTS}

    private static final Set<String> PROPERTIES = new HashSet<>(Arrays.asList(
            Property.QUERY.name(), Property.RESULTS.name()
    ));

    private final PropertyObservableProvider propertyObservableProvider;
    private final PropertySubscriber propertySubscriber;

    public ObservableSearchViewModel() {
        this.propertyObservableProvider = new PropertyObservableProvider(PROPERTIES);
        this.propertySubscriber = new PropertySubscriber(createPropertyActionMapping());
    }

    @Override
    public void dispose() {
        propertySubscriber.dispose();
    }

    @RxLogObservable
    public <T> Observable<T> observe(Property property) {
        return propertyObservableProvider.provide(property.name());
    }

    public void subscribe(Property property, Observable<?> observable) {
        propertySubscriber.subscribe(property.name(), observable);
    }

    @Override
    public void setQuery(String query) {
        String oldQuery = getQuery();
        super.setQuery(query);
        propertyObservableProvider.fireChange(Property.QUERY.name(), oldQuery, query);
    }

    @Override
    public void setResults(List<SearchResult> results) {
        List<SearchResult> oldSuggestions = getResults();
        super.setResults(results);
        propertyObservableProvider.fireChange(Property.RESULTS.name(), oldSuggestions, results);
    }

    private Map<String, Action1<?>> createPropertyActionMapping() {
        Map<String, Action1<?>> actionMap = new HashMap<>();
        actionMap.put(
                Property.QUERY.name(),
                new Action1<String>() {
                    @Override
                    public void call(String value) {
                        setQuery(value);
                    }
                }
        );

        actionMap.put(
                Property.RESULTS.name(),
                new Action1<List<SearchResult>>() {
                    @Override
                    public void call(List<SearchResult> value) {
                        setResults(value);
                    }
                }
        );
        return actionMap;
    }
}
