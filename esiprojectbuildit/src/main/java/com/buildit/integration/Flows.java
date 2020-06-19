package com.buildit.integration;

import com.buildit.rental.application.dto.PlantInventoryEntryDTO;

import com.buildit.rental.application.dto.PurchaseOrderDTO;
import com.buildit.rental.domain.model.PurchaseOrder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpMethod;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.Transformers;
import org.springframework.integration.http.dsl.Http;
import org.springframework.integration.scripting.dsl.Scripts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;



@Configuration
public class Flows {
    private static final String CREATE_PO_URI = "http://localhost:8090/api/sales/po";
    private static final String GET_AVALABLE_PLANTS_URI = "http://localhost:8090/api/plants/available/?startDate={startDate}&endDate={endDate}";
    private static final String GET_AVALABLE_PLANTS_URI_TEAM9 = "http://rentit-heroku.herokuapp.com/api/inventory/plants?name={name}&startDate={startDate}&endDate={endDate}";
    private static final String GET_PO_URI = "http://localhost:8090/api/sales/po?custName={custName}";
    private static final String GET_PO_URI_TEAM9 = "http://rentit-heroku.herokuapp.com/api/sales/orders/";
    private static final String CANCEL_PO_URI = "http://localhost:8090/api/sales/po/{id}/cancel";
    private static final String CANCEL_PO_URI_TEAM9 = "http://rentit-heroku.herokuapp.com/api/sales/orders/{id}/cancelel";

    @Bean
    IntegrationFlow scatterComponent() {
        System.out.println("SCATTER");
        return IntegrationFlows
                .from("req-channel")
                .publishSubscribeChannel(conf ->
                        conf.applySequence(true)
                                .subscribe(f -> f.channel("rentit-req"))
                                .subscribe(f -> f.channel("rentit-team9")))
                .get();
    }

    @Bean
    IntegrationFlow scatterComponent_PO() {
        System.out.println("SCATTER");
        return IntegrationFlows
                .from("req-channel-po")
                .publishSubscribeChannel(conf ->
                        conf.applySequence(true)
                                .subscribe(f -> f.channel("rentit-req-po"))
                                .subscribe(f -> f.channel("rentit-team9-po")))
                .get();
    }

    @Bean
    IntegrationFlow scatterComponent_cancelPO() {
        System.out.println("SCATTER");
        return IntegrationFlows
                .from("req-channel-cancel-po")
                .publishSubscribeChannel(conf ->
                        conf.applySequence(true)
                                .subscribe(f -> f.channel("rentit-req-cancel_po")))
                .get();
    }

//    @Bean
//    IntegrationFlow gatherComponent() {
//        System.out.println("GATHER");
//        return IntegrationFlows.from("gather-channel")
//                .aggregate(spec ->
//                        spec.outputProcessor(proc ->
//                                new CollectionModel<>(
//                                        proc.getMessages()
//                                                .stream()
//                                                .map(msg -> ((CollectionModel) msg.getPayload()).getContent())
//                                                .collect(Collectors.toList())))
//                                .groupTimeout(2000)
//                                .releaseStrategy(g -> g.size() > 1)
//                                .sendPartialResultOnExpiry(true))
//                .channel("rep-channel")
//                .get();
//    }


    @Bean
    IntegrationFlow gatherChannel() {
        System.out.println("GATHER");
        List<Plant> l;
        return IntegrationFlows.from("gather-channel")
                .aggregate(spec ->
                        spec.outputProcessor(proc ->
                                new CollectionModel<>(
                                        proc.getMessages()
                                                .stream()
                                                .map(msg -> ((List) msg.getPayload()))
                                                .collect(Collectors.toList())))
                                .groupTimeout(2000)
                                .releaseStrategy(g -> g.size() > 1)
                                .sendPartialResultOnExpiry(true))
                .channel("rep-channel")
                .get();
    }

    @Bean
    IntegrationFlow getPOGatherChannel() {
        System.out.println("GETPO-GATHER");
        List<RentITPurchaseOrderDTO> l;
        return IntegrationFlows.from("getpo-gather-channel")
                .aggregate(spec ->
                        spec.outputProcessor(proc ->
                                new CollectionModel<>(
                                        proc.getMessages()
                                                .stream()
                                                .map(msg -> ((List) msg.getPayload()))
                                                .collect(Collectors.toList())))
                                .groupTimeout(2000)
                                .releaseStrategy(g -> g.size() > 1)
                                .sendPartialResultOnExpiry(true))
                .channel("getpo-rep-channel")
                .get();
    }


    @Bean
    IntegrationFlow cancelPOGatherChannel() {
        System.out.println("CANCELPO-GATHER");
        List<RentITPurchaseOrderDTO> l;
        return IntegrationFlows.from("cancel-po-gather-channel")
                .aggregate(spec ->
                        spec.outputProcessor(proc ->
                                new CollectionModel<>(
                                        proc.getMessages()
                                                .stream()
                                                .map(msg -> ((List) msg.getPayload()))
                                                .collect(Collectors.toList())))
                                .groupTimeout(2000)
                                .releaseStrategy(g -> g.size() > 1)
                                .sendPartialResultOnExpiry(true))
                .channel("can-po-rep-channel")
                .get();
    }


//    @Bean
//    IntegrationFlow gatherComponent() {
//        return IntegrationFlows.from("gather-channel")
//                .aggregate(spec -> spec.outputProcessor(g -> g.getMessages().stream()
//                        .flatMap(m -> Arrays.stream((Plant[]) m.getPayload()))
//                        .collect(Collectors.toList())))
//                .channel("rep-channel")
//                .get();
//    }

