package com.tyctak.zerowastescalestill;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.JsonReader;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import javax.net.ssl.HttpsURLConnection;

public class XP_Library_WS
{
    public String downloadTextGET(String url) {
        return downloadTextGET(url, 3000, 3000);
    }

    public byte[] downloadBinaryGET(String url) {
        ByteArrayOutputStream retval = new ByteArrayOutputStream();

        try {
            URL urlType = new URL(url);
            HttpURLConnection urlConnection = (HttpURLConnection) urlType.openConnection();

            try {
                InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());

                int bytesRead;
                byte[] buffer = new byte[4096];
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    retval.write(buffer, 0, bytesRead);
                }
            } finally {
                urlConnection.disconnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return retval.toByteArray();
    }

    public String downloadTextGET(String url, Integer connectTimeOut, Integer readTimeOut) {
        StringBuilder retval = new StringBuilder();

        try {
            URL urlType = new URL(url);
            HttpsURLConnection urlConnection = (HttpsURLConnection) urlType.openConnection();
            urlConnection.setConnectTimeout(connectTimeOut);
            urlConnection.setReadTimeout(readTimeOut);
            urlConnection.setDoInput(true);
            urlConnection.setUseCaches(false);
            urlConnection.setRequestProperty("Host", "zerows");

            try {
                InputStream stream = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader br = new BufferedReader(new InputStreamReader(stream));

                String line;
                while ((line = br.readLine()) != null) {
                    retval.append(line);
                }
            } finally {
                urlConnection.disconnect();
            }
        } catch (Exception e) {
            retval = new StringBuilder();
            e.printStackTrace();
        }

        return retval.toString();
    }

    public String downloadTextPOST(String url, Integer connectTimeOut, Integer readTimeOut, String params) {
        StringBuilder retval = new StringBuilder();

        try {
            URL urlType = new URL(url);
            HttpURLConnection urlConnection = (HttpURLConnection) urlType.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setConnectTimeout(connectTimeOut);
            urlConnection.setReadTimeout(readTimeOut);
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Host", "zerows");

            try {
                OutputStream os = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(params);
                writer.flush();
                writer.close();
                os.close();

                int responseCode = urlConnection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream stream = new BufferedInputStream(urlConnection.getInputStream());
                    BufferedReader br = new BufferedReader(new InputStreamReader(stream));

                    for (int c; (c = br.read()) >= 0; )
                        retval.append((char) c);
                }
            } finally {
                urlConnection.disconnect();
            }
        } catch (Exception e) {
            retval = new StringBuilder();
            e.printStackTrace();
        }

