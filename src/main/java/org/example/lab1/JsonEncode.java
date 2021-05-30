package org.example.lab1;

import org.json.simple.JSONObject;
public class JsonEncode{
    public static String toJSON(Packet pack){
        JSONObject obj=new JSONObject();
        obj.put("bSrc",pack.getBSrc());
        obj.put("bMsq", pack.getBMsq());
        obj.put("bPktId", pack.getBPktId());
        obj.put("wLen", pack.getWLen());
        return obj.toString();
    }
}