    @Bean
    IntegrationFlow rentItFlowGetAvalaiblePlants() {
        System.out.println("RENTIT FLOW");
        return IntegrationFlows.from("rentit-req")
                .handle(Http.outboundGateway(GET_AVALABLE_PLANTS_URI)
                        .uriVariable("startDate", "payload.startDate")
                        .uriVariable("endDate", "payload.endDate")
                        .httpMethod(HttpMethod.GET)
                        .expectedResponseType(String.class))
                .handle("customTransformer", "fromHALForms")
                .channel("gather-channel")
                .get();
    }

    @Bean
    IntegrationFlow rentItFlowGetAvalaiblePlants_sophio() {
        System.out.println("RENTIT2 FLOW");
        return IntegrationFlows.from("rentit-team9")
                .handle(Http.outboundGateway(GET_AVALABLE_PLANTS_URI_TEAM9)
                        .uriVariable("name","payload.name")
                        .uriVariable("startDate", "payload.startDate")
                        .uriVariable("endDate", "payload.endDate")
                        .httpMethod(HttpMethod.GET)
                        .expectedResponseType(String.class))
                .handle("customTransformer", "fromHALFormsSophio")
                .channel("gather-channel")
                .get();
    }


    @Bean
    IntegrationFlow getPORentIt1() {
        System.out.println("RENTIT FLOW");
        return IntegrationFlows.from("rentit-req-po")
                .handle(Http.outboundGateway(GET_PO_URI)
                        .uriVariable("custName", "payload")
                        .httpMethod(HttpMethod.GET)
                        .expectedResponseType(String.class))
                .handle("customTransformer", "getPOFromHALForms")
                .channel("getpo-gather-channel")
                .get();
    }

    @Bean
    IntegrationFlow getPORentItTeam9() {
        System.out.println("RENTIT T9 FLOW");
        return IntegrationFlows.from("rentit-team9-po")
                .handle(Http.outboundGateway(GET_PO_URI_TEAM9)
                        .httpMethod(HttpMethod.GET)
                        .expectedResponseType(String.class))
                .handle("customTransformer", "getPOFromHALFormsT9")
                .channel("getpo-gather-channel")
                .get();
    }


    @Bean
    IntegrationFlow cancelPORentIt() {
        System.out.println("RENTIT CAN FLOW");
        return IntegrationFlows.from("rentit-req-cancel_po")
                .handle(Http.outboundGateway(CANCEL_PO_URI)
                        .uriVariable("id", "payload")
                        .httpMethod(HttpMethod.DELETE)
                        .expectedResponseType(String.class))
                .handle("customTransformer", "cancelPOFromHALForms")
                .channel("cancel-po-gather-channel")
                .get();
    }


//    @Bean
//    IntegrationFlow createPORentIt1() {
//        System.out.println("GET PLANT ENTRY RENTIT FLOW");
//        return IntegrationFlows.from("rentit-req")
//                .handle(Http.outboundGateway(CREATE_PO_URI)
//                        .uriVariable("startDate", "payload")
//                        .uriVariable("endDate", "headers.endDate")
//                        .httpMethod(HttpMethod.GET)
//                        .expectedResponseType(String.class))
//                .handle("customTransformer", "fromHALForms")
//                .channel("gather-channel")
//                .get();
//    }


//    @Bean
//    IntegrationFlow rentItFlow() {
//        System.out.println("RENTIT FLOW");
//        return IntegrationFlows.from("rentit-req")
//                .handle(Http.outboundGateway("http://localhost:8090/api/sales/plants?name={name}&startDate={startDate}&endDate={endDate}")
//                        .uriVariable("name", "payload")
//                        .uriVariable("startDate", "headers.startDate")
//                        .uriVariable("endDate", "headers.endDate")
//                        .httpMethod(HttpMethod.GET)
//                        .expectedResponseType(String.class))
//                .handle("customTransformer", "fromHALForms")
//                .channel("gather-channel")
//                .get();
//    }





//    @Bean
//    IntegrationFlow rentmtFlow() {
//        return IntegrationFlows
//                .from("rentmt-req")
//                //.handle("integrationServiceImpl", "findPlants")
//                .handle(Http.outboundGateway("http://localhost:8088/api/v1/plant?filter[plant]=name==*{name}*")
//                        .uriVariable("name", "payload")
//                        .httpMethod(HttpMethod.GET)
//                        .expectedResponseType(String.class))
//                .transform(Scripts.processor("classpath:/JsonApi2HAL.js")
//                        .lang("javascript"))
//                //.transform(Transformers.fromJson(Plant[].class))
//                .handle("customTransformer", "fromJson")
//                .channel("gather-channel")
//                .get();
//    }
}