        return retval.toString();
    }

    public void getLibrary(String baseUrl, String wsPassword) {
        String url = baseUrl + "getlibrary.php?p=" + wsPassword;
        byte[] binary = downloadBinaryGET(url);

        if (binary.length > 0) {
            try {
                DataOutputStream  outputStreamWriter = new DataOutputStream(MyApp.getAppContext().openFileOutput("zerowslib.jar", Context.MODE_PRIVATE));
                outputStreamWriter.write(binary);
                outputStreamWriter.close();
            }
            catch (IOException e) {
                Log.e("Exception", "File write failed: " + e.toString());
            }
        }
    }

    public _ProductDetail getProductDetail(String baseUrl, String wsPassword, _Product product) {
        _ProductDetail productDetail = new _ProductDetail(product);

        try {
            String url = baseUrl + "getproductdetail.php?p=" + wsPassword + "&i=" + product.ProductId;
            String html = downloadTextGET(url);

            if (!TextUtils.isEmpty(html)) {
                JSONObject json = new JSONObject(html);

                if (json != null) {
                    productDetail.ProductCode = json.getString("pro");
                    productDetail.Country = json.getString("cnt");
                    productDetail.Description = json.getString("des");
                    productDetail.IsOrganic = (json.getInt("org") == 1);
                    productDetail.IsVegan = (json.getInt("veg") == 1);
                    productDetail.AddedSalt = (json.getInt("asa") == 1);
                    productDetail.AddedSugar = (json.getInt("asu") == 1);
                    productDetail.Allergen = json.getString("all");
                    productDetail.Nutritional = json.getString("nut");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return productDetail;
    }

    public _Reward getReward(String baseUrl, String wsPassword) {
        _Reward reward = null;

        try {
            String url = baseUrl + "getreward.php?p=" + wsPassword;
            String html = downloadTextGET(url);

            if (!TextUtils.isEmpty(html)) {
                JSONObject json = new JSONObject(html);

                if (json != null) {
                    reward = new _Reward();
                    reward.IsReward = (json.getInt("ir") == 1);
                    reward.RewardMember = json.getString("rm");
                    reward.RewardDescription = json.getString("rd");
                    reward.RewardType = json.getInt("rt");
                    reward.RewardValue = json.getInt("rv");
                    reward.IsDisplayReward = (json.getInt("id") == 1);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return reward;
    }

    public _Process runProcess(String baseUrl, String wsPassword, String processCode) {
        _Process process = null;

        try {
            String url = baseUrl + "runprocess.php?p=" + wsPassword + "&i=" + processCode;
            String html = downloadTextGET(url);

            if (!TextUtils.isEmpty(html)) {
                    JSONObject json = new JSONObject(html);

                    if (json != null) {
                        process = new _Process();
                        process.Message = json.getString("mg");
                        process.Description = json.getString("de");
                    }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return process;
    }

    public String[] backup(String baseUrl, String wsPassword, String clientCode) {
        String url = baseUrl + "backup.php?p=" + wsPassword + "&c=" + clientCode;
        String retval = downloadTextGET(url);
        return retval.split(";");
    }

    public boolean checkLink(String baseUrl, String wsPassword, String linkCode) {
        Boolean retval = false;

        try {
            String url = baseUrl + "checklink.php?p=" + wsPassword + "&l=" + linkCode;
            String html = downloadTextGET(url);
            retval = html.toLowerCase().equals("ok");
        } catch (Exception ex) { }

        return retval;
    }

    public boolean ping(String baseUrl) {
        Boolean retval = false;

        try {
            String url = baseUrl + "ping.php";
            String html = downloadTextGET(url);
            retval = html.toLowerCase().equals("zerows");
        } catch (Exception ex) { }

        return retval;
    }

    public boolean reportAnalysis(String baseUrl, String wsPassword, Long startDate, Long endDate) {
        Boolean retval = false;

        String url = baseUrl + "mail.php?p=" + wsPassword + "&t=ANALYSIS&s=" + startDate + "&e=" + endDate;
        String html = downloadTextGET(url);
        retval = html.toLowerCase().equals("ok");

        return retval;
    }

    public boolean checkFixed(String baseUrl, String wsPassword) {
        Boolean retval = true;

        try {
            String url = baseUrl + "checkfixed.php?p=" + wsPassword;
            String html = downloadTextGET(url, 10000, 10000);

            if (!isBlank(html)) {
                InputStream inputStream = new ByteArrayInputStream(html.getBytes(Charset.forName("UTF-8")));
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                JsonReader jsonReader = new JsonReader(bufferedReader);

                jsonReader.beginObject();
                jsonReader.nextName();
                jsonReader.nextInt();
                jsonReader.nextName();
                jsonReader.nextString();
                jsonReader.nextName();
                jsonReader.nextString();
                jsonReader.nextName();
                jsonReader.nextString();
                jsonReader.nextName();
                jsonReader.beginArray();

                while(jsonReader.hasNext()) {
                    jsonReader.beginObject();
                    jsonReader.nextName();
                    jsonReader.nextString();
                    jsonReader.nextName();
                    jsonReader.nextString();
                    jsonReader.nextName();
                    jsonReader.nextString();
                    jsonReader.nextName();
                    jsonReader.nextString();
                    jsonReader.nextName();
                    if (!jsonReader.nextBoolean()) {
                        retval = false;
                    };

                    jsonReader.endObject();
                }

                jsonReader.endArray();

                jsonReader.nextName();
                jsonReader.beginArray();

                while(jsonReader.hasNext()) {
                    jsonReader.beginObject();
                    jsonReader.nextName();
                    jsonReader.nextString();
                    jsonReader.nextName();
                    jsonReader.nextString();
                    jsonReader.nextName();
                    jsonReader.nextString();
                    jsonReader.nextName();
                    jsonReader.nextString();
                    jsonReader.nextName();
                    jsonReader.nextBoolean();
                    jsonReader.endObject();
                }

                jsonReader.endArray();
                jsonReader.endObject();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return retval;
    }

    public _Verification getVericationList(String baseUrl, String wsPassword) {
        _Verification verification = new _Verification();

        try {
            String url = baseUrl + "checkall.php?p=" + wsPassword;
            String html = downloadTextGET(url, 120000, 120000);

            if (!isBlank(html)) {
                InputStream inputStream = new ByteArrayInputStream(html.getBytes(Charset.forName("UTF-8")));
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                JsonReader jsonReader = new JsonReader(bufferedReader);

                verification.Fixeds = new ArrayList<>();

                jsonReader.beginObject();
                jsonReader.nextName();
                verification.Build = jsonReader.nextInt();
                jsonReader.nextName();
                verification.ZipFile = jsonReader.nextString();
                jsonReader.nextName();
                verification.Built = jsonReader.nextString();
                jsonReader.nextName();
                verification.Version = jsonReader.nextString();
                jsonReader.nextName();
                jsonReader.beginArray();

                while(jsonReader.hasNext()) {
                    _Verification._File file = new _Verification._File();
                    file.IsFixed = true;

                    jsonReader.beginObject();
                    jsonReader.nextName(); // nm
                    file.Name = jsonReader.nextString();
                    jsonReader.nextName(); // cc
                    file.CurrentChecksum = jsonReader.nextString();
                    jsonReader.nextName(); // oc
                    file.OriginalChecksum = jsonReader.nextString();
                    jsonReader.nextName(); // ky
                    file.Key = jsonReader.nextString();
                    jsonReader.nextName(); // mc
                    file.IsMatch = jsonReader.nextBoolean();
                    jsonReader.endObject();

                    verification.Fixeds.add(file);
                }

                jsonReader.endArray();

                verification.Files = new ArrayList<>();

                jsonReader.nextName();
                jsonReader.beginArray();

                while(jsonReader.hasNext()) {
                    _Verification._File file = new _Verification._File();
                    file.IsFixed = false;

                    jsonReader.beginObject();
                    jsonReader.nextName(); // nm
                    file.Name = jsonReader.nextString();
                    jsonReader.nextName(); // cc
                    file.CurrentChecksum = jsonReader.nextString();
                    jsonReader.nextName(); // oc
                    file.OriginalChecksum = jsonReader.nextString();
                    jsonReader.nextName(); // ky
                    file.Key = jsonReader.nextString();
                    jsonReader.nextName(); // mc
                    file.IsMatch = jsonReader.nextBoolean();
                    jsonReader.endObject();

                    verification.Files.add(file);
                }

                jsonReader.endArray();
                jsonReader.endObject();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return verification;
    }

    public ArrayList<_Product> getProductList(String baseUrl, String wsPassword, Integer purchaseId) {
        ArrayList<_Product> products = null;

        try {
            String url = baseUrl + "listproducts.php?p=" + wsPassword + "&i=" + purchaseId;
            String html = downloadTextGET(url, 3000, 3000);

            if (!isBlank(html)) {
                InputStream inputStream = new ByteArrayInputStream(html.getBytes(Charset.forName("UTF-8")));
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                JsonReader jsonReader = new JsonReader(bufferedReader);

                products = new ArrayList<>();

                jsonReader.beginObject();
                jsonReader.nextName();
                jsonReader.beginArray();

                while(jsonReader.hasNext()) {
                    _Product product = new _Product();

                    jsonReader.beginObject();
                    jsonReader.nextName(); // id
                    product.Id = jsonReader.nextInt();
                    jsonReader.nextName(); // cat
                    product.Category = jsonReader.nextString();
                    jsonReader.nextName(); // dte
                    product.ActionDate = new Date(jsonReader.nextLong() * 1000);
                    jsonReader.nextName(); // unt
                    product.Unit = jsonReader.nextString();
                    jsonReader.nextName(); // prn
                    product.ProductName = jsonReader.nextString();
                    jsonReader.nextName(); // prc
                    product.Price = jsonReader.nextDouble();
                    jsonReader.nextName(); // bar
                    product.Barcode = jsonReader.nextString();
                    jsonReader.nextName(); // ung
                    product.UnitGross = Double.parseDouble(jsonReader.nextString());
                    jsonReader.nextName(); // unn
                    product.UnitNet = Double.parseDouble(jsonReader.nextString());
                    jsonReader.nextName(); // tar
                    product.Tare = Integer.parseInt(jsonReader.nextString());
                    jsonReader.nextName(); // new
                    product.NetWeight = Integer.parseInt(jsonReader.nextString());
                    jsonReader.nextName(); // grs
                    product.GrossWeight = Integer.parseInt(jsonReader.nextString());
                    jsonReader.nextName(); // cmb
                    product.CombinedSearchString = jsonReader.nextString();
                    jsonReader.nextName(); // isw
                    product.IsWeighed = (jsonReader.nextInt() == 1);
                    jsonReader.nextName(); // dlc
                    product.DealCode = jsonReader.nextString();
                    jsonReader.nextName(); // productid
                    product.ProductId = jsonReader.nextInt();
                    jsonReader.nextName(); // typ
                    product.Type = jsonReader.nextString();
                    jsonReader.endObject();

                    product.IsBasket = false;
                    product.Quantity = 0;

                    products.add(product);
                }

                jsonReader.endArray();
                jsonReader.endObject();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return products;
    }

    private String getProduct(_Product product) {
        return String.format("{\"pi\":%s,\"ca\":\"%s\",\"pn\":\"%s\",\"ug\":%s,\"un\":%s,\"qy\":%s,\"tp\":%s,\"bc\":\"%s\"}", product.Id, product.Category, product.ProductName, product.UnitGross, product.UnitNet, product.Quantity, product.Price, (product.Barcode == null ? "" : product.Barcode));
    }

    public boolean writeReceipt(String baseUrl, String wsPassword, Double totalPrice, Double pricePaid, Double saving, Integer userId, String provider, String paymentReference, ArrayList<_Product> weighedItems, ArrayList<_Product> stockItems, String paymentType) {
        Boolean retval = false;

        String wItems = "";
        for (_Product product : weighedItems) {
            wItems = wItems + (wItems.length() == 0 ? String.valueOf(product.Id) : ";" + String.valueOf(product.Id));
        }

        String sItems = "";
        for (_Product product : stockItems) {
            sItems = sItems + (sItems.length() == 0 ? getProduct(product) : "," + getProduct(product));
        }
        if (sItems.length() > 0) sItems = "st=[" + sItems + "]";

        String url = baseUrl + "addreceipt.php?p=" + wsPassword + "&tp=" + totalPrice + "&pp=" + pricePaid + "&sv=" + saving + "&us=" + userId + "&pr=" + provider + "&pa=" + Uri.encode(paymentReference) + "&pt=" + paymentType + "&wi=" + wItems;
        String html = downloadTextPOST(url, 3000, 3000, sItems);

        retval = (html.toLowerCase().equals("ok"));

        return retval;
    }

    public _ClientStatus getClientStatus(String baseUrl) {
        _ClientStatus status = null;

        try {
            String url = baseUrl + "getclientstatus.php";
            String html = downloadTextGET(url);

            if (!isBlank(html)) {
                JSONObject json = new JSONObject(html);

                if (json != null) {
                    status = new _ClientStatus();
                    status.ClientCode = json.getString("cc");
                    status.ClientVersion = json.getString("cv");
                    status.ClientInstalled = new Date(json.getLong("ci"));
                    status.CurrentIP = json.getString("cr");
                    status.HostIP = json.getString("hs");
                    status.Connection = json.getBoolean("cn");
                    status.DateFormat = json.getString("df");
                    status.Currency = json.getString("cu");
                    status.CurrencyCode = json.getString("ce");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return status;
    }

    public boolean isBlank(String string) {
        if (string == null || string.length() == 0)
            return true;

        int l = string.length();
        for (int i = 0; i < l; i++) {
            if (!isWhitespace(string.codePointAt(i)))
                return false;
        }

        return true;
    }

    private boolean isWhitespace(int c){
        return c == ' ' || c == '\t' || c == '\n' || c == '\f' || c == '\r';
    }
}
