package com.buildit.common.application;

import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ErrorResponseHelper {

    /*
        Converts errors to json string

        Example:
        {
            "errors": {
                "total": [
                    "total amount cannot be zero-2",
                ],
                "rentalPeriod.endDate": [
                    "endDate must be in the future",
                ],
                "rentalPeriod.startDate": [
                    "startDate must be before endDate",
                    "startDate must be in the future",
                ],
            }
        }
     */
    public static String errorMapToJsonString(Map<String, List<String>> errorMap){
        String errResponse = "{\"errors\":{";

        for (Map.Entry<String, List<String>> entry : errorMap.entrySet()) {
            String field = entry.getKey();
            List<String> messageList = entry.getValue();

            errResponse += "\""+field+"\": "+stringListToJson(messageList)+",";
        }

        return errResponse+"}}";
    }

    public static Map<String, List<String>> objectErrorsToMap(List<ObjectError> objectErrors){
        Map<String, List<String>> map = new HashMap<>();

        for(ObjectError err: objectErrors){
            FieldError fieldError = (FieldError) err;
            String fieldName = fieldError.getField();
            String errorMessage = fieldError.getCode();

            map = addToMessageList(map, fieldName, errorMessage);
        }

        return map;
    }

    public static Map<String, List<String>> addToMessageList(Map<String, List<String>> errorMap, String key, String message){
        /*
            Checks if given key exists in errorMap.
            If exists => adds 'message' to messageList of given key
            If does not exist => creates key and adds message to messageList of given key
         */
        List<String> messageList = errorMap.containsKey(key) ? errorMap.get(key) : new ArrayList<>();

        messageList.add(message);
        errorMap.put(key, messageList);

        return errorMap;
    }

    public static String stringListToJson(List<String> list){
        String res = "[";

        for(String str: list){
            res += "\""+str+"\",";
        }

        return res += "]";
    }
}


