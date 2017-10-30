/*
 * Copyright 2017 Kentico s.r.o. and Richard Sustek
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.kentico.delivery.core.query.element;

import com.kentico.delivery.core.adapters.IHttpAdapter;
import com.kentico.delivery.core.config.DeliveryClientConfig;
import com.kentico.delivery.core.models.common.IDeliveryResponse;
import com.kentico.delivery.core.models.element.DeliveryContentTypeElementResponse;
import com.kentico.delivery.core.models.exceptions.KenticoCloudException;
import com.kentico.delivery.core.adapters.IRxAdapter;

import org.json.JSONObject;

import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.functions.Function;

public class SingleContentTypeElement extends BaseContentTypeElementQuery {

    private static final String URL_PART_TYPE = "/types/";
    private static final String URL_PART_ELEMENTS = "/elements/";

    private final String typeCodename;
    private final String elementCodename;

    public SingleContentTypeElement(DeliveryClientConfig config, IRxAdapter requestService, IHttpAdapter httpAdapter, String typeCodename, String elementCodename) {
        super(config, requestService, httpAdapter);
        this.typeCodename = typeCodename;
        this.elementCodename = elementCodename;
    }

    @Override
    public String getQueryUrl(){
        String action = URL_PART_TYPE + this.typeCodename + "/" + URL_PART_ELEMENTS + "/" + this.elementCodename;

        return this.queryService.getUrl(action, parameters);
    }

    // observable
    public Observable<DeliveryContentTypeElementResponse> getObservable() {
        return this.queryService.<JSONObject>getObservable(this.getQueryUrl())
                .map(new Function<JSONObject, DeliveryContentTypeElementResponse>() {
                    @Override
                    public DeliveryContentTypeElementResponse apply(JSONObject jsonObject) throws KenticoCloudException {
                        try {
                            return responseMapService.mapDeliveryContentTypeResponse(jsonObject);
                        } catch (IOException ex) {
                            throw new KenticoCloudException("Could not get content type element response with error: " + ex.getMessage(), ex);
                        }
                    }
                });
    }

    @Override
    public DeliveryContentTypeElementResponse get() {
        try {
            return responseMapService.mapDeliveryContentTypeResponse(this.queryService.getJson(this.getQueryUrl()));
        } catch (IOException ex) {
            throw new KenticoCloudException("Could not get content type element response with error: " + ex.getMessage(), ex);
        }
    }
}
