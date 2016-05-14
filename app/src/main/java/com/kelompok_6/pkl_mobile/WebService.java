package com.kelompok_6.pkl_mobile;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

public class WebService {
    private String NAMESPACE = "http://schemas.xmlsoap.org/wsdl";
    private String URL = "http://webtest.unpar.ac.id/pklws/pkl.php?wsdl";

    private String SOAP_ACTION;
    private String METHOD_NAME;

    private String result;

    public boolean registerPKL(String username, String nama, String alamat, String nohp, String tanggallahir, String produkunggulan){
        SOAP_ACTION = "regpkl";
        METHOD_NAME = "regpkl";

        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

        request.addProperty("user", username);
        request.addProperty("nama", nama);
        request.addProperty("alamat", alamat);
        request.addProperty("nohp", nohp);
        request.addProperty("tgllahir", tanggallahir);
        request.addProperty("produkunggulan", produkunggulan);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

        try{
            androidHttpTransport.call(SOAP_ACTION, envelope);
            SoapObject resultsRequestSOAP = (SoapObject) envelope.bodyIn;

            result = resultsRequestSOAP.getProperty("return").toString();
            return true;
        } catch (XmlPullParserException ed){
            ed.printStackTrace();
        } catch (Exception e){

        }

        return false;
    }

    public boolean getPKL(String SID){
        SOAP_ACTION = "getpkl";
        METHOD_NAME = "getpkl";

        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

        request.addProperty("sid", SID);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

        try{
            androidHttpTransport.call(SOAP_ACTION, envelope);
            SoapObject resultsRequestSOAP = (SoapObject) envelope.bodyIn;

            result = resultsRequestSOAP.getProperty("return").toString();
            return true;
        } catch (XmlPullParserException ed){
            ed.printStackTrace();
        } catch (Exception e){

        }

        return false;
    }

    public boolean login(String username, String password){
        SOAP_ACTION = "login";
        METHOD_NAME = "login";

        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

        request.addProperty("user", username);
        request.addProperty("password", password);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

        try{
            androidHttpTransport.call(SOAP_ACTION, envelope);
            SoapObject resultsRequestSOAP = (SoapObject) envelope.bodyIn;

            String hasil = resultsRequestSOAP.getProperty("return").toString();
            if (hasil.contains("NOK")){
                return false;
            } else {
                result = hasil.substring(7, hasil.length() - 2);
            }
            return true;
        } catch (XmlPullParserException ed){
            ed.printStackTrace();
        } catch (Exception e){

        }

        return false;
    }

    public boolean logout(String SID){
        SOAP_ACTION = "logout";
        METHOD_NAME = "logout";

        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

        request.addProperty("sid", SID);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

        try{
            androidHttpTransport.call(SOAP_ACTION, envelope);
            SoapObject resultsRequestSOAP = (SoapObject) envelope.bodyIn;

            result = resultsRequestSOAP.getProperty("return").toString();
            return true;
        } catch (XmlPullParserException ed){
            ed.printStackTrace();
        } catch (Exception e){

        }

        return false;
    }

    public boolean regProduk(String sid, String namaProduk, String hargaPokok, String hargaJual){
        SOAP_ACTION = "regproduk";
        METHOD_NAME = "regproduk";

        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

        request.addProperty("sid", sid);
        request.addProperty("namaproduk", namaProduk);
        request.addProperty("hargapokok", hargaPokok);
        request.addProperty("hargajual", hargaJual);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

        try{
            androidHttpTransport.call(SOAP_ACTION, envelope);
            SoapObject resultsRequestSOAP = (SoapObject) envelope.bodyIn;

            result = resultsRequestSOAP.getProperty("return").toString();
            return true;
        } catch (XmlPullParserException ed){
            ed.printStackTrace();
        } catch (Exception e){

        }

        return false;
    }

    public boolean getProduk(String sid, String namaProduk){
        SOAP_ACTION = "getproduk";
        METHOD_NAME = "getproduk";

        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

        request.addProperty("sid", sid);
        request.addProperty("namaproduk", namaProduk);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

        try{
            androidHttpTransport.call(SOAP_ACTION, envelope);
            SoapObject resultsRequestSOAP = (SoapObject) envelope.bodyIn;

            result = resultsRequestSOAP.getProperty("return").toString();
            return true;
        } catch (XmlPullParserException ed){
            ed.printStackTrace();
        } catch (Exception e){

        }

        return false;
    }

    public boolean delProduk(String sid, String namaProduk){
        SOAP_ACTION = "delproduk";
        METHOD_NAME = "delproduk";

        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

        request.addProperty("sid", sid);
        request.addProperty("namaproduk", namaProduk);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

        try{
            androidHttpTransport.call(SOAP_ACTION, envelope);
            SoapObject resultsRequestSOAP = (SoapObject) envelope.bodyIn;

            result = resultsRequestSOAP.getProperty("return").toString();
            return true;
        } catch (XmlPullParserException ed){
            ed.printStackTrace();
        } catch (Exception e){

        }

        return false;
    }

    public boolean getKatalog(String sid){
        SOAP_ACTION = "getkatalog";
        METHOD_NAME = "getkatalog";

        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

        request.addProperty("sid", sid);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

        try{
            androidHttpTransport.call(SOAP_ACTION, envelope);
            SoapObject resultsRequestSOAP = (SoapObject) envelope.bodyIn;

            result = resultsRequestSOAP.getProperty("return").toString();
            return true;
        } catch (XmlPullParserException ed){
            ed.printStackTrace();
        } catch (Exception e){

        }

        return false;
    }

    public boolean regTransaksi(String sid, String namaProduk, String hargaJual, String qtyJual, String tglJual){
        SOAP_ACTION = "regtransaksi";
        METHOD_NAME = "regtransaksi";

        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

        request.addProperty("sid", sid);
        request.addProperty("namaproduk", namaProduk);
        request.addProperty("hargajual", hargaJual);
        request.addProperty("qtyjual", qtyJual);
        request.addProperty("tgljual", tglJual);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

        try{
            androidHttpTransport.call(SOAP_ACTION, envelope);
            SoapObject resultsRequestSOAP = (SoapObject) envelope.bodyIn;

            result = resultsRequestSOAP.getProperty("return").toString();
            return true;
        } catch (XmlPullParserException ed){
            ed.printStackTrace();
        } catch (Exception e){

        }

        return false;
    }

    public boolean getTransaksi(String sid, String tglDari){
        SOAP_ACTION = "gettransaksi";
        METHOD_NAME = "gettransaksi";

        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

        request.addProperty("sid", sid);
        request.addProperty("tgldari", tglDari);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

        try{
            androidHttpTransport.call(SOAP_ACTION, envelope);
            SoapObject resultsRequestSOAP = (SoapObject) envelope.bodyIn;

            result = resultsRequestSOAP.getProperty("return").toString();
            return true;
        } catch (XmlPullParserException ed){
            ed.printStackTrace();
        } catch (Exception e){

        }

        return false;
    }

    public boolean isConnectedToInternet(Context context){
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null){
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI || networkInfo.getType() == ConnectivityManager.TYPE_MOBILE){
                connected = true;
            }
        } else {
            connected = false;
        }
        return connected;
    }

    public String getResult() {
        return result;
    }
}
