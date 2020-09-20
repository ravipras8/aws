package com.skaas.sigen;

import java.net.URLEncoder;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class SignatureGenSample {

    private static String service = "ec2",
                region = "us-east-2",
                accessKey = "AKIAXQLQPDCBATQXLFBZ",
                secretKey = "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",
                queryParam = "Action=DescribeInstances&Version=2016-11-15",
                timestamp, date;


    public static void main(String[] args) throws Exception {
        setTimestamp();
        generateQueryParamSignature();
        System.out.println("\n__________________________________________________________________________________\n");
        generateHeaderSignature();
    }



    private static void generateQueryParamSignature() throws Exception  {
        String signedHeaders = "host";
        String canonicalRequest =  "GET\n" +
                                   "/\n" +
                                   queryParam + "&X-Amz-Algorithm=AWS4-HMAC-SHA256" +
                                   "&X-Amz-Credential="+ URLEncoder.encode(accessKey+"/"+date+"/"+region+"/"+service+"/aws4_request", "UTF-8")+
                                   "&X-Amz-Date="+timestamp +
                                   "&X-Amz-SignedHeaders="+signedHeaders+"\n" +
                                   "host:"+service+"."+region+".amazonaws.com\n" +
                                   "\n" +
                                   signedHeaders+"\n" +
                                   generateHex("");
        System.out.println("\n\nCanonical Request:\n" + canonicalRequest);


        String stringToSign =  "AWS4-HMAC-SHA256\n" +
                               timestamp+"\n" +
                               date+"/"+region+"/"+service+"/aws4_request\n" +
                               generateHex(canonicalRequest);
        System.out.println("\n\nString to Sign:\n" + stringToSign);


        byte[] signature = HmacSHA256( getSignatureKey(secretKey, date, region, service), stringToSign);
        String hexSignature = binToHexSignature(signature);

        System.out.println("\n\n\nConstructing the signature...\n" + hexSignature);

        String url = "https://"+service+"."+region+".amazonaws.com/?" +
                        queryParam +
                        "&X-Amz-Algorithm=AWS4-HMAC-SHA256" +
                        "&X-Amz-Credential="+ URLEncoder.encode(accessKey+"/"+date+"/"+region+"/"+service+"/aws4_request", "UTF-8")+
                        "&X-Amz-Date="+timestamp +
                        "&X-Amz-SignedHeaders="+signedHeaders +
                        "&X-Amz-Signature="+hexSignature;

        System.out.println("\n\n\nConstructing the final URL with Query Params...\n"+ url);
    }

    private static void generateHeaderSignature() throws Exception  {
        String signedHeaders = "host;x-amz-date";
        String canonicalRequest =   "GET\n" +
                                    "/\n" +
                                    queryParam+"\n" +
                                    "host:"+service+"."+region+".amazonaws.com\n" +
                                    "x-amz-date:"+timestamp+"\n" +
                                    "\n" +
                                    signedHeaders+"\n" +
                                    generateHex("");
        System.out.println("\n\nCanonical Request:\n" + canonicalRequest);


        String stringToSign =  "AWS4-HMAC-SHA256\n" +
                               timestamp+"\n" +
                               date+"/"+region+"/"+service+"/aws4_request\n" +
                               generateHex(canonicalRequest);
        System.out.println("\n\nString to Sign:\n" + stringToSign);


        byte[] signature = HmacSHA256( getSignatureKey(secretKey, date, region, service), stringToSign);
        String hexSignature = binToHexSignature(signature);

        System.out.println("\n\n\nConstructing the signature...\n" + hexSignature);


        String URL2 = "https://"+service+"."+region+".amazonaws.com/?" + queryParam;
        String headers = "x-amz-date: "+timestamp+"\n" +
                    "Authorization: AWS4-HMAC-SHA256 Credential=" +
                    accessKey + "/" + date + "/" + region + "/" + service + "/aws4_request," +
                    "SignedHeaders=" + signedHeaders + "," +
                    "Signature=" + hexSignature;

        System.out.println("\n\n\nConstructing the final URL with Authorization Header...");
        System.out.println("\nURL:\n" + URL2);
        System.out.println("\n\nHEADERS:\n" + headers);

    }

    private static void setTimestamp() {
        DateFormat timestampFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
        timestampFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        timestamp = timestampFormat.format(new Date());

        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        date = dateFormat.format(new Date());
    }

    private static String generateHex(String data) {
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(data.getBytes("UTF-8"));
            byte[] digest = messageDigest.digest();
            return String.format("%064x", new java.math.BigInteger(1, digest));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static byte[] getSignatureKey(String key, String date, String regionName, String serviceName) throws Exception {
        byte[] kSecret = ("AWS4" + key).getBytes("UTF8");
        byte[] kDate = HmacSHA256(kSecret, date);
        byte[] kRegion = HmacSHA256(kDate, regionName);
        byte[] kService = HmacSHA256(kRegion, serviceName);
        byte[] kSigning = HmacSHA256(kService, "aws4_request");
        return kSigning;
    }

    private static byte[] HmacSHA256(byte[] key, String data) throws Exception {
        String algorithm = "HmacSHA256";
        Mac mac = Mac.getInstance(algorithm);
        mac.init(new SecretKeySpec(key, algorithm));
        return mac.doFinal(data.getBytes("UTF8"));
    }

    private static String binToHexSignature(byte[] signature) {
        char[]  hexArray = "0123456789ABCDEF".toCharArray(),
                hexChars = new char[signature.length * 2];
        for (int j = 0; j < signature.length; j++) {
            int v = signature[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars).toLowerCase();
    }
}
