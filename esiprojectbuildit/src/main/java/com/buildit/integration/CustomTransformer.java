package com.buildit.integration;



import com.buildit.rental.domain.model.PurchaseOrder;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.query.ImmutableEntityUpdateQueryHandlingMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.core.TypeReferences;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomTransformer {

//
//    @Autowired
//    @Qualifier("_halObjectMapper")
    ObjectMapper mapper = new ObjectMapper();

//    public CollectionModel<EntityModel<Plant>> fromJson(String json) {
//        try {
//            List<Plant> plants = mapper.readValue(json, new TypeReference<List<Plant>>() {});
//            return new CollectionModel<>(plants.stream()
//                    .map(p -> new EntityModel(p, new Link("http://localhost:8088/api/v1/plant/" + p._id)))
//                    .collect(Collectors.toList()));
//        } catch (IOException e) {
//            return null;
//        }
//    }

//    public CollectionModel<Plant> fromHALForms(String json) {
//        try {
//            System.out.println("IN FROMHALFORMS");
//            System.out.println(json);
//            return mapper.readValue(json, new TypeReference<CollectionModel<Plant>>() {});
//
//            //TypeReferences.CollectionModelType collectionModelType = TypeReferences.CollectionModelType<Plant>(){};
//        } catch (Exception e) {
//            System.out.println("ERROR");
//            System.out.println(e);
//            return null;
//        }
//    }

    public List<Plant> fromHALForms(String json) {
        try {
            System.out.println("IN FROMHALFORMS");
            //System.out.println(json);
            return mapper.readValue(json, new TypeReference<List<Plant>>() {});
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    public List<PlantSophio> fromHALFormsSophio(String json) {
        try {
            System.out.println("IN FROMHALFORMS2");
            System.out.println("IN FROMHALFORMS2 : " +json);
            return mapper.readValue(json, new TypeReference<List<PlantSophio>>() {});
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    public List<RentITPurchaseOrderDTO> getPOFromHALForms(String json) {
        try {
            System.out.println("IN getPOFromHALForms");
            System.out.println(json);
            return mapper.readValue(json, new TypeReference<List<RentITPurchaseOrderDTO>>() {});
        } catch (Exception e) {
            System.out.println("ERRORPO");
            System.out.println(e);
            return null;
        }
    }

    public List<RentITPODto_Team9> getPOFromHALFormsT9(String json) {
        try {
            System.out.println("IN getPOFromHALFormsT9");
            System.out.println(json);
            return mapper.readValue(json, new TypeReference<List<RentITPODto_Team9>>() {});
        } catch (Exception e) {
            System.out.println("ERRORPOT9");
            System.out.println(e);
            return null;
        }
    }

    public List<RentITPurchaseOrderDTO> cancelPOFromHALForms(String json) {
        try {
            System.out.println("IN cancelPOFromHALForms");
            System.out.println(json);
            return mapper.readValue(json, new TypeReference<List<RentITPurchaseOrderDTO>>() {});
        } catch (Exception e) {
            System.out.println("ERRORCANCELPO");
            System.out.println(e);
            return null;
        }
    }


}
